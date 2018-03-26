package com.oversea.task.mapper;

import com.oversea.task.domain.RobotOrderDetail;
import com.oversea.task.domain.TransferClearInfo;

import java.util.Date;
import java.util.List;

public interface RobotOrderDetailDAO {

    /**
     * 获取全部订单
     *
     * @return
     */
    public List<RobotOrderDetail> findOrderDetailForAutoOrder(Integer status);

    /**
     * 根据账户查询这是今天第几单
     *
     * @param accountId
     * @return
     */
    public int countOrderDetailForAutoOrderByAccountId(String orderNo, Integer accountId, Long id);

    /**
     * 获取爬取物流
     *
     * @return
     */
    public List<Long> findOrderDetailAccountIdForExpress(String status);
    
    
    /**
     * 
     * @return
     */
    public List<Long> findOrderDetailAccountIdForExpressNode();
    

    /**
     * 查询accountId
     *
     * @param accountId
     * @return
     */
    public List<RobotOrderDetail> findOrderDetailByAccountId(Long accountId, String status);

    /**
     * 根据状态
     *
     * @param id
     */
    public void updateRobotOrderDetailStatusById(Integer status, Long id);

    /**
     * 根据id获取RobotOrderDetail
     *
     * @param id
     */
    public RobotOrderDetail getRobotOrderDetailById(Long id);

    /**
     * orderDetail 更新
     *
     * @param robotOrderDetail
     */
    public void updateRobotOrderDetail(RobotOrderDetail robotOrderDetail);

    /**
     * 根据状态获取相应的订单
     *
     * @param status
     * @return
     */
    public List<RobotOrderDetail> getRobotOrderDetailsByStatusAndAccountId(Integer accountId, Integer status);

    /**
     * 根据ID 获取商品的EntityCode
     * @param id
     * @return
     */
    public String getExternalProductEntityId(Long id);
    
    public List<RobotOrderDetail> getRobotOrderDetailByOrderNo(String orderNo);
    
    
    public int addRobotOrderDetail(RobotOrderDetail robotOrderDetail);
    
    public TransferClearInfo getExpressAddress(Long companyId);
    
    public List<RobotOrderDetail> findOrderDetailsForSpiderExpress(String status);
    
    public List<RobotOrderDetail> findOrderDetailsForSpiderExpressDetail();
    
    public int updateRobotOrderDetailExpressStatusById(Integer status, Long id);
    
    public List<RobotOrderDetail> findOrderDetailsByOrderTime(Date orderTime);
    public List<RobotOrderDetail> getOrderDetailByOrderNoGroupNumber(String orderNo,int groupNumber);
    
    public List<RobotOrderDetail> getOrderDetailByMallStatus();
    public List<RobotOrderDetail> getOrderDetailOrderByMallStatus();
    
    public RobotOrderDetail getOrderDetailBySiteName(String siteName);
    
    public List<RobotOrderDetail> getOrderDetailsForSpiderExpress(String status);
}
