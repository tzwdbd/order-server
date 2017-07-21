package com.oversea.task.mapper.impl;

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


}
 