package com.minis.test;

import com.minis.beans.BeansException;
import com.minis.context.ClassPathXmlApplicationContext;

public class AutowiredTest {
    public static void main(String[] args) {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("beans.xml");
        BaseService baseservice;
        try {
            baseservice = (BaseService) ctx.getBean("baseservice");
            baseservice.sayHello();
        } catch (BeansException e) {
            throw new RuntimeException(e);
        }
    }
}
