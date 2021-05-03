package com.agyohora.mobileperitc.worksheduler.works;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.agyohora.mobileperitc.data.network.AppApiHelper;

public class CheckForUpdatesWorker extends Worker {

    public CheckForUpdatesWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        Log.e("runImmediateSyncWork", "RunImmediatelyWorker called");
        new AppApiHelper().checkForUpdates();
    }

    @NonNull
    @Override
    public Result doWork() {
       return null;

    }

}
