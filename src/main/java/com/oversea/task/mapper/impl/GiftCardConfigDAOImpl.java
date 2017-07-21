package com.oversea.task.mapper.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.oversea.task.domain.GiftCardConfig;
import com.oversea.task.mapper.BaseDao;
import com.oversea.task.mapper.GiftCardConfigDAO;

@Repository
public class GiftCardConfigDAOImpl extends BaseDao implements GiftCardConfigDAO {

	@Override
	public List<GiftCardConfig> getGiftCardConfig() {
		return getSqlSession().selectList("GiftCardConfigMapper.getGiftCardConfig");
	}
}
