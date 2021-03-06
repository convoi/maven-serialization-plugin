package com.blocksberg.vsc;

import com.google.common.base.Predicate;

import org.reflections.Reflections;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Scans all given urls for classes annotated with a given annotation and returns those that belong to one of the given
 * packages (or subpackages of them). It uses the supplied classloader to load those classes.
 * 
 * @author Justin Heesemann
 */
public class AnnotationScanner {
    private final Reflections reflections;
    private static final Logger LOGGER = LoggerFactory.getLogger(AnnotationScanner.class);
    private ClassLoader classLoader;
    private final List<URL> urlsToScan;
    private String annotationName;

    public AnnotationScanner(final Collection<String> packagesToScan, ClassLoader classLoader, List<URL> urlsToScan,
            String annotationName) throws MalformedURLException {
        this.classLoader = classLoader;
        this.urlsToScan = urlsToScan;
        this.annotationName = annotationName;
        final ConfigurationBuilder configurationBuilder = new ConfigurationBuilder();
        configurationBuilder.addScanners(new TypeAnnotationsScanner());
        configurationBuilder.setClassLoaders(new ClassLoader[] {classLoader});
        configurationBuilder.setUrls(urlsToScan);
        configurationBuilder.forPackages(packagesToScan.toArray(new String[packagesToScan.size()]));
        configurationBuilder.filterInputsBy(new Predicate<String>() {
            public boolean apply(String s) {
                for (String packageToScan : packagesToScan) {
                    if (s.startsWith(packageToScan)) {
                        return true;
                    }
                }
                return false;
            }
        });
        reflections = new Reflections(configurationBuilder);

    }

    Class<? extends Annotation> getAnnotationClass(String annotationName) throws ClassNotFoundException {
        final Class<?> annotationClass = classLoader.loadClass(annotationName);
        if (annotationClass.isAnnotation()) {
            return (Class<? extends Annotation>) annotationClass;
        } else {
            throw new IllegalArgumentException("supplied class is not an annotation:" + annotationName);
        }
    }

    public Set<Class<?>> scan() throws ClassNotFoundException {
        final Class<? extends Annotation> annotationClass = getAnnotationClass(annotationName);
        LOGGER.info("scanning:" + urlsToScan + " for classes annotated with:" + annotationClass.getName());
        return reflections.getTypesAnnotatedWith(annotationClass);
    }
}
