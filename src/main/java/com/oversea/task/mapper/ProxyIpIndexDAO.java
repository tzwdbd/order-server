package com.oversea.task.mapper;

import java.util.List;

import com.oversea.task.domain.ProxyIpIndex;

public interface ProxyIpIndexDAO {

	int addProxyIpIndex(ProxyIpIndex record);

	int update(ProxyIpIndex record);

	List<ProxyIpIndex> getUnfinishRecord();

}