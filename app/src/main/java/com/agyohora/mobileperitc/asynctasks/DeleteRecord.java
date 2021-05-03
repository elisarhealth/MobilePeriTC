package com.agyohora.mobileperitc.asynctasks;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import com.agyohora.mobileperitc.data.database.AppDatabase;
import com.agyohora.mobileperitc.data.database.DatabaseInitializer;
import com.agyohora.mobileperitc.interfaces.AsyncDbDeleteRecordTask;

/**
 * Created by Invent on 13-3-18.
 * @see android.os.AsyncTask
 * Async task to delete a report from database using ID
 */

public class DeleteRecord extends AsyncTask<String, String, Boolean> {

    private AsyncDbDeleteRecordTask delegate = null;
    private Context context;

    public DeleteRecord(AsyncDbDeleteRecordTask delegate, Context context) {
        this.delegate = delegate;
        this.context = context;
    }

    @Override
    protected Boolean doInBackground(String... string) {
        String id = string[0];
        int records = DatabaseInitializer.deleteRecord(AppDatabase.getAppDatabase(context), id);
        Log.d("Records Deleted", " " + records);
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
