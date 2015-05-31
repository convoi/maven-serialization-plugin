package com.blocksberg.vsc;

import com.blocksberg.vsc.markers.VersionedSerialized;
import com.blocksberg.vsc.testmodel.bad.FooNotYetSerialized;
import com.blocksberg.vsc.testmodel.good.Foo;
import org.hamcrest.CoreMatchers;
import org.junit.Test;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

/**
 * @author Justin Heesemann
 */
public class AnnotationScannerTest {

    @Test
    public void testScan() throws Exception {
        final AnnotationScanner annotationScanner = new AnnotationScanner(
                getPackagesToScan(), getClassLoader(), getUrlsToScan(), getAnnotationName());
        final Set<Class<?>> scan = annotationScanner.scan();
        assertThat(scan, CoreMatchers.hasItem(Foo.class));
        assertThat(scan, not(CoreMatchers.hasItem(FooNotYetSerialized.class)));
    }

    private String getAnnotationName() {
        return VersionedSerialized.class.getCanonicalName();
    }

    private List<URL> getUrlsToScan() throws MalformedURLException {
        return Collections
                .singletonList(new File(".").toURI().toURL());
    }

    private ClassLoader getClassLoader() throws MalformedURLException {
        final URL url = new File("target/classes/").toURI().toURL();
        final URL[] urls = {url};
        return new URLClassLoader(urls);
    }

    private Set<String> getPackagesToScan() {
        return Collections.singleton(Foo.class.getPackage().getName());
    }
}