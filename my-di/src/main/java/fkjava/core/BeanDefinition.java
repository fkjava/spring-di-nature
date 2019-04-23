package fkjava.core;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class BeanDefinition {

    private boolean init = false;
    private Class<?> beanClass;
    private String beanName;
    private Object instance;
    private Map<Field, BeanDefinition> dependencies = new HashMap<>();

    public boolean isInit() {
        return init;
    }

    public void setInit(boolean init) {
        this.init = init;
    }

    public Class<?> getBeanClass() {
        return beanClass;
    }

    public void setBeanClass(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public Object getInstance() {
        return instance;
    }

    public void setInstance(Object instance) {
        this.instance = instance;
    }

    public Map<Field, BeanDefinition> getDependencies() {
        return dependencies;
    }

    public void setDependencies(Map<Field, BeanDefinition> dependencies) {
        this.dependencies = dependencies;
    }
}
