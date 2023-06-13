package com.minis.web.context;

import com.minis.context.ApplicationContext;

import javax.servlet.ServletContext;

public interface WebApplicationContext extends ApplicationContext {
    String ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE = WebApplicationContext.class.getName() + ".ROOT";
    ServletContext getServletContext();
    // 这个上下文接口指向了 Servlet 容器本身的上下文 ServletContext
    void setServletContext(ServletContext servletContext);
}
