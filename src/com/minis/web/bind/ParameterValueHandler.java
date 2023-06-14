package com.minis.web.bind;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Parameter;

public interface ParameterValueHandler {
    Object handler(Parameter parameter, HttpServletRequest request);
}
