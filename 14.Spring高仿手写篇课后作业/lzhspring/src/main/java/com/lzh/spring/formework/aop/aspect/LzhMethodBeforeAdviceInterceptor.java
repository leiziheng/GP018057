package com.lzh.spring.formework.aop.aspect;


import com.lzh.spring.formework.aop.intercept.LzhMethodInterceptor;
import com.lzh.spring.formework.aop.intercept.LzhMethodInvocation;

import java.lang.reflect.Method;

public class LzhMethodBeforeAdviceInterceptor extends LzhAbstractAspectAdvice implements LzhAdvice, LzhMethodInterceptor {


    private LzhJoinPoint joinPoint;

    public LzhMethodBeforeAdviceInterceptor(Method aspectMethod, Object aspectTarget) {
        super(aspectMethod, aspectTarget);
    }


    private void before(Method method,Object[] args,Object target) throws Throwable{
        //传送了给织入参数
        //method.invoke(target);
        super.invokeAdviceMethod(this.joinPoint,null,null);

    }

    @Override
    public Object invoke(LzhMethodInvocation mi) throws Throwable {
        //从被织入的代码中才能拿到，JoinPoint
        this.joinPoint = mi;
        before(mi.getMethod(), mi.getArguments(), mi.getThis());
        return mi.proceed();
    }

}
