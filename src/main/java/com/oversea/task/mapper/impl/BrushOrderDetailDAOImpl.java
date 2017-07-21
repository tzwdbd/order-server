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
	public void addBrushOrderDetail(BrushOrderDetail brushOrderDetail) {
		getSqlSession().insert("addBrushOrderDetail",brushOrderDetail);
}
	public void updateBrushOrderDetailById(BrushOrderDetail brushOrderDetail) {
		getSqlSession().update("updateBrushOrderDetailById",brushOrderDetail);
}
	public void updateBrushOrderDetailByDynamic(BrushOrderDetail brushOrderDetail) {
		getSqlSession().update("updateBrushOrderDetailByDynamic",brushOrderDetail);
}
	public BrushOrderDetail getBrushOrderDetailById(Long id) {
		return getSqlSession().selectOne("getBrushOrderDetailById", id);
}
	public int countBrushOrderDetailById(Long id) {
		return (Integer)getSqlSession().selectOne("countBrushOrderDetailById",id);
}
	@Override
	public BrushOrderDetail getBrushOrderDetailByDate(Date dispatchTime) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("dispatchTime", dispatchTime);
		return getSqlSession().selectOne("getBrushOrderDetailByDate", map);
	}
	@Override
	public void updateStatus(Long id, Integer status) {
		Map<String,Object> map = new HashMap<String, Object>();
		map.put("id", id);
		map.put("status", status);
		getSqlSession().update("updateStatus",map);
		
	}
	@Override
	public List<BrushOrderDetail> getBrushOrderDetailListBystatus(String status) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("status", status);
		return getSqlSession().selectList("getBrushOrderDetailListBystatus", map);
	}
	@Override
	public List<BrushOrderDetail> getBrushOrderDetailListByExpressstatus(
			String status) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("expressStatus", status);
		return getSqlSession().selectList("getBrushOrderDetailListByExpressstatus", map);
	}
	@Override
	public List<BrushOrderDetail> getBrushOrderDetailListByThree(String status) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("status", status);
		return getSqlSession().selectList("getBrushOrderDetailListByThree", map);
	}
	@Override
	public List<BrushOrderDetail> getBrushOrderDetailListByExpressNo(
			List<String> expressNos) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("expressNos", expressNos);
		return getSqlSession().selectList("getBrushOrderDetailListByExpressNo", map);
	}
}