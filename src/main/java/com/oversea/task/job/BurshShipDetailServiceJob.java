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
import com.oversea.task.domain.BrushOrderDetail;
import com.oversea.task.domain.ExpressNode;
import com.oversea.task.domain.OrderAccount;
import com.oversea.task.domain.RobotOrderDetail;
import com.oversea.task.domain.TradeExpressStatus;
import com.oversea.task.enums.AutoBuyStatus;
import com.oversea.task.mapper.BrushOrderDetailDAO;
import com.oversea.task.mapper.ExpressNodeDAO;
import com.oversea.task.mapper.ExpressSpiderDAO;
import com.oversea.task.mapper.OrderAccountDAO;
import com.oversea.task.mapper.OrderDeviceDAO;
import com.oversea.task.mapper.RobotOrderDetailDAO;
import com.oversea.task.mapper.TradeExpressStatusDAO;
import com.oversea.task.mapper.UserTradeExpressDAO;
import com.oversea.task.obj.Task;
import com.oversea.task.obj.TaskDetail;
import com.oversea.task.obj.TaskResult;
import com.oversea.task.util.OrderUtil;

/**
 * 爬取物流详情
 * @author boliu
 *
 */
public class BurshShipDetailServiceJob implements RpcCallback{
	
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
    
    @Resource
    private ExpressSpiderDAO expressSpiderDAO;
    
    @Resource
    private ExpressNodeDAO expressNodeDAO;
    
    @Resource
    private TradeExpressStatusDAO tradeExpressStatusDAO;
    
    @Resource
    private UserTradeExpressDAO userTradeExpressDAO;
	
	public void run(){
		log.error("============BurshShipDetailServiceJob begin============");
		try{
			List<BrushOrderDetail> brushOrderDetails = brushOrderDetailDAO.getBrushOrderDetailListByExpressstatus("5,6");
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
        
		log.error("============BurshShipDetailServiceJob end=================");
	}

	@Override
	public void callbackAck(boolean isSuccess, Method method, Object[] objs) {
		// TODO Auto-generated method stub
		if(isSuccess){
			if(objs != null){
				Task task = (Task)objs[0];
				BrushOrderDetail brushOrderDetail = (BrushOrderDetail) task.getParam("brushOrderDetail");
				log.error("刷单订单号:"+brushOrderDetail.getOrderNo()+"提交爬取物流详情成功"+"ip:"+task.getGroup());
			}
		}else{
			if(objs != null){
				Task task = (Task)objs[0];
				BrushOrderDetail brushOrderDetail = (BrushOrderDetail) task.getParam("brushOrderDetail");
				log.error("订单号:"+brushOrderDetail.getOrderNo()+"提交爬取物流失败");
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
        
        BrushOrderDetail _brushOrderDetail = brushOrderDetailDAO.getBrushOrderDetailById(brushOrderDetail.getId());
        _brushOrderDetail.setGmtModified(new Date());
        if(AutoBuyStatus.AUTO_FEED_BACK_SUCCESS.getValue()==brushOrderDetail.getStatus()){
        	_brushOrderDetail.setExpressStatus(14);
    		if(!StringUtil.isBlank(_brushOrderDetail.getReviewContent())){
        		_brushOrderDetail.setExpressStatus(6);
        	}
        }
        if(AutoBuyStatus.AUTO_REVIEW_SUCCESS.getValue()==brushOrderDetail.getStatus()){
        	_brushOrderDetail.setExpressStatus(14);
        }
        
        brushOrderDetailDAO.updateBrushOrderDetailByDynamic(_brushOrderDetail);
        log.error(String.format("刷单收到物流爬取结果:orderNo:%s,status:%s", brushOrderDetail.getOrderNo(), brushOrderDetail.getStatus()));
        
	}
	
}
