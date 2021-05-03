package com.agyohora.mobileperitc.asynctasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import com.agyohora.mobileperitc.data.database.AppDatabase;
import com.agyohora.mobileperitc.data.database.DatabaseInitializer;
import com.agyohora.mobileperitc.data.database.entity.PatientTestResult;
import com.agyohora.mobileperitc.interfaces.AsyncDbDeleteRecordTask;
import com.agyohora.mobileperitc.store.PerimetryObject;
import com.agyohora.mobileperitc.store.PerimetryObject_V2;
import com.agyohora.mobileperitc.ui.MainActivity;
import com.agyohora.mobileperitc.utils.CommonUtils;
import com.agyohora.mobileperitc.utils.VFI_Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.lang.reflect.Modifier;

import static com.agyohora.mobileperitc.data.database.DatabaseInitializer.getById;

public class UpdateTestRecord extends AsyncTask<String, String, Boolean> {

    private AsyncDbDeleteRecordTask delegate = null;
    private Context context;

    public UpdateTestRecord(AsyncDbDeleteRecordTask delegate, Context context) {
        this.delegate = delegate;
        this.context = context;
    }

    @Override
    protected Boolean doInBackground(String... string) {

        Gson gson = new GsonBuilder().excludeFieldsWithModifiers(Modifier.TRANSIENT)
                .serializeSpecialFloatingPointValues()
                .create();

        String id = string[0];
        String time = string[1];

        PatientTestResult patientTestResult = getById(AppDatabase.getAppDatabase(MainActivity.applicationContext), string[0]);

        JSONObject dataReceived = CommonUtils.bytesToJsonObject(patientTestResult.getResult());
        if (dataReceived != null) {
            PerimetryObject.FinalPerimetryResultObject oldObject = gson.fromJson(dataReceived.toString(), PerimetryObject.FinalPerimetryResultObject.class);
            PerimetryObject_V2.FinalPerimetryResultObject newObject = VFI_Utils.upgradePerimetryObject(oldObject);
            String data = gson.toJson(newObject);
            byte[] bytes = Base64.encode(data.getBytes(), Base64.NO_WRAP);
            int records = DatabaseInitializer.updateTestData(AppDatabase.getAppDatabase(MainActivity.applicationContext), id, bytes, 2);
            Log.e("Trackme", "Record Got Updated " + records);
            return records > 0;
        }
        return false;
    }

    @Override
    protected void onProgressUpdate(String... string) {
        super.onProgressUpdate(string);
    }

    @Override
    protected void onPostExecute(Boolean b) {
        delegate.onProcessFinish(b);
    }
}
