package com.oversea.task.mapper.impl;

import com.oversea.task.domain.HaitaoProductSeries;
import com.oversea.task.mapper.BaseDao;
import com.oversea.task.mapper.HaitaoProductSeriesDAO;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Repository
public class HaitaoProductSeriesDAOImpl extends BaseDao implements HaitaoProductSeriesDAO {

    @Override
    public List<HaitaoProductSeries> getHaitaoProductSeriesByItemId(String itemId) {
        Map<String, Object> map = new HashMap<String, Object>(1);
        map.put("itemId", itemId);
        return getSqlSession().selectList("HaitaoProductSeries.getHaitaoProductSeriesByItemId", map);
    }

    @Override
    public void batchAddHaitaoProductSeries(List<HaitaoProductSeries> haitaoProductSeries) {
        super.batch(haitaoProductSeries, "HaitaoProductSeries.addHaitaoProductSeries", false);
    }

    @Override
    public void deleteDetailProductSeriesByItemId(String itemId) {
        getSqlSession().delete("HaitaoProductSeries.deleteDetailProductSeriesByItemId", itemId);
    }
}
