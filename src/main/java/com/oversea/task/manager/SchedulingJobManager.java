package com.oversea.task.manager;

import java.util.List;

import com.oversea.task.domain.SchedulingJob;

public interface SchedulingJobManager {

  //任务状态为运行
    public final String JOB_STATUS_ON = "0";
    
    //任务状态为停止
    public final String JOB_STATUS_OFF = "1";
    
    /**
     * 根据ID启动任务
     * @param id
     * @throws Exception
     */
    public void start(int id) throws Exception;
    public void start(SchedulingJob schedulingJob) throws Exception;
    
    /**
     * 根据ID停止任务
     * @param id
     * @throws Exception
     */
    public void stop(int id) throws Exception;
    
    /**
     * 添加任务
     * @param schedulingJob
     * @throws Exception
     */
    public void add(SchedulingJob schedulingJob) throws Exception;
    
    /**
     * 更新任务
     * @param schedulingJob
     * @throws Exception
     */
    public void update(SchedulingJob schedulingJob) throws Exception;
    
    /**
     * 根据ID删除任务
     * @param id
     * @throws Exception
     */
    public void delete(int id) throws Exception;
    
    /**
     * 启动所有任务
     * @throws Exception
     */
    public void startAll() throws Exception;
    
    /**
     * 停止所有任务
     * @throws Exception
     */
    public void stopAll() throws Exception;
    
    /**
     * 获取所有任务
     * @return
     * @throws Exception
     */
    public List<SchedulingJob> getJobList() throws Exception;
    
    /**
     * 根据ID获取任务
     * @param id
     * @return
     * @throws Exception
     */
    public SchedulingJob getJob(int id) throws Exception;

    /**
     * 立即执行任务
     * @param id
     * @throws Exception
     */
    public void startNow(int id) throws Exception;
    
    /**
     * 按照顺序批量执行
     * @param ids
     * @throws Exception
     */
    public void startNow(String ids) throws Exception;
    
}
