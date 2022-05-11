package com.learn.redis.Utils;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * @author VHBin
 * @date 2022/5/10-20:32
 */

@Component
public class ApplicationContextUtils implements ApplicationContextAware {
    private static ApplicationContext application;

    public static Object getBean(String beanName) {
        return application.getBean(beanName);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        application = applicationContext;
    }
}
