package com.oversea.task.job;

import java.lang.reflect.Method;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.haihu.rpc.common.RpcCallback;
import com.haihu.rpc.server.RpcServerProxy;
import com.oversea.cdn.service.CdnService;
import com.oversea.rabbitmq.sender.MessageSender;
import com.oversea.rabbitmq.utils.StringUtil;
import com.oversea.task.common.TaskService;
import com.oversea.task.domain.BrushInfo;
import com.oversea.task.domain.BrushOrderDetail;
import com.oversea.task.domain.ExchangeDefinition;
import com.oversea.task.domain.OrderAccount;
import com.oversea.task.domain.OrderCreditCard;
import com.oversea.task.domain.OrderDevice;
import com.oversea.task.domain.OrderPayAccount;
import com.oversea.task.domain.OrderPayDetail;
import com.oversea.task.domain.TransferClearInfo;
import com.oversea.task.domain.UserTradeAddress;
import com.oversea.task.enums.AutoBuyStatus;
import com.oversea.task.enums.MoneyUnits;
import com.oversea.task.mapper.BrushInfoDAO;
import com.oversea.task.mapper.BrushOrderDetailDAO;
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
import com.oversea.task.util.MathUtil;

public class BrushOrderServiceJob implements RpcCallback{
	
	private Logger log = Logger.getLogger(getClass());
	
	@Resource
    private BrushOrderDetailDAO brushOrderDetailDAO;
	@Resource
    private BrushInfoDAO brushInfoDAO;
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
    
    public void run(){
    	log.error("==========BrushOrderServiceJob begin============");
    	BrushOrderDetail brushOrderDetail = brushOrderDetailDAO.getBrushOrderDetailByDate(new Date());
        if(brushOrderDetail!=null){
	        String orderNo = brushOrderDetail.getOrderNo();
	        String expiryDate = null;
	        
	        
	        try {
	            String ip = orderDeviceDAO.findById(brushOrderDetail.getDeviceId()).getDeviceIp();
	            OrderAccount acc = orderAccountDAO.findById(brushOrderDetail.getAccountId());
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
	            BrushInfo brushInfo = brushInfoDAO.getBrushInfoById(brushOrderDetail.getBrushInfoId());
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
	            Map<Long, String> asinCodeMap = new HashMap<Long, String>();
                long productEntityId = brushOrderDetail.getProductEntityId();
                String asinCode = robotOrderDetailDAO.getExternalProductEntityId(productEntityId);
                asinCodeMap.put(productEntityId, asinCode);
                task.addParam("asinMap", asinCodeMap);
	            task.addParam("brushOrderDetail", brushOrderDetail);
	            task.addParam("brushInfo", brushInfo);
	            task.addParam("account", acc);
	            task.addParam("isPay", true);
	            task.addParam("mallName", brushOrderDetail.getSiteName());
	            task.addParam("expiryDate", expiryDate);
	            if(orderCreditCard != null){
	            	task.addParam("orderCreditCard", orderCreditCard);
	            }
	            task.addParam("expiryDate", expiryDate);
	            task.setGroup(ip);
	            
	            
	
	            //固定4个地址
	            task.addParam("count", "1");
	            
	        	TransferClearInfo transferClearInfo = robotOrderDetailDAO.getExpressAddress((long)brushOrderDetail.getCompany());
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
	        	brushOrderDetailDAO.updateStatus(brushOrderDetail.getId(),AutoBuyStatus.AUTO_ORDER_ING.getValue());
	            TaskService taskService = (TaskService)rpcServerProxy.wrapProxy(TaskService.class, ip, this);
	            taskService.burshOrderService(task);
	        } catch (Exception e) {
	            log.error("ORDERDETAIL:[" + orderNo + "]:" + e.getMessage(), e);
	        }
        }
        log.error("==========brushServiceJob end============");
    }

	@Override
	public void callbackAck(boolean isSuccess, Method method, Object[] objs) {
		// TODO Auto-generated method stub
		if(isSuccess){
			try{
				Task task = (Task)objs[0];
				BrushOrderDetail brushOrderDetail = (BrushOrderDetail) task.getParam("brushOrderDetail");
				//brushOrderDetailDAO.updateStatus(brushOrderDetail.getId(),AutoBuyStatus.AUTO_ORDER_ING.getValue());
				log.info(String.format("刷单提交自动下单订单号[%s],分配账号:%s:,发送机器IP:%s", brushOrderDetail.getOrderNo(), brushOrderDetail.getAccountId(), task.getGroup()));
				
			}catch(Exception e){
				log.error(e);
			}
		}else{
			try{
				Task task = (Task)objs[0];
				BrushOrderDetail brushOrderDetail = (BrushOrderDetail) task.getParam("brushOrderDetail");
				brushOrderDetailDAO.updateStatus(brushOrderDetail.getId(),500);
				log.info(String.format("刷单提交出错自动下单订单号[%s],分配账号:%s:,发送机器IP:%s", brushOrderDetail.getOrderNo(), brushOrderDetail.getAccountId(), task.getGroup()));
				
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
		BrushOrderDetail brushOrderDetail = (BrushOrderDetail) taskResult.getValue();
        //成功处理
    	try{
    		BrushOrderDetail _brushOrderDetail = brushOrderDetailDAO.getBrushOrderDetailById(brushOrderDetail.getId());
            if (_brushOrderDetail == null) {
                log.error(String.format("brushOrderDetail id:%d can't found", brushOrderDetail.getId()));
                return;
            }

            AutoBuyStatus autoBuyStatus = AutoBuyStatus.getAutoBuyStatusByValue(brushOrderDetail.getStatus());
            log.error("brushOrderDetail orderNo:[" + brushOrderDetail.getOrderNo() + "],status=" + autoBuyStatus);

            _brushOrderDetail.setGmtModified(new Date());
            _brushOrderDetail.setStatus(brushOrderDetail.getStatus() == AutoBuyStatus.AUTO_ORDER_ING.getValue() ? AutoBuyStatus.CLIENT_ORDER_TASK_STATUS_ERROR.getValue() : brushOrderDetail.getStatus());
            _brushOrderDetail.setTotalPrice(brushOrderDetail.getTotalPrice());
            _brushOrderDetail.setSinglePrice(brushOrderDetail.getSinglePrice());
            _brushOrderDetail.setMallOrderNo(brushOrderDetail.getMallOrderNo());
            _brushOrderDetail.setIsManual("NO");
            if (_brushOrderDetail.getStatus() == AutoBuyStatus.AUTO_PAY_SUCCESS.getValue()) {
            	_brushOrderDetail.setOrderTime(new Date());
            }
            _brushOrderDetail.setRemarks(autoBuyStatus == null ? "" : autoBuyStatus.getName());
            brushOrderDetailDAO.updateBrushOrderDetailByDynamic(_brushOrderDetail);
    	}catch(Exception e){
    		log.error(e);
    	}
        
        
        // 追加信用卡付款额度
        // 多个订单取第一个订单的总价
        try {
        	
        	if (brushOrderDetail.getStatus() == AutoBuyStatus.AUTO_PAY_SUCCESS.getValue()) {
        		
        		//消费流水记录
        		try {
        			addOrderPayDetailLog(brushOrderDetail);
				} catch (Exception e) {
					log.error("消费流水记录出错",e);
				}
        		
        		
            	OrderAccount acc = orderAccountDAO.findById(brushOrderDetail.getAccountId());
                if (acc == null) {
                    log.error("[Account]:" + brushOrderDetail.getAccountId() + "找不到对应的账号信息");
                    return;
                }
                if(("amazon").equalsIgnoreCase(brushOrderDetail.getSiteName()) || ("amazon.jp").equalsIgnoreCase(brushOrderDetail.getSiteName())){
                	log.error("美亚日亚使用礼品卡，不需要追加信用卡消费额度");
                	if(!StringUtils.isBlank(brushOrderDetail.getBalanceWb()) && !StringUtil.isBlank(brushOrderDetail.getPayType()) && "giftcard".equals(brushOrderDetail.getPayType())){
                		orderAccountDAO.updateOrderAccountBalanceWb(acc.getAccountId(), Double.parseDouble(brushOrderDetail.getBalanceWb()));
                	}
                	return;
                }
                
                
                OrderCreditCard orderCreditCard  = null;
                
                //表示澳洲直邮订单 
                // 查询支付帐号及密码
                if(acc.getPayAccountId() != null){
                	OrderPayAccount orderPayAccount = orderPayAccountDAO.getOrderPayAccountById(acc.getPayAccountId());
                	log.info("支付宝帐号=====>"+ orderPayAccount.getAccount()+",orderNo====>"+ brushOrderDetail.getOrderNo());
                	if (orderPayAccount.getCreditCardId() != null) {
                		orderCreditCard = orderCreditCardDAO.getOrderCreditCardById(orderPayAccount.getCreditCardId());
                		log.info("支付宝信用卡消费金额累计=====>订单号===="+ brushOrderDetail.getOrderNo() +",cardNo===="+ orderCreditCard.getCardNo()+",payMoney===="+brushOrderDetail.getTotalPrice());
                	}
                } else {
                	if (acc.getCreditCardId() != null) {
                		orderCreditCard = orderCreditCardDAO.getOrderCreditCardById(acc.getCreditCardId());
                	}
                }
                
                
                if (orderCreditCard == null) { 
            		log.error("查找不到对应的信用卡信息");
            		return;
    			}
    			
    			if(brushOrderDetail.getUnits() == null){
    				log.error("货币类型不能为空");
    				return;
    			}
    			
    			MoneyUnits moneyUnits = MoneyUnits.getMoneyUnitsByCode(brushOrderDetail.getUnits());
    			ExchangeDefinition exchangeDefinition = exchangeDefinitionDAO.getExchangeDefinitionByUnits(moneyUnits.getValue());
    			if (exchangeDefinition ==null) {
    				log.error("查询不到相应的汇率"+ brushOrderDetail.getUnits());
    				return;
    			}
    			
    			if(brushOrderDetail.getTotalPrice() == null){
    				log.error("支付总价不能为空");
    				return;
    			}
    			Double exchangeMoney = MathUtil.divCelling(Double.valueOf(brushOrderDetail.getTotalPrice()) * exchangeDefinition.getRmb(), Double.valueOf(exchangeDefinition.getSource()));
    			log.info("商品总价===="+brushOrderDetail.getTotalPrice()+",汇率转换成功后的金额，money====="+ exchangeMoney);
    			
    			if(exchangeMoney == null){
    				log.error("转换金额不能为空");
    				return;
    			}
    			
    			String currentThresh = "";
     			if(orderCreditCard.getCurrentThresh() == null){
     				currentThresh = "0";
     			} else {
     				currentThresh = orderCreditCard.getCurrentThresh().toString();
     			}
    			Double newMoney = Double.parseDouble(MathUtil.add(currentThresh, exchangeMoney.toString()));
    			OrderCreditCard update = new OrderCreditCard();
    			update.setId(orderCreditCard.getId());
    			update.setCurrentThresh(newMoney);
    			orderCreditCardDAO.updateOrderCreditCardNew(update);
    			log.info("自动下单信用卡追加付款额度成功，订单号===="+ brushOrderDetail.getOrderNo() +",cardNo===="+ orderCreditCard.getCardNo()+",CurrentThresh===="+newMoney);
        	}
		} catch (Exception e) {
			log.error("自动下单追加信用卡额度出现异常",e);
		}
        
	}
	
	private void addOrderPayDetailLog(BrushOrderDetail _brushOrderDetail) {
		OrderPayDetail orderPayDetail = new OrderPayDetail();
		BrushOrderDetail brushOrderDetail = brushOrderDetailDAO.getBrushOrderDetailById(_brushOrderDetail.getId());
		//orderAccount
		orderPayDetail.setAccountId(brushOrderDetail.getAccountId());
		OrderAccount orderAccount = orderAccountDAO.findById(brushOrderDetail.getAccountId());
		orderPayDetail.setPayAccount(orderAccount.getPayAccount());
		//creditCard
		orderPayDetail.setCreditCardId(orderAccount.getCreditCardId());
		if (orderAccount.getCreditCardId() != null && orderAccount.getCreditCardId()>0) {
            
			OrderCreditCard orderCreditCard  = orderCreditCardDAO.getOrderCreditCardById(orderAccount.getCreditCardId());
            if(orderCreditCard != null){
            	orderPayDetail.setCardNo(orderCreditCard.getCardNo());
            	orderPayDetail.setBankName(orderCreditCard.getBankName());
            	orderPayDetail.setPayType("credit");
            }
        }
        if(orderAccount.getPayAccountId() != null && orderAccount.getPayAccountId()>0){
        	OrderPayAccount orderPayAccount = orderPayAccountDAO.getOrderPayAccountById(orderAccount.getPayAccountId());
        	orderPayDetail.setCreditCardId(orderPayAccount.getCreditCardId());
        	if(orderPayAccount.getCreditCardId() != null && orderAccount.getCreditCardId()>0){
        		OrderCreditCard orderCreditCard  = orderCreditCardDAO.getOrderCreditCardById(orderPayAccount.getCreditCardId());
        		if(orderCreditCard != null){
                	orderPayDetail.setCardNo(orderCreditCard.getCardNo());
                	orderPayDetail.setBankName(orderCreditCard.getBankName());
                }
        	}
        	orderPayDetail.setPayType(orderPayAccount.getAccountType());
        }
		//orderDevice
		OrderDevice orderDevice = orderDeviceDAO.findById(brushOrderDetail.getDeviceId());
		orderPayDetail.setDeviceId(brushOrderDetail.getDeviceId());
		orderPayDetail.setDeviceIp(orderDevice.getDeviceIp());
		
		orderPayDetail.setStatusType(0);
		orderPayDetail.setUnits(brushOrderDetail.getUnits());
		orderPayDetail.setPrice(brushOrderDetail.getTotalPrice());
		orderPayDetail.setSkuPrice(brushOrderDetail.getSinglePrice());
		orderPayDetail.setNum(brushOrderDetail.getNum());
		orderPayDetail.setSolePrice(String.valueOf(Float.parseFloat(brushOrderDetail.getSinglePrice())*brushOrderDetail.getNum()));
		orderPayDetail.setSiteName(brushOrderDetail.getSiteName());
		orderPayDetail.setOrderNo(brushOrderDetail.getOrderNo());
		orderPayDetail.setOperator("auto");
		orderPayDetail.setGmtCreate(new Date());
		orderPayDetail.setOrderTime(brushOrderDetail.getOrderTime());
		orderPayDetail.setCompany((long)brushOrderDetail.getCompany());
		orderPayDetail.setIsDirect(brushOrderDetail.getIsDirect());
		orderPayDetail.setIsManual(brushOrderDetail.getIsManual());
		orderPayDetail.setGroupNumber(0);
		orderPayDetail.setProductEntityId(brushOrderDetail.getProductEntityId());
		orderPayDetail.setOrderStatus(2);
		if("giftcard".equals(brushOrderDetail.getPayType())){
			orderPayDetail.setStartBalance(String.valueOf(orderAccount.getBalanceWb()));
			orderPayDetail.setEndBalance(String.valueOf(_brushOrderDetail.getBalanceWb()));
		}
		orderPayDetail.setPayType(brushOrderDetail.getPayType());
		orderPayDetail.setPayTime(brushOrderDetail.getDispatchTime());
		String rmbPrice = getRmbPrice(brushOrderDetail.getUnits(),brushOrderDetail.getTotalPrice());
		orderPayDetail.setRmbPrice(rmbPrice);
		orderPayDetailDao.addOrderPayDetail(orderPayDetail);
		
	}
	
	private String getRmbPrice(String units, String totalPrice) {
		try {
			MoneyUnits moneyUnits = MoneyUnits.getMoneyUnitsByCode(units);
			ExchangeDefinition exchangeDefinition = exchangeDefinitionDAO.getExchangeDefinitionByUnits(moneyUnits.getValue());
			Double exchangeMoney = MathUtil.divCelling(Double.valueOf(totalPrice) * exchangeDefinition.getRmb(), Double.valueOf(exchangeDefinition.getSource()));
			return String.valueOf(exchangeMoney);
		} catch (Exception e) {
			log.error("获取rmb出错");
		}
		
		return null;
	}


}
