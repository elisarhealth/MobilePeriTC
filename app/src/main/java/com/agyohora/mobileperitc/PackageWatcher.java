package com.agyohora.mobileperitc;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInstaller;
import android.util.Log;

import com.agyohora.mobileperitc.utils.CommonUtils;

/**
 * Created by Invent on 7-3-18.
 */

public class PackageWatcher extends BroadcastReceiver {
    private String TAG = PackageWatcher.class.getName();

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d("PackageWatcher", " " + intent.getAction());

        int result = intent.getIntExtra(PackageInstaller.EXTRA_STATUS, PackageInstaller.STATUS_FAILURE);
        String packageName = intent.getStringExtra(PackageInstaller.EXTRA_PACKAGE_NAME);

        Log.d(TAG, " PackageInstallerCallback: result = " + result + " packageName = " + packageName);
        CommonUtils.writeToBugFixLogFile("PackageInstallerCallback:result = " + result + " packageName = " + packageName);
        switch (result) {

            case PackageInstaller.STATUS_PENDING_USER_ACTION:
                CommonUtils.writeToBugFixLogFile("PackageInstaller.STATUS_PENDING_USER_ACTION");
                Log.d("Gonna", "Trigger intent");
                Log.d("Checking Extra", "  " + intent.getParcelableExtra(Intent.EXTRA_INTENT));

                // this should not happen in M, but will happen in L and L-MR1
                Intent update = intent.getParcelableExtra(Intent.EXTRA_INTENT);
                update.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(update);
                break;

            case PackageInstaller.STATUS_SUCCESS:
                CommonUtils.writeToBugFixLogFile("PackageInstaller.STATUS_SUCCESS");
                Log.d(TAG, "Install success.");
                break;

            case PackageInstaller.STATUS_FAILURE:
                CommonUtils.writeToBugFixLogFile("PackageInstaller.STATUS_FAILURE");
                Log.d(TAG, "Install failed.");
                break;

        }
    }

}