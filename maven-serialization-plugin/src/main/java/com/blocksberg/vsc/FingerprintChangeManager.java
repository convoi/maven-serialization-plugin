package com.blocksberg.vsc;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class FingerprintChangeManager {

    private final Set<FingerprintFile> oldFingerprintFiles;
    private final Set<FingerprintFile> newFingerprintFiles;

    public FingerprintChangeManager(final Set<FingerprintFile> oldFingerPrintFiles,
            final Set<FingerprintFile> newFingerprintFile) {
        this.oldFingerprintFiles = oldFingerPrintFiles;
        this.newFingerprintFiles = newFingerprintFile;
    }

    public Set<String> getAddedClassNames() {
        final Set<String> oldClassNames = getClassNames(oldFingerprintFiles);
        final Set<String> newClassNames = getClassNames(newFingerprintFiles);

        newClassNames.removeAll(oldClassNames);
        return newClassNames;
    }

    public Set<String> getChangedClassNames() {
        final Set<FingerprintFile> oldFiles = new HashSet<FingerprintFile>(oldFingerprintFiles);
        final Set<FingerprintFile> newFiles = new HashSet<FingerprintFile>(newFingerprintFiles);

        newFiles.removeAll(oldFiles);

        final Set<String> result = getClassNames(newFiles);
        result.removeAll(getAddedClassNames());

        return result;
    }

    public Set<String> getDeletedClassNames() {
        final Set<String> oldClassNames = getClassNames(oldFingerprintFiles);
        final Set<String> newClassNames = getClassNames(newFingerprintFiles);
        oldClassNames.removeAll(newClassNames);

        return oldClassNames;

    }

    public Set<String> getRemainingClassNames() {
        final Set<FingerprintFile> oldFiles = new HashSet<FingerprintFile>(oldFingerprintFiles);
        final Set<FingerprintFile> newFiles = new HashSet<FingerprintFile>(newFingerprintFiles);
        newFiles.retainAll(oldFiles);

        return getClassNames(newFiles);
    }

    private Set<String> getClassNames(final Collection<FingerprintFile> fingerprintFiles) {
        final Set<String> result = new HashSet<String>();
        for (final FingerprintFile file : fingerprintFiles) {
            result.add(file.getClassName());
        }

        return result;
    }
}
