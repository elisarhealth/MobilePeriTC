package com.agyohora.mobileperitc.worksheduler.works;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.agyohora.mobileperitc.exceptions.PrbException;
import com.agyohora.mobileperitc.utils.CommonUtils;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

public class NineLReachedWorker  extends Worker {

    Context context;

    public NineLReachedWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.e("NineLReachedWorker", "Working On");
        try {
            int prbCount = CommonUtils.returnClickCount();
            String msg = "PRB reaches the limit " + prbCount;
            FirebaseCrashlytics.getInstance().recordException(new PrbException(msg));
            return Result.success();
        } catch (Exception exception) {
            Log.e("NineLReachedWorker", " " + exception.getMessage());
            return Result.failure();
        }
    }
}