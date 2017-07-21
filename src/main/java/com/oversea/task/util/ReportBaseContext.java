package com.oversea.task.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Logger;

public abstract class ReportBaseContext {
	
	private static final Logger log = Logger.getLogger(ReportBaseContext.class);
	
	/**
	 * 
	* @Description: 获取当前整点的小时
	* @param @return    
	* @return Date    返回类型
	 */
	public Date getCurrentHour(){
		//设置当前时间
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		return calendar.getTime();
	}
	
	/**
	 * 
	 * @param periodTime
	 * @return
	 */
	public Long getCurrentTime(int periodTime){
		return ((System.currentTimeMillis() - getCurrentHour().getTime()) / periodTime) * periodTime + getCurrentHour().getTime();
	}
	
	/**
	 * 
	* @Description: 根据类型名称获取get方法
	* @param fieldName
	* @return Method    返回类型
	 */
	public Method getGetMethod(Class<? extends Object> clazz,String fieldName) {       
		StringBuffer sb = new StringBuffer();       
	    sb.append("get");       
	    sb.append(fieldName.substring(0, 1).toUpperCase());       
	    sb.append(fieldName.substring(1));       
	    try {       
	        return clazz.getMethod(sb.toString());       
	    } catch (Exception e) {       
	    	log.warn("获取"+fieldName+"get方法报错", e);
	    } 
	    return null;       
	}
	
	/**
	 * 
	* @Description: 根据类型名称获取set方法
	* @param fieldName
	* @return Method    返回类型
	 */
	public Method getSetMethod(Class<? extends Object> clazz,String fieldName) {       
	    try {       
	        Class<?>[] parameterTypes = new Class[1];       
	        Field field = clazz.getDeclaredField(fieldName);       
	        parameterTypes[0] = field.getType();       
	        StringBuffer sb = new StringBuffer();       
	        sb.append("set");       
	        sb.append(fieldName.substring(0, 1).toUpperCase());       
	        sb.append(fieldName.substring(1));       
	        Method method = clazz.getMethod(sb.toString(), parameterTypes);       
	        return method;       
	    } catch (Exception e) {       
	    	log.warn("获取"+fieldName+"set方法报错", e);     
	    }       
	    return null;       
	} 
	
	/**
	 * 
	* @Description: 执行set方法
	* @param @param o
	* @param @param fieldName
	* @param @param value    
	* @return void    返回类型
	 */
	public void invokeSet(Object o, String fieldName, Object value) {       
	    Method method = getSetMethod(o.getClass(),fieldName);       
	    try {       
	        method.invoke(o, new Object[] { value });
	        syschronizeReport(System.currentTimeMillis());
	    } catch (Exception e) {       
	    	log.warn("执行"+fieldName+"set方法报错", e);       
	    }       
	    syschronizeReport(System.currentTimeMillis());
	}       
	  
	/**
	 *   
	* @Description: 执行get方法
	* @param @param o
	* @param @param fieldName
	* @param @return    
	* @return Object    返回类型
	 */
	public Object invokeGet(Object o, String fieldName) {       
	    Method method = getGetMethod(o.getClass(),fieldName);       
	    try {       
	        return method.invoke(o, new Object[0]);       
	    } catch (Exception e) {       
	    	log.warn("执行"+fieldName+"get方法报错", e);    
	    }       
	  
	    return null;       
	}
	
	/**
	 * 
	* @Description: 将report同步到数据库
	* @param time    
	* @return void    返回类型
	 */
	public abstract void syschronizeReport(Long time);
	
	/**
	 * 
	* @Description: 添加一条报表记录
	* @param type 报表的字段 
	* @param count 记录数   
	* @return void    返回类型
	 */
	public abstract void addReprot(String type, int count);
	
	/**
	 * @Description: 添加一条报表记录，支持KEY-VALUE
	 * @param type 报表的字段
	 * @param value 报表字段对应的值
	 */
	public abstract void addKVReport(String type, String value);
}