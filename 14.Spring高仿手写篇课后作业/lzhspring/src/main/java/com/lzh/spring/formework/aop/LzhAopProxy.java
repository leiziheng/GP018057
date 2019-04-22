package com.lzh.spring.formework.aop;


public interface LzhAopProxy {


    Object getProxy();


    Object getProxy(ClassLoader classLoader);
}