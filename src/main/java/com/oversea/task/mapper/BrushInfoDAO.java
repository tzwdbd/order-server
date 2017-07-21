package com.oversea.task.mapper;

import com.oversea.task.domain.BrushInfo;
import java.util.List;
public interface BrushInfoDAO {
	public void addBrushInfo(BrushInfo brushInfo);
	public void updateBrushInfoById(BrushInfo brushInfo);
	public void updateBrushInfoByDynamic(BrushInfo brushInfo);
	public BrushInfo getBrushInfoById(Long id);
	public int countBrushInfoById(Long id);
}