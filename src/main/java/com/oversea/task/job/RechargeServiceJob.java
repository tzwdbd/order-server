package com.oversea.task.job;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;

import com.haihu.rpc.common.RpcCallback;
import com.haihu.rpc.server.RpcServerProxy;
import com.oversea.task.common.TaskService;
import com.oversea.task.domain.GiftCard;
import com.oversea.task.domain.OrderAccount;
import com.oversea.task.domain.OrderDevice;
import com.oversea.task.domain.RobotOrderDetail;
import com.oversea.task.enums.AccountStatus;
import com.oversea.task.enums.AutoBuyStatus;
import com.oversea.task.mapper.GiftCardDAO;
import com.oversea.task.mapper.OrderAccountDAO;
import com.oversea.task.mapper.OrderDeviceDAO;
import com.oversea.task.mapper.RobotOrderDetailDAO;
import com.oversea.task.obj.Task;
import com.oversea.task.obj.TaskDetail;
import com.oversea.task.obj.TaskResult;
import com.oversea.task.util.StringUtil;


public class RechargeServiceJob implements RpcCallback{
	@Resource
    private OrderAccountDAO orderAccountDAO;
    @Resource
    private GiftCardDAO giftCardDAO;
    @Resource
    private OrderDeviceDAO orderDeviceDAO;
    @Resource
    private RpcServerProxy rpcServerProxy;
    @Resource
    private RobotOrderDetailDAO robotOrderDetailDAO;
    
    private static final int MAX_FAIL_COUNT = 3;//充值最多失败记录
    private HashMap<Integer,Integer> rechargeFailMap = new HashMap<Integer, Integer>();//充值失败记录key:accountId value:失败次数
	
	private Logger log = Logger.getLogger(getClass());
	public void run(){
		log.error("========RechargeServiceJob begin==============");
		
		try{
			List<OrderAccount> orderAccounts = orderAccountDAO.getNeedRechargeAccounts();
	        handleRecharge(orderAccounts,0);
		}catch(Exception e){
			log.error(e);
		}
        
        log.error("========RechargeServiceJob end==============");
	}
	
	public void handleRecharge(List<OrderAccount> orderAccountList,int limitNum) {
        for (OrderAccount orderAccount : orderAccountList) {
            try {
                if (orderAccount != null) {
                    if (orderAccount.getStatus() == AccountStatus.Recharging.getValue()) {
                        continue;
                    }
                    
                    int num = 0;
                    String rechargeMoney = orderAccount.getRechargeMoney();
                    if(StringUtil.isNotEmpty(rechargeMoney)){
                    	try{
                    		float rm = Float.parseFloat(rechargeMoney);
                    		if("amazon".equalsIgnoreCase(orderAccount.getAccountType())){
                    			num = (int)Math.ceil(rm/50.0);
                    		}else if("amazon.jp".equalsIgnoreCase(orderAccount.getAccountType())){
                    			num = (int)Math.ceil(rm/3000.0);
                    		}
                    	}catch(Exception e){
                    		log.error("计算充值礼品卡数量出错",e);
                    	}
                    }
                    
                    if(num <= 0){
                    	num = 1;
                    }
                    List<GiftCard> list = giftCardDAO.getUnusedGiftCard(orderAccount.getAccountType(),limitNum,num);
                    if(list.size() > 0){
                    	log.debug("orderAccount.getAccountType() = " + orderAccount.getAccountType());
                        OrderDevice orderDevice = orderDeviceDAO.findById(Long.parseLong(orderAccount.getDeviceId()));
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
//					for(GiftCard giftCard : list){
//                		giftCardDAO.updateGiftCardProcessStatus(giftCard.getId(), "yes");
//                	}
//                    orderAccountDAO.updateOrderAccountStatusTemp(orderAccount.getAccountId(), AccountStatus.Recharging.getValue());
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
		Integer accountId = rechargeGift(taskResult);
        //更新订单状态,以便让订单重新下单
        if(accountId!=null && accountId>0){
        	List<RobotOrderDetail> orderDetailList = robotOrderDetailDAO.getRobotOrderDetailsByStatusAndAccountId(accountId , AutoBuyStatus.AUTO_PAY_GIFTCARD_IS_TAKEOFF.getValue());
	        if (orderDetailList != null && orderDetailList.size() > 0) {
	            //充值成功 订单发上去重新下单
	            String orderNo = "";
	            for (RobotOrderDetail detail : orderDetailList) {
	                if (StringUtil.isEmpty(orderNo)) {
	                    orderNo = detail.getOrderNo();
	                }
	                if (!StringUtil.isEmpty(orderNo)) {
	                    if (orderNo.equals(detail.getOrderNo())) {
	                        detail.setStatus(AutoBuyStatus.AUTO_PAY_PARPARE.getValue());
	                    } else {
	                        detail.setStatus(AutoBuyStatus.AUTO_ORDER_MANUAL__FAIL.getValue());//其余订单重新分配账号
	                    }
	                    robotOrderDetailDAO.updateRobotOrderDetail(detail);
	                }
	            }
	        }
        }
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
        boolean isSuccess = true;
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
	        	isSuccess = false;
	        	_giftCard.setIsUsed("no");
	        	_giftCard.setIsSuspect("yes");
	        	log.error(String.format("giftCard:%s充值失败,标记成可疑礼品卡", giftCard.getSecurityCode()));
	        }else{//没有进行充值
	        	isSuccess = false;
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
        log.error("balance = "+balance);
        if(StringUtil.isNotEmpty(balance)){
        	double balanceD = 0;
        	try{
        		balanceD = Double.parseDouble(balance);
        	}catch(Exception e){
        		log.error("转换数字出现异常",e);
        	}
        	if(balanceD > 0){
        		orderAccountDAO.updateOrderAccountStatusAndBalanceWb(accountId, AccountStatus.Init.getValue(), balanceD);
        	}else{
        		orderAccountDAO.updateOrderAccountStatusTemp(accountId, AccountStatus.Init.getValue());
        	}
        }else{
        	orderAccountDAO.updateOrderAccountStatusTemp(accountId, AccountStatus.Init.getValue());
        }
        if(isSuccess){
        	rechargeFailMap.remove(accountId);
        }else{
        	Integer count = rechargeFailMap.get(accountId);
    		if(count != null && count.intValue() >= MAX_FAIL_COUNT ){
    			rechargeFailMap.remove(accountId);
    			orderAccountDAO.updateOrderAccountStatusTemp(accountId, AccountStatus.Recharging_fail.getValue());
    		}else{
    			if(count == null){
    				rechargeFailMap.put(accountId,1);
    			}else{
    				count++;
    				rechargeFailMap.put(accountId,count);
    			}
    			orderAccountDAO.updateOrderAccountStatusTemp(accountId, AccountStatus.PayGiftcardIsTakeoff.getValue());
    		}
    		return null;
        }
        
        return accountId;
    }
}
