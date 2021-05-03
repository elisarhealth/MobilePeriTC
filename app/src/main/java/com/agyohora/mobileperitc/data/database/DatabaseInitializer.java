package com.agyohora.mobileperitc.data.database;

import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;

import com.agyohora.mobileperitc.data.database.entity.PatientInfo;
import com.agyohora.mobileperitc.data.database.entity.PatientTestResult;
import com.agyohora.mobileperitc.data.preferences.AppPreferencesHelper;
import com.agyohora.mobileperitc.interfaces.AsyncDbInsertRecordTask;
import com.agyohora.mobileperitc.myapplication.MyApplication;

import java.util.Date;
import java.util.List;

import static com.agyohora.mobileperitc.utils.AppConstants.PREF_NAME;

/**
 * Created by Invent on 20-12-17.
 */

public class DatabaseInitializer {
    private static final String TAG = DatabaseInitializer.class.getName();
    private static int insertedRow = 0;

    public static boolean populateAsyncTestResult(@NonNull final AppDatabase db, final String patName, final String patMrn, final String patMobile, final String patDOB, final String patSex, final String eye, final String testType, final String testPattern, final String suggestion, final int active, final byte[] data, String createdDate) {

        try {
            new InsertPatientInfo(db, patName, patMrn, patMobile, patDOB, patSex, new AsyncDbInsertRecordTask() {
                @Override
                public int onProcessFinish(int patInfoId) {
                    Log.d("patInfoId", "patInfoId " + patInfoId);
                    new PopulateResultAsync(db, patInfoId, patName, patMrn, patMobile, patDOB, patSex, eye, testType, testPattern, suggestion, active, data, createdDate, new AsyncDbInsertRecordTask() {
                        @Override
                        public int onProcessFinish(int b) {
                            insertedRow = b;
                            new AppPreferencesHelper(MyApplication.getInstance(), PREF_NAME).setLastInsertedRow("" + b);
                            return insertedRow;
                        }
                    }).execute();
                    return 0;
                }
            }).execute();
            /*new PopulateResultAsync(db, patName, patMrn, patMobile, patDOB, patSex, eye, testType,testPattern, suggestion, active, data, new AsyncDbInsertRecordTask() {
                @Override
                public int onProcessFinish(int b) {
                    insertedRow = b;
                    new AppPreferencesHelper(MyApplication.getInstance(), PREF_NAME).setLastInsertedRow("" + b);
                    return insertedRow;
                }
            }).execute();*/
        } catch (Exception e) {
            Log.e("DatabaseInitializer", "populateAsyncTestResult " + e.getMessage());
            return false;
        }
        return true;
    }

    public static void insertPatientInfo(@NonNull final AppDatabase db, final String patName, final String patMrn, final String patMobile, final String patDOB, final String patSex) {
        try {
            new InsertPatientInfo(db, patName, patMrn, patMobile, patDOB, patSex, new AsyncDbInsertRecordTask() {
                @Override
                public int onProcessFinish(int patInfoId) {
                    return 0;
                }
            }).execute();
        } catch (Exception e) {
            Log.e("insertPatientInfo", "" + e.getMessage());
        }
    }

    private static int insertResult(final AppDatabase db, PatientTestResult patientTestResult) {
        long result[] = db.testResultDao().insertAll(patientTestResult);
        Log.d("insertResult", " " + result[0]);
        return (int) result[0];
    }

    private static int insertPatientInfo(final AppDatabase db, PatientInfo patientInfo) {
        long result[] = db.patientInfoDao().insertAll(patientInfo);
        Log.d("insertResult", " " + result[0]);
        return (int) result[0];
    }

    private static int updatePatientInfo(final AppDatabase db, PatientInfo patientInfo) {
        int result = db.patientInfoDao().updateRecord(patientInfo.getPatientMrn(), patientInfo.getPatientName(), patientInfo.getPatientMobile(), patientInfo.getPatientDOB(), patientInfo.getPatientSex());
        Log.d("UpdatedInfo", " " + result);
        return result;
    }

    public static int getCount(@NonNull final AppDatabase db) {
        return db.testResultDao().countRows();
    }

    private static int getMrnCount(@NonNull final AppDatabase db, String mrn) {
        return db.patientInfoDao().countRows(mrn);
    }


    private static int populateWithResultData(AppDatabase db, int patInfoId, String patName, String patMrn, String patMobile, String patDOB, String patSex, String eye, String testType, String testPattern, String suggestion, int active, byte[] data, Date createDate) {
        data = Base64.encode(data, Base64.NO_WRAP);
        PatientTestResult patientTestResult = new PatientTestResult();
        patientTestResult.setPatientInfoId(patInfoId);
        patientTestResult.setPatientName(patName);
        patientTestResult.setPatientMrn(patMrn);
        patientTestResult.setPatientMobile(patMobile);
        patientTestResult.setPatientDOB(patDOB);
        patientTestResult.setPatientSex(patSex);
        patientTestResult.setTestEye(eye);
        patientTestResult.setTestType(testType);
        patientTestResult.setTestPattern(testPattern);
        patientTestResult.setTestSuggestion(suggestion);
        patientTestResult.setActive(active);
        patientTestResult.setResult(data);
        patientTestResult.setCreateDate(createDate);
        patientTestResult.setStatus(0);
        patientTestResult.setPerimeteryObjectVersion(2);
        return insertResult(db, patientTestResult);
    }

    public static List<PatientTestResult> getResultData(AppDatabase db) {
        return db.testResultDao().getAll();
    }

    public static List<PatientTestResult> getResultDataNotMigrated(AppDatabase db) {
        return db.testResultDao().getNotMigrated();
    }

    public static List<PatientTestResult> getUnSyncedData(AppDatabase db) {
        return db.testResultDao().getUnSynced();
    }

    public static int getNotMigratedData(AppDatabase db) {
        return db.testResultDao().getNotMigratedCount();
    }

    public static List<PatientTestResult> getLastSixty(AppDatabase db) {
        return db.testResultDao().getLastSixty();
    }

    public static List<PatientTestResult> getAbstractData(AppDatabase db) {
        return db.testResultDao().listAbstractDetails();
    }

    public static List<PatientTestResult> getMrns(AppDatabase db) {
        return db.testResultDao().listMrnDetails();
    }

    public static PatientRecordFound getPatientInfo(AppDatabase db, String mrn) {
        return db.testResultDao().findUserDetails(mrn);
    }

    public static PatientRecordFound getPatientInfoNew(AppDatabase db, String mrn) {
        return db.patientInfoDao().findByMrn(mrn);
    }

    public static PatientTestResult getById(AppDatabase db, String id) {
        return db.testResultDao().findById(id);
    }

    public static int deleteRecord(AppDatabase db, String id) {
        return db.testResultDao().deleteRecord(id);
    }

    public static void syncedRecord(AppDatabase db, String id) {
        db.testResultDao().syncedRecord(id);
    }

    public static int updateTestData(AppDatabase db, String id, byte[] data, int perimetryObjectVersion) {
        return db.testResultDao().updateTestData(id, data, perimetryObjectVersion);
    }


    static class PopulateResultAsync extends AsyncTask<Void, Void, Integer> {

        private AsyncDbInsertRecordTask delegate = null;
        private final AppDatabase mDb;
        private final byte[] mData;
        private final int mPatInfoId;
        private final String mPatName;
        private final String mPatMrn;
        private final String mPatMobile;
        private final String mPatDOB;
        private final String mPatSex;
        private final String mPatEye;
        private final String mTestType;
        private final String mTestPattern;
        private final String mTestSuggestion;
        private final int mActive;
        private final Date createdDate;

        PopulateResultAsync(AppDatabase db, int patInfoId, String patName, String patMrn, String patMobile, String patDOB, String patSex, String eye, String testType, String testPattern, String suggestion, int active, byte[] data, String date, AsyncDbInsertRecordTask delegate) {
            this.delegate = delegate;
            mDb = db;
            mPatInfoId = patInfoId;
            mData = data;
            mPatName = patName;
            mPatMrn = patMrn;
            mPatMobile = patMobile;
            mPatDOB = patDOB;
            mPatSex = patSex;
            mActive = active;
            mPatEye = eye;
            mTestType = testType;
            mTestPattern = testPattern;
            mTestSuggestion = suggestion;
            createdDate = new Date(Long.parseLong(date));
        }

        @Override
        protected Integer doInBackground(final Void... params) {
            Log.d("DIB patName", "" + mPatName);
            Log.d("DIB mPatInfoId", "" + mPatInfoId);
            Log.d("DIB patMrn", "" + mPatMrn);
            Log.d("DIB patMobile", "" + mPatMobile);
            Log.d("DIB patAge", "" + mPatDOB);
            Log.d("DIB patSex", "" + mPatSex);
            Log.d("DIB patEye", "" + mPatEye);
            Log.d("DIB testType", "" + mTestType);
            Log.d("DIB testPattern", "" + mTestPattern);
            Log.d("DIB suggestion", "" + mTestSuggestion);
            return populateWithResultData(mDb, mPatInfoId, mPatName, mPatMrn, mPatMobile, mPatDOB, mPatSex, mPatEye, mTestType, mTestPattern, mTestSuggestion, mActive, mData, createdDate);
        }

        @Override
        protected void onPostExecute(Integer s) {
            Log.d("onPostExecute", "status " + s);
            delegate.onProcessFinish(s);
        }
    }

    static class InsertPatientInfo extends AsyncTask<Void, Void, Integer> {
        private AsyncDbInsertRecordTask delegate = null;
        private final AppDatabase mDb;
        private final String mPatName;
        private final String mPatMrn;
        private final String mPatMobile;
        private final String mPatDOB;
        private final String mPatSex;

        InsertPatientInfo(AppDatabase db, String patName, String patMrn, String patMobile, String patDOB, String patSex, AsyncDbInsertRecordTask delegate) {
            this.delegate = delegate;
            mDb = db;
            mPatName = patName;
            mPatMrn = patMrn;
            mPatMobile = patMobile;
            mPatDOB = patDOB;
            mPatSex = patSex;
        }

        @Override
        protected Integer doInBackground(final Void... params) {
            PatientInfo patientInfo = new PatientInfo();
            patientInfo.setPatientName(mPatName);
            patientInfo.setPatientMrn(mPatMrn);
            patientInfo.setPatientMobile(mPatMobile);
            patientInfo.setPatientDOB(mPatDOB);
            patientInfo.setPatientSex(mPatSex);
            if (getMrnCount(mDb, mPatMrn) > 0) {
                Log.d("InsertPatientInfo", "updatePatientInfo ");
                return updatePatientInfo(mDb, patientInfo);
            } else {
                Log.d("InsertPatientInfo", "insertPatientInfo");
                return insertPatientInfo(mDb, patientInfo);
            }
        }

        @Override
        protected void onPostExecute(Integer s) {
            Log.d("InsertPatientInfo", "On Post Execute status " + s);
            delegate.onProcessFinish(s);
        }

    }

}

