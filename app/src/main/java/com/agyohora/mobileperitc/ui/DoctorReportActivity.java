package com.agyohora.mobileperitc.ui;

import android.app.Activity;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.agyohora.mobileperitc.R;
import com.agyohora.mobileperitc.asynctasks.LoadMappedIconView;
import com.agyohora.mobileperitc.asynctasks.LoadMappedLargeText;
import com.agyohora.mobileperitc.asynctasks.LoadMappedView;
import com.agyohora.mobileperitc.utils.CommonUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.text.Format;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Invent
 * Activity to generate doctor's copy of report
 */

@SuppressWarnings("unchecked")
public class DoctorReportActivity extends AppCompatActivity {

    private Bundle bundle;
    private boolean loadingFlag;
    private AsyncTask taskOne, taskTwo, taskThree, taskFour, taskFive, taskSix;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doctor_copy_layout);
        loadingFlag = true;
        bundle = getIntent().getBundleExtra("Payload");
    }

    @Override
    protected void onStart() {
        super.onStart();

        String eye = bundle.getString("setPatientTestEyeVal");
        if (eye != null) {
            eye = eye.equals("Right Eye") ? "Eye: Right" : "Eye: Left";
            ((TextView) findViewById(R.id.reportView_testEye)).setText(eye);
        }

        String sex = bundle.getString("setPatientSexVal");
        String name = bundle.getString("setPatientName");
        name = "Name: " + name;
        //name = sex.equals("Male") ? "Name: Mr. " + name : "Name: Mrs. " + name;
        ((TextView) findViewById(R.id.reportView_PatientName)).setText(name);

        String pattern = bundle.getString("setPatientTestPatternVal");

        String mrn = "ID: " + bundle.getString("setPatientMrnNumberVal");
        ((TextView) findViewById(R.id.reportView_MRNumber)).setText(mrn);

        String age = bundle.getString("setPatientDOBVal");
        if (age != null) {
            age = "AGE: " + CommonUtils.getAge(age);
            ((TextView) findViewById(R.id.reportView_Age)).setText(age);
        }

        String strategy = bundle.getString("setPatientTestStrategyVal");
        ((TextView) findViewById(R.id.reportView_testStrategy)).setText(strategy);
        String pattern_and_strategy = "Central " + pattern + " " + strategy;
        ((TextView) findViewById(R.id.pattern_statergy)).setText(pattern_and_strategy);
        strategy = "Strategy: " + strategy;
        ((TextView) findViewById(R.id.textStrategy)).setText(strategy);

        String fixationMonitor = "Fixation Monitor: Gaze / Blindspot";
        ((TextView) findViewById(R.id.textFixationMoniter)).setText(fixationMonitor);

        String fixationTarget = "Fixation Target: Central";
        ((TextView) findViewById(R.id.textFixationTarget)).setText(fixationTarget);

        String fixationLoss = bundle.getString("FL");
        fixationLoss = "Fixation Losses: " + fixationLoss;
        ((TextView) findViewById(R.id.textFixationLosses)).setText(fixationLoss);

        String falsePositives = bundle.getString("FP");
        falsePositives = "False POS Errors: " + falsePositives;
        ((TextView) findViewById(R.id.textFalsePositive)).setText(falsePositives);

        String falseNegatives = bundle.getString("FN");
        falseNegatives = "False NEG Errors: " + falseNegatives;
        ((TextView) findViewById(R.id.textFalseNegative)).setText(falseNegatives);

        String duration = bundle.getString("TestDuration");
        if (duration.startsWith("-")) {
            duration = "0:00" + "\\u002A";
            TextView timer_wrong_warning = findViewById(R.id.timer_wrong_warning);
            timer_wrong_warning.setVisibility(View.VISIBLE);
            ((TextView) findViewById(R.id.textTotalDuration)).setText(duration);
        } else {
            duration = "Total Duration: " + duration;
            ((TextView) findViewById(R.id.textTotalDuration)).setText(duration);
        }

        String background = bundle.getString("BackgroundIlluminationColorCodeSequence");
        ((TextView) findViewById(R.id.textBackground)).setText(background);

        String PupilSize = bundle.getString("PupilSize");
        ((TextView) findViewById(R.id.textPupilDia)).setText(PupilSize);

        String textVisualAcuity = bundle.getString("visualAcuity");
        ((TextView) findViewById(R.id.textVisualAcuity)).setText(textVisualAcuity);

        /*String agarwal = "OPTHALMOLOGY   DR.AGARWAL'S EYE HOSPITAL\nCHENNAI";
        ((TextView) findViewById(R.id.agarwal)).setText(agarwal);*/

       /* String pattern_and_strategy = "Central " + pattern + " " + strategy + " Test";
        Log.d("pattern_and_strategy"," "+pattern_and_strategy);
        ((TextView) findViewById(R.id.pattern_and_strategy)).setText(pattern_and_strategy);*/

        Format formatter = CommonUtils.getStandardFormat();
        String today = formatter.format(new Date());
        ((TextView) findViewById(R.id.testConductedTime)).setText(today);

        String copyright = "\u00a9 Elisar Life Sciences Private Limited. " + CommonUtils.currentYear();
        ((TextView) findViewById(R.id.copyRights)).setText(copyright);

        //Todo dynamic GHT value
        String ghtD = "";//"GHT  Outside normal limits";
        String mdD = bundle.getString("meanDeviation");
        String psdD = bundle.getString("psd");

        /*((TextView) findViewById(R.id.ghtText)).setText("" + ghtD);
        ((TextView) findViewById(R.id.mdText)).setText("" + mdD);
        ((TextView) findViewById(R.id.gpsdText)).setText("" + psdD);*/

        //((TextView) findViewById(R.id.ghtText)).setText(ghtD);
        ((TextView) findViewById(R.id.mdText)).setText(Html.fromHtml(mdD));
        ((TextView) findViewById(R.id.gpsdText)).setText(psdD);

        // ((TextView) findViewById(R.id.opthalmology)).setText("OPTHALMOLOGY");
        if (CommonUtils.isUserSetUpFinished(this)) {
            try {
                JSONObject config = CommonUtils.readConfig("Doctor Report Activity");
                if (config != null) {
                    String siteOrg = config.getString("OrganizationId") + " " + config.getString("SiteId");
                    ((TextView) findViewById(R.id.branch_and_site)).setText(siteOrg);
                    ((TextView) findViewById(R.id.deviceId)).setText(config.getString("DeviceId"));
                }
            } catch (JSONException e) {
                Log.e("DoctorReportActivity", e.getMessage());
            }
        } else {
            ((TextView) findViewById(R.id.branch_and_site)).setText("DR.AGARWAL'S EYE HOSPITAL\nCHENNAI");
        }

        String swVersion = CommonUtils.getVersionCombo();
        ((TextView) findViewById(R.id.swVersion)).setText(swVersion);

        ArrayList<String> result = bundle.getStringArrayList("dt_new_result");
        ArrayList<String> result_sensitivity = bundle.getStringArrayList("dt_result_sensitivity");
        ArrayList<String> result_deviation = bundle.getStringArrayList("dt_result_deviation");
        ArrayList<String> result_generalizedDefectCorrectedSensitivityDeviationValue = bundle.getStringArrayList("dt_result_generalizedDefectCorrectedSensitivityDeviationValue");
        ArrayList<String> result_probabilityDeviationValue = bundle.getStringArrayList("dt_result_probabilityDeviationValue");
        ArrayList<String> result_generalizedDefectCorrectedSensitivityDeviationProbabilityValue = bundle.getStringArrayList("dt_result_generalizedDefectCorrectedSensitivityDeviationProbabilityValue");
        double[][][] quadrants = (double[][][]) bundle.getSerializable("quadrants");

        ImageView imageOne = findViewById(R.id.imageOne);
        ImageView imageTwo = findViewById(R.id.imageTwo);
        ImageView imageThree = findViewById(R.id.imageThree);
        ImageView imageFour = findViewById(R.id.imageFour);
        ImageView imageFive = findViewById(R.id.imageFive);
        ImageView imageGreyScale = findViewById(R.id.greyScale);
        ArrayList<String> values = new ArrayList<>();
        values.add(eye);
        values.add(pattern);


        taskOne = new LoadMappedView(this, imageOne).execute(values, result, result_sensitivity);
        taskTwo = new LoadMappedLargeText(this, imageTwo).execute(values, result, result_deviation);
        taskThree = new LoadMappedLargeText(this, imageThree).execute(values, result, result_generalizedDefectCorrectedSensitivityDeviationValue);
        taskFour = new LoadMappedIconView(this, imageFour).execute(values, result, result_probabilityDeviationValue);
        taskFive = new LoadMappedIconView(this, imageFive).execute(values, result, result_generalizedDefectCorrectedSensitivityDeviationProbabilityValue);
        taskSix = new LoadGreyScale(this, imageGreyScale, eye, this).execute(quadrants);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        taskOne.cancel(true);
        taskTwo.cancel(true);
        taskThree.cancel(true);
        taskFour.cancel(true);
        taskFive.cancel(true);
        taskSix.cancel(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.doctor_report_menu, menu);
        if (loadingFlag) {
            for (int i = 0; i < menu.size(); i++)
                menu.getItem(i).setVisible(false);
        }
        return super.onCreateOptionsMenu(menu);
    }

    private static class LoadGreyScale extends AsyncTask<double[][][], Void, Bitmap> {
        Activity activity;
        private ImageView imageView;
        String eye;
        WeakReference<DoctorReportActivity> weakReference;

        LoadGreyScale(Activity activity, ImageView imageView, String eye, DoctorReportActivity doctorReportActivity) {
            this.activity = activity;
            this.imageView = imageView;
            this.eye = eye;
            this.weakReference = new WeakReference<>(doctorReportActivity);
        }

        @Override
        protected Bitmap doInBackground(double[][][]... params) {
            double[][][] quadrants = params[0];
            View greyScale = CommonUtils.createGreyScale(quadrants, activity, eye);
            return CommonUtils.viewToBitmap(greyScale);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            String name = activity.getResources().getResourceEntryName(imageView.getId());
            CommonUtils.saveImage(bitmap, name, activity.getApplicationContext());
            imageView.setImageBitmap(bitmap);
            weakReference.get().loadingFlag = false;
            activity.invalidateOptionsMenu();
        }
    }
}
