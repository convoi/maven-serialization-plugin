package com.blocksberg.vsc;

import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class FingerprintFileTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void createFingerprintFile() {
        final FingerprintFile file = new FingerprintFile(Object.class.getCanonicalName(), 123);
        Assert.assertEquals(Object.class.getCanonicalName(), file.getClassName());
        Assert.assertEquals(123, file.getFingerprint());
        Assert.assertEquals("serial", FingerprintFile.SUFFIX);
        Assert.assertEquals("java.lang.Object.123.serial", file.getFileName());
    }

    @Test
    public void createFingerprintFileFromFilename() {
        final FingerprintFile file = new FingerprintFile("java.lang.Object.123.serial");
        Assert.assertEquals(Object.class.getCanonicalName(), file.getClassName());
        Assert.assertEquals(123, file.getFingerprint());
        Assert.assertEquals("serial", FingerprintFile.SUFFIX);
        Assert.assertEquals("java.lang.Object.123.serial", file.getFileName());
    }

    @Test
    public void createFingerprintFileFromFilenameWithInvalidSuffix() {
        thrown.expect(IllegalArgumentException.class);
        new FingerprintFile("java.lang.Object.123.serials");
    }

    @Test
    public void createFingerprintFileFromFilenameWithNegativeFingerprint() {
        final FingerprintFile file = new FingerprintFile("java.lang.Object.-123.serial");
        Assert.assertEquals(Object.class.getCanonicalName(), file.getClassName());
        Assert.assertEquals(-123, file.getFingerprint());
        Assert.assertEquals("serial", FingerprintFile.SUFFIX);
        Assert.assertEquals("java.lang.Object.-123.serial", file.getFileName());
    }

    @Test
    public void createFingerprintFileFromFilenameWithInvalidFingerprint() {
        thrown.expect(IllegalArgumentException.class);
        new FingerprintFile("java.lang.Object.123a.serial");
    }

}
