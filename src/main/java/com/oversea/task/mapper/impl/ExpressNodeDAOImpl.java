package com.oversea.task.mapper.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.oversea.task.domain.ExpressNode;
import com.oversea.task.mapper.BaseDao;
import com.oversea.task.mapper.ExpressNodeDAO;

@Repository
public class ExpressNodeDAOImpl extends BaseDao implements ExpressNodeDAO {

	@Override
	public int addExpressNode(ExpressNode expressNode) {
		// TODO Auto-generated method stub
		return getSqlSession().insert("ExpressNodeSQL.addExpressNode", expressNode);
	}

	@Override
	public List<ExpressNode> getExpressNodeByOccurTime(String expressNo,
			Date occurTime) {
		// TODO Auto-generated method stub
		Map<String, Object> map = new HashMap<String, Object>();
        map.put("expressNo", expressNo);
        map.put("occurTime", occurTime);
		return getSqlSession().selectList("ExpressNodeSQL.getExpressNodeByOccurTime", map);
	}

}
