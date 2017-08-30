package com.oversea.task.mapper;

import java.util.List;

import com.oversea.task.domain.AutoOrderCleanCart;
public interface AutoOrderCleanCartDAO {
	public void addAutoOrderCleanCart(AutoOrderCleanCart autoOrderCleanCart);
	public void updateAutoOrderCleanCartById(AutoOrderCleanCart autoOrderCleanCart);
	public void updateAutoOrderCleanCartByDynamic(AutoOrderCleanCart autoOrderCleanCart);
	public List<AutoOrderCleanCart> getAutoOrderCleanCartById(Long id);
	public int countAutoOrderCleanCartById(Long id);
	
	public AutoOrderCleanCart getAutoOrderCleanCartBySiteName(String siteName);
}