package com.agyohora.mobileperitc.asynctasks;

import android.os.AsyncTask;
import android.util.Log;

import com.agyohora.mobileperitc.data.database.AppDatabase;
import com.agyohora.mobileperitc.myapplication.MyApplication;
import com.agyohora.mobileperitc.ui.EssentialDataActivity;

public class TriggerTheUpgrade extends AsyncTask<Void, Void, Void> {
    @Override
    protected Void doInBackground(Void... voids) {
        AppDatabase appDatabase = AppDatabase.getAppDatabase(MyApplication.getInstance());

        int version = appDatabase.getOpenHelper().getWritableDatabase().getVersion();
        Log.e("TrackMe", "Database Version " + version);
        if(version == 3){
            new FillTheVFI(patientTestResults -> {
            }, EssentialDataActivity.essentialDataActivityReference).execute();
        }
        return null;
    }
}
