package com.agyohora.mobileperitc.worksheduler.works;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.agyohora.mobileperitc.data.database.AppDatabase;
import com.agyohora.mobileperitc.data.database.entity.PatientTestResult;
import com.agyohora.mobileperitc.myapplication.MyApplication;
import com.agyohora.mobileperitc.utils.CommonUtils;

import org.json.JSONObject;

import java.util.List;

import static com.agyohora.mobileperitc.data.database.DatabaseInitializer.getLastSixty;
import static com.agyohora.mobileperitc.utils.CommonUtils.bytesToJsonObject;

@SuppressWarnings("AccessStaticViaInstance")
public class SaveAsJsonWorker extends Worker {

    private static final String TAG = SaveAsJsonWorker.class.getSimpleName();

    public SaveAsJsonWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        /*Gson gson = new GsonBuilder().excludeFieldsWithModifiers(Modifier.TRANSIENT).serializeSpecialFloatingPointValues()
                .create();
        ArrayList<Integer> dt_result_sensitivity = new ArrayList<>();
        JSONArray mJSONArray;
        JSONObject jsonObject;*/
        List<PatientTestResult> patientTestResults = getLastSixty(AppDatabase.getAppDatabase(MyApplication.getInstance()));
        Log.e(TAG, " Data Size " + patientTestResults.size());

        try {
            /*int j = 1;
            if (patientTestResults.size() > 0)
                for (PatientTestResult patientTestResult : patientTestResults) {
                    //jsonObject = new JSONObject();
                    String mrns[] = {"FT01_7871252606", "FT01_9894612302", "FT01_8939643368"};
                    String mrnNumber = patientTestResult.getPatientMrn();
                    mrnNumber = mrnNumber != null ? mrnNumber : "MRN";
                    mrnNumber = mrnNumber.replace("/","-");
                    Log.e(TAG, "mrnNumber " + mrnNumber);
                    //if (Arrays.asList(mrns).contains(mrnNumber)) {
                    String createdAt = patientTestResult.getCreateDate().toString();
                    String fileName = mrnNumber + "_" + createdAt;
                    jsonObject = new JSONObject();
                    String data = new String(Base64.decode(patientTestResult.getResult(), Base64.NO_WRAP));
                    PerimetryObject.FinalPerimetryResultObject finalPerimetryTestFinalResultObject = gson.fromJson(data, PerimetryObject.FinalPerimetryResultObject.class);
                    int length = finalPerimetryTestFinalResultObject.Measurements.VisualFieldStaticPerimetryTestMeasurements.VisualFieldTestPointSequence.length;
                    for (int i = 0; i < length; i++) {
                        int s = (int) finalPerimetryTestFinalResultObject.Measurements.VisualFieldStaticPerimetryTestMeasurements.VisualFieldTestPointSequence[i].SensitivityValue;
                        dt_result_sensitivity.add(s);
                    }
                    mJSONArray = new JSONArray(Collections.singletonList(dt_result_sensitivity));
                    jsonObject.put("SensitivityValue", mJSONArray);
                    jsonObject.put("length", length);
                    CommonUtils.writeSensitiveValues(jsonObject, fileName);
                    dt_result_sensitivity.clear();
                        *//*String createdAt = patientTestResult.getCreateDate().toString();
                        String fileName = mrnNumber + "_" + createdAt;
                        String data = new String(Base64.decode(patientTestResult.getResult(), Base64.NO_WRAP));
                        jsonObject = new JSONObject(data);
                        CommonUtils.writeSensitiveValues(jsonObject, fileName);
                        dt_result_sensitivity.clear();
                        Log.e("writeSensitiveValues", "Saved " + j);
                        j++;*//*
                    //}
                    *//*PerimetryObject.FinalPerimetryResultObject finalPerimetryTestFinalResultObject = gson.fromJson(data, PerimetryObject.FinalPerimetryResultObject.class);
                    int length =finalPerimetryTestFinalResultObject.Measurements.VisualFieldStaticPerimetryTestMeasurements.VisualFieldTestPointSequence.length;
                    for (int i = 0; i < length; i++) {
                        int s = (int) finalPerimetryTestFinalResultObject.Measurements.VisualFieldStaticPerimetryTestMeasurements.VisualFieldTestPointSequence[i].SensitivityValue;
                        dt_result_sensitivity.add(s);
                    }
                    mJSONArray = new JSONArray(Arrays.asList(dt_result_sensitivity));
                    jsonObject.put("SensitivityValue", mJSONArray);*//*
                    //jsonObject.put("data", data);

                }*/
            if (patientTestResults.size() > 0)
                for (PatientTestResult patientTestResult : patientTestResults) {
                    String mrnNumber = patientTestResult.getPatientMrn();
                    String createdAt = patientTestResult.getCreateDate().toString();
                    String fileName = CommonUtils.removeSpecialChars(mrnNumber) + "_" + createdAt;
                    JSONObject dataReceived = bytesToJsonObject(patientTestResult.getResult());
                    if (dataReceived != null)
                        CommonUtils.writeJson(dataReceived, MyApplication.getInstance(), fileName);
                }
            return Result.success();
        } catch (Exception exception) {
            Log.e(TAG, " " + exception.getMessage());
            return Result.failure();
        }
    }
}
