package com.oversea.task.mapper;

import java.util.List;

import com.oversea.task.domain.Product;

public interface ProductDAO {

	public Product getProductById(Long id);
	
	public long getMaxProductId();
	
	public List<Product> getProductByAmazon(Long fromId,Integer step) ;
	
}
