package com.oversea.task.mapper.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.oversea.task.domain.BrushOrderDetail;
import com.oversea.task.mapper.BaseDao;
import com.oversea.task.mapper.BrushOrderDetailDAO;
@Repository
public class BrushOrderDetailDAOImpl extends BaseDao implements BrushOrderDetailDAO {
	private static final String NAMESPACE = "BrushOrderDetailSQL.";
	public void addBrushOrderDetail(BrushOrderDetail brushOrderDetail) {
		getSqlSession().insert(NAMESPACE+"addBrushOrderDetail",brushOrderDetail);
}
	public void updateBrushOrderDetailById(BrushOrderDetail brushOrderDetail) {
		getSqlSession().update(NAMESPACE+"updateBrushOrderDetailById",brushOrderDetail);
}
	public void updateBrushOrderDetailByDynamic(BrushOrderDetail brushOrderDetail) {
		getSqlSession().update(NAMESPACE+"updateBrushOrderDetailByDynamic",brushOrderDetail);
}
	public BrushOrderDetail getBrushOrderDetailById(Long id) {
		return getSqlSession().selectOne(NAMESPACE+"getBrushOrderDetailById", id);
}
	public int countBrushOrderDetailById(Long id) {
		return (Integer)getSqlSession().selectOne(NAMESPACE+"countBrushOrderDetailById",id);
}
	@Override
	public List<BrushOrderDetail> getBrushOrderDetailByDate(Date dispatchTime) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("dispatchTime", dispatchTime);
		return getSqlSession().selectList(NAMESPACE+"getBrushOrderDetailByDate", map);
	}
	@Override
	public void updateStatus(Long id, Integer status) {
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("id", id);
		map.put("status", status);
		getSqlSession().update(NAMESPACE+"updateStatus",map);
		
	}
	@Override
	public List<BrushOrderDetail> getBrushOrderDetailListBystatus(String status) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("status", status);
		return getSqlSession().selectList(NAMESPACE+"getBrushOrderDetailListBystatus", map);
	}
	@Override
	public List<BrushOrderDetail> getBrushOrderDetailListByExpressstatus(
			String status) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("expressStatus", status);
		return getSqlSession().selectList(NAMESPACE+"getBrushOrderDetailListByExpressstatus", map);
	}
	@Override
	public List<BrushOrderDetail> getBrushOrderDetailListByThree(String status) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("status", status);
		return getSqlSession().selectList(NAMESPACE+"getBrushOrderDetailListByThree", map);
	}
	@Override
	public List<BrushOrderDetail> getBrushOrderDetailListByExpressNo(
			List<String> expressNos) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("expressNos", expressNos);
		return getSqlSession().selectList(NAMESPACE+"getBrushOrderDetailListByExpressNo", map);
	}
}