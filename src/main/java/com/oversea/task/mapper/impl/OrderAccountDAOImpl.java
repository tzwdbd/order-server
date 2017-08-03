package com.oversea.task.mapper.impl;

import com.oversea.task.domain.OrderAccount;
import com.oversea.task.enums.AccountStatus;
import com.oversea.task.mapper.BaseDao;
import com.oversea.task.mapper.OrderAccountDAO;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author fengjian
 * @version V1.0
 * @title: sea-online
 * @Package com.oversea.task.mapper.impl
 * @Description:
 * @date 15/11/25 19:29
 */
@Repository
public class OrderAccountDAOImpl extends BaseDao implements OrderAccountDAO {

    @Override
    public OrderAccount findById(long id) {
        return getSqlSession().selectOne("OrderAccountMapper.findById", id);
    }

    @Override
    public String getOrderCreditCardNoByCardId(long id) {
        return getSqlSession().selectOne("getOrderCreditCardNoByCardId", id);
    }

    @Override
    public int updateOrderAccountStatus(long id, Integer status,String rechargeMoney) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("id", id);
        map.put("status", status);
        map.put("rechargeMoney", rechargeMoney);
        return getSqlSession().update("OrderAccountMapper.updateOrderAccountStatus", map);
    }
    
    @Override
    public int updateOrderAccountStatusTemp(long id, Integer status) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("id", id);
        map.put("status", status);
        return getSqlSession().update("OrderAccountMapper.updateOrderAccountStatusTemp", map);
    }
    
    @Override
    public List<OrderAccount> getNeedRechargeAccounts(){
        return getSqlSession().selectList("OrderAccountMapper.getNeedRechargeAccounts", AccountStatus.PayGiftcardIsTakeoff.getValue());
    }

	@Override
	public String getOrderCreditSuffixNoByCardId(Long creditCardId) {
		return getSqlSession().selectOne("getOrderCreditSuffixNoByCardId", creditCardId);
	}

	@Override
	public int updateOrderAccountBalanceWb(long id, Double balanceWb) {
		 Map<String, Object> map = new HashMap<String, Object>();
        map.put("id", id);
        map.put("balanceWb", balanceWb);
        return getSqlSession().update("OrderAccountMapper.updateOrderAccountBalanceWb", map);
	}
	@Override
	public int updateOrderAccountStatusAndBalanceWb(long id, Integer status,Double balanceWb){
		Map<String, Object> map = new HashMap<String, Object>();
        map.put("id", id);
        map.put("status", status);
        map.put("balanceWb", balanceWb);
        return getSqlSession().update("OrderAccountMapper.updateOrderAccountStatusAndBalanceWb", map);
	}

	@Override
	public List<OrderAccount> getNeedRechargeAccountByStatus(String status,
			String expressCompanyIds,String siteNames) {
		Map<String, Object> map = new HashMap<String, Object>();
        map.put("expressCompanyIds", expressCompanyIds);
        map.put("status", status);
        map.put("siteNames", siteNames);
        return getSqlSession().selectList("OrderAccountMapper.getNeedRechargeAccountByStatus", map);
	}

	@Override
	public int updateOrderAccountStatusByExpressIds(String expressCompanyIds,
			String siteNames) {
		Map<String, Object> map = new HashMap<String, Object>();
        map.put("expressCompanyIds", expressCompanyIds);
        map.put("siteNames", siteNames);
		return getSqlSession().update("OrderAccountMapper.updateOrderAccountStatusByExpressIds", map);
	}
}
