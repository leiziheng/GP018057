package com.lzh.spring.formework.aop.aspect;

import com.lzh.spring.formework.aop.intercept.LzhMethodInterceptor;
import com.lzh.spring.formework.aop.intercept.LzhMethodInvocation;

import java.lang.reflect.Method;

/**
 * Created by Tom on 2019/4/15.
 */
public class LzhAfterReturningAdviceInterceptor extends LzhAbstractAspectAdvice implements LzhAdvice,LzhMethodInterceptor {

    private LzhJoinPoint joinPoint;
    public LzhAfterReturningAdviceInterceptor(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }

    @Override
    public Object invoke(LzhMethodInvocation mi) throws Throwable {
        Object retVal = mi.proceed();
        this.joinPoint = mi;
        this.afterReturning(retVal,mi.getMethod(),mi.getArguments(),mi.getThis());
        return retVal;
    }

    private void afterReturning(Object retVal, Method method, Object[] arguments, Object aThis) throws Throwable {
        super.invokeAdviceMethod(this.joinPoint,retVal,null);
    }
}
