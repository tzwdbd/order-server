package com.oversea.task.mapper;

import com.oversea.task.obj.Machine;
import com.oversea.task.obj.MachineDB;


/**
 * 
 * @version V1.0
 * @title: sea-online
 * @Package com.oversea.web.mapper
 * @Description: 机器状态显示器
 * @date 15/12/9 13:44
 */
public interface MachineDAO {

    /**
     * 新增机器持久化
     *
     * @param machine
     */
    public void addMachine(Machine machine);

    /**
     * 刷新机器信息
     *
     * @param machine
     */
    public void flushMachine(Machine machine);


    /**
     * 移除机器
     *
     * @param machine
     */
    public void removeMachine(Machine machine);

    /**
     * 移除所有机器
     */
    public void removeAllMachine();
    
    public MachineDB getMachineById(String id);
}
