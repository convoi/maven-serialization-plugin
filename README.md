# maven-serialization-plugin

this plugin aims to help with serialization troubles occuring when several different versions of an application try to exchange serialized instances, by making incompatibilities between versions visible as early as possible.
```xml
<plugin>
  <groupId>com.blocksberg.versioned-serialization</groupId>
  <artifactId>versioned-serialization-maven-plugin</artifactId>
  <version>1.0-SNAPSHOT</version>
  <configuration>
    <outputDirectory>serialized</outputDirectory>
    <scanForAnnotation>true</scanForAnnotation>
    <annotationClass>com.blocksberg.vsc.markers.VersionedSerialized</annotationClass>
    <scanPackages>
      <scanPackage>com.blocksberg.vsctest.model</scanPackage>
    </scanPackages>
  </configuration>
  <executions>
    <execution>
      <phase>integration-test</phase>
      <goals>
        <goal>serialize</goal>
      </goals>
    </execution>
  </executions>
</plugin>
```
