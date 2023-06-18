package com.minis.beans.factory.support;

import com.minis.beans.BeansException;
import com.minis.beans.factory.FactoryBean;

public class FactoryBeanRegistrySupport extends DefaultSingletonBeanRegistry{
    protected Class<?> getTypeForFactoryBean(final FactoryBean<?> factoryBean) {
        return factoryBean.getObjectType();
    }
    //从factory bean中获取内部包含的对象
    protected Object getObjectFromFactoryBean(FactoryBean<?> factory, String beanName){
        Object object = doGetObjectFromFactoryBean(factory, beanName);
        try {
            object = postProcessObjectFromFactoryBean(object, beanName);
        } catch (BeansException e) {
            throw new RuntimeException(e);
        }
        return object;
    }

    private Object doGetObjectFromFactoryBean(FactoryBean<?> factory, String beanName) {
        Object object = null;
        try {
            object = factory.getObject();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return object;
    }

    private Object postProcessObjectFromFactoryBean(Object object, String beanName) throws BeansException {
        return object;
    }

    protected FactoryBean<?> getFactoryBean(String beanName, Object beanInstance) throws BeansException {
        if (!(beanInstance instanceof FactoryBean)) {
            throw new BeansException(
                    "Bean instance of type [" + beanInstance.getClass() + "] is not a FactoryBean");
        }
        return (FactoryBean<?>) beanInstance;
    }


}
