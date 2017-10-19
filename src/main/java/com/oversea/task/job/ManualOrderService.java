package com.oversea.task.job;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.annotation.Resource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.haihu.rpc.common.RpcCallback;
import com.haihu.rpc.server.RpcServerProxy;
import com.oversea.cdn.service.CdnService;
import com.oversea.rabbitmq.sender.MessageSender;
import com.oversea.task.common.TaskService;
import com.oversea.task.domain.AutoOrderCleanCart;
import com.oversea.task.domain.AutoOrderLogin;
import com.oversea.task.domain.AutoOrderPay;
import com.oversea.task.domain.AutoOrderSelectProduct;
import com.oversea.task.domain.ExchangeBankDefinition;
import com.oversea.task.domain.ExchangeDefinition;
import com.oversea.task.domain.ExternalOrderDetail;
import com.oversea.task.domain.GiftCard;
import com.oversea.task.domain.OrderAccount;
import com.oversea.task.domain.OrderCreditCard;
import com.oversea.task.domain.OrderDevice;
import com.oversea.task.domain.OrderPayAccount;
import com.oversea.task.domain.OrderPayDetail;
import com.oversea.task.domain.Resources;
import com.oversea.task.domain.RobotOrderDetail;
import com.oversea.task.domain.TransferClearInfo;
import com.oversea.task.domain.UserTradeAddress;
import com.oversea.task.domain.UserTradeDTL;
import com.oversea.task.domain.Zipcode;
import com.oversea.task.enums.AccountStatus;
import com.oversea.task.enums.AutoBuyStatus;
import com.oversea.task.enums.MoneyUnits;
import com.oversea.task.mapper.AutoOrderCleanCartDAO;
import com.oversea.task.mapper.AutoOrderLoginDAO;
import com.oversea.task.mapper.AutoOrderPayDAO;
import com.oversea.task.mapper.AutoOrderSelectProductDAO;
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
import com.oversea.task.obj.TaskResult;
import com.oversea.task.util.IDcardUtil;
import com.oversea.task.util.MathUtil;
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
    @Resource
    private AutoOrderCleanCartDAO autoOrderCleanCartDAO;
    @Resource
    private AutoOrderSelectProductDAO autoOrderSelectProductDAO;
    @Resource
    private AutoOrderPayDAO autoOrderPayDAO;
    private ExecutorService fixedThreadPool = Executors.newFixedThreadPool(2);
    
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
        for (RobotOrderDetail o: orderDetails){
        	if(o.getStatus()==100){
        		stockOrderList.add(o);
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
            
            for (RobotOrderDetail or : orderDetails) {
            	or.setStatus(AutoBuyStatus.AUTO_ORDER_ING.getValue());
                robotOrderDetailDAO.updateRobotOrderDetailStatusById(AutoBuyStatus.AUTO_ORDER_ING.getValue(),or.getId());
            }
            
            getAutoOrderBystep(task,steps, orderDetails.get(0).getSiteName());
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
        log.error("==========handleOrder end============");
    }
    
    public void run(){
    	log.error("==========handleOrder run begin============");
    	List<RobotOrderDetail> orderDetails = robotOrderDetailDAO.getOrderDetailOrderByMallStatus();
    	
    	//按orderno 分组
		Map<String,List<RobotOrderDetail>> map = new HashMap<String, List<RobotOrderDetail>>();
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
    	
        log.error("==========handleOrder run end============");
    }
    
    private void processOrder(List<RobotOrderDetail> orderDetails) {
    	handleOrder(orderDetails.get(0).getOrderNo(), 0, 4);
    }

	private void getAutoOrderBystep(Task task, int steps, String siteName) {
		AutoOrderLogin autoOrderLogin = autoOrderLoginDAO.getOrderLoginBySiteName(siteName);
        task.addParam("autoOrderLogin", autoOrderLogin);
		if(steps==2){
			AutoOrderCleanCart autoOrderCleanCart = autoOrderCleanCartDAO.getAutoOrderCleanCartBySiteName(siteName);
			task.addParam("autoOrderCleanCart", autoOrderCleanCart);
		}else if(steps==3){
			AutoOrderCleanCart autoOrderCleanCart = autoOrderCleanCartDAO.getAutoOrderCleanCartBySiteName(siteName);
			task.addParam("autoOrderCleanCart", autoOrderCleanCart);
			AutoOrderSelectProduct autoOrderSelectProduct = autoOrderSelectProductDAO.getOrderSelectProductBySiteName(siteName);
			task.addParam("autoOrderSelectProduct", autoOrderSelectProduct);
		}else if(steps==4){
			AutoOrderCleanCart autoOrderCleanCart = autoOrderCleanCartDAO.getAutoOrderCleanCartBySiteName(siteName);
			task.addParam("autoOrderCleanCart", autoOrderCleanCart);
			AutoOrderSelectProduct autoOrderSelectProduct = autoOrderSelectProductDAO.getOrderSelectProductBySiteName(siteName);
			task.addParam("autoOrderSelectProduct", autoOrderSelectProduct);
			AutoOrderPay autoOrderPay = autoOrderPayDAO.getOrderPayBySiteName(siteName);
			task.addParam("autoOrderPay", autoOrderPay);
		}
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
				for (RobotOrderDetail r : orderList) {
	                robotOrderDetailDAO.updateRobotOrderDetailStatusById(AutoBuyStatus.AUTO_PAY_PARPARE.getValue(),r.getId());
	            }
				log.info(String.format("Auto提交自动出错下单订单号[%s],分配账号:%s:,发送机器IP:%s", orderList.get(0).getOrderNo(), orderList.get(0).getAccountId(), task.getGroup()));
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
        //成功处理
        String productIds = "";
        String prouctEntityIds = "";
        String cdnUrl = "";
        for (RobotOrderDetail orderDetail : orderDetailList) {
        	try{
        		RobotOrderDetail _orderDetail = robotOrderDetailDAO.getRobotOrderDetailById(orderDetail.getId());
                if (_orderDetail == null) {
                    log.error(String.format("orderDetail id:%d can't found", orderDetail.getId()));
                    return;
                }

                productIds += (orderDetail.getProductId()+",");
                prouctEntityIds += (orderDetail.getProductEntityId()+",");
                AutoBuyStatus autoBuyStatus = AutoBuyStatus.getAutoBuyStatusByValue(orderDetail.getStatus());
                log.error("orderDetail orderNo:[" + orderDetail.getOrderNo() + "],status=" + autoBuyStatus);

                _orderDetail.setGmtModified(new Date());
                _orderDetail.setStatus(orderDetail.getStatus() == AutoBuyStatus.AUTO_ORDER_ING.getValue() ? AutoBuyStatus.CLIENT_ORDER_TASK_STATUS_ERROR.getValue() : orderDetail.getStatus());
                _orderDetail.setTotalPrice(orderDetail.getTotalPrice());
                _orderDetail.setSinglePrice(orderDetail.getSinglePrice());
                _orderDetail.setMallOrderNo(orderDetail.getMallOrderNo());
                _orderDetail.setIsManual("NO");
                _orderDetail.setPromotionCodeListStatus(orderDetail.getPromotionCodeListStatus());
                if (_orderDetail.getStatus() == AutoBuyStatus.AUTO_PAY_SUCCESS.getValue()) {
                	if(StringUtil.isBlank(cdnUrl)){
	                	byte[] screentShot = null;
	                    try{
	                    	screentShot = (byte[]) taskResult.getParam("screentShot");
	                    	if(screentShot!=null){
	                    		log.error("上传cdn:");
	                    		//上传cdn
	                    		cdnUrl = cdnClient.saveFile(screentShot, "", "png");
	                    		log.error("cdnId:"+cdnUrl);
	                    	}else{
	                    		log.error("客户端返回byte为空");
	                    	}
	                    	
	                    }catch (Exception e) {
	            			log.error("截图出错",e);
	            		}
                	}
                	_orderDetail.setOrderImg(cdnUrl);
                    _orderDetail.setOrderTime(new Date());
                }
                _orderDetail.setRemarks(autoBuyStatus == null ? "" : autoBuyStatus.getName());
                _orderDetail.setMallExpressFee(orderDetail.getMallExpressFee());
                _orderDetail.setPromotionFee(orderDetail.getPromotionFee());
                robotOrderDetailDAO.updateRobotOrderDetail(_orderDetail);

                //通过mq发送失败的验证码给主站
                String promotionCodeList = orderDetail.getPromotionCodeList();
                String promotionCodeListStatus = orderDetail.getPromotionCodeListStatus();
                if(StringUtil.isNotEmpty(promotionCodeList) && StringUtil.isNotEmpty(promotionCodeListStatus)){
                	String[] aas = promotionCodeList.split(",");
                	String[] bbs = promotionCodeListStatus.split(",");
    				if(aas != null && bbs != null && aas.length == bbs.length){
    					for(int i = 0;i<aas.length;i++){
    						if("0".equals(bbs[i]) || "1".equals(bbs[i])){//0失效 1互斥
    							final HashMap<String, Object> map = new HashMap<String, Object>();
    							map.put("orderNo", orderDetail.getOrderNo());
    							map.put("code", aas[i]);
    							map.put("type", Integer.parseInt(bbs[i]));
    							log.error("发送失效优惠码:"+map.toString());
    							fixedThreadPool.execute(new Runnable() {
    								@Override
    								public void run() {
    									// TODO Auto-generated method stub
    									try{
    										rabbitMqMessageSender.deliver("PROMOTION_CODE_INVALID_QUEUE", "PROMOTION_CODE_INVALID_EXCHANGE", map);
    						            }catch(Throwable e){
    						            	log.error("发送失效优惠码出错",e);
    						            }
    								}
    							});
    						}
    					}
    				}
                }
        	}catch(Exception e){
        		log.error(e);
        	}
        }
        
        try{
        	if(orderDetailList != null && orderDetailList.size() > 0){
            	RobotOrderDetail orderDetail = orderDetailList.get(0);
            	if(orderDetail != null && orderDetail.getStatus() != null && orderDetail.getStatus().equals(1012)){
//            		SYN_SMS("SYN-SMS-INFO","OVERSEA-SMS","发送短信的mq"),
            		List<String> mobileList = new ArrayList<String>();
            		mobileList.add("18750206531");
            		mobileList.add("15657106651");
            		mobileList.add("18606521817");
            		String msg = String.format("商品id:%s,entity_id:%s金额溢价", productIds,prouctEntityIds);
            		log.error(msg);
            		for(String mobile : mobileList){
            			final Map<String,Object> param = new HashMap<String,Object>();
                		param.put("mobile", mobile);
                		param.put("content",msg);
                		fixedThreadPool.execute(new Runnable() {
        					@Override
        					public void run() {
        						// TODO Auto-generated method stub
        						try{
        							rabbitMqMessageSender.deliver("SYN-SMS-INFO", "OVERSEA-SMS", param);
        			            }catch(Throwable e){
        			            	log.error("发送失效优惠码出错",e);
        			            }
        					}
        				});
            		}
            	}
            }
        }catch(Exception e){}
        
        // 追加信用卡付款额度
        // 多个订单取第一个订单的总价
        try {
        	RobotOrderDetail orderDetail = orderDetailList.get(0);
        	
        	if (orderDetail.getStatus() == AutoBuyStatus.AUTO_PAY_SUCCESS.getValue()) {
        		
        		//消费流水记录
        		try {
        			addOrderPayDetailLog(orderDetailList);
				} catch (Exception e) {
					log.error("消费流水记录出错",e);
				}
        		
        		
            	OrderAccount acc = orderAccountDAO.findById(orderDetail.getAccountId());
                if (acc == null) {
                    log.error("[Account]:" + orderDetail.getAccountId() + "找不到对应的账号信息");
                    return;
                }
                
                
                OrderCreditCard orderCreditCard  = null;
                
                //表示澳洲直邮订单 
                // 查询支付帐号及密码
                if(acc.getPayAccountId() != null){
                	OrderPayAccount orderPayAccount = orderPayAccountDAO.getOrderPayAccountById(acc.getPayAccountId());
                	log.info("支付宝帐号=====>"+ orderPayAccount.getAccount()+",orderNo====>"+ orderDetail.getOrderNo());
                	if (orderPayAccount.getCreditCardId() != null) {
                		orderCreditCard = orderCreditCardDAO.getOrderCreditCardById(orderPayAccount.getCreditCardId());
                		log.info("支付宝信用卡消费金额累计=====>订单号===="+ orderDetail.getOrderNo() +",cardNo===="+ orderCreditCard.getCardNo()+",payMoney===="+orderDetail.getTotalPrice());
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
    			
    			if(orderDetail.getUnits() == null){
    				log.error("货币类型不能为空");
    				return;
    			}
    			
    			if(orderDetail.getTotalPrice() == null){
    				log.error("支付总价不能为空");
    				return;
    			}
    			
    			String exchangeMoney = getRmbPrice(orderDetail.getUnits(), orderDetail.getTotalPrice());
    			
    			log.info("商品总价===="+orderDetail.getTotalPrice()+",汇率转换成功后的金额，money====="+ exchangeMoney);
    			
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
    			Double newMoney = Double.parseDouble(MathUtil.add(currentThresh, exchangeMoney));
    			OrderCreditCard update = new OrderCreditCard();
    			update.setId(orderCreditCard.getId());
    			update.setCurrentThresh(newMoney);
    			orderCreditCardDAO.updateOrderCreditCardNew(update);
    			log.info("自动下单信用卡追加付款额度成功，订单号===="+ orderDetail.getOrderNo() +",cardNo===="+ orderCreditCard.getCardNo()+",CurrentThresh===="+newMoney);
        	}
		} catch (Exception e) {
			log.error("自动下单追加信用卡额度出现异常",e);
		}
		
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
	
	private void addOrderPayDetailLog(List<RobotOrderDetail> orderDetailList) {
		for(RobotOrderDetail _orderDetail:orderDetailList){
			OrderPayDetail orderPayDetail = new OrderPayDetail();
			RobotOrderDetail orderDetail = robotOrderDetailDAO.getRobotOrderDetailById(_orderDetail.getId());
			//orderAccount
			orderPayDetail.setAccountId(orderDetail.getAccountId());
			OrderAccount orderAccount = orderAccountDAO.findById(orderDetail.getAccountId());
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
            	if(orderPayAccount.getCreditCardId() != null && orderPayAccount.getCreditCardId()>0){
            		OrderCreditCard orderCreditCard  = orderCreditCardDAO.getOrderCreditCardById(orderPayAccount.getCreditCardId());
            		if(orderCreditCard != null){
                    	orderPayDetail.setCardNo(orderCreditCard.getCardNo());
                    	orderPayDetail.setBankName(orderCreditCard.getBankName());
                    }
            	}
            	orderPayDetail.setPayType(orderPayAccount.getAccountType());
            }
			//orderDevice
			OrderDevice orderDevice = orderDeviceDAO.findById(orderDetail.getDeviceId());
			orderPayDetail.setDeviceId(orderDetail.getDeviceId());
			orderPayDetail.setDeviceIp(orderDevice.getDeviceIp());
			
			orderPayDetail.setStatusType(0);
			orderPayDetail.setUnits(orderDetail.getUnits());
			orderPayDetail.setPrice(orderDetail.getTotalPrice());
			orderPayDetail.setSkuPrice(orderDetail.getMyPrice());
			orderPayDetail.setNum(orderDetail.getNum());
			orderPayDetail.setSolePrice(String.valueOf(Float.parseFloat(orderDetail.getMyPrice())*orderDetail.getNum()));
			orderPayDetail.setSiteName(orderDetail.getSiteName());
			orderPayDetail.setOrderNo(orderDetail.getOrderNo());
			orderPayDetail.setOperator("auto");
			orderPayDetail.setGmtCreate(new Date());
			orderPayDetail.setOrderTime(orderDetail.getOrderTime());
			orderPayDetail.setCompany(orderDetail.getCompany());
			orderPayDetail.setIsDirect(orderDetail.getIsDirect());
			orderPayDetail.setIsManual(orderDetail.getIsManual());
			orderPayDetail.setGroupNumber(orderDetail.getGroupNumber());
			orderPayDetail.setProductEntityId(orderDetail.getProductEntityId());
			if(StringUtil.isBlank(orderDetail.getIsStockpile())){
				orderPayDetail.setOrderStatus(0);
			}else if("1".equals(orderDetail.getIsStockpile())){
				orderPayDetail.setOrderStatus(1);
			}
			List<UserTradeDTL> userTrades = userTradeDTLDAO.getUserTradeDTLByOrderNo(orderDetail.getOrderNo());
			if(userTrades!=null && userTrades.size()>0){
				String payNo = userTrades.get(0).getPayNo();
				Date time = userTradeDTLDAO.getPayTime(payNo);
				if(time!=null){
					orderPayDetail.setPayTime(time);
				}else{
					orderPayDetail.setPayTime(orderDetail.getOrderTime());
				}
			}
			String rmbPrice = getRmbPrice(orderDetail.getUnits(),orderDetail.getTotalPrice());
			orderPayDetail.setRmbPrice(rmbPrice);
			orderPayDetailDao.addOrderPayDetail(orderPayDetail);
		}
		
	}
	
	private String getRmbPrice(String units, String totalPrice) {
		try {
			String exchangeMoney = totalPrice;
			units = MoneyUnits.getMoneyUnitsByCode(units).getValue();
			if(!"¥".equals(units)){
				ExchangeBankDefinition exchangeBankDefinition = exchangeBankDefinitionDAO.getExchangeBankDefinitionByUnit(units);
				BigDecimal rmb = new BigDecimal(exchangeBankDefinition.getRmb());
				BigDecimal source = new BigDecimal(exchangeBankDefinition.getSource());
				BigDecimal rate =  (rmb.divide(source));
				
				exchangeMoney = MathUtil.mul(String.valueOf(totalPrice), String.valueOf(rate));
			}
			return exchangeMoney;
		} catch (Exception e) {
			log.error("获取rmb出错");
		}
		
		return null;
	}
	
}
