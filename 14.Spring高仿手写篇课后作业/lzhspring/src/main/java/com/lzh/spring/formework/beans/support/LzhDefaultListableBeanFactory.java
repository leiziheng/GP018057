package com.lzh.spring.formework.beans.support;

import com.lzh.spring.formework.beans.config.LzhBeanDefinition;
import com.lzh.spring.formework.context.support.LzhAbstractApplicationContext;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public abstract class LzhDefaultListableBeanFactory extends LzhAbstractApplicationContext {

    //存储注册信息的BeanDefinition,伪IOC容器
    protected final Map<String, LzhBeanDefinition> beanDefinitionMap = new ConcurrentHashMap<String, LzhBeanDefinition>();
}
