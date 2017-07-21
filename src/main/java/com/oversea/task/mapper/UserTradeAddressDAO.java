package com.oversea.task.mapper;

import java.util.List;

import com.oversea.task.domain.UserTradeAddress;

public interface UserTradeAddressDAO {
	
	public void addUserTradeAddress(UserTradeAddress userTradeAddress);
	
	public UserTradeAddress getUserTradeAddressByUserIdAndPayNo(Long userId, String payNo);
	
	public List<UserTradeAddress> getUserTradeAddressByUserId(Long userId);
	
	public List<UserTradeAddress> getUserTradeAddressByIdCard(String idCard);
	
	public List<UserTradeAddress> getUserTradeAddressByMobile(String mobile);
	
	public void updateUserTradeAddressByUserIdAndPayNo(Long userId,String payNo,UserTradeAddress userTradeAddress);

	public List<UserTradeAddress> getUserTradeAddressByPayNoList(String payNoList);

	public void updateUserTradeAddressIdCardPhotoByPayNo(String payNo, String cardFront, String cardBack);

	public void updateUserTradeAddressIdCardPhotoByPayNoList(UserTradeAddress userTradeAddress);
}
