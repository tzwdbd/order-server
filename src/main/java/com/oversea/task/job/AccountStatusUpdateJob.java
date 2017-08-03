package com.oversea.task.job;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Component;

import com.oversea.task.mapper.OrderAccountDAO;
@Component
public class AccountStatusUpdateJob {
	
	private Logger log = Logger.getLogger(getClass());

    @Resource
    private OrderAccountDAO orderAccountDAO;

    private static final String acountStatus = "0,4";
    private static final String companyIdStatus = "3,4,8,9";
    private static final String siteNames = "'amazon','amazon.jp'";
    
    
	
	public void run(){
		log.error("============AccountStatusUpdateJob begin============");
		//orderAccountDAO.updateOrderAccountStatus(companyIdStatus, siteNames);
        
		log.error("============AccountStatusUpdateJob end=================");
	}

}
