package com.agyohora.mobileperitc.worksheduler.works;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.agyohora.mobileperitc.data.database.AppDatabase;
import com.agyohora.mobileperitc.data.database.DatabaseInitializer;
import com.agyohora.mobileperitc.exceptions.DailyNumberOfReportsException;
import com.agyohora.mobileperitc.utils.CommonUtils;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

public class DailyReportWork extends Worker {

    private static final String TAG = DailyReportWork.class.getSimpleName();
    Context context;

    public DailyReportWork(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.context = context;
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.e(TAG, "Working On");
        try {
            FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(context);
            int numberOfReports = DatabaseInitializer.getCount(AppDatabase.getAppDatabase(context));
            Bundle params = new Bundle();
            params.putString("DeviceId", CommonUtils.getHotSpotId());
            params.putInt("ReportsDone", numberOfReports);
            firebaseAnalytics.logEvent("NumberOfReports", params);
            String message = "Number of Reports from " + CommonUtils.getHotSpotId() + " is " + numberOfReports;
            Log.e("message", " " + message);
            FirebaseCrashlytics.getInstance().recordException(new DailyNumberOfReportsException(message));
            return Result.success();
        } catch (Exception exception) {
            Log.e(TAG, " " + exception.getMessage());
            return Result.failure();
        }
    }
}

