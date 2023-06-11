package com.minis.web.servlet;

import com.minis.beans.BeansException;
import com.minis.web.RequestMapping;
import com.minis.web.WebApplicationContext;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

public class RequestMappingHandlerMapping implements HandlerMapping{

    WebApplicationContext wac;
    private final MappingRegistry mappingRegistry = new MappingRegistry();

    public RequestMappingHandlerMapping (WebApplicationContext wac) {
        this.wac = wac;
        initMapping();
    }

    //建立URL与调用方法和实例的映射关系，存储在mappingRegistry中
    protected void initMapping(){
        Class<?> clz;
        Object obj;
        String[] controllerNames = this.wac.getBeanDefinitionNames();
        //扫描WAC中存放的所有bean
        for (String controllerName : controllerNames) {
            try {
                clz = Class.forName(controllerName);
                obj = this.wac.getBean(controllerName);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            } catch (BeansException e) {
                throw new RuntimeException(e);
            }

            Method[] methods = clz.getDeclaredMethods();
            if (methods != null) {
                //检查每一个方法声明
                for (Method method : methods) {
                    boolean isRequestMapping = method.isAnnotationPresent(RequestMapping.class);
                    if (isRequestMapping) {
                        //如果该方法带有@RequestMapping注解,则建立映射关系
                        String methodName = method.getName();
                        String urlMapping = method.getAnnotation(RequestMapping.class).value();
                        this.mappingRegistry.getUrlMappingNames().add(urlMapping);
                        this.mappingRegistry.getMappingObjs().put(urlMapping, obj);
                        this.mappingRegistry.getMappingMethods().put(urlMapping, method);
                        this.mappingRegistry.getMappingMethodNames().put(urlMapping, methodName);
                        this.mappingRegistry.getMappingClasses().put(urlMapping, clz);
                    }
                }
            }
        }

    }

    //根据访问URL查找对应的调用方法
    @Override
    public HandlerMethod getHandler(HttpServletRequest request) throws Exception {
        String sPath = request.getServletPath();
        if (!this.mappingRegistry.getUrlMappingNames().contains(sPath)) {
            return null;
        }

        Method method = this.mappingRegistry.getMappingMethods().get(sPath);
        Object obj = this.mappingRegistry.getMappingObjs().get(sPath);
        Class<?> clz = this.mappingRegistry.getMappingClasses().get(sPath);
        String methodName = this.mappingRegistry.getMappingMethodNames().get(sPath);

        HandlerMethod handlerMethod = new HandlerMethod(method, obj, clz, methodName);
        return handlerMethod;
    }
}
