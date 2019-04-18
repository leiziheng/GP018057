package com.lzh.spring.formework.beans;

public class LzhBeanWrapper {

    private Object wrappedInstance;
    private Class<?> wrappedClass;

    public LzhBeanWrapper(Object wrappedInstance) {
        this.wrappedInstance = wrappedInstance;
    }

    public Object getWrappedInstance() {
        return this.wrappedInstance;
    }

    // 返回代理以后的Class
    // 可能会是这个 $Proxy0
    public Class<?> getWrappedClass() {
        return this.wrappedInstance.getClass();
    }
}
