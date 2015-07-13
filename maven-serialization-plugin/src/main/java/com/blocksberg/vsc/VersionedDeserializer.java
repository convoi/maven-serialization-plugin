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

    private void deserializeClassFromFile(Class<?> expectedClass, File file) throws IOException, ClassNotFoundException {
        final FileInputStream in = new FileInputStream(file);
        try {
            LOGGER.debug("trying to deserialize:" + file + " as " + expectedClass.getCanonicalName());
            final ClassLoaderObjectInputStream objectInputStream =
                    new ClassLoaderObjectInputStream(classLoader, in);
            final Object deserializedObject = objectInputStream.readObject();
            if (!deserializedObject.getClass().equals(expectedClass)) {
                LOGGER.error(createClassMissmatchMessage(expectedClass, deserializedObject));
                throw new InvalidClassException(deserializedObject.getClass().getCanonicalName(),
                        "expected class " + expectedClass.getCanonicalName() + ", "
                                + "but was " + deserializedObject.getClass().getCanonicalName());
            }
        } finally {
            in.close();
        }
    }

    private String createClassMissmatchMessage(Class<?> expectedClass, Object deserializedObject) {
        return "expected " + expectedClass.getCanonicalName() + ", "
                + "but was " + deserializedObject.getClass().getCanonicalName();
    }
}
