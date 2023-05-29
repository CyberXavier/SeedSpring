package com.minis.beans;

/**
 * 定义管理单例Bean的方法规范
 */
public interface SingletonBeanRegistry {
    /**
     * 单例Bean的注册
     * @param beanName
     * @param singletonObject
     */
    void registerSingleton(String beanName, Object singletonObject);

    /**
     * 单例Bean获取
     * @param beanName
     * @return
     */
    Object getSingleton(String beanName);

    /**
     * 判断是否存在
     * @param beanName
     * @return
     */
    boolean containsSingleton(String beanName);

    /**
     * 获取所有的单例 Bean
     * @return
     */
    String[] getSingletonNames();
}