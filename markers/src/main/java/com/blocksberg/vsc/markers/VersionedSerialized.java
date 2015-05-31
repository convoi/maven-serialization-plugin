package com.blocksberg.vsc.markers;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author Justin Heesemann
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface VersionedSerialized {
    String id();

    int version() default 0;

    SerializationTechnique serialization() default SerializationTechnique.JAVA;

    enum SerializationTechnique {
        JAVA, JACKSON, GSON;
    }
}
