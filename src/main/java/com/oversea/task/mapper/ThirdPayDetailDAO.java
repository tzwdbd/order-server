package com.oversea.task.mapper;

import com.oversea.task.domain.ThirdPayDetail;
import java.util.List;
public interface ThirdPayDetailDAO {
	public void addThirdPayDetail(ThirdPayDetail thirdPayDetail);
	public void updateThirdPayDetailById(ThirdPayDetail thirdPayDetail);
	public void updateThirdPayDetailByDynamic(ThirdPayDetail thirdPayDetail);
	public List<ThirdPayDetail> getThirdPayDetailById(Long id);
	public int countThirdPayDetailById(Long id);
}