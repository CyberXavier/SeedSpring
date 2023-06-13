package com.minis.web.method.annotation;

import com.minis.beans.BeansException;
import com.minis.context.ApplicationContext;
import com.minis.context.ApplicationContextAware;
import com.minis.http.converter.HttpMessageConverter;
import com.minis.utils.StringUtils;
import com.minis.web.bind.support.WebBindingInitializer;
import com.minis.web.bind.WebDataBinder;
import com.minis.web.bind.support.WebDataBinderFactory;
import com.minis.web.bind.annotation.ResponseBody;
import com.minis.web.servlet.HandlerAdapter;
import com.minis.web.method.HandlerMethod;
import com.minis.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RequestMappingHandlerAdapter implements HandlerAdapter, ApplicationContextAware {

    private ApplicationContext applicationContext= null;

    private WebBindingInitializer webBindingInitializer = null;

    private HttpMessageConverter messageConverter = null;

    public HttpMessageConverter getMessageConverter() {
        return messageConverter;
    }

    public void setMessageConverter(HttpMessageConverter messageConverter) {
        this.messageConverter = messageConverter;
    }

    public RequestMappingHandlerAdapter() {
    }

    @Override
    public ModelAndView handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        return handleInternal(request, response, (HandlerMethod) handler);
    }

    private ModelAndView handleInternal(HttpServletRequest request, HttpServletResponse response, HandlerMethod handler) {

        ModelAndView mv = null;

        try {
            mv = invokeHandlerMethod(request, response, handler);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return mv;
    }

    protected ModelAndView invokeHandlerMethod(HttpServletRequest request,
                                               HttpServletResponse response, HandlerMethod handlerMethod) throws Exception{

        WebDataBinderFactory binderFactory = new WebDataBinderFactory();

        Parameter[] methodParameters = handlerMethod.getMethod().getParameters();
        Object[] methodParamObjs = new Object[methodParameters.length];

        // 取出request中全部参数
        Map<String, String[]> parameterMap = request.getParameterMap();
        List<String[]> parameterValueList = new ArrayList<>(parameterMap.size());
        for (String[] str : parameterMap.values()) {
            parameterValueList.add(str);
        }

        int i = 0;
        //对调用方法里的每一个参数，处理绑定
        for (Parameter methodParameter : methodParameters) {
            // 判断是否为基本数据类型：
            if (methodParameter.getType().isPrimitive()){
                String paramValue = parameterValueList.get(i)[0];
                // 参数类型
                Class<?> paramType = methodParameter.getType();
                // 判断参数类型
                Object tempValue = null;
                if (paramType == int.class || paramType == Integer.class) {
                    tempValue = Integer.parseInt(paramValue);
                }
                else if (paramType == long.class || paramType == Long.class) {
                    tempValue = Long.parseLong(paramValue);
                }
                else if (paramType == double.class || paramType == Double.class) {
                    tempValue = Double.parseDouble(paramValue);
                }
                else if (paramType == float.class || paramType == Float.class) {
                    tempValue = Float.parseFloat(paramValue);
                }
                else if (paramType == boolean.class || paramType == Boolean.class) {
                    tempValue = Boolean.parseBoolean(paramValue);
                }
                else {
                    throw new IllegalArgumentException("暂不支持{ " + paramType + " },这种类型！");
                }
                methodParamObjs[i] = tempValue;
            }
            else if (methodParameter.getType()!=HttpServletRequest.class && methodParameter.getType()!=HttpServletResponse.class){
                Object methodParamObj = methodParameter.getType().newInstance();
                // 给这个参数创建WebDatabinder
                WebDataBinder wdb = binderFactory.createBinder(request, methodParamObj, methodParameter.getName());
                webBindingInitializer.initBinder(wdb);
                wdb.bind(request);
                methodParamObjs[i] = methodParamObj;
            }
            else if (methodParameter.getType()==HttpServletRequest.class) {
                methodParamObjs[i] = request;
            }
            else if (methodParameter.getType()==HttpServletResponse.class) {
                methodParamObjs[i] = response;
            }
            i ++;
        }
        // 调用
        Method invocableMethod = handlerMethod.getMethod();
        Object returnObj = invocableMethod.invoke(handlerMethod.getBean(), methodParamObjs);
        Class<?> returnType = invocableMethod.getReturnType();

        ModelAndView mav = null;
        //如果是ResponseBody注解，仅仅返回值，则转换数据格式后直接写到response
        if (invocableMethod.isAnnotationPresent(ResponseBody.class)) {
            this.messageConverter.write(returnObj, response);
        }
        else if (returnType == void.class) {

        }
        else { //返回的是前端页面
            if (returnObj instanceof ModelAndView) {
                mav = (ModelAndView) returnObj;
            }
            else if (returnObj instanceof String) { //字符串也认为是前端页面
                String sTarget = (String) returnObj;
                mav = new ModelAndView();
                mav.setViewName(sTarget);
            }
        }
        return mav;
    }

    public WebBindingInitializer getWebBindingInitializer() {
        return webBindingInitializer;
    }

    public void setWebBindingInitializer(WebBindingInitializer webBindingInitializer) {
        this.webBindingInitializer = webBindingInitializer;
    }


    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }
}
