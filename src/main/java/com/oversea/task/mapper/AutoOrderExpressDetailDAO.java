package com.oversea.task.mapper;

import java.util.List;

import com.oversea.task.domain.AutoOrderExpressDetail;
public interface AutoOrderExpressDetailDAO {
	public void addAutoOrderExpressDetail(AutoOrderExpressDetail autoOrderExpressDetail);
	public void updateAutoOrderExpressDetailById(AutoOrderExpressDetail autoOrderExpressDetail);
	public void updateAutoOrderExpressDetailByDynamic(AutoOrderExpressDetail autoOrderExpressDetail);
	public List<AutoOrderExpressDetail> getAutoOrderExpressDetailById(Long id);
	public int countAutoOrderExpressDetailById(Long id);
	
	public AutoOrderExpressDetail getAutoOrderExpressDetailBySiteName(String siteName);
}