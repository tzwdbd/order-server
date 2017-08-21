package com.oversea.task.mapper.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.oversea.task.domain.Product;
import com.oversea.task.mapper.BaseDao;
import com.oversea.task.mapper.ProductDAO;

@Repository
public class ProductDAOImpl extends BaseDao implements ProductDAO{
	
	@Override
	public Product getProductById(Long id) {
        return getSqlSession().selectOne("getProductById", id);
    }
	
	@Override
    public long getMaxProductId() {
        return getSqlSession().selectOne("getMaxProductId");
    }

	@Override
	public List<Product> getProductByAmazon(Long fromId,Integer step) {
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("fromId", fromId);
		param.put("step", step);
		return getSqlSession().selectList("getProductByAmazon", param);
	}

	@Override
	public List<Product> getCheckProductByCondition(Long id, Long mallId) {
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("id", id);
		param.put("mallId", mallId);
		return getSqlSession().selectList("getProductByAmazon", param);
	}

	@Override
	public int updateProductStatusByIds(String productIds) {
		return getSqlSession().update("updateProductStatusByIds", productIds);
	}
	
}
