package com.blocksberg.vsc;

import org.apache.commons.io.input.ClassLoaderObjectInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.lang.reflect.InvocationTargetException;

/**
 * @author Justin Heesemann
 */
public class VersionedDeserializer {
    private static final Logger LOGGER = LoggerFactory.getLogger(VersionedDeserializer.class);
    private File outputDirectory;
    private ClassLoader classLoader;
    private final String annotationClassName;

    public VersionedDeserializer(File outputDirectory, ClassLoader classLoader, String annotationClassName) {

        this.outputDirectory = outputDirectory;
        this.classLoader = classLoader;
        this.annotationClassName = annotationClassName;
    }

    public void deserialize(String className)
            throws IOException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException,
            InvocationTargetException {
        final Class<?> aClass = classLoader.loadClass(className);
        final String filename = FilenameFactory.createFilename(true, classLoader, className, annotationClassName);
        final File file = new File(outputDirectory, filename);
        if (!file.exists()) {
            return;
        }
        deserializeClassFromFile(aClass, file);
    }

    private void deserializeClassFromFile(Class<?> aClass, File file) throws IOException, ClassNotFoundException {
        final FileInputStream in = new FileInputStream(file);
        try {
            LOGGER.info("trying to deserialize:" + file + " as " + aClass.getCanonicalName());
            final ClassLoaderObjectInputStream objectInputStream =
                    new ClassLoaderObjectInputStream(classLoader, in);
            final Object o = objectInputStream.readObject();
            LOGGER.info("deserialized object: " + o);
            if (!o.getClass().equals(aClass)) {
                LOGGER.warn("expected " + aClass.getCanonicalName() + ", but was " + o.getClass().getCanonicalName());
                throw new InvalidClassException(o.getClass().getCanonicalName(),
                        "expected class " + aClass.getCanonicalName() + ", but was " + o.getClass().getCanonicalName());
            }
        } finally {
            in.close();
        }
    }
}
