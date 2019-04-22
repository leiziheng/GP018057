package com.lzh.spring.formework.aop;


import com.lzh.spring.formework.aop.support.LzhAdvisedSupport;

/**
 * Created by Tom on 2019/4/14.
 */
public class LzhCglibAopProxy implements LzhAopProxy {


    public LzhCglibAopProxy(LzhAdvisedSupport config) {

    }

    @Override
    public Object getProxy() {
        return null;
    }

    @Override
    public Object getProxy(ClassLoader classLoader) {
        return null;
    }
}
