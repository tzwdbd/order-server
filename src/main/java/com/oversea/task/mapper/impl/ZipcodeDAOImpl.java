package com.oversea.task.mapper.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.oversea.task.domain.Zipcode;
import com.oversea.task.mapper.BaseDao;
import com.oversea.task.mapper.ZipcodeDAO;

@Repository("zipcodeDAO")
public class ZipcodeDAOImpl extends BaseDao implements ZipcodeDAO {

	private static final String namespace = "ZipcodeSQL";

	public int addZipcode(Zipcode zipcode) {
		return getSqlSession().insert(namespace+".addZipcode", zipcode);
	}

	public int updateZipcode(Zipcode zipcode) {
		return getSqlSession().update(namespace+".updateZipcode", zipcode);
	}

	public List<Zipcode> getZipcodeByCondition(Zipcode zipcode) {
		return getSqlSession().selectList(namespace+".getZipcodeByCondition", zipcode);
	}

	public int countZipcodeByCondition(Zipcode zipcode) {
		return (Integer)getSqlSession().selectOne(namespace+".countZipcodeByCondition", zipcode);
	}

	@Override
	public List<Zipcode> getZipcodeByStateCityDistrict(String state, String city, String district) {
		if(state!=null){
			state = state.trim() ;
		}
		if(city!=null){
			city = city.trim() ;
		}
		if(district!=null){
			district = district.trim() ;
		}
		Map<String,Object> map = new HashMap<String,Object>() ;
		map.put("state", state) ;
		map.put("city", city) ;
		map.put("district", district) ;
		return getSqlSession().selectList(namespace+".getZipcodeByStateCityDistrict", map);
	}

}