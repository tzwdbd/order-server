package com.oversea.task.job;

import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;

import com.haihu.rpc.common.RpcCallback;
import com.haihu.rpc.server.RpcServerProxy;
import com.oversea.rabbitmq.utils.StringUtil;
import com.oversea.task.common.TaskService;
import com.oversea.task.domain.ExchangeBankDefinition;
import com.oversea.task.domain.GiftCard;
import com.oversea.task.domain.GiftCardCheck;
import com.oversea.task.domain.OrderAccount;
import com.oversea.task.domain.OrderPayDetail;
import com.oversea.task.enums.AccountStatus;
import com.oversea.task.mapper.ExchangeBankDefinitionDAO;
import com.oversea.task.mapper.GiftCardCheckDAO;
import com.oversea.task.mapper.GiftCardDAO;
import com.oversea.task.mapper.OrderAccountDAO;
import com.oversea.task.mapper.OrderDeviceDAO;
import com.oversea.task.mapper.OrderPayDetailDAO;
import com.oversea.task.obj.Task;
import com.oversea.task.obj.TaskDetail;
import com.oversea.task.obj.TaskResult;
import com.oversea.task.util.DateUtil;
import com.oversea.task.util.MathUtil;
import com.oversea.task.util.ThreeDES;

public class GiftCardCheckJob implements RpcCallback{
	
	private Logger log = Logger.getLogger(getClass());
	@Resource
	private RpcServerProxy rpcServerProxy;

    @Resource
    private OrderAccountDAO orderAccountDAO;

    @Resource
    private OrderDeviceDAO orderDeviceDAO;
    @Resource
    private ExchangeBankDefinitionDAO exchangeBankDefinitionDAO;
    
    @Resource
    private GiftCardDAO giftCardDAO;
    
    @Resource
    private OrderPayDetailDAO orderPayDetailDao;
    @Resource
    private GiftCardCheckDAO giftCardCheckDAO;
    
    private static final String acountStatus = "4";
    private static final String companyIdStatus = "3,4,8,9";
    private static final String siteNames = "'amazon','amazon.jp'";
    
    
	
	public void run(){
		log.error("============GiftCardCheckJob begin============");
		try{
			List<OrderAccount> orderAccounts = orderAccountDAO.getNeedRechargeAccountByStatusByGroup(acountStatus, companyIdStatus,siteNames);
			log.error("GiftCardCheckJob orderAccounts size:"+orderAccounts.size());
	    	for(OrderAccount account : orderAccounts){
    			Task task = new TaskDetail();
                Integer deviceId = account.getDeviceId();
                String ip = orderDeviceDAO.findById(deviceId).getDeviceIp();
                account.setLoginPwd(ThreeDES.decryptMode(account.getLoginPwd()));
                task.addParam("account", account);
                task.setGroup(ip);
                orderAccountDAO.updateOrderAccountStatusTemp(account.getAccountId(), 0);
                TaskService taskService = (TaskService)rpcServerProxy.wrapProxy(TaskService.class, ip, this);
                taskService.CheckGiftCard(task);
	    	}
		}catch(Exception e){
			log.error(e);
		}
        
		log.error("============GiftCardCheckJob end=================");
	}

	@Override
	public void callbackAck(boolean isSuccess, Method method, Object[] objs) {
		// TODO Auto-generated method stub
		if(isSuccess){
			if(objs != null){
				Task task = (Task)objs[0];
				OrderAccount orderAccount = (OrderAccount) task.getParam("account");
				log.error("账号id:"+orderAccount.getAccountId()+"提交爬取礼品卡"+"ip:"+task.getGroup());
			}
		}else{
			if(objs != null){
				Task task = (Task)objs[0];
				OrderAccount orderAccount = (OrderAccount) task.getParam("account");
				orderAccountDAO.updateOrderAccountStatusTemp(orderAccount.getAccountId(), 4);
				log.error("爬取礼品卡失败账号id:"+orderAccount.getAccountId()+"提交爬取礼品卡"+"ip:"+task.getGroup());
			}
		}
	}

	@Override
	public void callbackResult(Object result, Method method, Object[] objs) {
		log.error("爬取礼品卡回来了");
		// TODO Auto-generated method stub
		TaskResult taskResult = (TaskResult)result;
		if(taskResult == null){
			return;
		}
		OrderAccount orderAccount = (OrderAccount) taskResult.getValue();
		log.error("收到:"+orderAccount.getAccountId()+"的礼品卡为"+orderAccount.getBalanceWb()+" 的卡号为"+orderAccount.getFirstName()+" 的prime 类型为"+orderAccount.getCardNo()+" 的prime过期时间为"+orderAccount.getSuffixNo());
		
		GiftCardCheck giftCardCheck = new GiftCardCheck();
		giftCardCheck.setAccountId((long)orderAccount.getAccountId());
		giftCardCheck.setCardType("giftcard");
		giftCardCheck.setCurrentBalance(String.valueOf(orderAccount.getBalanceWb()));
		giftCardCheck.setDate(DateUtil.zerolizedTime(new Date()));
		giftCardCheck.setGmtCreate(new Date());
		giftCardCheck.setPayAccount(orderAccount.getPayAccount());
		giftCardCheck.setSiteName(orderAccount.getAccountType());
		String recharge = "0";
		List<GiftCard> giftCards = giftCardDAO.getGiftCardAccount(orderAccount.getAccountId(), DateUtil.increaseDay(DateUtil.zerolizedTime(new Date()), -1));
		if(giftCards!=null && giftCards.size()>0){
			for(GiftCard g:giftCards){
				String b = g.getRealBalance();
				if(StringUtil.isBlank(b)){
					b = g.getBalance();
				}
				recharge = MathUtil.add(recharge, b);
			}
		}
		giftCardCheck.setRecharge(recharge);
		String refund = "0";
		List<OrderPayDetail> orderPayDetais = orderPayDetailDao.getOrderPayDetailByAccountId(orderAccount.getAccountId(), DateUtil.increaseDay(DateUtil.zerolizedTime(new Date()), -5), DateUtil.increaseDay(DateUtil.zerolizedTime(new Date()), -4));
		if(orderPayDetais!=null && orderPayDetais.size()>0){
			for(OrderPayDetail g:orderPayDetais){
				String b = g.getPrice();
				refund = MathUtil.add(recharge, b);
			}
		}
		giftCardCheck.setRefund(refund);
		String unit = "$";
		if("amazon.jp".equalsIgnoreCase(orderAccount.getAccountType())){
			unit = "円";
		}
		ExchangeBankDefinition exchangeBankDefinition = exchangeBankDefinitionDAO.getExchangeBankDefinitionByUnit(unit);
		BigDecimal rmb = new BigDecimal(exchangeBankDefinition.getRmb());
		BigDecimal source = new BigDecimal(exchangeBankDefinition.getSource());
		BigDecimal rate =  (rmb.divide(source));
		String rmbs = MathUtil.mul(String.valueOf(orderAccount.getBalanceWb()), String.valueOf(rate));
		giftCardCheck.setRmb(rmbs);
		giftCardCheckDAO.addGiftCardCheck(giftCardCheck);
		orderAccountDAO.updateOrderAccountStatusAndBalanceWb(orderAccount.getAccountId(), AccountStatus.Init.getValue(), orderAccount.getBalanceWb());
		OrderAccount oa = new OrderAccount();
		oa.setAccountId(orderAccount.getAccountId());
		oa.setFirstName(orderAccount.getFirstName());
		oa.setSuffixNo(orderAccount.getSuffixNo());
		oa.setCardNo(orderAccount.getCardNo());
		orderAccountDAO.updateOrderFirstNameAndRecipients(oa);
	}
}
