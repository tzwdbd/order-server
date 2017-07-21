package com.oversea.task.machine;


import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;

import com.haihu.rpc.common.HeartBeatListener;
import com.haihu.rpc.common.SpringObjectFactory;
import com.haihu.rpc.server.RpcNettyServer;
import com.oversea.task.mapper.MachineDAO;
import com.oversea.task.obj.Machine;
import com.oversea.task.util.StringUtil;

public class MachineStatusProcessor implements HeartBeatListener,InitializingBean{
	private Log log = LogFactory.getLog(getClass());
	@Resource
	private MachineDAO machineDAO;

	@Override
	public void channelDisconnect(String ip,String heartBeatRequestId) {
		// TODO Auto-generated method stub
		log.error("channelDisconnect.ip = "+ip+"&& heartBeatRequestId="+heartBeatRequestId);
		if(StringUtil.isNotEmpty(heartBeatRequestId)){
			Machine machine = new Machine();
			machine.setId(heartBeatRequestId);
			machineDAO.removeMachine(machine);
		}
	}

	@Override
	public void channelConnect(String version, String ip,String heartBeatRequestId) {
		// TODO Auto-generated method stub
//		log.error("channelConnect.version = "+version+"&& ip="+ip+"&& heartBeatRequestId="+heartBeatRequestId);
		if(machineDAO.getMachineById(heartBeatRequestId) == null){
			Machine machine = new Machine();
			machine.setId(heartBeatRequestId);
			machine.setClientjarVersion(version);
			machine.setMachineName(ip);
			machine.setChannelGroup(ip);
			machineDAO.addMachine(machine);
		}
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub
		machineDAO.removeAllMachine();
        RpcNettyServer rpcNettyServer = (RpcNettyServer)SpringObjectFactory.getInstance("rpcNettyServer");
        rpcNettyServer.setHeartBeatListener(this);
	}
}
