package com.oversea.task.mapper;

import com.oversea.task.domain.ProductEntity;

public interface DispatchManager {
	
	 /**
     * 将商品sku转成字符
     *
     * @param productEntity
     * @return
     */
    public String getProductSkuStr(ProductEntity productEntity);
    
}
