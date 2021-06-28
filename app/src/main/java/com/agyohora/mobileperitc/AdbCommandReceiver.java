package com.agyohora.mobileperitc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

public class AdbCommandReceiver extends BroadcastReceiver {

    String moveFiles = "com.agyohora.mobileperitc.movefiles";
    String getVectorFromHMD = "com.agyohora.mobileperitc.getvectorfromhmd";

    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "Command Received", Toast.LENGTH_LONG).show();

        if (intent != null) {
            String deviceId = intent.getStringExtra("deviceId");
            String action = intent.getAction();
            if (action != null) {
                Log.e("adbCommandReceiver", "command received ");
                if (action.equalsIgnoreCase(moveFiles)) {
                    if (deviceId != null) {
                        Toast.makeText(context, "DeviceId " + deviceId, Toast.LENGTH_LONG).show();
                    } else {

                    }
                } else if (action.equalsIgnoreCase(getVectorFromHMD)) {
                    if (deviceId != null) {

                    } else {

                    }
                }
            }
        }
    }
}
