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
import org.springframework.stereotype.Component;

import com.haihu.rpc.common.RpcCallback;
import com.haihu.rpc.server.RpcServerProxy;
import com.oversea.cdn.service.CdnService;
import com.oversea.rabbitmq.sender.MessageSender;
import com.oversea.rabbitmq.utils.StringUtil;
import com.oversea.task.common.TaskService;
import com.oversea.task.domain.AutoOrderExpressDetail;
import com.oversea.task.domain.AutoOrderLogin;
import com.oversea.task.domain.AutoOrderScribeExpress;
import com.oversea.task.domain.OrderAccount;
import com.oversea.task.domain.RobotOrderDetail;
import com.oversea.task.mapper.AutoOrderExpressDetailDAO;
import com.oversea.task.mapper.AutoOrderLoginDAO;
import com.oversea.task.mapper.AutoOrderScribeExpressDAO;
import com.oversea.task.mapper.ExchangeDefinitionDAO;
import com.oversea.task.mapper.GiftCardDAO;
import com.oversea.task.mapper.OrderAccountDAO;
import com.oversea.task.mapper.OrderCreditCardDAO;
import com.oversea.task.mapper.OrderDeviceDAO;
import com.oversea.task.mapper.OrderPayAccountDAO;
import com.oversea.task.mapper.OrderPayDetailDAO;
import com.oversea.task.mapper.RobotOrderDetailDAO;
import com.oversea.task.mapper.UserTradeAddressDAO;
import com.oversea.task.mapper.UserTradeDTLDAO;
import com.oversea.task.mapper.ZipcodeDAO;
import com.oversea.task.obj.Task;
import com.oversea.task.obj.TaskDetail;
import com.oversea.task.obj.TaskResult;
import com.oversea.task.util.ThreeDES;
@Component
public class ManualShipService implements RpcCallback{
	
	private Logger log = Logger.getLogger(getClass());
	
	@Resource
    private RobotOrderDetailDAO robotOrderDetailDAO;

    @Resource
    private OrderAccountDAO orderAccountDAO;

    @Resource
    private OrderDeviceDAO orderDeviceDAO;
    
    @Resource
    private OrderCreditCardDAO orderCreditCardDAO;
    
    @Resource
    private UserTradeDTLDAO userTradeDTLDAO;

    @Resource
   	private UserTradeAddressDAO userTradeAddressDAO;
       
    @Resource 
   	private ZipcodeDAO zipcodeDAO;
    
    @Resource
    private OrderPayAccountDAO orderPayAccountDAO;
    
    @Resource
    private RpcServerProxy rpcServerProxy;
    
    @Resource
    private ExchangeDefinitionDAO exchangeDefinitionDAO;
    
    @Resource
    private MessageSender rabbitMqMessageSender;
    @Resource
    private CdnService cdnClient;
    @Resource
    private GiftCardDAO giftCardDAO;
    @Resource
    private OrderPayDetailDAO orderPayDetailDao;
    @Resource
    private AutoOrderLoginDAO autoOrderLoginDAO;
    @Resource
    private AutoOrderScribeExpressDAO autoOrderScribeExpressDAO;
    @Resource
    private AutoOrderExpressDetailDAO autoOrderExpressDetailDAO;
    
    public void handleShip(String orderNo,int groupNumber,int steps){
    	log.error("==========handleShip begin============");
    	List<RobotOrderDetail> orderDetails = robotOrderDetailDAO.getOrderDetailByOrderNoGroupNumber(orderNo, groupNumber);
      
    	Task task = new TaskDetail();
        OrderAccount account = orderAccountDAO.findById(orderDetails.get(0).getAccountId());
        Integer deviceId = orderDetails.get(0).getDeviceId();
        String ip = orderDeviceDAO.findById(deviceId).getDeviceIp();
        Map<Long, String> asinCodeMap = new HashMap<Long, String>();
        for (RobotOrderDetail detail : orderDetails) {
            long productEntityId = detail.getProductEntityId();
            String asinCode = robotOrderDetailDAO.getExternalProductEntityId(productEntityId);
            asinCodeMap.put(productEntityId, asinCode);
        }
        task.addParam("asinMap", asinCodeMap);
        task.addParam("robotOrderDetails", orderDetails);
        account.setLoginPwd(ThreeDES.decryptMode(account.getLoginPwd()));
        task.addParam("account", account);
        task.setGroup(ip);
        
        getAutoOrderBystep(task, steps, orderDetails.get(0).getSiteName());
        log.error("商城为"+orderDetails.get(0).getSiteName()+"steps="+steps);
        TaskService taskService = (TaskService)rpcServerProxy.wrapProxy(TaskService.class, ip, this);
        taskService.manualShip(task);
        log.error("==========ManualShipService end============");
    }
    
    public void run(){
    	log.error("==========handleShip run begin============");
    	List<RobotOrderDetail> orderDetails = robotOrderDetailDAO.getOrderDetailByMallStatus();
    	//按orderno 分组
    	LinkedHashMap<String,List<RobotOrderDetail>> map = new LinkedHashMap<String, List<RobotOrderDetail>>();
		for(RobotOrderDetail robotOrderDetail:orderDetails){
			if(!map.containsKey(robotOrderDetail.getOrderNo())){
				List<RobotOrderDetail> list = new ArrayList<RobotOrderDetail>();
				list.add(robotOrderDetail);
				map.put(robotOrderDetail.getOrderNo(), list);
			}else{
				map.get(robotOrderDetail.getOrderNo()).add(robotOrderDetail);
			}
		}
		if(map.size() > 0){
    		for (Map.Entry<String,List<RobotOrderDetail>> entry : map.entrySet()) {  
    			List<RobotOrderDetail> temp = entry.getValue();
    			processOrder(temp);
    		}
    	}
    	
        log.error("==========ManualShipService end============");
    }
    
    private void processOrder(List<RobotOrderDetail> orderDetails) {
    	handleShip(orderDetails.get(0).getOrderNo(), 0, 3);
    }

	@Override
	public void callbackAck(boolean isSuccess, Method method, Object[] objs) {
		// TODO Auto-generated method stub
		if(isSuccess){
			if(objs != null){
				Task task = (Task)objs[0];
				List<RobotOrderDetail> orderDetailList = (List<RobotOrderDetail>)task.getParam("robotOrderDetails");
				log.error("手动订单号:"+orderDetailList.get(0).getOrderNo()+"提交爬取物流成功"+"ip:"+task.getGroup());
			}
		}else{
			if(objs != null){
				Task task = (Task)objs[0];
				List<RobotOrderDetail> orderDetailList = (List<RobotOrderDetail>)task.getParam("robotOrderDetails");
				log.error("手动订单号:"+orderDetailList.get(0).getOrderNo()+"提交爬取物流失败");
			}
		}
	}

	@Override
	public void callbackResult(Object result, Method method, Object[] objs) {
		TaskResult taskResult = (TaskResult)result;
		if(taskResult == null){
			return;
		}
        
        List<RobotOrderDetail> orderDetails = (List<RobotOrderDetail>) taskResult.getValue();
        log.error("callbackResult订单号:"+orderDetails.get(0).getOrderNo()+"--->status="+orderDetails.get(0).getStatus()+"--->express="+orderDetails.get(0).getExpressNo()+"--->company="+orderDetails.get(0).getExpressCompany());
        
        for(RobotOrderDetail orderDetail:orderDetails){
        	RobotOrderDetail _orderDetail = robotOrderDetailDAO.getRobotOrderDetailById(orderDetail.getId());
        	_orderDetail.setStatus(orderDetail.getStatus());
    		if(orderDetail.getStatus()==100){
        		if(!StringUtil.isBlank(orderDetail.getExpressNo())){
        			_orderDetail.setExpressNo(orderDetail.getExpressNo());
        		}
        		if(!StringUtil.isBlank(orderDetail.getExpressCompany())){
        			_orderDetail.setExpressCompany(orderDetail.getExpressCompany());
        		}
    		}
    		_orderDetail.setGmtModified(new Date());
			robotOrderDetailDAO.updateRobotOrderDetail(_orderDetail);
        }
	}
	
	
	private void getAutoOrderBystep(Task task, int steps, String siteName) {
		AutoOrderLogin autoOrderLogin = autoOrderLoginDAO.getOrderLoginBySiteName(siteName);
        task.addParam("autoOrderLogin", autoOrderLogin);
		if(steps==2){
			AutoOrderScribeExpress autoOrderScribeExpress = autoOrderScribeExpressDAO.getAutoOrderScribeExpressBySiteName(siteName);
			task.addParam("autoOrderScribeExpress", autoOrderScribeExpress);
		}else if(steps==3){
			AutoOrderScribeExpress autoOrderScribeExpress = autoOrderScribeExpressDAO.getAutoOrderScribeExpressBySiteName(siteName);
			task.addParam("autoOrderScribeExpress", autoOrderScribeExpress);
			AutoOrderExpressDetail autoOrderExpressDetail = autoOrderExpressDetailDAO.getAutoOrderExpressDetailBySiteName(siteName);
			task.addParam("autoOrderExpressDetail", autoOrderExpressDetail);
		}
	}
}
