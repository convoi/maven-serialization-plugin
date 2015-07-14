package com.blocksberg.vsc;

import com.blocksberg.vsc.markers.VersionedSerialized;
import com.blocksberg.vsc.testmodel.bad.FooNotYetSerialized;
import com.blocksberg.vsc.testmodel.good.Foo;
import org.junit.Ignore;
import org.junit.Test;

import javax.tools.ToolProvider;
import java.io.File;
import java.io.IOException;
import java.io.ObjectStreamClass;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * @author Justin Heesemann
 */
public class VersionedDeserializerTest {

    @Test
    @Ignore("fingerprinting experiment")
    public void testSerialVersionUID() throws IllegalAccessException, NoSuchFieldException {
        final ObjectStreamClass lookup = ObjectStreamClass.lookup(Foo.class);
        final Field suid = ObjectStreamClass.class.getDeclaredField("suid");
        suid.setAccessible(true);
        suid.set(lookup, null);
        final long serialVersionUID = lookup.getSerialVersionUID();
        assertThat(serialVersionUID, is(-1118334358600247778L));
    }

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
        return new VersionedDeserializer(outputDirectory, this.getClass().getClassLoader(),
                VersionedSerialized.class.getCanonicalName());
    }
}