package com.agyohora.mobileperitc.asynctasks;

import android.os.AsyncTask;
import android.util.Log;

import com.agyohora.mobileperitc.data.database.AppDatabase;
import com.agyohora.mobileperitc.data.database.PatientRecordFound;
import com.agyohora.mobileperitc.interfaces.AsyncDbTaskString;
import com.agyohora.mobileperitc.ui.MainActivity;

import java.util.ArrayList;

import static com.agyohora.mobileperitc.data.database.DatabaseInitializer.getPatientInfoNew;

/**
 * Created by Invent on 6-3-18.
 * @see AsyncTask
 * Async task to search patient detials in test report listview
 */

public class SearchPatientBasicDetails extends AsyncTask<String, String, ArrayList<String>> {

    private AsyncDbTaskString delegate = null;

    public SearchPatientBasicDetails(AsyncDbTaskString delegate) {
        this.delegate = delegate;
    }

    @Override
    protected ArrayList<String> doInBackground(String... string) {
        ArrayList<String> result = new ArrayList<>();
        String patMrn = string[0];
        //PatientRecordFound patientRecordFound = getPatientInfo(AppDatabase.getAppDatabase(MainActivity.applicationContext), patMrn);
        PatientRecordFound patientRecordFound = getPatientInfoNew(AppDatabase.getAppDatabase(MainActivity.applicationContext), patMrn);
        if (patientRecordFound != null) {
            String age = patientRecordFound.getPatientDOB();
            String mobile = patientRecordFound.getPatientMobile();
            String name = patientRecordFound.getPatientName();
            String sex = patientRecordFound.getPatientSex();
            Log.d("SearchedAge", " " + age);
            Log.d("Searchedmobile", " " + mobile);
            Log.d("Searchedname", " " + name);
            Log.d("Searchedsex", " " + sex);
            if (age != null && mobile != null && name != null && sex != null) {
                result.add(age);
                result.add(mobile);
                result.add(name);
                result.add(sex);
            }
            return result;
        } else {
            return new ArrayList<>();
        }
    }

    @Override
    protected void onProgressUpdate(String... string) {
        super.onProgressUpdate(string);
    }

    @Override
    protected void onPostExecute(ArrayList<String> strings) {
        delegate.onProcessFinish(strings);
    }
}

