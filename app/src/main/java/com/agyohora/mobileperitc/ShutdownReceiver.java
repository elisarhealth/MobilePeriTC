package com.agyohora.mobileperitc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.agyohora.mobileperitc.data.preferences.AppPreferencesHelper;

import static com.agyohora.mobileperitc.utils.AppConstants.DEVICE_PREF;

public class ShutdownReceiver  extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("Track Me", "ShutdownReceiver called");
        if (Intent.ACTION_SHUTDOWN.equals(intent.getAction()) || Intent.ACTION_REBOOT.equals(intent.getAction())) {
            AppPreferencesHelper appPreferencesHelper = new AppPreferencesHelper(context, DEVICE_PREF);
            appPreferencesHelper.setLoginStatus(false);
            appPreferencesHelper.setRole(0);
        }
    }
}
