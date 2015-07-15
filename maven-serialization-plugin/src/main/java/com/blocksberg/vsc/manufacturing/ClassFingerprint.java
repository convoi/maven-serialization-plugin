package com.blocksberg.vsc.manufacturing;

/**
 * Provides a mechanism to compute a fingerprint for a given class.
 *
 * Created by tbecker on 15.07.2015.
 */
public interface ClassFingerprint {
    Class<?> getFingerprintClass();
    long getFingerprint();
}
