package com.oversea.task.update;

import java.io.File;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.oversea.task.util.MD5Util;

@Component
public class ClientJarVersionProcessor extends Thread{
	private Logger log = Logger.getLogger(getClass());
	private volatile String clientJarVersion ;
	
	@Value("${client.jar.folder.path}")
	private String clientJarFilePath ;
	
	@Value("${client.jar.name}")
    private String clientName;
	
	public ClientJarVersionProcessor(){
		this.start();
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		while(true){
			try {
	            clientJarVersion = MD5Util.calMD5(new File(clientJarFilePath+clientName));
	        } catch (Exception e) {
	        	log.error(e);
	        }
			try {
				Thread.sleep(6500);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public String getClientJarVersion() {
		return clientJarVersion;
	}

	public void setClientJarVersion(String clientJarVersion) {
		this.clientJarVersion = clientJarVersion;
	}

	public String getClientJarFilePath() {
		return clientJarFilePath;
	}

	public void setClientJarFilePath(String clientJarFilePath) {
		this.clientJarFilePath = clientJarFilePath;
	}
}
