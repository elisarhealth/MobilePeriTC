package com.agyohora.mobileperitc.ui;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.agyohora.mobileperitc.R;
import com.agyohora.mobileperitc.data.preferences.AppPreferencesHelper;
import com.agyohora.mobileperitc.utils.CommonUtils;

import org.json.JSONException;
import org.json.JSONObject;

import static com.agyohora.mobileperitc.store.Store.activeView_number;
import static com.agyohora.mobileperitc.utils.AppConstants.DEVICE_PREF;


/**
 * Created by Invent on 23-1-18.
 * In QrResultActivity the data of scanned QR code will be validated by comparing the config.json
 */

public class QrResultActivity extends AppCompatActivity {
    private String orgName;
    private String devId;
    private String siteId;
    private String configString;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.qr_result);
        getSupportActionBar().setElevation(0);
    }

    @Override
    protected void onStart() {
        super.onStart();
        TextView hmd_id = findViewById(R.id.hmd_id);
        TextView org_id = findViewById(R.id.org_id);
        TextView org_id_text = findViewById(R.id.org_id_text);
        Button proceed = findViewById(R.id.proceed_button);
        Button progressStepOne = findViewById(R.id.progressBar1);
        TextView info = findViewById(R.id.info);
        ImageView qr = findViewById(R.id.qr_bitmap);
        Bundle bundle = getIntent().getExtras();
        AppPreferencesHelper appPreferencesHelper = new AppPreferencesHelper(this, DEVICE_PREF);
        try {
            JSONObject config = new JSONObject(bundle.getString("retrievedData"));
            configString = bundle.getString("retrievedData");
            Log.e("config", config.toString());
        } catch (JSONException e) {
            Log.e("retrievedData", e.getMessage());
        }
        orgName = bundle.getString("OrganizationId");
        devId = bundle.getString("DeviceId");
        siteId = bundle.getString("SiteId");
        String message = bundle.getString("message");
        String bitmapPath = bundle.getString("path");
        boolean proceedVisibility = bundle.getBoolean("proceed");
        Log.d("Visibility ", " " + proceedVisibility);
        hmd_id.setText(devId);
        if (appPreferencesHelper.getProductionSetUpStatus()) {
            org_id.setText(orgName);
            org_id_text.setVisibility(View.VISIBLE);
        } else {
            org_id.setVisibility(View.INVISIBLE);
            org_id_text.setVisibility(View.INVISIBLE);
        }

        info.setText(message);
        if (proceedVisibility) {
            proceed.setVisibility(View.VISIBLE);
            progressStepOne.setBackground(getDrawable(R.drawable.progress_bar_blue));
        } else {
            proceed.setVisibility(View.INVISIBLE);
            info.setTextColor(Color.RED);
        }
        proceed.setVisibility(proceedVisibility ? View.VISIBLE : View.INVISIBLE);
        qr.setImageURI(null);
        qr.setImageURI(Uri.parse(bitmapPath));
        CommonUtils.switchOffHotSpot(this);
    }

    public void onClickCallBackQrResult(View view) {
        switch (view.getId()) {
            case R.id.refresh_button:
                Intent intent = new Intent();
                //intent.putExtra("keyName", "");
                setResult(RESULT_FIRST_USER, intent);
                finish();
                //finishActivity(RESULT_FIRST_USER);
                break;
            case R.id.proceed_button:
                AppPreferencesHelper appPreferencesHelper = new AppPreferencesHelper(this, DEVICE_PREF);
                if (!appPreferencesHelper.getProductionSetUpStatus()) {
                    appPreferencesHelper.setDeviceId(devId);
                    //appPreferencesHelper.setOrgId(orgName);
                } else if (!appPreferencesHelper.getUserSetUpStatus()) {
                    appPreferencesHelper.setOrgId(orgName);
                    appPreferencesHelper.setOrgId(siteId);
                    CommonUtils.writeToConfigFile(devId, orgName, siteId, configString);
                }
                if (!appPreferencesHelper.getProductionSetUpStatus() && Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1)
                    activeView_number = R.layout.activity_setup_hotspot;
                else
                    activeView_number = R.layout.hmd_sync_check_activity;
                startActivity(new Intent(this, MainActivity.class));
                /*if (appPreferencesHelper.getProductionSetUpStatus() && appPreferencesHelper.getUserSetUpStatus()) {
                    CommonUtils.writeToConfigFile(devId,orgName,siteId);
                    activeView_number = R.layout.activity_home_screen;
                } else {
                    appPreferencesHelper.setDeviceId(devId);
                    activeView_number = R.layout.hmd_sync_check_activity;
                }
                startActivity(new Intent(this, MainActivity.class));*/
                break;
            case R.id.having_trouble_button:
                CommonUtils.showContactDialog(this);
                break;
            default:
                Log.d("QrResultActivity", "No Method Found");
        }

    }
}
