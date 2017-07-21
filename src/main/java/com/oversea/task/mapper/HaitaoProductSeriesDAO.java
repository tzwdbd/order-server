package com.oversea.task.mapper;

import com.oversea.task.domain.HaitaoProductSeries;

import java.util.List;

public interface HaitaoProductSeriesDAO {

    /**
     * 获取productInfo
     *
     * @param itemId
     * @return
     */
    public List<HaitaoProductSeries> getHaitaoProductSeriesByItemId(String itemId);

    /**
     * 批量新增
     *
     * @param haitaoProductSeries
     */
    public void batchAddHaitaoProductSeries(List<HaitaoProductSeries> haitaoProductSeries);


    /**
     * 删除
     * @param itemId
     */
    public void deleteDetailProductSeriesByItemId(String itemId);
}
