package com.lzh.spring.formework.aop.aspect;


import com.lzh.spring.formework.aop.intercept.LzhMethodInterceptor;
import com.lzh.spring.formework.aop.intercept.LzhMethodInvocation;

import java.lang.reflect.Method;

/**
 * Created by Tom on 2019/4/15.
 */
public class LzhAfterThrowingAdviceInterceptor extends LzhAbstractAspectAdvice implements LzhAdvice, LzhMethodInterceptor {
    private String throwingName;

    public LzhAfterThrowingAdviceInterceptor(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }

    @Override
    public Object invoke(LzhMethodInvocation mi) throws Throwable {
        try {
            return mi.proceed();
        } catch (Throwable e) {
            invokeAdviceMethod(mi, null, e.getCause());
            throw e;
        }
    }

    public void setThrowName(String throwName) {
        this.throwingName = throwName;
    }
}
