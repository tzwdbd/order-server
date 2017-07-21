package com.oversea.task.mapper;

import java.util.List;

import com.oversea.task.domain.HaitaoCompetitorRecord;

public interface HaitaoCompetitorRecordDAO {
	
	public int addHaitaoCompetitorRecord(HaitaoCompetitorRecord record);
	
	public int update(HaitaoCompetitorRecord record);
	
	public List<HaitaoCompetitorRecord> getUnfinishRecord();
}
