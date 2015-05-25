package com.blocksberg.vsc;

/**
 * @author jh
 */
public class SerializedVersionInfo {
    private String fullClassName;
    private int version;

    public SerializedVersionInfo() {

    }

    public SerializedVersionInfo(String fullClassName, int version) {
        this.fullClassName = fullClassName;
        this.version = version;
    }

    public String getFullClassName() {
        return fullClassName;
    }

    public void setFullClassName(String fullClassName) {
        this.fullClassName = fullClassName;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
