package com.oversea.task.job;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.haihu.rpc.common.RpcCallback;
import com.haihu.rpc.server.RpcServerProxy;
import com.oversea.cdn.service.CdnService;
import com.oversea.rabbitmq.sender.MessageSender;
import com.oversea.task.common.TaskService;
import com.oversea.task.domain.AutoOrderLogin;
import com.oversea.task.domain.ExchangeDefinition;
import com.oversea.task.domain.OrderAccount;
import com.oversea.task.domain.OrderCreditCard;
import com.oversea.task.domain.OrderPayAccount;
import com.oversea.task.domain.RobotOrderDetail;
import com.oversea.task.domain.TransferClearInfo;
import com.oversea.task.domain.UserTradeAddress;
import com.oversea.task.domain.UserTradeDTL;
import com.oversea.task.domain.Zipcode;
import com.oversea.task.enums.MoneyUnits;
import com.oversea.task.mapper.AutoOrderLoginDAO;
import com.oversea.task.mapper.ExchangeBankDefinitionDAO;
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
import com.oversea.task.util.IDcardUtil;
import com.oversea.task.util.StringUtil;
@Component
public class ManualOrderService implements RpcCallback{
	
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
    private ExchangeBankDefinitionDAO exchangeBankDefinitionDAO;
    @Resource
    private AutoOrderLoginDAO autoOrderLoginDAO;
    
    public void handleOrder(String orderNo,int groupNumber,int steps){
    	log.error("==========handleOrder begin============");
    	List<RobotOrderDetail> orderDetails = robotOrderDetailDAO.getOrderDetailByOrderNoGroupNumber(orderNo, groupNumber);
            
        String expiryDate = null;
        
        //订单没有全部扫过来
        List<UserTradeDTL> list = userTradeDTLDAO.getUserTradeDTLByOrderNo(orderNo);
        List<RobotOrderDetail> list0 = robotOrderDetailDAO.getRobotOrderDetailByOrderNo(orderNo);
        if(list != null && list0 != null && list0.size() < list.size()){
        	log.error("订单orderNo = "+orderNo+"还没完全扫描到order_detail表里");
        	return;
        }
        
        List<RobotOrderDetail> stockOrderList = new ArrayList<RobotOrderDetail>();
        
        for (RobotOrderDetail o: orderDetails){
        	if(o.getFromId()!=null && o.getFromId()>0){
        		stockOrderList.add(o);
        		log.error("囤货匹配订单orderNo = "+o.getOrderNo()+"remove掉 list size＝"+orderDetails.size());
        	}
        }
        orderDetails.removeAll(stockOrderList);
        if (orderDetails == null || orderDetails.size() == 0) {
            log.error("RobotOrderDetail对应的订单为空...");
            return;
        }
        RobotOrderDetail orderDetail = orderDetails.get(0);
        try {
            String ip = orderDeviceDAO.findById(orderDetail.getDeviceId()).getDeviceIp();
            OrderAccount acc = orderAccountDAO.findById(orderDetail.getAccountId());
            if (acc == null) {
                log.error("[Account]:" + orderDetail.getAccountId() + "找不到对应的账号信息");
                return;
            }
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
            task.addParam("robotOrderDetails", orderDetails);
            task.addParam("account", acc);
            task.addParam("isPay", true);
            task.addParam("mallName", orderDetail.getSiteName());
            task.addParam("expiryDate", expiryDate);
            if(orderCreditCard != null){
            	task.addParam("orderCreditCard", orderCreditCard);
            }
            task.addParam("expiryDate", expiryDate);
            task.setGroup(ip);
            
            AutoOrderLogin autoOrderLogin = autoOrderLoginDAO.getOrderLoginBySiteName(orderDetails.get(0).getSiteName());
            task.addParam("autoOrderLogin", autoOrderLogin);
            if(steps>1){
            	
            }
            //获取当前账户在当天下的第几单
            int cnt = robotOrderDetailDAO.countOrderDetailForAutoOrderByAccountId(orderNo, orderDetail.getAccountId(), orderDetail.getId());
            //固定4个地址
            task.addParam("count", String.valueOf(cnt));
            
            //表示直邮订单
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
                		if("zcn".equals(orderDetail.getSiteName())){
                			userTradeAddress.setIdCardBack(userTradeAddress.getIdCardBack()+"@1c_1e_1wh_250w");
                			userTradeAddress.setIdCardFront(userTradeAddress.getIdCardFront()+"@1c_1e_1wh_250w");
                			String code = "20260216";
                			if(StringUtil.isBlank(userTradeAddress.getIdCardBack())){
                				return;
                			}else{
                				String codes = "";
                				try {
                					codes = IDcardUtil.getEndDateByIdCardBack(userTradeAddress.getIdCardBack());
								} catch (Exception e) {
									codes = "";
									log.error("IDcardUtil code异常",e);
								}
                    			
                    			if(StringUtil.isBlank(codes)){
                    				userTradeAddress.setExpireDate(code);
                    			}else{
                    				userTradeAddress.setExpireDate(codes);
                    			}
                    			log.error("userTradeAddress用户[" + userTradeAddress.getPayNo() + "]的过期时间为[" + code + "]");
                			}
                		}
            		}else{
            			log.error("用户[" + userTradeDTL.getUserId() + "]订单收货地址不存在[" + userTradeDTL.getPayNo() + "]");
            		}
                }catch(Exception e){
                	log.error("直邮订单:"+orderDetail.getOrderNo()+"获取用户收货地址失败",e);
                }
            }else{
            	TransferClearInfo transferClearInfo = robotOrderDetailDAO.getExpressAddress(orderDetail.getCompany());
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
            taskService.manualOrder(task);
        } catch (Exception e) {
            log.error("ORDERDETAIL:[" + orderNo + "]:" + e.getMessage(), e);
        }
        log.error("==========AutoOrderServiceJob end============");
    }

	@Override
	public void callbackAck(boolean isSuccess, Method method, Object[] objs) {
		if(isSuccess){
			try{
				Task task = (Task)objs[0];
				List<RobotOrderDetail> orderList = (List<RobotOrderDetail>) task.getParam("robotOrderDetails");
				log.info(String.format("Auto提交自动下单订单号[%s],分配账号:%s:,发送机器IP:%s", orderList.get(0).getOrderNo(), orderList.get(0).getAccountId(), task.getGroup()));
			}catch(Exception e){
				log.error(e);
			}
		}else{
			try{
				Task task = (Task)objs[0];
				List<RobotOrderDetail> orderList = (List<RobotOrderDetail>) task.getParam("robotOrderDetails");
				log.info(String.format("Auto提交自动出错下单订单号[%s],分配账号:%s:,发送机器IP:%s", orderList.get(0).getOrderNo(), orderList.get(0).getAccountId(), task.getGroup()));
			}catch(Exception e){
				log.error(e);
			}
		}
	}

	@Override
	public void callbackResult(Object arg0, Method arg1, Object[] arg2) {
		
		
	}
	
}
