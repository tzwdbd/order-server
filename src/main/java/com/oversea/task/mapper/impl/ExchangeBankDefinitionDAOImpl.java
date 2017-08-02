package com.oversea.task.mapper.impl;

import com.oversea.task.domain.ExchangeBankDefinition;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.oversea.task.mapper.BaseDao;
import com.oversea.task.mapper.ExchangeBankDefinitionDAO;
@Repository
public class ExchangeBankDefinitionDAOImpl extends BaseDao implements ExchangeBankDefinitionDAO {
	public void addExchangeBankDefinition(ExchangeBankDefinition exchangeBankDefinition) {
		getSqlSession().insert("addExchangeBankDefinition",exchangeBankDefinition);
}
	public void updateExchangeBankDefinitionById(ExchangeBankDefinition exchangeBankDefinition) {
		getSqlSession().update("updateExchangeBankDefinitionById",exchangeBankDefinition);
}
	public void updateExchangeBankDefinitionByDynamic(ExchangeBankDefinition exchangeBankDefinition) {
		getSqlSession().update("updateExchangeBankDefinitionByDynamic",exchangeBankDefinition);
}
	public List<ExchangeBankDefinition> getExchangeBankDefinitionById(Long id) {
		return getSqlSession().selectList("getExchangeBankDefinitionById", id);
}
	public int countExchangeBankDefinitionById(Long id) {
		return (Integer)getSqlSession().selectOne("countExchangeBankDefinitionById",id);
}
	@Override
	public ExchangeBankDefinition getExchangeBankDefinitionByUnit(String units) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("units", units);
		return getSqlSession().selectOne("getExchangeBankDefinitionByUnit",map);
	}
}