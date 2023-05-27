package src.com.minis.test;

import src.com.minis.beans.BeansException;
import src.com.minis.context.ClassPathXmlApplicationContext;

public class Test1 {
    public static void main(String[] args) {
        ClassPathXmlApplicationContext ctx = new ClassPathXmlApplicationContext("beans.xml");
        AService aService = null;
        try {
            aService = (AService) ctx.getBean("aService");
        } catch (BeansException e) {
            throw new RuntimeException(e);
        }
        aService.sayHello();
    }
}
