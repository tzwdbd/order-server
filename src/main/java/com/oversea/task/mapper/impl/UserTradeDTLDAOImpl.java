package com.oversea.task.mapper.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.oversea.task.domain.UserTradeDTL;
import com.oversea.task.mapper.BaseDao;
import com.oversea.task.mapper.UserTradeDTLDAO;

@Repository
public class UserTradeDTLDAOImpl extends BaseDao implements UserTradeDTLDAO {


    @Override
    public void updateUserTradeDTLById(UserTradeDTL userTradeDTL) {
        getSqlSession().update("UserTradeDTLSQL.updateUserTradeDTLById", userTradeDTL);
    }

    @Override
    public UserTradeDTL getUserTradeDTLById(Long id) {
        return getSqlSession().selectOne("UserTradeDTLSQL.getUserTradeDTLById", id);
    }

    @Override
    public int countUserTradeDTLById(Long id) {
        return (Integer) getSqlSession().selectOne("UserTradeDTLSQL.countUserTradeDTLById", id);
    }

    @Override
    public List<UserTradeDTL> getUserTradeDTLByUserIdAndOrderNo(Long userId, String orderNo) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("userId", userId);
        param.put("orderNo", orderNo);
        return getSqlSession().selectList("UserTradeDTLSQL.getUserTradeDTLByUserIdAndOrderNo", param);
    }

    @Override
    public List<UserTradeDTL> getUserTradeDTLByOrderNo(String orderNo) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("orderNo", orderNo);
        return getSqlSession().selectList("UserTradeDTLSQL.getUserTradeDTLByOrderNo", param);
    }

    @Override
    public UserTradeDTL getUserTradeDTLByUserIdAndOrderNoAndProductEntityId(Long userId, String orderNo, Long productEntityId) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("userId", userId);
        param.put("orderNo", orderNo);
        param.put("productEntityId", productEntityId);
        return getSqlSession().selectOne("UserTradeDTLSQL.getUserTradeDTLByUserIdAndOrderNoAndProductEntityId", param);
    }

    @Override
    public void updateUserTradeDTLUrlById(Long id, String productRebateUrl) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("id", id);
        param.put("productRebateUrl", productRebateUrl);
        getSqlSession().update("UserTradeDTLSQL.updateUserTradeDTLUrlById", param);
    }

    @Override
    public List<UserTradeDTL> getUserTradeDTLByUserIdAndProductIdAndGmtCreate(Long userId, Long productId) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("userId", userId);
        param.put("productId", productId);
        return getSqlSession().selectList("UserTradeDTLSQL.getUserTradeDTLByUserIdAndProductIdAndGmtCreate", param);
    }

    @Override
    public int updateUserTradeDtlPushStatus(Long id, int pushStatus) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("id", id);
        param.put("pushStatus", pushStatus);
        return getSqlSession().update("UserTradeDTLSQL.updateUserTradeDtlPushStatus", param);
    }

    @Override
    public int partialRefund(Long id, int refundStatus) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("id", id);
        param.put("refundStatus", refundStatus);
        return getSqlSession().update("UserTradeDTLSQL.partialRefund", param);
    }

    @Override
    public int updateUserTradeDtlStatus(Long id, Integer fromStatus, Integer toStatus) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("id", id);
        param.put("fromStatus", fromStatus);
        param.put("toStatus", toStatus);
        return getSqlSession().update("UserTradeDTLSQL.updateUserTradeDtlStatus", param);
    }

    @Override
    public List<UserTradeDTL> getUserTradeDtlByStatus(int status, int num) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("status", status);
        param.put("num", num);
        return getSqlSession().selectList("UserTradeDTLSQL.getUserTradeDtlByStatus", param);
    }

    @Override
    public int updateUserTradeDtlStatusAndRemark(Long id, Integer fromStatus, Integer toStatus, String expressDesc) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("id", id);
        param.put("fromStatus", fromStatus);
        param.put("toStatus", toStatus);
        param.put("expressDesc", expressDesc);
        return getSqlSession().update("UserTradeDTLSQL.updateUserTradeDtlStatusAndRemark", param);
    }

    @Override
    public List<UserTradeDTL> getUserTradeDtlByPayNo(String payNo) {
        return getSqlSession().selectList("UserTradeDTLSQL.getUserTradeDtlByPayNo", payNo);
    }

    @Override
    public int updateUserTradeDtlStockStatus(Long id, Integer stockStatus) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("id", id);
        param.put("stockStatus", stockStatus);
        return getSqlSession().update("UserTradeDTLSQL.updateUserTradeDtlStockStatus", param);
    }

    @Override
    public List<Map<String, Object>> getTopProductByDate(Map<String, Object> map) {
        // TODO Auto-generated method stub
        return getSqlSession().selectList("UserTradeDTLSQL.getTopProductByDate", map);
    }

    @Override
    public UserTradeDTL getUserTradeDTLByOrderNoAndProductEntityId(
            String orderNo, Long productEntityId) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("orderNo", orderNo);
        param.put("productEntityId", productEntityId);
        return getSqlSession().selectOne("UserTradeDTLSQL.getUserTradeDTLByOrderNoAndProductEntityId", param);
    }

    @Override
    public int countUserTradeDtlByOrderNoAndStatus(String orderNo, Integer status) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("orderNo", orderNo);
        param.put("status", status);
        return getSqlSession().selectOne("UserTradeDTLSQL.countUserTradeDtlByOrderNoAndStatus", param);
    }

    @Override
    public List<UserTradeDTL> getUserTradeDtlByStatusAndDispatchType(int mallId, int status, String dispatchType, int num) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("status", status);
        param.put("mallId", mallId);
        param.put("dispatchType", dispatchType);
        param.put("num", num);
        return getSqlSession().selectList("UserTradeDTLSQL.getUserTradeDtlByStatusAndDispatchType", param);
    }

	@Override
	public int updateUserTradeDtlToFrozen(Long id, Integer fromStatus,
			Integer toStatus, Integer frozenStatus) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("id", id);
        param.put("fromStatus", fromStatus);
        param.put("toStatus", toStatus);
        param.put("frozenStatus", frozenStatus);
        return getSqlSession().update("UserTradeDTLSQL.updateUserTradeDtlToFrozen", param);
	}
	
    @Override
    public int updateUserTradeDtlStatusByPayNo(String payNo, Integer fromStatus, Integer toStatus) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("payNo", payNo);
        param.put("fromStatus", fromStatus);
        param.put("toStatus", toStatus);
        return getSqlSession().update("UserTradeDTLSQL.updateUserTradeDtlStatusByPayNo", param);
    }
    
    @Override
    public int updateUserTradeDtlSaleCouponById(Long id,Long saleCouponId,Integer saleCouponPrice) {
        Map<String, Object> param = new HashMap<String, Object>();
        param.put("id", id);
        param.put("saleCouponId", saleCouponId);
        param.put("saleCouponPrice", saleCouponPrice);
        return getSqlSession().update("UserTradeDTLSQL.updateUserTradeDtlSaleCouponById", param);
    }

	@Override
	public int updateUserTradeDtlAfterSaleTypeById(Long id, Integer fromStatus, Integer toStatus) {
		Map<String, Object> param = new HashMap<String, Object>();
        param.put("fromStatus", fromStatus);
        param.put("toStatus", toStatus);
        param.put("id", id);
        return getSqlSession().update("UserTradeDTLSQL.updateUserTradeDtlAfterSaleTypeById", param);
	}
	
	@Override
	public int updateUserTradeDtlAfterSaleTypeByOrderNo(String orderNo,Integer fromStatus,Integer toStatus) {
		Map<String, Object> param = new HashMap<String, Object>();
        param.put("fromStatus", fromStatus);
        param.put("toStatus", toStatus);
        param.put("orderNo", orderNo);
        return getSqlSession().update("UserTradeDTLSQL.updateUserTradeDtlAfterSaleTypeByOrderNo", param);
	}

	@Override
	public Date getPayTime(String payNo) {
		Map<String, Object> param = new HashMap<String, Object>();
        param.put("payNo", payNo);
        return getSqlSession().selectOne("UserTradeDTLSQL.getPayTime", param);
	}
    
}
