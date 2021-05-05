package com.agyohora.mobileperitc.ui;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.agyohora.mobileperitc.R;
import com.agyohora.mobileperitc.adapters.ClickerHistoryAdapter;
import com.agyohora.mobileperitc.data.database.AppDatabase;
import com.agyohora.mobileperitc.data.database.DatabaseInitializer;
import com.agyohora.mobileperitc.data.database.entity.ClickerHistory;
import com.agyohora.mobileperitc.model.ClickerHistoryModel;
import com.agyohora.mobileperitc.model.ResultModel;
import com.agyohora.mobileperitc.myapplication.MyApplication;
import com.agyohora.mobileperitc.utils.CommonUtils;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class ClickerHistoryList extends AppCompatActivity {

    private RecyclerView mRecyclerView;
    private TextView empty;
    private ArrayList<ClickerHistoryModel> mArrayList;
    private ClickerHistoryAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clicker_history);
        getSupportActionBar().setElevation(0);
        int count = CommonUtils.getPRBCount(this);
        Toast.makeText(this, "Current PRB Count " + count, Toast.LENGTH_SHORT).show();
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
    }

    @Override
    protected void onStop() {
        Log.d("onStop", "Called");
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        return super.onOptionsItemSelected(item);
    }

    private void init() {
        mRecyclerView = findViewById(R.id.card_recycler_view);
        empty = findViewById(R.id.empty_view);
        initViews();
    }

    private void resume() {
        mRecyclerView = findViewById(R.id.card_recycler_view);
        empty = findViewById(R.id.empty_view);
        initViews();
    }

    private void initViews() {
        mRecyclerView.setHasFixedSize(true);
        ArrayList<ResultModel> resultModels = new ArrayList<>();
        mArrayList = new ArrayList<>();
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        new ClickerHistoryList.MyTask(this).execute();
    }


    private static class MyTask extends AsyncTask<Void, Void, List<ClickerHistory>> {

        private WeakReference<ClickerHistoryList> activityReference;

        MyTask(ClickerHistoryList context) {
            activityReference = new WeakReference<>(context);
        }

        @Override
        protected List<ClickerHistory> doInBackground(Void... params) {

            List<ClickerHistory> clickerHistories = DatabaseInitializer.getClickerHistory(AppDatabase.getAppDatabase(MyApplication.getInstance()));
            Collections.sort(clickerHistories, new ClickerHistoryList.StringDateComparator());
            return clickerHistories;
        }

        @Override
        protected void onPostExecute(List<ClickerHistory> clickerHistories) {
            ClickerHistoryList activity = activityReference.get();
            Log.e("clickerHistories", " " + clickerHistories.size());
            int size = clickerHistories.size();
            if (size > 0) {
                for (ClickerHistory clickerHistory : clickerHistories) {
                    try {
                        activity.mArrayList.add(new ClickerHistoryModel("" + clickerHistory.getId(), "" + clickerHistory.getNature(), "" + clickerHistory.getService_no(), "" + clickerHistory.getSerial_number(), "" + clickerHistory.getCount(), "" + clickerHistory.getCreateDate()));
                        Log.e("clickerHistories", "getNature " + clickerHistory.getNature());
                        Log.e("clickerHistories", "getService_no " + clickerHistory.getService_no());
                        Log.e("clickerHistories", "getSerial_number " + clickerHistory.getSerial_number());
                        Log.e("clickerHistories", "getCount " + clickerHistory.getCount());
                        Log.e("clickerHistories", "getCreateDate " + clickerHistory.getCreateDate());
                    } catch (Exception e) {
                        Log.e("Exception", " " + e.getMessage());
                    }
                }
                activity.mAdapter = new ClickerHistoryAdapter(activity.mArrayList, activity);
                activity.mRecyclerView.setAdapter(activity.mAdapter);
                activity.mAdapter.notifyDataSetChanged();
            } else {
                Log.d("Count", "Zero");
                activity.empty.setVisibility(View.VISIBLE);
                // activity.mRecyclerView.setAdapter(activity.mAdapter);
                // activity.mAdapter.notifyDataSetChanged();
            }
        }
    }

    static class StringDateComparator implements Comparator<ClickerHistory> {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss aaa", Locale.US);

        @Override
        public int compare(ClickerHistory o1, ClickerHistory o2) {
            try {
                return o2.getCreateDate().compareTo(o1.getCreateDate());
            } catch (Exception e) {
                Log.e("StringDateComparator", " " + e.getMessage());
                return 0;
            }
        }
    }
}
