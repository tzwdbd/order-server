package com.oversea.task.mapper.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.oversea.task.domain.UserTradeExpress;
import com.oversea.task.mapper.BaseDao;
import com.oversea.task.mapper.UserTradeExpressDAO;

/**
 * @author xiong chen
 * @version V1.0
 * @title: sea-online
 * @Package com.oversea.task.mapper.impl
 * @Description:
 * @date 2016年12月22日
 */

@Repository("userTradeExpressDAO")
public class UserTradeExpressDAOImpl extends BaseDao implements UserTradeExpressDAO{

	private static String namespace = "UserTradeExpressMapper.";
	
	@Override
	public int insert(UserTradeExpress record) {
		return getSqlSession().insert(namespace+"insert", record);
	}

	@Override
	public List<UserTradeExpress> getUserTradeExpressByExpressCompanyAndMaxStatus(
			String expressCompany, Integer status) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("expressCompany", expressCompany);
		param.put("status", status);
		return getSqlSession().selectList(namespace+"getUserTradeExpressByExpressCompanyAndMaxStatus", param);
	}

	@Override
	public int updateUserTradeExpressByDynamic(
			UserTradeExpress userTradeExpress) {
		return getSqlSession().update(namespace+"updateUserTradeExpressByDynamic", userTradeExpress);
	}
	
	@Override
	public UserTradeExpress getUserTradeExpressByExpressNo(String expressNo){
		return (UserTradeExpress) getSqlSession().selectOne("getUserTradeExpressByExpressNo", expressNo);
	}
	
}
