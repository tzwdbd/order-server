package com.oversea.task.mapper.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.oversea.task.domain.ProxyIpIndex;
import com.oversea.task.mapper.BaseDao;
import com.oversea.task.mapper.ProxyIpIndexDAO;
@Repository
public class ProxyIpIndexDAOImpl extends BaseDao implements ProxyIpIndexDAO{

	@Override
	public int addProxyIpIndex(ProxyIpIndex record) {
		return getSqlSession().insert("ProxyIpIndexMapper.addProxyIpIndex", record);
	}

	@Override
	public int update(ProxyIpIndex record) {
		return getSqlSession().update("ProxyIpIndexMapper.updateProxyIpIndex", record);
	}

	@Override
	public List<ProxyIpIndex> getUnfinishRecord() {
		return getSqlSession().selectList("ProxyIpIndexMapper.getUnfinishIPRecord");
	}

}
