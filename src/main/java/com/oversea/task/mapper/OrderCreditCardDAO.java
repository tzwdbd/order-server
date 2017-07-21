package com.oversea.task.mapper;

import com.oversea.task.domain.OrderCreditCard;


/** 
* @author: yangyan 
  @Package: com.oversea.task.mapper
  @Description:
* @time   2016年8月15日 下午6:16:38 
*/
public interface OrderCreditCardDAO {

	public OrderCreditCard getOrderCreditCardById(long creditId);
	
	public void updateOrderCreditCardNew(OrderCreditCard orderCreditCard);
}
 