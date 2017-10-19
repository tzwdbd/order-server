package com.oversea.task.mapper;

import java.util.List;

import com.oversea.task.domain.AutoOrderPay;
public interface AutoOrderPayDAO {
	public void addAutoOrderPay(AutoOrderPay autoOrderPay);
	public void updateAutoOrderPayById(AutoOrderPay autoOrderPay);
	public void updateAutoOrderPayByDynamic(AutoOrderPay autoOrderPay);
	public List<AutoOrderPay> getAutoOrderPayById(Long id);
	public int countAutoOrderPayById(Long id);
	
	public AutoOrderPay getOrderPayBySiteName(String siteName);
}