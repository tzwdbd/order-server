package com.oversea.task.mapper.impl;


import com.oversea.task.domain.HaitaoProductImg;
import com.oversea.task.mapper.BaseDao;
import com.oversea.task.mapper.HaitaoProductImgDAO;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class HaitaoProductImgDAOImpl extends BaseDao implements HaitaoProductImgDAO {

    @Override
    public List<HaitaoProductImg> getHaitaoProductImgByAsinCode(String asinCode, String itemId) {
        Map<String, Object> map = new HashMap<String, Object>(1);
        map.put("asinCode", asinCode);
        map.put("itemId", itemId);
        return getSqlSession().selectList("HaitaoProductImg.getHaitaoProductImgByAsinCode", map);
    }

    @Override
    public void batchAddHaitaoProductImg(List<HaitaoProductImg> haitaoProductImgList) {
        super.batch(haitaoProductImgList, "HaitaoProductImg.addHaitaoProductImg", false);
    }

    @Override
    public int deleteProductImgByItemId(String itemId) {
        return getSqlSession().update("HaitaoProductImg.deleteProductImgByItemId", itemId);
    }
}
