# maven-serialization-plugin

This plugin aims to help with serialization troubles occurring when several different versions of an
application try to exchange serialized instances, by making incompatibilities between versions visible
as early as possible.

## how it works
The plugin is scanning the class path for classes annotated with a special annotation. It then tries to find
a file containing an already serialized instance of this class matching that annotation. If such a file exists, it then
tries to deserialize it and checks if the class of the deserialized instance is the same as the annotated one.

## usage
Usage consists of three steps:

1. add the plugin to your pom.xml
2. add an annotation (see above) to your project
3. annotate classes with that annotation

### adding the plugin
```xml
<plugin>
  <groupId>com.blocksberg.versioned-serialization</groupId>
  <artifactId>versioned-serialization-maven-plugin</artifactId>
  <version>1.0-SNAPSHOT</version>
  <executions>
    <execution>
      <id>serialize-instances</id>
      <goals>
        <goal>serialize</goal>
      </goals>
      <phase>post-integration-test</phase>
    </execution>
  </executions>
  <configuration>
    <outputDirectory>serialized</outputDirectory>
    <annotationClass>com.blocksberg.vsc.markers.VersionedSerialized</annotationClass>
    <scanPackages>
      <scanPackage>com.blocksberg.vsctest.model</scanPackage>
    </scanPackages>
  </configuration>
</plugin>
```

You need to pick an `outputDirectory` (the place where the serialized instances are stored), an `annotationClass` and
which packages to scan.


### annotation
Feel free to use the annotation from `versioned-serialization-markers`. But you can easily use your own annotation.
It should look roughly like this:
```java
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface VersionedSerialized {
    String id();

    int version() default 0;

    SerializationTechnique serialization() default SerializationTechnique.JAVA;

    enum SerializationTechnique {
        JAVA;
    }
}
```

Feel free to pick a better name.

### annotate some classes
```java
@VersionedSerialized(id="a", version=1)
public class A implements Serializable {
  private int anInt;
  public A(int a) {
    this.anInt = a;
  }
}
```

