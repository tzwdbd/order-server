package com.oversea.task.mapper.impl;

import org.springframework.stereotype.Repository;

import com.oversea.task.domain.MallDefinition;
import com.oversea.task.mapper.BaseDao;
import com.oversea.task.mapper.MallDefinitionDAO;

@Repository("mallDefinitionDAO")
public class MallDefinitionDAOImpl extends BaseDao implements MallDefinitionDAO {

	private static final String NAMESPACE = "MallDefinitionSQL.";
	
	@Override
	public MallDefinition getMallDefinitionByName(String name) {
		return getSqlSession().selectOne(NAMESPACE + "getMallDefinitionByName", name);
	}
}