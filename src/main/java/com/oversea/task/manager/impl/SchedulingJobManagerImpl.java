package com.oversea.task.manager.impl;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.Trigger;

import com.oversea.task.domain.QuartzJobBean;
import com.oversea.task.domain.SchedulingJob;
import com.oversea.task.manager.SchedulingJobManager;
import com.oversea.task.mapper.SchedulingJobDAO;
import com.oversea.task.util.QuartzHelper;

/**
 * 定时任务管理实现类
 *
 * @author linlvping
 */
public class SchedulingJobManagerImpl implements SchedulingJobManager {

    private Log log = LogFactory.getLog(SchedulingJobManagerImpl.class);

    private Scheduler scheduler = QuartzHelper.getScheduler();

    @Resource
    private SchedulingJobDAO schedulingJobDAO;

    @Override
    public void start(int id) throws Exception {
        log.error("SchedulingJobManagerImpl start id=" + id);
        SchedulingJob schedulingJob = schedulingJobDAO.getSchedulingJob(id);
        enable(schedulingJob);
        schedulingJob.setJobStatus(JOB_STATUS_ON);
        log.error("SchedulingJobManagerImpl start id update 1");
        schedulingJobDAO.update(schedulingJob);
        log.error("SchedulingJobManagerImpl start id update 2");
    }

    @Override
    public void start(SchedulingJob schedulingJob) throws Exception {
        log.error("SchedulingJobManagerImpl start schedulingJob.id=" + schedulingJob.getId());
        enable(schedulingJob);
        schedulingJob.setJobStatus(JOB_STATUS_ON);
        log.error("SchedulingJobManagerImpl start schedulingJob update 1");
        schedulingJobDAO.update(schedulingJob);
        log.error("SchedulingJobManagerImpl start schedulingJob update 2");
    }

    @Override
    public void add(SchedulingJob schedulingJob) throws Exception {
        schedulingJobDAO.insert(schedulingJob);
        enable(schedulingJob);
    }

    @Override
    public void startNow(int id) throws Exception {
        final SchedulingJob schedulingJob = schedulingJobDAO.getSchedulingJob(id);
        //交给线程执行
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    QuartzJobBean quartzJobBean = new QuartzJobBean();
                    quartzJobBean.executeMehotd(schedulingJob.getJobClass(), schedulingJob.getJobMethod(), schedulingJob.getMethodArgs());
                } catch (JobExecutionException e) {
                    log.error(e.getMessage(),e);
                }
            }
        }).start();
    }
    
    @Override
    public void startNow(String ids) throws Exception {
    	final String[] idArray = ids.split("\\,") ;
        //交给线程执行
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    QuartzJobBean quartzJobBean = new QuartzJobBean();
                    for(int i=0;i<idArray.length;i++){
                		int id = Integer.valueOf(idArray[i]) ;
                		SchedulingJob schedulingJob = schedulingJobDAO.getSchedulingJob(id);
                		log.error("批量job正在执行："+id);
                		quartzJobBean.executeMehotd(schedulingJob.getJobClass(), schedulingJob.getJobMethod(), schedulingJob.getMethodArgs());
                	}
                } catch (JobExecutionException e) {
                    log.error(e.getMessage(),e);
                }
            }
        }).start();
    }

    @Override
    public void update(SchedulingJob schedulingJob) throws Exception {
        SchedulingJob old = schedulingJobDAO.getSchedulingJob(Integer.parseInt(schedulingJob.getId()));
        diable(old);
        schedulingJobDAO.update(schedulingJob);
        if (JOB_STATUS_ON.equalsIgnoreCase(schedulingJob.getJobStatus())) {
            enable(schedulingJob);
        }
    }

    @Override
    public void delete(int id) throws Exception {
        SchedulingJob schedulingJob = schedulingJobDAO.getSchedulingJob(id);
        diable(schedulingJob);
        schedulingJobDAO.delete(id);
    }

    @Override
    public void stop(int id) throws Exception {
        SchedulingJob schedulingJob = schedulingJobDAO.getSchedulingJob(id);
        diable(schedulingJob);
        schedulingJob.setJobStatus(JOB_STATUS_OFF);
        schedulingJobDAO.update(schedulingJob);
    }

    @Override
    public void startAll() throws Exception {
        List<SchedulingJob> list = schedulingJobDAO.getSchedulingJobList();
        if (list != null) {
            for (SchedulingJob sj : list) {
                if (JOB_STATUS_OFF.equalsIgnoreCase(sj.getJobStatus())) {
                    sj.setJobStatus(JOB_STATUS_ON);
                    schedulingJobDAO.update(sj);
                    enable(sj);
                }
            }
        }
    }

    @Override
    public void stopAll() throws Exception {
        List<SchedulingJob> list = schedulingJobDAO.getSchedulingJobList();
        if (list != null) {
            for (SchedulingJob sj : list) {
                if (JOB_STATUS_ON.equalsIgnoreCase(sj.getJobStatus())) {
                    sj.setJobStatus(JOB_STATUS_OFF);
                    schedulingJobDAO.update(sj);
                    diable(sj);
                }
            }
        }
    }

    @Override
    public List<SchedulingJob> getJobList() throws Exception {
        return schedulingJobDAO.getSchedulingJobList();
    }

    @Override
    public SchedulingJob getJob(int id) throws Exception {
        return schedulingJobDAO.getSchedulingJob(id);
    }

    /**
     * 停止任务
     *
     * @param schedulingJob
     * @throws Exception
     */
    private void diable(SchedulingJob schedulingJob) throws Exception {
        Scheduler scheduler = QuartzHelper.getScheduler();
        Trigger trigger = scheduler.getTrigger(schedulingJob.getTriggerName(), schedulingJob.getJobGroup());
        if (null != trigger) {
            scheduler.deleteJob(schedulingJob.getJobName(), schedulingJob.getJobGroup());
        }
    }

    /**
     * 启动任务
     *
     * @param schedulingJob
     * @throws Exception
     */
    private void enable(SchedulingJob schedulingJob) throws Exception {
        log.error("SchedulingJobManagerImpl enable 1");
        CronTrigger trigger = (CronTrigger) scheduler.getTrigger(schedulingJob.getTriggerName(), schedulingJob.getJobGroup());
        log.error("SchedulingJobManagerImpl enable 2");
        if (null == trigger) {
            log.error("SchedulingJobManagerImpl enable 3");
            // Trigger不存在，那么创建一个  
            JobDetail jobDetail = new JobDetail(schedulingJob.getJobName(), schedulingJob.getJobGroup(), QuartzJobBean.class);
            jobDetail.getJobDataMap().put(QuartzJobBean.TARGET_CLASS, schedulingJob.getJobClass());
            jobDetail.getJobDataMap().put(QuartzJobBean.TARGET_METHOD, schedulingJob.getJobMethod());
            jobDetail.getJobDataMap().put(QuartzJobBean.TARGET_ARGUMENTS, schedulingJob.getMethodArgs());
            trigger = new CronTrigger(schedulingJob.getTriggerName(), schedulingJob.getJobGroup(), schedulingJob.getCronExpression());
            scheduler.scheduleJob(jobDetail, trigger);
        } else {
            // Trigger已存在，那么更新相应的定时设置  
            log.error("SchedulingJobManagerImpl enable 4");
            trigger.setCronExpression(schedulingJob.getCronExpression());
            scheduler.rescheduleJob(trigger.getName(), trigger.getGroup(), trigger);
        }
        scheduler.start();
    }
}
