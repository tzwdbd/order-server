package com.oversea.task.mapper.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.oversea.task.domain.ExchangeDefinition;
import com.oversea.task.mapper.BaseDao;
import com.oversea.task.mapper.ExchangeDefinitionDAO;

@Repository
public class ExchangeDefinitionDAOImpl extends BaseDao implements ExchangeDefinitionDAO {
	
	@Override
	public ExchangeDefinition getExchangeDefinitionByUnits(String units) {
		return getSqlSession().selectOne("ExchangeDefinitionSQL.getExchangeDefinitionByUnits", units);
	}

    @Override
    public int updateExchangeDefinitionByUnits(String units,Integer fromRmb,Integer toRmb) {
        Map<String,Object> pramMap = new HashMap<String,Object>();
        pramMap.put("units",units);
        pramMap.put("fromRmb",fromRmb);
        pramMap.put("toRmb", toRmb);
        return getSqlSession().update("ExchangeDefinitionSQL.updateExchangeDefinitionByUnits", pramMap);
    }

}
