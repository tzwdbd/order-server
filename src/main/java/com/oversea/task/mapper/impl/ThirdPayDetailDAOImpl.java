package com.oversea.task.mapper.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.oversea.task.domain.ThirdPayDetail;
import com.oversea.task.mapper.BaseDao;
import com.oversea.task.mapper.ThirdPayDetailDAO;
@Repository
public class ThirdPayDetailDAOImpl extends BaseDao implements ThirdPayDetailDAO {
	public void addThirdPayDetail(ThirdPayDetail thirdPayDetail) {
		getSqlSession().insert("addThirdPayDetail",thirdPayDetail);
}
	public void updateThirdPayDetailById(ThirdPayDetail thirdPayDetail) {
		getSqlSession().update("updateThirdPayDetailById",thirdPayDetail);
}
	public void updateThirdPayDetailByDynamic(ThirdPayDetail thirdPayDetail) {
		getSqlSession().update("updateThirdPayDetailByDynamic",thirdPayDetail);
}
	public List<ThirdPayDetail> getThirdPayDetailById(Long id) {
		return getSqlSession().selectList("getThirdPayDetailById", id);
}
	public int countThirdPayDetailById(Long id) {
		return (Integer)getSqlSession().selectOne("countThirdPayDetailById",id);
}
}