package com.oversea.task.mapper;

import com.oversea.task.domain.ProxyIpPool;

import java.util.List;

public interface ProxyIpPoolDAO {

    public int addProxyIp(ProxyIpPool record);

    public int update(ProxyIpPool record);

    public List<ProxyIpPool> findById(int id, int limit);

    public List<ProxyIpPool> findActiveIp(int oversea, int hours);

    public ProxyIpPool getById(Long id);

    public void deleteById(Long id);

    public int updateExpireIpStatus(int hours);

    public ProxyIpPool getProxyIPByIPAndPort(String ip, String port);

    public List<String> getProxyIP(int num, List<ProxyIpPool> pools);

}
