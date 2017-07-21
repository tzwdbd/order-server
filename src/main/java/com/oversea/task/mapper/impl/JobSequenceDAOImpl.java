package com.oversea.task.mapper.impl;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.oversea.task.domain.JobSequence;
import com.oversea.task.mapper.BaseDao;
import com.oversea.task.mapper.JobSequenceDAO;

@Repository
public class JobSequenceDAOImpl extends BaseDao implements JobSequenceDAO {
	@Override
	public void addJobSequence(JobSequence jobSequence) {
		getSqlSession().insert("addJobSequence",jobSequence);
    }
	
	@Override
	public void updateJobSequenceByDynamic(JobSequence jobSequence) {
		getSqlSession().update("updateJobSequenceByDynamic",jobSequence);
    }
	
	@Override
	public JobSequence getJobSequenceById(Long id) {
		return getSqlSession().selectOne("getJobSequenceById", id);
    }
	
	@Override
	public int countJobSequenceById(Long id) {
		return (Integer)getSqlSession().selectOne("countJobSequenceById",id);
    }
	
	@Override
	public JobSequence getJobSequenceByName(String name) {
		return getSqlSession().selectOne("getJobSequenceByName", name);
	}

	@Override
	public int updateJobSequenceById(Long id, Long fromStart, Long toStart) {
		Map<String,Object> param = new HashMap<String,Object>();
		param.put("id", id);
		param.put("fromStart", fromStart);
		param.put("toStart", toStart);
	    return getSqlSession().update("updateJobSequenceById",param);
	}
}