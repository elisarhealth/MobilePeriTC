package com.agyohora.mobileperitc.asynctasks;

import android.os.AsyncTask;
import android.util.Log;

import com.agyohora.mobileperitc.data.database.AppDatabase;
import com.agyohora.mobileperitc.data.database.DatabaseInitializer;
import com.agyohora.mobileperitc.data.preferences.AppPreferencesHelper;
import com.agyohora.mobileperitc.interfaces.AsyncDbInsertRecordTask;
import com.agyohora.mobileperitc.myapplication.MyApplication;
import com.agyohora.mobileperitc.ui.MainActivity;

import static com.agyohora.mobileperitc.utils.AppConstants.PREF_NAME;

/**
 * Created by Invent on 6-3-18.
 *
 * @see android.os.AsyncTask
 * Async task to insert the test data into database
 */

public class InsertRecordIntoDb extends AsyncTask<String, String, Boolean> {

    private AsyncDbInsertRecordTask delegate = null;

    public InsertRecordIntoDb(AsyncDbInsertRecordTask delegate) {
        this.delegate = delegate;
    }

    @Override
    protected Boolean doInBackground(String... string) {
        String patName = string[0];
        String patMrn = string[1];
        String patMobile = string[2];
        String patAge = string[3];
        String patSex = string[4];
        String patEye = string[5];
        String testType = string[6];
        String testPattern = string[7];
        String suggestion = string[8];
        String data = string[9];
        String date = string[10];
        Log.d("Insert patName", "" + patName);
        Log.d("Insert patMrn", "" + patMrn);
        Log.d("Insert patMobile", "" + patMobile);
        Log.d("Insert patAge", "" + patAge);
        Log.d("Insert patSex", "" + patSex);
        Log.d("Insert patEye", "" + patEye);
        Log.d("Insert testType", "" + testType);
        Log.d("Insert testPattern", "" + testPattern);
        Log.d("Insert suggestion", "" + suggestion);
        //CommonUtils.writeToFile(data, MainActivity.applicationContext);
        boolean insertedRow = DatabaseInitializer.populateAsyncTestResult(AppDatabase.getAppDatabase(MainActivity.applicationContext), patName, patMrn, patMobile, patAge, patSex, patEye, testType, testPattern, suggestion, 1, data.getBytes(), date);
        Log.d("InsertedID", "InsertRecordIntoDB " + insertedRow);
        return insertedRow;
    }

    @Override
    protected void onProgressUpdate(String... string) {
        super.onProgressUpdate(string);
    }

    @Override
    protected void onPostExecute(Boolean b) {
        String rowId = new AppPreferencesHelper(MyApplication.getInstance(), PREF_NAME).getLastInsertedRow();
        Log.d("Inserted", "From InsertRecordIntoDb " + rowId);
        delegate.onProcessFinish(1);
    }
}
