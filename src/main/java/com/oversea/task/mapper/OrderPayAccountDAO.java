package com.oversea.task.mapper;

import java.util.List;

import com.oversea.task.domain.OrderPayAccount;

/** 
* @author: yangyan 
  @Package: com.oversea.task.mapper
  @Description:
* @time   2016年10月21日 下午3:24:39 
*/
public interface OrderPayAccountDAO {
	
	public OrderPayAccount getOrderPayAccountById(long id);
	
	public List<OrderPayAccount> getOrderPayAccountByCreditCardId(long creditCardId);
	
	public int addOrderPayAccount(OrderPayAccount orderPayAccount);
	
	public int updateOrderPayAccount(OrderPayAccount orderPayAccount);
	
	
}
 