package com.minis.web.method.annotation;

import com.minis.beans.BeansException;
import com.minis.context.ApplicationContext;
import com.minis.context.ApplicationContextAware;
import com.minis.http.converter.HttpMessageConverter;
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
        int i = 0;
        //对调用方法里的每一个参数，处理绑定
        for (Parameter methodParameter : methodParameters) {
            if (methodParameter.getType()!=HttpServletRequest.class && methodParameter.getType()!=HttpServletResponse.class){
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
