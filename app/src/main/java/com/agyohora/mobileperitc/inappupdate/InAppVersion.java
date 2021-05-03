package com.agyohora.mobileperitc.inappupdate;

import androidx.annotation.NonNull;
import android.util.Log;

/**
 * Created by Invent on 18-1-18.
 * @see java.lang.Comparable
 * class to compare app versions
 */

public class InAppVersion implements Comparable<InAppVersion> {
    private String version;

    public InAppVersion(String version) {
        String TAG = "InAppVersion";
        if (version == null) {
            Log.e("InAppVersion", "Version can not be null");
        } else {
            version = version.replaceAll("[^0-9?!\\.]", "");
            if (!version.matches("[0-9]+(\\.[0-9]+)*")) {
                Log.e("InAppVersion", "Invalid version format");
            }
        }

        this.version = version;
    }

    private String get() {
        return this.version;
    }

    public int compareTo(@NonNull InAppVersion that) {
        String[] thisParts = this.get().split("\\.");
        String[] thatParts = that.get().split("\\.");
        int length = Math.max(thisParts.length, thatParts.length);

        for (int i = 0; i < length; ++i) {
            int thisPart = i < thisParts.length ? Integer.parseInt(thisParts[i]) : 0;
            int thatPart = i < thatParts.length ? Integer.parseInt(thatParts[i]) : 0;
            if (thisPart < thatPart) {
                return -1;
            }

            if (thisPart > thatPart) {
                return 1;
            }
        }

        return 0;
    }

    public boolean equals(Object that) {
        return this == that ? true : (that == null ? false : this.getClass() == that.getClass() && this.compareTo((InAppVersion) that) == 0);
    }
}

