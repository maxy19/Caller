package com.maxy.caller.core.utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * @author maxy
 */
@Component
public class SpringContextUtils implements ApplicationContextAware {

    private static ApplicationContext context = null;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext)
            throws BeansException {
        context = applicationContext;
    }

    public static ApplicationContext getApplicationContext() {
        return context;
    }

    public static <T> T getBean(String beanName) {
        return (T) context.getBean(beanName);
    }

    public static <T> T getBean(Class<T> clazz) {
        return context.getBean(clazz);
    }

    public static String getMessage(String key) {
        return context.getMessage(key, null, Locale.getDefault());
    }

}