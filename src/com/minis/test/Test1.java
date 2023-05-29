package com.minis.test;

import com.minis.beans.BeansException;
import com.minis.context.ClassPathXmlApplicationContext;

public class Test1 {
    public static void main(String[] args) {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("beans.xml");
        AService aService;
        BaseService bs;
        BaseBaseService bbs;
        try {
            aService = (AService) ctx.getBean("aservice");
            aService.sayHello();
            System.out.println("-----------单独输出-----------");
            bs = (BaseService) ctx.getBean("baseservice");
            System.out.println(bs.getName());
            bbs = (BaseBaseService) ctx.getBean("basebaseservice");
            System.out.println(bbs.getName());
        } catch (BeansException e) {
            throw new RuntimeException(e);
        }
        System.out.println("-----------AServiceImpl其他属性输出-----------");
        AServiceImpl a = (AServiceImpl) aService;
        System.out.println(a.getRef1().getName());
        System.out.println(a.getProperty1());
        System.out.println(a.getProperty2());
        System.out.println(a.getName());
        System.out.println(a.getLevel());
        System.out.println(a.getClass());
        System.out.println("-----------BaseService其他属性输出-----------");
        System.out.println(bs.getBbs().getName());
        System.out.println("-----------BaseBaseService其他属性输出-----------");
        System.out.println(bbs.getAs().getName());

    }
}
