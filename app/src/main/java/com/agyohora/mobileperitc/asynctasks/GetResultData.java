package com.agyohora.mobileperitc.asynctasks;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.agyohora.mobileperitc.R;
import com.agyohora.mobileperitc.data.database.AppDatabase;
import com.agyohora.mobileperitc.data.database.entity.PatientTestResult;
import com.agyohora.mobileperitc.interfaces.AsyncDbTaskResult;
import com.agyohora.mobileperitc.store.Interpolation;
import com.agyohora.mobileperitc.store.PerimetryObject_V2;
import com.agyohora.mobileperitc.ui.DoctorReportActivity;
import com.agyohora.mobileperitc.ui.MainActivity;
import com.agyohora.mobileperitc.ui.ScreeningReportActivity;
import com.agyohora.mobileperitc.ui.TestReport;
import com.agyohora.mobileperitc.utils.CommonUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.lang.reflect.Modifier;
import java.text.Format;
import java.util.ArrayList;

import static com.agyohora.mobileperitc.data.database.DatabaseInitializer.getById;
import static com.agyohora.mobileperitc.utils.CommonUtils.durationCalculation;

/**
 * Created by Invent on 6-3-18.
 *
 * @see android.os.AsyncTask
 * Async task to fetch the result from database, wrap it into bundle and pass it to necessary activity
 */

@SuppressWarnings("AccessStaticViaInstance")
public class GetResultData extends AsyncTask<String, String, Bundle> {

    private AsyncDbTaskResult delegate = null;
    private ProgressDialog dialog;
    private Activity activity;

    public GetResultData(AsyncDbTaskResult delegate, Activity activity) {
        this.delegate = delegate;
        this.activity = activity;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        try {
            dialog = ProgressDialog.show(activity, null, null, true, false);
            dialog.setContentView(R.layout.progress_layout);
            dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        } catch (Exception e) {
            Log.e("Exception", "onPreExecute of get result data " + e.getMessage());
        }
    }

    @Override
    protected Bundle doInBackground(String... string) {
        PatientTestResult patientTestResult = getById(AppDatabase.getAppDatabase(MainActivity.applicationContext), string[0]);
        ArrayList<String> dt_new_result = new ArrayList<>();
        ArrayList<String> dt_result_sensitivity = new ArrayList<>();
        ArrayList<String> dt_result_deviation = new ArrayList<>();
        ArrayList<String> dt_result_probabilityDeviationValue = new ArrayList<>();
        ArrayList<String> dt_result_generalizedDefectCorrectedSensitivityDeviationValue = new ArrayList<>();
        ArrayList<String> dt_result_generalizedDefectCorrectedSensitivityDeviationProbabilityValue = new ArrayList<>();
        ArrayList<String> dt_result_seen = new ArrayList<>();
        Bundle bundle = new Bundle();
        Gson gson = new GsonBuilder().excludeFieldsWithModifiers(Modifier.TRANSIENT).create();
        JSONObject dataReceived = CommonUtils.bytesToJsonObject(patientTestResult.getResult());

        if (dataReceived != null) {
            PerimetryObject_V2.FinalPerimetryResultObject finalPerimetryTestFinalResultObject = gson.fromJson(dataReceived.toString(), PerimetryObject_V2.FinalPerimetryResultObject.class);
            CommonUtils.writeToFile(finalPerimetryTestFinalResultObject.toString(), "pointsmissingafter");
            bundle.putString("ResultId", string[0]);
            bundle.putString("TestedAt", string[1]);
            CommonUtils.writeToFile(dataReceived.toString(), "ResultId " + string[0]);
            Interpolation interpolation = new Interpolation();
            double[][][] quadrants = interpolation.getGreyScaleVals(finalPerimetryTestFinalResultObject);
            bundle.putSerializable("quadrants", quadrants);
            bundle.putString("setPatientTestEyeVal", finalPerimetryTestFinalResultObject.Series.Laterality.equals("L") ? "Left Eye" : "Right Eye");
            bundle.putString("setPatientName", finalPerimetryTestFinalResultObject.Patient.PatientName);
            Log.d("Name", " " + finalPerimetryTestFinalResultObject.Patient.PatientName);
            bundle.putString("setPatientSexVal", finalPerimetryTestFinalResultObject.Patient.PatientSex);
            bundle.putString("setPatientMrnNumberVal", finalPerimetryTestFinalResultObject.Patient.PatientUID);
            bundle.putString("setPatientDOBVal", finalPerimetryTestFinalResultObject.Patient.PatientBirthDate);
            bundle.putString("setPatientTestStrategyVal", finalPerimetryTestFinalResultObject.Series.StrategySequence.CodeMeaning);
            String pattern = finalPerimetryTestFinalResultObject.Series.PatternSequence.CodeMeaning;
            bundle.putString("setPatientTestPatternVal", pattern);
            int FL_Denominator = finalPerimetryTestFinalResultObject.Measurements.VisualFieldStaticPerimetryTestReliability.FixationSequence.FixationCheckedQuantity;
            int FL_Numerator = finalPerimetryTestFinalResultObject.Measurements.VisualFieldStaticPerimetryTestReliability.FixationSequence.PatientNotProperlyFixatedQuantity;

            int FP_Denominator = finalPerimetryTestFinalResultObject.Measurements.VisualFieldStaticPerimetryTestReliability.VisualFieldCatchTrialSequence.PositiveCatchTrialsQuantity;
            int FP_Numerator = finalPerimetryTestFinalResultObject.Measurements.VisualFieldStaticPerimetryTestReliability.VisualFieldCatchTrialSequence.FalsePositivesQuantity;

            int FN_Denominator = finalPerimetryTestFinalResultObject.Measurements.VisualFieldStaticPerimetryTestReliability.VisualFieldCatchTrialSequence.NegativeCatchTrialsQuantity;
            int FN_Numerator = finalPerimetryTestFinalResultObject.Measurements.VisualFieldStaticPerimetryTestReliability.VisualFieldCatchTrialSequence.FalseNegativesQuantity;
            bundle.putString("FL", FL_Numerator + "/" + FL_Denominator);// + "%");
            bundle.putString("FP", FP_Numerator + "/" + FP_Denominator);//+ "%");
            bundle.putString("FN", FN_Numerator + "/" + FN_Denominator);// + "%");
            bundle.putInt("FL_Numerator", FL_Numerator);
            bundle.putInt("FL_Denominator", FL_Denominator);
            bundle.putInt("FP_Numerator", FP_Numerator);
            bundle.putInt("FP_Denominator", FP_Denominator);
            bundle.putInt("FN_Numerator", FN_Numerator);
            bundle.putInt("FN_Denominator", FN_Denominator);

            if (finalPerimetryTestFinalResultObject.TestResultsInfo.ResultNormalSequence.GlobalDeviationProbabilityNormalsFlag.equalsIgnoreCase("yes")) {

               /* bundle.putString("GHT", finalPerimetryTestFinalResultObject.TestResultsInfo.VisualFieldGlobalResultsIndexSequence[0].DataObservationSequence.TextValue);
                bundle.putString("VFI", finalPerimetryTestFinalResultObject.TestResultsInfo.VisualFieldGlobalResultsIndexSequence[1].DataObservationSequence.TextValue);
                */
                bundle.putString("GHT", finalPerimetryTestFinalResultObject.TestResultsInfo.VisualFieldGlobalResultsIndexSequence[0].DataObservationSequence.TextValue);
                bundle.putDouble("MDProbability", finalPerimetryTestFinalResultObject.TestResultsInfo.ResultNormalSequence.GlobalDeviationProbabilitySequence.GlobalDeviationProbability);
                bundle.putDouble("PDProbability", finalPerimetryTestFinalResultObject.TestResultsInfo.ResultNormalSequence.LocalDeviationProbabilitySequence.LocalDeviationProbability);
            } else {
                bundle.putString("GHT", " ");
                bundle.putDouble("MDProbability", 0);
                bundle.putDouble("PDProbability", 0);
            }
            bundle.putString("VFI", finalPerimetryTestFinalResultObject.TestResultsInfo.VisualFieldGlobalResultsIndexSequence[1].DataObservationSequence.TextValue);
            String isFoveaOn = finalPerimetryTestFinalResultObject.Measurements.VisualFieldStaticPerimetryTestMeasurements.FovealSensitivityMeasured;
            double foveaValue = finalPerimetryTestFinalResultObject.Measurements.VisualFieldStaticPerimetryTestMeasurements.FovealSensitivity;
            String foveaText = isFoveaOn.equalsIgnoreCase("yes") ? "Fovea: " + foveaValue : "Fovea: Off";
            bundle.putString("fovea", foveaText);
            dt_new_result.clear();
            dt_result_sensitivity.clear();
            int dt_new_result_length = finalPerimetryTestFinalResultObject.Measurements.VisualFieldStaticPerimetryTestMeasurements.VisualFieldTestPointSequence.length;
            Log.e("result_length", "" + dt_new_result_length);
            for (int i = 0; i < dt_new_result_length; i++) {
                String xVal = "", yVal = "", xyVal = "";
                int x = (int) finalPerimetryTestFinalResultObject.Measurements.VisualFieldStaticPerimetryTestMeasurements.VisualFieldTestPointSequence[i].VisualFieldTestPointXCoordinate;
                int y = (int) finalPerimetryTestFinalResultObject.Measurements.VisualFieldStaticPerimetryTestMeasurements.VisualFieldTestPointSequence[i].VisualFieldTestPointYCoordinate;
                int s = (int) finalPerimetryTestFinalResultObject.Measurements.VisualFieldStaticPerimetryTestMeasurements.VisualFieldTestPointSequence[i].SensitivityValue;
                long tempTotalDeviation = Math.round(finalPerimetryTestFinalResultObject.Measurements.VisualFieldStaticPerimetryTestMeasurements.VisualFieldTestPointSequence[i].VisualFieldTestPointNormalSequence.AgeCorrectedSensitivityDeviationValue);
                int deviation = (int) tempTotalDeviation;
                double probabilityDeviationValue = finalPerimetryTestFinalResultObject.Measurements.VisualFieldStaticPerimetryTestMeasurements.VisualFieldTestPointSequence[i].VisualFieldTestPointNormalSequence.AgeCorrectedSensitivityProbabilityDeviationValue;
                long tempPatternlDeviation = Math.round(finalPerimetryTestFinalResultObject.Measurements.VisualFieldStaticPerimetryTestMeasurements.VisualFieldTestPointSequence[i].VisualFieldTestPointNormalSequence.GeneralizedDefectCorrectedSensitivityDeviationValue);
                int generalizedDefectCorrectedSensitivityDeviationValue = (int) tempPatternlDeviation;
                double generalizedDefectCorrectedSensitivityDeviationProbabilityValue = finalPerimetryTestFinalResultObject.Measurements.VisualFieldStaticPerimetryTestMeasurements.VisualFieldTestPointSequence[i].VisualFieldTestPointNormalSequence.GeneralizedDefectCorrectedSensitivityDeviationProbabilityValue;
                String seen = finalPerimetryTestFinalResultObject.Measurements.VisualFieldStaticPerimetryTestMeasurements.VisualFieldTestPointSequence[i].StimulusResults;
                if (!(x == 0 & y == 0)) {
                    if (pattern.equals("24-2") || pattern.equals("30-2")) {
                        xVal = x > 0 ? "x" + ((x + 3) / 6) : "-x" + ((-x + 3) / 6);
                        yVal = y > 0 ? "y" + ((y + 3) / 6) : "-y" + ((-y + 3) / 6);
                        xyVal = xVal.concat(yVal);
                    } else {
                        xVal = x > 0 ? "x" + ((x + 1) / 2) : "-x" + ((-x + 1) / 2);
                        yVal = y > 0 ? "y" + ((y + 1) / 2) : "-y" + ((-y + 1) / 2);
                        xyVal = xVal.concat(yVal);
                    }
                    dt_new_result.add(xyVal);
                    dt_result_sensitivity.add("" + s);
                    dt_result_deviation.add("" + deviation);
                    dt_result_probabilityDeviationValue.add("" + probabilityDeviationValue);
                    Log.d("Seen" + i, "" + seen);
                    dt_result_seen.add("" + seen);
                    dt_result_generalizedDefectCorrectedSensitivityDeviationValue.add("" + generalizedDefectCorrectedSensitivityDeviationValue);
                    dt_result_generalizedDefectCorrectedSensitivityDeviationProbabilityValue.add("" + generalizedDefectCorrectedSensitivityDeviationProbabilityValue);
                    Log.e("GetResult", xVal + yVal + " One " + s + " Two " + deviation + " Three " + generalizedDefectCorrectedSensitivityDeviationValue + " Four " + probabilityDeviationValue + " Five " + generalizedDefectCorrectedSensitivityDeviationProbabilityValue);
                } else {
                    Log.d("GetResultErr", "X = " + x + " Y = " + y);
                }
            }

            String backGround = "Background: " + finalPerimetryTestFinalResultObject.Measurements.VisualFieldStaticPerimeteryTestParameters.BackgroundIlluminationColorCodeSequence.CodeMeaning;
            bundle.putString("BackgroundIlluminationColorCodeSequence", backGround);

            String md = Double.toString(finalPerimetryTestFinalResultObject.Measurements.VisualFieldStaticPerimetryTestResults.ResultNormalSequence.GlobalDeviationFromNormal);
            //md = md.substring(0, 6);
            //DecimalFormat dec = new DecimalFormat("#0.00");
            //String psd = dec.format(Double.toString(finalPerimetryTestFinalResultObject.Measurements.VisualFieldStaticPerimetryTestResults.ResultNormalSequence.LocalizedDeviationFromNormal));
            String psd = (Double.toString(finalPerimetryTestFinalResultObject.Measurements.VisualFieldStaticPerimetryTestResults.ResultNormalSequence.LocalizedDeviationFromNormal));
            //psd = psd.substring(0, 6);
            String meanDeviation = md;// + "<font color=#ff0000>" + prob + "</font>";
            bundle.putString("meanDeviation", meanDeviation);
            bundle.putString("psd", psd);

            String cylindricalAxis = Double.toString(finalPerimetryTestFinalResultObject.Measurements.OphthalmicPatientClinicalInformationAndTestLensParameters.OphthalmicPatientClinicalInformationLeftEyeSequence.RefractiveParametersUsedOnPatientSequence.CylinderAxis);
            String cylindricalPower = Double.toString(finalPerimetryTestFinalResultObject.Measurements.OphthalmicPatientClinicalInformationAndTestLensParameters.OphthalmicPatientClinicalInformationLeftEyeSequence.RefractiveParametersUsedOnPatientSequence.CylindricalLensPower);
            String sphericalPower = Double.toString(finalPerimetryTestFinalResultObject.Measurements.OphthalmicPatientClinicalInformationAndTestLensParameters.OphthalmicPatientClinicalInformationLeftEyeSequence.RefractiveParametersUsedOnPatientSequence.SphericalLensPower);
            String visualAcuity = "RX: " + sphericalPower + " DS " + cylindricalPower + " DC X " + cylindricalAxis;
            bundle.putString("visualAcuity", visualAcuity);
            String duration = "";
            if (finalPerimetryTestFinalResultObject.Series.isImported) {
                duration = finalPerimetryTestFinalResultObject.Series.importedDuration;
            } else {
                //duration = durationCalculation(finalPerimetryTestFinalResultObject.Series.PerformedProcedureStepStartTime, finalPerimetryTestFinalResultObject.Series.PerformedProcedureStepEndTime);
                if (finalPerimetryTestFinalResultObject.Series.duration == null || finalPerimetryTestFinalResultObject.Series.duration.isEmpty()) {
                    duration = durationCalculation(finalPerimetryTestFinalResultObject.Series.PerformedProcedureStepStartTime, finalPerimetryTestFinalResultObject.Series.PerformedProcedureStepEndTime);
                    CommonUtils.writeToTestLogFile("Duration is null so " + duration);
                } else {
                    duration = finalPerimetryTestFinalResultObject.Series.duration;
                    CommonUtils.writeToTestLogFile("Duration is not null so " + duration);
                }
            }
            bundle.putString("TestDuration", duration);


            String dia = "Pupil Diameter: " + finalPerimetryTestFinalResultObject.Measurements.OphthalmicPatientClinicalInformationAndTestLensParameters.OphthalmicPatientClinicalInformationRightEyeSequence.PupilSize;
            bundle.putString("PupilSize", dia);

            Format formatter = CommonUtils.getDateTimeFormat();
            String createdDate = formatter.format(patientTestResult.getCreateDate());
            bundle.putString("CreatedDate", createdDate);

            bundle.putStringArrayList("dt_new_result", dt_new_result);
            bundle.putStringArrayList("dt_result_seen", dt_result_seen);
            bundle.putStringArrayList("dt_result_sensitivity", dt_result_sensitivity);
            bundle.putStringArrayList("dt_result_deviation", dt_result_deviation);
            bundle.putStringArrayList("dt_result_probabilityDeviationValue", dt_result_probabilityDeviationValue);
            bundle.putStringArrayList("dt_result_generalizedDefectCorrectedSensitivityDeviationValue", dt_result_generalizedDefectCorrectedSensitivityDeviationValue);
            bundle.putStringArrayList("dt_result_generalizedDefectCorrectedSensitivityDeviationProbabilityValue", dt_result_generalizedDefectCorrectedSensitivityDeviationProbabilityValue);
            return bundle;
        } else {
            Log.d("GetResultData", "Json Null");
            if (dialog.isShowing())
                dialog.dismiss();
            cancel(true);
            return null;
        }
    }

    @Override
    protected void onProgressUpdate(String... string) {
        super.onProgressUpdate(string);
    }

    @Override
    protected void onPostExecute(Bundle patientTestResult) {

        Context context = MainActivity.applicationContext;
        String strategy = patientTestResult.getString("setPatientTestStrategyVal");
        String pattern = patientTestResult.getString("setPatientTestPatternVal");
        Log.d("Strategy", "" + strategy);
        if (strategy != null) {
            if (strategy.equals("Screening")) {
                Log.d("Strategy", "Screening");
                Intent ScreeningTestReport = new Intent(context, ScreeningReportActivity.class);
                ScreeningTestReport.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ScreeningTestReport.putExtra("Payload", patientTestResult);
                context.startActivity(ScreeningTestReport);
            } else if (pattern.equals("24-2") | pattern.equals("30-2") | pattern.equals("10-2")) {
                Intent intent = new Intent(context, TestReport.class);
                intent.putExtra("bottomView", "invisible");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("Payload", patientTestResult);
                context.startActivity(intent);
            } else {
                Intent ScreeningTestReport = new Intent(context, DoctorReportActivity.class);
                ScreeningTestReport.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                ScreeningTestReport.putExtra("Payload", patientTestResult);
                context.startActivity(ScreeningTestReport);
            }
        }
        if (dialog.isShowing())
            dialog.dismiss();

        delegate.onProcessFinish(patientTestResult);
    }
}