package com.oversea.task.mapper;

import java.util.List;

import com.oversea.task.domain.OrderAccount;

public interface OrderAccountDAO {

    /**
     * 查找Account
     *
     * @param id
     * @return
     */
    public OrderAccount findById(long id);

    /**
     * 根据cardId取得OrderCreditCard
     * @param id
     * @return
     */
    public String getOrderCreditCardNoByCardId(long id);

    /**
     * 更新状态
     *
     * @param id
     * @param status
     * @return
     */
    public int updateOrderAccountStatus(long id, Integer status,String rechargeMoney);
    
    /**
     * 获取需要充值的账号
     * @return
     */
	public List<OrderAccount> getNeedRechargeAccounts();
	
	/**
	 * 更新账号状态
	 * @param id
	 * @param status
	 * @return
	 */
	public int updateOrderAccountStatusTemp(long id, Integer status);

	/**
	 * 
	 * @param creditCardId
	 * @return
	 */
	public String getOrderCreditSuffixNoByCardId(Long creditCardId);
	
	/**
	 * 更新外币
	 * @param id
	 * @param balanceWb
	 * @return
	 */
	public int updateOrderAccountBalanceWb(long id, Double balanceWb);
	
	
	public int updateOrderAccountStatusAndBalanceWb(long id, Integer status,Double balanceWb);
	
	/**
     * 获取需要充值的账号
     * @return
     */
	public List<OrderAccount> getNeedRechargeAccountByStatus(String status,String expressCompanyIds,String siteNames);
	
	public int updateOrderAccountStatus(String expressCompanyIds,String siteNames);
}
