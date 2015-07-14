package com.blocksberg.vsc;

import com.blocksberg.vsc.manufacturing.InstanceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.InvocationTargetException;

/**
 * Serializes a complete instance to disk.
 *
 * @author Justin Heesemann
 */
public class VersionedSerializer {
    private static final Logger LOGGER = LoggerFactory.getLogger(VersionedSerializer.class);
    private final InstanceFactory instanceFactory;
    private boolean enforceAnnotation = false;
    private File outputDirectory;
    private ClassLoader classLoader;
    private final String annotationClassName;

    public VersionedSerializer(File outputDirectory, ClassLoader classLoader, String annotationClassName) {
        this.outputDirectory = outputDirectory;
        this.classLoader = classLoader;
        this.annotationClassName = annotationClassName;
        instanceFactory = new InstanceFactory();

    }

    public void serialize(Class classToSerialize)
            throws IOException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException,
            InvocationTargetException {
        final Serializable objectToSerialize = instanceFactory.createInstance(classToSerialize);
        LOGGER.debug("serializing : " + objectToSerialize);
        final String serialFileName = FilenameFactory.createFilename(enforceAnnotation, classLoader, classToSerialize
                .getCanonicalName(), annotationClassName);
        serializeToFile(objectToSerialize, serialFileName);
    }

    public void serializeClass(Class classToSerialize)
            throws IOException, ClassNotFoundException, NoSuchMethodException, InvocationTargetException,
            IllegalAccessException {
        final String filename = FilenameFactory.createFilename(enforceAnnotation, classLoader, classToSerialize
                .getCanonicalName(), annotationClassName);
        serializeToFile(classToSerialize, filename);
    }

    private Serializable manufactureInstance(Class classToSerialize) {
        return instanceFactory.createInstance(classToSerialize);
    }

    private void serializeToFile(Serializable objectToSerialize, String serialFileName) throws IOException {
        final File serialFile = new File(outputDirectory, serialFileName);
        if (serialFile.exists()) {
            return;
        }
        final FileOutputStream fileOutputStream = new FileOutputStream(serialFile);
        try {
            new ObjectOutputStream(fileOutputStream).writeObject(objectToSerialize);
        } finally {
            fileOutputStream.close();
        }
    }
}
