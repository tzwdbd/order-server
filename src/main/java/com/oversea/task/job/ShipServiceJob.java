package com.oversea.task.job;

import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
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
import com.oversea.cdn.service.CdnService;
import com.oversea.rabbitmq.utils.StringUtil;
import com.oversea.task.common.TaskService;
import com.oversea.task.domain.ExpressNode;
import com.oversea.task.domain.ExpressSpider;
import com.oversea.task.domain.OrderAccount;
import com.oversea.task.domain.Resources;
import com.oversea.task.domain.RobotOrderDetail;
import com.oversea.task.domain.TradeExpressStatus;
import com.oversea.task.domain.UserTradeExpress;
import com.oversea.task.enums.AutoBuyStatus;
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

public class ShipServiceJob implements RpcCallback{
	
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
    @Resource
    private CdnService cdnClient;
    
    private static final String EXPRESS_HAIHU = "express_haihu_config";//过滤物流时间
    
    public String getScanStatus(){
    	return AutoBuyStatus.AUTO_SCRIBE_ORDER_NOT_READY.getValue() +
    			"," + AutoBuyStatus.AUTO_PAY_SUCCESS.getValue();
    }
    
	public void run(){
		log.error("============ShipServiceJob begin============");
		try{
			List<RobotOrderDetail> orderDetails = robotOrderDetailDAO.findOrderDetailsForSpiderExpress(getScanStatus());
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
        
		log.error("============ShipServiceJob end=================");
	}
	

	@Override
	public void callbackAck(boolean isSuccess, Method method, Object[] objs) {
		// TODO Auto-generated method stub
		if(isSuccess){
			if(objs != null){
				Task task = (Task)objs[0];
				List<RobotOrderDetail> orderDetailList = (List<RobotOrderDetail>)task.getParam("robotOrderDetails");
				log.error("订单号:"+orderDetailList.get(0).getOrderNo()+"提交爬取物流成功"+"ip:"+task.getGroup());
				for(RobotOrderDetail orderDetail : orderDetailList){
					robotOrderDetailDAO.updateRobotOrderDetailStatusById(AutoBuyStatus.AUTO_SCRIBE_ORDER_NOT_READY.getValue(), orderDetail.getId());
				}
			}
		}else{
			if(objs != null){
				Task task = (Task)objs[0];
				List<RobotOrderDetail> orderDetailList = (List<RobotOrderDetail>)task.getParam("robotOrderDetails");
				String mallName = orderDetailList.get(0).getSiteName();
				if("amazon.jp".equalsIgnoreCase(mallName)){
					for(RobotOrderDetail orderDetail : orderDetailList){
						robotOrderDetailDAO.updateRobotOrderDetailStatusById(AutoBuyStatus.AUTO_SCRIBE_NEED_SINGLE.getValue(), orderDetail.getId());
					}
				}
				log.error("订单号:"+orderDetailList.get(0).getOrderNo()+"提交爬取物流失败");
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
        
        List<RobotOrderDetail> orderDetails = (List<RobotOrderDetail>) taskResult.getValue();
        //for 果冻 物流模糊查询
        try{
        	if (orderDetails != null && orderDetails.size() > 0) {
    			if ("escentual".equals(orderDetails.get(0).getSiteName())) {
    				for (RobotOrderDetail robotOrderDetail : orderDetails) {
    					final String expressNo = robotOrderDetail.getExpressNo();
    					if (!StringUtils.isBlank(expressNo)) {
    						List<ExpressSpider> expressSpiders = expressSpiderDAO.getExpressSpiderByExpressNo(expressNo);
    						if (expressSpiders == null || expressSpiders.size() == 0) {
    							ExpressSpider item = new ExpressSpider();
    							item.setOrderNo(robotOrderDetail.getOrderNo());
    							item.setMallOrderNo(robotOrderDetail.getMallOrderNo());
    							item.setExpressNo(expressNo);
    							item.setStatus(0);
    							expressSpiderDAO.addExpressSpider(item);
    						}
    					}
    				}
    				//return;
    			}
    		}
        }catch(Exception e){
        	log.error(e);
        }
        
        //处理日亚退税
        String cdnUrl  ="";
        try{
        	byte[] fedroadtext = (byte[]) taskResult.getParam("fedroadtext");
        	if(fedroadtext!=null){
        		//download(fedroadtext, "/home/www/logs/oversea/order/"+orderDetails.get(0).getOrderNo()+".png");
        		download(fedroadtext, "/home/www/logs/oversea/order/img/"+orderDetails.get(0).getOrderNo()+".png");
        		//cdnUrl = cdnClient.saveFile(fedroadtext, "", "png");
        		log.error("fedroadtext url:"+cdnUrl);
        	}else{
        		log.error("fedroadtext 为空");
        	}
        }catch(Exception e){
        	log.error("fedroadtext 异常");
        }   
        //插入物流节点
        List<String> finishList = new ArrayList<String>();
        List<ExpressNode> list = null;
        try{
            list = (List<ExpressNode>)taskResult.getParam("expressNodeList");
            if(list != null && list.size() > 0){
            	log.error("siteName="+orderDetails.get(0).getSiteName()+" expressNodeList size="+list.size());
            	for(ExpressNode node : list){
            		log.error("node expressNo="+node.getExpressNo()+" occurTime="+node.getOccurTime());
            		if(node.getStatus() != null && node.getStatus().equals(14)){
            			finishList.add(node.getExpressNo());
            		}
            		List<ExpressNode> temp = expressNodeDAO.getExpressNodeByOccurTime(node.getExpressNo(), node.getOccurTime());
            		if(temp == null || temp.size() <=0){
            			expressNodeDAO.addExpressNode(node);
            		}
            	}
            }else{
            	log.error("siteName="+orderDetails.get(0).getSiteName()+" expressNodeList size=0");
            }
        }catch(Exception e){
        	log.error(e);
        }
        
        try{
        	 //插入到物流节点表中
            addExpressNodeToTradeExpressStatus(list);
        }catch(Exception e){
        	log.error("插入到物流节点表异常",e);
        }
        
        //更新物流信息
        for (RobotOrderDetail orderDetail : orderDetails) {
            RobotOrderDetail _orderDetail = robotOrderDetailDAO.getRobotOrderDetailById(orderDetail.getId());
            if (_orderDetail == null) {
                log.error(String.format("orderDetail id:%d 没有找到", orderDetail.getId()));
                continue;
            }
            //TRAKPAK单独处理1
            if("TRAKPAK".equalsIgnoreCase(_orderDetail.getExpressCompany())&&_orderDetail.getExpressNo()!=null){
            	//如果是TRAKPAK不更新物流号
            	log.error("如果是TRAKPAK不更新物流号") ;
            	_orderDetail.setGmtModified(new Date());
            	if(!(_orderDetail.getStatus() != null && _orderDetail.getStatus().equals(100))){//100状态不用修改
                	_orderDetail.setStatus(orderDetail.getStatus());
                }
            	robotOrderDetailDAO.updateRobotOrderDetail(_orderDetail);
            	continue ;
            }
            if(!StringUtil.isBlank(_orderDetail.getExpressNo()) && !StringUtil.isBlank(orderDetail.getExpressNo()) && !_orderDetail.getExpressNo().equals(orderDetail.getExpressNo()) && _orderDetail.getCompany()==-1){
            	//换单号
            	UserTradeExpress userTradeExpress = userTradeExpressDAO.getUserTradeExpressByExpressNo(_orderDetail.getExpressNo()) ;
            	if(userTradeExpress==null){
            		log.error("还没有生成user_trade_express记录");
            		continue ;
            	}
            	String trackNo = userTradeExpress.getTrackNo() ;
            	String changeTrackNoList = userTradeExpress.getChangeTrackNoList() ;
            	UserTradeExpress update = new UserTradeExpress() ;
            	update.setId(userTradeExpress.getId());
            	
            	if(StringUtils.isBlank(changeTrackNoList)){
            		update.setChangeTrackNoList(","+orderDetail.getExpressNo());
            		update.setChangeSendStatus(0);
            	}else{
            		String[] changeTrackNoArray = changeTrackNoList.split(",") ;
            		String lastTrackNo = changeTrackNoArray[changeTrackNoArray.length-1] ;
            		if(orderDetail.getExpressNo().equalsIgnoreCase(lastTrackNo)){
            			continue ;
            		}else{
            			update.setChangeTrackNoList(changeTrackNoList+","+orderDetail.getExpressNo());
                		update.setChangeSendStatus(0);
            		}
            	}
            	userTradeExpressDAO.updateUserTradeExpressByDynamic(update) ;
            }else{
            	_orderDetail.setExpressNo(orderDetail.getExpressNo() != null ? orderDetail.getExpressNo().trim() : null);
            }
            _orderDetail.setGmtModified(new Date());
            _orderDetail.setExpressCompany(orderDetail.getExpressCompany());
            if(orderDetail.getSiteName().equalsIgnoreCase("amazon") || orderDetail.getSiteName().equalsIgnoreCase("amazon.jp") 
            		||orderDetail.getSiteName().equalsIgnoreCase("zcn")){
            	_orderDetail.setExpressStatus(4);
            }
            if(orderDetail.getSiteName().equalsIgnoreCase("6pm") && !StringUtil.isBlank(orderDetail.getExpressNo()) && !orderDetail.getExpressNo().startsWith("1Z")){
            	_orderDetail.setExpressStatus(4);
            }
            if(orderDetail.getSiteName().equalsIgnoreCase("bodyguardapotheke") && !StringUtil.isBlank(orderDetail.getExpressNo()) && orderDetail.getExpressNo().startsWith("3SCF")){
            	_orderDetail.setExpressStatus(4);
            }
            if(!(_orderDetail.getStatus() != null && _orderDetail.getStatus().equals(100))){//100状态不用修改
            	_orderDetail.setStatus(orderDetail.getStatus());
            }
            if(finishList.size()>0 && !StringUtil.isBlank(orderDetail.getExpressNo()) && finishList.contains(orderDetail.getExpressNo())){
  				_orderDetail.setExpressStatus(14);
  			}
            if(!StringUtil.isBlank(cdnUrl)){
            	_orderDetail.setPromotionFee(cdnUrl);
            }
            robotOrderDetailDAO.updateRobotOrderDetail(_orderDetail);
            log.error(String.format("收到物流爬取结果:orderNo:%s,status:%s", orderDetail.getOrderNo(), orderDetail.getStatus()));
        }
        
		//TRAKPAK单独处理2
        try{
	        for (RobotOrderDetail orderDetail : orderDetails) {
	            RobotOrderDetail _orderDetail = robotOrderDetailDAO.getRobotOrderDetailById(orderDetail.getId());
	            if (_orderDetail == null) {
	                log.error(String.format("orderDetail id:%d 没有找到", orderDetail.getId()));
	                continue;
	            }
	            String expressNo = null ;
	            if(orderDetail.getExpressNo() != null){
	            	expressNo = orderDetail.getExpressNo().trim() ;
	            }
	            if("TRAKPAK".equalsIgnoreCase(_orderDetail.getExpressCompany())&&_orderDetail.getExpressNo()!=null&&expressNo!=null){
	            	log.error("TRAKPAK单独处理") ;
	            	UserTradeExpress userTradeExpress = userTradeExpressDAO.getUserTradeExpressByExpressNo(_orderDetail.getExpressNo()) ;
	            	if(userTradeExpress==null){
	            		log.error("还没有生成user_trade_express记录");
	            		continue ;
	            	}
	            	String trackNo = userTradeExpress.getTrackNo() ;
	            	//如果爬回来的物流单号和原来一样不更改
	            	if(expressNo.equalsIgnoreCase(trackNo)){
	            		continue ;
	            	}
	            	String changeTrackNoList = userTradeExpress.getChangeTrackNoList() ;
	            	UserTradeExpress update = new UserTradeExpress() ;
	            	update.setId(userTradeExpress.getId());
	            	
	            	if(StringUtils.isBlank(changeTrackNoList)){
	            		update.setChangeTrackNoList(","+expressNo);
	            		update.setChangeSendStatus(0);
	            	}else{
	            		String[] changeTrackNoArray = changeTrackNoList.split(",") ;
	            		String lastTrackNo = changeTrackNoArray[changeTrackNoArray.length-1] ;
	            		if(expressNo.equalsIgnoreCase(lastTrackNo)){
	            			continue ;
	            		}else{
	            			update.setChangeTrackNoList(changeTrackNoList+","+expressNo);
	                		update.setChangeSendStatus(0);
	            		}
	            	}
	            	userTradeExpressDAO.updateUserTradeExpressByDynamic(update) ;
	            	//增加转单节点
					
	            }
	        }
        }catch(Exception e){
        	log.error("TRAKPAK单独处理出现异常:\t"+e);
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
    				tradeExpressStatus.setRemark(node.getName());
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
   
   public static void download(byte[] content, String filename) throws Exception {
		OutputStream os = null;
		InputStream is = null;
		try{
		    // 输入流
			is = new ByteArrayInputStream(content);
		    // 1K的数据缓冲
		    byte[] bs = new byte[1024];
		    // 读取到的数据长度
		    int len;
		    // 输出的文件流
		    os = new FileOutputStream(filename);
		    // 开始读取
		    while ((len = is.read(bs)) != -1) {
		      os.write(bs, 0, len);
		    }
		}
		catch (Throwable e){
		}finally {  
			try {  
		        if (os != null) {  
		        	os.close();  
		        } 
		    } catch (Exception e) {  
		        e.printStackTrace();  
		    }  
		    try {  
		        if (is != null) {  
		        	is.close();  
		        } 
		    } catch (Exception e) {  
		        e.printStackTrace();  
		    }  
		}
	}   
   
    
}
