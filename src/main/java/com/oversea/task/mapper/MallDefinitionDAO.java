package com.oversea.task.mapper;

import com.oversea.task.domain.MallDefinition;

public interface MallDefinitionDAO {
	public MallDefinition getMallDefinitionByName(String name);
}