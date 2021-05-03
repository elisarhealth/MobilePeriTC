package com.agyohora.mobileperitc.inappupdate;

import java.net.URL;

/**
 * Created by Invent on 18-1-18.
 * Getter and setter for App updates
 */

public class InAppUpdate {
    private String version;
    private Integer versionCode;
    private String releaseNotes;
    private URL apk;

    public InAppUpdate() {
    }

    public InAppUpdate(String latestVersion, Integer latestVersionCode) {
        this.version = latestVersion;
        this.versionCode = latestVersionCode;
    }

    public InAppUpdate(String latestVersion, String releaseNotes, URL apk) {
        this.version = latestVersion;
        this.apk = apk;
        this.releaseNotes = releaseNotes;
    }

    public InAppUpdate(String latestVersion, Integer latestVersionCode, String releaseNotes, URL apk) {
        this(latestVersion, releaseNotes, apk);
        this.versionCode = latestVersionCode;
    }

    public String getLatestVersion() {
        return this.version;
    }

    public void setLatestVersion(String latestVersion) {
        this.version = latestVersion;
    }

    public Integer getLatestVersionCode() {
        return this.versionCode;
    }

    public void setLatestVersionCode(Integer versionCode) {
        this.versionCode = versionCode;
    }

    public String getReleaseNotes() {
        return this.releaseNotes;
    }

    public void setReleaseNotes(String releaseNotes) {
        this.releaseNotes = releaseNotes;
    }

    public URL getUrlToDownload() {
        return this.apk;
    }

    public void setUrlToDownload(URL apk) {
        this.apk = apk;
    }
}
