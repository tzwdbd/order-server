package com.oversea.task.job;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;

import com.haihu.rpc.common.RpcCallback;
import com.haihu.rpc.server.RpcServerProxy;
import com.oversea.task.common.TaskService;
import com.oversea.task.domain.ExternalOrderDetail;
import com.oversea.task.domain.OrderAccount;
import com.oversea.task.enums.AutoBuyStatus;
import com.oversea.task.mapper.ExpressNodeDAO;
import com.oversea.task.mapper.ExpressSpiderDAO;
import com.oversea.task.mapper.ExternalOrderDetailDAO;
import com.oversea.task.mapper.OrderAccountDAO;
import com.oversea.task.mapper.OrderDeviceDAO;
import com.oversea.task.mapper.ResourcesDAO;
import com.oversea.task.mapper.TradeExpressStatusDAO;
import com.oversea.task.mapper.UserTradeExpressDAO;
import com.oversea.task.obj.Task;
import com.oversea.task.obj.TaskDetail;
import com.oversea.task.obj.TaskResult;

public class ExternalShipServiceJob implements RpcCallback{
	
	private Logger log = Logger.getLogger(getClass());
	@Resource
	private RpcServerProxy rpcServerProxy;

	@Resource
    private ExternalOrderDetailDAO externalOrderDetailDAO;

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
    
    public String getScanStatus(){
    	return AutoBuyStatus.AUTO_SCRIBE_ORDER_NOT_READY.getValue() +
    			"," + AutoBuyStatus.AUTO_PAY_SUCCESS.getValue();
    }
    
	public void run(){
		log.error("============ExternalShipServiceJob begin============");
		try{
			List<ExternalOrderDetail> externalOrderDetails = externalOrderDetailDAO.findExternalOrderDetailsForSpiderExpress(getScanStatus());
			//按orderno 分组
			LinkedHashMap<String,List<ExternalOrderDetail>> map = new LinkedHashMap<String, List<ExternalOrderDetail>>();
			for(ExternalOrderDetail externaOrderDeatil:externalOrderDetails){
				if(!map.containsKey(externaOrderDeatil.getSaleOrderCode())){
					List<ExternalOrderDetail> list = new ArrayList<ExternalOrderDetail>();
					list.add(externaOrderDeatil);
					map.put(externaOrderDeatil.getSaleOrderCode(), list);
				}else{
					map.get(externaOrderDeatil.getSaleOrderCode()).add(externaOrderDeatil);
				}
			}
			if(map.size() > 0){
	    		for (Map.Entry<String,List<ExternalOrderDetail>> entry : map.entrySet()) {  
	    			List<ExternalOrderDetail> temp = entry.getValue();
	    			processOrder(temp);
	    		}
	    	}
	    	
		}catch(Exception e){
			log.error(e);
		}
        
		log.error("============ExternalShipServiceJob end=================");
	}
	

	private void processOrder(List<ExternalOrderDetail> list) {
		if(list != null && list.size() > 0){
			Task task = new TaskDetail();
			ExternalOrderDetail externalOrderDetail = list.get(0);
            OrderAccount account = orderAccountDAO.findById(externalOrderDetail.getAccountId());
            Integer deviceId = externalOrderDetail.getDeviceId();
            String ip = orderDeviceDAO.findById(deviceId).getDeviceIp();
            Map<Long, String> asinCodeMap = new HashMap<Long, String>();
            for (ExternalOrderDetail detail : list) {
                long productEntityId = detail.getId();
                asinCodeMap.put(productEntityId, detail.getMallProductCode());
            }
            if((new Date().getTime()-externalOrderDetail.getOrderTime().getTime())/1000/60<120){
            	return;
            }
            task.addParam("asinMap", asinCodeMap);
            task.addParam("externalOrderDetails", list);
            task.addParam("account", account);
            task.setGroup(ip);
            
            TaskService taskService = (TaskService)rpcServerProxy.wrapProxy(TaskService.class, ip, this);
            taskService.externalShip(task);
		}
	}

	@Override
	public void callbackAck(boolean isSuccess, Method method, Object[] objs) {
		// TODO Auto-generated method stub
		if(isSuccess){
			if(objs != null){
				Task task = (Task)objs[0];
				List<ExternalOrderDetail> externalOrderDetailList = (List<ExternalOrderDetail>)task.getParam("externalOrderDetails");
				log.error("订单号:"+externalOrderDetailList.get(0).getSaleOrderCode()+"提交爬取物流成功"+"ip:"+task.getGroup());
				for(ExternalOrderDetail externalOrderDetail : externalOrderDetailList){
					externalOrderDetailDAO.updateStatus(AutoBuyStatus.AUTO_SCRIBE_ORDER_NOT_READY.getValue(), externalOrderDetail.getId());
				}
			}
		}else{
			if(objs != null){
				Task task = (Task)objs[0];
				List<ExternalOrderDetail> externalOrderDetailList = (List<ExternalOrderDetail>)task.getParam("externalOrderDetails");
				log.error("订单号:"+externalOrderDetailList.get(0).getSaleOrderCode()+"提交爬取物流失败");
			}
		}
	}

	@Override
	public void callbackResult(Object result, Method method, Object[] objs) {
		
		TaskResult taskResult = (TaskResult)result;
		if(taskResult == null){
			return;
		}
        
		List<ExternalOrderDetail> externalOrderDetailList  = (List<ExternalOrderDetail>) taskResult.getValue();
        //更新物流信息
        for (ExternalOrderDetail externalOrderDetail : externalOrderDetailList) {
        	ExternalOrderDetail _externalOrderDetail = externalOrderDetailDAO.getExternalOrderDetailById(externalOrderDetail.getId());
            if (_externalOrderDetail == null) {
                log.error(String.format("externalOrderDetail id:%d 没有找到", externalOrderDetail.getId()));
                continue;
            }
            
            _externalOrderDetail.setExpressNo(externalOrderDetail.getExpressNo() != null ? externalOrderDetail.getExpressNo().trim() : null);
            _externalOrderDetail.setGmtModified(new Date());
            _externalOrderDetail.setExpressCompany(externalOrderDetail.getExpressCompany());
        	_externalOrderDetail.setStatus(externalOrderDetail.getStatus());
        	externalOrderDetailDAO.updateExternalOrderDetailByDynamic(_externalOrderDetail);
            log.error(String.format("externalOrderDetail收到物流爬取结果:orderNo:%s,status:%s", externalOrderDetail.getSaleOrderCode(), externalOrderDetail.getStatus()));
        }
        
	}
	
    
}
