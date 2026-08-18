package org.dev.core;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class ApplicationContext {
    public  final BeanFactory beanFactory;

    public ApplicationContext(String basePackage) {
        ClasspathScanner classpathScanner = new ClasspathScanner();
        Set<Class<?>> classes = classpathScanner.findComponents(basePackage);
        Map<Class<?>,  BeanDefinition> definitionMap = new HashMap<>();

        for (Class<?> clazz : classes) {
            definitionMap.put(clazz, buildDefinition(clazz));
        }
        this.beanFactory = new BeanFactory(definitionMap);

    }

    public <T> T getBean(Class<T> clazz) {
       return clazz.cast(beanFactory.getBean(clazz));
    }

    private BeanDefinition buildDefinition(Class<?> clazz) {
        Constructor<?> classConstructors = findConstructor(clazz);

        List<Class<?>> dependencies = List.of(classConstructors.getParameterTypes());

        return new BeanDefinition(clazz, classConstructors, dependencies);
    }

    private Constructor<?> findConstructor(Class<?> clazz) {
        Constructor<?>[] constructors = clazz.getDeclaredConstructors();

        if (constructors.length == 0) throw new IllegalStateException("No constructor found for " + clazz);

        return constructors[0];
    }
}
