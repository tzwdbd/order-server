package com.oversea.task.mapper.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.oversea.task.domain.AutoOrderScribeExpress;
import com.oversea.task.mapper.AutoOrderScribeExpressDAO;
import com.oversea.task.mapper.BaseDao;
@Repository
public class AutoOrderScribeExpressDAOImpl extends BaseDao implements AutoOrderScribeExpressDAO {
	public void addAutoOrderScribeExpress(AutoOrderScribeExpress autoOrderScribeExpress) {
		getSqlSession().insert("addAutoOrderScribeExpress",autoOrderScribeExpress);
}
	public void updateAutoOrderScribeExpressById(AutoOrderScribeExpress autoOrderScribeExpress) {
		getSqlSession().update("updateAutoOrderScribeExpressById",autoOrderScribeExpress);
}
	public void updateAutoOrderScribeExpressByDynamic(AutoOrderScribeExpress autoOrderScribeExpress) {
		getSqlSession().update("updateAutoOrderScribeExpressByDynamic",autoOrderScribeExpress);
}
	public List<AutoOrderScribeExpress> getAutoOrderScribeExpressById(Long id) {
		return getSqlSession().selectList("getAutoOrderScribeExpressById", id);
}
	public int countAutoOrderScribeExpressById(Long id) {
		return (Integer)getSqlSession().selectOne("countAutoOrderScribeExpressById",id);
}
	@Override
	public AutoOrderScribeExpress getAutoOrderScribeExpressBySiteName(
			String siteName) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("siteName", siteName);
		return getSqlSession().selectOne("getAutoOrderScribeExpressBySiteName",map);
	}
}