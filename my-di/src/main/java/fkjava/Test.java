package fkjava;

import fkjava.beans.UserService;
import fkjava.core.AnnotationConfigApplicationContext;
import fkjava.core.ApplicationContext;

public class Test {
    public static void main(String[] args) {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(Config.class);
        UserService us = ctx.getBean(UserService.class);
        us.test();
    }
}
