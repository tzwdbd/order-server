package com.oversea.task.mapper.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.oversea.task.domain.ExternalOrderDetail;
import com.oversea.task.mapper.BaseDao;
import com.oversea.task.mapper.ExternalOrderDetailDAO;
@Repository
public class ExternalOrderDetailDAOImpl extends BaseDao implements ExternalOrderDetailDAO {
	private static final String NAMESPACE = "ExternalOrderDetailSQL.";
	public void addExternalOrderDetail(ExternalOrderDetail externalOrderDetail) {
		getSqlSession().insert(NAMESPACE+"addExternalOrderDetail",externalOrderDetail);
}
	public void updateExternalOrderDetailById(ExternalOrderDetail externalOrderDetail) {
		getSqlSession().update(NAMESPACE+"updateExternalOrderDetailById",externalOrderDetail);
}
	public void updateExternalOrderDetailByDynamic(ExternalOrderDetail externalOrderDetail) {
		getSqlSession().update(NAMESPACE+"updateExternalOrderDetailByDynamic",externalOrderDetail);
}
	public ExternalOrderDetail getExternalOrderDetailById(Long id) {
		return getSqlSession().selectOne(NAMESPACE+"getExternalOrderDetailById", id);
}
	public int countExternalOrderDetailById(Long id) {
		return (Integer)getSqlSession().selectOne(NAMESPACE+"countExternalOrderDetailById",id);
}
	@Override
	public List<ExternalOrderDetail> getExternalOrderDetailByStatus(int status) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("status", status);
		return getSqlSession().selectList(NAMESPACE+"getExternalOrderDetailByStatus", map);
	}
	@Override
	public void updateStatus(int status,Long id) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("status", status);
		map.put("id", id);
		getSqlSession().update(NAMESPACE+"updateStatus", map);
	}
	@Override
	public List<ExternalOrderDetail> getExternalOrderDetailByOrderNo(
			String saleOrderCode) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("saleOrderCode", saleOrderCode);
		return getSqlSession().selectList(NAMESPACE+"getExternalOrderDetailByOrderNo", map);
	}
	@Override
	public List<ExternalOrderDetail> findExternalOrderDetailsForSpiderExpress(
			String status) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("status", status);
		return getSqlSession().selectList(NAMESPACE+"findExternalOrderDetailsForSpiderExpress", map);
	}
	@Override
	public int countExternalOrderDetail(String saleOrderCode, int accountId,
			Integer company, String siteName, Long id) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("saleOrderCode", saleOrderCode);
		map.put("accountId", accountId);
		map.put("company", company);
		map.put("siteName", siteName);
		map.put("id", id);
		Integer count = getSqlSession().selectOne(NAMESPACE+"countExternalOrderDetail",map);
		if(count==null){
			return 0;
		}
		return count;
	}
}