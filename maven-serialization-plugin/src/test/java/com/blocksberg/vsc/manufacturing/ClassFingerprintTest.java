package com.blocksberg.vsc.manufacturing;

import org.junit.Test;

import java.io.Serializable;
import java.util.Collection;
import java.util.LinkedList;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class ClassFingerprintTest {

    @Test
    public void algorithmComputesDifferentFingerprintsForDifferentClasses() throws NoSuchFieldException, IllegalAccessException {
        ClassFingerprint testClassFingerprint = new ClassFingerprint(TestClass.class);
        ClassFingerprint anotherTestClassFingerprint = new ClassFingerprint(AnotherTestClass.class);
        assertThat(testClassFingerprint.get(), is(not(anotherTestClassFingerprint.get())));
    }

    @Test
    public void algorithmCanExcludePackages() throws NoSuchFieldException, IllegalAccessException {
        ClassFingerprint testClassFingerprintExcluded = new ClassFingerprint(TestClass.class, "java.lang", "java.util");
        ClassFingerprint testClassFingerprint = new ClassFingerprint(TestClass.class);
        assertThat(testClassFingerprint.get(), is(not(testClassFingerprintExcluded.get())));
    }

    @Test
    public void algorithmTerminatesForCyclicDependency() throws NoSuchFieldException, IllegalAccessException {
        ClassFingerprint firstFingerprint = new ClassFingerprint(FirstCyclicDependencyTestClass.class);
        assertTrue(true);
    }

    private static class TestClass implements Serializable {
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
    }

    private static class FirstCyclicDependencyTestClass implements Serializable {
        SecondCyclicDependencyTestClass testClass = new SecondCyclicDependencyTestClass();
    }

    private static class SecondCyclicDependencyTestClass implements Serializable {
        FirstCyclicDependencyTestClass testClass = new FirstCyclicDependencyTestClass();
    }
}