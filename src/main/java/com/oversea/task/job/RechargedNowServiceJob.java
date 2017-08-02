package com.oversea.task.job;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;

import com.haihu.rpc.common.RpcCallback;
import com.haihu.rpc.server.RpcServerProxy;
import com.oversea.task.common.TaskService;
import com.oversea.task.domain.GiftCard;
import com.oversea.task.domain.GiftCardConfig;
import com.oversea.task.domain.OrderAccount;
import com.oversea.task.domain.OrderDevice;
import com.oversea.task.enums.AccountStatus;
import com.oversea.task.mapper.GiftCardConfigDAO;
import com.oversea.task.mapper.GiftCardDAO;
import com.oversea.task.mapper.OrderAccountDAO;
import com.oversea.task.mapper.OrderDeviceDAO;
import com.oversea.task.obj.Task;
import com.oversea.task.obj.TaskDetail;
import com.oversea.task.obj.TaskResult;
import com.oversea.task.util.MathUtil;
import com.oversea.task.util.StringUtil;

public class RechargedNowServiceJob implements RpcCallback{
	
	private Logger log = Logger.getLogger(getClass());
	
	@Resource
	private OrderAccountDAO orderAccountDAO;
	@Resource
	private GiftCardConfigDAO giftCardConfigDAO;
    @Resource
    private GiftCardDAO giftCardDAO;
    @Resource
    private OrderDeviceDAO orderDeviceDAO;
    @Resource
    private GiftCardConfigDAO giftCardconfigDAO;
    @Resource
    private RpcServerProxy rpcServerProxy;
    @Resource
    private RechargeServiceJob rechargeServiceJob;

    private static final String acountStatus = "0,4";
    private static final String companyIdStatus = "3,4,8,9";
    private static final String siteNames = "'amazon','amazon.jp'";
	
	public void run(){
		log.error("==========RechargedNowServiceJob begin============");
		try{
			List<OrderAccount> orderAccounts = orderAccountDAO.getNeedRechargeAccountByStatus(acountStatus, companyIdStatus,siteNames);
	        //美亚会员账号集合
	        List<OrderAccount> amazonPrimeList = new ArrayList<OrderAccount>();
	        //美亚非会员账号集合
	        List<OrderAccount> amazonNotPrimeList = new ArrayList<OrderAccount>();
	        //日亚会员账号集合
	        List<OrderAccount> amazonJPPrimeList = new ArrayList<OrderAccount>();
	        //日亚非会员账号集合
	        List<OrderAccount> amazonJPNotPrimeList = new ArrayList<OrderAccount>();
	        for (OrderAccount orderAccount : orderAccounts) {
	        	if("amazon".equalsIgnoreCase(orderAccount.getAccountType())){
	        		if("yes".equalsIgnoreCase(orderAccount.getIsPrime())){
	        			amazonPrimeList.add(orderAccount);
	        		}else{
	        			amazonNotPrimeList.add(orderAccount);
	        		}
	        	}
	        	if("amazon.jp".equalsIgnoreCase(orderAccount.getAccountType())){
	        		if("yes".equalsIgnoreCase(orderAccount.getIsPrime())){
	        			amazonJPPrimeList.add(orderAccount);
	        		}else{
	        			amazonJPNotPrimeList.add(orderAccount);
	        		}
	        	}
	        }
	        //充值
	        log.error("NowGiftCardRechargeBootstrasp amazonPrimeList size"+amazonPrimeList.size());
	        log.error("NowGiftCardRechargeBootstrasp amazonNotPrimeList size"+amazonNotPrimeList.size());
	        log.error("NowGiftCardRechargeBootstrasp amazonJPPrimeList size"+amazonJPPrimeList.size());
	        log.error("NowGiftCardRechargeBootstrasp amazonJPNotPrimeList size"+amazonJPNotPrimeList.size());
	        List<GiftCardConfig> giftCardConfigList = giftCardconfigDAO.getGiftCardConfig();
	        //美亚礼品卡配置会员账号集合
	        List<GiftCardConfig> amazonGiftPrimeList = new ArrayList<GiftCardConfig>();
	        //美亚礼品卡配置非会员账号集合
	        List<GiftCardConfig> amazonGiftNotPrimeList = new ArrayList<GiftCardConfig>();
	        //日亚礼品卡配置会员账号集合
	        List<GiftCardConfig> amazonJPGiftPrimeList = new ArrayList<GiftCardConfig>();
	        //日亚礼品卡配置非会员账号集合
	        List<GiftCardConfig> amazonJPGiftNotPrimeList = new ArrayList<GiftCardConfig>();
	        for(GiftCardConfig giftCardConfig:giftCardConfigList){
	        	if("amazon".equalsIgnoreCase(giftCardConfig.getSiteName())){
	        		if(giftCardConfig.getIsPrime()==1){
	        			amazonGiftPrimeList.add(giftCardConfig);
	        		}else{
	        			amazonGiftNotPrimeList.add(giftCardConfig);
	        		}
	        	}
	        	if("amazon.jp".equalsIgnoreCase(giftCardConfig.getSiteName())){
	        		if(giftCardConfig.getIsPrime()==1){
	        			amazonJPGiftPrimeList.add(giftCardConfig);
	        		}else{
	        			amazonJPGiftNotPrimeList.add(giftCardConfig);
	        		}
	        	}
	        }
	        log.error("NowGiftCardRechargeBootstrasp amazonGiftPrimeList size"+amazonGiftPrimeList.size());
	        log.error("NowGiftCardRechargeBootstrasp amazonGiftNotPrimeList size"+amazonGiftNotPrimeList.size());
	        log.error("NowGiftCardRechargeBootstrasp amazonJPGiftPrimeList size"+amazonJPGiftPrimeList.size());
	        log.error("NowGiftCardRechargeBootstrasp amazonJPGiftNotPrimeList size"+amazonJPGiftNotPrimeList.size());
	        //获取需要充值的钱
	        amazonPrimeList = handleAcount(amazonPrimeList,amazonGiftPrimeList);
	        amazonNotPrimeList = handleAcount(amazonNotPrimeList,amazonGiftNotPrimeList);
	        amazonJPPrimeList = handleAcount(amazonJPPrimeList,amazonJPGiftPrimeList);
	        amazonJPNotPrimeList = handleAcount(amazonJPNotPrimeList,amazonJPGiftNotPrimeList);
	        handleRecharge(amazonPrimeList);
	        handleRecharge(amazonNotPrimeList);
	        handleRecharge(amazonJPPrimeList);
	        handleRecharge(amazonJPNotPrimeList);
		}catch(Exception e){
			log.error(e);
		}
		
        log.error("==========RechargedNowServiceJob end============");
	}
	
	private List<OrderAccount> handleAcount(List<OrderAccount> amazonPrimeList,
			List<GiftCardConfig> amazonGiftPrimeList) {
		int i=0;
		int j=0;
		int amazonPrimeNum = amazonPrimeList.size();
		List<OrderAccount> newAmazonPrimeList = new ArrayList<OrderAccount>();
		for(GiftCardConfig gift:amazonGiftPrimeList){
			int accountNum = (int) (amazonPrimeNum*gift.getPercent())+1;
			j = accountNum+j;
			if(j>amazonPrimeNum){
				j = amazonPrimeNum;
			}
			for(;i<j;i++){
				if(amazonPrimeList.get(i).getBalanceWb().doubleValue()<gift.getBalance().intValue()){
					amazonPrimeList.get(i).setRechargeMoney(MathUtil.sub(String.valueOf(gift.getBalance()),String.valueOf(amazonPrimeList.get(i).getBalanceWb())));
					newAmazonPrimeList.add(amazonPrimeList.get(i));
				}
			}
		}
		return newAmazonPrimeList;
	}
	
	public void handleRecharge(List<OrderAccount> orderAccountList) {
        for (OrderAccount orderAccount : orderAccountList) {
            try {
                if (orderAccount != null) {
                    if (orderAccount.getStatus() == AccountStatus.Recharging.getValue()) {
                        continue;
                    }
                    
                    int num = 0;
                    String rechargeMoney = orderAccount.getRechargeMoney();
                    if(com.oversea.task.util.StringUtil.isNotEmpty(rechargeMoney)){
                    	try{
                    		float rm = Float.parseFloat(rechargeMoney);
                    		if("amazon".equalsIgnoreCase(orderAccount.getAccountType())){
                    			num = (int)Math.ceil(rm/100.0);
                    		}else if("amazon.jp".equalsIgnoreCase(orderAccount.getAccountType())){
                    			num = (int)Math.ceil(rm/10000.0);
                    		}
                    	}catch(Exception e){
                    		log.error("计算充值礼品卡数量出错",e);
                    	}
                    }
                    
                    if(num <= 0){
                    	num = 1;
                    }
                    List<GiftCard> list = giftCardDAO.getNowRechargeGiftCard(orderAccount.getAccountType(),num);
                    if(list.size() > 0){
                    	log.debug("orderAccount.getAccountType() = " + orderAccount.getAccountType());
                        OrderDevice orderDevice = orderDeviceDAO.findById(orderAccount.getDeviceId());
                        if (orderDevice != null) {
                        	Task task = new TaskDetail();
                            task.addParam("account", orderAccount);
                            task.addParam("giftCard", list);
                            task.setGroup(orderDevice.getDeviceIp());
                            for(GiftCard giftCard : list){
                        		giftCardDAO.updateGiftCardProcessStatus(giftCard.getId(), "yes");
                        	}
                            orderAccountDAO.updateOrderAccountStatusTemp(orderAccount.getAccountId(), AccountStatus.Recharging.getValue());
                            TaskService taskService = (TaskService)rpcServerProxy.wrapProxy(TaskService.class, task.getGroup(), this);
                            taskService.giftService(task);
                            
                        } else {
                            log.error("[Recharge]账号:" + orderAccount.getPayAccount() + "找不到对应的机器");
                        }
                    }else{
                    	log.debug("[Recharge]账号:" + orderAccount.getPayAccount() + "找不到充值的礼品卡");
                    }
                    
                }
            } catch (Exception e) {
                log.error("[Recharge]orderDetail no:[" + orderAccount.getPayAccount() + "]:" + e.getMessage(), e);
            }
        }
    }

	@Override
	public void callbackAck(boolean isSuccess, Method method, Object[] objs) {
		// TODO Auto-generated method stub
		if(isSuccess){
			try{
				if(objs != null){
					Task task = (Task)objs[0];
					List<GiftCard> list = (List<GiftCard>)task.getParam("giftCard");
					OrderAccount orderAccount = (OrderAccount)task.getParam("account");
                    String giftCards = "";
                    for(GiftCard giftCard : list){
                    	giftCards += giftCard.getSecurityCode();
                    	giftCards += ",";
                    }
                    log.error(String.format("[Recharge]ip:[%s]发送充值任务:%s账号:%s,giftCard:%s", task.getGroup(),orderAccount.getAccountType(), orderAccount.getPayAccount(),giftCards));
				}
			}catch(Exception e){
				log.error(e);
			}
		}else{
			try{
				if(objs != null){
					Task task = (Task)objs[0];
					List<GiftCard> list = (List<GiftCard>)task.getParam("giftCard");
					OrderAccount orderAccount = (OrderAccount)task.getParam("account");
					String giftCards = "";
                    for(GiftCard giftCard : list){
                    	giftCardDAO.updateGiftCardProcessStatus(giftCard.getId(), "no");
                    	giftCards += giftCard.getSecurityCode();
                    	giftCards += ",";
                    }
                    orderAccountDAO.updateOrderAccountStatusTemp(orderAccount.getAccountId(), AccountStatus.Init.getValue());
                    log.error("发送充值任务失败礼品卡code:"+giftCards);
				}
			}catch(Exception e){
				log.error(e);
			}
		}
	}

	@Override
	public void callbackResult(Object result, Method method, Object[] objs) {
		// TODO Auto-generated method stub
		if(result == null){
			return;
		}
		TaskResult taskResult = (TaskResult)result;
		rechargeGift(taskResult);
	}
	
	public Integer rechargeGift(TaskResult taskResult){
        List<GiftCard> list = (List<GiftCard>) taskResult.getValue();
        if(list == null || list.size() == 0){
        	log.error("回传的List<GiftCard>为空");
        	return null;
        }
        
        Integer accountId = (Integer)taskResult.getParam("accountId");
        if(accountId == null){
        	log.error("回传的accountId为空");
        	return null;
        }
        
        //判断是否充值成功,只要有一张卡充值失败就认为失败
        for(GiftCard giftCard : list){
        	GiftCard _giftCard = giftCardDAO.getGiftCardById(giftCard.getId());
	        if (_giftCard == null) {
	            log.error(String.format("giftCard id:%d can't found", giftCard.getId()));
	            continue;
	        }
	        _giftCard.setIsProcess("no");
	        _giftCard.setRealBalance(giftCard.getRealBalance());
	        if("no".equalsIgnoreCase(giftCard.getIsSuspect())){//充值成功
	        	_giftCard.setIsUsed("yes");
	        	_giftCard.setAccountId(accountId);
	        	log.error(String.format("giftCard:%s充值成功,accountId:%d", giftCard.getSecurityCode(),accountId));
	        }else if("yes".equalsIgnoreCase(giftCard.getIsSuspect())){//充值失败
	        	_giftCard.setIsUsed("no");
	        	_giftCard.setIsSuspect("yes");
	        	log.error(String.format("giftCard:%s充值失败,标记成可疑礼品卡", giftCard.getSecurityCode()));
	        }else{//没有进行充值
	        	log.error(String.format("giftCard:%s没有进行充值,可能登录失败", giftCard.getSecurityCode()));
	        	_giftCard.setIsUsed("no");
	        }
	        giftCardDAO.updateGiftCard(_giftCard);
    	}
        
        //更新账号状态
        String balance = (String)taskResult.getParam("balance");
        if(StringUtil.isNotEmpty(balance)){
        	balance = balance.replaceAll(",", "");
        }
        log.error("balance now = "+balance);
        
        orderAccountDAO.updateOrderAccountStatusTemp(accountId, AccountStatus.Init.getValue());
        if(StringUtil.isNotEmpty(balance)){
        	double balanceD = 0;
        	try{
        		balanceD = Double.parseDouble(balance);
        	}catch(Exception e){
        		log.error("转换数字出现异常",e);
        	}
        	if(balanceD > 0){
        		orderAccountDAO.updateOrderAccountStatusAndBalanceWb(accountId, AccountStatus.Init.getValue(), balanceD);
        	}
        }
        return accountId;
    }

}
