package com.agyohora.mobileperitc.ui;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.agyohora.mobileperitc.R;
import com.agyohora.mobileperitc.asynctasks.CountOfNotMigratedData;
import com.agyohora.mobileperitc.asynctasks.FillTheVFI;
import com.agyohora.mobileperitc.data.preferences.AppPreferencesHelper;

import static com.agyohora.mobileperitc.utils.AppConstants.DEVICE_PREF;

public class EssentialDataActivity extends AppCompatActivity {

    AppPreferencesHelper appPreferencesHelper;
    public static Activity essentialDataActivityReference;
    String vfi_work_finished_action = "VFI_WORK_FINISHED";


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.essential_data_activity);
        essentialDataActivityReference = EssentialDataActivity.this;
        appPreferencesHelper = new AppPreferencesHelper(this, DEVICE_PREF);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(vfi_work_finished_action);
        registerReceiver(VFI_WORK_BroadCastReceiver, intentFilter);
        int dbVersion = appPreferencesHelper.getDatabaseVersion();
        Log.e("TrackMe", " dbVersion " + dbVersion);
        new CountOfNotMigratedData(bool -> {
            if (bool) {
                new FillTheVFI(patientTestResults -> {
                }, EssentialDataActivity.essentialDataActivityReference).execute();
            } else {
                Intent intent = new Intent("VFI_WORK_FINISHED");
                sendBroadcast(intent);
            }
            return bool;
        }, this).execute();
    }

    @Override
    protected void onResume() {
        super.onResume();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(vfi_work_finished_action);
        registerReceiver(VFI_WORK_BroadCastReceiver, intentFilter);
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e("TrackMe", " VFI_WORK_BroadCastReceiver de-registered");
        unregisterReceiver(VFI_WORK_BroadCastReceiver);
    }

    private BroadcastReceiver VFI_WORK_BroadCastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e("TrackMe", " VFI_WORK_BroadCastReceiver called");
            final String action = intent.getAction();
            if (action != null) {
                if (action.equals(vfi_work_finished_action)) {
                    Log.e("TrackMe", " VFI_WORK_BroadCastReceiver " + action);
                    Log.e("TrackMe", " getLoginStatus " + appPreferencesHelper.getLoginStatus());
                    startActivity(new Intent(EssentialDataActivity.this, LoginActivity.class));
                    finish();
                }
            }
        }
    };


    void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplicationContext().startActivity(intent);
    }
}
