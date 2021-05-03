package com.agyohora.mobileperitc.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.agyohora.mobileperitc.R;
import com.agyohora.mobileperitc.adapters.ResultDataAdapter;
import com.agyohora.mobileperitc.data.database.AppDatabase;
import com.agyohora.mobileperitc.data.database.entity.PatientTestResult;
import com.agyohora.mobileperitc.model.ResultModel;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.agyohora.mobileperitc.data.database.DatabaseInitializer.getAbstractData;

/**
 * Created by Invent on 28-1-18.
 * List with abstract details will be listed using ResultDataAdapter
 */

public class TestResultsListActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private TextView empty;
    private SearchView searchView;
    private ArrayList<ResultModel> mArrayList;
    private ResultDataAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_results);
        /*Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/
        getSupportActionBar().setElevation(0);
        //init();
    }

    @Override
    protected void onPause() {
        Log.d("onPause", "Called");
        super.onPause();
    }

    @Override
    protected void onResume() {
        Log.d("onResume", "Called");
        super.onResume();
        resume();
        /*mAdapter = new ResultDataAdapter(resultModels, TestResultsListActivity.this);
        empty.setVisibility(mAdapter.getItemCount() == 0 ? View.INVISIBLE : View.VISIBLE);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.notifyDataSetChanged();*/
    }

    @Override
    protected void onStop() {
        Log.d("onStop", "Called");
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        /*getMenuInflater().inflate(R.menu.menu_search, menu);

        MenuItem search = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(search);
        search(searchView);*/
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    private void init() {
        mRecyclerView = findViewById(R.id.card_recycler_view);
        empty = findViewById(R.id.empty_view);
        SearchView searchView = findViewById(R.id.searchView);
        //searchView.setLayoutParams(new ActionBar.LayoutParams(Gravity.END));
        searchView.onActionViewExpanded(); //new Added line
        searchView.setIconifiedByDefault(false);
        searchView.setQueryHint("Search by Patient Name / MR No / Mobile");
        if (!searchView.isFocused()) {
            searchView.clearFocus();
        }
        search(searchView);
        initViews();
    }

    private void resume() {
        mRecyclerView = findViewById(R.id.card_recycler_view);
        empty = findViewById(R.id.empty_view);
        searchView = findViewById(R.id.searchView);
        //searchView.setLayoutParams(new ActionBar.LayoutParams(Gravity.END));
        searchView.onActionViewExpanded(); //new Added line
        searchView.setIconifiedByDefault(false);
        searchView.setQueryHint("Search by Patient Name / MR No / Mobile");
        searchView.setQuery("", false);
        searchView.clearFocus();
        search(searchView);
        initViews();
    }

    private void initViews() {
        mRecyclerView.setHasFixedSize(true);
        ArrayList<ResultModel> resultModels = new ArrayList<>();
        mArrayList = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        // linearLayoutManager.setStackFromEnd(true);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mAdapter = new ResultDataAdapter(resultModels, TestResultsListActivity.this);
        mRecyclerView.setAdapter(mAdapter);
        new MyTask(this).execute();
    }

    private void search(SearchView searchView) {

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                if (mAdapter != null) mAdapter.getFilter().filter(newText);
                return true;
            }
        });
    }

    private static class MyTask extends AsyncTask<Void, Void, List<PatientTestResult>> {

        private WeakReference<TestResultsListActivity> activityReference;

        MyTask(TestResultsListActivity context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected List<PatientTestResult> doInBackground(Void... params) {

            List<PatientTestResult> patientTestResults = getAbstractData(AppDatabase.getAppDatabase(MainActivity.applicationContext));
            Collections.sort(patientTestResults, new StringDateComparator());
            return patientTestResults;
        }

        @Override
        protected void onPostExecute(List<PatientTestResult> patientTestResults) {
            TestResultsListActivity activity = activityReference.get();
            int size = patientTestResults.size();
            if (size > 0) {
                Log.e("TestResultsList", "Size " + size);
                activity.searchView.setQueryHint("Search among " + size + " records by Name / MR No / Mobile");
                activity.empty.setVisibility(View.GONE);
                for (PatientTestResult testResult : patientTestResults) {
                    try {
                        String id = String.valueOf(testResult.getId());
                        String version = String.valueOf(testResult.getPerimeteryObjectVersion());
                        String patName = testResult.getPatientName();
                        String patSex = testResult.getPatientSex();
                        String patMrn = testResult.getPatientMrn();
                        String patMobile = testResult.getPatientMobile();
                        String patMobileUnformatted = patMobile.replace("-", "");
                        String patEye = testResult.getTestEye();
                        String testPattern = testResult.getTestPattern();
                        String testType = testResult.getTestType();
                        String testSuggestion = testPattern + "  " + testType; //= testResult.getTestSuggestion();
                        Date createdDate = testResult.getCreateDate();
                        // Log.d("Values", " " + id + " " + patSex + " " + patName + " " + patMrn + " " + patMobile + " " + patEye + " " + testSuggestion + " " + createdDate);
                        activity.mArrayList.add(new ResultModel(id, version, patSex, patName, patMrn, patMobile, patMobileUnformatted, patEye, testSuggestion, createdDate));
                    } catch (Exception e) {
                        Log.e("Empty Adapter", " " + e.getMessage());
                    }
                }
                activity.mAdapter = new ResultDataAdapter(activity.mArrayList, activity);
                activity.mRecyclerView.setAdapter(activity.mAdapter);
                activity.mAdapter.notifyDataSetChanged();
            } else {
                Log.d("Count", "Zero");
                activity.empty.setVisibility(View.VISIBLE);
                activity.mRecyclerView.setAdapter(activity.mAdapter);
                activity.mAdapter.notifyDataSetChanged();
            }
        }
    }

    static class StringDateComparator implements Comparator<PatientTestResult> {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss aaa", Locale.US);

        @Override
        public int compare(PatientTestResult o1, PatientTestResult o2) {
            try {
                return o2.getCreateDate().compareTo(o1.getCreateDate());
            } catch (Exception e) {
                Log.e("StringDateComparator", " " + e.getMessage());
                return 0;
            }
        }
    }
}