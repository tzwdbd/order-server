package com.oversea.task.job;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.haihu.rpc.common.RpcCallback;
import com.haihu.rpc.server.RpcServerProxy;
import com.oversea.cdn.service.CdnService;
import com.oversea.rabbitmq.sender.MessageSender;
import com.oversea.task.common.TaskService;
import com.oversea.task.domain.AutoOrderLogin;
import com.oversea.task.domain.OrderAccount;
import com.oversea.task.domain.RobotOrderDetail;
import com.oversea.task.mapper.AutoOrderLoginDAO;
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
        task.addParam("account", account);
        task.setGroup(ip);
        
        AutoOrderLogin autoOrderLogin = autoOrderLoginDAO.getOrderLoginBySiteName(orderDetails.get(0).getSiteName());
        task.addParam("autoOrderLogin", autoOrderLogin);
        if(steps>1){
        	
        }
        TaskService taskService = (TaskService)rpcServerProxy.wrapProxy(TaskService.class, ip, this);
        taskService.manualShip(task);
        log.error("==========ManualShipService end============");
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
	public void callbackResult(Object arg0, Method arg1, Object[] arg2) {
		
		
	}
	
}