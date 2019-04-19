package com.lzh.spring.formework.beans.support;

import com.lzh.spring.formework.beans.config.LzhBeanDefinition;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class LzhBeanDefinitionReader {

    private List<String> registyBeanClasses = new ArrayList<String>();

    private Properties config = new Properties();

    //固定配置文件中的key，相对于xml的规范
    private final String SCAN_PACKAGE = "scanPackage";


    public LzhBeanDefinitionReader(String... locations) {
        //通过URL定位找到其所对应的文件，然后转换为文件流
        InputStream is = this.getClass().getClassLoader().getResourceAsStream(locations[0].replace("classpath:", ""));
        try {
            config.load(is);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != is) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        doScanner(config.getProperty(SCAN_PACKAGE));

    }


    private void doScanner(String scanPackage) {
        //转换为文件路径，实际上就是把.替换为/就OK了
        //this.getClass()
        // this.getClass().getClassLoader()
        URL url = this.getClass().getResource("/" + scanPackage.replaceAll("\\.", "/"));
        File classPath = new File(url.getFile());
        for (File file : classPath.listFiles()) {
            if (file.isDirectory()) {
                doScanner(scanPackage + "." + file.getName());
            } else {
                if (!file.getName().endsWith(".class")) {
                    continue;
                }
                String className = (scanPackage + "." + file.getName().replace(".class", ""));
                registyBeanClasses.add(className);
            }
        }
    }


    public Properties getConfig() {
        return this.config;
    }

    //把配置文件中扫描到的所有的配置信息转换为LzhBeanDefinition对象，以便于之后IOC操作方便
    public List<LzhBeanDefinition> loadBeanDefinitions() {
        List<LzhBeanDefinition> result = new ArrayList<LzhBeanDefinition>();
        for (String className : registyBeanClasses) {
            LzhBeanDefinition beanDefinition = doCreateBeanDefinition(className);
            if (null == beanDefinition) {
                continue;
            }
            result.add(beanDefinition);
        }
        return result;
    }

    //把每一个配信息解析成一个BeanDefinition
    private LzhBeanDefinition doCreateBeanDefinition(String className) {
        try {
            Class<?> beanClass = null;

            beanClass = Class.forName(className);

            //如果是一个接口，是不能实例化的
            //用它实现类来实例化
            if (beanClass.isInterface()) {
                return null;
            }
            LzhBeanDefinition beanDefinition = new LzhBeanDefinition();
            beanDefinition.setBeanClassName(className);
            beanDefinition.setFactoryBeanName(beanClass.getSimpleName());
            return beanDefinition;

        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;

    }

    //如果类名本身是小写字母，确实会出问题
    //但是我要说明的是：这个方法是我自己用，private的
    //传值也是自己传，类也都遵循了驼峰命名法
    //默认传入的值，存在首字母小写的情况，也不可能出现非字母的情况

    //为了简化程序逻辑，就不做其他判断了，大家了解就OK
    //其实用写注释的时间都能够把逻辑写完了
    private String toLowerFirstCase(String simpleName) {
        char[] chars = simpleName.toCharArray();
        //之所以加，是因为大小写字母的ASCII码相差32，
        // 而且大写字母的ASCII码要小于小写字母的ASCII码
        //在Java中，对char做算学运算，实际上就是对ASCII码做算学运算
        chars[0] += 32;
        return String.valueOf(chars);
    }
}
