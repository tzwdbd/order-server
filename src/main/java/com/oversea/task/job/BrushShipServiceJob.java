package com.oversea.task.job;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;

import com.haihu.rpc.common.RpcCallback;
import com.haihu.rpc.server.RpcServerProxy;
import com.oversea.rabbitmq.utils.StringUtil;
import com.oversea.task.common.TaskService;
import com.oversea.task.domain.BrushOrderDetail;
import com.oversea.task.domain.ExpressNode;
import com.oversea.task.domain.OrderAccount;
import com.oversea.task.domain.RobotOrderDetail;
import com.oversea.task.enums.AutoBuyStatus;
import com.oversea.task.mapper.BrushOrderDetailDAO;
import com.oversea.task.mapper.OrderAccountDAO;
import com.oversea.task.mapper.OrderDeviceDAO;
import com.oversea.task.mapper.RobotOrderDetailDAO;
import com.oversea.task.obj.Task;
import com.oversea.task.obj.TaskDetail;
import com.oversea.task.obj.TaskResult;

public class BrushShipServiceJob implements RpcCallback{
	
	private Logger log = Logger.getLogger(getClass());
	@Resource
	private RpcServerProxy rpcServerProxy;

    @Resource
    private BrushOrderDetailDAO brushOrderDetailDAO;
    @Resource
    private RobotOrderDetailDAO robotOrderDetailDAO;

    @Resource
    private OrderAccountDAO orderAccountDAO;

    @Resource
    private OrderDeviceDAO orderDeviceDAO;
    
    public String getScanStatus(){
    	return "1000,100,99";
    }
    
    
	
	public void run(){
		log.error("============BrushShipServiceJob begin============");
		try{
			List<BrushOrderDetail> brushOrderDetails = brushOrderDetailDAO.getBrushOrderDetailListBystatus(getScanStatus());
	    	for(BrushOrderDetail brushOrderDetail : brushOrderDetails){
    			Task task = new TaskDetail();
                OrderAccount account = orderAccountDAO.findById(brushOrderDetail.getAccountId());
                Integer deviceId = brushOrderDetail.getDeviceId();
                String ip = orderDeviceDAO.findById(deviceId).getDeviceIp();
                Map<Long, String> asinCodeMap = new HashMap<Long, String>();
                long productEntityId = brushOrderDetail.getProductEntityId();
                String asinCode = robotOrderDetailDAO.getExternalProductEntityId(productEntityId);
                asinCodeMap.put(productEntityId, asinCode);
                task.addParam("asinMap", asinCodeMap);
                task.addParam("brushOrderDetail", brushOrderDetail);
                task.addParam("account", account);
                task.setGroup(ip);
                
                TaskService taskService = (TaskService)rpcServerProxy.wrapProxy(TaskService.class, ip, this);
                taskService.burshShipService(task);
	    	}
		}catch(Exception e){
			log.error(e);
		}
        
		//处理第三方
		try{
			List<BrushOrderDetail> brushOrderDetails = brushOrderDetailDAO.getBrushOrderDetailListByThree("1000,99");
	    	for(BrushOrderDetail brushOrderDetail : brushOrderDetails){
	    		List<RobotOrderDetail> robotOrderDetails = robotOrderDetailDAO.findOrderDetailsByOrderTime(brushOrderDetail.getOrderTime());
	    		if(robotOrderDetails!=null && robotOrderDetails.size()>0){
	    			List<String> expressNos = new ArrayList<String>();
	    			for(RobotOrderDetail r:robotOrderDetails){
	    				expressNos.add(r.getExpressNo());
	    			}
	    			
	    			List<BrushOrderDetail> expressList = brushOrderDetailDAO.getBrushOrderDetailListByExpressNo(expressNos);
	    			List<String> bexpressNos = new ArrayList<String>();
	    			for(BrushOrderDetail b:expressList){
	    				bexpressNos.add(b.getExpressNo());
	    			}
	    			expressNos.removeAll(bexpressNos);
	    			if(expressNos!=null && expressNos.size()>0){
			    		brushOrderDetail.setExpressNo(expressNos.get(0));
			    		brushOrderDetail.setExpressStatus(14);
			    		if(!StringUtil.isBlank(brushOrderDetail.getReviewContent())){
			    			brushOrderDetail.setExpressStatus(5);
			        	}
			    		brushOrderDetail.setStatus(100);
			    		brushOrderDetailDAO.updateBrushOrderDetailByDynamic(brushOrderDetail);
	    			}
	    		}
	    	}
		}catch(Exception e){
			log.error(e);
		}
		log.error("============BrushShipServiceJob end=================");
	}

	@Override
	public void callbackAck(boolean isSuccess, Method method, Object[] objs) {
		// TODO Auto-generated method stub
		if(isSuccess){
			if(objs != null){
				Task task = (Task)objs[0];
				BrushOrderDetail brushOrderDetail = (BrushOrderDetail) task.getParam("brushOrderDetail");
				brushOrderDetailDAO.updateStatus(brushOrderDetail.getId(), AutoBuyStatus.AUTO_SCRIBE_ORDER_NOT_READY.getValue());
				log.error("刷单订单号:"+brushOrderDetail.getOrderNo()+"提交爬取物流详情成功"+"ip:"+task.getGroup());
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
		BrushOrderDetail brushOrderDetail =  (BrushOrderDetail) taskResult.getValue();
		 //插入物流节点
        List<String> finishList = new ArrayList<String>();
        List<ExpressNode> list = null;
        try{
            list = (List<ExpressNode>)taskResult.getParam("expressNodeList");
            if(list != null && list.size() > 0){
            	log.error("siteName="+brushOrderDetail.getSiteName()+" expressNodeList size="+list.size());
            	for(ExpressNode node : list){
            		log.error("node expressNo="+node.getExpressNo()+" occurTime="+node.getOccurTime());
            		if(node.getStatus() != null && node.getStatus().equals(14)){
            			finishList.add(node.getExpressNo());
            		}
            	}
            }else{
            	log.error("siteName="+brushOrderDetail.getSiteName()+" expressNodeList size=0");
            }
        }catch(Exception e){
        	log.error(e);
        }
		
        
        BrushOrderDetail _brushOrderDetail = brushOrderDetailDAO.getBrushOrderDetailById(brushOrderDetail.getId());
        _brushOrderDetail.setGmtModified(new Date());
        _brushOrderDetail.setExpressCompany(brushOrderDetail.getExpressCompany());
        _brushOrderDetail.setExpressNo(brushOrderDetail.getExpressNo() != null ? brushOrderDetail.getExpressNo().trim() : null);
        if(!(_brushOrderDetail.getStatus() != null && _brushOrderDetail.getStatus().equals(100))){//100状态不用修改
        	_brushOrderDetail.setStatus(brushOrderDetail.getStatus());
        }
        if(brushOrderDetail.getStatus()!=null && brushOrderDetail.getStatus()==100 && StringUtil.isBlank(_brushOrderDetail.getReviewContent())){
        	_brushOrderDetail.setExpressStatus(1);
        }
        if(finishList.size()>0 && !StringUtil.isBlank(brushOrderDetail.getExpressNo()) && finishList.contains(brushOrderDetail.getExpressNo())){
        	_brushOrderDetail.setExpressStatus(14);
        	if(!StringUtil.isBlank(_brushOrderDetail.getReviewContent())){
        		_brushOrderDetail.setExpressStatus(5);
        	}
		}
        brushOrderDetailDAO.updateBrushOrderDetailByDynamic(_brushOrderDetail);
        log.error(String.format("刷单收到物流爬取结果:orderNo:%s,status:%s", brushOrderDetail.getOrderNo(), brushOrderDetail.getStatus()));
        
	}
}
