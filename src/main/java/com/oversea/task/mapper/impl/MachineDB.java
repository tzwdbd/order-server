package com.oversea.task.mapper.impl;

import java.io.Serializable;
import java.util.Date;

/**
 * @author fengjian
 * @version V1.0
 * @title: sea-online
 * @Package com.oversea.entity
 * @Description: 机器入库持久化类
 * @date 15/12/9 13:45
 */
public class MachineDB implements Serializable {

    private static final long serialVersionUID = 2554822648686114407L;

    private String id;

    private String machineName;

    private String group;

    private Integer maxQueueSize;

    private String clientJarVersion;

    private String osName;

    private String usedMemory;

    private String cpuRatio;

    private String freeMemory;

    private Date createTime;

    private Date modifyTime;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMachineName() {
        return machineName;
    }

    public void setMachineName(String machineName) {
        this.machineName = machineName;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public Integer getMaxQueueSize() {
        return maxQueueSize;
    }

    public void setMaxQueueSize(Integer maxQueueSize) {
        this.maxQueueSize = maxQueueSize;
    }

    public String getClientJarVersion() {
        return clientJarVersion;
    }

    public void setClientJarVersion(String clientJarVersion) {
        this.clientJarVersion = clientJarVersion;
    }

    public String getOsName() {
        return osName;
    }

    public void setOsName(String osName) {
        this.osName = osName;
    }

    public String getUsedMemory() {
        return usedMemory;
    }

    public void setUsedMemory(String usedMemory) {
        this.usedMemory = usedMemory;
    }

    public String getCpuRatio() {
        return cpuRatio;
    }

    public void setCpuRatio(String cpuRatio) {
        this.cpuRatio = cpuRatio;
    }

    public String getFreeMemory() {
        return freeMemory;
    }

    public void setFreeMemory(String freeMemory) {
        this.freeMemory = freeMemory;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Date getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(Date modifyTime) {
        this.modifyTime = modifyTime;
    }
}
