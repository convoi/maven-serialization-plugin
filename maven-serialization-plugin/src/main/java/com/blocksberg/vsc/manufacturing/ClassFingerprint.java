package com.blocksberg.vsc.manufacturing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ObjectStreamClass;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Computes a fingerprint for a given class,
 * i.e. a hash computed from the serial version uids of all the
 * serializable classes directly or indirectly used by the given class.
 *
 * Created by tbecker on 14.07.2015.
 */
public class ClassFingerprint {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClassFingerprint.class);
    private Class<?> clazz;
    private long fingerprint;


    public ClassFingerprint(Class<?> clazz) throws NoSuchFieldException, IllegalAccessException {
        this.clazz = clazz;
        Set<Class<?>> serializableClasses = collectSerializableClasses(new HashSet<Class<?>>(), clazz);
        Map<Class<?>, Long> serialVersionUIDMap = buildSerialVersionUIDMap(serializableClasses);
        fingerprint = generateFingerprint(serialVersionUIDMap);
    }


    private Set<Class<?>> collectSerializableClasses(Set<Class<?>> serializableClasses, Class<?> clazz) {
        if (!serializableClasses.contains(clazz) && isSerializable(clazz)) {
            serializableClasses.add(clazz);
            Field[] clazzDeclaredFields = clazz.getDeclaredFields();
            for (Field declaredField : clazzDeclaredFields) {
                collectSerializableClasses(serializableClasses, declaredField.getType());
            }
        }
        return serializableClasses;
    }


    private Map<Class<?>, Long> buildSerialVersionUIDMap(Set<Class<?>> serializableClasses)
            throws NoSuchFieldException, IllegalAccessException {
        Map<Class<?>, Long> serialVersionUIDMap = new HashMap<Class<?>, Long>();
        for (Class<?> clazz : serializableClasses) {
            serialVersionUIDMap.put(clazz, getComputedSerialVersionUID(clazz));
        }
        return serialVersionUIDMap;
    }


    public Class<?> getFingerprintClass() {
        return clazz;
    }


    public long get() {
        return fingerprint;
    }


    private boolean isSerializable(Class<?> clazz) {
        ObjectStreamClass c = ObjectStreamClass.lookup(clazz);
        return c != null;
    }


    private long getComputedSerialVersionUID(Class<?> clazz) throws NoSuchFieldException, IllegalAccessException {
        final ObjectStreamClass objectStreamClass = ObjectStreamClass.lookup(clazz);
        final Field serialVersionUID = ObjectStreamClass.class.getDeclaredField("suid");
        if (serialVersionUID != null) {
            serialVersionUID.setAccessible(true);
            serialVersionUID.set(objectStreamClass, null);
        }
        return objectStreamClass.getSerialVersionUID();
    }


    private long generateFingerprint(Map<Class<?>, Long> serialVersionUIDMap) {
        LOGGER.info("serialVersionUIDMap with size = {}", serialVersionUIDMap.size());
        //System.out.println("serialVersionUIDMap with size = " + serialVersionUIDMap.size());
        long fingerprint = 0l;
        for (Long serialVersionUID : serialVersionUIDMap.values()) {
            fingerprint ^= serialVersionUID;
        }
        return fingerprint;
    }


        /*
    private Map<Class<?>, Long> serialVersionUIDMapFromClass(Class<?> clazz)
            throws NoSuchFieldException, IllegalAccessException {
        Map<Class<?>, Long> serialVersionUIDMap = new HashMap<Class<?>, Long>();
        if (!this.inspectedClasses.contains(clazz)){
            this.inspectedClasses.add(clazz);
            if (isSerializable(clazz)) {
                serialVersionUIDMap.put(clazz, getComputedSerialVersionUID(clazz));
                Field[] clazzDeclaredFields = clazz.getDeclaredFields();
                for (Field declaredField : clazzDeclaredFields) {
                    serialVersionUIDMap.putAll(processField(declaredField));
                }
            }
        }
        return serialVersionUIDMap;
    }
    */

    /*
    private Map<Class<?>, Long> processField(Field field)
            throws NoSuchFieldException, IllegalAccessException {
        Map<Class<?>, Long> serialVersionUIDMap = new HashMap<Class<?>, Long>();
        Class<?> clazzOfField = field.getType();
        serialVersionUIDMap.putAll(serialVersionUIDMapFromClass(clazzOfField));
        return serialVersionUIDMap;
    }
    */


}
