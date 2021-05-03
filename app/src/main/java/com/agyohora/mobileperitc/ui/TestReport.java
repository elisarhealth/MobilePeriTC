package com.agyohora.mobileperitc.ui;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.agyohora.mobileperitc.R;
import com.agyohora.mobileperitc.data.preferences.AppPreferencesHelper;
import com.agyohora.mobileperitc.myapplication.MyApplication;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;

import java.util.ArrayList;
import java.util.List;

import static com.agyohora.mobileperitc.actions.Actions.backToHomeScreen;
import static com.agyohora.mobileperitc.utils.AppConstants.PREF_NAME;
import static com.agyohora.mobileperitc.utils.CommonUtils.deleteReportConfirmationDialog;

/**
 * Created by Invent on 1-3-18.
 * TabView activity to show both Doctor's copy and patient's copy using fragments
 */

public class TestReport extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private Bundle bundle;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
        switch (item.getItemId()) {
            case R.id.action_post_test_delete_test:
                String rowId = new AppPreferencesHelper(MyApplication.getInstance(), PREF_NAME).getLastInsertedRow();
                Log.d("Row to be deleted", " " + rowId);
                deleteReportConfirmationDialog(TestReport.this, TestReport.this, rowId, item, true);
                return true;
                    /*case R.id.action_post_test_retest:
                        //Actions.beginTestProfile();
                        finish();
                        Actions.beginPatientDetails();
                        return true;*/
            case R.id.action_post_test_home:
                item.setEnabled(false);
                finish();
                backToHomeScreen();
                return true;
        }
        return false;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressDialog = ProgressDialog.show(this, "", "Loading...");
        progressDialog.show();
        setContentView(R.layout.activity_test_report);
        bundle = getIntent().getBundleExtra("Payload");
        ViewPager viewPager = findViewById(R.id.viewpager);
        setupViewPager(viewPager);
        getSupportActionBar().setElevation(0);
        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(viewPager);
        String visibility = getIntent().getExtras().getString("bottomView");
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        bottomNavigationView.setVisibility(visibility.equals("invisible") ? View.GONE : View.VISIBLE);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (progressDialog.isShowing())
            progressDialog.dismiss();
    }

    /* @Override
     public boolean onCreateOptionsMenu(Menu menu) {
         return super.onCreateOptionsMenu(menu);
     }

     @Override
     public boolean onOptionsItemSelected(MenuItem item) {

         Log.d("OnMenuClicked","fromPrent");
         return super.onOptionsItemSelected(item);
     }
 */
    private void setupViewPager(ViewPager viewPager) {
        String pattern = bundle.getString("setPatientTestPatternVal");
        Log.e("Pattern", " " + pattern);
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        Fragment doctor;
        if (pattern.equals("10-2")) {
            doctor = new TenDashTwoReport();
        } else {
            doctor = new DoctorsCopyFragment();
        }
        Fragment patient = new PatientCopyFragment();
        /*doctor.setArguments(bundle);
        patient.setArguments(bundle);*/
        adapter.addFragment(doctor, "Report");
        //adapter.addFragment(patient, "Patient's Copy");

        viewPager.setAdapter(adapter);
    }

    public void showImage(View v) {
        Dialog builder = new Dialog(this);
        builder.requestWindowFeature(Window.FEATURE_NO_TITLE);
        builder.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.WHITE));
        builder.setOnDismissListener(dialogInterface -> {

        });
        ImageView image = (ImageView) v;
        ImageView imageView = new ImageView(this);
        imageView.setImageDrawable(image.getDrawable());
        builder.addContentView(imageView, new RelativeLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        builder.show();
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {

            Fragment fragment = mFragmentList.get(position);
            fragment.setArguments(bundle);
            return fragment;
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
}