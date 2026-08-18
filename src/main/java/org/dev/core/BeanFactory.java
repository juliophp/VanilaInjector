package org.dev.core;

import java.lang.reflect.InvocationTargetException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class BeanFactory {
    public final Map<Class<?>, BeanDefinition> definitions;
    public final Set<Class<?>> underConstruction = new HashSet<>();

    public BeanFactory(Map<Class<?>, BeanDefinition> definitions) {
        this.definitions = definitions;
    }

    public Object getBean(Class<?> beanClass) {

        //get bean from definitions otherwise throw not found exception
        BeanDefinition definition = definitions.get(beanClass);
        if (definition == null) throw new IllegalStateException("Could not find bean definition for " + beanClass.getName());

        //return running instance if it already exists
        if (definition.getInstance() != null) return definition.getInstance();

        //add beanClass to underConstruction and throw CircularException if it already exists
        if (underConstruction.contains(beanClass)) throw new IllegalStateException("Bean has already been instantiated");

        //run getBean recursively on each of the dependencies of the bean
        Object[] args = definition.getDependencies().stream().map(this::getBean).toArray();

        Object newInstanceFromConstructorWithDependencies = null;
        try {
            newInstanceFromConstructorWithDependencies = definition.getConstructor().newInstance(args);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        underConstruction.remove(beanClass);
        definition.setInstance(newInstanceFromConstructorWithDependencies);
        return definition;
    }




}
