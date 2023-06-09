package com.minis.beans.factory.config;

import com.minis.beans.PropertyValues;

public class BeanDefinition {
    String SCOPE_SINGLETON = "singleton"; //单例模式
    String SCOPE_PROTOTYPE = "prototype"; //原型模式
    // 表示 Bean 要不要在加载的时候初始化
    private boolean lazyInit = true;// 设置为false时，循环依赖会报错！XmlBeanDefinitionReader注册一个DefinitionBean后就会调用getBean方法，在循环依赖中注入其他bean时就会报错（原因：没有其他bean的DefinitionBean因此会空指针）。
    // 记录 Bean 之间的依赖关系
    private String[] dependsOn;
    private ConstructorArgumentValues constructorArgumentValues;
    private PropertyValues propertyValues;
    // 一个 Bean 构造好并实例化之后是否要让框架调用初始化方法
    private String initMethodName;
    // 修改对所有线程都可见，避免了多线程之间的数据不一致性问题
    private volatile Object beanClass;
    private String id;
    private String className;
    private String scope = SCOPE_SINGLETON;
    public BeanDefinition(String id, String className){
        this.id = id;
        this.className = className;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public boolean hasBeanClass() {
        return (this.beanClass instanceof Class);
    }

    public void setBeanClass(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    public Class<?> getBeanClass(){

        return (Class<?>) this.beanClass;
    }
    public void setScope(String scope) {
        this.scope = scope;
    }
    public String getScope() {
        return this.scope;
    }
    public boolean isSingleton() {
        return SCOPE_SINGLETON.equals(scope);
    }
    public boolean isPrototype() {
        return SCOPE_PROTOTYPE.equals(scope);
    }
    public void setLazyInit(boolean lazyInit) {
        this.lazyInit = lazyInit;
    }
    public boolean isLazyInit() {
        return this.lazyInit;
    }
    public void setDependsOn(String... dependsOn) {
        this.dependsOn = dependsOn;
    }
    public String[] getDependsOn() {
        return this.dependsOn;
    }
    public void setConstructorArgumentValues(ConstructorArgumentValues constructorArgumentValues) {
        this.constructorArgumentValues =
                (constructorArgumentValues != null ? constructorArgumentValues : new ConstructorArgumentValues());
    }
    public ConstructorArgumentValues getConstructorArgumentValues() {
        return this.constructorArgumentValues;
    }
    public boolean hasConstructorArgumentValues() {
        return !this.constructorArgumentValues.isEmpty();
    }
    public void setPropertyValues(PropertyValues propertyValues) {
        this.propertyValues = (propertyValues != null ? propertyValues : new PropertyValues());
    }
    public PropertyValues getPropertyValues() {
        return this.propertyValues;
    }
    public void setInitMethodName(String initMethodName) {
        this.initMethodName = initMethodName;
    }
    public String getInitMethodName() {
        return this.initMethodName;
    }

}
