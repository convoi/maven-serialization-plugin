package com.blocksberg.vsc;

import com.blocksberg.vsc.manufacturing.FingerprintGenerationException;
import com.blocksberg.vsc.manufacturing.FingerprintGenerator;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FingerprintFile implements Comparable<FingerprintFile> {

    public static final String SEPARATOR = ".";

    public static final String SUFFIX = "serial";

    private final long fingerprint;

    private final String className;

    public FingerprintFile(final Class<?> clazz, final FingerprintGenerator generator)
        throws FingerprintGenerationException {
        this.className = clazz.getCanonicalName();
        this.fingerprint = generator.getFingerprint(clazz);
    }

    public FingerprintFile(final String className, final long fingerprint) {
        this.className = className;
        this.fingerprint = fingerprint;
    }

    public FingerprintFile(final String fileName) {
        final Pattern pattern = Pattern.compile("(.*)\\.([-]*[0-9]+)\\." + SUFFIX);
        final Matcher matcher = pattern.matcher(fileName);

        if (!matcher.matches()) {
            throw new IllegalArgumentException("Filename does not match required pattern.");
        }

        className = matcher.group(1);
        fingerprint = Long.valueOf(matcher.group(2));
    }

    public String getClassName() {
        return className;
    }

    public long getFingerprint() {
        return fingerprint;
    }

    public String getFileName() {
        return className + SEPARATOR + fingerprint + SEPARATOR + SUFFIX;
    }

    public void create(final File outputDirectory) throws IOException {
        final File file = new File(outputDirectory, getFileName());
        if (file.exists()) {
            return;
        }
        file.createNewFile();
    }

    @Override
    public int hashCode() {
        return getFileName().hashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final FingerprintFile other = (FingerprintFile) obj;
        if (getFileName() == null) {
            if (other.getFileName() != null) {
                return false;
            }
        } else if (!getFileName().equals(other.getFileName())) {
            return false;
        }
        return true;
    }

    @Override
    public int compareTo(final FingerprintFile o) {
        return getFileName().compareTo(o.getFileName());
    }

    @Override
    public String toString() {
        return getClassName();
    }

}
