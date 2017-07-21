package com.oversea.task.mapper;

import java.util.List;

import com.oversea.task.domain.Zipcode;

public interface ZipcodeDAO {

	public int addZipcode(Zipcode zipcode);

	public int updateZipcode(Zipcode zipcode);
	
	public List<Zipcode> getZipcodeByCondition(Zipcode zipcode);
	
	public int countZipcodeByCondition(Zipcode zipcode);
	
	public List<Zipcode> getZipcodeByStateCityDistrict(String state,String city,String district);

}