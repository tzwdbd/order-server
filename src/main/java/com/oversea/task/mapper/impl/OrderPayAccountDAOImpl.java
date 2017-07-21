package com.oversea.task.mapper.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.oversea.task.domain.OrderPayAccount;
import com.oversea.task.mapper.BaseDao;
import com.oversea.task.mapper.OrderPayAccountDAO;

/** 
* @author: yangyan 
  @Package: com.oversea.task.mapper.impl
  @Description:
* @time   2016年10月21日 下午3:27:39 
*/
@Repository("orderPayAccountDAO")
public class OrderPayAccountDAOImpl extends BaseDao implements OrderPayAccountDAO {

	private static final String namespace = "OrderPayAccountSQL";
	
	@Override
	public OrderPayAccount getOrderPayAccountById(long id) {
		return getSqlSession().selectOne(namespace+".getOrderPayAccountById", id);
	}

	@Override
	public List<OrderPayAccount> getOrderPayAccountByCreditCardId(long creditCardId) {
		return getSqlSession().selectList(namespace+".getOrderPayAccountByCreditCardId", creditCardId);
	}

	@Override
	public int addOrderPayAccount(OrderPayAccount orderPayAccount) {
		return getSqlSession().insert(namespace+".addOrderPayAccount", orderPayAccount);
	}

	@Override
	public int updateOrderPayAccount(OrderPayAccount orderPayAccount) {
		return getSqlSession().update(namespace+".updateOrderPayAccount", orderPayAccount);
	}

}
 