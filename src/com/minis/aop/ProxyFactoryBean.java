package com.minis.aop;

import com.minis.beans.BeansException;
import com.minis.beans.factory.BeanFactory;
import com.minis.beans.factory.BeanFactoryAware;
import com.minis.beans.factory.FactoryBean;
import com.minis.utils.ClassUtils;

public class ProxyFactoryBean implements FactoryBean<Object>, BeanFactoryAware {

    private BeanFactory beanFactory;
    private AopProxyFactory aopProxyFactory;

    private String interceptorName; // 指定需要启用什么样的增强

    public void setInterceptorName(String interceptorName) {
        this.interceptorName = interceptorName;
    }

    private Advisor advisor;

    private Object target;

    // 当前线程的上下文类加载器（Thread.currentThread().getContextClassLoader()）作为 ClassLoader 参数传入
    private ClassLoader proxyClassLoader = ClassUtils.getDefaultClassLoader();

    private Object singletonInstance;

    public ProxyFactoryBean() {
        this.aopProxyFactory = new DefaultAopProxyFactory();
    }

    private synchronized void initializeAdvisor(){
        Object advice = null;
        MethodInterceptor mi = null;
        try {
            advice = this.beanFactory.getBean(this.interceptorName);
        } catch (BeansException e) {
            throw new RuntimeException(e);
        }
        if (advice instanceof BeforeAdvice) {
            mi = new MethodBeforeAdviceInterceptor((MethodBeforeAdvice) advice);
        }else if (advice instanceof AfterAdvice) {
            mi = new AfterReturningAdviceInterceptor((AfterReturningAdvice) advice);
        }else if (advice instanceof MethodInterceptor) {
            mi = (MethodInterceptor) advice;
        }

        advisor = new DefaultAdvisor();
        advisor.setMethodInterceptor(mi);
    }

    protected AopProxy createAopProxy() {
        System.out.println("----------createAopProxy for :"+target+"--------");
        return getAopProxyFactory().createAopProxy(target, this.advisor);
    }

    public AopProxyFactory getAopProxyFactory() {
        return aopProxyFactory;
    }

    public void setAopProxyFactory(AopProxyFactory aopProxyFactory) {
        this.aopProxyFactory = aopProxyFactory;
    }

    public Object getTarget() {
        return target;
    }

    public void setTarget(Object target) {
        this.target = target;
    }

    @Override
    public Object getObject() throws Exception { //获取内部对象
        initializeAdvisor();
        return getSingletonInstance();
    }

    private synchronized Object getSingletonInstance() { //获取代理
        if (this.singletonInstance == null) {
            this.singletonInstance = getProxy(createAopProxy());
        }
        System.out.println("----------return proxy for :"+this.singletonInstance+"--------"+this.singletonInstance.getClass());

        return this.singletonInstance;
    }

    //生成代理对象
    protected Object getProxy(AopProxy aopProxy) {
        return aopProxy.getProxy();
    }

    @Override
    public Class<?> getObjectType() {
        return null;
    }

    @Override
    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }
}
