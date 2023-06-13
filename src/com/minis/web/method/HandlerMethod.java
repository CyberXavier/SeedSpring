package com.minis.web.method;

import com.minis.web.servlet.MethodParameter;

import java.lang.reflect.Method;

public class HandlerMethod {
    private Object bean;
    private Class<?> beanType;
    private Method method;
    private MethodParameter[] parameters;
    private  Class<?> returnType;
    private  String description;
    private  String className;
    private  String methodName;

    public HandlerMethod(Method method, Object obj) {
        this.setMethod(method);
        this.setBean(obj);
    }

    public HandlerMethod(Method method, Object obj, Class<?> clz, String methodName){
        this.method = method;
        this.bean = obj;
        this.beanType = clz;
        this.methodName = methodName;
    }

    public Object getBean() {
        return this.bean;
    }

    public void setBean(Object bean) {
        this.bean = bean;
    }

    public Method getMethod() {
        return method;
    }

    public void setMethod(Method method) {
        this.method = method;
    }
}
