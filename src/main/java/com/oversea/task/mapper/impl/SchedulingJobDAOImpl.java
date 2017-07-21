package com.oversea.task.mapper.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.oversea.task.domain.SchedulingJob;
import com.oversea.task.mapper.BaseDao;
import com.oversea.task.mapper.SchedulingJobDAO;
import com.oversea.task.util.StringUtil;
/**
 * 任务quartz_job表DAO实现
 * @author linlvping
 *
 */
@Repository(value="schedulingJobDAO")
public class SchedulingJobDAOImpl extends BaseDao implements SchedulingJobDAO {
	
	private String onlyJobId;
	
	public String getOnlyJobId() {
		return onlyJobId;
	}

	public void setOnlyJobId(String onlyJobId) {
		this.onlyJobId = onlyJobId;
	}

	@Override
	public void insert(SchedulingJob schedulingJob) {
		getSqlSession().insert("addSchedulingJob", schedulingJob);
	}

	@Override
	public void update(SchedulingJob schedulingJob) {
		getSqlSession().update("updateSchedulingJob", schedulingJob);
	}

	@Override
	public void delete(int id) {
		getSqlSession().delete("deleteSchedulingJob", id);

	}

	@Override
	public List<SchedulingJob> getSchedulingJobList() {
		if(StringUtil.isNotEmpty(onlyJobId)){
			return getSqlSession().selectList("getSchedulingJobList",onlyJobId);
		}
		return getSqlSession().selectList("getSchedulingJobList",null);
	}

	@Override
	public SchedulingJob getSchedulingJob(int id) {
		List<SchedulingJob> list = getSqlSession().selectList("getSchedulingJob", id);
		if (list != null && list.size() == 1) {
			return list.get(0);
		}
		return null;
	}

}
