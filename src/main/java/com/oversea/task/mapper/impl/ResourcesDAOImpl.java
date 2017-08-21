package com.oversea.task.mapper.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.oversea.task.domain.Resources;
import com.oversea.task.mapper.BaseDao;
import com.oversea.task.mapper.ResourcesDAO;

@Repository("resourcesDAO")
public class ResourcesDAOImpl extends BaseDao implements ResourcesDAO {

	private static final String NAMESPACE = "ResourcesSQL.";
	
	@Override
	public void updateResourcesByDynamic(Resources resources) {
		getSqlSession().update(NAMESPACE + "updateResourcesByDynamic" , resources);
	}

	@Override
	public Resources getResourcesByName(String name) {
		return getSqlSession().selectOne(NAMESPACE + "getResourcesByName", name);
	}
	
	
	public Map<String, Resources> getSaleResourceByMap(String type) {
		List<Resources> list = getSqlSession().selectList(NAMESPACE + "getSaleResourceByType", type);
		Map<String, Resources> resourcesMap=new HashMap<String, Resources>();
		try {
			for (Resources resources : list) {
				String name=resources.getName();
				resourcesMap.put(name, resources);
			}	
		} catch (Exception e) {
			logger.error("ERROR IN ResourcesManagerImpl ===> getSaleResourceByMap",e);
		}
		return resourcesMap;
	}

	@Override
	public List<Resources> getSiteResourcesByTime(String time) {
		return getSqlSession().selectList(NAMESPACE + "getSiteResourcesByTime", time);
	}
}