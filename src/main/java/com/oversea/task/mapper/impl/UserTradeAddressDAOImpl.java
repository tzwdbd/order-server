package com.oversea.task.mapper.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.oversea.task.domain.UserTradeAddress;
import com.oversea.task.mapper.BaseDao;
import com.oversea.task.mapper.UserTradeAddressDAO;

@Repository
public class UserTradeAddressDAOImpl extends BaseDao implements UserTradeAddressDAO {

	@Override
	public void addUserTradeAddress(UserTradeAddress userTradeAddress) {
		super.getSqlSession().insert("addUserTradeAddress", userTradeAddress);
	}

	@Override
	public UserTradeAddress getUserTradeAddressByUserIdAndPayNo(Long userId, String payNo) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("userId", userId);
		param.put("payNo", payNo);
		return getSqlSession().selectOne("getUserTradeAddressByUserIdAndPayNo", param);
	}

	@Override
	public List<UserTradeAddress> getUserTradeAddressByUserId(Long userId){
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("userId", userId);
		return getSqlSession().selectList("getUserTradeAddressByUserId", param);
	}

	@Override
	public void updateUserTradeAddressByUserIdAndPayNo(Long userId,String payNo,UserTradeAddress userTradeAddress) {
		userTradeAddress.setUserId(userId);
		userTradeAddress.setPayNo(payNo);
		getSqlSession().update("updateUserTradeAddressByUserIdAndPayNo", userTradeAddress);
	}

	@Override
	public List<UserTradeAddress> getUserTradeAddressByMobile(String mobile) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("mobile", mobile);
		return getSqlSession().selectList("getUserTradeAddressByMobile", param);
	}
	
	@Override
	public List<UserTradeAddress> getUserTradeAddressByIdCard(String idCard) {
		Map<String, Object> param = new HashMap<String, Object>();
		param.put("idCard", idCard);
		return getSqlSession().selectList("getUserTradeAddressByIdCard", param);
	}

	@Override
	public List<UserTradeAddress> getUserTradeAddressByPayNoList(String payNoList) {
		Map<String, String> param = new HashMap<String, String>();
		param.put("payNoList", payNoList);
		return getSqlSession().selectList("getUserTradeAddressByPayNoList", param);
	}

	@Override
	public void updateUserTradeAddressIdCardPhotoByPayNo(String payNo, String cardFront, String cardBack) {
		UserTradeAddress userTradeAddress = new UserTradeAddress();
		userTradeAddress.setPayNo(payNo);
		userTradeAddress.setIdCardFront(cardFront);
		userTradeAddress.setIdCardBack(cardBack);
		getSqlSession().update("updateUserTradeAddressIdCardPhotoByPayNo", userTradeAddress);
	}

	@Override
	public void updateUserTradeAddressIdCardPhotoByPayNoList(UserTradeAddress userTradeAddress) {
		getSqlSession().update("updateUserTradeAddressIdCardPhotoByPayNoList", userTradeAddress);
	}
}
