package com.oversea.task.mapper.impl;

import java.util.List;

import com.oversea.task.mapper.BaseDao;
import org.springframework.stereotype.Repository;

import com.oversea.task.domain.HaitaoCompetitorRecord;
import com.oversea.task.mapper.HaitaoCompetitorRecordDAO;

@Repository
public class HaitaoCompetitorRecordDAOImpl extends BaseDao implements
		HaitaoCompetitorRecordDAO {
	public int addHaitaoCompetitorRecord(HaitaoCompetitorRecord record){
		return getSqlSession().insert("HaitaoCompetitorRecord.insert", record);
	}
	
	public int update(HaitaoCompetitorRecord record){
		return getSqlSession().update("HaitaoCompetitorRecord.update", record);
	}
	
	public List<HaitaoCompetitorRecord> getUnfinishRecord(){
		return getSqlSession().selectList("HaitaoCompetitorRecord.getUnfinishRecord");
	}
}
