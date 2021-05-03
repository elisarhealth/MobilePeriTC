package com.agyohora.mobileperitc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.agyohora.mobileperitc.data.preferences.AppPreferencesHelper;

import static com.agyohora.mobileperitc.utils.AppConstants.DEVICE_PREF;

/**
 * Created by Invent on 19-1-18.
 */

public class StartUpReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(final Context context, final Intent intent) {
        try {
            Intent i = context.getPackageManager()
                    .getLaunchIntentForPackage(context.getPackageName());
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            context.startActivity(i);
            new AppPreferencesHelper(context, DEVICE_PREF).resetCriticalCounter();
            new AppPreferencesHelper(context, DEVICE_PREF).setCriticalUpdate(false);

        } catch (Exception e) {
            Log.e("StartUpReceiver", e.getMessage());
        }
    }
}