package com.oversea.task.mapper.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.oversea.task.domain.RobotOrderDetail;
import com.oversea.task.domain.TransferClearInfo;
import com.oversea.task.mapper.BaseDao;
import com.oversea.task.mapper.RobotOrderDetailDAO;
import com.oversea.task.util.DateUtil;

/**
 * @author fengjian
 * @version V1.0
 * @title: sea-online
 * @Package com.oversea.task.mapper.impl
 * @Description:
 * @date 15/11/25 19:34
 */
@Repository
public class RobotOrderDetailDAOImpl extends BaseDao implements RobotOrderDetailDAO {

    @Override
    public List<RobotOrderDetail> findOrderDetailForAutoOrder(Integer status) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("status", status);
        map.put("isManual", "NO");
        return getSqlSession().selectList("findOrderDetailForAutoOrder", map);
    }

    @Override
    public int countOrderDetailForAutoOrderByAccountId(String orderNo, Integer accountId, Long id) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("accountId", accountId);
        map.put("orderNo", orderNo);
        map.put("id", id);
        return getSqlSession().selectOne("countOrderDetailForAutoOrderByAccountId", map);
    }

    @Override
    public List<Long> findOrderDetailAccountIdForExpress(String status) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("status", status);
        map.put("dateDiff", DateUtil.ymdhmsFormat(DateUtil.increaseDate(new Date(), -1)));
        return getSqlSession().selectList("findOrderDetailAccountIdForExpress", map);
    }
    @Override
    
    public List<Long> findOrderDetailAccountIdForExpressNode(){
    	return getSqlSession().selectList("findOrderDetailAccountIdForExpressNode");
    }

    @Override
    public List<RobotOrderDetail> findOrderDetailByAccountId(Long accountId, String status) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("accountId", accountId);
        map.put("status", status);
        return getSqlSession().selectList("findOrderDetailByAccountId", map);
    }

    @Override
    public void updateRobotOrderDetailStatusById(Integer status, Long id) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("status", status);
        map.put("id", id);
        getSqlSession().update("updateRobotOrderDetailStatusById", map);
    }

    @Override
    public void updateRobotOrderDetail(RobotOrderDetail robotOrderDetail) {
        getSqlSession().update("updateRobotOrderDetail", robotOrderDetail);
    }

    @Override
    public RobotOrderDetail getRobotOrderDetailById(Long id) {
        return getSqlSession().selectOne("getRobotOrderDetailById", id);
    }

    @Override
    public List<RobotOrderDetail> getRobotOrderDetailsByStatusAndAccountId(Integer accountId, Integer status) {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("status", status);
        map.put("accountId", accountId);
        return getSqlSession().selectList("getRobotOrderDetailsByStatusAndAccountId", map);
    }

    @Override
    public String getExternalProductEntityId(Long id) {
        return (String) getSqlSession().selectOne("getExternalProductEntityId", id);
    }
    
    @Override
    public List<RobotOrderDetail> getRobotOrderDetailByOrderNo(String orderNo){
    	return getSqlSession().selectList("getRobotOrderDetailByOrderNo", orderNo);
    }
    
    @Override
	public int addRobotOrderDetail(RobotOrderDetail robotOrderDetail){
		return getSqlSession().insert("addRobotOrderDetail", robotOrderDetail);
	}

	@Override
	public TransferClearInfo getExpressAddress(Long companyId) {
		return getSqlSession().selectOne("getExpressAddress", companyId);
	}
	@Override
	public List<RobotOrderDetail> findOrderDetailsForSpiderExpress(String status){
		Map<String, Object> map = new HashMap<String, Object>();
        map.put("status", status);
        return getSqlSession().selectList("findOrderDetailsForSpiderExpress", map);
	}
	@Override
	public List<RobotOrderDetail> findOrderDetailsForSpiderExpressDetail(){
        return getSqlSession().selectList("findOrderDetailsForSpiderExpressDetail");
	}
	
	public int updateRobotOrderDetailExpressStatusById(Integer status, Long id){
		Map<String, Object> map = new HashMap<String, Object>();
        map.put("expressStatus", status);
        map.put("id", id);
        return getSqlSession().update("updateRobotOrderDetailExpressStatusById", map);
	}

	@Override
	public List<RobotOrderDetail> findOrderDetailsByOrderTime(Date orderTime) {
		Map<String, Object> map = new HashMap<String, Object>();
        map.put("orderTime", orderTime);
        return getSqlSession().selectList("findOrderDetailsByOrderTime", map);
	}

	@Override
	public List<RobotOrderDetail> getOrderDetailByOrderNoGroupNumber(
			String orderNo, int groupNumber) {
		Map<String, Object> map = new HashMap<String, Object>();
        map.put("orderNo", orderNo);
        map.put("groupNumber", groupNumber);
        return getSqlSession().selectList("getOrderDetailByOrderNoGroupNumber", map);
	}

	@Override
	public List<RobotOrderDetail> getOrderDetailByMallStatus() {
        return getSqlSession().selectList("getOrderDetailByMallStatus");
	}
}
