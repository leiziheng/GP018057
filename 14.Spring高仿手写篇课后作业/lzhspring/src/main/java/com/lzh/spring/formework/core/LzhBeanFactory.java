package com.lzh.spring.formework.core;

public interface LzhBeanFactory {

    /**
     * 根据 beanName 从 IOC 容器之中获得一个实例 Bean
     * @param beanName
     * @return
     */
    Object getBean(String beanName);

    Object getBean(Class<?> beanClass) throws Exception;
}
