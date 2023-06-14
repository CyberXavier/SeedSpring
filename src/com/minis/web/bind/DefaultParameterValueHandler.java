package com.minis.web.bind;

import com.minis.beans.PropertyEditor;
import com.minis.beans.PropertyEditorRegistrySupport;
import com.minis.web.method.DefaultParameterNameDiscoverer;
import com.minis.web.method.ParameterNameDiscoverer;

import javax.servlet.http.HttpServletRequest;
import java.lang.annotation.Annotation;
import java.lang.reflect.Parameter;

public class DefaultParameterValueHandler extends PropertyEditorRegistrySupport implements ParameterValueHandler{

    private ParameterNameDiscoverer parameterNameDiscoverer = null;

    public DefaultParameterValueHandler(){
        this.parameterNameDiscoverer = new DefaultParameterNameDiscoverer();
    }

    @Override
    public Object handler(Parameter parameter, HttpServletRequest request) {
        Object res = null;
        res = innerHandler(parameter, request);
        return res;
    }

    private Object innerHandler(Parameter parameter, HttpServletRequest request) {
        String parameterName = this.parameterNameDiscoverer.discover(parameter);
        Class<?> requiredType = parameter.getType();
        PropertyEditor editors = this.getEditors(requiredType);
        String parameterValue = request.getParameter(parameterName);
        editors.setAsText(parameterValue);
        return editors.getValue();
    }

    public PropertyEditor getEditors(Class<?> requiredType) {
        return super.getDefaultEditors(requiredType);
    }
}
