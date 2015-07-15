package com.blocksberg.vsc.manufacturing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ObjectStreamClass;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Computes a fingerprint for a given class,
 * i.e. a hash computed from the serial version uids of all the
 * serializable classes directly or indirectly used by the given class via fields.
 *
 * Created by tbecker on 14.07.2015.
 */
public class ClassFingerprintImpl implements ClassFingerprint {

    private static final Logger LOGGER = LoggerFactory.getLogger(ClassFingerprintImpl.class);
    private Class<?> clazz;
    private long fingerprint;

    public ClassFingerprintImpl(Class<?> clazz, String... excludedPackages)
            throws NoSuchFieldException, IllegalAccessException {
        this.clazz = clazz;
        Set<Class<?>> classes = collectClasses(new HashSet<Class<?>>(), clazz, excludedPackages);
        Set<Class<?>> serializableClasses = filterSerializableClasses(classes);
        Map<Class<?>, Long> serialVersionUIDMap = buildSerialVersionUIDMap(serializableClasses);
        fingerprint = generateFingerprint(serialVersionUIDMap);
    }


    private Set<Class<?>> collectClasses(Set<Class<?>> classes, Class<?> clazz, String... excludedPackages) {
        if (!classes.contains(clazz) && !isExcluded(clazz, excludedPackages)) {
                classes.add(clazz);
                Field[] clazzDeclaredFields = clazz.getDeclaredFields();
                for (Field declaredField : clazzDeclaredFields) {
                    Type type = declaredField.getGenericType();
                    if (type instanceof ParameterizedType) {
                        for (Type parameterType : ((ParameterizedType) type).getActualTypeArguments()) {
                            if (parameterType instanceof Class) {
                                collectClasses(classes, (Class) parameterType, excludedPackages);
                            }
                        }
                    }
                    collectClasses(classes, declaredField.getType(), excludedPackages);
                }
        }
        return classes;
    }


    private boolean isExcluded(Class<?> clazz, String... excludedPackages) {
        for (String excludedPackage : excludedPackages) {
            if (clazz.getCanonicalName().startsWith(excludedPackage)) {
                return true;
            }
        }
        return false;
    }


    private Set<Class<?>> filterSerializableClasses(Set<Class<?>> classes) {
        Set<Class<?>> serializableClasses = new HashSet<Class<?>>();
        for (Class<?> clazz : classes) {
            if (isSerializable(clazz)) {
                serializableClasses.add(clazz);
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


    @Override public Class<?> getFingerprintClass() {
        return clazz;
    }


    @Override public long getFingerprint() {
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
        System.out.println("serialVersionUIDMap with size = " + serialVersionUIDMap.size());
        long fingerprint = 0l;
        for (Map.Entry<Class<?>, Long> entry : serialVersionUIDMap.entrySet()) {
            String message =
                    MessageFormat.format("class: {0}, suid: {1}", entry.getKey().getCanonicalName(), entry.getValue());
            LOGGER.info(message);
            System.out.println(message);
            fingerprint ^= entry.getValue();
        }
        return fingerprint;
    }

}
