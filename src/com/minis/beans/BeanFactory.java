package src.com.minis.beans;

import src.com.minis.beans.BeanDefinition;
import src.com.minis.beans.BeansException;

public interface BeanFactory {
    /**
     * 获取bean
     * @param beanName
     * @return
     * @throws BeansException
     */
    Object getBean(String beanName) throws BeansException;

    /**
     * 注册一个 BeanDefinition
     * @param beanDefinition
     */
    void registerBeanDefinition(BeanDefinition beanDefinition);
}
