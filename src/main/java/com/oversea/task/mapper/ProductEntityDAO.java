package com.oversea.task.mapper;

import java.util.List;

import com.oversea.task.domain.ProductEntity;

public interface ProductEntityDAO {

	public ProductEntity getProductEntityById(Long id);
	
	/**
	 * 根据商品Id和状态查询
	 * @param productId
	 * @param status
	 * @return
	 */
	public List<ProductEntity> getProductEntityByProductIdAndStatus(Long productId,Integer status);
	
	public List<String> getProductIdListByExternalIds(String externalIds);
}
