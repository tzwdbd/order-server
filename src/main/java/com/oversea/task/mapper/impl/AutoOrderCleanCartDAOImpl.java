package com.oversea.task.mapper.impl;

import com.oversea.task.domain.AutoOrderCleanCart;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.oversea.task.mapper.AutoOrderCleanCartDAO;
import com.oversea.task.mapper.BaseDao;

public class AutoOrderCleanCartDAOImpl extends BaseDao implements AutoOrderCleanCartDAO {
	public void addAutoOrderCleanCart(AutoOrderCleanCart autoOrderCleanCart) {
		getSqlSession().insert("addAutoOrderCleanCart",autoOrderCleanCart);
}
	public void updateAutoOrderCleanCartById(AutoOrderCleanCart autoOrderCleanCart) {
		getSqlSession().update("updateAutoOrderCleanCartById",autoOrderCleanCart);
}
	public void updateAutoOrderCleanCartByDynamic(AutoOrderCleanCart autoOrderCleanCart) {
		getSqlSession().update("updateAutoOrderCleanCartByDynamic",autoOrderCleanCart);
}
	public List<AutoOrderCleanCart> getAutoOrderCleanCartById(Long id) {
		return getSqlSession().selectList("getAutoOrderCleanCartById", id);
}
	public int countAutoOrderCleanCartById(Long id) {
		return (Integer)getSqlSession().selectOne("countAutoOrderCleanCartById",id);
}
	@Override
	public AutoOrderCleanCart getAutoOrderCleanCartBySiteName(String siteName) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("siteName", siteName);
		return getSqlSession().selectOne("getAutoOrderCleanCartBySiteName",map);
	}
}