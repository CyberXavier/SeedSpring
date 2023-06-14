package com.minis.web.method;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public interface ParameterNameDiscoverer {

    String discover(Parameter parameter);

}
