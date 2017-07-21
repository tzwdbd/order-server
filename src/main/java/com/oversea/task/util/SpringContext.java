/**
 * 淘粉吧版权所有 2013
 */
package com.oversea.task.util;

import java.util.Map;

import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * @author rambing
 *
 * 创建时间： 2014年7月29日
 */
public class SpringContext {
	
	protected static ClassPathXmlApplicationContext context;
	
	static{
		context = new ClassPathXmlApplicationContext(
					"task-job-application.xml"
					,"spring-task-server.xml"
					,"application-server-core.xml");
	}
	
	public static void shutdown(){
		context.destroy();
	}
	
	/**
	 * 获取某一个类型的Bean
	 * 
	 * @param clazz
	 * @return
	 */
	public static <T> T getBean(Class<T> clazz){
		return context.getBean(clazz);
	}
	
	/**
	 * 获取指定类型的所有BEAN
	 * 
	 * @param clazz
	 * @return
	 */
	public static <T> Map<String, T> getBeans(Class<T> clazz){
		return context.getBeansOfType(clazz);
	}


}
