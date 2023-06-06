package com.minis.web.servlet;

import com.minis.web.WebApplicationContext;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class RequestMappingHandlerAdapter implements HandlerAdapter{

    WebApplicationContext wac;

    public RequestMappingHandlerAdapter(WebApplicationContext wac){
        this.wac = wac;
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        handleInternal(request, response, (HandlerMethod) handler);
    }

    private void handleInternal(HttpServletRequest request, HttpServletResponse response, HandlerMethod handler) {
        Object obj = handler.getBean();
        Method method = handler.getMethod();
        Object objResult = null;
        try {
            objResult = method.invoke(obj);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        try {
            response.setContentType("text/html; charset=UTF-8");
            response.getWriter().append(objResult.toString());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
