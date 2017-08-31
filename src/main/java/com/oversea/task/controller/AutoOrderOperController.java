package com.oversea.task.controller;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.oversea.task.controller.base.BaseController;
import com.oversea.task.frame.BaseModel;
import com.oversea.task.job.ManualOrderService;
import com.oversea.task.job.ManualShipService;

@Controller
public class AutoOrderOperController extends BaseController{
	
	private static final Logger log = Logger.getLogger(AutoOrderOperController.class);
	
	@Resource
	private ManualOrderService manualOrderService;
	@Resource
	private ManualShipService manualShipService;
	
	@RequestMapping("handleAutoOrderOperByType")
	public void handleAutoOrderOperByType(HttpServletRequest request){
		try{
			String orderNo = request.getParameter("orderNo");
			String groupNumber = request.getParameter("groupNumber");
			String steps = request.getParameter("steps");
			String type = request.getParameter("type");
			
			if("logistics".equals(type)){
				manualShipService.handleShip(orderNo, Integer.valueOf(groupNumber), Integer.valueOf(steps));
			}else if("order".equals(type)){
				manualOrderService.handleOrder(orderNo, Integer.valueOf(groupNumber), Integer.valueOf(steps));
			}
			
		}catch(Exception e){
			log.error("handleAutoOrderOperByType error : " , e);
		}
	}
}
