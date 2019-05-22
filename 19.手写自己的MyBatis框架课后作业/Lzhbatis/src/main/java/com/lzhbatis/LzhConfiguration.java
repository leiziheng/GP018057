package com.lzhbatis;

import java.lang.reflect.Proxy;
import java.util.ResourceBundle;

public class LzhConfiguration {

    public static final ResourceBundle sqlMappings;

    static {
        sqlMappings = ResourceBundle.getBundle("lzhsql");
    }


    public Object getMapper(Class clazz, LzhSqlSession sqlSession) {
        return Proxy.newProxyInstance(this.getClass().getClassLoader(), new Class[]{clazz}, new LzhMapperProxy(sqlSession));
    }
}
