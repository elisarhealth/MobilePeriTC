package com.agyohora.mobileperitc.ui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
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
import com.agyohora.mobileperitc.asynctasks.LoadDefectiveGraph;
import com.agyohora.mobileperitc.utils.CommonUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import static com.agyohora.mobileperitc.utils.CommonUtils.showBigToast;

/**
 * Created by Invent on 1-3-18.
 * Fragment to generate the Patient's view of report
 */

@SuppressWarnings("unchecked")
public class PatientCopyFragment extends Fragment {
    private Bundle bundle;
    private String resultId, testedAt;
    private static String fileName;
    private boolean loadingFlag;
    private AsyncTask taskOne, taskTwo;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        loadingFlag = true;
        bundle = this.getArguments();
        resultId = bundle.getString("ResultId");
        testedAt = bundle != null ? bundle.getString("TestedAt") : "";
        Log.d("OnCreate", "Called");

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.patient_copy_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        int point05 = 0, point1 = 0, point2 = 0, point5 = 0, point5plus = 0;

        String sex = bundle.getString("setPatientSexVal");
        String strategy = bundle.getString("setPatientTestStrategyVal");
        String pattern = bundle.getString("setPatientTestPatternVal");
        String eye = bundle.getString("setPatientTestEyeVal");

        String name = bundle.getString("setPatientName");
        name = "Name: " + name;
        //name = sex.equals("Male") ? "Name: Mr. " + name : "Name: Mrs. " + name;
        ((TextView) view.findViewById(R.id.reportView_PatientName)).setText(name);

        String pattern_and_strategy = "Central " + pattern + " " + strategy + " Test";
        ((TextView) view.findViewById(R.id.pattern_and_strategy)).setText(pattern_and_strategy);


        String age = "AGE: " + CommonUtils.getAge(bundle.getString("setPatientDOBVal"));
        ((TextView) view.findViewById(R.id.reportView_Age)).setText(age);

        String mrn = bundle.getString("setPatientMrnNumberVal");
        String CreatedDate = bundle.getString("CreatedDate");
        fileName = CommonUtils.generateFileName("patient_copy", mrn, CreatedDate, eye);
        mrn = "ID: " + mrn;
        ((TextView) view.findViewById(R.id.reportView_MRNumber)).setText(mrn);

        String fieldOfVision = pattern.equals("30-2") ? getResources().getString(R.string.field_of_vision_72) : getResources().getString(R.string.field_of_vision_54);
        ((TextView) view.findViewById(R.id.fieldOfVision)).setText(fieldOfVision);

        ((TextView) view.findViewById(R.id.testConductedTime)).setText(CreatedDate);

        String copyright = "\u00a9 Elisar Life Sciences Private Limited. " + CommonUtils.currentYear();
        ((TextView) view.findViewById(R.id.copyRights)).setText(copyright);

        if (CommonUtils.isUserSetUpFinished(getContext())) {
            try {
                JSONObject config = CommonUtils.readConfig("Patient Copy Fragment");
                if (config != null) {
                    //String siteOrg = config.getString("OrganizationId") + " \n" + config.getString("SiteId");
                    //((TextView) view.findViewById(R.id.branch_and_site)).setText(siteOrg);
                    ((TextView) view.findViewById(R.id.deviceId)).setText(config.getString("DeviceId"));
                }
            } catch (JSONException e) {
                Log.e("DoctorReportActivity", e.getMessage());
            }
        } /*else {
            ((TextView) view.findViewById(R.id.branch_and_site)).setText("DR.AGARWAL'S EYE HOSPITAL\nCHENNAI");
        }*/
        String swVersion = CommonUtils.getVersionCombo();
        ((TextView) view.findViewById(R.id.swVersion)).setText(swVersion);


        eye = eye.equals("Right Eye") ? "Test Result - Right Eye" : "Test Result - Left Eye";
        ((TextView) view.findViewById(R.id.reportView_testEye)).setText(eye);

        String limit = "Outside normal limits";
        ((TextView) view.findViewById(R.id.reportView_testResultLimit)).setText(limit);

        final String probability = "high probability";
        String indication = "This indicates a <u>" + probability + "</u> of Glaucoma.</p>";
        ((TextView) view.findViewById(R.id.reportView_testResultIndication)).setText(Html.fromHtml(indication));

        String advice = "Please visit an eye specialist at the earliest";
        ((TextView) view.findViewById(R.id.reportView_testResultAdvice)).setText(Html.fromHtml(advice));
        ProgressBar progressBar = view.findViewById(R.id.progress);
        final ArrayList<String> pointValues = bundle.getStringArrayList("dt_result_probabilityDeviationValue");
        final ArrayList<String> coordinates = bundle.getStringArrayList("dt_new_result");
        ImageView patientVision = view.findViewById(R.id.patientVision);
        ArrayList<String> values = new ArrayList<>();
        values.add(pattern);
        values.add(bundle.getString("setPatientTestEyeVal"));
        taskOne = new LoadDefectiveGraph(getActivity()).execute(values, coordinates, pointValues);
        taskTwo = new LoadGreyScale(getActivity(), patientVision, bundle.getString("setPatientTestEyeVal"), progressBar, PatientCopyFragment.this, strategy).execute(values, coordinates, pointValues);
        for (int i = 0; i < pointValues.size(); i++) {
            float point = Float.parseFloat(pointValues.get(i));
            if (point <= 0.5)
                point05++;
            else if (point > 0.5 && point < 1)
                point1++;
            else if (point >= 1 && point < 2)
                point2++;
            else if (point >= 2 && point < 5)
                point5++;
            else
                point5plus++;
        }

        int totalDefective = point1 + point2 + point5 + point05;
        int total = pointValues.size();
        ((TextView) view.findViewById(R.id.whiteView)).setText(String.valueOf(point5plus));
        ((TextView) view.findViewById(R.id.greenView)).setText(String.valueOf(point5));
        ((TextView) view.findViewById(R.id.yellowView)).setText(String.valueOf(point2));
        ((TextView) view.findViewById(R.id.orangeView)).setText(String.valueOf(point1));
        ((TextView) view.findViewById(R.id.redView)).setText(String.valueOf(point05));
        String defective = "Defective points: " + totalDefective + " / " + total;
        ((TextView) view.findViewById(R.id.defectivePoints)).setText(defective);

    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        Log.d("onPrepareOptionsMenu", "loading flag " + loadingFlag);
        super.onPrepareOptionsMenu(menu);
        if (loadingFlag) {
            menu.findItem(R.id.action_patient_print_report).setVisible(false);
            menu.findItem(R.id.action_patient_email_report).setVisible(false);
            menu.findItem(R.id.action_patient_delete_report).setVisible(false);
        } else {
            menu.findItem(R.id.action_patient_print_report).setVisible(true);
            menu.findItem(R.id.action_patient_email_report).setVisible(true);
            menu.findItem(R.id.action_patient_delete_report).setVisible(true);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        Log.d("onCreateOptionsMenu", "");
        inflater.inflate(R.menu.patient_report_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_patient_delete_report:
                CommonUtils.deleteReportConfirmationDialog(getContext(), getActivity(), resultId, item, true);
                break;
            case R.id.action_patient_print_report:
                triggerUserAction("print");
                return true;
            case R.id.action_patient_email_report:
                if (CommonUtils.haveNetworkConnection(getContext()))
                    triggerUserAction("email");
                else
                    CommonUtils.initiateNetworkOptions(getContext(), getActivity(), "NA");
                return true;
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
    }

    private void triggerUserAction(String action) {
        File file = CommonUtils.isFileExists(fileName, getContext());
        if (action.equals("print")) {
            if (file != null) {
                showToast("File found.. opening file...");
                CommonUtils.doPrint(getActivity(), file.getPath(), file);
            } else {
                showToast("Generating pdf.. Please wait..");
                new PatientReportPdf(bundle, getActivity(), getContext(), "print").execute();
            }
        } else {
            if (file != null) {
                showToast("File found.. opening file...");
                CommonUtils.doShare(getContext(), file);
            } else {
                showToast("Generating pdf.. Please wait..");
                new PatientReportPdf(bundle, getActivity(), getContext(), "email").execute();
            }
        }
    }

    private static class LoadGreyScale extends AsyncTask<ArrayList<String>, Void, Bitmap> {
        Activity activity;
        private ImageView imageView;
        ProgressBar progressBar;
        String eye, strategy;
        private ProgressDialog dialog;
        private WeakReference<PatientCopyFragment> activityReference;

        LoadGreyScale(Activity activity, ImageView imageView, String eye, ProgressBar progressBar, PatientCopyFragment copyFragment, String strategy) {
            this.activity = activity;
            this.imageView = imageView;
            this.eye = eye;
            this.strategy = strategy;
            this.progressBar = progressBar;
            this.activityReference = new WeakReference<>(copyFragment);
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            /*try {
                dialog = ProgressDialog.show(activity, null, null, true, false);
                dialog.setContentView(R.layout.progress_layout);
                dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
            } catch (Exception e) {
               Log.e("Exception","onPreExecute of patient copy fragment"+e.getMessage());
            }*/
        }

        @Override
        protected Bitmap doInBackground(ArrayList<String>... params) {
            ArrayList<String> values = params[0];
            ArrayList<String> coordinates = params[1];
            ArrayList<String> pointValues = params[2];
            String pattern = values.get(0);
            String eye = values.get(1);
            //View greyScale = CommonUtils.getDefectivePointGreyGraph(activity, pattern, eye, coordinates, pointValues);
            //Log.d("GreyScale", "is null " + greyScale);
            return CommonUtils.viewToBitmap(CommonUtils.getDefectivePointGreyGraph(activity, pattern, eye, coordinates, pointValues, strategy));
        }

        @Override
        protected void onPostExecute(Bitmap grey) {
            String name = activity.getResources().getResourceEntryName(imageView.getId());
            //Log.d("SavedName", " " + name);
            Log.d("PatientCopyFragment", "Before calling Save");
            //CommonUtils.saveImage(grey, name, activity.getApplicationContext());
            //CommonUtils.saveImage(CommonUtils.getResizedBitmap(grey), "Resized", activity.getApplicationContext());
            CommonUtils.overlayBitmap(activity, grey, imageView);
            progressBar.setVisibility(View.GONE);
            Log.d("PatientCopyFragment", "After calling Save");
            activityReference.get().loadingFlag = false;
            activity.invalidateOptionsMenu();
            /*if (dialog.isShowing())
                dialog.dismiss();*/
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
                Log.e("Exception", "onPreExecute of patient copy fragment " + e.getMessage());
            }
        }


        @SuppressLint("WrongThread")
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
            int point05 = 0, point1 = 0, point2 = 0, point5 = 0, point5plus = 0;
            String sex = bundle.getString("setPatientSexVal");
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
            eye = eye.equals("Right Eye") ? "Test Result: Right Eye" : "Test Result: Left Eye";
            ((TextView) patientLayout.findViewById(R.id.reportView_testEye)).setText(eye);

            String limit = "Outside normal limits";
            ((TextView) patientLayout.findViewById(R.id.reportView_testResultLimit)).setText(limit);

            final String probability = "high probability";
            String indication = "This indicates a <u>" + probability + "</u> of Glaucoma.</p>";
            ((TextView) patientLayout.findViewById(R.id.reportView_testResultIndication)).setText(Html.fromHtml(indication));

            String advice = "Please visit an eye specialist at the earliest";
            ((TextView) patientLayout.findViewById(R.id.reportView_testResultAdvice)).setText(Html.fromHtml(advice));

            ((TextView) patientLayout.findViewById(R.id.testConductedTime)).setText(bundle.getString("CreatedDate"));

            String copyright = "\u00a9 Elisar Life Sciences Private Limited. " + CommonUtils.currentYear();
            ((TextView) patientLayout.findViewById(R.id.copyRights)).setText(copyright);

            if (CommonUtils.isUserSetUpFinished(getContext())) {
                try {
                    JSONObject config = CommonUtils.readConfig("Patient Copy Fragment Pdf");
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

            ImageView imageGrey = patientLayout.findViewById(R.id.patientVision);
            Log.d("ImageGrey", "Width" + imageGrey.getWidth());
            Log.d("ImageGrey", "Height" + imageGrey.getHeight());
            imageGrey.setImageURI(null);
            imageGrey.setImageURI(Uri.parse(root + "/patientVisionCropped.png"));

            final ArrayList<String> pointValues = bundle.getStringArrayList("dt_result_probabilityDeviationValue");
            for (int i = 0; i < pointValues.size(); i++) {
                float point = Float.parseFloat(pointValues.get(i));
                if (point < 0.5)
                    point05++;
                else if (point > 0.5 && point < 1)
                    point1++;
                else if (point > 1 && point < 2)
                    point2++;
                else if (point > 2 && point < 5)
                    point5++;
                else
                    point5plus++;
            }

            int totalDefective = point1 + point2 + point5 + point05;
            int total = pointValues.size();
            ((TextView) patientLayout.findViewById(R.id.whiteView)).setText(String.valueOf(point5plus));
            ((TextView) patientLayout.findViewById(R.id.greenView)).setText(String.valueOf(point5));
            ((TextView) patientLayout.findViewById(R.id.yellowView)).setText(String.valueOf(point2));
            ((TextView) patientLayout.findViewById(R.id.orangeView)).setText(String.valueOf(point1));
            ((TextView) patientLayout.findViewById(R.id.redView)).setText(String.valueOf(point05));
            String defective = "Defective points: " + totalDefective + " / " + total;
            ((TextView) patientLayout.findViewById(R.id.resultView_defective_points)).setText(defective);
            mrn = bundle.getString("setPatientMrnNumberVal");
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
        Toast toast = Toast.makeText(getContext(), message, Toast.LENGTH_SHORT);
        showBigToast(toast);
    }

}