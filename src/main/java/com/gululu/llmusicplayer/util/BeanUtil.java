package com.gululu.llmusicplayer.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;


@Slf4j
public class BeanUtil implements ApplicationContextAware, DisposableBean {

    private static ApplicationContext applicationContext = null;

    /**
     * 从静态变量applicationContext中取得Bean, 自动转型为所赋值对象的类型.
     */
    public static <T> T getBean(Class<T> requiredType) {
        if(applicationContext==null){
            throw new IllegalStateException("applicaitonContext属性未注入, 请在SpringBoot启动类中注册BeanUtil.");
        }
        return applicationContext.getBean(requiredType);
    }



    @Override
    public void destroy() {
        applicationContext = null;
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        if (BeanUtil.applicationContext != null) {
            log.warn("BeanUtil中的ApplicationContext被覆盖, 原有ApplicationContext为:" + BeanUtil.applicationContext);
        }
        BeanUtil.applicationContext = applicationContext;
    }
}