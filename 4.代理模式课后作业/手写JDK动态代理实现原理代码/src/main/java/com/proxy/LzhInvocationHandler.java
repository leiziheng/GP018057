package com.proxy;

import java.lang.reflect.Method;

public interface LzhInvocationHandler {
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable;
}
