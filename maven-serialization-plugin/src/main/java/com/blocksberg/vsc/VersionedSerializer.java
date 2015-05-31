package com.blocksberg.vsc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.jemos.podam.api.DataProviderStrategy;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;
import uk.co.jemos.podam.api.RandomDataProviderStrategy;

import java.io.*;
import java.lang.reflect.InvocationTargetException;

/**
 * Serializes a complete instance to disk.
 *
 * @author Justin Heesemann
 */
public class VersionedSerializer {
    private static final Logger LOGGER = LoggerFactory.getLogger(VersionedSerializer.class);
    private final PodamFactory podamFactory;
    private boolean enforceAnnotation = false;
    private File outputDirectory;
    private ClassLoader classLoader;

    public VersionedSerializer(File outputDirectory, ClassLoader classLoader) {
        this.outputDirectory = outputDirectory;
        this.classLoader = classLoader;
        DataProviderStrategy dataProviderStrategy = RandomDataProviderStrategy.getInstance();
        podamFactory = new PodamFactoryImpl(dataProviderStrategy);
    }

    public void serialize(Class classToSerialize)
            throws IOException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException,
            InvocationTargetException {
        final Serializable objectToSerialize =
                (Serializable) podamFactory.manufacturePojoWithFullData(classToSerialize);
        LOGGER.info("serializing : " + objectToSerialize);
        final String serialFileName = FilenameFactory.createFilename(enforceAnnotation, classLoader, classToSerialize
                .getCanonicalName());
        serializeToFile(objectToSerialize, serialFileName);
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
