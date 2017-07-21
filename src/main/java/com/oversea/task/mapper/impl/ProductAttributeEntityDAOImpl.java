package com.oversea.task.mapper.impl;

import org.springframework.stereotype.Repository;

import com.oversea.task.domain.ProductAttributeEntity;
import com.oversea.task.mapper.BaseDao;
import com.oversea.task.mapper.ProductAttributeEntityDAO;

@Repository
public class ProductAttributeEntityDAOImpl extends BaseDao implements ProductAttributeEntityDAO{

	@Override
    public ProductAttributeEntity getProductAttributeEntityById(Long id) {
        return getSqlSession().selectOne("getProductAttributeEntityById", id);
    }
	
}
