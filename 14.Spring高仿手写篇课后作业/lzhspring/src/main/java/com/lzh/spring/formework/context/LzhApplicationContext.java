package com.lzh.spring.formework.context;

import com.lzh.spring.formework.annotation.LzhAutowired;
import com.lzh.spring.formework.annotation.LzhController;
import com.lzh.spring.formework.annotation.LzhService;
import com.lzh.spring.formework.beans.config.LzhBeanDefinition;
import com.lzh.spring.formework.beans.LzhBeanWrapper;
import com.lzh.spring.formework.beans.config.LzhBeanPostProcessor;
import com.lzh.spring.formework.beans.support.LzhBeanDefinitionReader;
import com.lzh.spring.formework.beans.support.LzhDefaultListableBeanFactory;
import com.lzh.spring.formework.core.LzhBeanFactory;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 按之前源码分析的套路，IOC、DI、MVC、AOP
 */
public class LzhApplicationContext extends LzhDefaultListableBeanFactory implements LzhBeanFactory {

    //单例的IOC容器缓存
    private Map<String, Object> singletonObjects = new ConcurrentHashMap<String, Object>();
    //通用的IOC容器
    private Map<String, LzhBeanWrapper> factoryBeanInstanceCache = new ConcurrentHashMap<String, LzhBeanWrapper>();

    private String[] configLoactions;

    private LzhBeanDefinitionReader reader;

    public LzhApplicationContext(String... configLoactions) {
        this.configLoactions = configLoactions;
        try {
            refresh();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void refresh() throws Exception {
        //1、定位，定位配置文件
        reader = new LzhBeanDefinitionReader(this.configLoactions);

        //2、加载配置文件，扫描相关的类，把它们封装成BeanDefinition
        List<LzhBeanDefinition> beanDefinitions = reader.loadBeanDefinitions();

        //3、注册，把配置信息放到容器里面(伪IOC容器)
        doRegisterBeanDefinition(beanDefinitions);

        //4、把不是延时加载的类，有提前初始化
        doAutowrited();
    }

    //只处理非延时加载的情况
    private void doAutowrited() {
        for (Map.Entry<String, LzhBeanDefinition> beanDefinitionEntry : super.beanDefinitionMap.entrySet()) {
            String beanName = beanDefinitionEntry.getKey();
            if (!beanDefinitionEntry.getValue().isLazyInit()) {
                try {
                    getBean(beanName);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void doRegisterBeanDefinition(List<LzhBeanDefinition> beanDefinitions) {
        for (LzhBeanDefinition beanDefinition : beanDefinitions) {
            super.beanDefinitionMap.put(beanDefinition.getFactoryBeanName(), beanDefinition);
        }
    }


    @Override
    public Object getBean(Class<?> beanClass) throws Exception {
        return getBean(beanClass.getSimpleName());
    }

    @Override
    public Object getBean(String beanName) throws Exception {

        Object instance = null;

        LzhBeanPostProcessor postProcessor = new LzhBeanPostProcessor();

        postProcessor.postProcessBeforeInitialization(instance, beanName);

        //1、初始化
        instance = instantiateBean(beanName, super.beanDefinitionMap.get(beanName));

        //3、把这个对象封装到BeanWrapper中
        LzhBeanWrapper beanWrapper = new LzhBeanWrapper(instance);

        //2、拿到BeanWraoper之后，把BeanWrapper保存到IOC容器中去
        this.factoryBeanInstanceCache.put(beanName, beanWrapper);

        postProcessor.postProcessAfterInitialization(instance, beanName);

        //3、注入
        populateBean(beanName, new LzhBeanDefinition(), beanWrapper);

        return this.factoryBeanInstanceCache.get(beanName).getWrappedInstance();
    }


    private void populateBean(String beanName, LzhBeanDefinition beanDefinition, LzhBeanWrapper beanWrapper) {

        Object instance = beanWrapper.getWrappedInstance();

//        LzhBeanDefinition.getBeanClassName();

        Class<?> clazz = beanWrapper.getWrappedClass();
        //判断只有加了注解的类，才执行依赖注入
        if (!(clazz.isAnnotationPresent(LzhController.class) || clazz.isAnnotationPresent(LzhService.class))) {
            return;
        }

        //获得所有的fields
        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            if (!field.isAnnotationPresent(LzhAutowired.class)) {
                continue;
            }

            LzhAutowired autowired = field.getAnnotation(LzhAutowired.class);

            String autowiredBeanName = autowired.value().trim();
            if ("".equals(autowiredBeanName)) {
                autowiredBeanName = field.getType().getName();
            }

            //强制访问
            field.setAccessible(true);

            try {
                //为什么会为NULL，先留个坑
                if (this.factoryBeanInstanceCache.get(autowiredBeanName) == null) {
                    continue;
                }
//                if(instance == null){
//                    continue;
//                }
                field.set(instance, this.factoryBeanInstanceCache.get(autowiredBeanName).getWrappedInstance());
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        }

    }

    private Object instantiateBean(String beanName, LzhBeanDefinition beanDefinition) {

        //1、拿到要实例化的对象的类名
        String className = beanDefinition.getBeanClassName();

        //2、反射实例化，得到一个对象
        Object instance = null;
        try {
//            LzhBeanDefinition.getFactoryBeanName()
            //假设默认就是单例,细节暂且不考虑，先把主线拉通
            if (this.singletonObjects.containsKey(className)) {
                instance = this.singletonObjects.get(className);
            } else {
                Class<?> clazz = Class.forName(className);
                instance = clazz.newInstance();
                this.singletonObjects.put(className, instance);
                this.singletonObjects.put(beanDefinition.getFactoryBeanName(), instance);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return instance;
    }

    public String[] getBeanDefinitionNames() {
        return this.beanDefinitionMap.keySet().toArray(new String[this.beanDefinitionMap.size()]);
    }

    public int getBeanDefinitionCount() {
        return this.beanDefinitionMap.size();
    }

    public Properties getConfig() {
        return this.reader.getConfig();
    }
}
