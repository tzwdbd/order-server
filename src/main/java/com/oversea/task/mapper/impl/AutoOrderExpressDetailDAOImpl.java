package com.oversea.task.mapper.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.oversea.task.domain.AutoOrderExpressDetail;
import com.oversea.task.mapper.AutoOrderExpressDetailDAO;
import com.oversea.task.mapper.BaseDao;
@Repository
public class AutoOrderExpressDetailDAOImpl extends BaseDao implements AutoOrderExpressDetailDAO {
	public void addAutoOrderExpressDetail(AutoOrderExpressDetail autoOrderExpressDetail) {
		getSqlSession().insert("addAutoOrderExpressDetail",autoOrderExpressDetail);
}
	public void updateAutoOrderExpressDetailById(AutoOrderExpressDetail autoOrderExpressDetail) {
		getSqlSession().update("updateAutoOrderExpressDetailById",autoOrderExpressDetail);
}
	public void updateAutoOrderExpressDetailByDynamic(AutoOrderExpressDetail autoOrderExpressDetail) {
		getSqlSession().update("updateAutoOrderExpressDetailByDynamic",autoOrderExpressDetail);
}
	public List<AutoOrderExpressDetail> getAutoOrderExpressDetailById(Long id) {
		return getSqlSession().selectList("getAutoOrderExpressDetailById", id);
}
	public int countAutoOrderExpressDetailById(Long id) {
		return (Integer)getSqlSession().selectOne("countAutoOrderExpressDetailById",id);
}
	@Override
	public AutoOrderExpressDetail getAutoOrderExpressDetailBySiteName(
			String siteName) {
		Map<String,Object> map = new HashMap<String,Object>();
		map.put("siteName", siteName);
		return getSqlSession().selectOne("getAutoOrderExpressDetailBySiteName",map);
	}
}