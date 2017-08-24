package com.oversea.task.mapper.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.oversea.task.domain.ProductEntity;
import com.oversea.task.mapper.BaseDao;
import com.oversea.task.mapper.ProductEntityDAO;

@Repository
public class ProductEntityDAOImpl extends BaseDao implements ProductEntityDAO{

	@Override
    public ProductEntity getProductEntityById(Long id) {
        return getSqlSession().selectOne("getProductEntityById", id);
    }
	
	@Override
    public List<ProductEntity> getProductEntityByProductIdAndStatus(Long productId,Integer status) {
		Map<String, Object> param = new HashMap<String, Object>();
        param.put("productId", productId);
        param.put("status", status);
        return getSqlSession().selectList("getProductEntityByProductIdAndStatus", param);
        
    }

	@Override
	public String getProductIdListByExternalId(String externalId) {
		return getSqlSession().selectOne("getProductIdListByExternalId", externalId);
	}
}
