package com.blocksberg.vsc.manufacturing;

import org.junit.Test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class ClassFingerprintImplTest {

    @Test
    public void computesDifferentFingerprintsForDifferentClasses() throws NoSuchFieldException, IllegalAccessException {
        ClassFingerprint testClassFingerprint = new ClassFingerprintImpl(TestClass.class);
        ClassFingerprint anotherTestClassFingerprint = new ClassFingerprintImpl(AnotherTestClass.class);
        assertThat(testClassFingerprint.getFingerprint(), is(not(anotherTestClassFingerprint.getFingerprint())));
    }

    @Test
    public void canExcludePackages() throws NoSuchFieldException, IllegalAccessException {
        ClassFingerprint
                testClassFingerprintExcluded = new ClassFingerprintImpl(TestClass.class, "java.lang", "java.util");
        ClassFingerprint testClassFingerprint = new ClassFingerprintImpl(TestClass.class);
        assertThat(testClassFingerprint.getFingerprint(), is(not(testClassFingerprintExcluded.getFingerprint())));
    }

    @Test
    public void terminatesForCyclicDependency() throws NoSuchFieldException, IllegalAccessException {
        ClassFingerprint firstFingerprint = new ClassFingerprintImpl(FirstCyclicDependencyTestClass.class);
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
        ArrayList<String> list = new ArrayList<String>();
    }

    private static class FirstCyclicDependencyTestClass implements Serializable {
        SecondCyclicDependencyTestClass testClass = new SecondCyclicDependencyTestClass();
    }

    private static class SecondCyclicDependencyTestClass implements Serializable {
        FirstCyclicDependencyTestClass testClass = new FirstCyclicDependencyTestClass();
    }
}