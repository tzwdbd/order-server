package com.oversea.task.job;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.haihu.rpc.common.RpcCallback;
import com.haihu.rpc.server.RpcServerProxy;
import com.oversea.rabbitmq.utils.StringUtil;
import com.oversea.task.common.TaskService;
import com.oversea.task.domain.ExpressNode;
import com.oversea.task.domain.OrderAccount;
import com.oversea.task.domain.Resources;
import com.oversea.task.domain.RobotOrderDetail;
import com.oversea.task.domain.TradeExpressStatus;
import com.oversea.task.mapper.ExpressNodeDAO;
import com.oversea.task.mapper.ExpressSpiderDAO;
import com.oversea.task.mapper.OrderAccountDAO;
import com.oversea.task.mapper.OrderDeviceDAO;
import com.oversea.task.mapper.ResourcesDAO;
import com.oversea.task.mapper.RobotOrderDetailDAO;
import com.oversea.task.mapper.TradeExpressStatusDAO;
import com.oversea.task.mapper.UserTradeExpressDAO;
import com.oversea.task.obj.Task;
import com.oversea.task.obj.TaskDetail;
import com.oversea.task.obj.TaskResult;
import com.oversea.task.util.DateUtil;
import com.oversea.task.util.OrderUtil;

/**
 * 爬取物流详情
 * @author boliu
 *
 */
public class ShipDetailServiceJob implements RpcCallback{
	
	private Logger log = Logger.getLogger(getClass());
	@Resource
	private RpcServerProxy rpcServerProxy;

    @Resource
    private RobotOrderDetailDAO robotOrderDetailDAO;

    @Resource
    private OrderAccountDAO orderAccountDAO;

    @Resource
    private OrderDeviceDAO orderDeviceDAO;
    
    @Resource
    private ExpressSpiderDAO expressSpiderDAO;
    
    @Resource
    private ExpressNodeDAO expressNodeDAO;
    
    @Resource
    private TradeExpressStatusDAO tradeExpressStatusDAO;
    
    @Resource
    private UserTradeExpressDAO userTradeExpressDAO;
    
    @Resource
	private ResourcesDAO resourcesDAO;
    
    private static final String EXPRESS_HAIHU = "express_haihu_config";//过滤物流时间
	
	public void run(){
		log.error("============ShipDetailServiceJob begin============");
		try{
			List<RobotOrderDetail> orderDetails = robotOrderDetailDAO.findOrderDetailsForSpiderExpressDetail();
	    	List<List<RobotOrderDetail>> waittingForPlaceOrders = OrderUtil.getRobotOrderDetailListGroupByAccountId(orderDetails);
	    	for(List<RobotOrderDetail> list : waittingForPlaceOrders){
	    		if(list != null && list.size() > 0){
	    			Task task = new TaskDetail();
	    			RobotOrderDetail orderDetail = list.get(0);
	                OrderAccount account = orderAccountDAO.findById(orderDetail.getAccountId());
	                Integer deviceId = orderDetail.getDeviceId();
	                String ip = orderDeviceDAO.findById(deviceId).getDeviceIp();
	                Map<Long, String> asinCodeMap = new HashMap<Long, String>();
	                for (RobotOrderDetail detail : list) {
	                    long productEntityId = detail.getProductEntityId();
	                    String asinCode = robotOrderDetailDAO.getExternalProductEntityId(productEntityId);
	                    asinCodeMap.put(productEntityId, asinCode);
	                }
	                task.addParam("asinMap", asinCodeMap);
	                task.addParam("robotOrderDetails", list);
	                task.addParam("account", account);
	                task.setGroup(ip);
	                
	                TaskService taskService = (TaskService)rpcServerProxy.wrapProxy(TaskService.class, ip, this);
	                taskService.shipService(task);
	    		}
	    	}
		}catch(Exception e){
			log.error(e);
		}
        
		log.error("============ShipDetailServiceJob end=================");
	}

	@Override
	public void callbackAck(boolean isSuccess, Method method, Object[] objs) {
		// TODO Auto-generated method stub
		if(isSuccess){
			if(objs != null){
				Task task = (Task)objs[0];
				List<RobotOrderDetail> orderDetailList = (List<RobotOrderDetail>)task.getParam("robotOrderDetails");
				log.error("订单号:"+orderDetailList.get(0).getOrderNo()+"提交爬取物流详情成功"+"ip:"+task.getGroup());
				for(RobotOrderDetail orderDetail : orderDetailList){
					robotOrderDetailDAO.updateRobotOrderDetailExpressStatusById(4, orderDetail.getId());
				}
			}
		}else{
			if(objs != null){
				Task task = (Task)objs[0];
				List<RobotOrderDetail> orderDetailList = (List<RobotOrderDetail>)task.getParam("robotOrderDetails");
				log.error("订单号:"+orderDetailList.get(0).getOrderNo()+"提交爬取物流详情失败");
			}
		}
	}

	@Override
	public void callbackResult(Object result, Method method, Object[] objs) {
		// TODO Auto-generated method stub
		TaskResult taskResult = (TaskResult)result;
		if(taskResult == null){
			return;
		}

		//插入物流节点
		List<String> finishList = new ArrayList<String>();
        List<ExpressNode> list = null;
        try{
            list = (List<ExpressNode>)taskResult.getParam("expressNodeList");
            if(list != null && list.size() > 0){
            	for(ExpressNode node : list){
            		if(node.getStatus() != null && node.getStatus().equals(14)){
            			finishList.add(node.getExpressNo());
            		}
            		List<ExpressNode> temp = expressNodeDAO.getExpressNodeByOccurTime(node.getExpressNo(), node.getOccurTime());
            		if(temp == null || temp.size() <=0){
            			expressNodeDAO.addExpressNode(node);
            		}
            	}
            }
        }catch(Exception e){
        	log.error(e);
        }
        
        //修改express_status状态
        try{
        	List<RobotOrderDetail> orderDetails = (List<RobotOrderDetail>) taskResult.getValue();
      		for (RobotOrderDetail orderDetail : orderDetails) {
      			RobotOrderDetail _orderDetail = robotOrderDetailDAO.getRobotOrderDetailById(orderDetail.getId());
      			if (_orderDetail == null) {
      				log.error(String.format("orderDetail id:%d 没有找到", orderDetail.getId()));
      				continue;
      			}
      			if(orderDetail.getStatus()==96){
      				_orderDetail.setExpressStatus(15);
      				robotOrderDetailDAO.updateRobotOrderDetail(_orderDetail);
      			}
      			if(finishList.size()>0 && !StringUtil.isBlank(_orderDetail.getExpressNo()) && finishList.contains(_orderDetail.getExpressNo())){
      				_orderDetail.setExpressStatus(14);
      				robotOrderDetailDAO.updateRobotOrderDetail(_orderDetail);
      			}
            }
        }catch(Exception e){
        	log.error(e);
        }
  		
        //插入到物流节点表中
        try{
            addExpressNodeToTradeExpressStatus(list);
        }catch(Exception e){
        	log.error("插入到物流节点表异常",e);
        }
	}
	
	// 插入物流信息到trade_express_status_x表中
    public void addExpressNodeToTradeExpressStatus(List<ExpressNode> list) {
    	if (list == null || list.size() == 0) return;
    	
    	try{
    		// 获取订单号
        	String orderNo = list.get(0).getOrderNo();
        	// 获取运单号
        	String expressNo = ((ExpressNode)list.get(0)).getExpressNo();
        	// 获取分表信息
        	String shardKey = "_" + orderNo.charAt(orderNo.length() - 1);
    		// 获取商城信息
        	String siteName = null;
        	Date orderTime = null;
        	List<RobotOrderDetail> orderDetailList = robotOrderDetailDAO.getRobotOrderDetailByOrderNo(orderNo);
        	if (orderDetailList != null && orderDetailList.size() > 0) {
        		RobotOrderDetail orderDetail = orderDetailList.get(0);
        		siteName = orderDetail.getSiteName();
        		orderTime = orderDetail.getOrderTime();
        	}
        	if (siteName == null) {
        		log.error("没有获取到商城信息");
        		return;
        	}
        	for(ExpressNode node : list){
        		if (StringUtils.isEmpty(node.getName()) || null == node.getOccurTime()) {
        			continue;
        		}
        		
        		// 时间转换成国内时间
        		Date occurTime = node.getOccurTime();
    			Calendar calendar = Calendar.getInstance();
    			calendar.setTime(occurTime);
    			
    			if ("amazon".equalsIgnoreCase(siteName)) { // 美亚时间加12小时
    				calendar.add(Calendar.HOUR_OF_DAY, 12);
    			} else if ("amazon.jp".equalsIgnoreCase(siteName)) { //日亚时间减一小时
    				calendar.add(Calendar.HOUR_OF_DAY, -1);
    			}
    			occurTime = calendar.getTime();
    			
    			// 查看是否已经插入过
    			if (tradeExpressStatusDAO.getCountOfTradeExpressStatusByExpressNoAndOccurTime(orderNo,expressNo, occurTime) == 0) {
    				TradeExpressStatus tradeExpressStatus = new TradeExpressStatus();
    				tradeExpressStatus.setExpressNo(expressNo);
    				tradeExpressStatus.setOrderNo(orderNo);
    				tradeExpressStatus.setOccurTime(occurTime);
    				tradeExpressStatus.setStatus(3);
    				tradeExpressStatus.setName("商城已发货");
    				tradeExpressStatus.setRemark(node.getName());
    				if(expressHaihu(siteName, orderTime) && !node.getName().contains("清关")){
    					continue;
    				}
    				if("zcn".equalsIgnoreCase(siteName) && node.getStatus() != null && node.getStatus().equals(14)){
    					tradeExpressStatus.setStatus(14);
        				tradeExpressStatus.setName("已签收");
    				}
    				if("bodyguardapotheke".equalsIgnoreCase(siteName) && node.getStatus() != null && node.getStatus().equals(14)){
    					tradeExpressStatus.setStatus(14);
        				tradeExpressStatus.setName("已签收");
    				}
    				tradeExpressStatus.setShardKey(shardKey);
    				tradeExpressStatus.setOperator("com.oversea.task.service.express.ExpressHandler.addExpressNodeToTradeExpressStatus");
    				tradeExpressStatusDAO.addTradeExpressStatus(tradeExpressStatus);
    			}
        	}
    	}
    	catch(Exception e){
    		log.error("插入物流节点到trade_express_status表中异常:\t"+e);
    	}
    }
    
    private boolean expressHaihu(String siteName,Date orderTime){
 	   boolean mark = false;
 	   try {
 		   List<Resources> resources = resourcesDAO.getSaleResourceByType(EXPRESS_HAIHU);
 		   for(Resources r:resources){
 			   if(siteName.equalsIgnoreCase(r.getName())){
 				   Date startTime = DateUtil.ymdhmsString2DateTime(r.getResValue());
 				   Date endTime = DateUtil.ymdhmsString2DateTime(r.getValue2());
 				   if (orderTime.after(startTime) && orderTime.before(endTime)) {
 					   mark = true;
 					   break;
 				   }
 			   }
 		   }
 		} catch (Exception e) {
 			
 		}
 	   
 	   return mark;
    }
}
