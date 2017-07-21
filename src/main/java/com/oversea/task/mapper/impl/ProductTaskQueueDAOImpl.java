package com.oversea.task.mapper.impl;

import com.oversea.task.mapper.BaseDao;
import org.springframework.stereotype.Repository;

import com.oversea.task.domain.HaitaoProductTask;
import com.oversea.task.mapper.ProductTaskQueueDAO;

@Repository
public class ProductTaskQueueDAOImpl extends BaseDao implements ProductTaskQueueDAO {
	public int addProductTaskQueue(HaitaoProductTask task){
		return getSqlSession().insert("HaitaoProductTask.insert", task);
	}
}
