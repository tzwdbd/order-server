package com.oversea.task.mapper;

import java.util.Date;

import com.oversea.task.domain.TradeExpressStatus;

public interface TradeExpressStatusDAO {
	public int addTradeExpressStatus(TradeExpressStatus tradeExpressStatus);
	
	public int getCountOfTradeExpressStatusByExpressNoAndOccurTime(String orderNo,String expressNo, Date occurTime);
}
