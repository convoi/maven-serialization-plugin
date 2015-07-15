package com.blocksberg.vsc;

import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;

public class FingerprintChangeManagerTest {

    private FingerprintChangeManager manager;
    final FingerprintFile first = new FingerprintFile("com.classes.First.123.serial");
    final FingerprintFile firstChanged = new FingerprintFile("com.classes.First.124.serial");
    final FingerprintFile remains = new FingerprintFile("com.classes.Second.123.serial");
    final FingerprintFile deleted = new FingerprintFile("com.classes.Third.123.serial");
    final FingerprintFile added = new FingerprintFile("com.classes.Fourth.123.serial");

    @Before
    public void setUp() {
        manager =
                new FingerprintChangeManager(createSet(first, remains, deleted),
                        createSet(firstChanged, remains, added));
    }

    @Test
    public void deleted() {
        assertEquals(getClassNames(deleted), manager.getDeletedClassNames());
    }

    @Test
    public void changed() {
        assertEquals(getClassNames(first), manager.getChangedClassNames());
    }

    @Test
    public void added() {
        assertEquals(getClassNames(added), manager.getAddedClassNames());
    }

    @Test
    public void remaining() {
        assertEquals(getClassNames(remains), manager.getRemainingClassNames());
    }

    private Set<String> getClassNames(final FingerprintFile... fingerprintFiles) {
        final Set<String> result = new HashSet<String>();
        for (final FingerprintFile file : fingerprintFiles) {
            result.add(file.getClassName());
        }
        return result;
    }

    private Set<FingerprintFile> createSet(FingerprintFile... files) {
        Set<FingerprintFile> result = new HashSet<FingerprintFile>();
        for (FingerprintFile file : files) {
            result.add(file);
        }
        return result;
    }

}
