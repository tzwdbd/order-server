package com.oversea.task.mapper;

import java.util.List;

import com.oversea.task.domain.AutoOrderScribeExpress;
public interface AutoOrderScribeExpressDAO {
	public void addAutoOrderScribeExpress(AutoOrderScribeExpress autoOrderScribeExpress);
	public void updateAutoOrderScribeExpressById(AutoOrderScribeExpress autoOrderScribeExpress);
	public void updateAutoOrderScribeExpressByDynamic(AutoOrderScribeExpress autoOrderScribeExpress);
	public List<AutoOrderScribeExpress> getAutoOrderScribeExpressById(Long id);
	public int countAutoOrderScribeExpressById(Long id);
	
	public AutoOrderScribeExpress getAutoOrderScribeExpressBySiteName(String siteName);
}