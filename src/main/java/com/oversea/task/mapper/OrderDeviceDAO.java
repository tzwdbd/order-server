package com.oversea.task.mapper;

import com.oversea.task.domain.OrderDevice;

public interface OrderDeviceDAO {

    /**
     * 查找订单
     * @param id
     * @return
     */
    public OrderDevice findById(long id);

    /**
     * 更新订单信息
     * @param id
     * @param status
     * @return
     */
    public int updateOrderDeviceStatus(long id, Integer status);
}
