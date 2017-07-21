package com.oversea.task.mapper;

import java.util.Date;
import java.util.List;
import java.util.Map;

import com.oversea.task.domain.UserTradeDTL;

public interface UserTradeDTLDAO {

    public void updateUserTradeDTLById(UserTradeDTL userTradeDTL);

    public UserTradeDTL getUserTradeDTLById(Long id);

    public int countUserTradeDTLById(Long id);

    /**
     * 查询订单中所有商品
     *
     * @param userId
     * @param orderNo
     * @return
     */
    public List<UserTradeDTL> getUserTradeDTLByUserIdAndOrderNo(Long userId, String orderNo);

    /**
     * 查询订单中制定的商品
     *
     * @param userId
     * @param orderNo
     * @param productEntityId
     * @return
     */
    public UserTradeDTL getUserTradeDTLByUserIdAndOrderNoAndProductEntityId(Long userId, String orderNo, Long productEntityId);

    public UserTradeDTL getUserTradeDTLByOrderNoAndProductEntityId(String orderNo, Long productEntityId);

    /**
     * 根据订单号查询用户详细订单
     *
     * @param orderNo
     * @return
     */
    public List<UserTradeDTL> getUserTradeDTLByOrderNo(String orderNo);

    /**
     * 更新订单明细的返利链接
     *
     * @param id
     * @param productRebateUrl
     */
    public void updateUserTradeDTLUrlById(Long id, String productRebateUrl);

    /**
     * 用户今天已经购买过指定商品
     *
     * @param userId
     * @param productId
     * @return
     */
    public List<UserTradeDTL> getUserTradeDTLByUserIdAndProductIdAndGmtCreate(Long userId, Long productId);

    /**
     * 更新推送状态
     *
     * @param id
     * @param pushStatus
     */
    public int updateUserTradeDtlPushStatus(Long id, int pushStatus);


    public int partialRefund(Long id, int refundStatus);

    /**
     * 更新订单明细状态
     *
     * @param id
     * @param fromStatus
     * @param toStatus
     * @return
     */
    public int updateUserTradeDtlStatus(Long id, Integer fromStatus, Integer toStatus);

    /**
     * 用户
     *
     * @param status
     * @param num
     * @return
     */
    public List<UserTradeDTL> getUserTradeDtlByStatus(int status, int num);

    /**
     * 更新订单明细状态和描述
     *
     * @param id
     * @param fromStatus
     * @param toStatus
     * @return
     */
    public int updateUserTradeDtlStatusAndRemark(Long id, Integer fromStatus, Integer toStatus, String expressDesc);

    /**
     * 根据payNo获取UserTradeDTL列表
     *
     * @param payNo
     * @return
     */
    public List<UserTradeDTL> getUserTradeDtlByPayNo(String payNo);

    /**
     * 修改用户状态,是否是消耗囤货商品
     *
     * @param id
     * @param stockStatus
     * @return
     */
    public int updateUserTradeDtlStockStatus(Long id, Integer stockStatus);

    public List<Map<String, Object>> getTopProductByDate(Map<String, Object> map);

    public int countUserTradeDtlByOrderNoAndStatus(String orderNo, Integer status);

    public List<UserTradeDTL> getUserTradeDtlByStatusAndDispatchType(int mallId, int status, String dispatchType, int num);
    
    
    public int updateUserTradeDtlToFrozen(Long id, Integer fromStatus, Integer toStatus, Integer frozenStatus);

    public int updateUserTradeDtlStatusByPayNo(String payNo, Integer fromStatus, Integer toStatus);
    
    /**
     * 领取补偿红包后，将子订单的红包信息修改
     * @param id
     * @param saleCouponId
     * @param saleCouponPrice
     * @return
     */
    public int updateUserTradeDtlSaleCouponById(Long id,Long saleCouponId,Integer saleCouponPrice) ;
    
    /**
     * 更改子订单售后服务状态
     * @param id
     * @param fromStatus
     * @param toStatus
     * @return
     */
    public int updateUserTradeDtlAfterSaleTypeById(Long id,Integer fromStatus,Integer toStatus);
    
    /**
     * 根据订单号批量修改子订单售后服务状态
     * @param orderNo
     * @param fromStatus
     * @param toStatus
     * @return
     */
    public int updateUserTradeDtlAfterSaleTypeByOrderNo(String orderNo,Integer fromStatus,Integer toStatus);
    
    public Date getPayTime(String payNo);
    
}