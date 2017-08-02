package com.oversea.task.mapper.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.oversea.task.domain.OrderPayDetail;
import com.oversea.task.mapper.BaseDao;
import com.oversea.task.mapper.OrderPayDetailDAO;

/** 
* @author: yangyan 
  @Package: com.oversea.task.mapper.impl
  @Description:
* @time   2016年8月15日 下午6:08:12 
*/
@Repository
public class OrderPayDetailDAOImpl extends BaseDao implements OrderPayDetailDAO {

	@Override
	public void addOrderPayDetail(OrderPayDetail orderPayDetail) {
		getSqlSession().insert("OrderPayDetailMapper.addOrderPayDetail", orderPayDetail);
	}

	@Override
	public List<OrderPayDetail> getOrderPayDetailByAccountId(int accountId,
			Date startTime, Date endTime) {
		Map<String, Object> map = new HashMap<String, Object>();
        map.put("accountId", accountId);
        map.put("startTime", startTime);
        map.put("endTime", endTime);
		return getSqlSession().selectList("OrderPayDetailMapper.getOrderPayDetailByAccountId", map);
	}


}
 