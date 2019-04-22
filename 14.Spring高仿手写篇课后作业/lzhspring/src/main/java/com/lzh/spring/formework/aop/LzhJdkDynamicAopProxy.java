package com.lzh.spring.formework.aop;


import com.lzh.spring.formework.aop.intercept.LzhMethodInvocation;
import com.lzh.spring.formework.aop.support.LzhAdvisedSupport;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;


public class LzhJdkDynamicAopProxy implements LzhAopProxy, InvocationHandler {

    private LzhAdvisedSupport advised;

    public LzhJdkDynamicAopProxy(LzhAdvisedSupport advised) {
        this.advised = advised;
    }

    @Override
    public Object getProxy() {
        return getProxy(this.advised.getTargetClass().getClassLoader());
    }

    @Override
    public Object getProxy(ClassLoader classLoader) {
        return Proxy.newProxyInstance(classLoader,this.advised.getTargetClass().getInterfaces(),this);
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        LzhMethodInvocation invocation = new LzhMethodInvocation(proxy,this.advised.getTarget(),method,args,this.advised.getTargetClass(),this.advised.getInterceptorsAndDynamicInterceptionAdvice(method,this.advised.getTargetClass()));
        return invocation.proceed();
    }
}
