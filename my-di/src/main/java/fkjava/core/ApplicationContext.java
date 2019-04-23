package fkjava.core;

import fkjava.beans.UserService;

public interface ApplicationContext {
    <T> T getBean(Class<T> cla);
}
