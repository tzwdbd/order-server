package com.oversea.task.mapper;

import com.oversea.task.domain.ExchangeBankDefinition;
import java.util.List;
public interface ExchangeBankDefinitionDAO {
	public void addExchangeBankDefinition(ExchangeBankDefinition exchangeBankDefinition);
	public void updateExchangeBankDefinitionById(ExchangeBankDefinition exchangeBankDefinition);
	public void updateExchangeBankDefinitionByDynamic(ExchangeBankDefinition exchangeBankDefinition);
	public List<ExchangeBankDefinition> getExchangeBankDefinitionById(Long id);
	public int countExchangeBankDefinitionById(Long id);
	
	public ExchangeBankDefinition getExchangeBankDefinitionByUnit(String unit);
}