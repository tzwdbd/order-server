package com.oversea.task.mapper.impl;

import com.oversea.task.domain.AutoOrderLogin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.oversea.task.mapper.AutoOrderLoginDAO;
import com.oversea.task.mapper.BaseDao;
@Repository
public class AutoOrderLoginDAOImpl extends BaseDao implements AutoOrderLoginDAO {
	public void addAutoOrderLogin(AutoOrderLogin autoOrderLogin) {
		getSqlSession().insert("addAutoOrderLogin",autoOrderLogin);
}
	public void updateAutoOrderLoginById(AutoOrderLogin autoOrderLogin) {
		getSqlSession().update("updateAutoOrderLoginById",autoOrderLogin);
}
	public void updateAutoOrderLoginByDynamic(AutoOrderLogin autoOrderLogin) {
		getSqlSession().update("updateAutoOrderLoginByDynamic",autoOrderLogin);
}
	public List<AutoOrderLogin> getAutoOrderLoginById(Long id) {
		return getSqlSession().selectList("getAutoOrderLoginById", id);
}
	public int countAutoOrderLoginById(Long id) {
		return (Integer)getSqlSession().selectOne("countAutoOrderLoginById",id);
}
	@Override
	public AutoOrderLogin getOrderLoginBySiteName(String siteName) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("mallName", siteName);
		return getSqlSession().selectOne("getOrderLoginBySiteName",map);
	}
}