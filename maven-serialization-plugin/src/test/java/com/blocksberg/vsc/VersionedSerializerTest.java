package com.blocksberg.vsc;

import com.blocksberg.vsc.markers.VersionedSerialized;
import com.blocksberg.vsc.testmodel.good.Foo;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;

import static org.junit.Assert.*;

public class VersionedSerializerTest {

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    @Test
    public void testSerializeClass() throws Exception {
        final VersionedSerializer versionedSerializer =
                new VersionedSerializer(temporaryFolder.newFolder(), this.getClass().getClassLoader(),
                        VersionedSerialized.class.getCanonicalName());
        versionedSerializer.serializeClass(Foo.class);
        System.out.println("test");
    }
}