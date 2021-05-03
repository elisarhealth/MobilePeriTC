package com.agyohora.mobileperitc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class LogoutReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e("Track Me", "LogoutReceiver called");
        /*if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            AppPreferencesHelper appPreferencesHelper = new AppPreferencesHelper(context, DEVICE_PREF);
            appPreferencesHelper.setLoginStatus(false);
            appPreferencesHelper.setRole(0);
        }*/
    }
}
