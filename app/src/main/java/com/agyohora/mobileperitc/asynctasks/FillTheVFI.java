package com.agyohora.mobileperitc.asynctasks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.widget.TextView;

import com.agyohora.mobileperitc.R;
import com.agyohora.mobileperitc.data.database.AppDatabase;
import com.agyohora.mobileperitc.data.database.DatabaseInitializer;
import com.agyohora.mobileperitc.data.database.entity.PatientTestResult;
import com.agyohora.mobileperitc.data.preferences.AppPreferencesHelper;
import com.agyohora.mobileperitc.interfaces.AsyncDbTaskResult;
import com.agyohora.mobileperitc.store.PerimetryObject;
import com.agyohora.mobileperitc.store.PerimetryObject_V2;
import com.agyohora.mobileperitc.ui.MainActivity;
import com.agyohora.mobileperitc.utils.CommonUtils;
import com.agyohora.mobileperitc.utils.VFI_Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.lang.reflect.Modifier;
import java.util.List;

import static com.agyohora.mobileperitc.data.database.DatabaseInitializer.getResultDataNotMigrated;
import static com.agyohora.mobileperitc.utils.AppConstants.DEVICE_PREF;

public class FillTheVFI extends AsyncTask<String, String, Bundle> {

    private AsyncDbTaskResult delegate = null;
    private ProgressDialog dialog;
    private Activity activity;
    TextView index;

    public FillTheVFI(AsyncDbTaskResult delegate, Activity activity) {
        this.delegate = delegate;
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        try {
            dialog = ProgressDialog.show(activity, null, null, true, false);
            dialog.setContentView(R.layout.database_upgrade_dialog);
            index = dialog.findViewById(R.id.index);
        } catch (Exception e) {
            Log.e("TrackMe", " " + e.getMessage());
        }
    }

    @Override
    protected Bundle doInBackground(String... string) {
        try {
            Gson gson = new GsonBuilder().excludeFieldsWithModifiers(Modifier.TRANSIENT)
                    .serializeSpecialFloatingPointValues()
                    .create();
            List<PatientTestResult> patientTestResults = getResultDataNotMigrated(AppDatabase.getAppDatabase(MainActivity.applicationContext));
            AppPreferencesHelper appPreferencesHelper = new AppPreferencesHelper(activity, DEVICE_PREF);
            int size = patientTestResults.size();
            if (patientTestResults.size() > 0) {
                try {
                    appPreferencesHelper.setDatabaseVersion(3);
                    int i = 1;
                    for (PatientTestResult testResult : patientTestResults) {
                        String id = "" + testResult.getId();
                        Log.e("TrackMe", "Record ID " + id);
                        String message = i + "/" + size + " records updated";
                        publishProgress(message);
                        i++;
                        JSONObject dataReceived = CommonUtils.bytesToJsonObject(testResult.getResult());
                        if (dataReceived != null) {
                            CommonUtils.writeToBugFixLogFile("\nRecord Id " + id);
                            PerimetryObject.FinalPerimetryResultObject oldObject = gson.fromJson(dataReceived.toString(), PerimetryObject.FinalPerimetryResultObject.class);
                            PerimetryObject_V2.FinalPerimetryResultObject newObject = VFI_Utils.upgradePerimetryObject(oldObject);
                            String data = gson.toJson(newObject);
                            byte[] bytes = Base64.encode(data.getBytes(), Base64.NO_WRAP);
                            int records = DatabaseInitializer.updateTestData(AppDatabase.getAppDatabase(MainActivity.applicationContext), id, bytes, 2);
                            Log.e("Trackme", "Record Got Updated " + records);
                        }
                    }
                } catch (Exception e) {
                    Log.e("TrackMe", " " + e.getMessage());
                }
                return null;

            } else {
                appPreferencesHelper.setDatabaseVersion(3);
                Log.e("TrackMe", "GetResultData Json Null");
                Log.e("TrackMe", "Json Null");
                if (dialog.isShowing())
                    dialog.dismiss();
                Intent intent = new Intent("VFI_WORK_FINISHED");
                activity.sendBroadcast(intent);
                return null;
            }
        } catch (Exception e) {
            Log.e("TrackMe", " " + e.getMessage());
            return null;
        }
    }

    @Override
    protected void onProgressUpdate(String... string) {
        super.onProgressUpdate(string);
        index.setText(string[0]);
    }

    @Override
    protected void onPostExecute(Bundle patientTestResult) {
        Log.e("TrackMe", "onPostExecute Fill the VFi ");
        if (dialog.isShowing())
            dialog.dismiss();
        Intent intent = new Intent("VFI_WORK_FINISHED");
        activity.sendBroadcast(intent);
    }
}
