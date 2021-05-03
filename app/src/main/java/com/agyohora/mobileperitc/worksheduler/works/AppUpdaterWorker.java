package com.agyohora.mobileperitc.worksheduler.works;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

/**
 * Created by Invent
 * Worker Class which used to check for updates every 15 minutes
 */


public class AppUpdaterWorker extends Worker {

    private static final String TAG = AppUpdaterWorker.class.getSimpleName();

    public AppUpdaterWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.e(TAG, "Checking for Update in background");
        try {
            //if (isOnline())
                //new AppApiHelper().checkAndInstallUpdate();
            return Result.success();
        } catch (Exception e) {
            Log.e(TAG, " " + e.getMessage());
            return Result.failure();
        }
    }
}
