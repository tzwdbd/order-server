package com.haitao;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.oversea.rabbitmq.utils.StringUtil;
import com.oversea.task.domain.BrushOrderDetail;
import com.oversea.task.domain.ExchangeDefinition;
import com.oversea.task.domain.OrderAccount;
import com.oversea.task.domain.OrderCreditCard;
import com.oversea.task.domain.OrderDevice;
import com.oversea.task.domain.OrderPayAccount;
import com.oversea.task.domain.OrderPayDetail;
import com.oversea.task.domain.RobotOrderDetail;
import com.oversea.task.enums.AutoBuyStatus;
import com.oversea.task.enums.MoneyUnits;
import com.oversea.task.job.OrderServiceJob;
import com.oversea.task.job.ShipServiceJob;
import com.oversea.task.job.ShipServiceSingleJob;
import com.oversea.task.mapper.BrushOrderDetailDAO;
import com.oversea.task.mapper.ExchangeDefinitionDAO;
import com.oversea.task.mapper.MachineDAO;
import com.oversea.task.mapper.OrderAccountDAO;
import com.oversea.task.mapper.OrderCreditCardDAO;
import com.oversea.task.mapper.OrderDeviceDAO;
import com.oversea.task.mapper.OrderPayAccountDAO;
import com.oversea.task.mapper.OrderPayDetailDAO;
import com.oversea.task.mapper.RobotOrderDetailDAO;
import com.oversea.task.mapper.UserTradeDTLDAO;
import com.oversea.task.obj.Machine;
import com.oversea.task.util.MathUtil;

/**
 * @author fengjian
 * @version V1.0
 * @title: sea-online
 * @Package com.haitao
 * @Description:
 * @date 15/12/28 13:54
 */
//classpath*:/rpc/spring-server-bean.xml,classpath*:spring-task.xml,classpath*:spring-rabbitmq.xml,classpath*:spring-db.xml
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:/rpc/spring-server-bean.xml","classpath*:/*.xml", "classpath*:/server/*.xml"})
public class TestJunit {
	
//	@Resource
//	private RechargeBootstrasp rechargeBootstrasp;
	
	@Resource
	private RobotOrderDetailDAO robotOrderDetailDAO;
	
//	@Resource
//	private AutomanBootstrasp automanBootstrasp;
	
//	@Resource
//    private RabbitMqMessageSender rabbitMqMessageSender;
//	
//	@Resource GiftCardDAO giftCardDAO;
	
	@Resource
	ShipServiceJob shipServiceJob;
	
	@Resource
	OrderServiceJob orderServiceJob;
	
	@Resource
	MachineDAO machineDAO;
	@Resource
	OrderAccountDAO orderAccountDAO;
	@Resource
	OrderDeviceDAO orderDeviceDAO;
	@Resource
	OrderPayDetailDAO orderPayDetailDao;
	@Resource
	OrderCreditCardDAO orderCreditCardDAO;
	@Resource
	OrderPayAccountDAO orderPayAccountDAO;
	@Resource
	ExchangeDefinitionDAO exchangeDefinitionDAO;
	
	@Resource
	ShipServiceSingleJob shipServiceSingleJob;
	@Resource
	BrushOrderDetailDAO brushOrderDetailDAO;
	
	@Resource
	UserTradeDTLDAO userTradeDTLDAO;
	
	@Test
	public void testRechargeBootstrasp()throws Exception {
		//System.out.println(robotOrderDetailDAO.updateRobotOrderDetailExpressStatusById(15, 132358l));
//		List<RobotOrderDetail> orderDetails = robotOrderDetailDAO.findOrderDetailsForSpiderExpressDetail();
//		List<RobotOrderDetail> orderDetails = robotOrderDetailDAO.findOrderDetailForAutoOrder(AutoBuyStatus.AUTO_PAY_PARPARE.getValue());
//		System.out.println("size = "+orderDetails.size());
//		orderServiceJob.run();
//		shipServiceSingleJob.run();
//		List<RobotOrderDetail> orderDetails = robotOrderDetailDAO.findOrderDetailsForSpiderExpress("70");
//		System.out.println("size = "+orderDetails.size()+" status = "+orderDetails.get(0).getStatus());
//		try {
//			rechargeBootstrasp.process("");
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		List<RobotOrderDetail> list = robotOrderDetailDAO.getRobotOrderDetailByOrderNo("1606121616423633296152292");
//		System.out.println("size = "+list.size());
//		list.get(0).setOrderNo("1606121616423633296152293");
//		try{
//			int a = robotOrderDetailDAO.addRobotOrderDetail(list.get(0));
//			System.out.println("a = "+a);
//		}catch(Exception e){
//			e.printStackTrace();
//		}
//		
//		System.out.println("finish");
		
//		automanBootstrasp.process("");
//		rabbitMqMessageSender.deliver("hello", "hello", "hello world");
//		System.out.println("sina");
		
//		List<RobotOrderDetail> list = robotOrderDetailDAO.findOrderDetailForAutoOrder(1222);
//		RobotOrderDetail robotOrderDetail = new RobotOrderDetail();
//		robotOrderDetail.setId(12194l);
//		robotOrderDetail.setPromotionCodeListStatus("1,0,1");
//		robotOrderDetailDAO.updateRobotOrderDetail(robotOrderDetail);
//		GiftCard giftCard = new GiftCard();
//		giftCard.setId(22l);
//		giftCard.setIsSuspect("yes");
//		giftCardDAO.updateGiftCard(giftCard);
//		String scanStatus = AutoBuyStatus.AUTO_SCRIBE_ORDER_NOT_READY.getValue() +
//				"," + AutoBuyStatus.AUTO_PAY_SUCCESS.getValue()+ "," + AutoBuyStatus.AUTO_SCRIBE_NEED_SINGLE.getValue();
//		String scanStatus = String.valueOf(AutoBuyStatus.AUTO_SCRIBE_NEED_SINGLE.getValue());
//		
//		Map<Integer,List<RobotOrderDetail>> map = new HashMap<Integer,List<RobotOrderDetail>>();
//		List<RobotOrderDetail> list = robotOrderDetailDAO.findOrderDetailsForExpress(scanStatus);
//		for(RobotOrderDetail robotOrderDetail : list){
//			Integer accountId = robotOrderDetail.getAccountId();
//			List<RobotOrderDetail> array = map.get(accountId);
//			if(array == null){
//				array = new ArrayList<RobotOrderDetail>();
//				map.put(accountId, array);
//			}
//			array.add(robotOrderDetail);
//		}
//		shipServiceJob.run();
		
//		System.out.println(map.size());
//		System.out.println(list.size());
//		RobotOrderDetail robotOrderDetail = robotOrderDetailDAO.getRobotOrderDetailById(40L);
//		List<RobotOrderDetail> robotOrderDetailList = new ArrayList<RobotOrderDetail>();
//		robotOrderDetailList.add(robotOrderDetail);
//		addOrderPayDetailLog(robotOrderDetailList);
//		System.out.println(robotOrderDetail.getOrderNo());
//		BrushOrderDetail brushOrderDetail = brushOrderDetailDAO.getBrushOrderDetailByDate(new Date());
//		System.out.println(brushOrderDetail.getId());
//		List<BrushOrderDetail> brushOrderDetails = brushOrderDetailDAO.getBrushOrderDetailListByThree("1000,99");
//    	for(BrushOrderDetail brushOrderDetail : brushOrderDetails){
//    		List<RobotOrderDetail> robotOrderDetails = robotOrderDetailDAO.findOrderDetailsByOrderTime(brushOrderDetail.getOrderTime());
//    		if(robotOrderDetails!=null && robotOrderDetails.size()>0){
//    			List<String> expressNos = new ArrayList<String>();
//    			for(RobotOrderDetail r:robotOrderDetails){
//    				expressNos.add(r.getExpressNo());
//    			}
//    			
//    			List<BrushOrderDetail> expressList = brushOrderDetailDAO.getBrushOrderDetailListByExpressNo(expressNos);
//    			List<String> bexpressNos = new ArrayList<String>();
//    			for(BrushOrderDetail b:expressList){
//    				bexpressNos.add(b.getExpressNo());
//    			}
//    			expressNos.removeAll(bexpressNos);
//    			if(expressNos!=null && expressNos.size()>0){
//		    		brushOrderDetail.setExpressNo(expressNos.get(0));
//		    		brushOrderDetail.setExpressStatus(14);
//		    		if(!StringUtil.isBlank(brushOrderDetail.getReviewContent())){
//		    			brushOrderDetail.setExpressStatus(5);
//		        	}
//		    		brushOrderDetail.setStatus(100);
//		    		brushOrderDetailDAO.updateBrushOrderDetailByDynamic(brushOrderDetail);
//    			}
//    		}
//    	}
		System.out.println(userTradeDTLDAO.getPayTime("15072010190500P076"));
	}
	
	private void addOrderPayDetailLog(List<RobotOrderDetail> orderDetailList) {
		for(RobotOrderDetail orderDetail:orderDetailList){
			OrderPayDetail orderPayDetail = new OrderPayDetail();
			//orderAccount
			orderPayDetail.setAccountId(orderDetail.getAccountId());
			OrderAccount orderAccount = orderAccountDAO.findById(orderDetail.getAccountId());
			orderPayDetail.setPayAccount(orderAccount.getPayAccount());
			//creditCard
			orderPayDetail.setCreditCardId(orderAccount.getCreditCardId());
			if (orderAccount.getCreditCardId() != null && orderAccount.getCreditCardId()>0) {
                
				OrderCreditCard orderCreditCard  = orderCreditCardDAO.getOrderCreditCardById(orderAccount.getCreditCardId());
                if(orderCreditCard != null){
                	orderPayDetail.setCardNo(orderCreditCard.getCardNo());
                	orderPayDetail.setBankName(orderCreditCard.getBankName());
                	orderPayDetail.setPayType("credit");
                }
            }
            if(orderAccount.getPayAccountId() != null && orderAccount.getPayAccountId()>0){
            	OrderPayAccount orderPayAccount = orderPayAccountDAO.getOrderPayAccountById(orderAccount.getPayAccountId());
            	orderPayDetail.setCreditCardId(orderPayAccount.getCreditCardId());
            	if(orderPayAccount.getCreditCardId() != null && orderAccount.getCreditCardId()>0){
            		OrderCreditCard orderCreditCard  = orderCreditCardDAO.getOrderCreditCardById(orderPayAccount.getCreditCardId());
            		if(orderCreditCard != null){
                    	orderPayDetail.setCardNo(orderCreditCard.getCardNo());
                    	orderPayDetail.setBankName(orderCreditCard.getBankName());
                    }
            	}
            	orderPayDetail.setPayType(orderPayAccount.getAccountType());
            }
			//orderDevice
			OrderDevice orderDevice = orderDeviceDAO.findById(orderDetail.getDeviceId());
			orderPayDetail.setDeviceId(orderDetail.getDeviceId());
			orderPayDetail.setDeviceIp(orderDevice.getDeviceIp());
			
			orderPayDetail.setStatusType(0);
			orderPayDetail.setUnits(orderDetail.getUnits());
			orderPayDetail.setPrice(orderDetail.getTotalPrice());
			orderPayDetail.setSkuPrice(orderDetail.getMyPrice());
			orderPayDetail.setNum(orderDetail.getNum());
			orderPayDetail.setSolePrice(String.valueOf(Float.parseFloat(orderDetail.getMyPrice())*orderDetail.getNum()));
			orderPayDetail.setSiteName(orderDetail.getSiteName());
			orderPayDetail.setOrderNo(orderDetail.getOrderNo());
			orderPayDetail.setOperator("auto");
			orderPayDetail.setGmtCreate(new Date());
			orderPayDetail.setOrderTime(orderDetail.getOrderTime());
			orderPayDetail.setCompany(orderDetail.getCompany());
			orderPayDetail.setIsDirect(orderDetail.getIsDirect());
			orderPayDetail.setIsManual(orderDetail.getIsManual());
			orderPayDetail.setGroupNumber(orderDetail.getGroupNumber());
			orderPayDetail.setProductEntityId(orderDetail.getProductEntityId());
			if(("amazon").equalsIgnoreCase(orderDetail.getSiteName()) || ("amazon.jp").equalsIgnoreCase(orderDetail.getSiteName())){
				orderPayDetail.setStartBalance(String.valueOf(orderAccount.getBalanceWb()));
				orderPayDetail.setEndBalance(String.valueOf(orderDetail.getBalanceWb()));
				orderPayDetail.setPayType("giftcard");
	        }
			if("victoriassecret".equalsIgnoreCase(orderDetail.getSiteName())){
				orderPayDetail.setPayType("giftcard");
			}
			orderPayDetail.setPayTime(orderDetail.getDispatchTime());
			String rmbPrice = getRmbPrice(orderDetail.getUnits(),orderDetail.getTotalPrice());
			orderPayDetail.setRmbPrice(rmbPrice);
			orderPayDetailDao.addOrderPayDetail(orderPayDetail);
		}
	}
	private String getRmbPrice(String units, String totalPrice) {
		try {
			MoneyUnits moneyUnits = MoneyUnits.getMoneyUnitsByCode(units);
			ExchangeDefinition exchangeDefinition = exchangeDefinitionDAO.getExchangeDefinitionByUnits(moneyUnits.getValue());
			Double exchangeMoney = MathUtil.divCelling(Double.valueOf(totalPrice) * exchangeDefinition.getRmb(), Double.valueOf(exchangeDefinition.getSource()));
			return String.valueOf(exchangeMoney);
		} catch (Exception e) {
		}
		
		return null;
	}

}
