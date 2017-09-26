package com.oversea.task.mapper;

import java.util.List;

import com.oversea.task.domain.ExternalOrderDetail;
public interface ExternalOrderDetailDAO {
	public void addExternalOrderDetail(ExternalOrderDetail externalOrderDetail);
	public void updateExternalOrderDetailById(ExternalOrderDetail externalOrderDetail);
	public void updateExternalOrderDetailByDynamic(ExternalOrderDetail externalOrderDetail);
	public ExternalOrderDetail getExternalOrderDetailById(Long id);
	public int countExternalOrderDetailById(Long id);
	
	public List<ExternalOrderDetail> getExternalOrderDetailByStatus(int status);
	
	public void updateStatus(int status,Long id);
	
	public List<ExternalOrderDetail> getExternalOrderDetailByOrderNo(String saleOrderCode);
	
	public List<ExternalOrderDetail> findExternalOrderDetailsForSpiderExpress(String status);
	
}