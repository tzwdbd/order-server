package com.oversea.task.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import com.oversea.task.controller.base.BaseController;
import com.oversea.task.frame.BaseModel;

@Controller
public class AutoOrderOperController extends BaseController{
	
	private static final Logger log = Logger.getLogger(AutoOrderOperController.class);
	
	@RequestMapping("handleAutoOrderOperByType")
	@ResponseBody
	public Object handleAutoOrderOperByType(HttpServletRequest request){
		try{
			String orderNo = request.getParameter("orderNo");
			String groupNumber = request.getParameter("groupNumber");
			String type = request.getParameter("type");
			
			return null;
		}catch(Exception e){
			log.error("handleAutoOrderOperByType error : " , e);
			return new BaseModel("异常");
		}
	}
}
