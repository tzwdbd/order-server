package com.oversea.task.mapper.impl;

import com.oversea.task.domain.ProxyIpPool;
import com.oversea.task.mapper.BaseDao;
import com.oversea.task.mapper.ProxyIpPoolDAO;
import com.oversea.task.util.DateUtil;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class ProxyIpPoolDAOImpl extends BaseDao implements ProxyIpPoolDAO {

    @Override
    public int addProxyIp(ProxyIpPool record) {
        return getSqlSession().insert("ProxyIpPoolMapper.addProxyIpPool", record);
    }

    @Override
    public int update(ProxyIpPool record) {
        return getSqlSession().update("ProxyIpPoolMapper.updateProxyIpPool", record);
    }

    @Override
    public List<ProxyIpPool> findById(int id, int limit) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("id", id);
        map.put("limit", limit);
        return getSqlSession().selectList("ProxyIpPoolMapper.findById", map);
    }

    @Override
    public List<ProxyIpPool> findActiveIp(int oversea, int hours) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("activeTime", DateUtil.increaseHourDate(new Date(), -hours));
        map.put("oversea", oversea);
        return getSqlSession().selectList("ProxyIpPoolMapper.findActiveIp", map);
    }

    @Override
    public ProxyIpPool getById(Long id) {
        return getSqlSession().selectOne("ProxyIpPoolMapper.getById", id);
    }

    @Override
    public void deleteById(Long id) {
        getSqlSession().delete("ProxyIpPoolMapper.deleteById", id);
    }

    @Override
    public ProxyIpPool getProxyIPByIPAndPort(String ip, String port) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("ip", ip);
        map.put("port", port);
        return getSqlSession().selectOne("ProxyIpPoolMapper.getProxyIPByIPAndPort", map);
    }

    @Override
    public int updateExpireIpStatus(int hours) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("expireTime", DateUtil.increaseHourDate(new Date(), -hours));
        return getSqlSession().update("ProxyIpPoolMapper.updateExpireIpStatus", map);
    }

    @Override
    public List<String> getProxyIP(int num, List<ProxyIpPool> pools) {
        List<String> list = new ArrayList<String>();
        if (pools != null && pools.size() > 0) {
            for (int i = 0; i < num; i++) {
                int randomIndex = new Random().nextInt(pools.size() - 1);
                ProxyIpPool pool = pools.get(randomIndex);
                list.add(pool.getIp() + ":" + pool.getPort());
            }
        }
        return list;
    }
}
