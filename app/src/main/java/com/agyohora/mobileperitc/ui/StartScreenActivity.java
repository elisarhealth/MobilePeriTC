package com.agyohora.mobileperitc.ui;

import android.Manifest;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.UserManager;
import android.provider.Settings;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.agyohora.mobileperitc.BuildConfig;
import com.agyohora.mobileperitc.DeviceAdminReceiver;
import com.agyohora.mobileperitc.R;
import com.agyohora.mobileperitc.data.preferences.AppPreferencesHelper;
import com.agyohora.mobileperitc.myapplication.MyApplication;
import com.agyohora.mobileperitc.utils.CommonUtils;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import static com.agyohora.mobileperitc.utils.AppConstants.DEVICE_PREF;
import static com.agyohora.mobileperitc.utils.CommonUtils.getBitmap;
import static com.agyohora.mobileperitc.utils.CommonUtils.getHotSpotId;

/**
 * Created by Invent on 22-1-18.
 * Entry screen to the app.
 * Here we redirect the screen according to the status of the setup process
 */

public class StartScreenActivity extends AppCompatActivity {

    private DevicePolicyManager mDevicePolicyManager;
    private ComponentName mAdminComponentName;
    int PERMISSION_ALL = 1;
    static final int MY_PERMISSIONS_MANAGE_WRITE_SETTINGS = 100;
    private boolean mSettingPermission = true;
    String[] PERMISSIONS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_CONTACTS
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_launcher_screen);
        mDevicePolicyManager = (DevicePolicyManager)
                getSystemService(Context.DEVICE_POLICY_SERVICE);
        mAdminComponentName = DeviceAdminReceiver.getComponentName(this);
        MyApplication.getInstance().set_HMD_CONNECTION_NEED(true);
        String copyright = "\u00a9 Elisar Life Sciences Private Limited. " + CommonUtils.currentYear();
        ((TextView) findViewById(R.id.copyRights)).setText(copyright);

        if (!hasPermissions(this, PERMISSIONS)) {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL);
        }
       // CommonUtils.sendSMS(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        showWritePermissionSettings();
        settingPermission();
        //CommonUtils.setInMemoryRoomDatabases();`
        /*DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        Log.e("Density",""+metrics.densityDpi);*/
        if (BuildConfig.ACTIVATE_KIOSK)
            CommonUtils.startKiosk(StartScreenActivity.this);

        if (mDevicePolicyManager.isDeviceOwnerApp(getPackageName())) {
            // mDevicePolicyManager.removeActiveAdmin();
            //registerReceiver(mInstallReceiver, new IntentFilter(Actions.ACTION_INSTALL_COMPLETE));
            setDefaultAdminPolicies(true);
        } else {
            ///registerReceiver(mInstallReceiver, new IntentFilter(Actions.ACTION_INSTALL_COMPLETE));
            Toast.makeText(getApplicationContext(),
                    R.string.not_device_owner, Toast.LENGTH_SHORT);
            ///.show();
        }
        // Log.e("TrackMe", " " + DebugDB.getAddressLog());
        sendUnSendReports();
    }

    public void onClickCallBack(View view) {
        switch (view.getId()) {
            case R.id.proceed_button:
                //Actions.initCommunication();
                AppPreferencesHelper appPreferencesHelper = new AppPreferencesHelper(this.getApplicationContext(), DEVICE_PREF);
                //appPreferencesHelper.setDatabaseVersion(3);
                //appPreferencesHelper.setDeviceConfigurationStatus(true);
                if (!appPreferencesHelper.getProductionSetUpStatus())
                    startBarCodeActivity();
                else if (!appPreferencesHelper.getUserSetUpStatus()) {
                    if (appPreferencesHelper.getProductionTestingStatus())
                        showRequirementDialog(this);
                    else
                        startActivity(new Intent(this, MainActivity.class));
                } else {
                    /*//Patch for congo
                    if (!appPreferencesHelper.getOrgSiteUpdateStatus()) {
                        CommonUtils.changeSiteOrg(this, appPreferencesHelper);
                    }*/
                    startActivity(new Intent(this, EssentialDataActivity.class));
                    /*if (!appPreferencesHelper.getLoginStatus()) {
                        startActivity(new Intent(this, LoginActivity.class));
                    } else {
                        startMainActivity();
                    }*/
                }
                break;
            default:
                Log.d("StartScreenActivity", "No Method Found");
        }

    }

    void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        getApplicationContext().startActivity(intent);
    }

    private boolean showWritePermissionSettings() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
            if (!Settings.System.canWrite(this)) {
                Log.v("DANG", " " + !Settings.System.canWrite(this));
                Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + this.getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                this.startActivity(intent);
                return false;
            }
        }
        return true; //Permission already given
    }

    private void settingPermission() {
        mSettingPermission = true;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(getApplicationContext())) {
                mSettingPermission = false;
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, MY_PERMISSIONS_MANAGE_WRITE_SETTINGS);
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RESULT_FIRST_USER) {
            startBarCodeActivity();
        } else if (requestCode == MY_PERMISSIONS_MANAGE_WRITE_SETTINGS) {
            if (resultCode == RESULT_OK) {
                mSettingPermission = true;
                startActivity(new Intent(android.provider.Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES, Uri.parse("package:your.application.package")));
            } else {
                settingPermission();
            }
        }
        try {
            IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (result != null) {
                if (!(result.getContents() == null)) {
                    String path = result.getBarcodeImagePath();
                    Intent intent = new Intent(this, QrResultActivity.class);
                    JSONObject retrievedData = new JSONObject(result.getContents());
                    AppPreferencesHelper appPreferencesHelper = new AppPreferencesHelper(this, DEVICE_PREF);
                    if (isQrContainsValidParams(appPreferencesHelper, retrievedData)) {
                        String devId = retrievedData.getString("DeviceId").trim();
                        String org = appPreferencesHelper.getProductionSetUpStatus() ? retrievedData.getString("OrganizationId").trim() : "";
                        String msg = appPreferencesHelper.getProductionSetUpStatus() ? "Check device and organization" : "Check device";
                        //String siteId = retrievedData.getString("SiteId").trim();
                        Log.e("QRSCAN", "Scanned ID " + devId + " id from config " + getHotSpotId());
                        if (devId.equalsIgnoreCase(getHotSpotId())) {

                            Bundle bundle = createBundle(path, msg +
                                    " information", true, retrievedData);
                            intent.putExtras(bundle);
                            startActivityForResult(intent, RESULT_FIRST_USER);

                        } else {
                            Bundle bundle = createBundle(path, "Device or Org Id scanned does not match.\n" +
                                            "Please contact customer care\n",
                                    false, retrievedData);
                            intent.putExtras(bundle);
                            startActivityForResult(intent, RESULT_FIRST_USER);
                        }

                    } else {
                        invalidQrDialog("QR Code sent by us seems broken. contact us");
                        Bundle bundle = createBundle(path, "QR code not recognized.\n" +
                                "• Please re-scan using the QR code sent to\n" +
                                "your registered ID", false, retrievedData);
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }
                } else {
                    invalidQrDialog("The QR Code you have scanned is not been sent by us.");
                }
            }

        } catch (Exception e) {
            Log.e("Exception", " " + e.getMessage());
            invalidQrDialog("The QR Code u have scanned is not been sent by us.");
        }
    }

    public static boolean hasPermissions(Context context, String... permissions) {
        if (context != null && permissions != null) {
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isQrContainsValidParams(AppPreferencesHelper appPreferencesHelper, JSONObject retrievedData) {

        if (!appPreferencesHelper.getProductionSetUpStatus()) {
            if (retrievedData.has("DeviceId"))
                return !retrievedData.isNull("DeviceId");
        } else if (!appPreferencesHelper.getUserSetUpStatus()) {
            if (retrievedData.has("DeviceId") && retrievedData.has("OrganizationId") && retrievedData.has("SiteId")
                    && retrievedData.has("TcId") && retrievedData.has("TcSwId") && retrievedData.has("TcSwOs")
                    && retrievedData.has("TcHwModelId") && retrievedData.has("HmdSwId") && retrievedData.has("HmdId")
                    && retrievedData.has("HmdSwOs") && retrievedData.has("HmdWoWNormativeDataId") && retrievedData.has("HmdFirmwareId")
                    && retrievedData.has("HmdLogScale") && retrievedData.has("HmdHwModelId") && retrievedData.has("HmdPhoneModelId")
                    && retrievedData.has("HmdPhoneDisplayId"))

                return !retrievedData.isNull("DeviceId") && !retrievedData.isNull("OrganizationId") && !retrievedData.isNull("SiteId")
                        && !retrievedData.isNull("TcId") && !retrievedData.isNull("TcSwId") && !retrievedData.isNull("TcSwOs")
                        && !retrievedData.isNull("TcHwModelId") && !retrievedData.isNull("HmdSwId") && !retrievedData.isNull("HmdId")
                        && !retrievedData.isNull("HmdSwOs") && !retrievedData.isNull("HmdWoWNormativeDataId") && !retrievedData.isNull("HmdFirmwareId")
                        && !retrievedData.isNull("HmdLogScale") && !retrievedData.isNull("HmdHwModelId") && !retrievedData.isNull("HmdPhoneModelId")
                        && !retrievedData.isNull("HmdPhoneDisplayId");
        }
        return false;
    }


    private void invalidQrDialog(String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.myDialog));
        builder.setTitle("Error!");
        builder.setMessage(message)
                .setCancelable(true)
                .setPositiveButton("Retry", (dialog, id) -> startBarCodeActivity())
                .setNegativeButton("Cancel", (dialog, id) -> {
                    //do things
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void startBarCodeActivity() {
        // new TriggerTheUpgrade().execute();
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setOrientationLocked(false);
        integrator.setBarcodeImageEnabled(true);
        integrator.setBeepEnabled(true);
        integrator.setPrompt("Focus the QR code inside this box");
        integrator.setCaptureActivity(BarCodeCaptureActivity.class);
        integrator.initiateScan();
    }

    private Bundle createBundle(String path, String message, boolean proceed, JSONObject retrievedData) {
        try {
            Bundle bundle = new Bundle();

            bundle.putString("path", path);
            bundle.putString("message", message);
            bundle.putBoolean("proceed", proceed);
            bundle.putString("retrievedData", retrievedData.toString());
            bundle.putString("DeviceId", retrievedData.getString("DeviceId"));
            bundle.putString("OrganizationId", retrievedData.getString("OrganizationId"));
            bundle.putString("SiteId", retrievedData.getString("SiteId"));
            bundle.putString("TcId", retrievedData.getString("TcId"));
            bundle.putString("TcSwId", retrievedData.getString("TcSwId"));
            bundle.putString("TcSwOs", retrievedData.getString("TcSwOs"));
            bundle.putString("TcHwModelId", retrievedData.getString("TcHwModelId"));
            bundle.putString("HmdSwId", retrievedData.getString("HmdSwId"));
            bundle.putString("HmdId", retrievedData.getString("HmdId"));
            bundle.putString("HmdSwOs", retrievedData.getString("HmdSwOs"));
            bundle.putString("HmdWoWNormativeDataId", retrievedData.getString("HmdWoWNormativeDataId"));
            bundle.putString("HmdFirmwareId", retrievedData.getString("HmdFirmwareId"));
            bundle.putString("HmdFirmwareId", retrievedData.getString("HmdFirmwareId"));
            bundle.putString("HmdPhoneModelId", retrievedData.getString("HmdPhoneModelId"));
            bundle.putString("HmdHwModelId", retrievedData.getString("HmdHwModelId"));
            bundle.putString("HmdPhoneDisplayId", retrievedData.getString("HmdPhoneDisplayId"));
            return bundle;
        } catch (JSONException e) {
            Log.e("CreateBundle", " " + e.getMessage());
            return null;
        }
    }

    private void setDefaultAdminPolicies(boolean active) {
        // set user restrictions

        setUserRestriction(UserManager.DISALLOW_ADD_USER, active);
        setUserRestriction(UserManager.DISALLOW_MOUNT_PHYSICAL_MEDIA, active);
        setUserRestriction(UserManager.DISALLOW_ADJUST_VOLUME, active);
        //setUserRestriction(UserManager.DISALLOW_FACTORY_RESET, false);

        /*setUserRestriction(UserManager.DISALLOW_INSTALL_APPS, active);

        mDevicePolicyManager.setStatusBarDisabled(mAdminComponentName, false);
        setUserRestriction(UserManager.DISALLOW_SAFE_BOOT, active);
        setUserRestriction(UserManager.DISALLOW_FACTORY_RESET, active);

        //disable keyguard and status bar
        mDevicePolicyManager.setKeyguardDisabled(mAdminComponentName, active);
        mDevicePolicyManager.setStatusBarDisabled(mAdminComponentName, active);


        // enable STAY_ON_WHILE_PLUGGED_IN
        enableStayOnWhilePluggedIn(active);

        // set system update policy
        if (active){
            mDevicePolicyManager.setSystemUpdatePolicy(mAdminComponentName,
                    SystemUpdatePolicy.createWindowedInstallPolicy(60, 120));
        } else {
            mDevicePolicyManager.setSystemUpdatePolicy(mAdminComponentName,
                    null);
        }
*/
        // set this Activity as a lock task package

        mDevicePolicyManager.setLockTaskPackages(mAdminComponentName,
                active ? new String[]{getPackageName()} : new String[]{});

        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_MAIN);
        intentFilter.addCategory(Intent.CATEGORY_HOME);
        intentFilter.addCategory(Intent.CATEGORY_DEFAULT);

        if (active) {
            // set Cosu activity as home intent receiver so that it is started on reboot
            mDevicePolicyManager.addPersistentPreferredActivity(
                    mAdminComponentName, intentFilter, new ComponentName(
                            getPackageName(), MainActivity.class.getName()));
        } else {
            mDevicePolicyManager.clearPackagePersistentPreferredActivities(
                    mAdminComponentName, getPackageName());
        }
        //mDevicePolicyManager.setApplicationRestrictions(DeviceAdminReceiver,);
        //mDevicePolicyManager.setApplicationHidden(DeviceAdminReceiver.getComponentName(this),"com.android.vending",false);
    }

    private void setUserRestriction(String restriction, boolean disallow) {
        if (disallow) {
            mDevicePolicyManager.addUserRestriction(mAdminComponentName,
                    restriction);
        } else {
            mDevicePolicyManager.clearUserRestriction(mAdminComponentName,
                    restriction);
        }
    }

    void showRequirementDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.myDialog));
        builder.setTitle("Setup Process");
        builder.setMessage("The following setup process needs the QR code sent with this product and HMD needs to be switched on. Please make sure of that.")
                .setCancelable(true)
                .setNegativeButton("Later", (dialog, which) -> {
                    //Do Nothing
                })
                .setPositiveButton("Continue", (dialog, id) -> startBarCodeActivity());
        AlertDialog alert = builder.create();
        alert.show();
    }

    private void generateAssets() {
        try {
            int[] assets = {R.drawable.tunnel_vision_75, R.drawable.tunnel_vision_150, R.drawable.tunnel_vision_225, R.drawable.tunnel_vision_300, R.drawable.tunnel_vision_375};
            int[] radius = {75, 150, 225, 300, 375};
            for (int i = 0; i < assets.length; i++) {
                Bitmap grey1 = getBitmap(this, assets[i]);
                CommonUtils.saveOverlayBitmap(this, grey1, radius[i]);
                //Thread.sleep(5000);
            }
        } catch (Exception e) {
            Log.e("generateAssets", e.getMessage());
        }
    }

    void sendUnSendReports() {
        if (CommonUtils.haveNetworkConnection(this))
            FirebaseCrashlytics.getInstance().sendUnsentReports();
    }

   /* private BroadcastReceiver mInstallReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (!Actions.ACTION_INSTALL_COMPLETE.equals(intent.getAction())) {
                Log.d("Action", " fails");
                return;
            }
            Log.d("we are", "here");
            int result = intent.getIntExtra(PackageInstaller.EXTRA_STATUS,
                    PackageInstaller.STATUS_FAILURE);
            String packageName = intent.getStringExtra(PackageInstaller.EXTRA_PACKAGE_NAME);
            Log.d(MainActivity.TAG, "PackageInstallerCallback: result=" + result
                    + " packageName=" + packageName);
            switch (result) {
                case PackageInstaller.STATUS_PENDING_USER_ACTION: {
                    // this should not happen in M, but will happen in L and L-MR1
                    startActivity((Intent) intent.getParcelableExtra(Intent.EXTRA_INTENT));
                }
                break;ta
                case PackageInstaller.STATUS_SUCCESS: {
                    *//*mHandler.sendMessage(mHandler.obtainMessage(CosuUtils.MSG_INSTALL_COMPLETE,
                            packageName));*//*
                    Log.e(MainActivity.TAG, "Install success.");
                }
                break;
                default: {
                    Log.e(MainActivity.TAG, "Install failed.");
                    return;
                }
            }
        }
    };*/
}
