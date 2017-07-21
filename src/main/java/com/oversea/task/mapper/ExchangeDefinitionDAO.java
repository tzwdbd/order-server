package com.oversea.task.mapper;

import com.oversea.task.domain.ExchangeDefinition;

public interface ExchangeDefinitionDAO {

	/**
	 * 单位都是分
	 * @param units:$、円、
	 * @return
	 */
	ExchangeDefinition getExchangeDefinitionByUnits(String units);

    /**
     * 更新汇率
     * @param units    货币单位
     * @param fromRmb
     * @param toRmb
     * @return
     */
    public int updateExchangeDefinitionByUnits(String units,Integer fromRmb,Integer toRmb);

}
