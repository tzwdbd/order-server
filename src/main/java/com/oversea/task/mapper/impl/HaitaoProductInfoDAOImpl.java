package com.oversea.task.mapper.impl;


import com.oversea.task.domain.HaitaoProductInfo;
import com.oversea.task.mapper.BaseDao;
import com.oversea.task.mapper.HaitaoProductInfoDAO;

import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class HaitaoProductInfoDAOImpl extends BaseDao implements HaitaoProductInfoDAO {

    @Override
    public List<HaitaoProductInfo> getHaitaoDailiProductInfo(Integer status, Integer pageSize) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("status", status);
        map.put("pageSize", pageSize);
        return getSqlSession().selectList("HaitaoProductInfo.getDetailProductInfoByStauts", map);
    }

    @Override
    public int updateHaitaoDailiProductInfoStatus(String itemId, Integer status) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("itemId", itemId);
        map.put("status", status);
        return getSqlSession().update("HaitaoProductInfo.updateHaitaoDailiProductInfoStatus", map);
    }

    @Override
    public int updateHaitaoDailiProductInfo(HaitaoProductInfo haitaoProductInfo) {
        return getSqlSession().update("HaitaoProductInfo.updateHaitaoDailiProductInfo", haitaoProductInfo);
    }

    @Override
    public int deleteDetailProductInfoByItemId(String itemId) {
        return getSqlSession().update("HaitaoProductInfo.deleteDetailProductInfoByItemId", itemId);
    }
    
    public int addHaitaoProductInfo(HaitaoProductInfo info){
    	return getSqlSession().insert("HaitaoProductInfo.addHaitaoProductInfo", info);
    }
    
    public List<HaitaoProductInfo> getHaitaoProductInfoByItemId(String itemId){
    	return getSqlSession().selectList("HaitaoProductInfo.getHaitaoProductInfoByItemId", itemId);
    }
}
