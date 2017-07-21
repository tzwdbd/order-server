package com.oversea.task.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.SchedulerFactory;
import org.quartz.impl.StdSchedulerFactory;


public class QuartzHelper {
	private static final Log log = LogFactory.getLog(QuartzHelper.class);
	private static SchedulerFactory sf = new StdSchedulerFactory();
	private QuartzHelper(){
	}
	
	public static Scheduler getScheduler(){
		try {
			log.error("get scheduler");
			return sf.getScheduler();
		} catch (SchedulerException e) {
			log.error("get scheduler error"+e.getMessage(),e);
		}
		return null;
	}
}
