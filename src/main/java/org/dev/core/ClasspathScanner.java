package org.dev.core;

import org.dev.annotations.Component;
import org.reflections.Reflections;

import java.util.Set;

public class ClasspathScanner {
    public Set<Class<?>> findComponents(String basePackage) {
        Reflections reflections = new Reflections(basePackage);
        return reflections.getTypesAnnotatedWith(Component.class);
    };
}
