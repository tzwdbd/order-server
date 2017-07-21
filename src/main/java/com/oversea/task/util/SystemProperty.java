package com.oversea.task.util;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.PropertyPlaceholderConfigurer;
import org.springframework.beans.factory.config.PropertyResourceConfigurer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.core.io.support.PropertiesLoaderSupport;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Properties;

public class SystemProperty implements ApplicationContextAware {

    private static Properties properties = new Properties();

    public static String getContextPropertyStr(String name) {
        return (String) properties.get(name);
    }

    public static Integer getContextPropertyInteger(String name) {
        return Integer.valueOf(getContextPropertyStr(name));
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        try {
            Map<String, PropertyPlaceholderConfigurer> beans = applicationContext.getBeansOfType(PropertyPlaceholderConfigurer.class);
            for (PropertyPlaceholderConfigurer beanProcessor : beans.values()) {
                if (beanProcessor instanceof PropertyResourceConfigurer) {
                    PropertyResourceConfigurer propertyResourceConfigurer = beanProcessor;
                    Method mergeProperties = PropertiesLoaderSupport.class.getDeclaredMethod("mergeProperties");
                    mergeProperties.setAccessible(true);
                    Properties props = (Properties) mergeProperties.invoke(propertyResourceConfigurer);
                    Method convertProperties = PropertyResourceConfigurer.class.getDeclaredMethod("convertProperties", Properties.class);
                    convertProperties.setAccessible(true);
                    convertProperties.invoke(propertyResourceConfigurer, props);
                    properties.putAll(props);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
