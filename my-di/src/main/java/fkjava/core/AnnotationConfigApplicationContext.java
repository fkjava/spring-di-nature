package fkjava.core;

import fkjava.annotations.Autowired;
import fkjava.annotations.Component;
import fkjava.annotations.ComponentScan;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

public class AnnotationConfigApplicationContext implements ApplicationContext {

    // 用Bean的类型作为Key存储Bean的定义信息
    private final Map<Class, BeanDefinition> definitionMap = new HashMap<>();

    public AnnotationConfigApplicationContext(Class<?> configClass) {
        // 解析Bean的定义，包括@Service、@Component、@Configuration等注解的类，都是一个Bean
        buildConfig(configClass);

        // 解决Bean的依赖问题
        // 此时不需要递归，只需要根据容器里面所有的Bean定义去解析自己需要的依赖即可。
        postConfig();
    }

    private void buildConfig(Class<?> configClass) {

        // 递归类上面的所有注解，如果发现有@ComponentScan注解，则需要扫描包里面的类，并递归配置
        Set<Annotation> annotations = revolveAnnotations(configClass);


        // 判断是否有@Component注解，如果有则加入容器里面
        Component component = getAnnotation(Component.class, annotations);
        if (component != null) {
            BeanDefinition def = new BeanDefinition();
            def.setBeanClass(configClass);
            def.setBeanName(configClass.getSimpleName());
            definitionMap.put(configClass, def);
        }

        ComponentScan cs = getAnnotation(ComponentScan.class, annotations);
        if (cs != null) {
            // 创建包扫描器
            PackageScanner ps = new PackageScanner();
            Set<Class<?>> classes = ps.scan(cs.basePackages());
            for (Class<?> c : classes) {
                if (!definitionMap.containsKey(c)) {
                    // 递归扫描
                    buildConfig(c);
                }
            }
        }
    }

    private <T> T getAnnotation(Class<T> annotationClass, Set<Annotation> annotations) {
        for (Annotation a : annotations) {
            if (annotationClass.isAssignableFrom(a.getClass())) {
                return (T) a;
            }
        }
        return null;
    }

    private Set<Annotation> revolveAnnotations(Class<?> configClass) {
        Annotation[] as = configClass.getAnnotations();
        Set<Annotation> annotations = revolveAnnotations(as);
        return annotations;
    }

    private Set<Annotation> revolveAnnotations(Annotation[] as) {
        Set<Annotation> annotations = new HashSet<>();
        for (Annotation a : as) {
            // 不是java.lang开头的注解，全部扫描出来
            if (!a.annotationType().getName().startsWith("java.lang")) {
                annotations.add(a);
                Set<Annotation> list = revolveAnnotations(a.annotationType().getAnnotations());
                annotations.addAll(list);
            }
        }
        return annotations;
    }


    private void postConfig() {
        // 遍历所有的Bean定义
        definitionMap.forEach((beanClass, definition) -> {

            // 获取类中所有的@Autowired注解
            List<Field> fields = getFields(beanClass);
            // 根据注解需要的数据类型，找到对应的BeanDefinition
            fields.forEach(field -> {
                Class requiredType = field.getType();
                BeanDefinition def = definitionMap.get(requiredType);
                definition.getDependencies().put(field, def);
            });
        });
    }

    private List<Field> getFields(Class beanClass) {
        List<Field> allFields = new LinkedList<>();
        Field[] fields = beanClass.getDeclaredFields();
        for (Field f : fields) {
            // 有@Autowired注解才要加入进来，表示要自动注入的
            if (f.getAnnotation(Autowired.class) != null) {
                allFields.add(f);
            }
        }
        Class superClass = beanClass.getSuperclass();
        if (superClass != null && superClass != Object.class) {
            // 不是Object类，递归！
            List<Field> superClassFields = getFields(superClass);
            allFields.addAll(superClassFields);
        }
        return allFields;
    }

    @Override
    public <T> T getBean(Class<T> cla) {
        BeanDefinition def = definitionMap.get(cla);
        if (!def.isInit()) {
            createBean(def);
        }
        return (T) def.getInstance();
    }

    private void createBean(BeanDefinition def) {
        // 线程安全的一种单例模式实现
        if (def.getInstance() == null) {
            synchronized (def) {
                // 未实例化的时候，创建实例
                if (def.getInstance() == null) {
                    try {
                        // 创建实例
                        Object instance = def.getBeanClass().getConstructor().newInstance();
                        def.setInstance(instance);

                        // 处理创建对象之后的操作，比如依赖注入
                        postCreated(def);

                        // 设置为已经初始化完成
                        def.setInit(true);
                    } catch (InstantiationException
                            | IllegalAccessException
                            | InvocationTargetException
                            | NoSuchMethodException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void postCreated(BeanDefinition def) {
        // 处理依赖关系
        def.getDependencies().forEach(((field, beanDefinition) -> {
            Class type = field.getType();
            Object x = getBean(type);
            try {
                // 修改访问权限
                field.setAccessible(true);
                field.set(def.getInstance(), x);
            } catch (Exception e) {
                throw new RuntimeException("无法实现自动注入：" + e.getLocalizedMessage(), e);
            }
        }));
    }
}
