package com.oversea.task.mapper;

import com.oversea.task.domain.BrushOrderDetail;

import java.util.Date;
import java.util.List;
public interface BrushOrderDetailDAO {
	public void addBrushOrderDetail(BrushOrderDetail brushOrderDetail);
	public void updateBrushOrderDetailById(BrushOrderDetail brushOrderDetail);
	public void updateBrushOrderDetailByDynamic(BrushOrderDetail brushOrderDetail);
	public BrushOrderDetail getBrushOrderDetailById(Long id);
	public int countBrushOrderDetailById(Long id);
	
	public BrushOrderDetail getBrushOrderDetailByDate(Date dispatchTime);
	public void updateStatus(Long id,Integer status);
	
	public List<BrushOrderDetail> getBrushOrderDetailListBystatus(String status);
	
	public List<BrushOrderDetail> getBrushOrderDetailListByExpressstatus(String status);
	
	public List<BrushOrderDetail> getBrushOrderDetailListByThree(String status);
	
	public List<BrushOrderDetail> getBrushOrderDetailListByExpressNo(List<String> expressNos);
}