package com.oversea.task.job;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.haihu.rpc.common.RpcCallback;
import com.haihu.rpc.server.RpcServerProxy;
import com.oversea.cdn.service.CdnService;
import com.oversea.rabbitmq.sender.MessageSender;
import com.oversea.task.common.TaskService;
import com.oversea.task.domain.ExchangeDefinition;
import com.oversea.task.domain.OrderAccount;
import com.oversea.task.domain.OrderCreditCard;
import com.oversea.task.domain.OrderPayAccount;
import com.oversea.task.domain.Resources;
import com.oversea.task.domain.RobotOrderDetail;
import com.oversea.task.domain.TransferClearInfo;
import com.oversea.task.domain.UserTradeAddress;
import com.oversea.task.domain.UserTradeDTL;
import com.oversea.task.domain.Zipcode;
import com.oversea.task.enums.MoneyUnits;
import com.oversea.task.mapper.ExchangeDefinitionDAO;
import com.oversea.task.mapper.GiftCardDAO;
import com.oversea.task.mapper.OrderAccountDAO;
import com.oversea.task.mapper.OrderCreditCardDAO;
import com.oversea.task.mapper.OrderDeviceDAO;
import com.oversea.task.mapper.OrderPayAccountDAO;
import com.oversea.task.mapper.ResourcesDAO;
import com.oversea.task.mapper.RobotOrderDetailDAO;
import com.oversea.task.mapper.UserTradeAddressDAO;
import com.oversea.task.mapper.UserTradeDTLDAO;
import com.oversea.task.mapper.ZipcodeDAO;
import com.oversea.task.obj.Task;
import com.oversea.task.obj.TaskDetail;
import com.oversea.task.obj.TaskResult;

public class TestOrderServiceJob implements RpcCallback{
	
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
	private ResourcesDAO resourcesDAO;
    
    private static final String TESTORDER = "TestOrder";//0信用卡
    
    public void run(){
    	log.error("==========TestOrderServiceJob begin============");
    	
    	List<Resources> resourcesList = resourcesDAO.getSaleResourceByType(TESTORDER);
    	for(Resources r:resourcesList){
	    	RobotOrderDetail orderDetail = robotOrderDetailDAO.getOrderDetailBySiteName(r.getResValue());
	        String orderNo = orderDetail.getOrderNo();
	        String expiryDate = null;
	        try {
	            String ip = orderDeviceDAO.findById(orderDetail.getDeviceId()).getDeviceIp();
	            OrderAccount acc = orderAccountDAO.findById(orderDetail.getAccountId());
	            OrderCreditCard orderCreditCard = null;
	            if (acc.getCreditCardId() != null) {
	                String cardNo = orderAccountDAO.getOrderCreditCardNoByCardId(acc.getCreditCardId());
	                acc.setCardNo(cardNo);
	                String suffixNo = orderAccountDAO.getOrderCreditSuffixNoByCardId(acc.getCreditCardId());
	                acc.setSuffixNo(suffixNo);
	                
	                orderCreditCard  = orderCreditCardDAO.getOrderCreditCardById(acc.getCreditCardId());
	                if(orderCreditCard != null){
	                	 expiryDate = orderCreditCard.getExpiryDate();
	                }
	            }
	            
	            Task task = new TaskDetail();
	            // 查询支付帐号及密码
	            if(acc.getPayAccountId() != null){
	            	OrderPayAccount orderPayAccount = orderPayAccountDAO.getOrderPayAccountById(acc.getPayAccountId());
	            	task.addParam("orderPayAccount", orderPayAccount);
	            	
	            	if(orderPayAccount.getCreditCardId() != null){
	            		 orderCreditCard  = orderCreditCardDAO.getOrderCreditCardById(orderPayAccount.getCreditCardId());
	            		 acc.setCardNo(orderCreditCard.getCardNo());
	                     acc.setSuffixNo(orderCreditCard.getSuffixNo());
	            	}
	            }
	            MoneyUnits moneyUnits = MoneyUnits.getMoneyUnitsByCode(orderDetail.getUnits());
    			ExchangeDefinition exchangeDefinition = exchangeDefinitionDAO.getExchangeDefinitionByUnits(moneyUnits.getValue());
    			BigDecimal rmb = new BigDecimal(exchangeDefinition.getRmb());
    			BigDecimal source = new BigDecimal(exchangeDefinition.getSource());
    			BigDecimal rate =  (rmb.divide(source));
    			task.addParam("rate", rate.floatValue());
	            Map<Long, String> asinCodeMap = new HashMap<Long, String>();
	            long productEntityId = orderDetail.getProductEntityId();
	            orderDetail.setPromotionCodeList("1111");
	            String asinCode = robotOrderDetailDAO.getExternalProductEntityId(productEntityId);
	            asinCodeMap.put(productEntityId, asinCode);
	            task.addParam("asinMap", asinCodeMap);
	            List<RobotOrderDetail> robotOrderDetails = new ArrayList<RobotOrderDetail>();
	            robotOrderDetails.add(orderDetail);
	            task.addParam("robotOrderDetails", robotOrderDetails);
	            task.addParam("account", acc);
	            task.addParam("isPay", false);
	            task.addParam("mallName", orderDetail.getSiteName());
	            task.addParam("expiryDate", expiryDate);
	            if(orderCreditCard != null){
	            	task.addParam("orderCreditCard", orderCreditCard);
	            }
	            task.addParam("expiryDate", expiryDate);
	            task.setGroup(ip);
	            
	            List<UserTradeDTL> list = userTradeDTLDAO.getUserTradeDTLByOrderNo(orderNo);
	
	            //固定4个地址
	            task.addParam("count", 0);
	            if(orderDetail.getCompany() != null && orderDetail.getCompany().equals(-1l)){
	            	try{
	                	// 直邮网站需要填写用户收件人信息
	                    UserTradeDTL userTradeDTL = list.get(0);
	                    UserTradeAddress userTradeAddress = userTradeAddressDAO.getUserTradeAddressByUserIdAndPayNo(userTradeDTL.getUserId(), userTradeDTL.getPayNo());
	            		if (userTradeAddress != null) {
	            			//邮编
	                		String zip = userTradeAddress.getZip() ;
	                		if(StringUtils.isBlank(zip)){
	                			//1、如果用户没有保存邮编则调用查询邮编数据库,因为用户可能只保存省市，县为空时取第一条数据
	                			List<Zipcode> zipcodeList = zipcodeDAO.getZipcodeByStateCityDistrict(userTradeAddress.getState(), userTradeAddress.getCity(), userTradeAddress.getDistrict()) ;
	                			if(zipcodeList!=null&&zipcodeList.size()>0){
	                				Zipcode zipcode = zipcodeList.get(0) ;
	                				userTradeAddress.setZip(zipcode.getZip());
	                			}else{
	                				//如果数据库中也没，就填写默认值
	                				userTradeAddress.setZip("000086");
	                			}
	                		}
	                		task.addParam("address", userTradeAddress);
	            		}else{
	            			log.error("用户[" + userTradeDTL.getUserId() + "]订单收货地址不存在[" + userTradeDTL.getPayNo() + "]");
	            		}
	                }catch(Exception e){
	                	log.error("直邮订单:"+orderDetail.getOrderNo()+"获取用户收货地址失败",e);
	                }
	            }else{
		        	TransferClearInfo transferClearInfo = robotOrderDetailDAO.getExpressAddress((long)orderDetail.getCompany());
		        	if(transferClearInfo!=null){
		            	task.addParam("expressAddress", transferClearInfo.getRecipientName());
		            	UserTradeAddress userTradeAddress = new UserTradeAddress();
		            	userTradeAddress.setAddress(transferClearInfo.getRecipientAddress());
		            	userTradeAddress.setCity(transferClearInfo.getRecipientCity());
		            	userTradeAddress.setName(transferClearInfo.getRecipientName());
		            	userTradeAddress.setState(transferClearInfo.getRecipientProvince());
		            	userTradeAddress.setZip(transferClearInfo.getRecipientZipCode());
		            	userTradeAddress.setMobile(transferClearInfo.getRecipientTel());
		            	task.addParam("address", userTradeAddress);
		        	}
	            }
	            TaskService taskService = (TaskService)rpcServerProxy.wrapProxy(TaskService.class, ip, this);
	            taskService.orderService(task);
	        } catch (Exception e) {
	            log.error("TestOrderServiceJob ORDERDETAIL:[" + orderNo + "]:" + e.getMessage(), e);
	        }
    	}
        log.error("==========TestOrderServiceJob end============");
    }

	@Override
	public void callbackAck(boolean isSuccess, Method method, Object[] objs) {
		if(isSuccess){
			try{
				Task task = (Task)objs[0];
				List<RobotOrderDetail> orderList = (List<RobotOrderDetail>) task.getParam("robotOrderDetails");
				log.info(String.format("TestOrderServiceJob提交自动下单订单号[%s],分配账号:%s:,发送机器IP:%s", orderList.get(0).getOrderNo(), orderList.get(0).getAccountId(), task.getGroup()));
				
			}catch(Exception e){
				log.error(e);
			}
		}else{
			try{
				Task task = (Task)objs[0];
				List<RobotOrderDetail> orderList = (List<RobotOrderDetail>) task.getParam("robotOrderDetails");
				log.info(String.format("TestOrderServiceJob提交自动出错下单订单号[%s],分配账号:%s:,发送机器IP:%s", orderList.get(0).getOrderNo(), orderList.get(0).getAccountId(), task.getGroup()));
			}catch(Exception e){
				log.error(e);
			}
		}
	}
	
	@Override
	public void callbackResult(Object result, Method method, Object[] objs) {
		if(result == null){
			return;
		}
		TaskResult taskResult = (TaskResult)result;
		List<RobotOrderDetail> orderDetailList = getRobotOrderDetailList(taskResult.getValue());
		log.error("TestOrderServiceJob orderDetailList sitename"+orderDetailList.get(0).getSiteName()+" order="+orderDetailList.get(0).getOrderNo()+" status="+orderDetailList.get(0).getStatus()+"promotion="+orderDetailList.get(0).getPromotionCodeListStatus());
	}
	public List<RobotOrderDetail> getRobotOrderDetailList(Object result) {
        List<RobotOrderDetail> orderDetailList = null;
        if (result instanceof RobotOrderDetail) {
            orderDetailList = new ArrayList<RobotOrderDetail>();
            orderDetailList.add((RobotOrderDetail) result);
        } else if (result instanceof List) {
            orderDetailList = (List<RobotOrderDetail>) result;
        } else {
            return null;
        }
        return orderDetailList;
    }

}
