package com.blocksberg.vsc.manufacturing;

import java.util.List;


/**
 * Provides a mechanism to compute a fingerprint for a given class.
 * 
 * Created by tbecker on 15.07.2015.
 */
public interface FingerprintGenerator {
    void setExcludedPackages(List<String> excludedPackages);

    long getFingerprint(Class<?> clazz) throws FingerprintGenerationException;
}
