package com.blocksberg.vsc;

import com.google.common.base.Objects;
import org.apache.commons.io.input.ClassLoaderObjectInputStream;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InvalidClassException;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;

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
            LOGGER.debug("trying to deserialize: {} as {}", file, expectedClass.getCanonicalName());
            final ClassLoaderObjectInputStream objectInputStream =
                    new ClassLoaderObjectInputStream(classLoader, in);
            final Object deserializedObject = objectInputStream.readObject();
                LOGGER.info(ToStringBuilder.reflectionToString(deserializedObject, new RecursiveToStringStyle(8)));
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

    private static class RecursiveToStringStyle extends ToStringStyle {

        private static final int    INFINITE_DEPTH  = -1;

        /**
         * Setting {@link #maxDepth} to 0 will have the same effect as using original {@link #ToStringStyle}: it will
         * print all 1st level values without traversing into them. Setting to 1 will traverse up to 2nd level and so
         * on.
         */
        private int                 maxDepth;

        private int                 depth;

        public RecursiveToStringStyle() {
            this(INFINITE_DEPTH);
        }

        public RecursiveToStringStyle(int maxDepth) {
            setUseShortClassName(true);
            setUseIdentityHashCode(false);

            this.maxDepth = maxDepth;
        }

        @Override
        protected void appendDetail(StringBuffer buffer, String fieldName, Object value) {
            if (value.getClass().getName().startsWith("java.lang.")
                    || (maxDepth != INFINITE_DEPTH && depth >= maxDepth)) {
                buffer.append(value);
            }
            else {
                depth++;
                buffer.append(ReflectionToStringBuilder.toString(value, this));
                depth--;
            }
        }

        // another helpful method
        @Override
        protected void appendDetail(StringBuffer buffer, String fieldName, Collection coll) {
            depth++;
            buffer.append(ReflectionToStringBuilder.toString(coll.toArray(), this, true, true));
            depth--;
        }
    }

}
