package com.oversea.task.mapper;

import com.oversea.task.domain.HaitaoProductInfo;

import java.util.List;

public interface HaitaoProductInfoDAO {

    /**
     * 获取productInfo
     *
     * @param status
     * @param pageSize
     * @return
     */
    public List<HaitaoProductInfo> getHaitaoDailiProductInfo(Integer status, Integer pageSize);

    /**
     * 更新状态
     *
     * @param itemId
     * @param status
     * @return
     */
    public int updateHaitaoDailiProductInfoStatus(String itemId, Integer status);

    /**
     * 更新商品信息
     *
     * @param haitaoProductInfo
     * @return
     */
    public int updateHaitaoDailiProductInfo(HaitaoProductInfo haitaoProductInfo);

    /**
     * 删除productInfo
     * @param itemId
     * @return
     */
    public int deleteDetailProductInfoByItemId(String itemId);
    
    /**
     * 增加
     * @param info
     * @return
     */
    public int addHaitaoProductInfo(HaitaoProductInfo info);
    
    /**
     * 
     * @param itemId
     * @return
     */
    public List<HaitaoProductInfo> getHaitaoProductInfoByItemId(String itemId);
}
