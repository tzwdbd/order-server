package com.oversea.task.mapper.impl;

import org.springframework.stereotype.Repository;

import com.oversea.task.mapper.BaseDao;
import com.oversea.task.mapper.MachineDAO;
import com.oversea.task.obj.Machine;
import com.oversea.task.obj.MachineDB;

/**
 * @version V1.0
 * @title: sea-online
 * @Package com.oversea.web.mapper.impl
 * @Description: 机器信息
 * @date 15/12/9 13:43
 */
@Repository
public class MachineDAOImpl extends BaseDao implements MachineDAO {

    @Override
    public void addMachine(Machine machine) {
        getSqlSession().insert("addMachineDB", createMachineDB(machine));
    }

    @Override
    public void flushMachine(Machine machine) {
        getSqlSession().update("flushMachineDB", createMachineDB(machine));
    }

    /**
     * 移除机器
     */
    @Override
    public void removeMachine(Machine machine) {
        getSqlSession().delete("removeMachineDB", machine.getId());
    }

    /**
     * 移除所有机器
     */
    @Override
    public void removeAllMachine() {
        getSqlSession().delete("removeAllMachine");
    }

    /**
     * 创建机器
     *
     * @param machine
     * @return
     */
    private MachineDB createMachineDB(Machine machine) {
        MachineDB machineDB = new MachineDB();
        machineDB.setId(machine.getId());
        machineDB.setClientJarVersion(machine.getClientjarVersion());
        machineDB.setMachineName(machine.getMachineName());
        machineDB.setGroup(machine.getChannelGroup());
        machineDB.setOsName(machine.getOsName());
        machineDB.setFreeMemory(machine.getFreeMemory());
        machineDB.setCpuRatio(machine.getCpuRatio());
        machineDB.setMaxQueueSize(machine.getMaxQueueSize());
        machineDB.setUsedMemory(machine.getUsedMemory());
        return machineDB;
    }
    
    public MachineDB getMachineById(String id){
    	return getSqlSession().selectOne("getMachineById", id);
    }
}
