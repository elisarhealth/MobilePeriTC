package com.agyohora.mobileperitc.asynctasks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.agyohora.mobileperitc.R;
import com.agyohora.mobileperitc.actions.Actions;
import com.agyohora.mobileperitc.data.database.AppDatabase;
import com.agyohora.mobileperitc.data.database.DatabaseInitializer;
import com.agyohora.mobileperitc.data.database.OldDatabase;
import com.agyohora.mobileperitc.data.database.entity.PatientInfo;
import com.agyohora.mobileperitc.data.database.entity.PatientTestResult;
import com.agyohora.mobileperitc.interfaces.MergeDatabaseResult;
import com.agyohora.mobileperitc.store.PerimetryObject_V2;
import com.agyohora.mobileperitc.ui.MainActivity;
import com.agyohora.mobileperitc.utils.CommonUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MergeDatabase extends AsyncTask<String, String, Bundle> {

    private MergeDatabaseResult delegate = null;
    private ProgressDialog dialog;
    private Activity activity;
    TextView index;
    int recordsInserted = 0, recordMismatched = 0, recordsDuplicated = 0;

    public MergeDatabase(MergeDatabaseResult delegate, Activity activity) {
        this.delegate = delegate;
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        try {
            Log.e("onPreExecute", "Executed");
            dialog = ProgressDialog.show(activity, null, null, true, false);
            dialog.setContentView(R.layout.database_merging_dialog);
            index = dialog.findViewById(R.id.index);
        } catch (Exception e) {
            Log.e("TrackMe", " " + e.getMessage());
        }
    }

    @Override
    protected Bundle doInBackground(String... strings) {
        Log.e("doInBackground", "Executed");
        boolean isRecordsMismatched = false;
        AppDatabase appDatabase = AppDatabase.getAppDatabase(MainActivity.applicationContext);
        OldDatabase oldDatabase = OldDatabase.getAppDatabase(MainActivity.applicationContext);
        Gson gson = new GsonBuilder().excludeFieldsWithModifiers(Modifier.TRANSIENT).create();

        //Getting all patient info from Old DB
        List<PatientInfo> patientInfoFromOldDB = DatabaseInitializer.getAllPatientInfoFromOldDB(oldDatabase);
        Log.e("patientInfoFromOldDB", "Size " + patientInfoFromOldDB.size());
        int totalNumberOfRecords = DatabaseInitializer.getNumberOfTestRecordsOfOldBD(oldDatabase);
        //loop every patient info
        for (PatientInfo oldDBPatientInfo : patientInfoFromOldDB) {

            String oldDBPatientInfoPatientMrn = oldDBPatientInfo.getPatientMrn();
            Log.e("oldDB Patient Mrn", " " + oldDBPatientInfoPatientMrn);
            //Checking if MRN of old db is used in new db
            List<PatientInfo> matchedPatientInfo = DatabaseInitializer.getAllPatientInfoByMrn(appDatabase, oldDBPatientInfoPatientMrn);
            List<PatientTestResult> allTestResultsFromOldDB = DatabaseInitializer.getAllTestResultsFromOldDB(oldDatabase, oldDBPatientInfoPatientMrn);
            int count = matchedPatientInfo.size();
            Log.e("getMrnCount", " " + count);
            if (count > 0) {
                PatientTestResult patientTestResult = DatabaseInitializer.getByMRN(appDatabase, matchedPatientInfo.get(0).getPatientMrn());
                String patientName = patientTestResult.getPatientName();
                //OLD db mrn and new db mrn matches, then checking patient name matches or not
                if (!patientName.equals((oldDBPatientInfo.getPatientName()))) {
                    //patient name is matching so, writing all the test result to text file
                    if (allTestResultsFromOldDB.size() > 0) {
                        Log.e("TrackMe ", "mrn matches but name mismatches " + oldDBPatientInfoPatientMrn);
                        isRecordsMismatched = true;
                        for (PatientTestResult patientTestResult1 : allTestResultsFromOldDB) {
                            recordMismatched++;
                            publishProgress("Records inserted " + recordsInserted + "\nRecords mismatched " + recordMismatched + "\nRecords Duplicated " + recordsDuplicated);

                            JSONObject dataReceived = CommonUtils.bytesToJsonObject(patientTestResult1.getResult());
                            CommonUtils.writeToDatabaseMergingLogFIle(dataReceived.toString());

                        }
                    }
                } else {
                    //patient name is not matching, so we map the old database details into new database against new database's patient info id
                    insertIntoDb(gson, allTestResultsFromOldDB, appDatabase);
                }
            } else {
                insertIntoDb(gson, allTestResultsFromOldDB, appDatabase);
            }
        }

        Bundle bundle = new Bundle();
        bundle.putInt("totalNumberOfRecords", totalNumberOfRecords);
        bundle.putInt("recordMismatched", recordMismatched);
        bundle.putInt("recordsInserted", recordsInserted);
        bundle.putInt("recordsDuplicated", recordsDuplicated);
        return bundle;
    }

    @Override
    protected void onPostExecute(Bundle bundle) {
        int mismatched = bundle.getInt("recordMismatched");
        int total = bundle.getInt("totalNumberOfRecords");
        Log.e("onPostExecute", "Executed");
        Log.e("mismatched", " " + mismatched);
        Log.e("total", " " + total);
        if (dialog.isShowing())
            dialog.dismiss();
        if (mismatched > 0) {
            Actions.uploadDataRestoreLogs(recordsInserted + " " + recordMismatched + " " + recordsDuplicated + " " + total);
        } else {
            Actions.dataRestoreFinished(recordsInserted + " " + recordMismatched + " " + recordsDuplicated + " " + total);
        }
       // OldDatabase oldDatabase = OldDatabase.getAppDatabase(MainActivity.applicationContext);
        //oldDatabase.close();
        activity.getApplicationContext().deleteDatabase("old-patient-database");
    }

    void insertIntoDb(Gson gson, List<PatientTestResult> allTestResultsFromOldDB, AppDatabase appDatabase) {
        for (PatientTestResult patientTestResult1 : allTestResultsFromOldDB) {
            JSONObject jsonObject = CommonUtils.bytesToJsonObject(patientTestResult1.getResult());
            if (jsonObject != null) {
                PerimetryObject_V2.FinalPerimetryResultObject finalPerimetryTestResultObject = gson.fromJson(jsonObject.toString(), PerimetryObject_V2.FinalPerimetryResultObject.class);
                if (!DatabaseInitializer.findIsTestDataAlreadyThere(appDatabase, patientTestResult1.getPatientMrn(), patientTestResult1.getPatientName(), patientTestResult1.getTestType(), patientTestResult1.getTestPattern(), formatDate(patientTestResult1.getCreateDate()))) {
                    Log.e("CreatedDate", " Records not present");
                    recordsInserted++;
                    publishProgress("Records inserted " + recordsInserted + " Records mismatched " + recordMismatched + " Records Duplicated " + recordsDuplicated);
                    new InsertRecordIntoDb(b -> {
                        Log.e("InsertedID", " " + b);
                        return b;
                    }).execute(patientTestResult1.getPatientName(), patientTestResult1.getPatientMrn(), patientTestResult1.getPatientMobile(), patientTestResult1.getPatientDOB(), patientTestResult1.getPatientSex(), patientTestResult1.getTestEye(),
                            patientTestResult1.getTestType(), patientTestResult1.getTestPattern(), patientTestResult1.getTestSuggestion(), gson.toJson(finalPerimetryTestResultObject), "" + patientTestResult1.getCreateDate().getTime());
                } else {
                    Log.e("CreatedDate", " Records present");
                    recordsDuplicated++;
                    publishProgress("Records inserted " + recordsInserted + " Records mismatched " + recordMismatched + " Records Duplicated " + recordsDuplicated);
                }
            }
        }
    }

    @Override
    protected void onProgressUpdate(String... string) {
        super.onProgressUpdate(string);
        index.setText(string[0]);
    }


    String formatDate(Date date) {

        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss aaa", Locale.US);
            String stringDate = sdf.format(date);
            Log.e("stringDate", " " + stringDate);
            return stringDate;
        } catch (Exception e) {
            Log.e("stringDateToMillis", " " + e.getMessage());
        }
        return "";
    }

}
