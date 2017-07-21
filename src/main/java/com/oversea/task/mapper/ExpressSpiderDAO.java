package com.oversea.task.mapper;

import java.util.List;

import com.oversea.task.domain.ExpressSpider;

public interface ExpressSpiderDAO {
	
	public long addExpressSpider(ExpressSpider item);
	
	public List<ExpressSpider> getExpressSpiderByStatus(Integer status);
	
	public List<ExpressSpider> getExpressSpiderByExpressNo(String expressNo);
	
	public void updateExpressSpider(ExpressSpider item);

}
