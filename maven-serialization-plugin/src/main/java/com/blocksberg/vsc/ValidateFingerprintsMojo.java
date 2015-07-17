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
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Goal which creates serialized instances and writes them to disc.
 * 
 * @author Justin Heesemann
 */
@Mojo(name = "validate", defaultPhase = LifecyclePhase.INTEGRATION_TEST)
public class ValidateFingerprintsMojo extends AbstractMojo {

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

    @Parameter(name = "generatorClass", property = "generatorClass",
            defaultValue = "com.blocksberg.vsc.manufacturing.DeepSUIDFingerprintGenerator")
    private String generatorClass;

    @Parameter(name = "excludedGeneratorPackages")
    private List<String> excludedGeneratorPackages;

    private FingerprintGenerator generator;

    private ClassLoader classLoader;
    private List<URL> urlsToScan;

    private AnnotationScanner annotationScanner;

    public void execute() throws MojoExecutionException {

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

        final Set<FingerprintFile> fingerprintsFromLastRun = getFingerprintedFiles();
        validateFingerprintedFiles(fingerprintsFromLastRun);

        final Set<FingerprintFile> fingerprintsFromCurrentRun;
        try {
            fingerprintsFromCurrentRun = getCurrentFingerprintedFiles();
        } catch (final Exception e) {
            throw new MojoExecutionException("an error occured:" + e.getClass().getCanonicalName() + " "
                    + e.getMessage(), e);
        }

        final FingerprintChangeManager changeManager =
                new FingerprintChangeManager(fingerprintsFromLastRun, fingerprintsFromCurrentRun);

        if (!changeManager.getAddedClassNames().isEmpty() || !changeManager.getChangedClassNames().isEmpty()
                || !changeManager.getDeletedClassNames().isEmpty()) {

            String errorMessage = new String();
            if (!changeManager.getAddedClassNames().isEmpty()) {
                errorMessage = String.format("\r\nAdded files: %s", changeManager.getAddedClassNames());
            }
            if (!changeManager.getDeletedClassNames().isEmpty()) {
                errorMessage += String.format("\r\nDeleted Files: %s", changeManager.getDeletedClassNames());
            }
            if (!changeManager.getChangedClassNames().isEmpty()) {
                errorMessage += String.format("\r\nChanged Files: %s", changeManager.getChangedClassNames());
            }

            throw new MojoExecutionException(errorMessage);
        }

    }

    private Set<FingerprintFile> getCurrentFingerprintedFiles() throws ClassNotFoundException,
        FingerprintGenerationException {
        final Set<FingerprintFile> result = new HashSet<FingerprintFile>();
        final Set<Class<?>> classes = annotationScanner.scan();
        getLog().info("found " + classes.size() + " classes, annotated by " + annotationClass);
        for (final Class<?> aClass : classes) {
            result.add(new FingerprintFile(aClass, generator));
        }
        return result;
    }

    private Set<FingerprintFile> getFingerprintedFiles() {
        if (outputDirectory.exists() && outputDirectory.isDirectory() && outputDirectory.canRead()) {
            final Set<FingerprintFile> result = new HashSet<FingerprintFile>();
            for (int i = 0; i < outputDirectory.list().length; i++) {
                result.add(new FingerprintFile(outputDirectory.list()[i]));
            }
            return result;
        } else {
            return Collections.<FingerprintFile> emptySet();
        }
    }

    private void validateFingerprintedFiles(final Set<FingerprintFile> files) throws MojoExecutionException {
        final Set<String> fileNames = new HashSet<String>();
        final Set<String> duplicates = new HashSet<String>();
        for (final FingerprintFile file : files) {
            if (fileNames.contains(file.getClassName())) {
                duplicates.add(file.getClassName());
            }
            fileNames.add(file.getClassName());
        }
        if (!duplicates.isEmpty()) {
            throw new MojoExecutionException("The following classes have duplicate entries in the serialized folder: "
                    + duplicates);
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
