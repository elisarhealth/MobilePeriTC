package com.agyohora.mobileperitc.ui;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.agyohora.mobileperitc.R;
import com.agyohora.mobileperitc.asynctasks.LoadScreeningGraphs;
import com.agyohora.mobileperitc.utils.CommonUtils;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

import static com.agyohora.mobileperitc.utils.CommonUtils.showBigToast;

/**
 * Created by Invent on 22-3-18.
 * Activity to generate Screening report of a patient.
 */

@SuppressWarnings("unchecked")
public class ScreeningReportActivity extends AppCompatActivity {

    private Bundle bundle;
    private String resultId, testedAt;
    private static String fileName;
    private AsyncTask taskOne;
    public static boolean loadingFlag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.post_test_screening_result);
        loadingFlag = true;
        BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);
        bottomNavigation.setVisibility(View.GONE);
        bundle = getIntent().getBundleExtra("Payload");
        resultId = bundle.getString("ResultId");
        testedAt = bundle != null ? bundle.getString("TestedAt") : "";
        //stopService(new Intent(ScreeningReportActivity.this, StoreTransmitter.class));
        //stopService(new Intent(ScreeningReportActivity.this, CommunicationService.class));
        //CommonUtils.startReceiver(this, CommunicationReceiver.class);
        //WifiCommunicationManager.nextState = WifiCommunicationManager.State.CLOSE_HOTSPOT;

    }

    @Override
    protected void onStart() {
        super.onStart();

        int seen = 0, notSeen = 0, missedInitialPoints = 0;
        String strategy = bundle.getString("setPatientTestStrategyVal");
        String pattern = bundle.getString("setPatientTestPatternVal");

        String name = bundle.getString("setPatientName");
        String eye = bundle.getString("setPatientTestEyeVal");
        name = "Name: " + name;
        //name = sex.equals("Male") ? "Name: Mr. " + name : "Name: Mrs. " + name;
        ((TextView) findViewById(R.id.reportView_PatientName)).setText(name);

        String pattern_and_strategy = "Central " + pattern + " " + strategy + " Test";
        ((TextView) findViewById(R.id.pattern_and_strategy)).setText(pattern_and_strategy);

        String age = "AGE: " + CommonUtils.getAge(bundle.getString("setPatientDOBVal"));
        ((TextView) findViewById(R.id.reportView_Age)).setText(age);

        fileName = CommonUtils.generateFileName("patient_copy", bundle.getString("setPatientMrnNumberVal"), testedAt, eye);

        String mrn = "ID: " + bundle.getString("setPatientMrnNumberVal");
        ((TextView) findViewById(R.id.reportView_MRNumber)).setText(mrn);

        String fieldOfVision = pattern.equals("30-2") ? getResources().getString(R.string.field_of_vision_72_screening) : getResources().getString(R.string.field_of_vision_54_screening);
        ((TextView) findViewById(R.id.fieldOfVision)).setText(fieldOfVision);

        ((TextView) findViewById(R.id.testConductedTime)).setText(bundle.getString("CreatedDate"));

        String copyright = "\u00a9 Elisar Life Sciences Private Limited. " + CommonUtils.currentYear();
        ((TextView) findViewById(R.id.copyRights)).setText(copyright);

        if (CommonUtils.isUserSetUpFinished(this)) {
            try {
                JSONObject config = CommonUtils.readConfig("Screening Test Report Activity");
                if (config != null) {
                    //String siteOrg = config.getString("OrganizationId") + " \n" + config.getString("SiteId");
                    //((TextView) view.findViewById(R.id.branch_and_site)).setText(siteOrg);
                    ((TextView) findViewById(R.id.deviceId)).setText(config.getString("DeviceId"));
                }
            } catch (JSONException e) {
                Log.e("DoctorReportActivity", e.getMessage());
            }
        } /*else {
            ((TextView) view.findViewById(R.id.branch_and_site)).setText("DR.AGARWAL'S EYE HOSPITAL\nCHENNAI");
        }*/
        String swVersion = CommonUtils.getVersionCombo();
        ((TextView) findViewById(R.id.swVersion)).setText(swVersion);


        eye = eye.equals("Right Eye") ? "Test Result - Right Eye" : "Test Result - Left Eye";
        ((TextView) findViewById(R.id.reportView_testEye)).setText(eye);

        float fl_percentage = (bundle.getInt("FL_Numerator") * 100.0f) / bundle.getInt("FL_Denominator");
        float fp_percentage = (bundle.getInt("FP_Numerator") * 100.0f) / bundle.getInt("FP_Denominator");
        //float fn_percentage = (bundle.getInt("FN_Numerator") * 100.0f) / bundle.getInt("FN_Denominator");

        final ArrayList<String> pointValues = bundle.getStringArrayList("dt_result_probabilityDeviationValue");
        final ArrayList<String> dt_result_seen = bundle.getStringArrayList("dt_result_seen");
        final ArrayList<String> coordinates = bundle.getStringArrayList("dt_new_result");
        ArrayList<String> values = new ArrayList<>();
        values.add(pattern);
        values.add(bundle.getString("setPatientTestEyeVal"));
        taskOne = new LoadScreeningGraphs(ScreeningReportActivity.this).execute(values, coordinates, dt_result_seen);
        for (int i = 0; i < dt_result_seen.size(); i++) {
            if (dt_result_seen.get(i).equals("SEEN"))
                seen++;
            else {
                if (!CommonUtils.isValueInBlindStop(pointValues.get(i)))
                    notSeen++;
                if (CommonUtils.isValueInFirstBlock(pointValues.get(i))) {
                    notSeen++;
                    missedInitialPoints++;
                }

            }
        }

        int total = pointValues.size();
        ((TextView) findViewById(R.id.whiteView)).setText(Integer.toString(seen));
        ((TextView) findViewById(R.id.redView)).setText(Integer.toString(notSeen));
        String defective = "Defective points: " + notSeen + " / " + total;
        ((TextView) findViewById(R.id.defectivePoints)).setText(defective);

        String limit = "";
        String advice = "";


        Log.e("fl_percentage", "" + bundle.getInt("FL_Numerator"));
        Log.e("fl_percentage", "" + bundle.getInt("FL_Denominator"));
        Log.e("fl_percentage", "" + bundle.getInt("FP_Numerator"));
        Log.e("fl_percentage", "" + bundle.getInt("FP_Denominator"));
        Log.e("fl_percentage", "" + fl_percentage);
        Log.e("fp_percentage", "" + fp_percentage);
        Log.e("notSeen", "" + notSeen);
        Log.e("missedInitialPoints", "" + missedInitialPoints);

        if (fl_percentage <= 33.33 && fp_percentage <= 33.33) {
            if (notSeen <= 3) {
                limit = "Normal ";
                advice = "Your visual field test looks okay";
            } else if (missedInitialPoints == 0) {
                limit = "Abnormal";
                advice = "Please visit an eye specialist for further investigation.";
            } else if (missedInitialPoints > 0 && missedInitialPoints <= 2) {
                limit = "Abnormal";
                advice = "Please visit an eye specialist for further investigation.";
            } else if (missedInitialPoints > 2 && missedInitialPoints <= 4) {
                limit = "Suspect";
                advice = "Redo the test because you missed points in the initial part of the test.";
            }
        } else if (fl_percentage > 33.33 || fp_percentage > 33.33) {
            if (notSeen <= 3) {
                limit = "Suspect";
                advice = "Redo the test because reliability is low.";
            } else if (missedInitialPoints >= 3) {
                limit = "Suspect";
                advice = "Redo the test because you missed points in the initial part of the test.";
            } else if (missedInitialPoints >= 2) {
                limit = "Abnormal";
                advice = "Please visit an eye specialist for further investigation.";
            }
        }

        ((TextView) findViewById(R.id.reportView_testResultLimit)).setText(limit);
        ((TextView) findViewById(R.id.reportView_testResultAdvice)).setText(advice);
    }


    @Override
    protected void onStop() {
        super.onStop();
        taskOne.cancel(true);
    }

    @Override
    public void onBackPressed() {
        //CommonUtils.startReceiver(this,CommunicationReceiver.class);
        super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.patient_report_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Log.d("onPrepareOptionsMenu", "loading flag " + loadingFlag);
        if (loadingFlag) {
            menu.findItem(R.id.action_patient_print_report).setVisible(false);
            menu.findItem(R.id.action_patient_email_report).setVisible(false);
            menu.findItem(R.id.action_patient_bluetooth_report).setVisible(false);
            menu.findItem(R.id.action_patient_delete_report).setVisible(false);
        } else {
            menu.findItem(R.id.action_patient_print_report).setVisible(true);
            menu.findItem(R.id.action_patient_email_report).setVisible(true);
            menu.findItem(R.id.action_patient_bluetooth_report).setVisible(true);
            menu.findItem(R.id.action_patient_delete_report).setVisible(true);
            MenuItem item = menu.findItem(R.id.action_patient_delete_report);
            if (CommonUtils.isItUserLoggedIn(this)) {
                item.getIcon().setAlpha(130);
            } else {
                item.getIcon().setAlpha(255);
            }

        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_patient_delete_report:
                if (CommonUtils.isItUserLoggedIn(this)) {
                    CommonUtils.switchToAdminProfileToDeleteReport(this);
                } else {
                    CommonUtils.deleteReportConfirmationDialog(this, this, resultId, item, true);
                }
                break;
            case R.id.action_patient_print_report:
                triggerUserAction("print");
                return true;
            case R.id.action_patient_bluetooth_report:
                triggerUserAction("bluetooth");
                return true;
            case R.id.action_patient_email_report:
                if (CommonUtils.haveNetworkConnection(this))
                    triggerUserAction("email");
                else
                    CommonUtils.initiateNetworkOptions(this, this, "NA");
                return true;
            default:
                break;
        }

        return false;
    }

    private void triggerUserAction(String action) {
        File file = CommonUtils.isFileExists(fileName, this);
        if (action.equals("print")) {
            new PatientReportPdf(bundle, this, this, "print").execute();
        } else if (action.equals("email")) {
            new PatientReportPdf(bundle, this, this, "email").execute();
        } else {
            new PatientReportPdf(bundle, this, this, "bluetooth").execute();
        }
    }

    private class PatientReportPdf extends AsyncTask<Void, Void, Boolean> {
        Context context;
        String action;
        Activity activity;
        Bundle bundle;
        private ProgressDialog dialog;

        PatientReportPdf(Bundle bundle, final Activity activity, final Context context, final String action) {
            this.bundle = bundle;
            this.context = context;
            this.action = action;
            this.activity = activity;
            this.context = context;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            try {
                dialog = ProgressDialog.show(activity, null, null, true, false);
                dialog.setContentView(R.layout.progress_layout);
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            } catch (Exception e) {
                Log.e("Exception", "onPreExecute of Screening report activity " + e.getMessage());
            }
        }


        @Override
        protected Boolean doInBackground(Void... voids) {
            View patientLayout;
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            RelativeLayout view1 = new RelativeLayout(context);
            patientLayout = mInflater.inflate(R.layout.patient_report_pdf, view1, true);
            patientLayout.setLayoutParams(new ViewGroup.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT));
            patientLayout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            patientLayout.layout(0, 0, patientLayout.getMeasuredWidth(), patientLayout.getMeasuredHeight());
            int seen = 0, notSeen = 0, missedInitialPoints = 0;
            String strategy = bundle.getString("setPatientTestStrategyVal");
            String pattern = bundle.getString("setPatientTestPatternVal");

            String name = bundle.getString("setPatientName");
            name = "Name: " + name;
            //name = sex.equals("Male") ? "Name: Mr. " + name : "Name: Mrs. " + name;
            ((TextView) patientLayout.findViewById(R.id.reportView_PatientName)).setText(name);

            String pattern_and_strategy = "Central " + pattern + " " + strategy + " Test";
            ((TextView) patientLayout.findViewById(R.id.pattern_and_strategy)).setText(pattern_and_strategy);
            ((TextView) patientLayout.findViewById(R.id.reportView_testStrategy)).setText(strategy);

            String age = "AGE: " + CommonUtils.getAge(bundle.getString("setPatientDOBVal"));
            ((TextView) patientLayout.findViewById(R.id.reportView_Age)).setText(age);

            String mrn = "ID: " + bundle.getString("setPatientMrnNumberVal");
            ((TextView) patientLayout.findViewById(R.id.reportView_MRNumber)).setText(mrn);

            String eye = bundle.getString("setPatientTestEyeVal");
            ((TextView) patientLayout.findViewById(R.id.reportView_testEyeCaps)).setText(eye);
            eye = eye.equals("Right Eye") ? "Test Result: Right Eye" : "Test Result: Left Eye";
            ((TextView) patientLayout.findViewById(R.id.reportView_testEye)).setText(eye);

            String fieldOfVision = pattern.equals("30-2") ? getResources().getString(R.string.field_of_vision_72_screening) : getResources().getString(R.string.field_of_vision_54_screening);
            ((TextView) patientLayout.findViewById(R.id.fieldOfVision)).setText(fieldOfVision);
            ((TextView) patientLayout.findViewById(R.id.testConductedTime)).setText(bundle.getString("CreatedDate"));

            String copyright = "\u00a9 Elisar Life Sciences Private Limited. " + CommonUtils.currentYear();
            ((TextView) patientLayout.findViewById(R.id.copyRights)).setText(copyright);

            if (CommonUtils.isUserSetUpFinished(context)) {
                try {
                    JSONObject config = CommonUtils.readConfig("Screening Test Report Activity PDF");
                    if (config != null) {
                        //String siteOrg = config.getString("OrganizationId") + " \n" + config.getString("SiteId");
                        //((TextView) view.findViewById(R.id.branch_and_site)).setText(siteOrg);
                        ((TextView) patientLayout.findViewById(R.id.deviceId)).setText(config.getString("DeviceId"));
                    }
                } catch (JSONException e) {
                    Log.e("DoctorReportActivity", e.getMessage());
                }
            } /*else {
            ((TextView) view.findViewById(R.id.branch_and_site)).setText("DR.AGARWAL'S EYE HOSPITAL\nCHENNAI");
        }*/
            String swVersion = CommonUtils.getVersionCombo();
            ((TextView) patientLayout.findViewById(R.id.swVersion)).setText(swVersion);

            String root = context.getFilesDir().getAbsolutePath();
            Log.d("Root", "" + root);
            ImageView imageOne = patientLayout.findViewById(R.id.graph);
            imageOne.setImageURI(null);
            imageOne.setImageURI(Uri.parse(root + "/graph.png"));

            float fl_percentage = (bundle.getInt("FL_Numerator") * 100.0f) / bundle.getInt("FL_Denominator");
            float fp_percentage = (bundle.getInt("FP_Numerator") * 100.0f) / bundle.getInt("FP_Denominator");
            //float fn_percentage = (bundle.getInt("FN_Numerator") * 100.0f) / bundle.getInt("FN_Denominator");

            final ArrayList<String> pointValues = bundle.getStringArrayList("dt_result_probabilityDeviationValue");
            final ArrayList<String> dt_result_seen = bundle.getStringArrayList("dt_result_seen");
            final ArrayList<String> coordinates = bundle.getStringArrayList("dt_new_result");
            ArrayList<String> values = new ArrayList<>();
            values.add(pattern);
            values.add(bundle.getString("setPatientTestEyeVal"));
            taskOne = new LoadScreeningGraphs(ScreeningReportActivity.this).execute(values, coordinates, dt_result_seen);
            for (int i = 0; i < dt_result_seen.size(); i++) {
                if (dt_result_seen.get(i).equals("SEEN"))
                    seen++;
                else {
                    if (!CommonUtils.isValueInBlindStop(pointValues.get(i)))
                        notSeen++;
                    if (CommonUtils.isValueInFirstBlock(pointValues.get(i))) {
                        notSeen++;
                        missedInitialPoints++;
                    }

                }
            }

            int total = pointValues.size();
            ((TextView) patientLayout.findViewById(R.id.whiteView)).setText(Integer.toString(seen));
            ((TextView) patientLayout.findViewById(R.id.redView)).setText(Integer.toString(notSeen));
            String defective = "Defective points: " + notSeen + " / " + total;
            ((TextView) patientLayout.findViewById(R.id.resultView_defective_points)).setText(defective);

            String limit = "";
            String advice = "";


            Log.e("fl_percentage", "" + bundle.getInt("FL_Numerator"));
            Log.e("fl_percentage", "" + bundle.getInt("FL_Denominator"));
            Log.e("fl_percentage", "" + bundle.getInt("FP_Numerator"));
            Log.e("fl_percentage", "" + bundle.getInt("FP_Denominator"));
            Log.e("fl_percentage", "" + fl_percentage);
            Log.e("fp_percentage", "" + fp_percentage);
            Log.e("notSeen", "" + notSeen);
            Log.e("missedInitialPoints", "" + missedInitialPoints);

            if (fl_percentage <= 33.33 && fp_percentage <= 33.33) {
                if (notSeen <= 3) {
                    limit = "Normal ";
                    advice = "Your visual field test looks okay";
                } else if (missedInitialPoints == 0) {
                    limit = "Abnormal";
                    advice = "Please visit an eye specialist for further investigation.";
                } else if (missedInitialPoints > 0 && missedInitialPoints <= 2) {
                    limit = "Abnormal";
                    advice = "Please visit an eye specialist for further investigation.";
                } else if (missedInitialPoints > 2 && missedInitialPoints <= 4) {
                    limit = "Suspect";
                    advice = "Redo the test because you missed points in the initial part of the test.";
                }
            } else if (fl_percentage > 33.33 || fp_percentage > 33.33) {
                if (notSeen <= 3) {
                    limit = "Suspect";
                    advice = "Redo the test because reliability is low.";
                } else if (missedInitialPoints >= 3) {
                    limit = "Suspect";
                    advice = "Redo the test because you missed points in the initial part of the test.";
                } else if (missedInitialPoints >= 2) {
                    limit = "Abnormal";
                    advice = "Please visit an eye specialist for further investigation.";
                }
            }

            ((TextView) patientLayout.findViewById(R.id.reportView_testResultLimit)).setText(limit);
            ((TextView) patientLayout.findViewById(R.id.reportView_testResultAdvice)).setText(advice);
            CommonUtils.saveBitmapFromView(patientLayout, context, activity, action, "patient_copy", mrn, testedAt, bundle.getString("setPatientTestEyeVal"));
            return null;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (dialog.isShowing())
                dialog.dismiss();
        }
    }

    private void showToast(String message) {
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        showBigToast(toast);
    }

}
