package com.oversea.task.mapper.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Repository;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.oversea.task.domain.ProductAttributeEntity;
import com.oversea.task.domain.ProductEntity;
import com.oversea.task.mapper.DispatchManager;
import com.oversea.task.mapper.ProductAttributeEntityDAO;

@Repository
public class DispatchManagerImpl implements DispatchManager {
	
	@Resource
    private ProductAttributeEntityDAO productAttributeEntityDAO;
	
	private Gson gson = new GsonBuilder().disableHtmlEscaping().create();
	
	@Override
    public String getProductSkuStr(ProductEntity productEntity) {
        String productEntityCode = productEntity.getProductEntityCode();
        List<List<String>> skuList = new ArrayList<List<String>>();
        if (!ProductEntity.INVALID_SIGN.equals(productEntityCode)) {
            String[] productAttributeVals = productEntityCode.split("_");
            for (int i = 0; i < productAttributeVals.length; i++) {
                List<String> entityValList = new ArrayList<String>(2);
                ProductAttributeEntity attrEntity = productAttributeEntityDAO.getProductAttributeEntityById(Long.parseLong(productAttributeVals[i]));
                if (attrEntity != null) {
                    entityValList.add(attrEntity.getName());
                    entityValList.add(attrEntity.getValue());
                }
                skuList.add(entityValList);
            }
            return gson.toJson(skuList);
        }
        return null;
    }
}
