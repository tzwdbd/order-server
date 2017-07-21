package com.oversea.task.domain;

import java.lang.reflect.Method;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;
import org.springframework.context.ApplicationContext;

import com.oversea.task.util.StringUtil;

/**
 * 定时任务类
 *
 * @author linlvping
 */
public class QuartzJobBean implements StatefulJob {

    private Log log = LogFactory.getLog(QuartzJobBean.class);

    public static final String TARGET_CLASS = "class";
    public static final String TARGET_METHOD = "method";
    public static final String TARGET_ARGUMENTS = "arguments";

    private static ApplicationContext ac;

    public static void setAc(ApplicationContext ac) {
        QuartzJobBean.ac = ac;
    }

    /**
     * 通过类名和方法名去获取目标对象，再通过反射执行
     * 类名和方法名保存在jobDetail中
     */
    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        String targetClass = (String) context.getMergedJobDataMap().get(TARGET_CLASS);
        String targetMethod = (String) context.getMergedJobDataMap().get(TARGET_METHOD);
        String methodArgs = (String) context.getMergedJobDataMap().get(TARGET_ARGUMENTS);
        executeMehotd(targetClass,targetMethod,methodArgs);
    }

    public void executeMehotd(String targetClass, String targetMethod, String methodArgs) throws JobExecutionException {
        Object[] args = null;
        if (StringUtil.isEmpty(targetClass) || StringUtil.isEmpty(targetMethod)) {
            return;
        }
        try {
            Object target = ac.getBean(targetClass);
            if (target == null) {
                throw new NullPointerException(String.format("can't found [%s] target", targetClass));
            }
            Method executeMethod = getMethod(target.getClass(), targetMethod);
            if (executeMethod == null) {
                throw new NullPointerException(String.format("can't found [%s] method", targetMethod));
            }

            int parameterSiz = executeMethod.getParameterTypes().length;
            if (parameterSiz > 0) {
                String[] argString = methodArgs.split("&&");
                args = new Object[argString.length];
                for (int i = 0; i < parameterSiz; i++) {
                    if (i < argString.length) {
                        args[i] = argString[i].trim();
                    } else {
                        args[i] = null;
                    }
                }
            }

            // Class tc = target.getClass();
            Class[] parameterType = null;
            if (args != null) {
                parameterType = new Class[args.length];
                for (int i = 0; i < args.length; i++) {
                    parameterType[i] = String.class;
                }
            }
            //取到父类的方法
            Method method = null;
            for (Class<?> clazz = target.getClass(); clazz != Object.class; clazz = clazz.getSuperclass()) {
                try {
                    method = clazz.getDeclaredMethod(targetMethod, parameterType);
                    break;
                } catch (NoSuchMethodException e) {
                    continue;
                }
            }
            if (null != method) {
                method.invoke(target, args);
            }
        } catch (Exception e) {
            log.error(e.toString(), e);
            throw new JobExecutionException(e);
        }
    }

    private Method getMethod(Class clazz, String methodName) {
        for (Method method : clazz.getMethods()) {
            if (method.getName().equals(methodName)) {
                return method;
            }
        }
        return null;
    }
}
