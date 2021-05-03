package com.agyohora.mobileperitc.asynctasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.agyohora.mobileperitc.data.database.AppDatabase;
import com.agyohora.mobileperitc.data.database.DatabaseInitializer;
import com.agyohora.mobileperitc.interfaces.AsyncDbDeleteRecordTask;

public class CountOfNotMigratedData extends AsyncTask<String, String, Boolean> {

    private AsyncDbDeleteRecordTask delegate = null;
    private Context context;

    public CountOfNotMigratedData(AsyncDbDeleteRecordTask delegate, Context context) {
        this.delegate = delegate;
        this.context = context;
    }

    @Override
    protected Boolean doInBackground(String... string) {
        int records = DatabaseInitializer.getNotMigratedData(AppDatabase.getAppDatabase(context));
        Log.e("Records Not Migrated", " " + records);
        return records > 0;
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

