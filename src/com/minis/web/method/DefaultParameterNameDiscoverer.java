package com.minis.web.method;

import com.minis.web.bind.annotation.RequestParam;

import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;

public class DefaultParameterNameDiscoverer implements ParameterNameDiscoverer{
    @Override
    public String discover(Parameter parameter) {
//        boolean annotationPresent = parameter.isAnnotationPresent(RequestParam.class);
        RequestParam annotation = parameter.getDeclaredAnnotation(RequestParam.class);
        String parameterName;
        if (annotation != null) { // 有RequestParam注解
            parameterName = annotation.value();
        }else { // 没有RequestParam注解
            parameterName = parameter.getName();
        }
        return parameterName;
    }

}
