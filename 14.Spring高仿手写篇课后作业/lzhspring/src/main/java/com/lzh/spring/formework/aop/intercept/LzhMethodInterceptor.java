package com.lzh.spring.formework.aop.intercept;


public interface LzhMethodInterceptor {
    Object invoke(LzhMethodInvocation invocation) throws Throwable;
}
