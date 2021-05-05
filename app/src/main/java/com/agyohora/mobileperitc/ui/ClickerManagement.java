package com.agyohora.mobileperitc.ui;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.agyohora.mobileperitc.R;
import com.agyohora.mobileperitc.actions.Actions;
import com.agyohora.mobileperitc.data.database.AppDatabase;
import com.agyohora.mobileperitc.data.database.DatabaseInitializer;
import com.agyohora.mobileperitc.data.database.entity.ClickerHistory;
import com.agyohora.mobileperitc.data.preferences.AppPreferencesHelper;
import com.agyohora.mobileperitc.interfaces.AsyncDbInsertRecordTask;
import com.agyohora.mobileperitc.myapplication.MyApplication;
import com.agyohora.mobileperitc.store.Store;
import com.agyohora.mobileperitc.utils.CommonUtils;
import com.agyohora.mobileperitc.worksheduler.workcreator.WorkCreator;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Date;

import static com.agyohora.mobileperitc.utils.AppConstants.DEVICE_PREF;

public class ClickerManagement extends AppCompatActivity implements View.OnClickListener {
    AppPreferencesHelper appPreferencesHelper;
    private static ArrayAdapter<String> reasonAdapter;
    private String[] reasonOptions;
    Spinner reasonSpinner;
    EditText service_report_number, prb_serial_value, prb_count_value;
    Button prb_update;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_prb_management);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setElevation(0);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        reasonOptions = getResources().getStringArray(R.array.reason);
        appPreferencesHelper = new AppPreferencesHelper(this.getApplicationContext(), DEVICE_PREF);
        reasonAdapter = new ArrayAdapter<>(
                this,
                R.layout.spinner_item,
                reasonOptions);
        reasonSpinner = findViewById(R.id.reason_spinner);
        reasonSpinner.setAdapter(reasonAdapter);
        service_report_number = findViewById(R.id.service_report_number);
        prb_serial_value = findViewById(R.id.prb_serial_value);
        prb_count_value = findViewById(R.id.prb_count_value);
        prb_update = findViewById(R.id.prb_update);
        prb_update.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.prb_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_prb_history:
                // startActivity(new Intent(this, ClickerHistoryActivity.class));
                startActivity(new Intent(this, ClickerHistoryList.class));
                break;
        }
        return true;
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.prb_update) {
            if (reasonSpinner.getSelectedItem().toString().equals("Select reason")) {
                Toast.makeText(this, "Select Nature of change", Toast.LENGTH_SHORT).show();
                reasonSpinner.performClick();
            } else if (service_report_number.getText().toString().isEmpty()) {
                Toast.makeText(this, "Fill Service report number!", Toast.LENGTH_SHORT).show();
                service_report_number.requestFocus();
            } else if (prb_serial_value.getText().toString().isEmpty()) {
                Toast.makeText(this, "Fill PRB Serial number!", Toast.LENGTH_SHORT).show();
                prb_serial_value.requestFocus();
            } else if (prb_count_value.getText().toString().isEmpty()) {
                Toast.makeText(this, "Fill PRB Count!", Toast.LENGTH_SHORT).show();
                prb_count_value.requestFocus();
            } else {
                String existingPrb;
                try {
                    JSONObject config = CommonUtils.readConfig("Clicker Management");
                    existingPrb = config.getString("PrbId");
                } catch (Exception e) {
                    existingPrb = CommonUtils.getDeviceId(this);
                }
                EditText enteredPrb = findViewById(R.id.prb_serial_value);
                String enteredPrbValue = enteredPrb.getText().toString();
                int enteredCount = Integer.parseInt(prb_count_value.getText().toString());
                int existingCount = appPreferencesHelper.getPRBCount();
                if (existingPrb.equals(enteredPrbValue)) {
                    String reason = reasonSpinner.getSelectedItem().toString();
                    if (reason.equals("Button Change")) {
                        if (enteredCount == existingCount) {
                            showCountDialog(this);
                        } else {
                            checkHMDConnectionAndUpdateDB(enteredCount);
                        }
                    }
                } else {
                    if (enteredCount == existingCount) {
                        showCountDialog1(this);
                    } else {
                        checkHMDConnectionAndUpdateDB(enteredCount);
                    }
                }
            }
        }
    }

    void checkHMDConnectionAndUpdateDB(int count) {
        if (Store.communicationActive) {
            new InsertClickerInfo(AppDatabase.getAppDatabase(MainActivity.applicationContext), reasonSpinner.getSelectedItem().toString(), service_report_number.getText().toString(), prb_serial_value.getText().toString(), prb_count_value.getText().toString(), patInfoId -> patInfoId).execute();
            appPreferencesHelper.setPRBCount(count);
            Actions.doPrbUpdate();
            writeToConfigFile(prb_serial_value.getText().toString());
            Toast.makeText(this, "PRB Details Updated", Toast.LENGTH_SHORT).show();
            new WorkCreator(this).prbUpdatedByServiceWork();
            finish();
        } else {
            Toast.makeText(this, "Please check HMD connection", Toast.LENGTH_SHORT).show();
        }
    }

    void showCountDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.myDialog));
        builder.setTitle("Alert");
        builder.setMessage("When button is changed count cannot be equal to previous")
                .setCancelable(true)
                .setPositiveButton("Okay", (dialog, id) -> {
                    //Do Nothing
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    void showCountDialog1(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.myDialog));
        builder.setTitle("Alert");
        builder.setMessage("When PRB is changed count cannot be equal to previous")
                .setCancelable(true)
                .setNegativeButton("Later", (dialog, which) -> {
                    //Do Nothing
                })
                .setPositiveButton("Okay", (dialog, id) -> {
                    //Do Nothing
                });
        AlertDialog alert = builder.create();
        alert.show();
    }


    class InsertClickerInfo extends AsyncTask<Void, Void, Integer> {
        private AsyncDbInsertRecordTask delegate = null;
        private final AppDatabase mDb;
        private final String natureOfChange;
        private final String serviceNumber;
        private final String serialNumber;
        private final String prbCount;

        InsertClickerInfo(AppDatabase db, String nature, String servicenumber, String serailnumber, String prbcount, AsyncDbInsertRecordTask delegate) {
            this.delegate = delegate;
            mDb = db;
            natureOfChange = nature;
            serviceNumber = servicenumber;
            serialNumber = serailnumber;
            prbCount = prbcount;
        }

        @Override
        protected Integer doInBackground(final Void... params) {
            ClickerHistory clickerHistory = new ClickerHistory();
            clickerHistory.setNature(natureOfChange);
            clickerHistory.setSerial_number(serialNumber);
            clickerHistory.setService_no(serviceNumber);
            clickerHistory.setCount(Integer.parseInt(prbCount));
            clickerHistory.setCreateDate(new Date());
            return DatabaseInitializer.insertClickerInfo(mDb, clickerHistory);

        }

        @Override
        protected void onPostExecute(Integer s) {
            Log.d("InsertPatientInfo", "On Post Execute status " + s);
            delegate.onProcessFinish(s);
        }

    }

    public static void writeToConfigFile(String prbId) {
        Context context = MyApplication.getInstance();
        String path = context.getFilesDir().getAbsolutePath();
        Log.d("writeToConfigFile", "Called");
        try {
            File outputFile = new File(path, "config.json");
            if (!outputFile.exists())
                outputFile.createNewFile();
            JSONObject jsonObject = CommonUtils.readConfig("Clicker Management");
            jsonObject.put("PrbId", prbId);
            String config = jsonObject.toString();
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(outputFile));
            if (config != null) {
                outputStreamWriter.write(config);
            }
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e("IOException", " " + e.getMessage());
        } catch (JSONException e) {
            Log.e("JSonException", " " + e.getMessage());
        }
    }
}
