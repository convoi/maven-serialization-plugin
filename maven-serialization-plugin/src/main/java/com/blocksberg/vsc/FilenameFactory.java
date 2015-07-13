package com.blocksberg.vsc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author Justin Heesemann
 */
public class FilenameFactory {
    private static final Logger LOGGER = LoggerFactory.getLogger(FilenameFactory.class);

    public static String createFilename(boolean enforceAnnotation, ClassLoader classLoader, String
            classNameToSerialize, String annotationClassName)
            throws ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        final Class<?> classToSerialize = classLoader.loadClass(classNameToSerialize);
        Annotation versionedSerialized = null;
        for (Annotation annotation : classToSerialize.getAnnotations()) {
            if (annotation.annotationType().getCanonicalName().equals(annotationClassName)) {
                LOGGER.debug("found a VersionedSerialized annotation");
                versionedSerialized = annotation;
            } else {
                LOGGER.trace("found a not handled annotation:" + annotation.getClass().getCanonicalName());
                LOGGER.trace("annotation has type:" + annotation.annotationType());
                LOGGER.trace("annotation has methods:" + annotation.getClass().getMethods());
            }
        }

        if (versionedSerialized != null) {
            return getId(versionedSerialized) + "-" + getVersion(versionedSerialized) + ".serial";
        }
        if (enforceAnnotation) {
            throw new IllegalArgumentException("class " + classToSerialize.getCanonicalName() + " is not annotated");
        }
        return classToSerialize.getCanonicalName();

    }

    private static Integer getVersion(Annotation versionedSerialized)
            throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        final Method idMethod = versionedSerialized.getClass().getMethod("version");
        return (Integer) idMethod.invoke(versionedSerialized);
    }

    private static String getId(Annotation versionedSerialized)
            throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        final Method idMethod = versionedSerialized.getClass().getMethod("id");
        return (String) idMethod.invoke(versionedSerialized);
    }
}
