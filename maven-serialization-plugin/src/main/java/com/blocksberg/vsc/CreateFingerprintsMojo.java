package com.blocksberg.vsc;

import com.blocksberg.vsc.manufacturing.FingerprintGenerationException;
import com.blocksberg.vsc.manufacturing.FingerprintGenerator;

import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 * Goal which creates serialized instances and writes them to disc.
 * 
 * @author Justin Heesemann
 */
@Mojo(name = "create", defaultPhase = LifecyclePhase.INTEGRATION_TEST)
public class CreateFingerprintsMojo extends AbstractMojo {

    /**
     * Location of the file.
     * 
     * @parameter expression="${project.build.directory}"
     * @required
     */
    @Parameter(name = "outputDirectory", defaultValue = ".")
    private File outputDirectory;

    @Parameter(name = "annotationClass")
    private String annotationClass;

    @Parameter(name = "enforceAnnotatedClasses", defaultValue = "false")
    private boolean enforceAnnotatedClasses;

    @Parameter(name = "scanPackages")
    private List<String> scanPackages;

    @Parameter(defaultValue = "${project}", readonly = true, required = true)
    private MavenProject project;

    @Parameter(property = "clazz", defaultValue = "ALL")
    private String clazz;

    @Parameter(name = "generatorClass", property = "generatorClass",
            defaultValue = "com.blocksberg.vsc.manufacturing.DeepSUIDFingerprintGenerator")
    private String generatorClass;

    @Parameter(name = "excludedGeneratorPackages")
    private List<String> excludedGeneratorPackages;

    private FingerprintGenerator generator;

    private ClassLoader classLoader;
    private List<URL> urlsToScan;

    private AnnotationScanner annotationScanner;

    // public void setClazz(String clazz) {
    // this.clazz = clazz;
    // }

    public void execute() throws MojoExecutionException {

        if (!outputDirectory.exists()) {
            outputDirectory.mkdirs();
        }

        try {
            urlsToScan = getUrlsToScan();
            classLoader = getClassLoader();
            initAnnotationScanner();
        } catch (final Exception e) {
            throw new MojoExecutionException("Initialize classloader failed, probably there is a problem with the "
                    + "classpath", e);
        }
        try {
            generator =
                    (FingerprintGenerator) FingerprintGenerator.class.getClassLoader().loadClass(generatorClass)
                            .newInstance();
            generator.setExcludedPackages(excludedGeneratorPackages);
        } catch (final Exception e) {
            throw new MojoExecutionException("Creating generator (" + generatorClass + ") failed.", e);
        }

        try {
            serializeAnnotatedClasses(clazz);
        } catch (final Exception e) {
            throw new MojoExecutionException("an error occured:" + e.getClass().getCanonicalName() + " "
                    + e.getMessage(), e);
        }
    }

    private void initAnnotationScanner() throws MalformedURLException {
        annotationScanner = new AnnotationScanner(scanPackages, classLoader, urlsToScan, annotationClass);

    }

    public ClassLoader getClassLoader() throws DependencyResolutionRequiredException, MalformedURLException {
        if (project != null) {
            final URL[] urlArray = urlsToScan.toArray(new URL[urlsToScan.size()]);
            final URLClassLoader urlClassLoader = new URLClassLoader(urlArray);
            return urlClassLoader;
        } else {
            return getClass().getClassLoader();
        }
    }

    public List<URL> getUrlsToScan() throws MalformedURLException, DependencyResolutionRequiredException {
        if (project != null) {
            final List<String> compileClasspathElements = new ArrayList<String>(project.getCompileClasspathElements());
            for (final Artifact artifact : project.getDependencyArtifacts()) {
                compileClasspathElements.add(artifact.getFile().toString());
            }
            if (compileClasspathElements != null) {
                getLog().info(compileClasspathElements.toString());
            }
            return makeClasspathUrls(compileClasspathElements);
        } else {
            return Collections.singletonList(new File(".").toURI().toURL());
        }
    }

    private void serializeAnnotatedClasses(final String classToFingerprint) throws IOException, ClassNotFoundException,
        NoSuchMethodException, IllegalAccessException, InvocationTargetException, FingerprintGenerationException {
        final Set<Class<?>> classes = annotationScanner.scan();
        getLog().info("found " + classes.size() + " classes, annotated by " + annotationClass);
        for (final Class<?> aClass : classes) {
            if ("ALL".equals(classToFingerprint) || classToFingerprint.equals(aClass.getSimpleName())
                    || classToFingerprint.equals(aClass.getCanonicalName())) {

                final File[] existingFiles = outputDirectory.listFiles(new FilenameFilter() {

                    @Override
                    public boolean accept(final File dir, final String name) {
                        return new FingerprintFile(name).getClassName().equals(aClass.getCanonicalName());
                    }
                });

                for (File existingFile : existingFiles) {
                    getLog().debug("deleting existing file " + existingFile.getName());
                    existingFile.delete();
                }

                getLog().debug("trying to serialize instance of " + aClass.getCanonicalName());

                new FingerprintFile(aClass, generator).create(outputDirectory);
            }
        }
    }

    private List<URL> makeClasspathUrls(final List<String> classpaths) throws MalformedURLException {
        final List<URL> urls = new ArrayList<URL>();
        if (classpaths != null) {
            for (final String classpath : classpaths) {
                urls.add(new File(classpath).toURI().toURL());
            }
            return urls;
        } else {
            return null;
        }
    }
}
