package com.agyohora.mobileperitc.worksheduler.works;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

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
import static com.agyohora.mobileperitc.utils.CommonUtils.isOnline;

/**
 * Created by Invent
 * Worker Class which used to sync test data to server every 15 minutes
 */

public class SyncDbWorker extends Worker {

    private static final String TAG = SyncDbWorker.class.getSimpleName();

    public SyncDbWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.e(TAG, "Working On Sync");
        Log.e(TAG, "IS ONLINE " + isOnline());
        try {
            if (isOnline()) {
                List<PatientTestResult> patientTestResults = getUnSyncedData(AppDatabase.getAppDatabase(MyApplication.getInstance()));
                Log.e(TAG, "UnSynced data size " + patientTestResults.size());
                if (patientTestResults.size() > 0)
                    for (PatientTestResult patientTestResult : patientTestResults) {
                        JSONObject dataReceived = bytesToJsonObject(patientTestResult.getResult());
                        ANResponse anResponse = new AppApiHelper().syncData(dataReceived);
                        if (anResponse.isSuccess()) {
                            String id = "" + patientTestResult.getId();
                            Log.e(TAG, "Synced record " + id);
                            syncedRecord(AppDatabase.getAppDatabase(MyApplication.getInstance()), id);
                        } else {
                            Log.e(TAG, "Synced Failed");
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
