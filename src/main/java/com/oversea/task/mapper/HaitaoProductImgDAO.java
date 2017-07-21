package com.oversea.task.mapper;

import com.oversea.task.domain.HaitaoProductImg;

import java.util.List;

/**
 * @author fengjian
 * @version V1.0
 * @title: sea-online
 * @Package com.oversea.task.mapper
 * @Description:
 * @date 15/12/17 15:34
 */
public interface HaitaoProductImgDAO {

    /**
     * 获取asinCode
     *
     * @param asinCode
     * @param itemId
     * @return
     */
    public List<HaitaoProductImg> getHaitaoProductImgByAsinCode(String asinCode, String itemId);

    /**
     * 批量新增
     *
     * @param haitaoProductImgList
     */
    public void batchAddHaitaoProductImg(List<HaitaoProductImg> haitaoProductImgList);

    /**
     * 删除
     *
     * @param itemId
     * @return
     */
    public int deleteProductImgByItemId(String itemId);
}
