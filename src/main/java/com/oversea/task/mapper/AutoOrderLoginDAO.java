package com.oversea.task.mapper;

import java.util.List;

import com.oversea.task.domain.AutoOrderLogin;
public interface AutoOrderLoginDAO {
	public void addAutoOrderLogin(AutoOrderLogin autoOrderLogin);
	public void updateAutoOrderLoginById(AutoOrderLogin autoOrderLogin);
	public void updateAutoOrderLoginByDynamic(AutoOrderLogin autoOrderLogin);
	public List<AutoOrderLogin> getAutoOrderLoginById(Long id);
	public int countAutoOrderLoginById(Long id);
	
	public AutoOrderLogin getOrderLoginBySiteName(String siteName);
}