package com.blocksberg.vsc;

import com.blocksberg.vsc.testmodel.bad.FooNotYetSerialized;
import com.blocksberg.vsc.testmodel.good.Foo;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author jh
 */
public class VersionedDeserializerTest {

    @Test
    public void testDeserialize() throws Exception {
        final VersionedDeserializer versionedDeserializer = getVersionedDeserializer();
        versionedDeserializer.deserialize(Foo.class.getCanonicalName());
    }

    @Test
    public void testDeserializeNotExistingFile()
            throws URISyntaxException, IOException, ClassNotFoundException, NoSuchMethodException,
            IllegalAccessException, InvocationTargetException {
        getVersionedDeserializer().deserialize(FooNotYetSerialized.class.getCanonicalName());
    }

    private VersionedDeserializer getVersionedDeserializer() throws URISyntaxException {
        final URI resource = getClass().getResource("/outputDirectory").toURI();
        final File outputDirectory = new File(resource);
        return new VersionedDeserializer(outputDirectory, this.getClass().getClassLoader());
    }
}