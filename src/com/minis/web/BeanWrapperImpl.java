package com.minis.web;

import com.minis.beans.AbstractPropertyAccessor;
import com.minis.beans.PropertyEditor;
import com.minis.beans.PropertyValue;
import com.minis.beans.PropertyValues;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class BeanWrapperImpl extends AbstractPropertyAccessor {

    Object wrappedObject; // 目标对象

    Class<?> clz;

    PropertyValues pvs; //参数值

    public BeanWrapperImpl(Object obj){
        super(); //注册不同数据类型的参数转换器editor
        this.wrappedObject = obj;
        this.clz = obj.getClass();
    }

    public void setBeanInstance(Object obj){
        this.wrappedObject = obj;
    }

    public Object getWrappedObject(){
        return this.wrappedObject;
    }
    // 绑定参数值
    public void setPropertyValues(PropertyValues pvs){
        if (pvs != null && !pvs.isEmpty()){
            this.pvs = pvs;
            for (PropertyValue pv : this.pvs.getPropertyValues()) {
                this.setPropertyValue(pv);
            }
        }

    }

    // 绑定具体某个参数
    @Override
    public void setPropertyValue(PropertyValue pv) {
        //拿到参数处理器
        BeanPropertyHandler propertyHandler = new BeanPropertyHandler(pv.getName());
        PropertyEditor pe = this.getCustomEditor(propertyHandler.getPropertyClz());
        if (pe == null) {
            pe = this.getDefaultEditors(propertyHandler.getPropertyClz());
        }
        if (pe != null) {
            pe.setAsText((String) pv.getValue());
            propertyHandler.setValue(pe.getValue());
        }
        else {
            propertyHandler.setValue(pe.getValue());
        }
    }

    //一个内部类，用于处理参数，通过getter()和setter()操作属性
    class BeanPropertyHandler {
        Method writeMethod = null;
        Method readMethod = null;
        Class<?> propertyClz = null;
        public Class<?> getPropertyClz(){
            return this.propertyClz;
        }
        public BeanPropertyHandler(String propertyName){
            try {
                // 获取target参数对应的属性及类型
                Field field = clz.getDeclaredField(propertyName);
                propertyClz = field.getType();
                // 获取设置属性的方法，默认约定setXxx()
                this.writeMethod = clz.getDeclaredMethod("set"
                        + propertyName.substring(0,1).toUpperCase()
                        + propertyName.substring(1), propertyClz);
                // 获取读属性的方法，默认约定getXxx()
                this.readMethod = clz.getDeclaredMethod("get"
                        + propertyName.substring(0,1).toUpperCase()
                        + propertyName.substring(1));
            } catch (NoSuchFieldException e) {
                throw new RuntimeException(e);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }

        // 调用getter读属性值
        public Object getValue(){
            Object result = null;
            readMethod.setAccessible(true);
            try {
                result = readMethod.invoke(wrappedObject);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
            return result;
        }

        // 调用setter设置属性值
        public void setValue(Object value){
            writeMethod.setAccessible(true);
            try {
                writeMethod.invoke(wrappedObject, value);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        }

    }
}
