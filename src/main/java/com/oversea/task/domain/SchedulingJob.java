package com.oversea.task.domain;

import java.io.Serializable;

/**
 * 定时任务实体类
 *
 * @author linlvping
 */
public class SchedulingJob implements Serializable {
    private static final long serialVersionUID = 5691917942839700856L;
    private String id;
    private String jobClass;
    private String jobMethod;
    private String jobName;
    private String jobGroup;
    private String jobStatus;
    private String cronExpression;
    private String description;
    private String methodArgs;

    public String getMethodArgs() {
        return methodArgs;
    }

    public void setMethodArgs(String methodArgs) {
        this.methodArgs = methodArgs;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getJobClass() {
        return jobClass;
    }

    public void setJobClass(String jobClass) {
        this.jobClass = jobClass;
    }

    public String getJobMethod() {
        return jobMethod;
    }

    public void setJobMethod(String jobMethod) {
        this.jobMethod = jobMethod;
    }

    public String getJobName() {
        return jobName;
    }

    public void setJobName(String jobName) {
        this.jobName = jobName;
    }

    public String getJobGroup() {
        return jobGroup;
    }

    public void setJobGroup(String jobGroup) {
        this.jobGroup = jobGroup;
    }

    public String getJobStatus() {
        return jobStatus;
    }

    public void setJobStatus(String jobStatus) {
        this.jobStatus = jobStatus;
    }

    public String getCronExpression() {
        return cronExpression;
    }

    public void setCronExpression(String cronExpression) {
        this.cronExpression = cronExpression;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTriggerName() {
        return "quartzJob" + this.getJobName() + "Trigger";
    }

    @Override
    public String toString() {
        return "SchedulingJob [id=" + id + ", jobClass=" + jobClass
                + ", jobMethod=" + jobMethod + ", jobName=" + jobName
                + ", jobGroup=" + jobGroup + ", jobStatus=" + jobStatus
                + ", cronExpression=" + cronExpression + ", description="
                + description + ", methodArgs=" + methodArgs + "]";
    }


}