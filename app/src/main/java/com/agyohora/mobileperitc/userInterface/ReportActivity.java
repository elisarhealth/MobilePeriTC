package com.agyohora.mobileperitc.userInterface;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.TabLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import android.widget.Toast;

import com.agyohora.mobileperitc.R;
import com.agyohora.mobileperitc.actions.Actions;


public class ReportActivity extends AppCompatActivity {

    public static Context applicationContext;
    public static Bundle b;
    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v4.app.FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        applicationContext = getApplicationContext();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);


        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        b = getIntent().getBundleExtra("Payload");

        //Set patient Information
        ((TextView) findViewById(R.id.rp_patientName)).setText(b.getString("PatName"));
        ((TextView) findViewById(R.id.rp_patAge)).setText(b.getString("PatAge"));
        ((TextView) findViewById(R.id.rp_testEye)).setText(b.getString("testEye"));
        ((TextView) findViewById(R.id.rp_testType)).setText(b.getString("testType"));

        ((TextView) findViewById(R.id.pt_falsePostitiveState)).setText(b.getString("FP"));
        ((TextView) findViewById(R.id.pt_falseNegetiveState)).setText(b.getString("FN"));
        ((TextView) findViewById(R.id.pt_fixationState)).setText(b.getString("FL"));

        /*FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(),MyActivity.class);
                startActivity(intent);
            }
        });*/

    }


    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_report, container, false);
            TextView textView = (TextView) rootView.findViewById(R.id.section_label);
            textView.setText(getString(R.string.section_format, getArguments().getInt(ARG_SECTION_NUMBER)));
            return rootView;
        }
    }


    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position){
                case 0 :
                    Fragment absValueFragment = new AbsoluteValueFragment();
                    absValueFragment.setArguments(b);
                    return absValueFragment;
                case 1:
                    Fragment meanDeviationFragment = new MeanDeviationFragment();
                    meanDeviationFragment.setArguments(b);
                    return meanDeviationFragment;
                case 2:
                    Fragment patternDeviationFragment = new PatternDeviationFragment();
                    patternDeviationFragment.setArguments(b);
                    return patternDeviationFragment;

            }
            return null;
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "ABSOLUTE VALUES";
                case 1:
                    return "TOTAL DEVIATION";
                case 2:
                    return "PATTERN DEVIATION";
            }
            return null;
        }
    }
}
