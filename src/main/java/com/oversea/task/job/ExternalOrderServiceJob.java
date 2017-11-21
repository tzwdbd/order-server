package com.oversea.task.job;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
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
import com.oversea.task.domain.ExchangeBankDefinition;
import com.oversea.task.domain.ExternalOrderDetail;
import com.oversea.task.domain.OrderAccount;
import com.oversea.task.domain.OrderDevice;
import com.oversea.task.domain.OrderPayDetail;
import com.oversea.task.domain.TransferClearInfo;
import com.oversea.task.domain.UserTradeAddress;
import com.oversea.task.enums.AutoBuyStatus;
import com.oversea.task.enums.MoneyUnits;
import com.oversea.task.mapper.ExchangeBankDefinitionDAO;
import com.oversea.task.mapper.ExchangeDefinitionDAO;
import com.oversea.task.mapper.ExternalOrderDetailDAO;
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
import com.oversea.task.util.MathUtil;
import com.oversea.task.util.StringUtil;

public class ExternalOrderServiceJob implements RpcCallback{
	
	private Logger log = Logger.getLogger(getClass());
	
	@Resource
    private ExternalOrderDetailDAO externalOrderDetailDAO;

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
    @Resource
    private RobotOrderDetailDAO robotOrderDetailDAO;
    
    public void run(){
    	log.error("==========ExternalOrderServiceJob begin============");
    	List<ExternalOrderDetail> externalOrderDetails = externalOrderDetailDAO.getExternalOrderDetailByStatus(500);

    	//按orderno 分组
		Map<String,List<ExternalOrderDetail>> map = new HashMap<String, List<ExternalOrderDetail>>();
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
       
        log.error("==========ExternalOrderServiceJob end============");
    }

	private void processOrder(List<ExternalOrderDetail> temp) {
		ExternalOrderDetail externalOrderDetail = temp.get(0);
        String saleOrderCode = externalOrderDetail.getSaleOrderCode();
        //订单没有全部扫过来
        List<ExternalOrderDetail> list0 = externalOrderDetailDAO.getExternalOrderDetailByOrderNo(saleOrderCode);
        if(list0 != null && list0.size() != temp.size()){
        	log.error("订单orderNo = "+saleOrderCode+"还没完全扫描到externalOrderDetail表里");
        	return;
        }
        
        try {
            String ip = orderDeviceDAO.findById(externalOrderDetail.getDeviceId()).getDeviceIp();
            OrderAccount acc = orderAccountDAO.findById(externalOrderDetail.getAccountId());
            if (acc == null) {
                log.error("[Account]:" + externalOrderDetail.getAccountId() + "找不到对应的账号信息");
                return;
            }
            
            Task task = new TaskDetail();
            
			BigDecimal rmb = new BigDecimal(externalOrderDetail.getExchangeRate());
			task.addParam("rate", rmb.floatValue());
            task.addParam("externalOrderDetails", temp);
            task.addParam("account", acc);
            task.addParam("mallName", externalOrderDetail.getSiteName());
            task.setGroup(ip);
            for (ExternalOrderDetail eo : temp) {
            	externalOrderDetailDAO.updateStatus(AutoBuyStatus.AUTO_ORDER_ING.getValue(),eo.getId());
            }

            int cnt = (int) (Math.random()*10);
            //固定4个地址
            task.addParam("count", String.valueOf(cnt));
            
           	if(externalOrderDetail.getCompany()!=null && externalOrderDetail.getCompany()>0){
           		int company = externalOrderDetail.getCompany();
           		if(company==1 && ("amazon").equalsIgnoreCase(externalOrderDetail.getSiteName())){
           			company = 37;
           		}else if(company==1 && ("amazon.jp").equalsIgnoreCase(externalOrderDetail.getSiteName())){
           			company = 38;
           		}else if(company==2 && ("amazon").equalsIgnoreCase(externalOrderDetail.getSiteName())){
           			company = 35;
           		}else if(company==2 && ("amazon.jp").equalsIgnoreCase(externalOrderDetail.getSiteName())){
           			company = 36;
           		}else{
           			return ;
           		}
	        	TransferClearInfo transferClearInfo = robotOrderDetailDAO.getExpressAddress(Long.parseLong(String.valueOf(company)));
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
            taskService.externalOrder(task);
        } catch (Exception e) {
            log.error("ORDERDETAIL:[" + saleOrderCode + "]:" + e.getMessage(), e);
        }

	}

	@Override
	public void callbackAck(boolean isSuccess, Method method, Object[] objs) {
		// TODO Auto-generated method stub
		if(isSuccess){
			try{
				Task task = (Task)objs[0];
				List<ExternalOrderDetail> externalOrderDetailList = (List<ExternalOrderDetail>) task.getParam("externalOrderDetails");
				log.info(String.format("提交自动下单订单号[%s],分配账号:%s:,发送机器IP:%s", externalOrderDetailList.get(0).getSaleOrderCode(), externalOrderDetailList.get(0).getAccountId(), task.getGroup()));
				
			}catch(Exception e){
				log.error(e);
			}
		}else{
			try{
				Task task = (Task)objs[0];
				List<ExternalOrderDetail> externalOrderDetailList = (List<ExternalOrderDetail>) task.getParam("externalOrderDetails");
				for (ExternalOrderDetail r : externalOrderDetailList) {
					externalOrderDetailDAO.updateStatus(500,r.getId());
	            }
				log.info(String.format("提交自动出错下单订单号[%s],分配账号:%s:,发送机器IP:%s", externalOrderDetailList.get(0).getSaleOrderCode(), externalOrderDetailList.get(0).getAccountId(), task.getGroup()));
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
		List<ExternalOrderDetail> externalOrderDetailList = (List<ExternalOrderDetail>) (taskResult.getValue());
        //成功处理
        String cdnUrl = "";
        for (ExternalOrderDetail externalOrderDetail : externalOrderDetailList) {
        	try{
        		ExternalOrderDetail _externalOrderDetail = externalOrderDetailDAO.getExternalOrderDetailById(externalOrderDetail.getId());
                if (_externalOrderDetail == null) {
                    log.error(String.format("orderDetail id:%d can't found", externalOrderDetail.getId()));
                    return;
                }

                AutoBuyStatus autoBuyStatus = AutoBuyStatus.getAutoBuyStatusByValue(externalOrderDetail.getStatus());
                log.error("orderDetail orderNo:[" + externalOrderDetail.getSaleOrderCode() + "],status=" + autoBuyStatus);

                _externalOrderDetail.setGmtModified(new Date());
                _externalOrderDetail.setStatus(externalOrderDetail.getStatus() == AutoBuyStatus.AUTO_ORDER_ING.getValue() ? AutoBuyStatus.CLIENT_ORDER_TASK_STATUS_ERROR.getValue() : externalOrderDetail.getStatus());
                _externalOrderDetail.setTotalPrice(externalOrderDetail.getTotalPrice());
                String rmbPrice = getRmbPrice(externalOrderDetail.getUnit(),externalOrderDetail.getTotalPrice());
                _externalOrderDetail.setRmbPrice(rmbPrice);
                _externalOrderDetail.setSinglePrice(externalOrderDetail.getSinglePrice());
                _externalOrderDetail.setMallOrderNo(externalOrderDetail.getMallOrderNo());
                _externalOrderDetail.setPromotionCodeListStatus(externalOrderDetail.getPromotionCodeListStatus());
                if (_externalOrderDetail.getStatus() == AutoBuyStatus.AUTO_PAY_SUCCESS.getValue()) {
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
                	_externalOrderDetail.setOrderImg(cdnUrl);
                	_externalOrderDetail.setOrderTime(new Date());
                }
                _externalOrderDetail.setOrderRemark(autoBuyStatus == null ? "" : autoBuyStatus.getName());
                externalOrderDetailDAO.updateExternalOrderDetailByDynamic(_externalOrderDetail);
        	}catch(Exception e){
        		log.error(e);
        	}
                
        }
        
        try {
        	
        	if (externalOrderDetailList.get(0).getStatus() == AutoBuyStatus.AUTO_PAY_SUCCESS.getValue()) {
        		
        		//消费流水记录
        		try {
        			if(externalOrderDetailList.get(0).getCompany()!=null && externalOrderDetailList.get(0).getCompany()>0){
        				addOrderPayDetailLog(externalOrderDetailList);
        			}
				} catch (Exception e) {
					log.error("消费流水记录出错",e);
				}
        		
        		
            	OrderAccount acc = orderAccountDAO.findById(externalOrderDetailList.get(0).getAccountId());
                if (acc == null) {
                    log.error("[Account]:" + externalOrderDetailList.get(0).getAccountId() + "找不到对应的账号信息");
                    return;
                }
                if(("amazon").equalsIgnoreCase(externalOrderDetailList.get(0).getSiteName()) || ("amazon.jp").equalsIgnoreCase(externalOrderDetailList.get(0).getSiteName())){
                	log.error("美亚日亚使用礼品卡，不需要追加信用卡消费额度");
                	if(!StringUtils.isBlank(externalOrderDetailList.get(0).getBalanceWb())){
                		orderAccountDAO.updateOrderAccountBalanceWb(acc.getAccountId(), Double.parseDouble(externalOrderDetailList.get(0).getBalanceWb()));
                	}
                	return;
                }
        	}
		} catch (Exception e) {
			log.error("自动下单追加信用卡额度出现异常",e);
		}
        
	}
	
	private void addOrderPayDetailLog(List<ExternalOrderDetail> externalOrderDetailList) {
		for(ExternalOrderDetail _externalOrderDetail:externalOrderDetailList){
			OrderPayDetail orderPayDetail = new OrderPayDetail();
			ExternalOrderDetail externalOrderDetail = externalOrderDetailDAO.getExternalOrderDetailById(_externalOrderDetail.getId());
			//orderAccount
			orderPayDetail.setAccountId(externalOrderDetail.getAccountId());
			OrderAccount orderAccount = orderAccountDAO.findById(externalOrderDetail.getAccountId());
			//orderDevice
			OrderDevice orderDevice = orderDeviceDAO.findById(externalOrderDetail.getDeviceId());
			orderPayDetail.setDeviceId(externalOrderDetail.getDeviceId());
			orderPayDetail.setDeviceIp(orderDevice.getDeviceIp());
			
			orderPayDetail.setStatusType(0);
			orderPayDetail.setUnits(externalOrderDetail.getUnit());
			orderPayDetail.setPrice(externalOrderDetail.getTotalPrice());
			orderPayDetail.setSkuPrice(externalOrderDetail.getRealPriceOrg());
			orderPayDetail.setNum(externalOrderDetail.getItemCount());
			orderPayDetail.setSolePrice(String.valueOf(Float.parseFloat(externalOrderDetail.getRealPriceOrg())*externalOrderDetail.getItemCount()));
			orderPayDetail.setSiteName(externalOrderDetail.getSiteName());
			orderPayDetail.setOrderNo(externalOrderDetail.getSaleOrderCode());
			orderPayDetail.setOperator("auto");
			orderPayDetail.setGmtCreate(new Date());
			orderPayDetail.setOrderTime(externalOrderDetail.getOrderTime());
			orderPayDetail.setGroupNumber(0);
			orderPayDetail.setOrderStatus(4);
			orderPayDetail.setProductEntityId(Long.parseLong(externalOrderDetail.getSkuId()));
			if(("amazon").equalsIgnoreCase(externalOrderDetail.getSiteName()) || ("amazon.jp").equalsIgnoreCase(externalOrderDetail.getSiteName())){
				orderPayDetail.setStartBalance(String.valueOf(orderAccount.getBalanceWb()));
				orderPayDetail.setEndBalance(String.valueOf(_externalOrderDetail.getBalanceWb()));
				orderPayDetail.setPayType("giftcard");
	        }
			String rmbPrice = getRmbPrice(externalOrderDetail.getUnit(),externalOrderDetail.getTotalPrice());
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
