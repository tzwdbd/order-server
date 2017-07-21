package com.oversea.task.mapper.impl;

import com.oversea.task.domain.OrderDevice;
import com.oversea.task.mapper.BaseDao;
import com.oversea.task.mapper.OrderDeviceDAO;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

/**
 * @author fengjian
 * @version V1.0
 * @title: sea-online
 * @Package com.oversea.task.mapper.impl
 * @Description:
 * @date 15/11/25 19:32
 */
@Repository
public class OrderDeviceDAOImpl extends BaseDao implements OrderDeviceDAO {

    @Override
    public OrderDevice findById(long id) {
        return getSqlSession().selectOne("OrderDeviceMapper.findById", id);
    }

    @Override
    public int updateOrderDeviceStatus(long id, Integer status) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("id", id);
        map.put("status", status);
        return getSqlSession().update("OrderDeviceMapper.updateOrderDeviceStatus", map);
    }
}
