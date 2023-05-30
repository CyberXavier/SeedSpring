package com.minis.beans.factory;

import com.minis.beans.BeansException;

public interface BeanFactory {
    /**
     * 获取bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    Object getBean(String beanName) throws BeansException;

    /**
     * 判断BeanFactory是否包含名为name的bean
     * @param name
     * @return
     */
    boolean containsBean(String name);

    /**
     * 注册bean
     * @param beanName
     * @param obj beanName对应的Bean的信息
     */
    void registerBean(String beanName, Object obj);

    /**
     * 是否为单例模式
     * @param name
     * @return
     */
    boolean isSingleton(String name);

    /**
     * 是否为原型模式
     * @param name
     * @return
     */
    boolean isPrototype(String name);

    /**
     * 获取bean的类型
     * @param name
     * @return
     */
    Class<?> getType(String name);

//    void registerBeanDefinition(BeanDefinition beanDefinition);
}
