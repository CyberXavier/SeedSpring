package com.minis.web.bind;

public class DefaultWebDataBinder {
    private Object parameterType;
    private String parameterName;
    private ParameterValueHandler parameterValueHandler;

    public DefaultWebDataBinder(String parameterName, Object parameterType){
        this.parameterName = parameterName;
        this.parameterType = parameterType;
    }


}
