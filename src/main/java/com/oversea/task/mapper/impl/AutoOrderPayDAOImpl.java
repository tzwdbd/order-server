package com.oversea.task.mapper.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.oversea.task.domain.AutoOrderPay;
import com.oversea.task.mapper.AutoOrderPayDAO;
import com.oversea.task.mapper.BaseDao;
@Repository
public class AutoOrderPayDAOImpl extends BaseDao implements AutoOrderPayDAO {
	public void addAutoOrderPay(AutoOrderPay autoOrderPay) {
		getSqlSession().insert("addAutoOrderPay",autoOrderPay);
}
	public void updateAutoOrderPayById(AutoOrderPay autoOrderPay) {
		getSqlSession().update("updateAutoOrderPayById",autoOrderPay);
}
	public void updateAutoOrderPayByDynamic(AutoOrderPay autoOrderPay) {
		getSqlSession().update("updateAutoOrderPayByDynamic",autoOrderPay);
}
	public List<AutoOrderPay> getAutoOrderPayById(Long id) {
		return getSqlSession().selectList("getAutoOrderPayById", id);
}
	public int countAutoOrderPayById(Long id) {
		return (Integer)getSqlSession().selectOne("countAutoOrderPayById",id);
}
	@Override
	public AutoOrderPay getOrderPayBySiteName(String siteName) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("siteName", siteName);
		return getSqlSession().selectOne("getOrderPayBySiteName",map);
	}
}