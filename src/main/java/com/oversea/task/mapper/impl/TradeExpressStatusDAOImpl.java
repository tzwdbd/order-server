package com.oversea.task.mapper.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.oversea.task.domain.TradeExpressStatus;
import com.oversea.task.mapper.BaseDao;
import com.oversea.task.mapper.TradeExpressStatusDAO;

@Repository
public class TradeExpressStatusDAOImpl extends BaseDao implements TradeExpressStatusDAO {

	@Override
	public int addTradeExpressStatus(TradeExpressStatus tradeExpressStatus) {
		return getSqlSession().insert("addTradeExpressStatus", tradeExpressStatus);
	}

	@Override
	public int getCountOfTradeExpressStatusByExpressNoAndOccurTime(String orderNo,String expressNo, Date occurTime) {
		Map<String, Object> map = new HashMap<String, Object>();
        map.put("expressNo", expressNo);
        map.put("occurTime", occurTime);
        map.put("shardKey", "_" + orderNo.charAt(orderNo.length() - 1));
		return getSqlSession().selectOne("countTradeExpressStatusByOrderNoAndOccurTime", map);
	}

}
