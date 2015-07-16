# maven-serialization-plugin

This plugin aims to help with serialization troubles occurring when several different versions of an
application try to exchange serialized instances, by making incompatibilities between versions visible
as early as possible. This plugin can be integrated into the build and indicates when the serialized version of an object changes.

## how it works
The plugin has two different goals. One goal creates fingerprints of the annotated files (create). These fingerprints should be checked in with your code. The second goal (validate), checks whether the fingerprints of the annotated files have changed.

## usage
Usage consists of three steps:

1. add the plugin to your pom.xml
2. add an annotation (see above) to your project
3. annotate classes with that annotation

### adding the plugin
create fingerprints step
```xml
<plugin>
    <groupId>com.blocksberg.versioned-serialization</groupId>
    <artifactId>versioned-serialization-maven-plugin</artifactId>
    <version>1.1-SNAPSHOT</version>
    <executions>
        <execution>
            <id>create-fingerprints</id>
            <goals>
                <goal>create</goal>
            </goals>
            <phase>process-classes</phase>
        </execution>
    </executions>
    <configuration>
        <outputDirectory>serialized</outputDirectory>
        <annotationClass>com.blocksberg.vsc.markers.VersionedSerialized</annotationClass>
        <scanPackages>
            <scanPackage>com.loyaltypartner.lm.lmsbatch.core.mobilepush.api.data</scanPackage>
        </scanPackages>
        <generatorClass>com.blocksberg.vsc.manufacturing.DeepSUIDFingerprintGenerator</generatorClass>
        <excludedGeneratorPackages>
            <excludedGeneratorPackage>java.lang</excludedGeneratorPackage>
        </excludedGeneratorPackages>
    </configuration>
</plugin>
```


validate fingerprints step
```xml
<plugin>
    <groupId>com.blocksberg.versioned-serialization</groupId>
    <artifactId>versioned-serialization-maven-plugin</artifactId>
    <version>1.1-SNAPSHOT</version>
    <executions>
        <execution>
            <id>validate-fingerprints</id>
            <goals>
                <goal>validate</goal>
            </goals>
            <phase>post-integration-test</phase>
        </execution>
    </executions>
    <configuration>
        <outputDirectory>serialized</outputDirectory>
        <annotationClass>com.blocksberg.vsc.markers.VersionedSerialized</annotationClass>
        <scanPackages>
            <scanPackage>com.loyaltypartner.lm.lmsbatch.core.mobilepush.api.data</scanPackage>
        </scanPackages>
        <generatorClass>com.blocksberg.vsc.manufacturing.DeepSUIDFingerprintGenerator</generatorClass>
        <excludedGeneratorPackages>
            <excludedGeneratorPackage>java.lang</excludedGeneratorPackage>
        </excludedGeneratorPackages>
    </configuration>
</plugin>
```

You need to pick an `outputDirectory` (the place where the serialized instances are stored), an `annotationClass` and
which packages to scan ('scanDirectories'). 


### annotation
Feel free to use the annotation from `versioned-serialization-markers`. But you can easily use your own annotation.
It should look roughly like this:
```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface VersionedSerialized {
}
```

Feel free to pick a better name.

### annotate some classes
```java
@VersionedSerialized
public class A implements Serializable {
  private int anInt;
  public A(int a) {
    this.anInt = a;
  }
}
```

