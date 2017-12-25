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

import com.haihu.rpc.common.RpcCallback;
import com.haihu.rpc.server.RpcServerProxy;
import com.oversea.cdn.service.CdnService;
import com.oversea.rabbitmq.sender.MessageSender;
import com.oversea.task.common.TaskService;
import com.oversea.task.domain.ExchangeBankDefinition;
import com.oversea.task.domain.ExchangeDefinition;
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
import com.oversea.task.mapper.ExchangeBankDefinitionDAO;
import com.oversea.task.mapper.ExchangeDefinitionDAO;
import com.oversea.task.mapper.GiftCardDAO;
import com.oversea.task.mapper.OrderAccountDAO;
import com.oversea.task.mapper.OrderCreditCardDAO;
import com.oversea.task.mapper.OrderDeviceDAO;
import com.oversea.task.mapper.OrderPayAccountDAO;
import com.oversea.task.mapper.OrderPayDetailDAO;
import com.oversea.task.mapper.ResourcesDAO;
import com.oversea.task.mapper.RobotOrderDetailDAO;
import com.oversea.task.mapper.UserTradeAddressDAO;
import com.oversea.task.mapper.UserTradeDTLDAO;
import com.oversea.task.mapper.ZipcodeDAO;
import com.oversea.task.obj.Task;
import com.oversea.task.obj.TaskDetail;
import com.oversea.task.obj.TaskResult;
import com.oversea.task.util.IDcardUtil;
import com.oversea.task.util.MathUtil;
import com.oversea.task.util.OrderUtil;
import com.oversea.task.util.StringUtil;
import com.oversea.task.util.ThreeDES;

public class OrderServiceJob implements RpcCallback{
	
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
	private ResourcesDAO resourcesDAO;
    
    private ExecutorService fixedThreadPool = Executors.newFixedThreadPool(2);
    
    private static final String PAYTYPE = "payType";//0信用卡
    
    public void run(){
    	log.error("==========OrderServiceJob begin============");
    	List<RobotOrderDetail> orderDetails = robotOrderDetailDAO.findOrderDetailForAutoOrder(AutoBuyStatus.AUTO_PAY_PARPARE.getValue());
    	List<List<RobotOrderDetail>> waittingForPlaceOrders = OrderUtil.getRobotOrderDetailListGroupByAccountId(orderDetails);

        for (List<RobotOrderDetail> orderList : waittingForPlaceOrders) {
            if (orderList == null || orderList.size() == 0) {
                log.error("RobotOrderDetail对应的订单为空...");
                continue;
            }
            
            RobotOrderDetail firstOrderDetail = orderList.get(0);
            String orderNo = firstOrderDetail.getOrderNo();
            String expiryDate = null;
            
            //订单没有全部扫过来
            List<UserTradeDTL> list = userTradeDTLDAO.getUserTradeDTLByOrderNo(orderNo);
            List<UserTradeDTL> list1 = new ArrayList<UserTradeDTL>();
            List<RobotOrderDetail> list0 = robotOrderDetailDAO.getRobotOrderDetailByOrderNo(orderNo);
            for(UserTradeDTL utdtl:list){
            	if(utdtl.getStockStatus()<3){
            		list1.add(utdtl);
            	}
            }
            if(list != null && list0 != null && list0.size() < list1.size()){
            	log.error("订单orderNo = "+orderNo+"还没完全扫描到order_detail表里");
            	continue;
            }
            
            List<RobotOrderDetail> stockOrderList = new ArrayList<RobotOrderDetail>();
            
            for (RobotOrderDetail o: orderList){
            	if(o.getFromId()!=null && o.getFromId()>0){
            		stockOrderList.add(o);
            		log.error("囤货匹配订单orderNo = "+o.getOrderNo()+"remove掉 list size＝"+orderList.size());
            	}
            }
            for (RobotOrderDetail o: orderList){
            	if(o.getStatus()==100){
            		stockOrderList.add(o);
            		log.error("100订单orderNo = "+o.getOrderNo()+"remove掉 list size＝"+orderList.size());
            	}
            }
            orderList.removeAll(stockOrderList);
            if (orderList == null || orderList.size() == 0) {
                log.error("RobotOrderDetail对应的订单为空...");
                continue;
            }
            
            try {
                String ip = orderDeviceDAO.findById(firstOrderDetail.getDeviceId()).getDeviceIp();
                OrderAccount acc = orderAccountDAO.findById(firstOrderDetail.getAccountId());
                if (acc == null) {
                    log.error("[Account]:" + firstOrderDetail.getAccountId() + "找不到对应的账号信息");
                    continue;
                }
                OrderCreditCard orderCreditCard = null;
                if (acc.getCreditCardId() != null) {
                    String cardNo = orderAccountDAO.getOrderCreditCardNoByCardId(acc.getCreditCardId());
                    acc.setCardNo(cardNo);
                    String suffixNo = orderAccountDAO.getOrderCreditSuffixNoByCardId(acc.getCreditCardId());
                    acc.setSuffixNo(ThreeDES.decryptMode(suffixNo));
                    
                    orderCreditCard  = orderCreditCardDAO.getOrderCreditCardById(acc.getCreditCardId());
                    if(orderCreditCard != null){
                    	 expiryDate = ThreeDES.decryptMode(orderCreditCard.getExpiryDate());
                    }
                }
                
                Task task = new TaskDetail();
                
                // 查询支付帐号及密码
                if(acc.getPayAccountId() != null){
                	OrderPayAccount orderPayAccount = orderPayAccountDAO.getOrderPayAccountById(acc.getPayAccountId());
                	orderPayAccount.setLoginPassword(ThreeDES.decryptMode(orderPayAccount.getLoginPassword()));
                	orderPayAccount.setPayPassword(ThreeDES.decryptMode(orderPayAccount.getPayPassword()));
                	task.addParam("orderPayAccount", orderPayAccount);
                	
                	if(orderPayAccount.getCreditCardId() != null){
                		 orderCreditCard  = orderCreditCardDAO.getOrderCreditCardById(orderPayAccount.getCreditCardId());
                		 acc.setCardNo(orderCreditCard.getCardNo());
                         acc.setSuffixNo(ThreeDES.decryptMode(orderCreditCard.getSuffixNo()));
                	}
                }
                MoneyUnits moneyUnits = MoneyUnits.getMoneyUnitsByCode(firstOrderDetail.getUnits());
    			ExchangeDefinition exchangeDefinition = exchangeDefinitionDAO.getExchangeDefinitionByUnits(moneyUnits.getValue());
    			BigDecimal rmb = new BigDecimal(exchangeDefinition.getRmb());
    			BigDecimal source = new BigDecimal(exchangeDefinition.getSource());
    			BigDecimal rate =  (rmb.divide(source));
    			task.addParam("rate", rate.floatValue());
                task.addParam("robotOrderDetails", orderList);
                acc.setLoginPwd(ThreeDES.decryptMode(acc.getLoginPwd()));
                task.addParam("account", acc);
                task.addParam("isPay", true);
                task.addParam("mallName", firstOrderDetail.getSiteName());
                if(orderCreditCard != null){
                	task.addParam("orderCreditCard", orderCreditCard);
                }
                task.addParam("expiryDate", expiryDate);
                task.setGroup(ip);
                boolean mark = false;
                List<Resources> resourcesList = resourcesDAO.getSaleResourceByType(PAYTYPE);
                for(Resources r:resourcesList){
                	if(firstOrderDetail.getSiteName().equalsIgnoreCase(r.getName()) && "1".equals(r.getResValue())){
                		task.addParam("type", "1");
                    	List<GiftCard> giftCardList = giftCardDAO.getPassWordCard(firstOrderDetail.getSiteName());
                    	if(giftCardList.size()==0){
                    		mark = true;
                    		break;
                    	}else{
                    		for(GiftCard giftCard : giftCardList){
                        		giftCardDAO.updateGiftCardProcessStatus(giftCard.getId(), "yes");
                        	}
                    		task.addParam("giftCardList", giftCardList);
                    	}
                    	break;
                	}
                }
                if(mark){
                	continue;
                }
                for (RobotOrderDetail orderDetail : orderList) {
                    orderDetail.setStatus(AutoBuyStatus.AUTO_ORDER_ING.getValue());
                    robotOrderDetailDAO.updateRobotOrderDetailStatusById(AutoBuyStatus.AUTO_ORDER_ING.getValue(),orderDetail.getId());
                }

                //获取当前账户在当天下的第几单
                int cnt = robotOrderDetailDAO.countOrderDetailForAutoOrderByAccountId(orderNo, firstOrderDetail.getAccountId(), firstOrderDetail.getId());
                //固定4个地址
                task.addParam("count", String.valueOf(cnt));
                
                //表示直邮订单
                if(firstOrderDetail.getCompany() != null && firstOrderDetail.getCompany().equals(-1l)){
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
                    		if("zcn".equals(firstOrderDetail.getSiteName())){
//                    			if(firstOrderDetail.getAccountId()==23815){
//                    				userTradeAddress.setName("王雅娟");
//                    				userTradeAddress.setIdCardBack("http://img.haihu.com/011612140135113ae18846d999f87e5e55ec4364.png@1c_1e_1wh_250w");
//                    				userTradeAddress.setIdCardFront("http://img.haihu.com/01161214cc725fe7a5614d9dbadc23603cec413f.png@1c_1e_1wh_250w");
//                    				userTradeAddress.setExpireDate("20350720");
//                    				userTradeAddress.setIdCard("430103198110040048");
//                    			}else if(firstOrderDetail.getAccountId()==22810){
//                    				userTradeAddress.setIdCardBack("http://img.haihu.com/01161214449fc9f6e6ed4af1ae9f324e5a454ae0.png@1c_1e_1wh_250w");
//                    				userTradeAddress.setIdCardFront("http://img.haihu.com/01161214298bf88711744d0e8f46ec4b00690518.png@1c_1e_1wh_250w");
//                    				userTradeAddress.setExpireDate("20350629");
//                    				userTradeAddress.setName("李志存");
//                    				userTradeAddress.setIdCard("440224198002010959");
//                    			}else{
	                    			userTradeAddress.setIdCardBack(userTradeAddress.getIdCardBack()+"@1c_1e_1wh_250w");
	                    			userTradeAddress.setIdCardFront(userTradeAddress.getIdCardFront()+"@1c_1e_1wh_250w");
	                    			String code = "20260216";
	                    			if(StringUtil.isBlank(userTradeAddress.getIdCardBack())){
	                    				continue;
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
                    			//}
                    		}
                		}else{
                			log.error("用户[" + userTradeDTL.getUserId() + "]订单收货地址不存在[" + userTradeDTL.getPayNo() + "]");
                		}
                    }catch(Exception e){
                    	log.error("直邮订单:"+firstOrderDetail.getOrderNo()+"获取用户收货地址失败",e);
                    }
                }else{
                	TransferClearInfo transferClearInfo = robotOrderDetailDAO.getExpressAddress(firstOrderDetail.getCompany());
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
                log.error("ORDERDETAIL:[" + orderNo + "]:" + e.getMessage(), e);
            }
        }
        log.error("==========OrderServiceJob end============");
    }

	@Override
	public void callbackAck(boolean isSuccess, Method method, Object[] objs) {
		// TODO Auto-generated method stub
		if(isSuccess){
			try{
				Task task = (Task)objs[0];
				List<RobotOrderDetail> orderList = (List<RobotOrderDetail>) task.getParam("robotOrderDetails");
				RobotOrderDetail orderDetail = null;
				for (RobotOrderDetail r : orderList) {
					if(orderDetail == null){
						orderDetail = r;
					}
	                //robotOrderDetailDAO.updateRobotOrderDetailStatusById(AutoBuyStatus.AUTO_ORDER_ING.getValue(),r.getId());
	            }
				log.info(String.format("提交自动下单订单号[%s],分配账号:%s:,发送机器IP:%s", orderDetail.getOrderNo(), orderDetail.getAccountId(), task.getGroup()));
				
			}catch(Exception e){
				log.error(e);
			}
		}else{
			try{
				Task task = (Task)objs[0];
				List<RobotOrderDetail> orderList = (List<RobotOrderDetail>) task.getParam("robotOrderDetails");
				RobotOrderDetail orderDetail = null;
				for (RobotOrderDetail r : orderList) {
					if(orderDetail == null){
						orderDetail = r;
					}
	                robotOrderDetailDAO.updateRobotOrderDetailStatusById(AutoBuyStatus.AUTO_PAY_PARPARE.getValue(),r.getId());
	            }
				log.info(String.format("提交自动出错下单订单号[%s],分配账号:%s:,发送机器IP:%s", orderDetail.getOrderNo(), orderDetail.getAccountId(), task.getGroup()));
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

                //如果礼品卡不足,则账户变更状态
                if (orderDetail.getStatus() == AutoBuyStatus.AUTO_PAY_GIFTCARD_IS_TAKEOFF.getValue()) {
                    orderAccountDAO.updateOrderAccountStatus(orderDetail.getAccountId(), AccountStatus.PayGiftcardIsTakeoff.getValue(),orderDetail.getTotalPrice());
                }
                
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
        	//处理victoriassecret
        	List<Resources> resourcesList = resourcesDAO.getSaleResourceByType(PAYTYPE);
             for(Resources r:resourcesList){
             	if(orderDetail.getSiteName().equalsIgnoreCase(r.getName()) && "1".equals(r.getResValue())){
		           	 try{
		           		List<GiftCard> giftCardList = (List<GiftCard>) taskResult.getParam("giftCardList");
		           		if(giftCardList!=null){
		               		for(GiftCard giftCard : giftCardList){
		           				giftCardDAO.updateGiftCardProcessStatus(giftCard.getId(), "no");
		                   	}
		           		}
		                	
		                }catch (Exception e) {
		        			log.error("giftCardList",e);
		        		}
	           	 break;
             	}
             }
        	
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
                if(("amazon").equalsIgnoreCase(orderDetail.getSiteName()) || ("amazon.jp").equalsIgnoreCase(orderDetail.getSiteName())){
                	log.error("美亚日亚使用礼品卡，不需要追加信用卡消费额度");
                	if(!StringUtils.isBlank(orderDetail.getBalanceWb())){
                		orderAccountDAO.updateOrderAccountBalanceWb(acc.getAccountId(), Double.parseDouble(orderDetail.getBalanceWb()));
                	}
                	return;
                }
                
                for(Resources r:resourcesList){
                 	if(orderDetail.getSiteName().equalsIgnoreCase(r.getName()) && "1".equals(r.getResValue())){
	                	 try{
	                		List<GiftCard> giftCardList = (List<GiftCard>) taskResult.getParam("giftCardList");
	                		if(giftCardList!=null){
		                		for(GiftCard giftCard : giftCardList){
		                			if("yes".equals(giftCard.getIsUsed())){
		                				giftCard.setIsUsed("no");
		                				giftCard.setIsProcess("no");
		                   				giftCardDAO.updateGiftCard(giftCard);
		                			}
		                    	}
	                		}
	                     	
	                     }catch (Exception e) {
	             			log.error("giftCardList",e);
	             		}
	                	break;
                 	}
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
			if(("amazon").equalsIgnoreCase(orderDetail.getSiteName()) || ("amazon.jp").equalsIgnoreCase(orderDetail.getSiteName())){
				orderPayDetail.setStartBalance(String.valueOf(orderAccount.getBalanceWb()));
				orderPayDetail.setEndBalance(String.valueOf(_orderDetail.getBalanceWb()));
				orderPayDetail.setPayType("giftcard");
	        }
			List<Resources> resourcesList = resourcesDAO.getSaleResourceByType(PAYTYPE);
			for(Resources r:resourcesList){
             	if(orderDetail.getSiteName().equalsIgnoreCase(r.getName()) && "1".equals(r.getResValue())){
             		orderPayDetail.setPayType("giftcard");;
             		break;
             	}
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

//	@Override
//	public void callbackResult(Object result, Method method, Object[] objs) {
//		// TODO Auto-generated method stub
//		try{
//			if(result != null){
//				TaskResult taskResult = (TaskResult)result;
//				if(taskResult == null){
//					return;
//				}
//				List<RobotOrderDetail> orderDetailList = (List<RobotOrderDetail>) taskResult.getValue();
//				if(orderDetailList == null || orderDetailList.size() == 0){
//					return;
//				}
//				
//
//		        //成功处理
//		        for (RobotOrderDetail orderDetail : orderDetailList) {
//		            RobotOrderDetail _orderDetail = robotOrderDetailDAO.getRobotOrderDetailById(orderDetail.getId());
//		            if (_orderDetail == null) {
//		                log.error(String.format("orderDetail id:%d can't found", orderDetail.getId()));
//		                continue;
//		            }
//		            if(_orderDetail.getStatus() == AutoBuyStatus.AUTO_PAY_SUCCESS.getValue()){
//		            	log.error(String.format("orderDetail id:%d 已经入库", orderDetail.getId()));
//		            	continue;
//		            }
//
//		            AutoBuyStatus autoBuyStatus = AutoBuyStatus.getAutoBuyStatusByValue(orderDetail.getStatus());
//		            log.error("orderDetail orderNo:[" + orderDetail.getOrderNo() + "],status=" + autoBuyStatus);
//
//		            _orderDetail.setGmtModified(new Date());
//		            _orderDetail.setStatus(orderDetail.getStatus());
//		            _orderDetail.setTotalPrice(orderDetail.getTotalPrice());
//		            _orderDetail.setSinglePrice(orderDetail.getSinglePrice());
//		            _orderDetail.setMallOrderNo(orderDetail.getMallOrderNo());
//		            _orderDetail.setIsManual("NO");
//		            _orderDetail.setPromotionCodeListStatus(orderDetail.getPromotionCodeListStatus());
//		            if (_orderDetail.getStatus() == AutoBuyStatus.AUTO_PAY_SUCCESS.getValue()) {
//		                _orderDetail.setOrderTime(new Date());
//		            }
//		            _orderDetail.setRemarks(autoBuyStatus == null ? "" : autoBuyStatus.getName());
//		            robotOrderDetailDAO.updateRobotOrderDetail(_orderDetail);
//
//		            //如果礼品卡不足,则账户变更状态
//		            if (orderDetail.getStatus() == AutoBuyStatus.AUTO_PAY_GIFTCARD_IS_TAKEOFF.getValue()) {
//		                orderAccountDAO.updateOrderAccountStatus(orderDetail.getAccountId(), AccountStatus.PayGiftcardIsTakeoff.getValue(),orderDetail.getTotalPrice());
//		            }
//		            
//		            //通过mq发送失败的验证码给主站
//		            String promotionCodeList = orderDetail.getPromotionCodeList();
//		            String promotionCodeListStatus = orderDetail.getPromotionCodeListStatus();
//		            if(StringUtil.isNotEmpty(promotionCodeList) && StringUtil.isNotEmpty(promotionCodeListStatus)){
//		            	String[] aas = promotionCodeList.split(",");
//		            	String[] bbs = promotionCodeListStatus.split(",");
//						if(aas != null && bbs != null && aas.length == bbs.length){
//							for(int i = 0;i<aas.length;i++){
//								if("0".equals(bbs[i]) || "1".equals(bbs[i])){//0失效 1互斥
//									final HashMap<String, Object> map = new HashMap<String, Object>();
//									map.put("orderNo", orderDetail.getOrderNo());
//									map.put("code", aas[i]);
//									map.put("type", Integer.parseInt(bbs[i]));
//									log.error("发送失效优惠码:"+map.toString());
//									fixedThreadPool.execute(new Runnable() {
//										@Override
//										public void run() {
//											// TODO Auto-generated method stub
//											try{
//												rabbitMqMessageSender.deliver("PROMOTION_CODE_INVALID_QUEUE", "PROMOTION_CODE_INVALID_EXCHANGE", map);
//								            }catch(Throwable e){
//								            	log.error("发送失效优惠码出错",e);
//								            }
//										}
//									});
//								}
//							}
//						}
//		            }
//		            
//		        }
//		        
//		        // 追加信用卡付款额度
//		        // 多个订单取第一个订单的总价
//		        try {
//		        	RobotOrderDetail orderDetail = orderDetailList.get(0);
//		        	if (orderDetail.getStatus() == AutoBuyStatus.AUTO_PAY_SUCCESS.getValue()) {
//		        		if(("amazon").equalsIgnoreCase(orderDetail.getSiteName()) || ("amazon.jp").equalsIgnoreCase(orderDetail.getSiteName())){
//		            		log.error("美亚日亚使用礼品卡，不需要追加信用卡消费额度");
//		            		return;
//		            	}
//
//		            	OrderAccount acc = orderAccountDAO.findById(orderDetail.getAccountId());
//		                if (acc == null) {
//		                    log.error("[Account]:" + orderDetail.getAccountId() + "找不到对应的账号信息");
//		                    return;
//		                }
//		                
//		                OrderCreditCard orderCreditCard  = null;
//		                // 查询支付帐号及密码
//		                if(acc.getPayAccountId() != null){
//		                	OrderPayAccount orderPayAccount = orderPayAccountDAO.getOrderPayAccountById(acc.getPayAccountId());
//		                	if (orderPayAccount.getCreditCardId() != null) {
//		                		orderCreditCard = orderCreditCardDAO.getOrderCreditCardById(orderPayAccount.getCreditCardId());
//		                	}
//		                } else {
//		                	if (acc.getCreditCardId() != null) {
//		                		orderCreditCard = orderCreditCardDAO.getOrderCreditCardById(acc.getCreditCardId());
//		                	}
//		                }
//		                
//		                if (orderCreditCard == null) { 
//		            		log.error("查找不到对应的信用卡信息");
//		            		return;
//		    			}
//		    			
//		    			if(orderDetail.getUnits() == null){
//		    				log.error("货币类型不能为空");
//		    				return;
//		    			}
//		    			
//		    			MoneyUnits moneyUnits = MoneyUnits.getMoneyUnitsByCode(orderDetail.getUnits());
//		    			ExchangeDefinition exchangeDefinition = exchangeDefinitionDAO.getExchangeDefinitionByUnits(moneyUnits.getValue());
//		    			if (exchangeDefinition ==null) {
//		    				log.error("查询不到相应的汇率"+ orderDetail.getUnits());
//		    				return;
//		    			}
//		    			
//		    			if(orderDetail.getTotalPrice() == null){
//		    				log.error("支付总价不能为空");
//		    				return;
//		    			}
//		    			Double exchangeMoney = MathUtil.divCelling(Double.valueOf(orderDetail.getTotalPrice()) * exchangeDefinition.getRmb(), Double.valueOf(exchangeDefinition.getSource()));
//		    			log.info("商品总价===="+orderDetail.getTotalPrice()+",汇率转换成功后的金额，money====="+ exchangeMoney);
//		    			
//		    			if(exchangeMoney == null){
//		    				log.error("转换金额不能为空");
//		    				return;
//		    			}
//		    			
//		    			String currentThresh = "";
//		     			if(orderCreditCard.getCurrentThresh() == null){
//		     				currentThresh = "0";
//		     			} else {
//		     				currentThresh = orderCreditCard.getCurrentThresh().toString();
//		     			}
//		    			Double newMoney = Double.parseDouble(MathUtil.add(currentThresh, exchangeMoney.toString()));
//		    			OrderCreditCard update = new OrderCreditCard();
//		    			update.setId(orderCreditCard.getId());
//		    			update.setCurrentThresh(newMoney);
//		    			orderCreditCardDAO.updateOrderCreditCardNew(update);
//		    			log.info("自动下单信用卡追加付款额度成功，订单号===="+ orderDetail.getOrderNo() +",cardNo===="+ orderCreditCard.getCardNo()+",CurrentThresh===="+newMoney);
//		        	}
//				} catch (Exception e) {
//					log.error("自动下单追加信用卡额度出现异常",e);
//				}
//			}
//		}catch(Throwable e){
//			log.error(e);
//		}
//	}
}
