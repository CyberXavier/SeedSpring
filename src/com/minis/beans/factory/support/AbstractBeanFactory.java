package com.minis.beans.factory.support;

import com.minis.beans.BeansException;
import com.minis.beans.PropertyValue;
import com.minis.beans.PropertyValues;
import com.minis.beans.factory.BeanFactory;
import com.minis.beans.factory.BeanFactoryAware;
import com.minis.beans.factory.FactoryBean;
import com.minis.beans.factory.config.BeanDefinition;
import com.minis.beans.factory.config.ConfigurableBeanFactory;
import com.minis.beans.factory.config.ConstructorArgumentValue;
import com.minis.beans.factory.config.ConstructorArgumentValues;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public abstract class AbstractBeanFactory extends FactoryBeanRegistrySupport implements ConfigurableBeanFactory,BeanDefinitionRegistry {
    protected Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>(256);
    protected List<String> beanDefinitionNames = new ArrayList<>();
    private final Map<String, Object> earlySingletonObjects = new HashMap<String, Object>(16);

    public AbstractBeanFactory(){}

    public void refresh() {
        for (String beanName : beanDefinitionNames) {
            try {
                getBean(beanName);
            } catch (BeansException e) {
                e.printStackTrace();
            }
        }
    }

    public Object getBean(String beanName) throws BeansException{
        Object singleton = this.getSingleton(beanName);

        if (singleton == null) {
            singleton = this.earlySingletonObjects.get(beanName);
            if (singleton == null) {
                System.out.println("get bean null -------------- " + beanName);
                BeanDefinition bd = beanDefinitionMap.get(beanName);
                // 在webApplication环境中没有当前bean的定义，就去parentWebApplication找
                if (bd != null) {
                    singleton=createBean(bd);

                    this.registerBean(beanName, singleton);
                    // ProxyFactoryBean需要设置BeanFactory
                    if (singleton instanceof BeanFactoryAware) {
                        ((BeanFactoryAware) singleton).setBeanFactory(this);
                    }

                    //beanpostprocessor
                    //step 1 : postProcessBeforeInitialization
                    singleton = applyBeanPostProcessorsBeforeInitialization(singleton, beanName);

                    //step 2 : init-method
                    if (bd.getInitMethodName() != null && !bd.getInitMethodName().equals("")) {
                        invokeInitMethod(bd, singleton);
                    }

                    //step 3 : postProcessAfterInitialization
                    applyBeanPostProcessorsAfterInitialization(singleton, beanName);

                    //将代理类放入IOC中，否则Autowired注入的将会是非代理类
                    this.removeSingleton(beanName);
                    this.registerBean(beanName, singleton);

                }else {
                    return null;
                }
            }

        } else {
            System.out.println("bean exist -------------- " + beanName + "----------------"+singleton);
        }

        // process Factory bean
        if (singleton instanceof FactoryBean) {
            System.out.println("factory bean -------------- " + beanName + "----------------"+singleton);
            return this.getObjectForBeanInstance(singleton, beanName);
        }else {
            System.out.println("normal bean -------------- " + beanName + "----------------"+singleton);
        }

        return singleton;
    }

    private void invokeInitMethod(BeanDefinition bd, Object obj) {
        Class<?> clz = obj.getClass();
        Method method = null;
        try {
            method = clz.getMethod(bd.getInitMethodName());
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        try {
            method.invoke(obj);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean containsBean(String name) {
        return containsSingleton(name);
    }

    public void registerBean(String beanName, Object obj) {
        this.registerSingleton(beanName, obj);

    }

    @Override
    public void registerBeanDefinition(String name, BeanDefinition bd) {
        this.beanDefinitionMap.put(name,bd);
        this.beanDefinitionNames.add(name);
        if (!bd.isLazyInit()) {
            try {
                getBean(name);
            } catch (BeansException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void removeBeanDefinition(String name) {
        this.beanDefinitionMap.remove(name);
        this.beanDefinitionNames.remove(name);
        this.removeSingleton(name);

    }

    @Override
    public BeanDefinition getBeanDefinition(String name) {
        return this.beanDefinitionMap.get(name);
    }

    @Override
    public boolean containsBeanDefinition(String name) {
        return this.beanDefinitionMap.containsKey(name);
    }

    @Override
    public boolean isSingleton(String name) {
        return this.beanDefinitionMap.get(name).isSingleton();
    }

    @Override
    public boolean isPrototype(String name) {
        return this.beanDefinitionMap.get(name).isPrototype();
    }

    @Override
    public Class<?> getType(String name) {
        return this.beanDefinitionMap.get(name).getClass();
    }

    private Object createBean(BeanDefinition bd) {
        Class<?> clz = null;
        Object obj = doCreateBean(bd);

        this.earlySingletonObjects.put(bd.getId(), obj);

        try {
            clz = Class.forName(bd.getClassName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        populateBean(bd, clz, obj);

        return obj;
    }

    private Object doCreateBean(BeanDefinition bd) {
        Class<?> clz = null;
        Object obj = null;
        Constructor<?> con = null;

        try {
            clz = Class.forName(bd.getClassName());

            //handle constructor
            ConstructorArgumentValues argumentValues = bd.getConstructorArgumentValues();
            if (argumentValues != null && !argumentValues.isEmpty()) {
                Class<?>[] paramTypes = new Class<?>[argumentValues.getArgumentCount()];
                Object[] paramValues =   new Object[argumentValues.getArgumentCount()];
                for (int i=0; i<argumentValues.getArgumentCount(); i++) {
                    ConstructorArgumentValue argumentValue = argumentValues.getIndexedArgumentValue(i);
                    if ("String".equals(argumentValue.getType()) || "java.lang.String".equals(argumentValue.getType())) {
                        paramTypes[i] = String.class;
                        paramValues[i] = argumentValue.getValue();
                    }
                    else if ("Integer".equals(argumentValue.getType()) || "java.lang.Integer".equals(argumentValue.getType())) {
                        paramTypes[i] = Integer.class;
                        paramValues[i] = Integer.valueOf((String) argumentValue.getValue());
                    }
                    else if ("int".equals(argumentValue.getType())) {
                        paramTypes[i] = int.class;
                        paramValues[i] = Integer.valueOf((String) argumentValue.getValue()).intValue();
                    }
                    else {
                        paramTypes[i] = String.class;
                        paramValues[i] = argumentValue.getValue();
                    }
                }
                try {
                    con = clz.getConstructor(paramTypes);
                    obj = con.newInstance(paramValues);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (SecurityException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
            else {
                obj = clz.newInstance();
            }

        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println(bd.getId() + " bean created. " + bd.getClassName() + " : " + obj.toString());

        return obj;

    }

    private void populateBean(BeanDefinition bd, Class<?> clz, Object obj) {
        handleProperties(bd, clz, obj);
    }

    private void handleProperties(BeanDefinition bd, Class<?> clz, Object obj) {
        //handle properties
        System.out.println("handle properties for bean : " + bd.getId());
        PropertyValues propertyValues = bd.getPropertyValues();
        if (propertyValues != null && !propertyValues.isEmpty()) {
            for (int i=0; i<propertyValues.size(); i++) {
                PropertyValue propertyValue = propertyValues.getPropertyValueList().get(i);
                String pName = propertyValue.getName();
                String pType = propertyValue.getType();
                Object pValue = propertyValue.getValue();
                boolean isRef = propertyValue.getIsRef();
                Class<?>[] paramTypes = new Class<?>[1];
                Object[] paramValues = new Object[1];
                if (!isRef) {
                    parameterTypesValuesSetter(paramTypes, paramValues, pType, pValue);
                }
                else { //is ref, create the dependent beans
                    try {
                        paramTypes[0] = Class.forName(pType);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                    try {
                        paramValues[0] = getBean((String)pValue);
                    } catch (BeansException e) {
                        e.printStackTrace();
                    }
                }

                String methodName = "set" + pName.substring(0,1).toUpperCase() + pName.substring(1);

                Method method = null;
                try {
                    method = clz.getMethod(methodName, paramTypes);
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (SecurityException e) {
                    e.printStackTrace();
                }
                try {
                    method.invoke(obj, paramValues);
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }


            }
        }

    }

    private void parameterTypesValuesSetter(Class<?>[] paramTypes, Object[] paramValues, String pType, Object pValue){
        if ("String".equals(pType) || "java.lang.String".equals(pType)) {
            paramTypes[0] = String.class;
            paramValues[0] = pValue;
        }
        else if ("Integer".equals(pType) || "java.lang.Integer".equals(pType)) {
            paramTypes[0] = Integer.class;
            paramValues[0] = Integer.parseInt((String) pValue);
        }
        else if ("int".equals(pType)) {
            paramTypes[0] = int.class;
            paramValues[0] = Integer.parseInt((String) pValue);
        }
        else if ("Long".equals(pType) || "java.lang.Long".equals(pType)) {
            paramTypes[0] = Long.class;
            paramValues[0] = Long.parseLong((String) pValue);
        }
        else if ("long".equals(pType)) {
            paramTypes[0] = long.class;
            paramValues[0] = Long.parseLong((String) pValue);
        }
        else if ("Double".equals(pType) || "java.lang.Double".equals(pType)) {
            paramTypes[0] = Double.class;
            paramValues[0] = Double.parseDouble((String) pValue);
        }
        else if ("double".equals(pType)) {
            paramTypes[0] = double.class;
            paramValues[0] = Double.parseDouble((String) pValue);
        }
        else if ("Float".equals(pType) || "java.lang.Float".equals(pType)) {
            paramTypes[0] = Float.class;
            paramValues[0] = Float.parseFloat((String) pValue);
        }
        else if ("float".equals(pType)) {
            paramTypes[0] = float.class;
            paramValues[0] = Float.parseFloat((String) pValue);
        }
        else if ("Boolean".equals(pType) || "java.lang.Boolean".equals(pType)) {
            paramTypes[0] = Boolean.class;
            paramValues[0] = Boolean.parseBoolean((String) pValue);
        }
        else if ("boolean".equals(pType)) {
            paramTypes[0] = boolean.class;
            paramValues[0] = Boolean.parseBoolean((String) pValue);
        }
        else {
            paramTypes[0] = String.class;
            paramValues[0] = pValue;
        }
    }

    private Object getObjectForBeanInstance(Object beanInstance, String beanName) {
        // Now we have the bean instance, which may be a normal bean or a FactoryBean.
        if (!(beanInstance instanceof FactoryBean)) {
            return beanInstance;
        }

        Object object = null;
        FactoryBean<?> factory = null;

        try {
            factory = getFactoryBean(beanName, beanInstance);
        } catch (BeansException e) {
            throw new RuntimeException(e);
        }

        object = getObjectFromFactoryBean(factory, beanName);
        return object;
    }

    abstract public Object applyBeanPostProcessorsBeforeInitialization(Object existingBean, String beanName)
            throws BeansException;

    abstract public Object applyBeanPostProcessorsAfterInitialization(Object existingBean, String beanName)
            throws BeansException;

}
