package org.fkjava;

import org.fkjava.beans.UserService;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class Test {
    public static void main(String[] args) {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(Config.class);
        UserService us = ctx.getBean(UserService.class);
        us.test();
    }
}
