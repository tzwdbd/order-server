package com.oversea.task.mapper.impl;

import org.springframework.stereotype.Repository;

import com.oversea.task.domain.BrushInfo;
import com.oversea.task.mapper.BaseDao;
import com.oversea.task.mapper.BrushInfoDAO;
@Repository
public class BrushInfoDAOImpl extends BaseDao implements BrushInfoDAO {
	public void addBrushInfo(BrushInfo brushInfo) {
		getSqlSession().insert("addBrushInfo",brushInfo);
}
	public void updateBrushInfoById(BrushInfo brushInfo) {
		getSqlSession().update("updateBrushInfoById",brushInfo);
}
	public void updateBrushInfoByDynamic(BrushInfo brushInfo) {
		getSqlSession().update("updateBrushInfoByDynamic",brushInfo);
}
	public BrushInfo getBrushInfoById(Long id) {
		return getSqlSession().selectOne("getBrushInfoById", id);
}
	public int countBrushInfoById(Long id) {
		return (Integer)getSqlSession().selectOne("countBrushInfoById",id);
}
}