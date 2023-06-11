package com.minis.web.servlet;

import com.minis.beans.BeansException;
import com.minis.web.WebApplicationContext;
import com.minis.web.WebBindingInitializer;
import com.minis.web.WebDataBinder;
import com.minis.web.WebDataBinderFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class RequestMappingHandlerAdapter implements HandlerAdapter{

    WebApplicationContext wac;

    private WebBindingInitializer webBindingInitializer = null;

    public RequestMappingHandlerAdapter(WebApplicationContext wac){
        this.wac = wac;
        try {
            this.webBindingInitializer = (WebBindingInitializer) this.wac.getBean("webBindingInitializer");
        } catch (BeansException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        handleInternal(request, response, (HandlerMethod) handler);
    }

    private void handleInternal(HttpServletRequest request, HttpServletResponse response, HandlerMethod handler) {
        try {
            invokeHandlerMethod(request, response, handler);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void invokeHandlerMethod(HttpServletRequest request,
                                       HttpServletResponse response, HandlerMethod handlerMethod) throws Exception{

        WebDataBinderFactory binderFactory = new WebDataBinderFactory();

        Parameter[] methodParameters = handlerMethod.getMethod().getParameters();
        Object[] methodParamObjs = new Object[methodParameters.length];
        int i = 0;
        //对调用方法里的每一个参数，处理绑定
        for (Parameter methodParameter : methodParameters) {
            Object methodParamObj = methodParameter.getType().newInstance();
            // 给这个参数创建WebDatabinder
            WebDataBinder wdb = binderFactory.createBinder(request, methodParamObj, methodParameter.getName());
            webBindingInitializer.initBinder(wdb);
            wdb.bind(request);
            methodParamObjs[i] = methodParamObj;
            i ++;
        }
        // 调用
        Method invocableMethod = handlerMethod.getMethod();
        Object objResult = invocableMethod.invoke(handlerMethod.getBean(), methodParamObjs);
        response.setContentType("text/html; charset=UTF-8");
        response.getWriter().append(objResult.toString());
    }

    public WebBindingInitializer getWebBindingInitializer() {
        return webBindingInitializer;
    }

    public void setWebBindingInitializer(WebBindingInitializer webBindingInitializer) {
        this.webBindingInitializer = webBindingInitializer;
    }

}
