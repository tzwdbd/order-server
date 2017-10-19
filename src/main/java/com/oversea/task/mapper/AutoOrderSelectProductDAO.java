package com.oversea.task.mapper;

import java.util.List;

import com.oversea.task.domain.AutoOrderSelectProduct;
public interface AutoOrderSelectProductDAO {
	public void addAutoOrderSelectProduct(AutoOrderSelectProduct autoOrderSelectProduct);
	public void updateAutoOrderSelectProductById(AutoOrderSelectProduct autoOrderSelectProduct);
	public void updateAutoOrderSelectProductByDynamic(AutoOrderSelectProduct autoOrderSelectProduct);
	public List<AutoOrderSelectProduct> getAutoOrderSelectProductById(Long id);
	public int countAutoOrderSelectProductById(Long id);
	
	public AutoOrderSelectProduct getOrderSelectProductBySiteName(String siteName);
}