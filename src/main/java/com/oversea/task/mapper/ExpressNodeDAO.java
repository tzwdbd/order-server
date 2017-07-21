package com.oversea.task.mapper;

import java.util.Date;
import java.util.List;

import com.oversea.task.domain.ExpressNode;

public interface ExpressNodeDAO {
	
	
	public int addExpressNode(ExpressNode expressNode);
	
	public List<ExpressNode> getExpressNodeByOccurTime(String expressNo,Date date);
	
}
