package com.oversea.task.mapper;

import java.util.List;

import com.oversea.task.domain.UserTradeExpress;

public interface UserTradeExpressDAO {

	public int insert(UserTradeExpress record);

	public List<UserTradeExpress> getUserTradeExpressByExpressCompanyAndMaxStatus(String expressCompany,
			Integer status);

	public int updateUserTradeExpressByDynamic(UserTradeExpress userTradeExpress);

	public UserTradeExpress getUserTradeExpressByExpressNo(String expressNo);

}