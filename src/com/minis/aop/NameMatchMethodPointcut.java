package com.minis.aop;

import com.minis.utils.PatternMatchUtils;

import java.lang.reflect.Method;

public class NameMatchMethodPointcut implements MethodMatcher, Pointcut{
    private String mappedName = "";

    public void setMapperName(String mappedName) {
        this.mappedName = mappedName;
    }

    @Override
    public boolean matches(Method method, Class<?> targetCLass) {
        if (mappedName.equals(method.getName()) || isMatch(method.getName(), mappedName)) {
            return true;
        }
        return false;
    }

    //核心方法，判断方法名是否匹配给定的模式
    private boolean isMatch(String methodName, String mappedName) {
        return PatternMatchUtils.simpleMatch(mappedName, methodName);
    }

    @Override
    public MethodMatcher getMethodMatcher() {
        return this;
    }
}
