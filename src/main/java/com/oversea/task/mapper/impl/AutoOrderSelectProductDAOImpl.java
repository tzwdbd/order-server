package com.oversea.task.mapper.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.oversea.task.domain.AutoOrderSelectProduct;
import com.oversea.task.mapper.AutoOrderSelectProductDAO;
import com.oversea.task.mapper.BaseDao;
@Repository
public class AutoOrderSelectProductDAOImpl extends BaseDao implements AutoOrderSelectProductDAO {
	public void addAutoOrderSelectProduct(AutoOrderSelectProduct autoOrderSelectProduct) {
		getSqlSession().insert("addAutoOrderSelectProduct",autoOrderSelectProduct);
}
	public void updateAutoOrderSelectProductById(AutoOrderSelectProduct autoOrderSelectProduct) {
		getSqlSession().update("updateAutoOrderSelectProductById",autoOrderSelectProduct);
}
	public void updateAutoOrderSelectProductByDynamic(AutoOrderSelectProduct autoOrderSelectProduct) {
		getSqlSession().update("updateAutoOrderSelectProductByDynamic",autoOrderSelectProduct);
}
	public List<AutoOrderSelectProduct> getAutoOrderSelectProductById(Long id) {
		return getSqlSession().selectList("getAutoOrderSelectProductById", id);
}
	public int countAutoOrderSelectProductById(Long id) {
		return (Integer)getSqlSession().selectOne("countAutoOrderSelectProductById",id);
}
	@Override
	public AutoOrderSelectProduct getOrderSelectProductBySiteName(
			String siteName) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("siteName", siteName);
		return getSqlSession().selectOne("getOrderSelectProductBySiteName",map);
	}
}