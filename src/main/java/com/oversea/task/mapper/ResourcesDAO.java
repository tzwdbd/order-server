package com.oversea.task.mapper;

import java.util.List;
import java.util.Map;

import com.oversea.task.domain.Resources;

public interface ResourcesDAO {
	
	public void updateResourcesByDynamic(Resources resources);
	
	public Resources getResourcesByName(String name);
	
	public List<Resources> getSaleResourceByType(String type);
	
	public List<Resources> getSiteResourcesByTime(String time);
	
}