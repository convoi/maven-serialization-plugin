package com.blocksberg.vsc.manufacturing;

import com.blocksberg.vsc.testmodel.hierarchy.SubClass;

import org.junit.Test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class DeepSUIDFingerprintGeneratorTest {

    @Test
    public void computesDifferentFingerprintsForDifferentClasses() throws Exception {
        final DeepSUIDFingerprintGenerator fingerprintGenerator = new DeepSUIDFingerprintGenerator();
        final long testClassFingerprint = fingerprintGenerator.getFingerprint(TestClass.class);
        final long anotherTestClassFingerprint = fingerprintGenerator.getFingerprint(AnotherTestClass.class);
        assertThat(testClassFingerprint, is(not(anotherTestClassFingerprint)));
    }

    @Test
    public void canExcludePackages() throws Exception {
        final DeepSUIDFingerprintGenerator fingerprintGenerator = new DeepSUIDFingerprintGenerator();
        final DeepSUIDFingerprintGenerator fingerprintGeneratorWithExclusion =
                new DeepSUIDFingerprintGenerator("java.lang", "java.util");
        final long testClassFingerprint = fingerprintGenerator.getFingerprint(TestClass.class);
        final long testClassFingerprintExcluded = fingerprintGeneratorWithExclusion.getFingerprint(TestClass.class);
        assertThat(testClassFingerprintExcluded, is(not(testClassFingerprint)));
    }

    @Test
    public void terminatesForCyclicDependency() throws Exception {
        final DeepSUIDFingerprintGenerator fingerprintGenerator = new DeepSUIDFingerprintGenerator();
        fingerprintGenerator.getFingerprint(FirstCyclicDependencyTestClass.class);
        assertTrue(true);
    }

    @Test
    public void regardsSuperClasses() throws FingerprintGenerationException {
        final DeepSUIDFingerprintGenerator fingerprintGeneratorWithSuperClass = new DeepSUIDFingerprintGenerator();
        final long fingerPrintWithSuperClass = fingerprintGeneratorWithSuperClass.getFingerprint(SubClass.class);

        final DeepSUIDFingerprintGenerator fingerprintGeneratorWithoutSuperClass =
                new DeepSUIDFingerprintGenerator("com.blocksberg.vsc.testmodel.hierarchy.superclass");
        final long fingerPrintWithoutSuperClass = fingerprintGeneratorWithoutSuperClass.getFingerprint(SubClass.class);

        assertThat(fingerPrintWithSuperClass, is(not(fingerPrintWithoutSuperClass)));

    }

    private static class TestClass implements Serializable {
        private static final long serialVersionUID = -788241330095110156L;
        Collection<Long> longs = new LinkedList<Long>();
        AnotherTestClass anotherTestClassField = new AnotherTestClass();
        Double doubleWrapperField = new Double(5.0);
        Integer[] integerWrapperArray = new Integer[4];

        public TestClass() {
            integerWrapperArray[0] = 4;
            integerWrapperArray[1] = 123;
        }
    }

    private static class AnotherTestClass implements Serializable {
        int integerField = 1;
        String stringField = "Hello!";
        ArrayList<String> list = new ArrayList<String>();
    }

    private static class FirstCyclicDependencyTestClass implements Serializable {
        SecondCyclicDependencyTestClass testClass = new SecondCyclicDependencyTestClass();
    }

    private static class SecondCyclicDependencyTestClass implements Serializable {
        FirstCyclicDependencyTestClass testClass = new FirstCyclicDependencyTestClass();
    }
}