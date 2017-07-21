package com.oversea.task.mapper.impl;

import org.springframework.stereotype.Repository;

import com.oversea.task.domain.OrderCreditCard;
import com.oversea.task.mapper.BaseDao;
import com.oversea.task.mapper.OrderCreditCardDAO;

/** 
* @author: yangyan 
  @Package: com.oversea.task.mapper.impl
  @Description:
* @time   2016年8月15日 下午6:08:12 
*/
@Repository
public class OrderCreditCardDAOImpl extends BaseDao implements OrderCreditCardDAO {

	@Override
	public OrderCreditCard getOrderCreditCardById(long creditId){
		return (OrderCreditCard)getSqlSession().selectOne("OrderCreditCardMapper.getOrderCreditCardById", creditId);
	}

	@Override
	public void updateOrderCreditCardNew(OrderCreditCard orderCreditCard) {
		getSqlSession().update("OrderCreditCardMapper.updateOrderCreditCardNew", orderCreditCard);
	}

}
 