package com.minis.beans.factory.config;

import com.minis.beans.factory.BeanFactory;

/**
 * 维护 Bean 之间的依赖关系以及支持 Bean 处理器也看作一个独立的特性，这个特性定义在 ConfigurableBeanFactory 接口中
 */
public interface ConfigurableBeanFactory extends BeanFactory,SingletonBeanRegistry {

    String SCOPE_SINGLETON = "singleton";
    String SCOPE_PROTOTYPE = "prototype";

    void addBeanPostProcessor(BeanPostProcessor beanPostProcessor);

    int getBeanPostProcessorCount();

    void registerDependentBean(String beanName, String dependentBeanName);

    String[] getDependentBeans(String beanName);

    String[] getDependenciesForBean(String beanName);

}
