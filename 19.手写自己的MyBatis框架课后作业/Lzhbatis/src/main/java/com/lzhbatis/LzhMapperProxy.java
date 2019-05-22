package com.lzhbatis;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class LzhMapperProxy implements InvocationHandler {
    private LzhSqlSession sqlSession;

    public LzhMapperProxy(LzhSqlSession sqlSession) {
        this.sqlSession = sqlSession;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        String mapperInterface = method.getDeclaringClass().getName();
        String methodName = method.getName();
        String statementId = mapperInterface + "." + methodName;
        return sqlSession.selectOne(statementId, args[0]);
    }
}
