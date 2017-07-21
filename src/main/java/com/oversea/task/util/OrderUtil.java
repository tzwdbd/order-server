package com.oversea.task.util;

import java.util.ArrayList;
import java.util.List;
import com.oversea.task.domain.RobotOrderDetail;

public class OrderUtil {

	public static List<RobotOrderDetail> getRobotOrderDetailByOrderNo(List<List<RobotOrderDetail>> waittingForPlaceOrders, RobotOrderDetail orderDetail) {
        for (List<RobotOrderDetail> orderList : waittingForPlaceOrders) {
        	RobotOrderDetail temp = orderList.get(0);
            if (temp.getOrderNo().equalsIgnoreCase(orderDetail.getOrderNo()) && temp.getAccountId().equals(orderDetail.getAccountId())) {
                return orderList;
            }
        }
        return null;
    }
	
	public static List<List<RobotOrderDetail>> getRobotOrderDetailListGroupByAccountId(List<RobotOrderDetail> orderDetails){
		List<List<RobotOrderDetail>> waittingForPlaceOrders = new ArrayList<List<RobotOrderDetail>>();        
        for (RobotOrderDetail orderDetail : orderDetails) {
            List<RobotOrderDetail> orderList = OrderUtil.getRobotOrderDetailByOrderNo(waittingForPlaceOrders, orderDetail);
            if (orderList == null) {
                orderList = new ArrayList<RobotOrderDetail>();
                waittingForPlaceOrders.add(orderList);
            }
            orderList.add(orderDetail);
        }
        return waittingForPlaceOrders;
	}
}
