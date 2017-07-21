package com.oversea.task.mapper.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.oversea.task.domain.ExpressSpider;
import com.oversea.task.mapper.BaseDao;
import com.oversea.task.mapper.ExpressSpiderDAO;

@Repository(value="expressSpiderDAO")
public class ExpressSpiderDAOImpl extends BaseDao implements ExpressSpiderDAO {

	@Override
	public long addExpressSpider(ExpressSpider expressSpider) {
		return getSqlSession().insert("ExpressSpiderSQL.addExpressSpider", expressSpider);
	}

	@Override
	public List<ExpressSpider> getExpressSpiderByStatus(Integer status) {
		Map<String, Object> map = new HashMap<String, Object>();
        map.put("status", status);
		return getSqlSession().selectList("ExpressSpiderSQL.getExpressSpiderByStatus", map);
	}

	@Override
	public List<ExpressSpider> getExpressSpiderByExpressNo(String expressNo) {
		Map<String, Object> map = new HashMap<String, Object>();
        map.put("expressNo", expressNo);
		return getSqlSession().selectList("ExpressSpiderSQL.getExpressSpiderByExpressNo", map);
	}
	
	@Override
	public void updateExpressSpider(ExpressSpider item) {
		getSqlSession().update("ExpressSpiderSQL.updateExpressSpider", item);
	}

}
