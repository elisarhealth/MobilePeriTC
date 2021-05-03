package com.agyohora.mobileperitc.ui;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.agyohora.mobileperitc.BuildConfig;
import com.agyohora.mobileperitc.R;
import com.agyohora.mobileperitc.actions.Actions;
import com.agyohora.mobileperitc.data.database.AppDatabase;
import com.agyohora.mobileperitc.data.database.entity.PatientTestResult;
import com.agyohora.mobileperitc.data.preferences.AppPreferencesHelper;
import com.agyohora.mobileperitc.myapplication.MyApplication;
import com.agyohora.mobileperitc.store.Store;
import com.agyohora.mobileperitc.store.StoreTransmitter;
import com.agyohora.mobileperitc.utils.CommonUtils;
import com.agyohora.mobileperitc.utils.Constants;
import com.agyohora.mobileperitc.worksheduler.workcreator.WorkCreator;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.List;

import static com.agyohora.mobileperitc.data.database.DatabaseInitializer.getUnSyncedData;
import static com.agyohora.mobileperitc.store.Store.isHotSpotOn;
import static com.agyohora.mobileperitc.utils.AppConstants.DEVICE_PREF;
import static com.agyohora.mobileperitc.utils.CommonUtils.startKiosk;
import static com.agyohora.mobileperitc.utils.CommonUtils.stopKiosk;

/**
 * Created by Invent
 * Activity to show HMD details
 */

public class HmdDetailsActivity extends AppCompatActivity implements View.OnClickListener {

    AppPreferencesHelper preferencesHelper;
    private Button sync_details_now;
    Button production_test_done;
    private ImageView note;
    private View production_test_done_bar;

    private static void turnOffOrRebootHMD(final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(activity, R.style.myDialog));
        builder.setTitle("Action alert")
                .setMessage("Are you sure?")
                .setCancelable(false)
                .setPositiveButton("TurnOff", (dialog, which) -> {
                    Actions.beginShutDown();
                    Store.newTestVisibility = false;
                    activity.finish();
                })
                .setNegativeButton("Reboot", (dialog, which) -> {
                    Actions.beginReboot();
                    Store.newTestVisibility = false;
                    activity.finish();
                }).setNeutralButton("Cancel", (dialog, which) -> {
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_hmd_details_activity);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setElevation(0);
        TextView hmd_id = findViewById(R.id.hmd_id);
        TextView org_id = findViewById(R.id.org_id);
        sync_details_now = findViewById(R.id.sync_details_now);
        production_test_done_bar = findViewById(R.id.production_test_done_bar);
        production_test_done = findViewById(R.id.production_test_done);
        Button rebootORpoweroff = findViewById(R.id.rebootORpoweroff);
        note = findViewById(R.id.note);
        preferencesHelper = new AppPreferencesHelper(this, DEVICE_PREF);
        if (CommonUtils.isUserSetUpFinished(this)) {
            try {
                JSONObject config = CommonUtils.readConfig("HMD Details Activity");
                if (config != null) {
                    hmd_id.setText(config.getString("DeviceId"));
                    org_id.setText(config.getString("OrganizationId"));
                }
            } catch (JSONException e) {
                Log.e("HmdDetailsActivity", e.getMessage());
            }
        } else {
            hmd_id.setText(CommonUtils.getSavedDeviceId(this));
            org_id.setText(CommonUtils.getSavedOrgId(this));
        }
        if (!preferencesHelper.getProductionTestingStatus()) {
            production_test_done_bar.setVisibility(View.VISIBLE);
            production_test_done.setVisibility(View.VISIBLE);
        }
        production_test_done.setOnClickListener(this);
        sync_details_now.setOnClickListener(this);
        rebootORpoweroff.setOnClickListener(this);
        new UpdateVisibilityAsyncTask(this).execute();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (BuildConfig.ACTIVATE_KIOSK)
            startKiosk(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.hmd_settings_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Log.e("onPrepareOptionsMenu", " isHotSpotOn " + isHotSpotOn);
        MenuItem toggleHotSpot = menu.findItem(R.id.toggleHotSpot);
        if (isHotSpotOn) toggleHotSpot.setIcon(R.drawable.ic_wifi_tethering_on);
        else toggleHotSpot.setIcon(R.drawable.ic_wifi_tethering_off);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.d("onOptionsItemSelected", "Called");
        if (item.getItemId() == R.id.toggleHotSpot) {
            Log.e("onPrepareOptionsMenu", " stat " + isHotSpotOn);
            if (isHotSpotOn) {
                item.setIcon(R.drawable.ic_wifi_tethering_off);
                Store.newTestVisibility = false;
                isHotSpotOn = false;
                Bundle bundle = new Bundle();
                bundle.putString("data", "NA");
                isHotSpotOn = false;
                StoreTransmitter.doCommFunction(StoreTransmitter.COMM_FUNCTION_STOP, bundle);
                MyApplication.getInstance().set_HMD_CONNECTION_NEED(false);
                MyApplication.getInstance().set_HMD_CONNECTED(false);
                showHotSpotStatusDialog(false);
            } else {
                item.setIcon(R.drawable.ic_wifi_tethering_on);
                MyApplication.getInstance().set_HMD_CONNECTION_NEED(true);
                MyApplication.getInstance().set_HMD_CONNECTED(true);
                isHotSpotOn = true;
                Actions.initCommunication();
                showHotSpotStatusDialog(true);
            }
            return true;
        }
        return false;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.sync_details_now:
                if (CommonUtils.haveNetworkConnection(this)) {
                    CommonUtils.showToasty(this, "Working on sync in background...", false, 'I');
                    note.setVisibility(View.INVISIBLE);
                    sync_details_now.setVisibility(View.INVISIBLE);
                    new WorkCreator(this).runImmediateSyncWork();
                    new WorkCreator(this).runImmediateSentNumberOfTestWork();
                } else {
                    stopKiosk(this);
                    CommonUtils.initiateNetworkOptions(this, this, "NA");
                }
                break;
            case R.id.rebootORpoweroff:
                if (MyApplication.getInstance().is_HMD_CONNECTED())
                    turnOffOrRebootHMD(HmdDetailsActivity.this);
                else
                    CommonUtils.showHMDDisconnectedDialog(this);
                break;
            case R.id.production_test_done:
                boolean vectorFileStatus = preferencesHelper.getVectorFileCopiedStatus();
                boolean configFileStatus = preferencesHelper.getConfigFileCopiedStatus();
                String vectorFileName = "vector" + CommonUtils.getHotSpotId() + ".json";
                String vectorInputPath = Environment.getExternalStorageDirectory() + Constants.VECTOR_FILE_PATH_ROOT;
                String vectorOutputPath = getFilesDir().getAbsolutePath();
                String configFileName = "config.json";
                String confiInputPath = Environment.getExternalStorageDirectory() + Constants.CONFIG_FILE_PATH_ROOT;
                String configOutputPath = getFilesDir().getAbsolutePath();
                boolean isVectorFileCopied = CommonUtils.copyFile(vectorInputPath, vectorFileName, vectorOutputPath);
                boolean isConfigFileCopied = CommonUtils.copyFile(confiInputPath, configFileName, configOutputPath);
                if (!vectorFileStatus) {
                    if (isConfigFileCopied) {
                        preferencesHelper.putConfigFileCopiedStatus(true);
                        CommonUtils.showToasty(this, "Config File Copied Successfully", true, 'I');
                    } else {
                        CommonUtils.showToasty(this, "Config File Not Copied", true, 'E');
                    }
                }
                if (!configFileStatus) {
                    if (isVectorFileCopied) {
                        if (CommonUtils.readVector() != null) {
                            if (CommonUtils.deleteFile(vectorInputPath, vectorFileName)) {
                                preferencesHelper.putVectorFileCopiedStatus(true);
                                CommonUtils.showToasty(this, "Vector File Copied Successfully", true, 'I');
                            } else {
                                CommonUtils.showToasty(this, "Vector File in SD card Not deleted Successfully", true, 'W');
                            }
                        } else {
                            CommonUtils.showToasty(this, "Vector File Copied Successfully and error in reading ", true, 'E');
                        }
                    } else {
                        CommonUtils.showToasty(this, "Vector File Not Copied", true, 'E');
                    }
                }

                vectorFileStatus = preferencesHelper.getVectorFileCopiedStatus();
                configFileStatus = preferencesHelper.getConfigFileCopiedStatus();

                if (vectorFileStatus && configFileStatus) {
                    production_test_done_bar.setVisibility(View.INVISIBLE);
                    production_test_done.setVisibility(View.INVISIBLE);
                    preferencesHelper.setProductionTestingStatus(true);
                }
                break;
        }
    }

    private static class UpdateVisibilityAsyncTask extends AsyncTask<Void, Void, Integer> {

        private WeakReference<HmdDetailsActivity> weakActivity;

        UpdateVisibilityAsyncTask(HmdDetailsActivity activity) {
            weakActivity = new WeakReference<>(activity);
        }

        @Override
        protected Integer doInBackground(Void... params) {
            List<PatientTestResult> patientTestResults = getUnSyncedData(AppDatabase.getAppDatabase(MyApplication.getInstance()));
            return patientTestResults.size();
        }

        @Override
        protected void onPostExecute(Integer unsyncedCount) {
            HmdDetailsActivity activity = weakActivity.get();
            if (activity == null) {
                return;
            }

            if (unsyncedCount > 0) {
                activity.note.setVisibility(View.VISIBLE);
                activity.sync_details_now.setVisibility(View.VISIBLE);
            } else {
                activity.note.setVisibility(View.INVISIBLE);
                activity.sync_details_now.setVisibility(View.INVISIBLE);
            }
        }
    }

    void showHotSpotStatusDialog(boolean status) {
        Context context = new ContextThemeWrapper(HmdDetailsActivity.this, R.style.AppTheme2);
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(context);
        if (status)
            builder.setView(R.layout.dialog_initiating_hot_spot);
        else
            builder.setView(R.layout.dialog_turn_off_hotspot);
        builder.setCancelable(false);
        androidx.appcompat.app.AlertDialog dialog = builder.show();

        new Handler().postDelayed(() -> {
            if (dialog.isShowing())
                dialog.dismiss();
        }, 5000);
    }
}
