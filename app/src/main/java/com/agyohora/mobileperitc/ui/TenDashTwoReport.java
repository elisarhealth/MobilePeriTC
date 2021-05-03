package com.agyohora.mobileperitc.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.agyohora.mobileperitc.R;
import com.agyohora.mobileperitc.asynctasks.LoadMappedIconView;
import com.agyohora.mobileperitc.asynctasks.LoadMappedView;
import com.agyohora.mobileperitc.asynctasks.PD_Plot_Ten_Dash_Two;
import com.agyohora.mobileperitc.asynctasks.TD_Plot_Ten_dash_Two;
import com.agyohora.mobileperitc.exceptions.NegativeTestDurationException;
import com.agyohora.mobileperitc.utils.CommonUtils;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Arrays;

import hugo.weaving.DebugLog;

import static com.agyohora.mobileperitc.utils.CommonUtils.showBigToast;

public class TenDashTwoReport extends Fragment {

    private static Bundle bundle;
    private static String fileName;
    private String resultId, testedAt;
    private boolean loadingFlag;
    private AsyncTask taskOne, taskTwo, taskThree, taskFour, taskFive, taskSix;
    private boolean canICancel;
    boolean isValueLessThan8 = false;

   /* public static final String DUMMY_DEVICE_ADDRESS = "AA:AA:AA:AA:AA:AA";
    private static final UUID DEVICE_CALLBACK_0 = UUID.randomUUID();
    private static final UUID DEVICE_CALLBACK_1 = UUID.randomUUID();
    private static final UUID WRITE_CHARACTERISTIC = UUID.randomUUID();
    private byte[] bytesToWrite = new byte[1024]; // a kilobyte array*/

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        loadingFlag = true;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        bundle = this.getArguments();
        resultId = bundle != null ? bundle.getString("ResultId") : "";
        testedAt = bundle != null ? bundle.getString("TestedAt") : "";
        Log.e("resultId", " " + resultId);
        Log.e("testedAt", " " + testedAt);
        return inflater.inflate(R.layout.ten_dash_two_report, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView pd_message_1 = view.findViewById(R.id.pd_message_1);
        TextView pd_message_2 = view.findViewById(R.id.pd_message_2);
        TextView patternDeviation = view.findViewById(R.id.patternDeviation);
        String eye = bundle.getString("setPatientTestEyeVal");
        String pattern = bundle.getString("setPatientTestPatternVal");
        String mrn = bundle.getString("setPatientMrnNumberVal");
        String sex = bundle.getString("setPatientSexVal");
        String name = bundle.getString("setPatientName");
        fileName = CommonUtils.generateFileName("doctor_copy", mrn, testedAt, eye);

        name = "Name: " + name;
        //name = sex.equals("Male") ? "Name: Mr. " + name : "Name: Mrs. " + name;
        ((TextView) view.findViewById(R.id.reportView_PatientName)).setText(name);

        mrn = "ID: " + mrn;
        ((TextView) view.findViewById(R.id.reportView_MRNumber)).setText(mrn);

        String dob = bundle.getString("setPatientDOBVal");
        Log.d("dob", " " + dob);

        if (dob != null) {
            String age = "AGE: " + CommonUtils.getAge(dob);
            ((TextView) view.findViewById(R.id.reportView_Age)).setText(age);
            dob = "DOB : " + dob.trim();
            ((TextView) view.findViewById(R.id.reportView_DOB)).setText(dob);
        }

        if (eye != null) {
            eye = eye.equals("Right Eye") ? "Eye: Right" : "Eye: Left";
            ((TextView) view.findViewById(R.id.reportView_testEye)).setText(eye);
        }


        String strategy = bundle.getString("setPatientTestStrategyVal");
        ((TextView) view.findViewById(R.id.reportView_testStrategy)).setText(strategy);
        String pattern_and_strategy = "Central " + pattern + " " + strategy;
        ((TextView) view.findViewById(R.id.pattern_statergy)).setText(pattern_and_strategy);
        strategy = "Strategy: " + strategy;
        ((TextView) view.findViewById(R.id.textStrategy)).setText(strategy);

        String fixationMonitor = "Fixation Monitor: Gaze / Blindspot";
        ((TextView) view.findViewById(R.id.textFixationMoniter)).setText(fixationMonitor);

        String fixationTarget = "Fixation Target: Central";
        ((TextView) view.findViewById(R.id.textFixationTarget)).setText(fixationTarget);

        String fixationLoss = bundle.getString("FL");
        fixationLoss = "Fixation Losses: " + fixationLoss;
        ((TextView) view.findViewById(R.id.textFixationLosses)).setText(fixationLoss);

        String falsePositives = bundle.getString("FP");
        falsePositives = "False POS Errors: " + falsePositives;
        ((TextView) view.findViewById(R.id.textFalsePositive)).setText(falsePositives);

        String falseNegatives = bundle.getString("FN");
        falseNegatives = "False NEG Errors: " + falseNegatives;
        ((TextView) view.findViewById(R.id.textFalseNegative)).setText(falseNegatives);

        ((TextView) view.findViewById(R.id.fovea)).setText(bundle.getString("fovea"));

        String duration = bundle.getString("TestDuration");
        if (duration.startsWith("-")) {
            duration = "Total Duration: 0:00 " + "\u002A";
            ((TextView) view.findViewById(R.id.textTotalDuration)).setText(duration);

            TextView timer_wrong_warning = view.findViewById(R.id.timer_wrong_warning);
            timer_wrong_warning.setText("\u002A Test Duration not recorded correctly.");
            timer_wrong_warning.setVisibility(View.VISIBLE);
            FirebaseCrashlytics.getInstance().recordException(new NegativeTestDurationException());

        } else {
            duration = "Total Duration: " + duration;
            ((TextView) view.findViewById(R.id.textTotalDuration)).setText(duration);
        }

        String background = bundle.getString("BackgroundIlluminationColorCodeSequence");
        ((TextView) view.findViewById(R.id.textBackground)).setText(background);

        String PupilSize = bundle.getString("PupilSize");
        ((TextView) view.findViewById(R.id.textPupilDia)).setText(PupilSize);

        String textVisualAcuity = bundle.getString("visualAcuity");
        ((TextView) view.findViewById(R.id.textVisualAcuity)).setText(textVisualAcuity);

        /*String agarwal = "OPTHALMOLOGY   DR.AGARWAL'S EYE HOSPITAL\nCHENNAI";
        ((TextView) view.findViewById(R.id.agarwal)).setText(agarwal);*/

       /* String pattern_and_strategy = "Central " + pattern + " " + strategy + " Test";
        Log.d("pattern_and_strategy"," "+pattern_and_strategy);
        ((TextView) view.findViewById(R.id.pattern_and_strategy)).setText(pattern_and_strategy);*/

        ((TextView) view.findViewById(R.id.testConductedTime)).setText(bundle.getString("CreatedDate"));

        String copyright = "\u00a9 Elisar Life Sciences Private Limited. " + CommonUtils.currentYear();
        ((TextView) view.findViewById(R.id.copyRights)).setText(copyright);

        ArrayList<String> result_sensitivity = bundle.getStringArrayList("dt_result_sensitivity");
        ArrayList<String> result_deviation = bundle.getStringArrayList("dt_result_deviation");
        ArrayList<String> result_generalizedDefectCorrectedSensitivityDeviationValue = bundle.getStringArrayList("dt_result_generalizedDefectCorrectedSensitivityDeviationValue");
        ArrayList<String> result_probabilityDeviationValue = bundle.getStringArrayList("dt_result_probabilityDeviationValue");
        ArrayList<String> result_generalizedDefectCorrectedSensitivityDeviationProbabilityValue = bundle.getStringArrayList("dt_result_generalizedDefectCorrectedSensitivityDeviationProbabilityValue");
        double[][][] quadrants = (double[][][]) bundle.getSerializable("quadrants");


        int tempCount = 0;
        int size = result_sensitivity.size();
        double[] sensitiveValues = new double[size];
        for (int i = 0; i < size; i++) {
            sensitiveValues[i] = Double.parseDouble(result_sensitivity.get(i));
            if (sensitiveValues[i] < 8) {
                tempCount++;
            }
        }
        isValueLessThan8 = tempCount > 0;

        String mdD = bundle.getString("meanDeviation");
        String psdD = bundle.getString("psd");
        mdD = String.format("%.2f", Double.parseDouble(mdD));
        psdD = String.format("%.2f", Double.parseDouble(psdD));
        Log.e("mdD", "" + mdD);
        Log.e("psdD", "" + psdD);

        double mdProb = bundle.getDouble("MDProbability");
        double pdProb = bundle.getDouble("PDProbability");
        Log.e("mdProb", " " + mdProb);
        Log.e("pdProb", " " + pdProb);

        if (isValueLessThan8) {
            mdD = "MD : " + mdD + " dB ";
            psdD = "PSD : " + psdD + " dB ";
        } else {
            mdD = "MD : " + mdD + " dB ";
            psdD = "PSD : " + psdD + " dB ";
        }

        if (mdProb < 0.5) {
            mdD = mdD + "P < 0.5%";
        } else if (mdProb < 1) {
            mdD = mdD + "P < 1 %";
        } else if (mdProb < 2) {
            mdD = mdD + "P < 2 %";
        } else if (mdProb < 5) {
            mdD = mdD + "P < 5 %";
        } else {
            mdD = mdD + "P > 5 %";
        }
        if (pdProb < 0.5) {
            psdD = psdD + "P < 0.5%";
        } else if (pdProb < 1) {
            psdD = psdD + "P < 1 %";
        } else if (pdProb < 2) {
            psdD = psdD + "P < 2 %";
        } else if (pdProb < 5) {
            psdD = psdD + "P < 5 %";
        } else {
            psdD = psdD + "P > 5 %";
        }

        //MD = (-13.74 ±  1) dB


        SpannableStringBuilder spanbablePsd = new SpannableStringBuilder(psdD);
        spanbablePsd.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        SpannableStringBuilder spanbablemD = new SpannableStringBuilder(mdD);
        spanbablemD.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        ((TextView) view.findViewById(R.id.mdText)).setText(spanbablemD);
        ((TextView) view.findViewById(R.id.gpsdText)).setText(spanbablePsd);
        // ((TextView) view.findViewById(R.id.opthalmology)).setText("OPTHALMOLOGY");
        if (CommonUtils.isUserSetUpFinished(getContext())) {
            try {
                JSONObject config = CommonUtils.readConfig("Doctor Copy Fragment");
                if (config != null) {
                    String siteOrg = config.getString("OrganizationId") + " " + config.getString("SiteId");
                    ((TextView) view.findViewById(R.id.branch_and_site)).setText(siteOrg);
                    ((TextView) view.findViewById(R.id.deviceId)).setText(config.getString("DeviceId"));
                }
            } catch (JSONException e) {
                Log.e("DoctorsCopyFrag", e.getMessage());
            }
        } else {
            ((TextView) view.findViewById(R.id.branch_and_site)).setText("DR.AGARWAL'S EYE HOSPITAL\nCHENNAI");
        }
        String swVersion = CommonUtils.getVersionCombo();
        ((TextView) view.findViewById(R.id.swVersion)).setText(swVersion);

        ProgressBar progressBar = view.findViewById(R.id.progress);
        ArrayList<String> result = bundle.getStringArrayList("dt_new_result");


        ImageView imageOne = view.findViewById(R.id.imageOne);
        ImageView imageTwo = view.findViewById(R.id.imageTwo);
        ImageView imageThree = view.findViewById(R.id.imageThree);
        ImageView imageFour = view.findViewById(R.id.imageFour);
        ImageView imageFive = view.findViewById(R.id.imageFive);
        ImageView imageGreyScale = view.findViewById(R.id.greyScale);
        ArrayList<String> values = new ArrayList<>();
        eye = bundle.getString("setPatientTestEyeVal");
        values.add(eye);
        values.add(pattern);

        Arrays.sort(sensitiveValues);
        canICancel = sensitiveValues[58] <= 7;
        if (sensitiveValues[58] <= 7) {
            imageThree.setVisibility(View.INVISIBLE);
            imageFive.setVisibility(View.INVISIBLE);
            pd_message_1.setVisibility(View.VISIBLE);
            pd_message_2.setVisibility(View.VISIBLE);
            patternDeviation.setVisibility(View.INVISIBLE);
        } else {
            taskThree = new PD_Plot_Ten_Dash_Two(getActivity(), imageThree).execute(values, result, result_generalizedDefectCorrectedSensitivityDeviationValue, result_sensitivity);
            taskFive = new LoadMappedIconView(getActivity(), imageFive).execute(values, result, result_generalizedDefectCorrectedSensitivityDeviationProbabilityValue);
        }
        taskOne = new LoadMappedView(getActivity(), imageOne).execute(values, result, result_sensitivity);
        taskTwo = new TD_Plot_Ten_dash_Two(getActivity(), imageTwo).execute(values, result, result_deviation, result_sensitivity);

        taskFour = new LoadMappedIconView(getActivity(), imageFour).execute(values, result, result_probabilityDeviationValue);

        taskSix = new TenDashTwoReport.LoadGreyScale(getActivity(), imageGreyScale, eye, progressBar, TenDashTwoReport.this).execute(quadrants);
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (loadingFlag) {
            menu.findItem(R.id.action_doctor_print_report).setVisible(false);
            menu.findItem(R.id.action_doctor_email_report).setVisible(false);
            menu.findItem(R.id.action_doctor_delete_report).setVisible(false);
        } else {
            menu.findItem(R.id.action_doctor_print_report).setVisible(true);
            menu.findItem(R.id.action_doctor_email_report).setVisible(true);
            menu.findItem(R.id.action_doctor_delete_report).setVisible(true);
            MenuItem item = menu.findItem(R.id.action_doctor_delete_report);
            if (CommonUtils.isItUserLoggedIn(getContext())) {
                item.getIcon().setAlpha(130);
            } else {
                item.getIcon().setAlpha(255);
            }
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.doctor_report_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_doctor_delete_report:
                if (CommonUtils.isItUserLoggedIn(getContext())) {
                    CommonUtils.switchToAdminProfileToDeleteReport(getActivity());
                } else {
                    CommonUtils.deleteReportConfirmationDialog(getContext(), getActivity(), resultId, item, true);
                }
                break;
            case R.id.action_doctor_print_report:
                triggerUserAction("print");
                return false;
            case R.id.action_doctor_email_report:
                if (CommonUtils.haveNetworkConnection(getContext()))
                    triggerUserAction("email");
                else
                    CommonUtils.initiateNetworkOptions(getContext(), getActivity(), "NA");
                return true;
          /*  case R.id.action_doctor_bluetooth_report:
                triggerUserAction("bluetooth");
                return false;*/
            default:
                break;
        }
        return false;
    }

    @Override
    public void onStop() {
        super.onStop();
        taskOne.cancel(true);
        taskTwo.cancel(true);
        taskFour.cancel(true);
        taskSix.cancel(true);
        if (!canICancel) {
            taskThree.cancel(true);
            taskFive.cancel(true);
        }
    }

    @DebugLog
    private void triggerUserAction(String action) {
        File file = CommonUtils.isFileExists(fileName, getContext());
        if (action.equals("print")) {
            new TenDashTwoReport.DoctorReportPdf(bundle, getActivity(), getContext(), "print").execute();
            /*if (file != null) {
                //Toast.makeText(getContext(), "File found.. opening file...", Toast.LENGTH_SHORT).show();
                showToast("File found.. opening file...");
                CommonUtils.doPrint(getActivity(), file.getPath());
            } else {
                //Toast.makeText(getContext(), "Generating pdf.. Please wait..", Toast.LENGTH_SHORT).show();
                showToast("Generating pdf.. Please wait..");
                new DoctorReportPdf(bundle, getActivity(), getContext(), "print").execute();
            }*/
        } else if (action.equals("email")) {
            new TenDashTwoReport.DoctorReportPdf(bundle, getActivity(), getContext(), "email").execute();
            /*if (file != null) {
                //Toast.makeText(getContext(), "File found.. opening file...", Toast.LENGTH_SHORT).show();
                showToast("File found.. opening file...");
                CommonUtils.doShare(file, getActivity());
            } else {
                //Toast.makeText(getContext(), "Generating pdf.. Please wait...", Toast.LENGTH_SHORT).show();
                showToast("Generating pdf.. Please wait..");
                new DoctorReportPdf(bundle, getActivity(), getContext(), "email").execute();
            }*/
        } else {
           // new TenDashTwoReport.DoctorReportPdf(bundle, getActivity(), getContext(), "bluetooth").execute();
            //sendFileOverBluetooth();
        }
    }

    private void showToast(String message) {
        Toast toast = Toast.makeText(getContext(), message, Toast.LENGTH_SHORT);
        showBigToast(toast);
    }

    private static class LoadGreyScale extends AsyncTask<double[][][], Void, Bitmap> {
        Activity activity;
        ProgressBar progressBar;
        String eye;
        WeakReference<TenDashTwoReport> weakReference;
        private ImageView imageView;

        LoadGreyScale(Activity activity, ImageView imageView, String eye, ProgressBar progressBar, TenDashTwoReport TenDashTwoReport) {
            this.activity = activity;
            this.imageView = imageView;
            this.eye = eye;
            this.progressBar = progressBar;
            this.weakReference = new WeakReference<>(TenDashTwoReport);
        }

        @Override
        protected Bitmap doInBackground(double[][][]... params) {
            double[][][] quadrants = params[0];
            View greyScale = CommonUtils.createGreyScaleForTenDashTwo(quadrants, activity, eye);
            return CommonUtils.viewToBitmap(greyScale);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            String name = activity.getResources().getResourceEntryName(imageView.getId());
            CommonUtils.saveImage(bitmap, name, activity.getApplicationContext());
            imageView.setImageBitmap(bitmap);
            progressBar.setVisibility(View.GONE);
            weakReference.get().loadingFlag = false;
            activity.invalidateOptionsMenu();
        }
    }

    private class DoctorReportPdf extends AsyncTask<Void, Void, Boolean> {
        Context context;
        String action;
        Activity activity;
        Bundle bundle;
        private ProgressDialog dialog;

        DoctorReportPdf(Bundle bundle, final Activity activity, final Context context, final String action) {
            this.bundle = bundle;
            this.context = context;
            this.action = action;
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
                Log.e("Exception", "onPreExecute of doctors copy fragment " + e.getMessage());
            }
        }

        @SuppressLint("WrongThread")
        @Override
        protected Boolean doInBackground(Void... voids) {
            View doctorLayout;
            LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            RelativeLayout view1 = new RelativeLayout(context);
            String pattern = bundle.getString("setPatientTestPatternVal");
            doctorLayout = mInflater.inflate(R.layout.doctor_report_ten_dash_two, view1, true);
            //doctorLayout = view1;
            doctorLayout.setLayoutParams(new ViewGroup.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                    RelativeLayout.LayoutParams.WRAP_CONTENT));
            doctorLayout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                    View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            doctorLayout.layout(0, 0, doctorLayout.getMeasuredWidth(), doctorLayout.getMeasuredHeight());


            String eye = bundle.getString("setPatientTestEyeVal");
            eye = eye.equals("Right Eye") ? "Eye: Right" : "Eye: Left";
            ((TextView) doctorLayout.findViewById(R.id.reportView_testEye)).setText(eye);

            String sex = bundle.getString("setPatientSexVal");
            String name = bundle.getString("setPatientName");
            name = "Name: " + name;
            //name = sex.equals("Male") ? "Name: Mr. " + name : "Name: Mrs. " + name;
            ((TextView) doctorLayout.findViewById(R.id.reportView_PatientName)).setText(name);


            String mrn = "ID: " + bundle.getString("setPatientMrnNumberVal");
            ((TextView) doctorLayout.findViewById(R.id.reportView_MRNumber)).setText(mrn);

            String dob = bundle.getString("setPatientDOBVal");


            String age = "AGE: " + CommonUtils.getAge(dob);
            ((TextView) doctorLayout.findViewById(R.id.reportView_Age)).setText(age);
            dob = "DOB : " + dob;
            ((TextView) doctorLayout.findViewById(R.id.reportView_DOB)).setText(dob);

            String textVisualAcuity = bundle.getString("visualAcuity");
            ((TextView) doctorLayout.findViewById(R.id.textVisualAcuity)).setText(textVisualAcuity);


            String strategy = bundle.getString("setPatientTestStrategyVal");
            String pattern_and_strategy = "Central " + pattern + " " + strategy + " Test";
            ((TextView) doctorLayout.findViewById(R.id.pattern_and_strategy)).setText(pattern_and_strategy);
            ((TextView) doctorLayout.findViewById(R.id.reportView_testStrategy)).setText(strategy);
            strategy = "Strategy: " + strategy;
            ((TextView) doctorLayout.findViewById(R.id.textStrategy)).setText(strategy);

            String fixationMonitor = "Fixation Monitor: Gaze / Blindspot";
            ((TextView) doctorLayout.findViewById(R.id.textFixationMoniter)).setText(fixationMonitor);

            String fixationTarget = "Fixation Target: Central";
            ((TextView) doctorLayout.findViewById(R.id.textFixationTarget)).setText(fixationTarget);

            String fixationLoss = bundle.getString("FL");
            fixationLoss = "Fixation Losses: " + fixationLoss;
            ((TextView) doctorLayout.findViewById(R.id.textFixationLosses)).setText(fixationLoss);

            String falsePositives = bundle.getString("FP");
            falsePositives = "False POS Errors: " + falsePositives;
            ((TextView) doctorLayout.findViewById(R.id.textFalsePositive)).setText(falsePositives);

            String falseNegatives = bundle.getString("FN");
            falseNegatives = "False NEG Errors: " + falseNegatives;
            ((TextView) doctorLayout.findViewById(R.id.textFalseNegative)).setText(falseNegatives);

            String duration = bundle.getString("TestDuration");
            if (duration.startsWith("-")) {
                duration = "Total Duration: 0:00 " + "\u002A";
                ((TextView) doctorLayout.findViewById(R.id.textTotalDuration)).setText(duration);

                TextView timer_wrong_warning = doctorLayout.findViewById(R.id.timer_wrong_warning);
                timer_wrong_warning.setText("\u002A Test Duration not recorded correctly.");
                timer_wrong_warning.setVisibility(View.VISIBLE);
                FirebaseCrashlytics.getInstance().recordException(new NegativeTestDurationException());
            } else {
                duration = "Total Duration: " + duration;
                ((TextView) doctorLayout.findViewById(R.id.textTotalDuration)).setText(duration);
            }
            /*String agarwal = "OPTHALMOLOGY   DR.AGARWAL'S EYE HOSPITAL\nCHENNAI";
            ((TextView) doctorLayout.findViewById(R.id.agarwal)).setText(agarwal);*/
            ((TextView) doctorLayout.findViewById(R.id.fovea)).setText(bundle.getString("fovea"));


            ((TextView) doctorLayout.findViewById(R.id.testConductedTime)).setText(bundle.getString("CreatedDate"));

            String copyright = "\u00a9 Elisar Life Sciences Private Limited. " + CommonUtils.currentYear();
            ((TextView) doctorLayout.findViewById(R.id.copyRights)).setText(copyright);


            String mdD = bundle.getString("meanDeviation");
            String psdD = bundle.getString("psd");
            mdD = String.format("%.2f", Double.parseDouble(mdD));
            psdD = String.format("%.2f", Double.parseDouble(psdD));
            double mdProb = bundle.getDouble("MDProbability");
            double pdProb = bundle.getDouble("PDProbability");
            Log.e("mdProb", " " + mdProb);
            Log.e("pdProb", " " + pdProb);
            if (isValueLessThan8) {
                mdD = "MD : " + mdD + " dB ";
                psdD = "PSD : " + psdD + " dB ";
            } else {
                mdD = "MD : " + mdD + " dB ";
                psdD = "PSD : " + psdD + " dB ";
            }

            if (mdProb < 0.5) {
                mdD = mdD + "P < 0.5%";
            } else if (mdProb < 1) {
                mdD = mdD + "P < 1 %";
            } else if (mdProb < 2) {
                mdD = mdD + "P < 2 %";
            } else if (mdProb < 5) {
                mdD = mdD + "P < 5 %";
            } else {
                mdD = mdD + "P > 5 %";
            }
            if (pdProb < 0.5) {
                psdD = psdD + "P < 0.5%";
            } else if (pdProb < 1) {
                psdD = psdD + "P < 1 %";
            } else if (pdProb < 2) {
                psdD = psdD + "P < 2 %";
            } else if (pdProb < 5) {
                psdD = psdD + "P < 5 %";
            } else {
                psdD = psdD + "P > 5 %";
            }


            SpannableStringBuilder spanbablePsd = new SpannableStringBuilder(psdD);
            spanbablePsd.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            SpannableStringBuilder spanbablemD = new SpannableStringBuilder(mdD);
            spanbablemD.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);


            ((TextView) doctorLayout.findViewById(R.id.mdData)).setText(spanbablemD);
            ((TextView) doctorLayout.findViewById(R.id.psdData)).setText(spanbablePsd);


            ((TextView) doctorLayout.findViewById(R.id.opthalmology)).setText(activity.getText(R.string.ophthalmology));

            if (CommonUtils.isUserSetUpFinished(getContext())) {
                try {
                    JSONObject config = CommonUtils.readConfig("Doctor Copy Fragment PDF");
                    if (config != null) {
                        String siteOrg = config.getString("OrganizationId") + " \n" + config.getString("SiteId");
                        ((TextView) doctorLayout.findViewById(R.id.branch_and_site)).setText(siteOrg);
                        ((TextView) doctorLayout.findViewById(R.id.deviceId)).setText(config.getString("DeviceId"));
                    }
                } catch (JSONException e) {
                    Log.e("DoctorReportActivity", e.getMessage());
                }
            } else {
                ((TextView) doctorLayout.findViewById(R.id.branch_and_site)).setText("DR.AGARWAL'S EYE HOSPITAL\nCHENNAI");
            }
            String swVersion = CommonUtils.getVersionCombo();
            ((TextView) doctorLayout.findViewById(R.id.swVersion)).setText(swVersion);
            String root = context.getFilesDir().getAbsolutePath();

            ImageView greyScale = doctorLayout.findViewById(R.id.greyScale);

            ImageView imageOne = doctorLayout.findViewById(R.id.imageOne);
            ImageView imageTwo = doctorLayout.findViewById(R.id.imageTwo);
            ImageView imageThree = doctorLayout.findViewById(R.id.imageThree);
            ImageView imageFour = doctorLayout.findViewById(R.id.imageFour);
            ImageView imageFive = doctorLayout.findViewById(R.id.imageFive);
            TextView pd_message_1 = doctorLayout.findViewById(R.id.pd_message_1);
            TextView pd_message_2 = doctorLayout.findViewById(R.id.pd_message_2);
            TextView patternDeviation = doctorLayout.findViewById(R.id.patternDeviation);
            Log.e("canICancel", " " + canICancel);
            if (canICancel) {
                imageThree.setVisibility(View.INVISIBLE);
                imageFive.setVisibility(View.INVISIBLE);
                pd_message_1.setVisibility(View.VISIBLE);
                pd_message_2.setVisibility(View.VISIBLE);
                patternDeviation.setVisibility(View.INVISIBLE);
            } else {
                imageThree.setImageURI(null);
                imageThree.setImageURI(Uri.parse(root + "/imageThree.png"));

                imageFive.setImageURI(null);
                imageFive.setImageURI(Uri.parse(root + "/imageFive.png"));
            }


            greyScale.setImageURI(null);
            greyScale.setImageURI(Uri.parse(root + "/greyScale.png"));

            imageOne.setImageURI(null);
            imageOne.setImageURI(Uri.parse(root + "/imageOne.png"));

            imageTwo.setImageURI(null);
            imageTwo.setImageURI(Uri.parse(root + "/imageTwo.png"));

            imageFour.setImageURI(null);
            imageFour.setImageURI(Uri.parse(root + "/imageFour.png"));

            mrn = bundle.getString("setPatientMrnNumberVal");
            CommonUtils.saveBitmapFromView(doctorLayout, context, activity, action, "doctor_copy", mrn, testedAt, bundle.getString("setPatientTestEyeVal"));
            return null;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (dialog.isShowing())
                dialog.dismiss();
        }
    }
}