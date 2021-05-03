package com.agyohora.mobileperitc.asynctasks;

import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import com.agyohora.mobileperitc.data.database.AppDatabase;
import com.agyohora.mobileperitc.data.database.DatabaseInitializer;
import com.agyohora.mobileperitc.data.database.entity.PatientTestResult;
import com.agyohora.mobileperitc.data.preferences.AppPreferencesHelper;
import com.agyohora.mobileperitc.myapplication.MyApplication;
import com.agyohora.mobileperitc.store.PerimetryObject;
import com.agyohora.mobileperitc.store.PerimetryObject_V2;
import com.agyohora.mobileperitc.utils.CommonUtils;
import com.agyohora.mobileperitc.utils.VFI_Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.lang.reflect.Modifier;
import java.util.List;

import static com.agyohora.mobileperitc.data.database.DatabaseInitializer.getResultDataNotMigrated;
import static com.agyohora.mobileperitc.utils.AppConstants.DEVICE_PREF;

public class SilentlyUpdateTheVFI extends AsyncTask<Void, Void, Void> {
    @Override
    protected Void doInBackground(Void... voids) {
        Gson gson = new GsonBuilder().excludeFieldsWithModifiers(Modifier.TRANSIENT)
                .serializeSpecialFloatingPointValues()
                .create();
        AppPreferencesHelper appPreferencesHelper = new AppPreferencesHelper(MyApplication.getInstance(), DEVICE_PREF);
        List<PatientTestResult> patientTestResults = getResultDataNotMigrated(AppDatabase.getAppDatabase(MyApplication.getInstance()));
        int size = patientTestResults.size();
        if (size > 0) {
            try {
                appPreferencesHelper.setDatabaseVersion(3);
                for (PatientTestResult testResult : patientTestResults) {
                    String id = "" + testResult.getId();
                    Log.e("TrackMe", "Record ID " + id);
                    JSONObject dataReceived = CommonUtils.bytesToJsonObject(testResult.getResult());
                    if (dataReceived != null) {
                        CommonUtils.writeToBugFixLogFile("\nRecord Id " + id);
                        PerimetryObject.FinalPerimetryResultObject oldObject = gson.fromJson(dataReceived.toString(), PerimetryObject.FinalPerimetryResultObject.class);
                        PerimetryObject_V2.FinalPerimetryResultObject newObject = VFI_Utils.upgradePerimetryObject(oldObject);
                        String data = gson.toJson(newObject);
                        byte[] bytes = Base64.encode(data.getBytes(), Base64.NO_WRAP);
                        int records = DatabaseInitializer.updateTestData(AppDatabase.getAppDatabase(MyApplication.getInstance()), id, bytes, 2);
                        Log.e("Trackme", "Record Got Updated " + records);
                    }
                }
            } catch (Exception e) {
                Log.e("TrackMe", " " + e.getMessage());
                appPreferencesHelper.setDatabaseVersion(3);
            }

        }
        return null;
    }
}
