package com.agyohora.mobileperitc.worksheduler.works;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.agyohora.mobileperitc.BuildConfig;
import com.agyohora.mobileperitc.data.database.AppDatabase;
import com.agyohora.mobileperitc.data.database.entity.PatientTestResult;
import com.agyohora.mobileperitc.data.network.AppApiHelper;
import com.agyohora.mobileperitc.myapplication.MyApplication;
import com.androidnetworking.common.ANResponse;
import com.google.gson.JsonIOException;

import org.json.JSONObject;

import java.util.List;

import static com.agyohora.mobileperitc.data.database.DatabaseInitializer.getUnSyncedData;
import static com.agyohora.mobileperitc.data.database.DatabaseInitializer.syncedRecord;
import static com.agyohora.mobileperitc.utils.CommonUtils.bytesToJsonObject;

/**
 * Created by Invent
 * Worker Class which used sync the test results to server immediately
 */

public class RunImmediatelyWorker extends Worker {

    private static final String TAG = RunImmediatelyWorker.class.getSimpleName();

    public RunImmediatelyWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        Log.e("runImmediateSyncWork","RunImmediatelyWorker called");
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.e("runImmediateSyncWork","doWork called");
        List<PatientTestResult> patientTestResults = getUnSyncedData(AppDatabase.getAppDatabase(MyApplication.getInstance()));
        Log.e(TAG, "UnSynced data size " + patientTestResults.size());

        if(BuildConfig.IN21_012_EyeTracking){
            //piece of code
        }
        if (BuildConfig.IN21_012_IdleTimer) {
            //piece of code
        }


        try {
            if (patientTestResults.size() > 0)
                for (PatientTestResult patientTestResult : patientTestResults) {
                    JSONObject dataReceived = bytesToJsonObject(patientTestResult.getResult());
                    if (dataReceived != null) {
                        ANResponse anResponse = new AppApiHelper().syncData(dataReceived);
                        if (anResponse.isSuccess()) {
                            String id = "" + patientTestResult.getId();
                            Log.e(TAG, "Synced record " + id);
                            syncedRecord(AppDatabase.getAppDatabase(MyApplication.getInstance()), id);
                        }
                    }
                }
            return Result.success();
        } catch (JsonIOException exception) {
            Log.e(TAG, " " + exception.getMessage());
            return Result.success();
        } catch (Exception exception) {
            Log.e(TAG, " " + exception.getMessage());
            return Result.failure();
        }
    }
}
