package org.dev.core;

import java.lang.reflect.Constructor;
import java.util.List;

public class BeanDefinition {
    public Class<?> clazz;
    public Constructor<?> constructor;
    public List<Class<?>> dependencies;

    private Object instance;

    public BeanDefinition(Class<?> clazz, Constructor<?> constructor, List<Class<?>> dependencies) {
        this.clazz = clazz;
        this.constructor = constructor;
        this.dependencies = dependencies;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public Constructor<?> getConstructor() {
        return constructor;
    }

    public void setConstructor(Constructor<?> constructor) {
        this.constructor = constructor;
    }

    public List<Class<?>> getDependencies() {
        return dependencies;
    }

    public void setDependencies(List<Class<?>> dependencies) {
        this.dependencies = dependencies;
    }

    public Object getInstance() {
        return instance;
    }

    public void setInstance(Object instance) {
        this.instance = instance;
    }
}
