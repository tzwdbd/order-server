package com.oversea.task.mapper;

import java.util.List;

import com.oversea.task.domain.SchedulingJob;

/**
 * 任务quartz_job表DAO
 * @author linlvping
 *
 */
public interface SchedulingJobDAO {

    /**
     * 添加任务
     * @param schedulingJob
     */
    public void insert(SchedulingJob schedulingJob);
    
    /**
     * 更新任务
     * @param schedulingJob
     */
    public void update(SchedulingJob schedulingJob);
    
    /**
     * 删除任务
     * @param id
     */
    public void delete(int id);
    
    /**
     * 获取所有任务
     * @return
     */
    public List<SchedulingJob> getSchedulingJobList();
    
    /**
     * 根据ID获取任务
     * @param id
     * @return
     */
    public SchedulingJob getSchedulingJob(int id);
}
