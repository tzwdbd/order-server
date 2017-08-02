package com.oversea.task.mapper;

import java.util.Date;
import java.util.List;

import com.oversea.task.domain.OrderPayDetail;

public interface OrderPayDetailDAO {
	
	public void addOrderPayDetail(OrderPayDetail orderPayDetail);
	
	public List<OrderPayDetail> getOrderPayDetailByAccountId(int accountId,Date startTime,Date endTime);
	
}
