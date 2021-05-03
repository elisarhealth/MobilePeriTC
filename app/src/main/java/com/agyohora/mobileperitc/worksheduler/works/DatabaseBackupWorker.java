package com.agyohora.mobileperitc.worksheduler.works;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.agyohora.mobileperitc.myapplication.MyApplication;
import com.agyohora.mobileperitc.utils.CommonUtils;

/**
 * Created by Invent
 * Worker Class which used backup existing DB every 12 Hours
 */

public class DatabaseBackupWorker extends Worker {

    private static final String TAG = DatabaseBackupWorker.class.getSimpleName();

    public DatabaseBackupWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.e(TAG, "Working On DB backup");
        try {
            CommonUtils.backupDatabase(MyApplication.getInstance());
            return Result.success();
        } catch (Exception exception) {
            Log.e(TAG, " " + exception.getMessage());
            return Result.failure();
        }
    }
}
