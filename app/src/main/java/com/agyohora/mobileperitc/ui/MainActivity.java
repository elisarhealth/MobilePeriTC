package com.agyohora.mobileperitc.ui;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.OnAccountsUpdateListener;
import android.animation.Animator;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewStub;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;

import com.agyohora.mobileperitc.BuildConfig;
import com.agyohora.mobileperitc.DeviceAdminReceiver;
import com.agyohora.mobileperitc.R;
import com.agyohora.mobileperitc.actions.Actions;
import com.agyohora.mobileperitc.asynctasks.InsertRecordIntoDb;
import com.agyohora.mobileperitc.asynctasks.LoadGreyScale;
import com.agyohora.mobileperitc.asynctasks.LoadMappedIconView;
import com.agyohora.mobileperitc.asynctasks.LoadMappedLargeText;
import com.agyohora.mobileperitc.asynctasks.LoadMappedView;
import com.agyohora.mobileperitc.asynctasks.LoadScreeningGraphs;
import com.agyohora.mobileperitc.asynctasks.MergeDatabase;
import com.agyohora.mobileperitc.asynctasks.SearchPatientBasicDetails;
import com.agyohora.mobileperitc.communication.CommunicationReceiver;
import com.agyohora.mobileperitc.communication.CommunicationService;
import com.agyohora.mobileperitc.data.database.AppDatabase;
import com.agyohora.mobileperitc.data.database.entity.PatientTestResult;
import com.agyohora.mobileperitc.data.network.ApiEndPoint;
import com.agyohora.mobileperitc.data.preferences.AppPreferencesHelper;
import com.agyohora.mobileperitc.exceptions.AccessoryStatusException;
import com.agyohora.mobileperitc.exceptions.PrbException;
import com.agyohora.mobileperitc.interfaces.AsyncDbInsertRecordTask;
import com.agyohora.mobileperitc.interfaces.AsyncDbTaskString;
import com.agyohora.mobileperitc.myapplication.MyApplication;
import com.agyohora.mobileperitc.store.Store;
import com.agyohora.mobileperitc.store.StoreTransmitter;
import com.agyohora.mobileperitc.utils.ClickerStatus;
import com.agyohora.mobileperitc.utils.CommonUtils;
import com.agyohora.mobileperitc.utils.Constants;
import com.agyohora.mobileperitc.utils.DisplayStatus;
import com.agyohora.mobileperitc.utils.ElectronicInterfaceStatus;
import com.agyohora.mobileperitc.utils.EyeTrackingStatus;
import com.agyohora.mobileperitc.utils.PhoneNumberTextWatcher;
import com.agyohora.mobileperitc.worksheduler.workcreator.WorkCreator;
import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.OkHttpResponseListener;
import com.bumptech.glide.Glide;
import com.downloader.Error;
import com.downloader.OnDownloadListener;
import com.downloader.PRDownloader;
import com.downloader.Status;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.lang.ref.WeakReference;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Response;

import static android.view.View.GONE;
import static com.agyohora.mobileperitc.actions.Actions.backToHomeScreen;
import static com.agyohora.mobileperitc.actions.Actions.goHomeImmediately;
import static com.agyohora.mobileperitc.communication.WifiCommunicationManager.isHotspotOn;
import static com.agyohora.mobileperitc.data.database.DatabaseInitializer.getUnSyncedData;
import static com.agyohora.mobileperitc.store.Store.accessoriesChecked;
import static com.agyohora.mobileperitc.store.Store.activeView_number;
import static com.agyohora.mobileperitc.store.Store.clickerStatus;
import static com.agyohora.mobileperitc.store.Store.communicationActive;
import static com.agyohora.mobileperitc.store.Store.displayStatus;
import static com.agyohora.mobileperitc.store.Store.eyeTrackingStatus;
import static com.agyohora.mobileperitc.store.Store.getState;
import static com.agyohora.mobileperitc.store.Store.isCommInitializationOver;
import static com.agyohora.mobileperitc.store.Store.isFoveaProceeded;
import static com.agyohora.mobileperitc.store.Store.isHotSpotOn;
import static com.agyohora.mobileperitc.store.Store.newTestVisibility;
import static com.agyohora.mobileperitc.store.Store.previousView_number;
import static com.agyohora.mobileperitc.store.Store.showTip;
import static com.agyohora.mobileperitc.store.Store.tipId;
import static com.agyohora.mobileperitc.utils.AppConstants.DEVICE_PREF;
import static com.agyohora.mobileperitc.utils.AppConstants.PREF_NAME;
import static com.agyohora.mobileperitc.utils.AppConstants.RESULT_PREF;
import static com.agyohora.mobileperitc.utils.CommonUtils.abortTestConfirmationDialog;
import static com.agyohora.mobileperitc.utils.CommonUtils.deleteReportConfirmationDialog;
import static com.agyohora.mobileperitc.utils.CommonUtils.getAge;
import static com.agyohora.mobileperitc.utils.CommonUtils.nullCheck;
import static com.agyohora.mobileperitc.utils.CommonUtils.showBigToast;
import static com.agyohora.mobileperitc.utils.CommonUtils.showToasty;
import static com.agyohora.mobileperitc.utils.CommonUtils.stopKiosk;
import static com.agyohora.mobileperitc.utils.Constants.DATABASE_RESTORE_LOGS_FOLDER;
import static com.agyohora.mobileperitc.utils.HmdBatteryStatus.BATTERY_LOW_KEEP_CHARGING;
import static com.agyohora.mobileperitc.utils.HmdBatteryStatus.BATTERY_LOW_PLEASE_CHARGE_HMD;
import static com.agyohora.mobileperitc.utils.HmdBatteryStatus.BATTERY_OK;
import static com.agyohora.mobileperitc.utils.HmdBatteryStatus.PLUG_OUT_CHARGE_OK;
import static com.agyohora.mobileperitc.utils.HmdBatteryStatus.UNDEFINED;

/**
 * Edited by Invent
 * Activity to maintain the full flow
 */


@SuppressWarnings("unchecked")
public class MainActivity extends AppCompatActivity implements OnAccountsUpdateListener, AdapterView.OnItemSelectedListener {

    public static final String TAG = MainActivity.class.getName();
    public static Context applicationContext;
    public static Activity dialogReference;
    public static Thread DT_Image_Update_Thread;
    public static Thread Cl_Image_Update_Thread;
    public static Thread ipd_Image_Update_Thread;
    //variables that ensure threads to update calibration occur only once per test sequence
    public static boolean calibrationImageViewLinked, ipdImageViewLinked, testImageViewLinked = false;
    public static Menu menu;
    public static long timeWhenStopped = 0;
    public static Chronometer chronometerForDurationCalculation;
    public static boolean isChronometerRunning = false;
    public static boolean hmdSyncHandlerRunning = false;
    public static int calibrationRetryCounter, beginTestRetryCounter;
    private static FloatingActionButton fab;
    private static Thread hmd_Init_Thread;
    private static int activeViewID;
    private static int prevViewID = 0;
    private static Bundle activeState;
    private static ArrayAdapter<String> patternAdapter, strategyAdapter;
    private static long elapsedTime;
    Button sync_details_now;
    private ImageView note;
    View production_test_done_bar;
    Button production_test_done;
    Status tcDownloadStatus, hmdDownloadStatus, databaseDownloadStatus;
    Handler h = new Handler();
    boolean isUpdateCalled = false;
    boolean[] isTcUpdateSuccess = {false};
    public static boolean isHmdNotConnectedYet, isUpdateNotDoneYet, isHMDConnected;
    private static Handler updateHandler = new Handler(Looper.getMainLooper());
    private final Runnable hmdSyncCheck = new Runnable() {
        public void run() {
            try {
                if (isHotspotOn(MyApplication.getInstance()))
                    Actions.hmdSyncCheckUpdate();
                h.postDelayed(this, 3000);
            } catch (Exception e) {
                Log.e(TAG, " " + e.getMessage());
            }
        }
    };

    private final Runnable oneMinuteTimerRunnable = new Runnable() {
        public void run() {
            Log.e("oneMinuteTimerRunnable", "ran");
            isHmdNotConnectedYet = true;
            couldNotConnectToHMD("oneMinuteTimerRunnable runnable");
        }
    };
    private final Runnable threeMinuteTimerRunnable = new Runnable() {
        public void run() {
            Log.e("threeMinuteTimer", "ran");
            isUpdateNotDoneYet = true;
            updateDownloadUI("threeMinuteTimerRunnable runnable");
        }
    };

    int delay = 10 * 1000; //1 second=1000 millisecond
    /*private final Runnable modeSyncCheck = new Runnable() {
        public void run() {
            try {
                Log.e("ModeSync", "Post Delayed Running in MAin Activity");
                // Store.modeSync(false, false, false);
                h.postDelayed(modeSyncCheck, delay);
            } catch (Exception e) {
                Log.e(TAG, " modeSyncCheck runnable " + e.getMessage());
            }
        }
    };*/
    Runnable runnable;
    private TextView batteryLevel;
    private LinearLayout fabParent, batteryFabLayout;
    private String[] patternOptions, strategyOptions;
    private Spinner selectedPattern, selectedStrategy;
    private DevicePolicyManager mDevicePolicyManager;
    private ComponentName mAdminComponentName;
    private BroadcastReceiver batteryReceiver;
    private ActivityManager am;
    private boolean productionSyncInitiated;
    private CardView wifi_card, account_card,
            wired_printer_card,
            wifi_card_options,
            account_card_options,
            wired_printer_card_options;
    //View fabBGLayout;
    private boolean isFABOpen,
            wifiDone,
            accountDone,
            wiredPrinterDone,
            isBatteryLevelSet,
            isNetworkStatusSet = false;

    EditText enter_admin_password, re_enter_admin_password;
    TextInputLayout enter_admin_password_layout, re_enter_admin_password_layout;
    EditText enter_user_password, re_enter_user_password;
    TextInputLayout enter_user_password_layout, re_enter_user_password_layout;
    Button set_user_password_proceed_button, set_admin_password_proceed_button;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
        switch (item.getItemId()) {
            case R.id.action_post_test_delete_test:
                String rowId = new AppPreferencesHelper(MyApplication.getInstance(), PREF_NAME).getLastInsertedRow();
                Log.d("Row to be deleted", " " + rowId);
                deleteReportConfirmationDialog(MainActivity.this, MainActivity.this, rowId, item, false);
                return true;
            case R.id.action_post_test_home:
                //item.setEnabled(false);
                backToHomeScreen();
                return true;
        }
        return false;
    };
    private BroadcastReceiver WifiBroadCastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            final String action = intent.getAction();
            if (action != null) {
                if (action.equals(WifiManager.NETWORK_STATE_CHANGED_ACTION)) {
                    isNetworkStatusSet = true;
                    NetworkInfo info = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);

                    if (info.isConnected()) {
                        menu.getItem(0).setIcon(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_network_wifi_white_24px));
                        //menu.getItem(2).setIcon(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_vr_headset_off));
                        MainActivity.this.setResult();
                    } else {
                        menu.getItem(0).setIcon(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_signal_wifi_off_white_24dp));
                    }
                }
            }
        }
    };

    private final BroadcastReceiver hotspotReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.e("hotspotReceiver", " action " + action);
            if ("android.net.wifi.WIFI_AP_STATE_CHANGED".equals(action)) {

                // get Wi-Fi Hotspot state here
                int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, 0);

                if (WifiManager.WIFI_STATE_ENABLED == state % 10) {
                    // Wifi is enabled
                    Log.e("hotspotReceiver", " Hotspot is enabled ");
                    isHotSpotOn = true;
                    invalidateOptionsMenu();
                } else {
                    Log.e("hotspotReceiver", " Hotspot is disabled ");
                    isHotSpotOn = false;
                    invalidateOptionsMenu();
                }
            }
        }
    };


    private Runnable duringTest_ImageView_Runnable = new Runnable() {
        @Override
        public void run() {

            Log.d("DuringTestImageAssign", "Started");
            while (activeViewID == R.layout.activity_during_test) {

                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    Log.e("MainActivity", "Error while sleep");
                }
                runOnUiThread(() -> {
                    if (activeViewID == R.layout.activity_during_test) {
                        ImageView DuringTestLiveImage = findViewById(R.id.DuringTestLiveImage);
                        Bitmap bitmap = CommunicationService.videoFrame;
                        if (bitmap != null) DuringTestLiveImage.setImageBitmap(bitmap);
                    }
                });
            }

        }
    };
    private Runnable Calib_ImageView_Runnabe = new Runnable() {
        @Override
        public void run() {
            Log.d("ImageAssign", "Started");
            while (activeViewID == R.layout.activity_calibiration) {

                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    Log.e("MainActivity", "Error while sleep");
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (activeViewID == R.layout.activity_calibiration) {
                            ImageView calib = findViewById(R.id.calibrationEye);
                            Bitmap bitmap = CommunicationService.videoFrame;
                            if (bitmap != null) calib.setImageBitmap(bitmap);
                        }
                    }
                });
            }

        }
    };
    private Runnable ipd_ImageView_Runnabe = new Runnable() {
        @Override
        public void run() {
            Log.e("ImageAssign", "Started");
            while (activeViewID == R.layout.activity_ipd_settings) {

                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    Log.e("MainActivity", "Error while sleep");
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

                        if (activeViewID == R.layout.activity_ipd_settings) {
                            ImageView leftEye = findViewById(R.id.ipdEye);
                            Bitmap bitmap = CommunicationService.videoFrame;
                            if (bitmap != null) leftEye.setImageBitmap(bitmap);
                        }
                    }
                });
            }

        }
    };
    private final Runnable waitScreenDelay = new Runnable() {
        @Override
        public void run() {
            if (activeView_number == R.layout.activity_waitscreen_test_profile) {
                activeView_number = R.layout.activity_lens_power_settings;
                showToast("Response delaying from HMD Try Again");
                updateUI("waitScreenDelay runnable");
            } else if (activeView_number == R.layout.activity_waitscreen_ipd_live_feed) {
                activeView_number = R.layout.activity_ipd_settings;
                updateUI("waitScreenDelay runnable");
            } else if (activeView_number == R.layout.activity_waitscreen_calibration_live_feed) {
                if (calibrationRetryCounter < 3) {
                    Actions.beginCalibrationSettings();
                    calibrationRetryCounter++;
                } else {
                    activeView_number = R.layout.activity_home_screen;
                    showToast("HMD is not responding! Going Home");
                    updateUI("waitScreenDelay runnable");
                }
            } else if (activeView_number == R.layout.activity_waitscreen_during_test) {
                if (beginTestRetryCounter < 3) {
                    Actions.beginTest(true, "WaitSceenDelay IF");
                    beginTestRetryCounter++;
                } else {
                    activeView_number = R.layout.activity_home_screen;
                    showToast("Response delaying from HMD Going Home.");
                    updateUI("waitScreenDelay runnable");
                }
            } else if (activeView_number == R.layout.response_waitscreen_abort_calibration) {
                activeView_number = R.layout.activity_home_screen;
                showToast("HMD is not responding! Going Home!");
                updateUI("waitScreenDelay runnable");
            } else if (activeView_number == R.layout.response_waitscreen_abort_ipd) {
                activeView_number = R.layout.activity_home_screen;
                showToast("HMD is not responding! Going Home!");
                updateUI("waitScreenDelay runnable");
            } else if (activeView_number == R.layout.response_waitscreen_abort_during_test) {
                activeView_number = R.layout.activity_home_screen;
                updateUI("waitScreenDelay runnable");
            } else if (activeView_number == R.layout.activity_waitscreen_accessory_status) {
                activeView_number = R.layout.accessories_status;
                updateUI("waitScreenDelay runnable");
            }
        }
    };
    private Runnable hmdTcInit = new Runnable() {
        @Override
        public void run() {
            try {
                while (true) {
                    if (isHotspotOn(MyApplication.getInstance())) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                activeView_number = R.layout.hmd_sync_check_activity;
                                updateUI("hmdTcInit runnable");
                            }
                        });
                        break;
                    }
                    Thread.sleep(2000);
                }
            } catch (InterruptedException e) {
                Log.e(TAG, " " + e.getMessage());
            }
        }
    };

    private final Runnable lookForEyeTrackingStatusUpdate = () -> {
        if (activeView_number == R.layout.accessories_status) {
            TextView eyeTracking_Status = findViewById(R.id.eyeTrackingStatus);
            if (eyeTrackingStatus == EyeTrackingStatus.EYE_TRACKING_OK) {
                eyeTracking_Status.setText(R.string.eye_tracking_okay);
            } else if (eyeTrackingStatus == EyeTrackingStatus.EYE_TRACKING_NOT_OK) {
                eyeTracking_Status.setText(R.string.eye_tracking_not_okay);
            } else if (eyeTrackingStatus == EyeTrackingStatus.UNDEFINED) {
                eyeTracking_Status.setText(R.string.eye_tracking_not_okay);
            }
        }
    };
    private final Runnable lookForDisplayUpdate = () -> {
        if (activeView_number == R.layout.accessories_status) {
            TextView display_status = findViewById(R.id.display_status);
            if (displayStatus == DisplayStatus.DISPLAY_OK) {
                display_status.setText(R.string.display_okay);
            } else if (displayStatus == DisplayStatus.DISPLAY_NOT_OK) {
                display_status.setText(R.string.display_not_okay);
            } else if (displayStatus == DisplayStatus.UNDEFINED) {
                display_status.setText(R.string.display_unknown);
            }
        }
    };

    private BroadcastReceiver UIReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateUI("UIReceiver");
        }
    };

    private static void saveUI(View view) {
        Log.d("SaveUI", "Called");
        AppPreferencesHelper appPreferencesHelper = new AppPreferencesHelper(MainActivity.applicationContext, PREF_NAME);
        Bundle state = getState();

        int activeView = state.getInt("viewID");
        activeState.putInt("viewID", activeView);
        switch (activeView) {

            case R.layout.activity_primary_patient_details:
                EditText patientFirstName = view.findViewById(R.id.patient_FirstName_editText_value);
                EditText patientMrnNumber = view.findViewById(R.id.patient_mrn_number_editText_value);
                EditText patientMobileNumber = view.findViewById(R.id.patient_mobile_number_editText_value);
                EditText patientDOB = view.findViewById(R.id.Patient_DOB_editText_value);
                RadioGroup patientSexGroup = view.findViewById(R.id.PatientSex_radio_group);
                RadioButton patientSex = view.findViewById(patientSexGroup.getCheckedRadioButtonId());
                activeState.putString("PatientFirstName", patientFirstName.getText().toString());
                activeState.putString("PatientMrnNumber", patientMrnNumber.getText().toString());
                activeState.putString("PatientMobileNumber", patientMobileNumber.getText().toString());
                activeState.putString("PatientDob", patientDOB.getText().toString());
                appPreferencesHelper.setPatientFirstName(patientFirstName.getText().toString());
                appPreferencesHelper.setPatientMrnNumber(patientMrnNumber.getText().toString());
                appPreferencesHelper.setPatientMobileNumber(patientMobileNumber.getText().toString());
                appPreferencesHelper.setPatientDOB(patientDOB.getText().toString());
                if (patientSex != null) {
                    activeState.putString("PatientSex", patientSex.getText().toString());
                    appPreferencesHelper.setPatientSex(patientSex.getText().toString());
                } else {
                    activeState.putString("PatientSex", "");
                    appPreferencesHelper.setPatientSex("");
                }
                break;

            case R.layout.activity_primary_test_details:
                RadioGroup selectEyeRadioGroup = view.findViewById(R.id.toggleEye);
                RadioButton selectedEye = view.findViewById(selectEyeRadioGroup.getCheckedRadioButtonId());
                Spinner pattern = view.findViewById(R.id.test_pattern_spinner);
                Spinner strategy = view.findViewById(R.id.test_strategy_spinner);
                String testPattern = patternAdapter.getItem(pattern.getSelectedItemPosition());
                String testStrategy = strategyAdapter.getItem(strategy.getSelectedItemPosition());
                activeState.putString("TestEye", selectedEye.getText().toString());
                activeState.putString("TestPattern", testPattern);
                activeState.putString("TestStrategy", testStrategy);
                appPreferencesHelper.setPatientTestEye(selectedEye.getText().toString());
                appPreferencesHelper.setTestPattern(testPattern);
                appPreferencesHelper.setTestStrategy(testStrategy);
                break;

            case R.layout.activity_lens_power_settings:
                EditText sphericalPower = view.findViewById(R.id.editable_spherical_power);
                EditText cylindricalPower = view.findViewById(R.id.editable_cylindrical_power);
                EditText cylindricalAxis = view.findViewById(R.id.editable_cylindrical_axis);
                TextView readable_spherical_power = view.findViewById(R.id.readable_spherical_power);
                TextView readable_cylindrical_power = view.findViewById(R.id.readable_cylindrical_power);
                TextView readable_cylindrical_axis = view.findViewById(R.id.readable_cylindrical_axis);
                activeState.putString("PatientSphericalInput", sphericalPower.getText().toString());
                activeState.putString("PatientCylindricalInput", cylindricalPower.getText().toString());
                activeState.putString("PatientCylindricalAxisInput", cylindricalAxis.getText().toString());

                activeState.putString("SphericalPower", readable_spherical_power.getText().toString());
                activeState.putString("CylindricalPower", readable_cylindrical_power.getText().toString());
                activeState.putString("CylindricalAxisPower", readable_cylindrical_axis.getText().toString());

                appPreferencesHelper.setTestSphericalPowerInput(sphericalPower.getText().toString());
                appPreferencesHelper.setTestCylindricalPowerInput(cylindricalPower.getText().toString());
                appPreferencesHelper.setTestCylindricalAxisInput(cylindricalAxis.getText().toString());

                appPreferencesHelper.setTestSphericalPower(readable_spherical_power.getText().toString());
                appPreferencesHelper.setTestCylindricalPower(readable_cylindrical_power.getText().toString());
                appPreferencesHelper.setTestCylindricalAxis(readable_cylindrical_axis.getText().toString());
                break;

            case R.layout.activity_during_test:
                break;

        }

        //Store.setValues();
        Actions.saveState(activeState);
    }

    public static void resetCalibrationData() {
        Store.calibrationRecalibration = "Calibration";
        Store.calibrationStatus = "";
        Store.calibrationSaveProceedVisibility = 8;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("LifeCycle", "OnCreate");
        onWindowFocusChanged(true);
        applicationContext = getApplicationContext();
        dialogReference = MainActivity.this;

        mDevicePolicyManager = (DevicePolicyManager)
                getSystemService(Context.DEVICE_POLICY_SERVICE);
        mAdminComponentName = DeviceAdminReceiver.getComponentName(this);
        //Main reason this is being done is so that intent service can be begun from the same context always.
        IntentFilter storeFilter = new IntentFilter();
        storeFilter.addAction(Actions.DISPATCH_TOSTORE);
        applicationContext.registerReceiver(Store.storeReceiver, storeFilter);
        //register activity receiver
        IntentFilter activityReceiver = new IntentFilter();
        activityReceiver.addAction(StoreTransmitter.CHANGETRIGG_UI);
        this.registerReceiver(UIReceiver, activityReceiver);
        //register communication receiver
        IntentFilter commFilter = new IntentFilter();
        commFilter.addAction(StoreTransmitter.CHANGETRIGG_COMM);
        this.registerReceiver(new CommunicationReceiver(), commFilter);
       /* IntentFilter restart = new IntentFilter();
        this.registerReceiver(new StartUpReceiver(), restart);*/

        am = (ActivityManager) getSystemService(
                Context.ACTIVITY_SERVICE);
        batteryReceiver = new BatteryBroadcastReceiver();

        IntentFilter hotspotReceiverIntent = new IntentFilter("android.net.wifi.WIFI_AP_STATE_CHANGED");
        registerReceiver(hotspotReceiver, hotspotReceiverIntent);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.setElevation(0);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        activeState = new Bundle();

        // throw new NullPointerException();

        AppPreferencesHelper devicePreferencesHelper = new AppPreferencesHelper(this, DEVICE_PREF);
        if (devicePreferencesHelper.getProductionTestingStatus() && devicePreferencesHelper.getUserSetUpStatus()) {
            startBackgroundWorks();
            //h.postDelayed(modeSyncCheck, delay);
        } else {
            Log.e("ModeSync", " " + devicePreferencesHelper.getProductionTestingStatus());
            Log.e("ModeSync", " " + devicePreferencesHelper.getUserSetUpStatus());
        }
    }

    @Override
    protected void onStart() {
        Log.e("LifeCycle", "OnStart");
        super.onStart();
        //AppUpdaterJob.scheduleAppUpdate();

        patternOptions = getResources().getStringArray(R.array.pattern);
        strategyOptions = getResources().getStringArray(R.array.strategies);
        patternAdapter = new ArrayAdapter<>(
                this,
                R.layout.spinner_item,
                patternOptions);
        strategyAdapter = new ArrayAdapter<>(
                this,
                R.layout.spinner_item,
                strategyOptions);

        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {
                registerReceiver(batteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
                IntentFilter intentFilter = new IntentFilter();
                intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
                registerReceiver(WifiBroadCastReceiver, intentFilter);
            }
        }, 2000);
    }

    @Override
    protected void onPause() {
        Log.e("LifeCycle", "OnPause");
        super.onPause();

    }

    @Override
    protected void onResume() {
        Log.e("LifeCycle", "OnResume");
        super.onResume();
        if (BuildConfig.ACTIVATE_KIOSK)
            if (CommonUtils.isKioskNotActive(am)) CommonUtils.startKiosk(this);
        IntentFilter activityReceiver = new IntentFilter();
        activityReceiver.addAction(StoreTransmitter.CHANGETRIGG_UI);
        this.registerReceiver(UIReceiver, activityReceiver);
        IntentFilter commFilter = new IntentFilter();
        commFilter.addAction(StoreTransmitter.CHANGETRIGG_COMM);
        new Handler(Looper.getMainLooper()).postDelayed(() -> registerReceiver(batteryReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED)), 2000);
        updateUI("onResume mainactivity");

    }

    @Override
    protected void onStop() {
        Log.e("LifeCycle", "OnStop");
        super.onStop();
        try {
            View root = getWindow().getDecorView().findViewById(android.R.id.content);
            saveUI(root);
            unregisterReceiver(UIReceiver);
            if (isBatteryLevelSet) {
                Log.d("isBatteryLevelSet", " " + isBatteryLevelSet);
                unregisterReceiver(batteryReceiver);
                isBatteryLevelSet = false;
            }

            if (isNetworkStatusSet) {
                Log.d("isNetworkStatusSet", " " + isNetworkStatusSet);
                unregisterReceiver(WifiBroadCastReceiver);
                isNetworkStatusSet = false;
            }
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
        }
    }

    @Override
    protected void onDestroy() {
        Log.e("LifeCycle", "OnDestroy");
        //h.removeCallbacks(modeSyncCheck); //stop handler when activity not visible
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        onBackPressed(activeViewID);
    }

    private void onBackPressed(int activeViewID) {
        switch (activeViewID) {
            case R.layout.checking_for_database_in_remote:

                activeView_number = R.layout.test_controller_settings;
                startActivity(new Intent(this, AdminProfileActivity.class));
                break;
            case R.layout.download_database:
                if (databaseDownloadStatus != Status.COMPLETED)
                    cancelDownloadAndGoToSettingsScreen();
                break;
            case R.layout.merging_database:
                if (databaseDownloadStatus == Status.COMPLETED)
                    cancelDownloadAndGoToSettingsScreen();
                break;
            case R.layout.download_update_screen:
                if (tcDownloadStatus != Status.COMPLETED || hmdDownloadStatus != Status.COMPLETED)
                    cancelDownloadAndGoToSettingsScreen();
                else if (!isHmdNotConnectedYet) {
                    dialogTwo();
                }
                break;
            case R.layout.software_upto_date:
                Actions.beginTestControllerSettings();
                break;
            case R.layout.software_update_available:
                Actions.beginTestControllerSettings();
                break;
            case R.layout.activity_pre_production:
                CommonUtils.abortCalibrationConfirmationDialog(applicationContext);
                break;
            case R.layout.new_hmd_details_activity:
                goHomeImmediately();
                break;
            case R.layout.activity_primary_test_details:
                //Actions.silentlyStopTest();
                Actions.backToHomeScreen();
                break;
            case R.layout.activity_primary_patient_details:
                Actions.showTestDetailsAndStopTest();
                break;
            case R.layout.activity_lens_power_settings:
                EditText sphericalPower = findViewById(R.id.editable_spherical_power);
                ImageView calculate = findViewById(R.id.lens_power_calc);
                if (!TextUtils.isEmpty(sphericalPower.getText()))
                    calculate.performClick();
                new AppPreferencesHelper(applicationContext, PREF_NAME).setPatientDetailsViewVisibility(true);
                Actions.beginPatientDetailsWithoutEIStart();
                break;
            case R.layout.activity_test_profile:
                Actions.beginLensSettings();
                break;
            case R.layout.checking_interfaces_screen:
            case R.layout.accessories_status:
            case R.layout.activity_waitscreen_ipd_live_feed:
            case R.layout.activity_ipd_settings:
            case R.layout.activity_waitscreen_calibration_live_feed:
            case R.layout.activity_calibiration:
            case R.layout.activity_waitscreen_during_test:
            case R.layout.activity_during_test:
                CommonUtils.abortTestConfirmationDialog(this);
                break;
            case R.layout.test_controller_settings:
            case R.layout.warning_bo_charging:
            case R.layout.warning_bno_charging:
            case R.layout.warning_bno_not_charging:
            case R.layout.error_component_failure:
                goHomeImmediately();
                break;
            case R.layout.activity_waitscreen_test_profile:
                break;
            case R.layout.decoy:
                break;

        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        //if (hasFocus) hideSystemUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_network_settings, menu);
        MainActivity.menu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        if (isHotSpotOn) {
            menu.findItem(R.id.action_hotspot_status).setIcon(R.drawable.ic_wifi_tethering_on);
        } else {
            menu.findItem(R.id.action_hotspot_status).setIcon(R.drawable.ic_wifi_tethering_off);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_network_settings:
                //stopKiosk();
                //startActivityForResult(new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK), 30);
                break;
            case R.id.action_hotspot_status:
                if (activeView_number == R.layout.activity_home_screen || activeView_number == R.layout.test_controller_settings
                        || activeView_number == R.layout.new_hmd_details_activity || activeView_number == R.layout.download_update_screen) {
                    Log.e("onOptionsItemSelected", " isHotSpotOn " + isHotSpotOn);
                    if (isHotSpotOn) {
                        showHotSpotStatusDialog(false);
                        Bundle bundle = new Bundle();
                        bundle.putString("data", "NA");
                        StoreTransmitter.doCommFunction(StoreTransmitter.COMM_FUNCTION_STOP, bundle);
                        item.setIcon(R.drawable.ic_wifi_tethering_off);
                        Store.newTestVisibility = false;
                        MyApplication.getInstance().set_HMD_CONNECTION_NEED(false);
                        MyApplication.getInstance().set_HMD_CONNECTED(false);
                    } else {
                        communicationActive = false;
                        showHotSpotStatusDialog(true);
                        MyApplication.getInstance().set_HMD_CONNECTION_NEED(true);
                        MyApplication.getInstance().set_HMD_CONNECTED(true);
                        Actions.initCommunication();
                        item.setIcon(R.drawable.ic_wifi_tethering_on);
                    }
                }
                break;
        }
        return true;
    }

    @Override
    public void onAccountsUpdated(Account[] accounts) {
        Log.d("AccountUpdated", " " + isDeviceHasGoogleAccount());
        accountDone = isDeviceHasGoogleAccount();
        //if (isDeviceHasGoogleAccount())
        //finishActivity(40);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        switch (i) {
            case 1:
                strategyOptions = getResources().getStringArray(R.array.twenty_four_dash_two_strategies);
                strategyAdapter = new ArrayAdapter<>(
                        this,
                        R.layout.spinner_item,
                        strategyOptions);
                selectedStrategy.setAdapter(strategyAdapter);
                break;
            case 2:
                strategyOptions = getResources().getStringArray(R.array.thirty_dash_two_strategies);
                strategyAdapter = new ArrayAdapter<>(
                        this,
                        R.layout.spinner_item,
                        strategyOptions);
                selectedStrategy.setAdapter(strategyAdapter);
                break;
            case 3:
                strategyOptions = getResources().getStringArray(R.array.ten_dash_two_strategies);
                strategyAdapter = new ArrayAdapter<>(
                        this,
                        R.layout.spinner_item,
                        strategyOptions);
                selectedStrategy.setAdapter(strategyAdapter);
                break;
            case 4:
                strategyOptions = getResources().getStringArray(R.array.macula_strategies);
                strategyAdapter = new ArrayAdapter<>(
                        this,
                        R.layout.spinner_item,
                        strategyOptions);
                selectedStrategy.setAdapter(strategyAdapter);
                break;
        }
        String stat = getState().getString("PatientTestStrategy");
        if (nullCheck(stat))
            selectedStrategy.setSelection(strategyAdapter.getPosition(stat));
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void doCameraAction(int id) {
        double tempAlpha, tempBeta;
        AppPreferencesHelper devicePreferencesHelper = new AppPreferencesHelper(MainActivity.this, DEVICE_PREF);
        EditText cameraAlpha = findViewById(R.id.cameraAlpha);
        EditText cameraBeta = findViewById(R.id.cameraBeta);
        TextView eyeType = findViewById(R.id.eyeType);
        String camAlp = cameraAlpha.getText().toString();
        String camBta = cameraBeta.getText().toString();
        if (!camAlp.equals("") && !camBta.equals("")) {
            double alpha = Double.parseDouble(cameraAlpha.getText().toString());
            double beta = Double.parseDouble(cameraBeta.getText().toString());
            if (id == R.id.contrastUp) {
                Log.e("doCameraAction", "ContrastUp");
                if (alpha < 1) {
                    cameraAlpha.setText("" + alpha);
                    alpha = alpha + 0.1;
                    updateCameraPreferences(eyeType.getText().toString().equalsIgnoreCase("left eye"), devicePreferencesHelper,
                            (float) alpha, (float) beta);
                } else if (alpha >= 3) {
                    CommonUtils.showToasty(this, "Camera Contrast cannot be more than 3", true, 'I');
                } else {
                    alpha = alpha + 0.1;
                    if (alpha > 3) {
                        CommonUtils.showToasty(this, "Camera Contrast cannot be more than 3", true, 'I');
                    } else {
                        cameraAlpha.setText("" + alpha);
                        updateCameraPreferences(eyeType.getText().toString().equalsIgnoreCase("left eye"), devicePreferencesHelper,
                                (float) alpha, (float) beta);
                    }
                }
            } else if (id == R.id.contrastDown) {
                Log.e("doCameraAction", "ContrastDown");
                if (alpha < 1) {
                    CommonUtils.showToasty(this, "Camera Contrast should be greater than or equal to 1", true, 'I');
                } else if (alpha == 1) {
                    CommonUtils.showToasty(this, "Camera Contrast should be not be less than 1", true, 'I');
                } else if (alpha >= 3) {
                    alpha = alpha - 0.1;
                    cameraAlpha.setText("" + alpha);
                    updateCameraPreferences(eyeType.getText().toString().equalsIgnoreCase("left eye"), devicePreferencesHelper,
                            (float) alpha, (float) beta);
                } else {
                    alpha = alpha - 0.1;
                    cameraAlpha.setText("" + alpha);
                    updateCameraPreferences(eyeType.getText().toString().equalsIgnoreCase("left eye"), devicePreferencesHelper,
                            (float) alpha, (float) beta);
                }
            } else if (id == R.id.brightnessUp) {
                Log.e("doCameraAction", "brightnessUp");
                if (beta < 1) {
                    beta = beta + 10;
                    updateCameraPreferences(eyeType.getText().toString().equalsIgnoreCase("left eye"), devicePreferencesHelper,
                            (float) alpha, (float) beta);
                    CommonUtils.showToasty(this, "Camera Brightness should be greater than or equal to 1", true, 'I');
                } else if (beta > 150) {
                    CommonUtils.showToasty(this, "Camera Brightness should be less than or equal to 150", true, 'I');
                } else {
                    beta = beta + 10;
                    if (beta >= 150) {
                        CommonUtils.showToasty(this, "Camera Brightness should be less than or equal to 150", true, 'I');
                    } else {
                        cameraBeta.setText("" + beta);
                        updateCameraPreferences(eyeType.getText().toString().equalsIgnoreCase("left eye"), devicePreferencesHelper,
                                (float) alpha, (float) beta);
                    }
                }
            } else if (id == R.id.brightnessDown) {
                Log.e("doCameraAction", "brightnessDown");
                if (beta < 1) {
                    CommonUtils.showToasty(this, "Camera Brightness should be greater than or equal to 1", true, 'I');
                } else if (beta == 1) {
                    CommonUtils.showToasty(this, "Camera Brightness should be not be less than 1", true, 'I');
                } else if (beta > 1 && beta < 10) {
                    CommonUtils.showToasty(this, "Camera Brightness should be not be less than 1", true, 'I');
                } else if (beta > 10) {
                    beta = beta - 10;
                    cameraBeta.setText("" + beta);
                    updateCameraPreferences(eyeType.getText().toString().equalsIgnoreCase("left eye"), devicePreferencesHelper,
                            (float) alpha, (float) beta);
                }
            }
        }
    }

    private void updateCameraPreferences(boolean isLeftCamera, AppPreferencesHelper devicePreferencesHelper, float alpha, float beta) {
        if (isLeftCamera) {
            devicePreferencesHelper.setLeftCameraAlpha(alpha);
            devicePreferencesHelper.setLeftCameraBeta(beta);
        } else {
            devicePreferencesHelper.setRightCameraAlpha(alpha);
            devicePreferencesHelper.setRightCameraBeta(beta);
        }
        Actions.sendHMDCameraAlphaBeta(alpha + " " + beta);
    }

    public void clickCallback(final View v) {
        View root = v.getRootView();
        final AppPreferencesHelper appPreferencesHelper = new AppPreferencesHelper(applicationContext, PREF_NAME);
        final AppPreferencesHelper devicePreferencesHelper = new AppPreferencesHelper(MainActivity.this, DEVICE_PREF);
        //if (id > 0 & exceptionClicks(id)) appPreferencesHelper.setCurrentViewId(v.getId());
        //Log.d("clickCallback ID", "Contains ID " + (id));
        //Log.d("clickCallback", "ViewId " + v.getResources().getResourceName(id));
        switch (v.getId()) {

            case R.id.cancelInstallation:
                dialogOne();
                break;
            case R.id.cancelUpdate:
                dialogOne();
                break;
            case R.id.tryAgain:
                tryAgainUI("clickCallback");
                updateHandler.postDelayed(oneMinuteTimerRunnable, 60000);
                break;
            case R.id.camera_settings:
                Button camera_settings = findViewById(R.id.camera_settings);
                CardView cameraSettingsCard = findViewById(R.id.cameraSettingsCard);
                CardView ipdKnobMessage = findViewById(R.id.ipdKnobMessage);
                if (cameraSettingsCard.getVisibility() == View.INVISIBLE) {
                    Log.e("camera_settings", "InVisible");
                    cameraSettingsCard.setVisibility(View.VISIBLE);
                    ipdKnobMessage.setVisibility(View.INVISIBLE);
                    camera_settings.setText("Close Camera Settings");
                } else {
                    Log.e("camera_settings", "Visible");
                    cameraSettingsCard.setVisibility(View.INVISIBLE);
                    ipdKnobMessage.setVisibility(View.VISIBLE);
                    Log.e("", "Visible");
                    camera_settings.setText("Open Camera Settings");
                }
                break;

            case R.id.contrastUp:
            case R.id.contrastDown:
            case R.id.brightnessUp:
            case R.id.brightnessDown:
                doCameraAction(v.getId());
                break;

            case R.id.update_tc_button:
                CommonUtils.packageInstaller(this);
                break;

            case R.id.restart_tc_download:
                Button restart_tc_download = findViewById(R.id.restart_tc_download);
                TextView tc_percentage = findViewById(R.id.tc_percentage);
                ProgressBar tc_progress = findViewById(R.id.tc_progress);
                restart_tc_download.setVisibility(View.INVISIBLE);
                downloadTCUpdate(tc_percentage, tc_progress, restart_tc_download, devicePreferencesHelper);
                break;

            case R.id.restart_database_download:
                Button restart_database_download = findViewById(R.id.restart_database_download);
                TextView database_percentage = findViewById(R.id.database_percentage);
                ProgressBar database_progress = findViewById(R.id.database_progress);
                restart_database_download.setVisibility(View.INVISIBLE);
                downloadDatabase(database_percentage, database_progress, restart_database_download);
                break;

            case R.id.database_finished:
                goHomeImmediately();
                break;

            case R.id.restart_hmd_download:
                Button restart_hmd_download = findViewById(R.id.restart_hmd_download);
                ProgressBar hmd_progress = findViewById(R.id.hmd_progress);
                TextView hmd_percentage = findViewById(R.id.hmd_percentage);
                restart_hmd_download.setVisibility(View.INVISIBLE);
                downloadHmdUpdate(hmd_percentage, hmd_progress, restart_hmd_download, devicePreferencesHelper);
                break;

           /* case R.id.update_HMD:
                Actions.beginHMDUpdate();
                break;*/

            case R.id.beginUpdate:
                Actions.startDownloadUpdates();
                break;
            case R.id.beginDatabaseMerging:
                Actions.startDownloadDatabase();
                break;

            case R.id.updateLater:
                Actions.beginTestControllerSettings();
                break;
            case R.id.mergeLater:
                Actions.beginTestControllerSettings();
                break;

            case R.id.error_hmd_update_failed_button:
                Actions.goHomeImmediately();
                break;

           /* case R.id.cant_start_ei_get_accessibility_status_button:
                Actions.checkAccessibilityStatus();
                break;
            case R.id.cant_start_ei_go_home:
                Actions.abortCalibAndGoHomeImmediately();
                break;*/
            case R.id.calibUp_Button:
                Actions.calibUp();
                break;
            case R.id.calibDown_Button:
                Actions.calibDown();
                break;
            case R.id.send_vector:
                Actions.setActionSendVectorData();
                v.setVisibility(View.INVISIBLE);
                break;

            case R.id.batteryFab:
                String bat = CommonUtils.splitHmdHeartBeat("Battery");
                CommonUtils.showToasty(this, bat.isEmpty() ? "Battery stat not availabe" : "HMD Battery level " + bat, true, 'I');
                break;
            case R.id.fab:
                Log.d("isFABOpen", " " + isFABOpen);
                if (!isFABOpen) {
                    showFABMenu();
                    Actions.updateHMDBatteryStatus();
                } else {
                    closeFABMenu();
                }
                break;
            case R.id.radio_male:
                RadioButton patMale = root.findViewById(R.id.radio_male);
                RadioButton patFemale = root.findViewById(R.id.radio_female);
                patMale.setError(null);
                patFemale.setError(null);
                break;
            case R.id.radio_female:
                RadioButton male = root.findViewById(R.id.radio_male);
                RadioButton female = root.findViewById(R.id.radio_female);
                male.setError(null);
                female.setError(null);
                TextView sexError = root.findViewById(R.id.radioError);
                sexError.setError(null);
                break;

            case R.id.searchMrn:
                CommonUtils.hideKeypad(root, applicationContext);
                final EditText patientFirstName = root.findViewById(R.id.patient_FirstName_editText_value);
                final EditText patientMrn = root.findViewById(R.id.patient_mrn_number_editText_value);
                final EditText patientMobileNumber = root.findViewById(R.id.patient_mobile_number_editText_value);
                final EditText patientDOB = root.findViewById(R.id.Patient_DOB_editText_value);
                final RadioButton patientMale = root.findViewById(R.id.radio_male);
                final RadioButton patientFemale = root.findViewById(R.id.radio_female);
                TextView sexError1 = root.findViewById(R.id.radioError);
                String mrn = patientMrn.getText().toString();
                ImageView searchMrn = findViewById(R.id.searchMrn);
                if (!mrn.isEmpty()) {
                    if (searchMrn.getContentDescription().toString().equals("back")) {
                        toggleSearch();
                        Log.d("Content", "back");
                        searchMrn.setImageResource(R.drawable.ic_search_black_48px);
                        patientMrn.setEnabled(true);
                    } else {
                        toggleSearch();
                        Log.d("Content", "search");
                        searchMrn.setImageResource(R.drawable.ic_baseline_keyboard_backspace_24px);
                        new SearchPatientBasicDetails(new AsyncDbTaskString() {
                            @Override
                            public void onProcessFinish(ArrayList<String> b) {
                                if (!b.isEmpty()) {
                                    isRecordsFound(true);
                                    enablePatientDetailsView(true);
                                    new AppPreferencesHelper(applicationContext, PREF_NAME).setPatientDetailsViewVisibility(true);
                                    String age = b.get(0);
                                    String mobile = b.get(1);
                                    String name = b.get(2);
                                    String sex = b.get(3);
                                    Log.d("SearchedAge", " " + age);
                                    Log.d("Searchedmobile", " " + mobile);
                                    Log.d("Searchedname", " " + name);
                                    Log.d("Searchedsex", " " + sex);
                                    if (age != null && mobile != null && name != null && sex != null) {
                                        patientFirstName.setError(null);
                                        patientFirstName.setText(name);
                                        patientMobileNumber.setError(null);
                                        patientMobileNumber.setText(mobile);
                                        patientDOB.setError(null);
                                        patientDOB.setText(age);
                                        if (sex.equals("Male")) patientMale.setChecked(true);
                                        else patientFemale.setChecked(true);
                                        sexError1.setError(null);
                                    }
                                    //Toast.makeText(applicationContext, "Records Found", Toast.LENGTH_SHORT).show();
                                    showToast("Records Found");
                                } else {
                                    toggleSearch();
                                    isRecordsFound(false);
                                    enablePatientDetailsView(true);
                                    patientFirstName.setText("");
                                    patientMobileNumber.setText("");
                                    patientDOB.setText("");
                                    //patientMale.setChecked(true);
                                    showToast("No Records Found");
                                }

                            }
                        }).execute(mrn);
                    }
                } else
                    showToast("Fill some text and click search");
                break;

            case R.id.new_test:
            case R.id.new_test_img:
                Store.isAbortClicked = false;
                Store.batteryLevel = null;
                Store.batteryLevelVisibility = false;
                Store.electronicInterfaceStatus = ElectronicInterfaceStatus.UNDEFINED;
                Store.hmdBatteryStatus = UNDEFINED;
                if (CommunicationService.videoFrame != null)
                    CommunicationService.videoFrame = null;
                timeWhenStopped = 0;
                isChronometerRunning = false;
                new AppPreferencesHelper(MainActivity.this, PREF_NAME).setPatientDetailsViewVisibility(false);
                int prbCount = CommonUtils.returnClickCount();
                Log.e("returnClickCount", " " + prbCount);
                if (!CommonUtils.is_TC_HMD_Version_Matches()) {
                    versionMismatchDialog();
                } else if (prbCount >= 1000000) {
                    prbValidityReached();
                } /*else if (CommonUtils.isMobileDataOn(this)) {
                    if (CommonUtils.returnClickCount() > 600000) {
                        int count = CommonUtils.getPRBCount(MainActivity.this);
                        FirebaseCrashlytics.getInstance().recordException(new PrbException("PRB reaches the limit " + count));
                    }
                    if (CommonUtils.getNetworkType(this).equalsIgnoreCase("2g")) {
                        CommonUtils.openMobileData(this);
                    } else {
                        checkCriticalUpdateAndContinue();
                    }
                } */ else {
                    if (prbCount >= 600000) {
                        String msg = "PRB reaches the limit " + prbCount;
                        FirebaseCrashlytics.getInstance().recordException(new PrbException(msg));
                    }
                    checkCriticalUpdateAndContinue();
                }


                break;


            case R.id.view_reports:
            case R.id.view_reports_img:

                Intent reportListImg = new Intent(applicationContext, TestResultsListActivity.class);
                reportListImg.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                applicationContext.startActivity(reportListImg);
                break;

            case R.id.settings:
            case R.id.settings_img:
                Actions.beginTestControllerSettings();
              /*  Intent settings = new Intent(applicationContext, TcSettings.class);
                settings.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                applicationContext.startActivity(settings);*/
                break;

            case R.id.error_bno_not_charging_button:
                goHomeImmediately();
                break;
            case R.id.error_bno_charging_button:
                goHomeImmediately();
                break;

            case R.id.error_bo_charging_button:
                Actions.checkBattery();
                /*Actions.startCheckingInterface();
                Actions.startEI();*/
                break;

            case R.id.error_accessory_modules_try_again_button:
                Actions.startCheckingInterface();
                Actions.startEI();
                break;

            case R.id.error_accessory_modules_restart_hmd_button:
                CommonUtils.rebootHMD(this);
                break;

            case R.id.primary_test_details_continue_button:
                saveUI(root);
                Log.e("BatteryStatus", "OnClick " + Store.hmdBatteryStatus);
                if (Store.hmdBatteryStatus == BATTERY_LOW_PLEASE_CHARGE_HMD) {
                    Actions.batteryNotOkayNotCharging();
                    Log.e("EI_MODULE_STATUS", "Battery Level Low. Please charge headset");
                } else if (Store.hmdBatteryStatus == BATTERY_LOW_KEEP_CHARGING) {
                    Actions.batteryLowKeepCharging();
                } else if (Store.hmdBatteryStatus == PLUG_OUT_CHARGE_OK) {
                    Actions.plugOutChargeOkay();
                } else if (Store.hmdBatteryStatus == BATTERY_OK) {
                    Actions.beginPatientDetails();
                } else if (Store.hmdBatteryStatus == UNDEFINED) {
                    showToasty(this, "Waiting for Battery Status, Try again.", true, 'I');
                }
                break;

            case R.id.primary_patient_details_continue_button:
                saveUI(root);
                EditText patientFirstName_t = root.findViewById(R.id.patient_FirstName_editText_value);
                EditText patientMrnNumber_t = root.findViewById(R.id.patient_mrn_number_editText_value);
                EditText patientMobileNumber_t = root.findViewById(R.id.patient_mobile_number_editText_value);
                EditText patientDOB_t = root.findViewById(R.id.Patient_DOB_editText_value);
                RadioGroup patientSexGroup_t = root.findViewById(R.id.PatientSex_radio_group);
                RadioButton patientSex_t = root.findViewById(patientSexGroup_t.getCheckedRadioButtonId());
                if (Store.hmdBatteryStatus == BATTERY_LOW_PLEASE_CHARGE_HMD) {
                    Actions.batteryNotOkayNotCharging();
                } else if (Store.hmdBatteryStatus == BATTERY_LOW_KEEP_CHARGING) {
                    Actions.batteryLowKeepCharging();
                } else if (Store.hmdBatteryStatus == PLUG_OUT_CHARGE_OK) {
                    Actions.plugOutChargeOkay();
                } else if (TextUtils.isEmpty(patientMrnNumber_t.getText())) {
                    Log.e("TrackMe", " 1");
                    showTip = true;
                    tipId = R.id.patient_mrn_number_editText_value;
                    Bundle b = new Bundle();
                    b.putInt("tipId", tipId);
                    b.putString("toastMessage", "Please enter MRN Number");
                    showMeTip(b);
                } else if (patientFirstName_t.getVisibility() == View.INVISIBLE) {
                    clickCallback(findViewById(R.id.searchMrn));
                } else if (TextUtils.isEmpty(patientFirstName_t.getText())) {
                    Log.e("TrackMe", " 2");
                    showTip = true;
                    tipId = R.id.patient_FirstName_editText_value;
                    Bundle b = new Bundle();
                    b.putInt("tipId", tipId);
                    b.putString("toastMessage", "Please enter patient Name");
                    showMeTip(b);
                } else if (TextUtils.isEmpty(patientMobileNumber_t.getText())) {
                    Log.e("TrackMe", " 3");
                    showTip = true;
                    tipId = R.id.patient_mobile_number_editText_value;
                    Bundle b = new Bundle();
                    b.putInt("tipId", tipId);
                    b.putString("toastMessage", "Please enter Mobile Number");
                    showMeTip(b);
                } else if (patientMobileNumber_t.getText().toString().length() < 12) {
                    Log.e("TrackMe", " 4");
                    showTip = true;
                    tipId = R.id.patient_mobile_number_editText_value;
                    Bundle b = new Bundle();
                    b.putInt("tipId", tipId);
                    b.putString("toastMessage", "Please enter valid Mobile Number");
                    showMeTip(b);
                } else if (TextUtils.isEmpty(patientDOB_t.getText())) {
                    Log.e("TrackMe", " 5");
                    showTip = true;
                    tipId = R.id.Patient_DOB_editText_value;
                    Bundle b = new Bundle();
                    b.putInt("tipId", tipId);
                    b.putString("toastMessage", "Please enter Age");
                    showMeTip(b);
                } else if (Integer.parseInt(getAge(patientDOB_t.getText().toString())) < 10) {
                    Log.e("TrackMe", " 6");
                    showTip = true;
                    tipId = R.id.Patient_DOB_editText_value;
                    Bundle b = new Bundle();
                    b.putInt("tipId", tipId);
                    b.putString("toastMessage", "Age must be greater than 10");
                    showMeTip(b);
                } else if (patientSex_t == null) {
                    // != R.id.radio_male && patientSex_t.getId() != R.id.radio_female
                    Log.e("TrackMe", " 7");
                    showTip = true;
                    tipId = R.id.radioError;
                    Bundle b = new Bundle();
                    b.putInt("tipId", tipId);
                    b.putString("toastMessage", "Please Select Gender");
                    showMeTip(b);
                } else {
                    Log.e("TrackMe", " 8");
                    switch (Store.electronicInterfaceStatus) {
                        case UNDEFINED:
                            Actions.startCheckingInterface();
                            Log.e("EI_MODULE_STATUS", "UNDEFINED");
                            break;
                        case INITIALIZING_EI:
                            previousView_number = R.layout.activity_primary_patient_details;
                            Actions.startCheckingInterface();
                            Log.e("EI_MODULE_STATUS", "Checking the EI modules in background. Please wait..");
                            break;
                        case INITIALIZATION_FAILED:
                            Actions.cantStartEIScreen();
                            Log.e("EI_MODULE_STATUS", "ERROR : Failed to initiate accessory modules.");
                            break;
                        case EI_MODULE_CONNECTED:
                            Actions.beginLensSettings();
                            Log.e("EI_MODULE_STATUS", "EIModuleStarted");
                            break;
                        case EI_MODULE_DISCONNECTED:
                            Log.e("EI_MODULE_STATUS", "EIModuleDisconnected");
                            Actions.cantStartEIScreen();
                        case SILENTLY_STARTED:
                            Log.e("EI_MODULE_STATUS", "SilentlyStarted");
                            break;
                        case SILENTLY_FAILED:
                            Log.e("EI_MODULE_STATUS", "SilentlyFailed");
                            break;
                    }
                }
                break;

            case R.id.lens_power_settings_continue_button:
                TextView editable_spherical_power_val = root.findViewById(R.id.editable_spherical_power);
                ImageView calculate = root.findViewById(R.id.lens_power_calc);
                if (TextUtils.isEmpty(editable_spherical_power_val.getText())) {
                    showTip = true;
                    tipId = R.id.editable_spherical_power;
                    Bundle b = new Bundle();
                    b.putInt("tipId", tipId);
                    b.putString("toastMessage", "Please enter spherical power");
                    showMeTip(b);
                } else {
                    calculate.performClick();
                    saveUI(root);
                    Log.e("CheckMe", "eyeTrackingStatus " + eyeTrackingStatus + " clickerStatus " + clickerStatus + " displayStatus " + displayStatus);
                    if (eyeTrackingStatus == EyeTrackingStatus.EYE_TRACKING_OK && clickerStatus == ClickerStatus.CLICKER_OK
                            && displayStatus == DisplayStatus.DISPLAY_OK) {
                        Actions.beginTestProfile();
                    } else {
                        Actions.showAccessoryCheckWaitScreen();
                    }
                }
                break;

            case R.id.start_test_button:
                Actions.beginIPD();
                break;

            case R.id.accessoryCheckOkay:
                Actions.beginTestProfile();
                break;
            case R.id.accessoryAbortTest:
                CommonUtils.abortTestConfirmationDialog(MainActivity.this);
                break;
            case R.id.accessoryContactSupport:
                CommonUtils.showContactDialog(this);
                break;

            case R.id.ipd_settings_continue_continue_button:
                if (ipdImageViewLinked) {
                    ipdImageViewLinked = false;
                    ipd_Image_Update_Thread.interrupt();
                }
                //Actions.beginCalibrationSettings();
                Actions.beginTest(false, "IPD continue");
                break;

            case R.id.calibration_continue_button:
                if (calibrationImageViewLinked) {
                    calibrationImageViewLinked = false;
                    Cl_Image_Update_Thread.interrupt();
                }
                resetCalibrationData();
                if (Store.toastMessage != null && !Store.toastMessage.equals("")) {
                    Log.d("ToastMessage", "not null");
                    boolean bool = Store.toastMessage.equals("Calibration done...");
                    Log.d("CalibDone", "" + bool);
                    Actions.beginTest(bool, "Calibration Continue if");
                } else {
                    Log.d("ToastMessage", "null");
                    Actions.beginTest(false, "Calibration Continue else");
                }
                break;

            case R.id.calibrateSkip_Button:
                resetCalibrationData();
                Actions.beginTest(false, "Calibration skip button");
                break;

            case R.id.calibrateRecalibrate_Button:
                Log.d("calibrateRecalibrate", "called");
                Button calibrateRecalibrate_Button = findViewById(R.id.calibrateRecalibrate_Button);
                String calibrateText = calibrateRecalibrate_Button.getText().toString();
                //calibrateText.equalsIgnoreCase("calibrate") ? "CALIBRATE" :
                calibrateRecalibrate_Button.setText(R.string.recalibrate);
                resetCalibrationData();
                Actions.doCalibration();
                break;

            case R.id.duringTestPause:
                TextView textView = root.findViewById(R.id.duringTestPause);
                Chronometer mChronometer = root.findViewById(R.id.duringTestChronometer);
                String text = textView.getText().toString();
                if (text.equals("Pause")) {
                    timeWhenStopped = mChronometer.getBase() - SystemClock.elapsedRealtime();
                    mChronometer.stop();
                    textView.setText(R.string.unpause);
                    Actions.pauseTest();
                } else {
                    textView.setText(R.string.pause);
                    mChronometer.setBase(SystemClock.elapsedRealtime() + timeWhenStopped);
                    mChronometer.start();
                    Actions.unPauseTest();
                }
                break;

            case R.id.duringTestAbort:
                Log.d("During Test", "Abort got pressed");
                /*appPreferencesHelper.setTimeBase(0);
                resetCalibrationData();
                Actions.abortTest();*/
                abortTestConfirmationDialog(MainActivity.this);
                break;

            case R.id.postTestRetest:
                Actions.beginTestProfile();
                break;

            case R.id.lens_power_calc:
                CommonUtils.hideKeypad(root, applicationContext);
                EditText editable_spherical_power = root.findViewById(R.id.editable_spherical_power);
                EditText editable_cylindrical_power = root.findViewById(R.id.editable_cylindrical_power);
                EditText editable_cylindrical_axis = root.findViewById(R.id.editable_cylindrical_axis);
                TextView readable_spherical_power = root.findViewById(R.id.readable_spherical_power);
                TextView readable_cylindrical_power = root.findViewById(R.id.readable_cylindrical_power);
                TextView readable_cylindrical_axis = root.findViewById(R.id.readable_cylindrical_axis);
                String sphericalTemp = editable_spherical_power.getText().toString();
                String cylindricalTemp = editable_cylindrical_power.getText().toString();
                String axisTemp = editable_cylindrical_axis.getText().toString();
                float spherical = Float.valueOf(nullCheck(sphericalTemp) ? sphericalTemp : "0");
                float cylindrical = Float.valueOf(nullCheck(cylindricalTemp) ? cylindricalTemp : "0");
                float axis = Float.valueOf(nullCheck(axisTemp) ? axisTemp : "0");
                if (spherical < -20 || spherical > 20) {
                    showToasty(this, "Spherical Power must between -20 to 20", true, 'E');
                } else if (cylindrical < -10 || cylindrical > 10) {
                    showToasty(this, "Cylindrical Power must between -10 to 10", true, 'E');
                } else if (axis < 0 || axis > 180) {
                    showToasty(this, "Cylindrical Axis must between 0 to 180", true, 'E');
                } else {
                    int age = Integer.parseInt(CommonUtils.getAge(appPreferencesHelper.getPatientDOB()));
                    Log.d("PowerCalc", "Age " + age);
                    Bundle bundle = CommonUtils.powerCalculation(spherical, cylindrical, axis, age);
                    Boolean cylinderVisibility = bundle.getBoolean("cylindricalVisibility");
                    Boolean axisVisibility = bundle.getBoolean("axisVisibility");

                    readable_spherical_power.setText(bundle.getString("calculatedSphericalValue"));
                    readable_cylindrical_power.setText(bundle.getString("calculatedCylindricalValue"));
                    readable_cylindrical_axis.setText(bundle.getString("calculatedAxis"));
                    saveUI(root);
                }
                break;

            case R.id.postTestSaveReport:
                AppPreferencesHelper resultAppPreferencesHelper = new AppPreferencesHelper(MainActivity.applicationContext, RESULT_PREF);
                String patName = resultAppPreferencesHelper.getSearchablePatientName();
                String patMrn = resultAppPreferencesHelper.getSearchablePatientMrNumber();
                String patMobile = resultAppPreferencesHelper.getSearchablePatientMobileNumber();
                String patDOB = resultAppPreferencesHelper.getSearchablePatientDOB();
                String patSex = resultAppPreferencesHelper.getSearchablePatientSex();
                Log.d("Save Sex", "" + patSex);
                String patEye = resultAppPreferencesHelper.getSearchablePatientEye();
                String testType = resultAppPreferencesHelper.getSearchableTestType();
                String testPattern = resultAppPreferencesHelper.getSearchableTestPattern();
                String data = resultAppPreferencesHelper.getResultData();
                String suggestion = "High Probability";

                new InsertRecordIntoDb(new AsyncDbInsertRecordTask() {
                    @Override
                    public int onProcessFinish(int b) {
                        return b;
                    }
                }).execute(patName, patMrn, patMobile, patDOB, patSex, patEye, testType, testPattern, suggestion, data, new Date().toString());
                showToast("Saved");
                v.setVisibility(View.INVISIBLE);
                break;

            case R.id.connection_lost_go_home:
                goHomeImmediately();
                break;

            case R.id.hmd_details:
                Actions.showHmdDetailsScreen();
                break;
        }

    }

    private static void decideNextUsingBatteryStatus() {
        if (Store.hmdBatteryStatus == BATTERY_LOW_PLEASE_CHARGE_HMD) {
            Actions.batteryNotOkayNotCharging();
            Log.e("EI_MODULE_STATUS", "Battery Level Low. Please charge headset");
        } else if (Store.hmdBatteryStatus == BATTERY_LOW_KEEP_CHARGING) {
            Actions.batteryLowKeepCharging();
        } else if (Store.hmdBatteryStatus == PLUG_OUT_CHARGE_OK) {
            Actions.plugOutChargeOkay();
        } else if (Store.hmdBatteryStatus == BATTERY_OK) {
            Actions.beginPatientDetails();
        }
    }

    private void checkCriticalUpdateAndContinue() {
        ipdImageViewLinked = false;
        testImageViewLinked = false;
        final AppPreferencesHelper devicePreferencesHelper = new AppPreferencesHelper(MainActivity.this, DEVICE_PREF);
        Log.e("ProductionSetup", "" + CommonUtils.isProductionSetUpFinished(this));
        Log.e("UserSetup", "" + CommonUtils.isUserSetUpFinished(this));
        Log.e("ProductionTesting", "" + devicePreferencesHelper.getProductionTestingStatus());

        if (CommonUtils.isProductionSetUpFinished(this) && CommonUtils.isUserSetUpFinished(this)) {
            if (newTestVisibility) {
                String bat = CommonUtils.splitHmdHeartBeat("Battery");
                if (Integer.parseInt(bat) <= 20) {
                    Actions.batteryNotOkayNotCharging();
                } else {
                    Actions.checkBattery();
                    Actions.beginTestDetails();
                }
            }
        } else if (CommonUtils.isProductionSetUpFinished(this) && !CommonUtils.isUserSetUpFinished(this)
                && !devicePreferencesHelper.getProductionTestingStatus()) {
            Actions.showPreProductionScreen();
        } else if (devicePreferencesHelper.getRole() == 3) {
            String bat = CommonUtils.splitHmdHeartBeat("Battery");
            if (Integer.parseInt(bat) <= 20) {
                Actions.batteryNotOkayNotCharging();
            } else {
                Actions.checkBattery();
                Actions.beginTestDetails();
            }
        }
    }

    public boolean isValidPassword(String password) {

        Pattern pattern;
        Matcher matcher;

        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).*$";

        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);

        return matcher.matches();

    }

    private boolean isPasswordEmpty(EditText password) {
        String enteredPwd = password.getText().toString();
        return enteredPwd.isEmpty();
    }

    private boolean isItNewPassword() {
        String pwd = enter_admin_password.getText().toString();
        String enteredPwd = re_enter_admin_password.getText().toString();
        return pwd.equals(enteredPwd);
    }

    private boolean isUserPasswordMatches(EditText enter_user_password, EditText re_enter_user_password) {
        String enteredPwd = enter_user_password.getText().toString();
        String re_enteredPwd = re_enter_user_password.getText().toString();
        return re_enteredPwd.equals(enteredPwd);
    }

    private boolean isAdminPasswordSizeQualifies(EditText password) {
        String enteredPwd = password.getText().toString();
        return enteredPwd.length() >= 8;
    }

    private boolean isUserPasswordSizeQualifies(EditText password) {
        String enteredPwd = password.getText().toString();
        return enteredPwd.length() == 4;
    }

    public void settingsClickCallback(final View v) {
        Log.d("SettingsClickCallback", " Called ");
        switch (v.getId()) {
            case R.id.hotspot_created_continue:
                CommonUtils.showAcknowlegmentDialog(this);
                break;
            case R.id.copy_hotspot_name:
                String serial = CommonUtils.isUserSetUpFinished(this) ? CommonUtils.getHotSpotId() : CommonUtils.getSavedDeviceId(this);
                String hotspotName = CommonUtils.getDetailsFromSerialNumber(serial, "DeviceId");
                ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Hotpsot Name", hotspotName);
                clipboard.setPrimaryClip(clip);
                Toast.makeText(this, "Hotspot Name Copied", Toast.LENGTH_SHORT).show();
                break;
            case R.id.copy_hotspot_password:
                ClipboardManager clipboard1 = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                ClipData clip1 = ClipData.newPlainText("Hotpsot Password", "agyohora");
                clipboard1.setPrimaryClip(clip1);
                Toast.makeText(this, "Hotspot Password Copied", Toast.LENGTH_SHORT).show();
                break;
            case R.id.open_hotspot_settings:
                //com.android.settings ClassName com.android.settings.SubSettings
                final Intent intent = new Intent(Intent.ACTION_MAIN, null);
                intent.addCategory(Intent.CATEGORY_LAUNCHER);
                final ComponentName cn = new ComponentName("com.android.settings", "com.android.settings.TetherSettings");
                intent.setComponent(cn);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                break;
            case R.id.hmd_sync_proceed_button:
                AppPreferencesHelper devicePreferencesHelper = new AppPreferencesHelper(MainActivity.this, DEVICE_PREF);
                if (!devicePreferencesHelper.getProductionSetUpStatus())
                    Actions.beginOtherSettings();
                else
                    Actions.setAdminPassword();
                break;
            case R.id.set_admin_password_proceed_button:
                String password = enter_admin_password.getText().toString();
                String re_password = re_enter_admin_password.getText().toString();
                if (password.equals(CommonUtils.getHotSpotId())) {
                    settingTemporaryPassword(true, password);
                } else {
                    settingPermanentPassword(true, password);
                }
                break;
            case R.id.set_user_password_proceed_button:
                String user_password = enter_user_password.getText().toString();
                String user_re_password = re_enter_user_password.getText().toString();
                if (user_password.equals("0000")) {
                    settingTemporaryPassword(false, user_password);
                } else {
                    settingPermanentPassword(false, user_password);
                }
                break;
            case R.id.wireless_go:
                CommonUtils.stopKiosk(MainActivity.this);
                startActivityForResult(new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK), 30);
                break;
            case R.id.wireless_skip:
                wifi_card.setVisibility(GONE);
                wifi_card_options.setVisibility(GONE);
                wifiDone = true;
                break;
            case R.id.email_go:
                CommonUtils.stopKiosk(MainActivity.this);
                startActivityForResult(new Intent(Settings.ACTION_ADD_ACCOUNT), 40);
                break;
            case R.id.email_skip:
                account_card.setVisibility(GONE);
                account_card_options.setVisibility(GONE);
                accountDone = true;
                break;
            case R.id.wired_printer_go:
                if (MyApplication.getInstance().is_OTG()) {
                    CommonUtils.stopKiosk(MainActivity.this);
                    Intent add_wired_printer_intent = new Intent(Settings.ACTION_PRINT_SETTINGS);
                    add_wired_printer_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(add_wired_printer_intent);
                } else
                    Toast.makeText(MainActivity.this, "Please attach the OTG cable and try again", Toast.LENGTH_SHORT).show();

                break;
            case R.id.wired_printer_skip:
                wired_printer_card.setVisibility(GONE);
                wired_printer_card_options.setVisibility(GONE);
                wiredPrinterDone = true;
                break;
            case R.id.proceed_button:
                //appPreferencesHelper.setDeviceConfigurationStatus(true);
                Intent mainIntent = new Intent(this, MainActivity.class);
                startActivity(mainIntent);
                break;
        }
        Log.d("wifiDone", "" + wifiDone);
        Log.d("accountDone", "" + accountDone);
        Log.d("wiredPrinterDone", "" + wiredPrinterDone);

        if (wifiDone && accountDone && wiredPrinterDone) {
            AppPreferencesHelper devicePreferencesHelper = new AppPreferencesHelper(applicationContext, DEVICE_PREF);
            if (!devicePreferencesHelper.getProductionSetUpStatus())
                devicePreferencesHelper.setProductionSetUpStatus(true);
            else
                devicePreferencesHelper.setUserSetUpStatus(true);
            Actions.beginHome();
        }
    }

    void settingPermanentPassword(boolean isThisAdmin, String password) {
        AppPreferencesHelper appPreferencesHelper = new AppPreferencesHelper(this.getApplicationContext(), DEVICE_PREF);
        String role = isThisAdmin ? "Admin" : "User";
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(new ContextThemeWrapper(this, R.style.myDialog));
        builder.setTitle("Message");
        builder.setMessage("You are setting the password which is need for " + role + " login after setup")
                .setCancelable(false)
                .setPositiveButton("Proceed", (dialog, id) -> {
                    if (isThisAdmin) {
                        appPreferencesHelper.setAdminPassword(password);
                        Actions.setUserPassword();
                    } else {
                        appPreferencesHelper.setUserPassword(password);
                        closeKeyboard();
                        Actions.beginOtherSettings();
                    }
                })
                .setNegativeButton("Cancel", (dialog, id) -> {

                });
        androidx.appcompat.app.AlertDialog alert = builder.create();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alert.show();
    }

    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    void settingTemporaryPassword(boolean isThisAdmin, String password) {
        AppPreferencesHelper appPreferencesHelper = new AppPreferencesHelper(this.getApplicationContext(), DEVICE_PREF);
        String role = isThisAdmin ? "Admin" : "User";
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(new ContextThemeWrapper(this, R.style.myDialog));
        builder.setTitle("Message");
        builder.setMessage("You are setting the temporary password which is needed to change at first login for " + role + " role")
                .setCancelable(false)
                .setPositiveButton("Proceed", (dialog, id) -> {
                    if (isThisAdmin) {
                        appPreferencesHelper.setAdminPassword(password);
                        Actions.setUserPassword();
                    } else {
                        appPreferencesHelper.setUserPassword(password);
                        Actions.beginOtherSettings();
                    }
                })
                .setNegativeButton("Cancel", (dialog, id) -> {

                });
        androidx.appcompat.app.AlertDialog alert = builder.create();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alert.show();
    }

    public void HMDSettingsClickCallback(final View v) {
        AppPreferencesHelper preferencesHelper = new AppPreferencesHelper(this.getApplicationContext(), DEVICE_PREF);
        switch (v.getId()) {
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
                    turnOffOrRebootHMD(this);
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
                if (!configFileStatus) {
                    if (isConfigFileCopied) {
                        preferencesHelper.putConfigFileCopiedStatus(true);
                        CommonUtils.showToasty(this, "Config File Copied Successfully", true, 'I');
                    } else {
                        CommonUtils.showToasty(this, "Config File Not Copied", true, 'E');
                    }
                }
                if (!vectorFileStatus) {
                    if (isVectorFileCopied) {
                        if (CommonUtils.readVector() != null) {
                            //if (CommonUtils.deleteDirectory(new File(vectorInputPath, vectorFileName))) {
                            if (true) {
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

    public void openSettings(View view) {
        switch (view.getId()) {
            case R.id.checkForUpdatesButton:
                Actions.setCheckForUpdateScreen();
                break;
            case R.id.add_wireless_network_card:
                Log.d("TcSettings", "add_wireless_network_card");
                startActivityForResult(new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK), 30);
                break;
            case R.id.add_email_account_card:
                Log.d("TcSettings", "add_email_account_card");
                startActivityForResult(new Intent(Settings.ACTION_ADD_ACCOUNT), 40);
                break;
            case R.id.add_printer_card:
                Log.d("TcSettings", "add_wired_printer_card");
                stopKiosk(this);
                Intent add_wireless_printer_intent = new Intent(Settings.ACTION_PRINT_SETTINGS);
                //add_wireless_printer_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivityForResult(add_wireless_printer_intent, 50);
                break;
            case R.id.check_for_app_updates_card:
                isHmdNotConnectedYet = false;
                isUpdateNotDoneYet = false;
                isHMDConnected = false;
                Actions.setCheckForUpdateScreen();

                break;
            case R.id.profile_card:
                AppPreferencesHelper preferencesHelper = new AppPreferencesHelper(this.getApplicationContext(), DEVICE_PREF);
                if (preferencesHelper.getRole() == 1) {
                    startActivity(new Intent(this, AdminProfileActivity.class));
                } else if (preferencesHelper.getRole() == 2) {
                    startActivity(new Intent(this, UserProfileActivity.class));
                } else if (preferencesHelper.getRole() == 3) {
                    startActivity(new Intent(this, ServiceProfileActivity.class));
                } else if (preferencesHelper.getRole() == 0) {
                    Toast.makeText(applicationContext, "Please close the app and Login first.", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public void getIsRestoreDatabaseAvailable() {
        AndroidNetworking.get(CommonUtils.generateDatabaseDownloadLink())
                .build()
                .getAsOkHttpResponse(new OkHttpResponseListener() {
                    @Override
                    public void onResponse(Response response) {
                        Log.e("CheckMe", "Success " + response.isSuccessful());
                        Actions.showDatabaseAvailable();
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e("CheckMe", "Error " + anError.getErrorBody());
                        Toast.makeText(applicationContext, "No Database Available!", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(MainActivity.this, AdminProfileActivity.class));
                    }
                });
    }

    public void getUpdateDetails() {
        String dId = CommonUtils.isUserSetUpFinished(this) ? CommonUtils.getHotSpotId() : CommonUtils.getSavedDeviceId(this);
        AndroidNetworking.get(ApiEndPoint.UPDATE_SERVER_ENDPOINT)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray jsonArray) {
                        try {
                            JSONObject jsonObject = new JSONObject();
                            boolean isDeviceIdFound = false;
                            String dId = CommonUtils.isUserSetUpFinished(MainActivity.this) ? CommonUtils.getHotSpotId() : CommonUtils.getSavedDeviceId(MainActivity.this);

                            for (int i = 0; i < jsonArray.length(); i++) {
                                jsonObject = (JSONObject) jsonArray.get(i);
                                String deviceId = jsonObject.getString("DeviceId");
                                Log.e("Searching", "DevId " + deviceId);
                                if (deviceId.equals(dId)) {
                                    isDeviceIdFound = true;
                                    break;
                                }
                                Log.e("Searching", "DevId " + i);
                            }

                            if (isDeviceIdFound) {
                                final String TcLatestVersionName = jsonObject.getString("TcLatestVersion").trim();
                                final int TcLatestVersion = jsonObject.optInt("TcLatestVersionCode");
                                final String HmdLatestVersionName = jsonObject.getString("HmdLatestVersion").trim();
                                final int HmdLatestVersion = jsonObject.optInt("HmdLatestVersionCode");
                                boolean isUpdateAvailable = CommonUtils.isUpdateAvailable(TcLatestVersionName, TcLatestVersion, HmdLatestVersionName, HmdLatestVersion, MainActivity.this);
                                if (isUpdateAvailable) {
                                    AppPreferencesHelper devicePreferencesHelper = new AppPreferencesHelper(MainActivity.this, DEVICE_PREF);


                                    String TC_URL = jsonObject.getString("TC_URL").trim();
                                    String HMD_URL = jsonObject.getString("HMD_URL").trim();

                                    devicePreferencesHelper.setTCDownloadLink(TC_URL);
                                    devicePreferencesHelper.setHmdDownloadLink(HMD_URL);

                                    devicePreferencesHelper.setLatestTcVersionCode(TcLatestVersion);
                                    devicePreferencesHelper.setLatestHmdVersionCode(HmdLatestVersion);

                                    devicePreferencesHelper.setLatestTcVersionName(TcLatestVersionName);
                                    devicePreferencesHelper.setLatestHmdVersionName(HmdLatestVersionName);

                                    // CommonUtils.newVersionAvailabe(MainActivity.this, TcLatestVersionName, TcLatestVersion);
                                    Actions.showUpdateAvailable();
                                } else {
                                    Actions.setSoftwareUptoDateScreen();
                                }
                            } else {
                                Actions.setSoftwareUptoDateScreen();
                            }

                        } catch (Exception e) {
                            Log.e("Exception", e.getMessage());
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e("Exception", anError.getErrorDetail());
                    }
                });

    }

    private void tryAgainUI(String calledFrom) {
        if (activeView_number == R.layout.download_update_screen) {
            TextView connectingHMD = findViewById(R.id.connectingHMD);
            ProgressBar connectingHMDProgress = findViewById(R.id.connectingHMDProgress);
            LinearLayout buttonPanel = findViewById(R.id.buttonPanel);
            LinearLayout keepDevicesSwitchedOn = findViewById(R.id.keepDevicesSwitchedOn);
            TextView proceedingToInstallation = findViewById(R.id.proceedingToInstallation);

            connectingHMD.setText("Connecting to HMD");
            connectingHMD.setVisibility(View.VISIBLE);
            connectingHMDProgress.setVisibility(View.VISIBLE);
            buttonPanel.setVisibility(GONE);
            keepDevicesSwitchedOn.setVisibility(GONE);
            proceedingToInstallation.setVisibility(GONE);
        }
    }

    private void couldNotConnectToHMD(String calledFrom) {
        if (activeView_number == R.layout.download_update_screen) {
            TextView connectingHMD = findViewById(R.id.connectingHMD);
            ProgressBar connectingHMDProgress = findViewById(R.id.connectingHMDProgress);
            LinearLayout buttonPanel = findViewById(R.id.buttonPanel);
            LinearLayout keepDevicesSwitchedOn = findViewById(R.id.keepDevicesSwitchedOn);
            TextView proceedingToInstallation = findViewById(R.id.proceedingToInstallation);

            connectingHMD.setText("Could not connect to HMD");
            connectingHMD.setVisibility(View.VISIBLE);
            connectingHMDProgress.setVisibility(View.GONE);
            buttonPanel.setVisibility(GONE);
            keepDevicesSwitchedOn.setVisibility(View.VISIBLE);
            proceedingToInstallation.setVisibility(GONE);
        }
    }


    private void updateDownloadUI(String calledFrom) {
        if (activeView_number == R.layout.download_update_screen) {
            TextView connectingHMD = findViewById(R.id.connectingHMD);
            ProgressBar connectingHMDProgress = findViewById(R.id.connectingHMDProgress);
            LinearLayout buttonPanel = findViewById(R.id.buttonPanel);
            LinearLayout keepDevicesSwitchedOn = findViewById(R.id.keepDevicesSwitchedOn);
            TextView proceedingToInstallation = findViewById(R.id.proceedingToInstallation);
            if (isHmdNotConnectedYet) {
                connectingHMD.setText("Could Not connect to HMD");
                connectingHMD.setVisibility(View.VISIBLE);
                connectingHMDProgress.setVisibility(GONE);
                buttonPanel.setVisibility(View.VISIBLE);
                keepDevicesSwitchedOn.setVisibility(View.VISIBLE);
                proceedingToInstallation.setVisibility(GONE);
            } else if (isHMDConnected) {
                connectingHMD.setText("Connected to HMD");
                connectingHMD.setVisibility(View.VISIBLE);
                connectingHMDProgress.setVisibility(GONE);
                buttonPanel.setVisibility(View.GONE);
                keepDevicesSwitchedOn.setVisibility(View.VISIBLE);
                proceedingToInstallation.setVisibility(View.VISIBLE);
                proceedingToInstallation.setText("Proceeding to installation");
                new CountDownTimer(30000, 1000) {

                    public void onTick(long millisUntilFinished) {
                        proceedingToInstallation.setText("Proceeding to installation in " + millisUntilFinished / 1000 + " seconds");
                        //here you can have your logic to set text to edittext
                    }

                    public void onFinish() {
                        Actions.beginHMDUpdate();
                    }

                }.start();
            }
        }
    }


    void closeTheApp() {
        finish();
        finishAffinity();
        System.exit(0);
        ActivityManager mActivityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        mActivityManager.killBackgroundProcesses("com.agyohora.mobileperitc");
    }

    private void updateUI(String calledFrom) {
        try {
            Log.e("updateUI", "Called from " + calledFrom);
            final AppPreferencesHelper devicePreferenceHelper = new AppPreferencesHelper(this.getApplicationContext(), DEVICE_PREF);
            Bundle state = getState();
            activeViewID = state.getInt("viewID");
            Log.d("ActiveViewId", " " + getResources().getResourceName(activeViewID));
            if (activeViewID != prevViewID) {
                setContentView(activeViewID);
                prevViewID = activeViewID;
            }

            switch (activeViewID) {

                case R.layout.check_for_updates_screen:
                    if (CommonUtils.isNetworkConnected(this)) {
                        getUpdateDetails();
                    } else {
                        CommonUtils.initiateNetworkOptions(this, this, "applicable");
                    }
                    break;
                case R.layout.checking_for_database_in_remote:
                    databaseDownloadStatus = Status.UNKNOWN;
                    if (CommonUtils.isNetworkConnected(this)) {
                        getIsRestoreDatabaseAvailable();
                    } else {
                        CommonUtils.initiateNetworkOptions(this, this, "applicable");
                    }
                    break;
                case R.layout.database_restore_available:
                    break;
                case R.layout.restore_database_finished:
                    TextView merging_helper_text = findViewById(R.id.merging_helper_text);
                    int recordMismatched = state.getInt("recordMismatched");
                    int recordsInserted = state.getInt("recordsInserted");
                    int recordsDuplicated = state.getInt("recordsDuplicated");
                    int totalNumberOfRecords = state.getInt("totalNumberOfRecords");
                    String success = "Database is successfully merged. ";
                    String totalRecords = "Total Number of records = " + totalNumberOfRecords;
                    String misMatchedMsg = "Total Errors = " + recordMismatched;
                    String insertedMsg = "Records Inserted = " + recordsInserted;
                    String duplicatedMsg = "Duplicate Records = " + recordsDuplicated;
                    String elisar = "Elisar Service Team will contact you on further decision. ";
                    if (recordMismatched > 0) {
                        String msg = totalRecords + "\n" + insertedMsg + "\n" + misMatchedMsg + "\n" + elisar;
                        merging_helper_text.setText(msg);
                    } else {
                        String msg = totalRecords + "\n Records inserted = " + totalNumberOfRecords + "\n" + success;
                        merging_helper_text.setText(msg);
                        /*if (recordsInserted > 0 && recordsDuplicated > 0) {
                            String msg = totalRecords + "\n Records inserted = " + totalNumberOfRecords;
                            merging_helper_text.setText(msg);
                        } else if (recordsInserted > 0) {
                            merging_helper_text.setText(success);
                        }*/
                    }
                    break;
                case R.layout.closing_the_app:
                    closeTheApp();
                    break;

                case R.layout.software_upto_date:
                    TextView currentVersion = findViewById(R.id.currentVersion);
                    TextView lastCheckOn = findViewById(R.id.lastCheckOn);
                    String version = CommonUtils.getVersionName();
                    version = "Current version : " + version;
                    String currentTime = CommonUtils.gerCurrentTime();
                    currentTime = "Last successful check for update at " + currentTime;
                    currentVersion.setText(version);
                    lastCheckOn.setText(currentTime);
                    break;

                case R.layout.software_update_available:
                    TextView newVersion = findViewById(R.id.newVersion);
                    AppPreferencesHelper devicePreferencesHelper = new AppPreferencesHelper(MainActivity.this, DEVICE_PREF);
                    String newVersionData = devicePreferencesHelper.getLatestTcVersionName() + " is available";
                    newVersion.setText(newVersionData);
                    break;
                case R.layout.connected_to_hmd_during_update:
                    TextView proceedingToInstall = findViewById(R.id.proceedingToInstallation);
                    proceedingToInstall.setVisibility(View.VISIBLE);
                    new CountDownTimer(5000, 1000) {

                        public void onTick(long millisUntilFinished) {
                            proceedingToInstall.setText("Proceeding to installation in " + millisUntilFinished / 1000 + " seconds");
                            //here you can have your logic to set text to edittext
                        }

                        public void onFinish() {
                            Actions.beginHMDUpdate();
                        }

                    }.start();
                    break;
                case R.layout.connected_to_hmd_during_hmd_version_mismatch:
                    TextView proceedingToInstall_HMD = findViewById(R.id.proceedingToInstallation);
                    proceedingToInstall_HMD.setVisibility(View.VISIBLE);
                    new CountDownTimer(5000, 1000) {
                        public void onTick(long millisUntilFinished) {
                            proceedingToInstall_HMD.setText("Proceeding to installation of HMD in " + millisUntilFinished / 1000 + " seconds");
                        }

                        public void onFinish() {
                            Actions.beginHMDUpdateAlone();
                        }
                    }.start();
                    break;
                case R.layout.connected_to_hmd_during_tc_version_mismatch:
                    TextView proceedingToInstall_TC = findViewById(R.id.proceedingToInstallation);
                    proceedingToInstall_TC.setVisibility(View.VISIBLE);
                    new CountDownTimer(5000, 1000) {
                        public void onTick(long millisUntilFinished) {
                            proceedingToInstall_TC.setText("Proceeding to installation of TC in " + millisUntilFinished / 1000 + " seconds");
                        }

                        public void onFinish() {
                            Actions.beginTcUpdateAlone();
                        }
                    }.start();
                    break;

                case R.layout.installing_test_controller:
                    AppPreferencesHelper appPreferencesHelper1 = new AppPreferencesHelper(this.getApplicationContext(), DEVICE_PREF);
                    appPreferencesHelper1.setLastUpdatedOn(CommonUtils.gerCurrentTime());
                    Log.e("isUpdateCalled", "isUpdateCalled " + isUpdateCalled);
                    if (!isUpdateCalled) {
                        isUpdateCalled = true;
                        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
                            isTcUpdateSuccess[0] = CommonUtils.packageInstaller(MainActivity.this);
                        else
                            CommonUtils.packageInstallerForQ(this);
                    }/* else if (!isTcUpdateSuccess[0]) {
                        dialogThree();
                    }*/
                    break;

                case R.layout.new_hmd_details_activity:
                    TextView hmd_id = findViewById(R.id.hmd_id);
                    TextView org_id = findViewById(R.id.org_id);
                    sync_details_now = findViewById(R.id.sync_details_now);
                    production_test_done_bar = findViewById(R.id.production_test_done_bar);
                    production_test_done = findViewById(R.id.production_test_done);
                    note = findViewById(R.id.note);
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
                    if (!devicePreferenceHelper.getProductionTestingStatus()) {
                        production_test_done_bar.setVisibility(View.VISIBLE);
                        production_test_done.setVisibility(View.VISIBLE);
                    }
                    new UpdateVisibilityAsyncTask(this).execute();
                    break;

                case R.layout.test_controller_settings:
                    AppPreferencesHelper appPreferencesHelper = new AppPreferencesHelper(this.getApplicationContext(), DEVICE_PREF);
                    String installedTCVersionName = CommonUtils.getAppInstalledVersion(this);
                    TextView version_details = findViewById(R.id.tc_version_details);
                    TextView lastUpdatedOn = findViewById(R.id.lastUpdatedOn);
                    lastUpdatedOn.setText(appPreferencesHelper.updatedOn());
                    TextView profileText = findViewById(R.id.profileText);
                    String tc = "Version : " + installedTCVersionName;
                    version_details.setText(tc);
                    int role = appPreferencesHelper.getRole();
                    if (role == 1) {
                        profileText.setText("Admin Profile");
                    } else if (role == 2) {
                        profileText.setText("User Profile");
                    } else if (role == 3) {
                        profileText.setText("Service Profile");
                    } else {
                        profileText.setText("");
                    }
                    break;

                case R.layout.download_database:
                    Button restart_database_download = findViewById(R.id.restart_database_download);
                    TextView database_percentage = findViewById(R.id.database_percentage);
                    ProgressBar database_progress = findViewById(R.id.database_progress);
                    if (databaseDownloadStatus != Status.COMPLETED) {
                        downloadDatabase(database_percentage, database_progress, restart_database_download);
                    }
                    break;

                case R.layout.merging_database:
                    new MergeDatabase(bool -> {
                        Log.e("MergeDatabase", "Executed");
                    }, this).execute();
                    break;

                case R.layout.uploading_database_restore_logs:
                    FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
                    StorageReference storageRef = firebaseStorage.getReference();

                    StorageReference mismatchFileRef = storageRef.child(CommonUtils.getFilePath());
                    Uri file = Uri.fromFile(new File(DATABASE_RESTORE_LOGS_FOLDER + "logs.txt"));

                    UploadTask uploadTask = mismatchFileRef.putFile(file);

                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            Log.e("Exception", " " + exception.getMessage());
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Actions.dataRestoreFinished();
                            Log.e("OnSuccess", "of storage " + taskSnapshot.getMetadata().getName());
                        }
                    });
                    break;

                case R.layout.download_update_screen:
                    Button restart_tc_download = findViewById(R.id.restart_tc_download);
                    Button restart_hmd_download = findViewById(R.id.restart_hmd_download);
                    TextView tc_percentage = findViewById(R.id.tc_percentage);
                    ProgressBar tc_progress = findViewById(R.id.tc_progress);
                    ProgressBar hmd_progress = findViewById(R.id.hmd_progress);
                    TextView hmd_percentage = findViewById(R.id.hmd_percentage);

                    TextView connectingHMD = findViewById(R.id.connectingHMD);
                    ProgressBar connectingHMDProgress = findViewById(R.id.connectingHMDProgress);
                    LinearLayout buttonPanel = findViewById(R.id.buttonPanel);


                    Log.e("enableHMDUpdate", " tcDownloadStatus " + tcDownloadStatus + " hmdDownloadStatus " + hmdDownloadStatus);
                    if (tcDownloadStatus == Status.COMPLETED && hmdDownloadStatus == Status.COMPLETED) {
                        connectingHMDProgress.setVisibility(View.VISIBLE);
                        connectingHMD.setVisibility(View.VISIBLE);
                    } else {
                        downloadTCUpdate(tc_percentage, tc_progress, restart_tc_download, devicePreferenceHelper);
                        downloadHmdUpdate(hmd_percentage, hmd_progress, restart_hmd_download, devicePreferenceHelper);
                        connectingHMDProgress.setVisibility(GONE);
                        connectingHMD.setVisibility(GONE);
                        buttonPanel.setVisibility(GONE);
                    }

                    break;


                case R.layout.error_cant_start_ei:
                    if (state.getBoolean("showToasty")) {
                        Log.d("Toast", "toast is going to be shown");
                        showToast(state.getString("toastMessage"));
                        Actions.resetToast();
                    }
                    break;

                case R.layout.hmd_update_tranfer_screen:
                    TextView message = findViewById(R.id.message);
                    message.setText(state.getString("transferMessage"));
                    break;

                case R.layout.installing_hmd_software:
                    updateHandler.postDelayed(threeMinuteTimerRunnable, 3 * 60 * 1000);
                    break;

                case R.layout.hmd_sync_check_activity:

                    boolean isHotspotOn = isHotspotOn(MyApplication.getInstance());
                    Log.e("isHotspotOn", " " + isHotspotOn);
                    Log.e("isCommInitOver", " " + isCommInitializationOver);
                    Log.e("isHmdConnected", " " + state.getBoolean("isHmdConnected"));
                    CardView hmd_card = findViewById(R.id.hmd_card);
                    CardView sync_card = findViewById(R.id.sync_card);

                    TextView hotspot_status = findViewById(R.id.hotspot_status);
                    TextView hmd_status = findViewById(R.id.hmd_status);
                    TextView sync_status = findViewById(R.id.sync_status);
                    Button proceed_button = findViewById(R.id.hmd_sync_proceed_button);

                    findViewById(R.id.stepOne).setBackground(getDrawable(R.drawable.progress_bar_blue));

                    if (!isCommInitializationOver) {
                        Actions.initCommunication();
                        if (!hmdSyncHandlerRunning)
                            h.post(hmdSyncCheck);
                        hmdSyncHandlerRunning = true;

                    } else {
                        Log.e("sync_check_activity", "hotspot  on");
                        if (state.getBoolean("isHmdConnected")) {
                            if (!devicePreferenceHelper.getFirstTimeProductionConnection()) {
                                Log.e("sync_check_activity", "hmd is connected and production sync is initiated");
                                productionSyncInitiated = true;
                                Actions.beginProductionSync();
                            } else if (!devicePreferenceHelper.getFirstTimeUserConnection() && !productionSyncInitiated) {
                                Log.e("sync_check_activity", "hmd is connected and user sync is initiated");
                                Actions.beginUserSync();
                            }
                        } else if (!MyApplication.getInstance().is_HMD_CONNECTED()) {
                            Log.e("sync_check_activity", "hmd is not connected");
                            showBigToast(Toast.makeText(applicationContext, "Please make sure that HMD is switched on", Toast.LENGTH_SHORT));
                        }

                    }

                    hotspot_status.setText(state.getString("hotspotstatus"));
                    hmd_status.setText(state.getString("hmdstatus"));
                    sync_status.setText(state.getString("syncstatus"));
                    hmd_card.setVisibility(state.getBoolean("hmdVisibility") ? View.VISIBLE : View.INVISIBLE);
                    sync_card.setVisibility(state.getBoolean("syncVisibility") ? View.VISIBLE : View.INVISIBLE);
                    proceed_button.setVisibility(state.getBoolean("syncVisibility") && devicePreferenceHelper.getFirstTimeProductionConnection() ? View.VISIBLE : View.INVISIBLE);
                    break;
                case R.layout.other_settings_activity:
                    h.removeCallbacks(hmdSyncCheck);
                    wifi_card = findViewById(R.id.add_wireless_network_card);
                    account_card = findViewById(R.id.add_email_account_card);
                    wired_printer_card = findViewById(R.id.add_printer_card);
                    findViewById(R.id.stepOne).setBackground(getDrawable(R.drawable.progress_bar_blue));
                    findViewById(R.id.stepTwo).setBackground(getDrawable(R.drawable.progress_bar_blue));
                    wifi_card_options = findViewById(R.id.add_wireless_network_card_options);
                    account_card_options = findViewById(R.id.add_email_account_card_options);
                    wired_printer_card_options = findViewById(R.id.add_wired_printer_card_options);
                    break;

                case R.layout.set_admin_password_during_setup:
                    enter_admin_password = findViewById(R.id.enter_admin_password);
                    enter_admin_password.addTextChangedListener(enter_admin_password_Watcher);
                    re_enter_admin_password = findViewById(R.id.re_enter_admin_password);
                    re_enter_admin_password.addTextChangedListener(re_enter_admin_password_Watcher);
                    enter_admin_password_layout = findViewById(R.id.enter_admin_password_layout);
                    re_enter_admin_password_layout = findViewById(R.id.re_enter_password_layout);
                    set_admin_password_proceed_button = findViewById(R.id.set_admin_password_proceed_button);
                    break;

                case R.layout.set_user_password_during_setup:
                    enter_user_password = findViewById(R.id.enter_user_password);
                    enter_user_password.addTextChangedListener(enter_user_password_Watcher);
                    re_enter_user_password = findViewById(R.id.re_enter_user_password);
                    re_enter_user_password.addTextChangedListener(re_enter_user_password_Watcher);
                    enter_user_password_layout = findViewById(R.id.enter_user_password_layout);
                    re_enter_user_password_layout = findViewById(R.id.re_enter_user_password_layout);
                    set_user_password_proceed_button = findViewById(R.id.set_user_password_proceed_button);
                    break;

                case R.layout.activity_home_screen:
                    try {
                        calibrationRetryCounter = 0;
                        beginTestRetryCounter = 0;
                        h.removeCallbacks(waitScreenDelay);

                        Button new_test = findViewById(R.id.new_test);

                        fabParent = findViewById(R.id.fabParent);
                        batteryFabLayout = findViewById(R.id.batteryFabLayout);
                        fab = findViewById(R.id.fab);
                        batteryLevel = findViewById(R.id.batteryLevel);

                        boolean isHmdActive = CommunicationService.isHMDConnected;
                        boolean isHMDConnectionNeeded = MyApplication.getInstance().is_HMD_CONNECTION_NEEDED();

                        Log.e("activity_home_screen", "Reached");
                        Log.e("is_HMD_CONNECTED", " " + MyApplication.getInstance().is_HMD_CONNECTED());
                        Log.e("newTestVisibility", "" + newTestVisibility);
                        Log.e("isHotSpotOn", "" + isHotSpotOn);
                        Log.e("isCommInitOver", "" + isCommInitializationOver);
                        Log.e("communicationActive", "" + communicationActive);
                        /*if (isCommInitializationOver) {
                            if (!isHotSpotOn) {
                                menu.getItem(2).setIcon(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_wifi_tethering_off));
                            }
                        }*/
                        if (newTestVisibility) {
                            if (CommonUtils.is_TC_HMD_Version_Matches()) {
                                new_test.setTextColor(getResources().getColor(R.color.primaryTwo));
                                new_test.setEnabled(true);
                                fabParent.setVisibility(View.VISIBLE);
                            } else {
                                new_test.setTextColor(getResources().getColor(R.color.reportGrey));
                                new_test.setEnabled(true);
                                fabParent.setVisibility(View.VISIBLE);
                            }
                            if (CommonUtils.returnClickCount() >= 1000000) {
                                new_test.setTextColor(getResources().getColor(R.color.reportGrey));
                                new_test.setEnabled(true);
                                fabParent.setVisibility(View.VISIBLE);
                            }
                        } else {
                            new_test.setTextColor(getResources().getColor(R.color.reportGrey));
                            new_test.setEnabled(false);
                            fabParent.setVisibility(View.INVISIBLE);
                        }

                        if (!newTestVisibility && isHMDConnectionNeeded)
                            Actions.initCommunication();

                    } catch (Exception e) {
                        Log.e("Exception", e.getMessage());
                        FirebaseCrashlytics.getInstance().recordException(e);
                    }

                    break;

                case R.layout.activity_waitscreen_accessory_status:
                    ImageView ggif = findViewById(R.id.loadingGif);
                    Glide.with(this).asGif().load(R.raw.ava_loading_new).into(ggif);
                    waitScreenDelayedAction(14000);
                    break;

                case R.layout.accessories_status:
                    h.removeCallbacks(waitScreenDelay);
                    TextView eyeTracking_Status = findViewById(R.id.eyeTrackingStatus);
                    TextView clicker_status = findViewById(R.id.clicker_status);
                    TextView display_status = findViewById(R.id.display_status);
                    Button accessoryCheckOkay = findViewById(R.id.accessoryCheckOkay);
                    Button accessoryContactSupport = findViewById(R.id.accessoryContactSupport);
                    Button accessoryAbortTest = findViewById(R.id.accessoryAbortTest);
                    CardView accessory_error_cardView = findViewById(R.id.accessory_error_cardView);
                    TextView accessory_error_textView = findViewById(R.id.accessory_error_textView);
                    Log.e("checking_accessories", accessoriesChecked + " cameraStatus -> " + eyeTrackingStatus + " clickerStatus -> " + clickerStatus + " displayStatus -> " + displayStatus);

                    //EyeTracking Status
                    if (eyeTrackingStatus == EyeTrackingStatus.EYE_TRACKING_OK) {
                        eyeTracking_Status.setText(R.string.eye_tracking_okay);
                    } else if (eyeTrackingStatus == EyeTrackingStatus.EYE_TRACKING_NOT_OK) {
                        eyeTracking_Status.setText(R.string.eye_tracking_not_okay);
                        //h.postDelayed(lookForDisplayUpdate, 8000);
                    } else if (eyeTrackingStatus == EyeTrackingStatus.UNDEFINED) {
                        eyeTracking_Status.setText(R.string.eye_tracking_not_okay);
                    }

                    //Clicker Status
                    if (clickerStatus == ClickerStatus.CLICKER_OK) {
                        clicker_status.setText(R.string.clicker_okay);
                    } else if (clickerStatus == ClickerStatus.CLICKER_NOT_OK) {
                        clicker_status.setText(R.string.clicker_not_okay);
                    } else if (clickerStatus == ClickerStatus.UNDEFINED) {
                        clicker_status.setText(R.string.clicker_unknown);
                    }

                    //Display Status
                    String pd1Message = Store.isPhotoDiode1Working ? "1" : "0";
                    String pd2Message = Store.isPhotoDiode2Working ? "1" : "0";
                    pd2Message = pd2Message + String.format("%04d", (int) Store.screenBrightnessLevelPD2);
                    pd1Message = pd1Message + String.format("%04d", (int) Store.screenBrightnessLevelPD1);
                    int displayStatusMessage = decideDisplayStatusMessage();
                    display_status.setText(displayStatusMessage);

                    String errorCode = decideErrorCode();
                    Log.e("DecidedErrorCode", " " + errorCode);

                    if (errorCode.equalsIgnoreCase("All_set")) {
                        accessory_error_cardView.setVisibility(View.INVISIBLE);

                        accessoryCheckOkay.setVisibility(View.VISIBLE);
                        accessoryContactSupport.setVisibility(View.INVISIBLE);
                        accessoryAbortTest.setVisibility(View.INVISIBLE);
                    } else if (errorCode.equalsIgnoreCase("na")) {
                        accessory_error_cardView.setVisibility(View.INVISIBLE);

                        accessoryCheckOkay.setVisibility(View.INVISIBLE);
                        accessoryContactSupport.setVisibility(View.INVISIBLE);
                        accessoryAbortTest.setVisibility(View.VISIBLE);
                    } else if (errorCode.equalsIgnoreCase("1_002")) {
                        accessoryCheckOkay.setText("Proceed Anyway");
                        accessoryCheckOkay.setVisibility(View.VISIBLE);
                        accessory_error_cardView.setVisibility(View.VISIBLE);
                        accessory_error_textView.setText("Error Code: " + errorCode + " Message from PD " + pd1Message + "_" + pd2Message + "\n" + getResources().getString(R.string.contact_customer_care));
                    } else {
                        accessory_error_cardView.setVisibility(View.VISIBLE);
                        accessory_error_textView.setText("Error Code: " + errorCode + " Message from PD " + pd1Message + "_" + pd2Message + "\n" + getResources().getString(R.string.contact_customer_care));

                        accessoryCheckOkay.setVisibility(View.INVISIBLE);
                        accessoryContactSupport.setVisibility(View.INVISIBLE);
                        accessoryAbortTest.setVisibility(View.VISIBLE);
                    }

                    if (state.getBoolean("showToasty")) {
                        showToast(state.getString("toastMessage"));
                        Actions.resetToast();
                    }

                    FirebaseCrashlytics.getInstance().recordException(new AccessoryStatusException("AccessoryStatus: EyeTracker " + eyeTrackingStatus + " Clicker " + clickerStatus + " DisplayStatus " + displayStatus));

                    break;
                case R.layout.activity_waitscreen_test_profile:
                    ImageView load = findViewById(R.id.loadingGif);
                    Glide.with(this).asGif().load(R.raw.ava_loading_new).into(load);
                    h.removeCallbacks(waitScreenDelay);
                    waitScreenDelayedAction(10000);
                    break;

                case R.layout.response_waitscreen_accessory_abort:
                case R.layout.activity_waitscreen_ipd_live_feed:
                case R.layout.response_waitscreen_abort_calibration:
                case R.layout.activity_waitscreen_during_test:
                case R.layout.activity_waitscreen_calibration_live_feed:
                case R.layout.response_waitscreen_abort_during_test:
                case R.layout.response_waitscreen_abort_ipd:
                    ImageView abortLoading = findViewById(R.id.loadingGif);
                    Glide.with(this).asGif().load(R.raw.ava_loading_new).into(abortLoading);

                    if (activeViewID == R.layout.response_waitscreen_abort_during_test ||
                            activeViewID == R.layout.response_waitscreen_accessory_abort ||
                            activeViewID == R.layout.activity_waitscreen_ipd_live_feed ||
                            activeViewID == R.layout.activity_waitscreen_calibration_live_feed ||
                            activeViewID == R.layout.response_waitscreen_abort_ipd
                    ) {
                        waitScreenDelayedAction(15000);
                    } else {
                        waitScreenDelayedAction(10000);
                    }
                    break;


                case R.layout.checking_interfaces_screen:
                    ImageView error_bno_charging = findViewById(R.id.loadingGif);
                    Glide.with(this).asGif().load(R.raw.ava_loading_new).into(error_bno_charging);
                    break;


                case R.layout.activity_primary_patient_details:
                    h.removeCallbacks(waitScreenDelay);
                    try {
                        LinearLayout front = findViewById(R.id.front_view);
                        front.setClipToOutline(true);
                        EditText patientFirstName = findViewById(R.id.patient_FirstName_editText_value);
                        EditText patientMRNNumber = findViewById(R.id.patient_mrn_number_editText_value);
                        patientMRNNumber.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                            @Override
                            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                                    clickCallback(findViewById(R.id.searchMrn));
                                    return true;
                                }
                                return false;
                            }
                        });

                        if (new AppPreferencesHelper(applicationContext, PREF_NAME).getPatientDetailsViewVisibility()) {
                            enablePatientDetailsView(true);
                            toggleSearch();
                            patientMRNNumber.setEnabled(false);
                        }

                        EditText patientDOB = findViewById(R.id.Patient_DOB_editText_value);
                        final EditText patientMobileNumber = findViewById(R.id.patient_mobile_number_editText_value);
                        patientMobileNumber.addTextChangedListener(new PhoneNumberTextWatcher(patientMobileNumber));
                        RadioButton male = findViewById(R.id.radio_male);
                        RadioButton female = findViewById(R.id.radio_female);
                        String sex = state.getString("PatientSex");
                        patientFirstName.setText(state.getString("PatientFirstName"));
                        patientMRNNumber.setText(state.getString("PatientMrnNumber"));
                        patientDOB.setText(state.getString("PatientDOB"));
                        patientMobileNumber.setText(state.getString("PatientMobile"));
                        if (nullCheck(sex)) {
                            if (sex.equals("Male")) male.setChecked(true);
                            else female.setChecked(true);
                            female.setError(null);
                        }
                        if (state.getBoolean("showToasty")) {
                            Log.d("Toast", "toast is going to be shown");
                            showToast(state.getString("toastMessage"));
                            Actions.resetToast();
                        }
                        showMeTip(state);
                    } catch (Exception e) {
                        Log.e("SomeException", " " + e.getMessage());
                    }
                    break;

                case R.layout.activity_primary_test_details:
                    RadioButton rightEye = findViewById(R.id.right_eye_switch);
                    RadioButton leftEye = findViewById(R.id.left_eye_switch);
                    selectedPattern = findViewById(R.id.test_pattern_spinner);
                    selectedStrategy = findViewById(R.id.test_strategy_spinner);
                    selectedPattern.setAdapter(patternAdapter);
                    selectedPattern.setOnItemSelectedListener(this);
                    selectedStrategy.setAdapter(strategyAdapter);


                    String testEye = state.getString("PatientTestEye");
                    String testStrategy = state.getString("PatientTestStrategy");
                    String testPattern = state.getString("PatientTestPattern");
                    if (testEye != null) {
                        if (testEye.equals("Left Eye")) leftEye.setChecked(true);
                        else rightEye.setChecked(true);
                    }
                    selectedPattern.setSelection(patternAdapter.getPosition(testPattern));
                    Log.d("testStrategy", " " + testStrategy);
                    Log.d("testStrategy", "Position " + strategyAdapter.getPosition(testStrategy));

                    selectedStrategy.setSelection(strategyAdapter.getPosition(testStrategy));
                    if (state.getBoolean("showToasty")) {
                        Log.d("Toast", "toast is going to be shown");
                        showToast(state.getString("toastMessage"));
                        Actions.resetToast();
                    }
                    showMeTip(state);
                    break;

                case R.layout.activity_lens_power_settings:
                    LinearLayout cardView = findViewById(R.id.cardView);
                    cardView.setElevation(10);
                    EditText sphericalPower = findViewById(R.id.editable_spherical_power);
                    EditText cylindricalPower = findViewById(R.id.editable_cylindrical_power);
                    EditText cylindricalAxis = findViewById(R.id.editable_cylindrical_axis);
                    TextView sphericalPowerCalculated = findViewById(R.id.readable_spherical_power);
                    TextView cylindricalPowerCalculated = findViewById(R.id.readable_cylindrical_power);
                    TextView cylindricalAxisCalculated = findViewById(R.id.readable_cylindrical_axis);
                    TextView patAge = findViewById(R.id.patAge);
                    String sphericalInputValue = state.getString("PatientSphericalInput");
                    String cylindricalInputValue = state.getString("PatientCylindricalInput");
                    String cylindricalAxisInputValue = state.getString("PatientCylindricalAxisInput");

                    String sphericalValue = state.getString("PatientSphericalValue");
                    String cylindricalValue = state.getString("PatientCylindricalValue");
                    String cylindricalAxisValue = state.getString("PatientCylindricalAxisValue");

                    sphericalPower.setText(sphericalInputValue);
                    cylindricalPower.setText(cylindricalInputValue);
                    cylindricalAxis.setText(cylindricalAxisInputValue);

                    sphericalPowerCalculated.setText(sphericalValue);
                    cylindricalPowerCalculated.setText(cylindricalValue);
                    cylindricalAxisCalculated.setText(cylindricalAxisValue);
                    String age = state.getString("PatientDOB");
                    if (age != null) {
                        patAge.setText(CommonUtils.getAge(age));
                    }
                    showMeTip(state);
                    break;

                case R.layout.activity_test_profile:
                    h.removeCallbacks(waitScreenDelay);
                    TextView selectedPatientName = findViewById(R.id.patient_first_name);
                    TextView selectedPatientMobileNumber = findViewById(R.id.patient_mobile_number);
                    TextView selectedPatientMRNNumber = findViewById(R.id.patient_mrn_number);
                    TextView selectedPatientBirthDate = findViewById(R.id.patient_dob);
                    TextView selectedTestPattern = findViewById(R.id.test_pattern_selected);
                    TextView selectedTestEye = findViewById(R.id.selectedTestEye);
                    TextView selectedTestStrategy = findViewById(R.id.selectedTestStrategy);
                    TextView selectedSphericalPower = findViewById(R.id.readable_spherical_power);
                    TextView selectedCylindricalPower = findViewById(R.id.readable_cylindrical_power);
                    TextView selectedCylindricalAxis = findViewById(R.id.readable_cylindrical_axis);
                    ImageView genderImage = findViewById(R.id.genderImage);
                    TextView genderText = findViewById(R.id.genderText);
                    String temp = state.getString("SetPatientDOB");
                    if (temp != null) {
                        temp = CommonUtils.getAge(temp);
                        selectedPatientBirthDate.setText(temp);
                    }
                    selectedPatientName.setText(state.getString("SetPatientFirstName"));
                    selectedPatientMobileNumber.setText(state.getString("SetPatientMobile"));
                    selectedPatientMRNNumber.setText(state.getString("SetPatientMrnNumber"));

                    selectedTestPattern.setText(state.getString("SetPatientTestPattern"));
                    selectedTestEye.setText(state.getString("SetPatientTestEye"));
                    selectedTestStrategy.setText(state.getString("SetPatientTestStrategy"));
                    String sphericalPowerPhrase = "Spherical Power: " + state.getString("SetPatientSphericalValue");
                    String cylindricalPowerPhrase = "Cylindrical Power: " + state.getString("SetPatientCylindricalValue");
                    String cylindricalAxisPhrase = "Cylindrical Axis: " + state.getString("SetPatientCylindricalAxisValue");
                    selectedSphericalPower.setText(sphericalPowerPhrase);
                    selectedCylindricalPower.setText(cylindricalPowerPhrase);
                    selectedCylindricalAxis.setText(cylindricalAxisPhrase);
                    String selectedSex = state.getString("SetPatientSex");
                    if (selectedSex != null)
                        if (selectedSex.equals("Male")) {
                            genderImage.setImageResource(R.drawable.icon_male_active);
                            genderText.setText(R.string.male);
                        } else {
                            genderImage.setImageResource(R.drawable.icon_female_active);
                            genderText.setText(R.string.female);
                        }
                    break;

                case R.layout.activity_ipd_settings:
                    h.removeCallbacks(waitScreenDelay);
                    TextView eyeType = findViewById(R.id.eyeType);
                    String ipdEyeVal = state.getString("SetPatientTestEye");
                    eyeType.setText(ipdEyeVal);
                    EditText cameraAlpha = findViewById(R.id.cameraAlpha);
                    EditText cameraBeta = findViewById(R.id.cameraBeta);
                    if (ipdEyeVal.equalsIgnoreCase("left eye")) {
                        cameraAlpha.setText("" + devicePreferenceHelper.getLeftCameraAlpha());
                        cameraBeta.setText("" + devicePreferenceHelper.getLeftCameraBeta());
                    } else {
                        cameraAlpha.setText("" + devicePreferenceHelper.getRightCameraAlpha());
                        cameraBeta.setText("" + devicePreferenceHelper.getRightCameraBeta());
                    }
                    cameraAlpha.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            String val = s.toString();
                            if (val != null && !val.equals("")) {
                                if (!val.endsWith(".") && !val.startsWith("-")) {
                                    double alpha = Double.parseDouble(val);
                                    if (alpha < 1) {
                                        CommonUtils.showToasty(MainActivity.this, "Camera Contrast should be greater than or equal to 1", true, 'I');
                                    } else if (alpha > 3) {
                                        CommonUtils.showToasty(MainActivity.this, "Camera Contrast should be less than or equal to 3", true, 'I');
                                    } else {
                                        if (eyeType.getText().toString().equalsIgnoreCase("left eye")) {
                                            devicePreferenceHelper.setLeftCameraAlpha((float) alpha);
                                        } else {
                                            devicePreferenceHelper.setLeftCameraBeta((float) alpha);
                                        }
                                        Actions.sendHMDCameraAlphaBeta(cameraAlpha.getText() + " " + cameraBeta.getText());
                                    }
                                }
                            }

                        }
                    });
                    cameraBeta.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {

                        }

                        @Override
                        public void afterTextChanged(Editable s) {
                            String val = s.toString();
                            if (val != null && !val.equals("")) {
                                if (!val.endsWith(".") && !val.startsWith("-")) {
                                    double beta = Double.parseDouble(val);
                                    if (beta < 1) {
                                        CommonUtils.showToasty(MainActivity.this, "Camera Brightness should be greater than or equal to 1", true, 'I');
                                    } else if (beta > 150) {
                                        CommonUtils.showToasty(MainActivity.this, "Camera Brightness should be less than or equal to 150", true, 'I');
                                    } else {
                                        if (eyeType.getText().toString().equalsIgnoreCase("left eye")) {
                                            devicePreferenceHelper.setLeftCameraBeta((float) beta);
                                        } else {
                                            devicePreferenceHelper.setRightCameraBeta((float) beta);
                                        }
                                        Actions.sendHMDCameraAlphaBeta(cameraAlpha.getText() + " " + cameraBeta.getText());
                                    }
                                }
                            }
                        }
                    });

                    ipd_Image_Update_Thread = new Thread(ipd_ImageView_Runnabe);
                    Log.e("activity_ipd_settings", "ipdImageViewLinked already linked " + ipdImageViewLinked);
                    if (!ipdImageViewLinked) {
                        ipd_Image_Update_Thread.start();
                        ipdImageViewLinked = true;
                    }
                    if (state.getBoolean("showToasty")) {
                        showToast(state.getString("toastMessage"));
                        Actions.resetToast();
                    }
                    break;
                case R.layout.activity_pre_production:
                    TextView calibData = findViewById(R.id.calibDetails);
                    calibData.setText(state.getString("calibData"));
                    break;

                case R.layout.activity_calibiration:
                    h.removeCallbacks(waitScreenDelay);
                    TextView PatientName = findViewById(R.id.PatientName);
                    TextView PatientSex = findViewById(R.id.PatientSex);
                    TextView PatientAge = findViewById(R.id.PatientAge);
                    TextView TestStrategy = findViewById(R.id.TestStrategy);
                    TextView TestEye = findViewById(R.id.TestEye);
                    TextView PatientMRNNumber = findViewById(R.id.PatientMRNNumber);
                    final Button calibration_continue_button = findViewById(R.id.calibration_continue_button);
                    final Button calibrateSkip_Button = findViewById(R.id.calibrateSkip_Button);
                    final Button calibrateRecalibrate_Button = findViewById(R.id.calibrateRecalibrate_Button);
                    String calibAge = state.getString("SetPatientDOB");
                    if (calibAge != null) {
                        calibAge = CommonUtils.getAge(calibAge);
                        PatientAge.setText(calibAge);
                    }
                    String selectedPatientMRNNumberVal = state.getString("SetPatientMrnNumber");
                    String selectedTestEyeVal = state.getString("SetPatientTestEye");
                    String selectedTestStrategyVal = state.getString("SetPatientTestStrategy");
                    PatientName.setText(state.getString("SetPatientFirstName"));
                    PatientSex.setText(state.getString("SetPatientSex"));
                    TestStrategy.setText(selectedTestStrategyVal);
                    TestEye.setText(selectedTestEyeVal);
                    PatientMRNNumber.setText(selectedPatientMRNNumberVal);
                    Log.e("activity_calibiration", "calibrationImageViewLinked already linked " + calibrationImageViewLinked);
                    if (!calibrationImageViewLinked) {
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                calibration_continue_button.setVisibility(View.VISIBLE);
                                calibrateSkip_Button.setVisibility(View.VISIBLE);
                                calibrateRecalibrate_Button.setVisibility(View.VISIBLE);
                            }
                        }, 1000);
                    }
                    Cl_Image_Update_Thread = new Thread(Calib_ImageView_Runnabe);
                    if (!calibrationImageViewLinked) {
                        Cl_Image_Update_Thread.start();
                        calibrationImageViewLinked = true;
                    }
                    if (state.getBoolean("showToasty")) {
                        if (Store.toastMessage.equals("Calibration done..."))
                            calibrateRecalibrate_Button.setText(R.string.recalibrate);
                    }
                    break;


                case R.layout.activity_during_test:
                    h.removeCallbacks(waitScreenDelay);
                    Log.e("activity_during_test", " isChronometerRunning " + isChronometerRunning);
                    Log.e("PRB_CLICK_RECEIVED", "dt_chronometerOn " + state.getBoolean("dt_chronometerOn"));
                    chronometerForDurationCalculation = findViewById(R.id.duringTestChronometer);
                    CommonUtils.writeToChronometerLogFile("isFoveaProceeded " + isFoveaProceeded + " dt_chronometerOn " + state.getBoolean("dt_chronometerOn"));
                    if (isFoveaProceeded) {
                       /* chronometer.stop();
                        chronometer.setBase(SystemClock.elapsedRealtime());
                        chronometer.start();*/
                        isFoveaProceeded = false;
                    }
                    if (state.getBoolean("dt_chronometerOn")) {
                        if (!isChronometerRunning) {
                            Log.e("isChronometerRunning ", " This got executed");
                            chronometerForDurationCalculation.setBase(SystemClock.elapsedRealtime());
                            chronometerForDurationCalculation.start();
                            isChronometerRunning = true;
                        }
                    } else {
                        Log.e("isChronometerNotRuing ", " This got executed");
                       /* long  currentReading = ((Chronometer)findViewById(R.id.dt_chrono)).getBase() - SystemClock.elapsedRealtime();
                        appPreferencesHelper.setTimeBase(currentReading);
                        Log.d(MainActivity.class.getName()," Reading "+currentReading);*/
                        timeWhenStopped = SystemClock.elapsedRealtime() - chronometerForDurationCalculation.getBase();
                        //long elapsedMillis = SystemClock.elapsedRealtime() - chronometer.getBase();
                        //Log.e(TAG," Reading "+elapsedMillis);
                        //Toast.makeText(this," elapsed time "+CommonUtils.duringCalculationFromMillis(elapsedMillis),Toast.LENGTH_SHORT).show();
                        chronometerForDurationCalculation.stop();
                    }

                    TextView duringTestPatientName = findViewById(R.id.PatientName);
                    TextView duringTestPatientSex = findViewById(R.id.PatientSex);
                    TextView duringTestTestStrategy = findViewById(R.id.TestStrategy);
                    TextView duringTestTestEye = findViewById(R.id.TestEye);
                    TextView duringTestPatientMRNNumber = findViewById(R.id.PatientMRNNumber);
                    String duringTestPatientMRNNumberVal = state.getString("SetPatientMrnNumber");
                    String duringTestTestEyeVal = state.getString("SetPatientTestEye");
                    String duringTestTestStrategyVal = state.getString("SetPatientTestStrategy");
                    duringTestPatientName.setText(state.getString("SetPatientFirstName"));
                    duringTestPatientSex.setText(state.getString("SetPatientSex"));
                    duringTestTestStrategy.setText(duringTestTestStrategyVal);
                    duringTestTestEye.setText(duringTestTestEyeVal);
                    duringTestPatientMRNNumber.setText(duringTestPatientMRNNumberVal);
                    String pattern = state.getString("SetPatientTestPattern");
                    String eye = state.getString("SetPatientTestEye");
                    Log.d("DuringTest", " Eye " + eye + " Pattern" + pattern);
                    ViewStub stub = findViewById(R.id.stub);
                    if (stub != null && pattern != null && eye != null)
                        switch (pattern) {
                            case "24-2":
                                if (eye.equals("Left Eye")) {
                                    Log.d("DuringTest", "Left Eye " + eye + " 24-2 Pattern");
                                    stub.setLayoutResource(R.layout.fifity_four_left);
                                    stub.inflate();
                                } else {
                                    Log.d("DuringTest", "Right Eye " + eye + " 24-2  Pattern");
                                    stub.setLayoutResource(R.layout.fifty_four_right);
                                    stub.inflate();
                                }
                                break;
                            case "30-2":
                                if (eye.equals("Left Eye")) {
                                    stub.setLayoutResource(R.layout.seventy_six_left);
                                    stub.inflate();
                                } else {
                                    stub.setLayoutResource(R.layout.seventy_six_right);
                                    stub.inflate();
                                }
                                break;
                            case "10-2":
                                if (eye.equals("Left Eye")) {
                                    stub.setLayoutResource(R.layout.ten_cross_two_left);
                                    stub.inflate();
                                } else {
                                    stub.setLayoutResource(R.layout.ten_cross_two_right);
                                    stub.inflate();
                                }
                                break;
                            case "Macula":
                                if (eye.equals("Left Eye")) {
                                    stub.setLayoutResource(R.layout.macula_left);
                                    stub.inflate();
                                } else {
                                    stub.setLayoutResource(R.layout.macula_right);
                                    stub.inflate();
                                }
                                break;
                        }
                    Drawable drawableSeen = getDrawable(R.drawable.seen_db);
                    Drawable drawableUnseen = getDrawable(R.drawable.unseen_db);
                    ArrayList<String> newResultArray = state.getStringArrayList("dt_new_result");
                    ArrayList<String> newSensitivityArray = state.getStringArrayList("dt_result_sensitivity");
                    ArrayList<String> dt_result_seen = state.getStringArrayList("dt_result_seen");
                    Log.d("ISArrayListEmpty", " " + newResultArray.isEmpty());
                    if (!newResultArray.isEmpty() && duringTestTestStrategyVal != null && dt_result_seen != null) {
                        if (duringTestTestStrategyVal.equals("Screening")) {
                            for (int i = 0; i < dt_result_seen.size(); i++) {
                                TextView textView = findViewById(R.id.tableParent).findViewWithTag(newResultArray.get(i));
                                textView.setText("");
                                if (dt_result_seen.get(i).equalsIgnoreCase("SEEN")) {
                                    textView.setBackground(drawableSeen);
                                } else {
                                    textView.setBackground(drawableUnseen);
                                }
                            }
                        } else {
                            for (int i = 0; i < newResultArray.size(); i++) {
                                try {
                                    ((TextView) findViewById(R.id.tableParent).findViewWithTag(newResultArray.get(i))).setText(newSensitivityArray.get(i));
                                } catch (Exception e) {
                                    Log.e("activity_during_test", " " + e.getMessage());
                                    Log.e("activity_during_test", " " + Arrays.toString(newResultArray.toArray()));

                                    CommonUtils.writeToTenDashTwoLogFile("View Not Found " + newResultArray.get(i));
                                }
                            }

                        }
                    }
                    ((TextView) findViewById(R.id.fixation_state)).setText(state.getString("FL"));
                    ((TextView) findViewById(R.id.false_positive_textview)).setText(state.getString("FP"));
                    ((TextView) findViewById(R.id.false_negative_textview)).setText(state.getString("FN"));
                    Log.e("activity_during_test", "activity_during_test already linked " + testImageViewLinked);
                    if (!testImageViewLinked) {
                        DT_Image_Update_Thread = new Thread(duringTest_ImageView_Runnable);
                        Log.d("VideoLoop", "this is starting again");
                        DT_Image_Update_Thread.start();
                        testImageViewLinked = true;
                    }
                    Log.d("DTTESTOVER", " " + state.getBoolean("dt_testOver"));
                    if (state.getBoolean("dt_testOver")) {
                        //((Chronometer) findViewById(R.id.dt_chrono)).stop();
                        elapsedTime = SystemClock.elapsedRealtime() - chronometerForDurationCalculation.getBase();
                        DT_Image_Update_Thread.interrupt();
                        testImageViewLinked = false;
                        //((Chronometer) findViewById(R.id.dt_chrono)).stop();
                        Log.d("DTTESTOVER", "Dt over " + elapsedTime);
                    }
                    if (state.getBoolean("showToasty")) {
                        showToast(state.getString("toastMessage"));
                        Actions.resetToast();
                    }
                    break;


                case R.layout.post_test_doctors_copy_result:
                    BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);
                    bottomNavigationView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
                    ProgressBar progress = findViewById(R.id.progress);
                    eye = state.getString("setPatientTestEye");
                    eye = eye.equals("Right Eye") ? "Eye: Right" : "Eye: Left";
                    ((TextView) findViewById(R.id.reportView_testEye)).setText(eye);

                    String sex = state.getString("setPatientSex");
                    String name = state.getString("setPatientName");
                    name = "Name: " + name;
                    //sex.equals("Male") ? "Name: Mr. " + name : "Name: Mrs. " + name;
                    ((TextView) findViewById(R.id.reportView_PatientName)).setText(name);

                    pattern = state.getString("setPatientTestPattern");

                    String mrn = "ID: " + state.getString("setPatientMrnNumber");
                    ((TextView) findViewById(R.id.reportView_MRNumber)).setText(mrn);

                    String dob = state.getString("setPatientDOB");
                    ((TextView) findViewById(R.id.reportView_DOB)).setText(dob);

                    age = "AGE: " + CommonUtils.getAge(dob);
                    ((TextView) findViewById(R.id.reportView_Age)).setText(age);

                    String strategy = state.getString("setPatientTestStrategy");
                    ((TextView) findViewById(R.id.reportView_testStrategy)).setText(strategy);
                    strategy = "Strategy: " + strategy;
                    ((TextView) findViewById(R.id.textStrategy)).setText(strategy);

                    String fixationMonitor = "Fixation Monitor: Gaze / Blindspot";
                    ((TextView) findViewById(R.id.textFixationMoniter)).setText(fixationMonitor);

                    String fixationTarget = "Fixation Target: Central";
                    ((TextView) findViewById(R.id.textFixationTarget)).setText(fixationTarget);

                    String fixationLoss = state.getString("FL");
                    fixationLoss = "Fixation Losses: " + fixationLoss;
                    ((TextView) findViewById(R.id.textFixationLosses)).setText(fixationLoss);

                    String falsePositives = state.getString("FP");
                    falsePositives = "False POS Errors: " + falsePositives;
                    ((TextView) findViewById(R.id.textFalsePositive)).setText(falsePositives);

                    String falseNegatives = state.getString("FN");
                    falseNegatives = "False NEG Errors: " + falseNegatives;
                    ((TextView) findViewById(R.id.textFalseNegative)).setText(falseNegatives);

                    String duration = state.getString("TestDuration");
                    if (duration.startsWith("-")) {
                        duration = "0:00" + "\\u002A";
                        TextView timer_wrong_warning = findViewById(R.id.timer_wrong_warning);
                        timer_wrong_warning.setVisibility(View.VISIBLE);
                        ((TextView) findViewById(R.id.textTotalDuration)).setText(duration);
                    } else {
                        duration = "Total Duration: " + duration;
                        ((TextView) findViewById(R.id.textTotalDuration)).setText(duration);
                    }

                    String background = state.getString("BackgroundIlluminationColorCodeSequence");
                    ((TextView) findViewById(R.id.textBackground)).setText(background);

                    String PupilSize = state.getString("PupilSize");
                    ((TextView) findViewById(R.id.textPupilDia)).setText(PupilSize);

                    String textVisualAcuity = state.getString("visualAcuity");
                    ((TextView) findViewById(R.id.textVisualAcuity)).setText(textVisualAcuity);

                    Format formatter = CommonUtils.getStandardFormat();
                    String today = formatter.format(new Date());
                    ((TextView) findViewById(R.id.currentTime)).setText(today);

                    String copyright = "\u00a9 Elisar Life Sciences Private Limited. " + CommonUtils.currentYear();
                    ((TextView) findViewById(R.id.copyRights)).setText(copyright);

                    ((TextView) findViewById(R.id.fovea)).setText(state.getString("fovea"));

                    String ghtD = state.getString("GHT");
                    ghtD = "GHT:  " + ghtD;
                    String mdD = state.getString("meanDeviation");
                    String psdD = state.getString("psd");
                    mdD = String.format("%.2f", Double.parseDouble(mdD));
                    psdD = String.format("%.2f", Double.parseDouble(psdD));
                    mdD = "MD:  " + mdD + " dB ";
                    psdD = "PSD:  " + psdD + " dB ";
                    double mdProb = state.getDouble("MDProbability");
                    double pdProb = state.getDouble("PDProbability");
                    Log.e("mdProb", " " + mdProb);
                    Log.e("pdProb", " " + pdProb);
                    if (mdProb < 0.5) {
                        mdD = mdD + "P < 0.5%";
                    } else if (mdProb < 1) {
                        mdD = mdD + "P < 1 %";
                    } else if (mdProb < 2) {
                        mdD = mdD + "P < 2 %";
                    } else if (mdProb < 5) {
                        mdD = mdD + "P < 5 %";
                    } else {
                        mdD = mdD + "P > 5 %";
                    }
                    if (pdProb < 0.5) {
                        psdD = psdD + "P < 0.5%";
                    } else if (pdProb < 1) {
                        psdD = psdD + "P < 1 %";
                    } else if (pdProb < 2) {
                        psdD = psdD + "P < 2 %";
                    } else if (pdProb < 5) {
                        psdD = psdD + "P < 5 %";
                    } else {
                        psdD = psdD + "P > 5 %";
                    }

                    SpannableStringBuilder spanbableGHT = new SpannableStringBuilder(ghtD);
                    spanbableGHT.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    SpannableStringBuilder spanbablePsd = new SpannableStringBuilder(psdD);
                    spanbablePsd.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    SpannableStringBuilder spanbablemD = new SpannableStringBuilder(mdD);
                    spanbablemD.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), 0, 3, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

                    ((TextView) findViewById(R.id.ghtText)).setText(spanbableGHT);
                    ((TextView) findViewById(R.id.mdText)).setText(spanbablemD);
                    ((TextView) findViewById(R.id.gpsdText)).setText(spanbablePsd);

                    ((TextView) findViewById(R.id.agarwal)).setText("DR.AGARWAL'S EYE HOSPITAL\nCHENNAI");

                    final ArrayList<String> result = state.getStringArrayList("dt_new_result");
                    final ArrayList<String> result_sensitivity = state.getStringArrayList("dt_result_sensitivity");
                    final ArrayList<String> result_deviation = state.getStringArrayList("dt_result_deviation");
                    final ArrayList<String> result_generalizedDefectCorrectedSensitivityDeviationValue = state.getStringArrayList("dt_result_generalizedDefectCorrectedSensitivityDeviationValue");
                    final ArrayList<String> result_probabilityDeviationValue = state.getStringArrayList("dt_result_probabilityDeviationValue");
                    final ArrayList<String> result_generalizedDefectCorrectedSensitivityDeviationProbabilityValue = state.getStringArrayList("dt_result_generalizedDefectCorrectedSensitivityDeviationProbabilityValue");
                    final double[][][] quadrants = (double[][][]) state.getSerializable("quadrants");
                    ImageView imageOne = findViewById(R.id.imageOne);
                    ImageView imageTwo = findViewById(R.id.imageTwo);
                    ImageView imageThree = findViewById(R.id.imageThree);
                    ImageView imageFour = findViewById(R.id.imageFour);
                    ImageView imageFive = findViewById(R.id.imageFive);
                    ImageView imageGreyScale = findViewById(R.id.greyScale);
                    ArrayList<String> values = new ArrayList<>();
                    eye = state.getString("setPatientTestEye");
                    values.add(eye);
                    values.add(pattern);
                    new LoadMappedView(MainActivity.this, imageOne).execute(values, result, result_sensitivity);
                    new LoadMappedLargeText(MainActivity.this, imageTwo).execute(values, result, result_deviation);
                    new LoadMappedLargeText(MainActivity.this, imageThree).execute(values, result, result_generalizedDefectCorrectedSensitivityDeviationValue);
                    new LoadMappedIconView(MainActivity.this, imageFour).execute(values, result, result_probabilityDeviationValue);
                    new LoadMappedIconView(MainActivity.this, imageFive).execute(values, result, result_generalizedDefectCorrectedSensitivityDeviationProbabilityValue);
                    new LoadGreyScale(MainActivity.this, imageGreyScale, progress, eye).execute(quadrants);
                    break;

                case R.layout.post_test_screening_result:
                    Log.d("MainActivity", "current layout set to post_test_patient_result");
                    BottomNavigationView bottomNavigation = findViewById(R.id.bottom_navigation);
                    bottomNavigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

                    String postTestPatientSex = state.getString("setPatientSex");
                    String postTestPatientStrategy = state.getString("setPatientTestStrategy");
                    String postTestPatientPattern = state.getString("setPatientTestPattern");

                    String postTestPatientName = state.getString("setPatientName");
                    postTestPatientName = "Name: " + postTestPatientName; //postTestPatientSex.equals("Male") ? "Name: Mr. " + postTestPatientName : "Name: Mrs. " + postTestPatientName;
                    ((TextView) findViewById(R.id.reportView_PatientName)).setText(postTestPatientName);

                    String pattern_and_strategy = "Central " + postTestPatientPattern + " " + postTestPatientStrategy + " Test";
                    ((TextView) findViewById(R.id.pattern_and_strategy)).setText(pattern_and_strategy);

                    String postTestPatientAge = "AGE: " + CommonUtils.getAge(state.getString("setPatientDOB"));
                    ((TextView) findViewById(R.id.reportView_Age)).setText(postTestPatientAge);

                    String postTestPatientMrn = "ID: " + state.getString("setPatientMrnNumber");
                    ((TextView) findViewById(R.id.reportView_MRNumber)).setText(postTestPatientMrn);

                    String fieldOfVision = postTestPatientPattern.equals("30-2") ? getResources().getString(R.string.field_of_vision_72_screening) : getResources().getString(R.string.field_of_vision_54_screening);
                    ((TextView) findViewById(R.id.fieldOfVision)).setText(fieldOfVision);

                    ((TextView) findViewById(R.id.testConductedTime)).setText(state.getString("CreatedDate"));

                    String cr = "\u00a9" + " " + CommonUtils.currentYear() + " Elisar Life Sciences Pvt Ltd";
                    ((TextView) findViewById(R.id.copyRights)).setText(cr);

                    if (CommonUtils.isUserSetUpFinished(this)) {
                        try {
                            JSONObject config = CommonUtils.readConfig("post_test_screening_result");
                            if (config != null) {
                                //String siteOrg = config.getString("OrganizationId") + " \n" + config.getString("SiteId");
                                //((TextView) view.findViewById(R.id.branch_and_site)).setText(siteOrg);
                                ((TextView) findViewById(R.id.deviceId)).setText(config.getString("DeviceId"));
                            }
                        } catch (JSONException e) {
                            Log.e("DoctorReportActivity", e.getMessage());
                        }
                    } /*else {
                ((TextView) view.findViewById(R.id.branch_and_site)).setText("DR.AGARWAL'S EYE HOSPITAL\nCHENNAI");
            }*/
                    String swVersion = CommonUtils.getVersionCombo();
                    ((TextView) findViewById(R.id.swVersion)).setText(swVersion);
                    Format dateTimeFormat = CommonUtils.getDateTimeFormat();
                    long millis = Long.parseLong(state.getString("CreatedDate"));
                    String createdDate = dateTimeFormat.format(new Date(millis));
                    ((TextView) findViewById(R.id.testConductedTime)).setText(createdDate);

                    String postTestPatientEye = state.getString("setPatientTestEye");
                    eye = postTestPatientEye.equals("Right Eye") ? "Test Result - Right Eye" : "Test Result - Left Eye";
                    ((TextView) findViewById(R.id.reportView_testEye)).setText(eye);

                    final ArrayList<String> pointValues = state.getStringArrayList("dt_result_probabilityDeviationValue");
                    final ArrayList<String> seen_Unseen = state.getStringArrayList("dt_result_seen");
                    final ArrayList<String> coordinates = state.getStringArrayList("dt_new_result");

                    ArrayList<String> postTestPatientValues = new ArrayList<>();
                    postTestPatientValues.add(postTestPatientPattern);
                    postTestPatientValues.add(state.getString("setPatientTestEye"));
                    new LoadScreeningGraphs(MainActivity.this).execute(postTestPatientValues, coordinates, seen_Unseen);
                    float fl_percentage = (state.getInt("FL_Numerator") * 100.0f) / state.getInt("FL_Denominator");
                    float fp_percentage = (state.getInt("FP_Numerator") * 100.0f) / state.getInt("FP_Denominator");
                    int seen = 0, notSeen = 0, missedInitialPoints = 0;
                    for (int i = 0; i < seen_Unseen.size(); i++) {
                        if (seen_Unseen.get(i).equals("SEEN"))
                            seen++;
                        else {
                            if (!CommonUtils.isValueInBlindStop(pointValues.get(i)))
                                notSeen++;
                            if (CommonUtils.isValueInFirstBlock(pointValues.get(i))) {
                                notSeen++;
                                missedInitialPoints++;
                            }

                        }
                    }
                    int total = pointValues.size();
                    ((TextView) findViewById(R.id.whiteView)).setText(Integer.toString(seen));
                    ((TextView) findViewById(R.id.redView)).setText(Integer.toString(notSeen));
                    String defective = "Defective points: " + notSeen + " / " + total;
                    ((TextView) findViewById(R.id.defectivePoints)).setText(defective);

                    String limit = "";
                    String advice = "";


                    Log.e("fl_percentage", "" + state.getInt("FL_Numerator"));
                    Log.e("fl_percentage", "" + state.getInt("FL_Denominator"));
                    Log.e("fl_percentage", "" + state.getInt("FP_Numerator"));
                    Log.e("fl_percentage", "" + state.getInt("FP_Denominator"));
                    Log.e("fl_percentage", "" + fl_percentage);
                    Log.e("fp_percentage", "" + fp_percentage);
                    Log.e("notSeen", "" + notSeen);
                    Log.e("missedInitialPoints", "" + missedInitialPoints);

                    if (fl_percentage <= 33.33 && fp_percentage <= 33.33) {
                        if (notSeen <= 3) {
                            limit = "Normal ";
                            advice = "Your visual field test looks okay";
                        } else if (missedInitialPoints == 0) {
                            limit = "Abnormal";
                            advice = "Please visit an eye specialist for further investigation.";
                        } else if (missedInitialPoints > 0 && missedInitialPoints <= 2) {
                            limit = "Abnormal";
                            advice = "Please visit an eye specialist for further investigation.";
                        } else if (missedInitialPoints > 2 && missedInitialPoints <= 4) {
                            limit = "Suspect";
                            advice = "Redo the test because you missed points in the initial part of the test.";
                        }
                    } else if (fl_percentage > 33.33 || fp_percentage > 33.33) {
                        if (notSeen <= 3) {
                            limit = "Suspect";
                            advice = "Redo the test because reliability is low.";
                        } else if (missedInitialPoints >= 3) {
                            limit = "Suspect";
                            advice = "Redo the test because you missed points in the initial part of the test.";
                        } else if (missedInitialPoints >= 2) {
                            limit = "Abnormal";
                            advice = "Please visit an eye specialist for further investigation.";
                        }
                    }

                    ((TextView) findViewById(R.id.reportView_testResultLimit)).setText(limit);
                    ((TextView) findViewById(R.id.reportView_testResultAdvice)).setText(advice);
                    break;
                case R.layout.new_activity_post_test:
                    int hours = (int) (elapsedTime / 3600000);
                    int minutes = (int) (elapsedTime - hours * 3600000) / 60000;
                    int seconds = (int) (elapsedTime - hours * 3600000 - minutes * 60000) / 1000;
                    String elapsedTime = String.valueOf(minutes) + ":" + String.valueOf(seconds);
                    ((TextView) findViewById(R.id.duringTestChronometer)).setText(elapsedTime);
                    Log.d("Elaspe", " " + elapsedTime);
                    TextView postTestPatientNameT = findViewById(R.id.PatientName);
                    TextView postTestPatientSexT = findViewById(R.id.PatientSex);
                    TextView postTestTestStrategy = findViewById(R.id.TestStrategy);
                    TextView postTestTestEye = findViewById(R.id.TestEye);
                    TextView postTestPatientMRNNumber = findViewById(R.id.PatientMRNNumber);
                    String postTestPatientMRNNumberVal = state.getString("SetPatientMrnNumber");
                    String postTestTestEyeVal = state.getString("SetPatientTestEye");
                    String postTestTestStrategyVal = state.getString("SetPatientTestStrategy");
                    postTestPatientNameT.setText(state.getString("SetPatientFirstName"));
                    postTestPatientSexT.setText(state.getString("SetPatientSex"));
                    postTestTestStrategy.setText(postTestTestStrategyVal);
                    postTestTestEye.setText(postTestTestEyeVal);
                    postTestPatientMRNNumber.setText(postTestPatientMRNNumberVal);
                    String postPattern = state.getString("SetPatientTestPattern");
                    String postEye = state.getString("SetPatientTestEye");
                    Log.d("DuringTest", " Eye " + postEye + " Pattern" + postPattern);
                    ViewStub postStub = findViewById(R.id.stub);
                    if (postStub != null)
                        switch (postPattern) {
                            case "24-2":
                                if (postEye.equals("Left Eye")) {
                                    Log.d("DuringTest", "Left Eye " + postEye + " 24-2 Pattern");
                                    postStub.setLayoutResource(R.layout.fifity_four_left);
                                    postStub.inflate();
                                } else {
                                    Log.d("DuringTest", "Right Eye " + postEye + " 24-2  Pattern");
                                    postStub.setLayoutResource(R.layout.fifty_four_right);
                                    postStub.inflate();
                                }
                                break;
                            case "30-2":
                                if (postEye.equals("Left Eye")) {
                                    postStub.setLayoutResource(R.layout.seventy_six_left);
                                    postStub.inflate();
                                } else {
                                    postStub.setLayoutResource(R.layout.seventy_six_right);
                                    postStub.inflate();
                                }
                                break;
                            case "10-2":
                                if (postEye.equals("Left Eye")) {
                                    postStub.setLayoutResource(R.layout.ten_cross_two_left);
                                    postStub.inflate();
                                } else {
                                    postStub.setLayoutResource(R.layout.ten_cross_two_right);
                                    postStub.inflate();
                                }
                                break;
                            case "Macula":
                                if (postEye.equals("Left Eye")) {
                                    postStub.setLayoutResource(R.layout.macula_left);
                                    postStub.inflate();
                                } else {
                                    postStub.setLayoutResource(R.layout.macula_right);
                                    postStub.inflate();
                                }
                                break;
                        }

                    ArrayList<String> postResultArray = state.getStringArrayList("dt_new_result");
                    ArrayList<String> postSensitivityArray = state.getStringArrayList("dt_result_sensitivity");
                    Log.d("ISArrayListEmpty", " " + postResultArray.isEmpty());
                    if (!postResultArray.isEmpty()) {
                        Log.d("Size of Result ", " " + postResultArray.size() + " Sensitivity " + postSensitivityArray.size());
                        for (int i = 0; i < postResultArray.size(); i++) {
                            Log.d("ID", "We Looking for " + postResultArray.get(i) + " as " + postSensitivityArray.get(i));
                            ((TextView) findViewById(R.id.tableParent).findViewWithTag(postResultArray.get(i))).setText(postSensitivityArray.get(i));
                        }
                    }
                    ((TextView) findViewById(R.id.fixation_state)).setText(state.getString("FL"));
                    ((TextView) findViewById(R.id.false_positive_textview)).setText(state.getString("FP"));
                    ((TextView) findViewById(R.id.false_negative_textview)).setText(state.getString("FN"));
                    if (state.getBoolean("showToasty")) {
                        Log.d("Toast", "toast is going to be shown");
                        showToast(state.getString("toastMessage"));
                        Actions.resetToast();
                    }
                    break;


                case R.layout.decoy:
                    Log.d("DecoyCalled", "DecoyCalled");
                    Intent intent = new Intent(applicationContext, TestReport.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("bottomView", "visible");
                    intent.putExtra("Payload", state.getBundle("finalPerimetryTestFinalResultObject"));
                    applicationContext.startActivity(intent);
                    activeView_number = R.layout.activity_home_screen;
                    break;
                case R.layout.logout_decoy:
                    finish();
                    Intent login_intent = new Intent(this, LoginActivity.class);
                    login_intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getApplicationContext().startActivity(login_intent);
                    break;

                case R.layout.error_component_failure:
                    // h.removeCallbacks(waitScreenDelay);
                    break;

                case R.layout.warning_bno_charging:
                    ImageView loading_Gif = findViewById(R.id.errorGif);
                    Glide.with(this).asGif().load(R.raw.continue_charging).into(loading_Gif);
                    break;

                case R.layout.warning_bno_not_charging:
                    break;

                case R.layout.warning_bo_charging:
                    ImageView error_bo_charging = findViewById(R.id.errorGif);
                    Glide.with(this).asGif().load(R.raw.plug_device_out).into(error_bo_charging);
                    break;
            }
            // modeSync(false, false, false);
        } catch (Exception e) {
            Log.e("Exception", " " + e.getMessage() + " Active view number is " + getResources().getResourceEntryName(activeView_number));
            activeView_number = R.layout.activity_home_screen;
            StoreTransmitter.updatedUIState("Exception");
        }
    }

    private int decideDisplayStatusMessage() {
        if (displayStatus == DisplayStatus.UNDEFINED) {
            if (eyeTrackingStatus == EyeTrackingStatus.EYE_TRACKING_NOT_OK || eyeTrackingStatus == EyeTrackingStatus.UNDEFINED)
                return R.string.display_status_not_received;
            else if (eyeTrackingStatus == EyeTrackingStatus.EYE_TRACKING_OK)
                return R.string.display_status_not_yet_received;
        } else if (displayStatus == DisplayStatus.DISPLAY_OK) {
            return R.string.display_okay;
        } else if (displayStatus == DisplayStatus.DISPLAY_NOT_OK) {
            if (Store.isAllLastFiveDisplayStatusAreOkay())
                return R.string.display_continue_with_caution;
            else
                return R.string.display_out_of_limits;
        }
        return 0;
    }

    private String decideErrorCode() {
        int id = decideDisplayStatusMessage();
        if (clickerStatus == ClickerStatus.CLICKER_OK && eyeTrackingStatus == EyeTrackingStatus.EYE_TRACKING_OK
                && id == R.string.display_okay) {
            return "All_set";
        }
        if (clickerStatus == ClickerStatus.CLICKER_OK && eyeTrackingStatus == EyeTrackingStatus.EYE_TRACKING_OK
                && id == R.string.display_continue_with_caution) {
            return "1_002";
        }
        if (clickerStatus == ClickerStatus.CLICKER_OK && eyeTrackingStatus == EyeTrackingStatus.UNDEFINED
                && id == R.string.display_continue_with_caution) {
            return "1_012";
        }
        if (clickerStatus == ClickerStatus.UNDEFINED && eyeTrackingStatus == EyeTrackingStatus.EYE_TRACKING_OK
                && id == R.string.display_continue_with_caution) {
            return "1_102";
        }
        if (clickerStatus == ClickerStatus.UNDEFINED && eyeTrackingStatus == EyeTrackingStatus.UNDEFINED
                && id == R.string.display_continue_with_caution) {
            return "1_112";
        }
        if (clickerStatus == ClickerStatus.UNDEFINED && eyeTrackingStatus == EyeTrackingStatus.EYE_TRACKING_NOT_OK
                && id == R.string.display_continue_with_caution) {
            return "1_122";
        }
        if (clickerStatus == ClickerStatus.CLICKER_NOT_OK && eyeTrackingStatus == EyeTrackingStatus.EYE_TRACKING_OK
                && id == R.string.display_continue_with_caution) {
            return "1_202";
        }
        if (clickerStatus == ClickerStatus.CLICKER_NOT_OK && eyeTrackingStatus == EyeTrackingStatus.UNDEFINED
                && id == R.string.display_continue_with_caution) {
            return "1_212";
        }
        if (clickerStatus == ClickerStatus.CLICKER_NOT_OK && eyeTrackingStatus == EyeTrackingStatus.EYE_TRACKING_NOT_OK
                && id == R.string.display_continue_with_caution) {
            return "1_222";
        }
        if (clickerStatus == ClickerStatus.CLICKER_OK && eyeTrackingStatus == EyeTrackingStatus.EYE_TRACKING_NOT_OK
                && id == R.string.display_continue_with_caution) {
            return "1_022";
        }
        return "NA";
    }

    private void downloadDatabase(TextView database_percentage, ProgressBar database_progress, Button
            restart_database_download) {
        databaseDownloadStatus = Status.UNKNOWN;
        PRDownloader.download(CommonUtils.generateDatabaseDownloadLink(), getFilesDir().getAbsolutePath(), "old-patient-database.db")
                .build()
                .setOnStartOrResumeListener(() -> database_progress.setIndeterminate(false))
                .setOnPauseListener(() -> {
                })
                .setOnCancelListener(() -> {
                    database_progress.setProgress(0);
                    database_percentage.setText("");
                    database_progress.setIndeterminate(false);
                })
                .setOnProgressListener(progress -> {
                    long progressPercent = progress.currentBytes * 100 / progress.totalBytes;
                    database_progress.setProgress((int) progressPercent);
                    database_percentage.setText(CommonUtils.getProgressDisplayLine(progress.currentBytes, progress.totalBytes));
                    database_progress.setIndeterminate(false);
                })
                .start(new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {

                        databaseDownloadStatus = Status.COMPLETED;
                        Actions.startDownloadMerging();
                    }

                    @Override
                    public void onError(Error error) {
                        tcDownloadStatus = Status.UNKNOWN;
                        restart_database_download.setVisibility(View.VISIBLE);
                        database_percentage.setText("Error while downloading");
                        database_progress.setProgress(0);
                        database_progress.setIndeterminate(false);
                    }
                });
    }

    private void downloadTCUpdate(TextView tc_percentage, ProgressBar tc_progress, Button
            restart_tc_download, AppPreferencesHelper devicePref) {
        tcDownloadStatus = Status.UNKNOWN;
        Log.e("getTCDownloadLink", " " + devicePref.getTCDownloadLink());
        PRDownloader.download(devicePref.getTCDownloadLink(), getFilesDir().getAbsolutePath(), "TC_latest.apk")
                .build()
                .setOnStartOrResumeListener(() -> tc_progress.setIndeterminate(false))
                .setOnPauseListener(() -> {
                })
                .setOnCancelListener(() -> {
                    tc_progress.setProgress(0);
                    tc_percentage.setText("");
                    tc_progress.setIndeterminate(false);
                })
                .setOnProgressListener(progress -> {
                    long progressPercent = progress.currentBytes * 100 / progress.totalBytes;
                    tc_progress.setProgress((int) progressPercent);
                    tc_percentage.setText(CommonUtils.getProgressDisplayLine(progress.currentBytes, progress.totalBytes));
                    tc_progress.setIndeterminate(false);
                })
                .start(new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {

                        tcDownloadStatus = Status.COMPLETED;
                        enableHMDUpdate();
                    }

                    @Override
                    public void onError(Error error) {
                        tcDownloadStatus = Status.UNKNOWN;
                        restart_tc_download.setVisibility(View.VISIBLE);
                        tc_percentage.setText("Error while downloading");
                        tc_progress.setProgress(0);
                        tc_progress.setIndeterminate(false);
                    }
                });
    }

    private void downloadHmdUpdate(TextView hmd_percentage, ProgressBar hmd_progress, Button
            restart_hmd_download, AppPreferencesHelper devicePref) {
        hmdDownloadStatus = Status.UNKNOWN;
        Log.e("getHMDDownloadLink", "" + devicePref.getHmdDownloadLink());
        PRDownloader.download(devicePref.getHmdDownloadLink(), getFilesDir().getAbsolutePath(), "HMD_latest.apk")
                .build()
                .setOnStartOrResumeListener(() -> hmd_progress.setIndeterminate(false))
                .setOnPauseListener(() -> {
                })
                .setOnCancelListener(() -> {
                    hmd_progress.setProgress(0);
                    hmd_percentage.setText("");
                    hmd_progress.setIndeterminate(false);
                })
                .setOnProgressListener(progress -> {
                    long progressPercent = progress.currentBytes * 100 / progress.totalBytes;
                    hmd_progress.setProgress((int) progressPercent);
                    hmd_percentage.setText(CommonUtils.getProgressDisplayLine(progress.currentBytes, progress.totalBytes));
                    hmd_progress.setIndeterminate(false);
                })
                .start(new OnDownloadListener() {
                    @Override
                    public void onDownloadComplete() {
                        hmdDownloadStatus = Status.COMPLETED;
                        enableHMDUpdate();
                    }

                    @Override
                    public void onError(Error error) {
                        hmdDownloadStatus = Status.UNKNOWN;
                        restart_hmd_download.setVisibility(View.VISIBLE);
                        hmd_percentage.setText("Error while downloading");
                        hmd_progress.setProgress(0);
                        hmd_progress.setIndeterminate(false);
                    }
                });
    }

    private void enableHMDUpdate() {
        TextView connectingHMD = findViewById(R.id.connectingHMD);
        LinearLayout buttonPanel = findViewById(R.id.buttonPanel);
        ProgressBar connectingHMDProgress = findViewById(R.id.connectingHMDProgress);
        Log.e("enableHMDUpdate", " tcDownloadStatus " + tcDownloadStatus + " hmdDownloadStatus " + hmdDownloadStatus);
        if (tcDownloadStatus == Status.COMPLETED && hmdDownloadStatus == Status.COMPLETED) {
            if (!isHotSpotOn) {
                MenuItem toggleHotSpot = menu.getItem(2);
                onOptionsItemSelected(toggleHotSpot);
                connectingHMD.setVisibility(View.VISIBLE);
                connectingHMDProgress.setVisibility(View.VISIBLE);
                buttonPanel.setVisibility(GONE);
                updateHandler.postDelayed(oneMinuteTimerRunnable, 60000);
            }
        } else {
            connectingHMD.setVisibility(GONE);
            connectingHMDProgress.setVisibility(GONE);
        }
    }


    private void waitScreenDelayedAction(long millis) {
        h.postDelayed(waitScreenDelay, millis);
    }


    private void showFABMenu() {
        boolean isHotspotActive = isHotspotOn(this);
        boolean isHmdActive = CommunicationService.isHMDConnected;
        boolean stat = MyApplication.getInstance().is_HMD_CONNECTION_NEEDED();
        if (stat) {
            if (isHotspotActive) {
                if (isHmdActive && communicationActive) {
                    isFABOpen = true;
                    batteryFabLayout.setVisibility(View.VISIBLE);
                    String bat = CommonUtils.splitHmdHeartBeat("Battery");
                    if (bat != null && !bat.equals("")) {
                        batteryLevel.setText(bat);
                        Log.i("'Mode1", " " + CommonUtils.splitHmdHeartBeat("ModeNo"));
                        Log.i("'Mode2", " " + CommonUtils.splitHmdHeartBeat("ModeTypeNo"));
                        Log.i("'Mode3", " " + CommonUtils.splitHmdHeartBeat("ModeStepNo"));
                    } else {
                        Log.d("batteryLevel", "is null or empty");
                        batteryLevel.setVisibility(View.INVISIBLE);
                    }
                    fab.setImageResource(R.drawable.baseline_close_white_18dp);
                    //fab.animate().rotationBy(45);
                    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
                        batteryFabLayout.animate().translationY(-getResources().getDimension(R.dimen.standard_55));
                    else
                        batteryFabLayout.animate().translationY(-55);
                } else {
                    CommonUtils.showToasty(this, "Getting Battery Info Please wait...", true, 'I');
                }
            }
        } else {
            CommonUtils.showToasty(this, "Hotspot turned off...", true, 'I');
        }
    }

    private void closeFABMenu() {
        isFABOpen = false;
        //fabBGLayout.setVisibility(View.GONE);
        fab.setImageResource(R.drawable.vr_float_purple_new2);
        //fab.animate().rotationBy(-45);
        batteryFabLayout.animate().translationY(0).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animator) {
                if (!isFABOpen) {
                    batteryFabLayout.setVisibility(GONE);
                }
            }

            @Override
            public void onAnimationEnd(Animator animator) {

            }

            @Override
            public void onAnimationCancel(Animator animator) {

            }

            @Override
            public void onAnimationRepeat(Animator animator) {

            }
        });
    }

    private void showMeTip(Bundle state) {
        try {
            String msg = state.getString("toastMessage");
            int id = state.getInt("tipId");
            Log.d("showMeTip", " msg" + msg + " id " + id);
            RadioButton radioButton;
            Spinner spinner;
            EditText editText;
            switch (id) {
                case R.id.patient_FirstName_editText_value:
                    editText = findViewById(R.id.patient_FirstName_editText_value);
                    editText.setError(msg);
                    editText.requestFocus();
                    Log.e("showMeTip", " patient_FirstName_editText_value");
                    break;
                case R.id.patient_mrn_number_editText_value:
                    editText = findViewById(R.id.patient_mrn_number_editText_value);
                    editText.requestFocus();
                    editText.setError(msg);
                    Log.e("showMeTip", " patient_mrn_number_editText_value");
                    break;
                case R.id.Patient_DOB_editText_value:
                    editText = findViewById(R.id.Patient_DOB_editText_value);
                    editText.requestFocus();
                    editText.setError(msg);
                    Log.e("showMeTip", " Patient_DOB_editText_value");
                    break;
                case R.id.patient_mobile_number_editText_value:
                    editText = findViewById(R.id.patient_mobile_number_editText_value);
                    editText.requestFocus();
                    editText.setError(msg);
                    Log.e("showMeTip", " patient_mobile_number_editText_value");
                    break;
                case R.id.radio_male:
                    radioButton = findViewById(R.id.radio_male);
                    radioButton.setError(msg);
                    Log.e("showMeTip", " radio_male");
                    break;
                case R.id.radioError:
                    TextView radioError = findViewById(R.id.radioError);
                    radioError.setError(msg);
                    Log.e("showMeTip", " radioError");
                    break;
                case R.id.test_strategy_spinner:
                    spinner = findViewById(R.id.test_strategy_spinner);
                    spinner.performClick();
                    Log.e("showMeTip", " test_strategy_spinner");
                    break;
                case R.id.test_pattern_spinner:
                    spinner = findViewById(R.id.test_pattern_spinner);
                    spinner.performClick();
                    Log.e("showMeTip", " test_pattern_spinner");
                    break;
                case R.id.editable_spherical_power:
                    editText = findViewById(R.id.editable_spherical_power);
                    editText.requestFocus();
                    editText.setError(msg);
                    tipId = 0;
                    showTip = false;
                    Log.e("showMeTip", " editable_spherical_power");
                    break;
            }
        } catch (Exception e) {
            Log.e("ShowMeTip", e.getMessage());
            FirebaseCrashlytics.getInstance().recordException(e);
        }
    }

    private void isRecordsFound(boolean state) {
        EditText patientMrnNumber = findViewById(R.id.patient_mrn_number_editText_value);
        if (state)
            patientMrnNumber.setEnabled(false);
        else
            patientMrnNumber.setEnabled(true);
    }

    private void toggleSearch() {
        ImageView searchMrn = findViewById(R.id.searchMrn);
        if (searchMrn.getContentDescription().toString().equals("back")) {
            searchMrn.setContentDescription("search");
            searchMrn.setImageResource(R.drawable.ic_search_black_48px);
        } else {
            searchMrn.setContentDescription("back");
            searchMrn.setImageResource(R.drawable.ic_baseline_keyboard_backspace_24px);
        }
    }

    private void enablePatientDetailsView(boolean state) {

        TextView patient_name_tv = findViewById(R.id.patient_name_tv);
        EditText patientFirstName = findViewById(R.id.patient_FirstName_editText_value);

        TextView patient_mobile_tv = findViewById(R.id.patient_mobile_tv);
        EditText patientMobileNumber = findViewById(R.id.patient_mobile_number_editText_value);

        TextView patient_dob_tv = findViewById(R.id.patient_dob_tv);
        EditText patientDOB = findViewById(R.id.Patient_DOB_editText_value);

        RadioGroup patientSexGroup = findViewById(R.id.PatientSex_radio_group);
        RadioButton male = findViewById(R.id.radio_male);
        RadioButton female = findViewById(R.id.radio_female);

        if (state) {
            patient_name_tv.setVisibility(View.VISIBLE);
            patientFirstName.setVisibility(View.VISIBLE);
            patientFirstName.setEnabled(true);

            patient_mobile_tv.setVisibility(View.VISIBLE);
            patientMobileNumber.setVisibility(View.VISIBLE);
            patientMobileNumber.setEnabled(true);

            patient_dob_tv.setVisibility(View.VISIBLE);
            patientDOB.setVisibility(View.VISIBLE);
            patientDOB.setEnabled(true);

            patientSexGroup.setVisibility(View.VISIBLE);
            male.setEnabled(true);
            female.setEnabled(true);
        } else {
            patient_name_tv.setVisibility(View.INVISIBLE);
            patientFirstName.setVisibility(View.INVISIBLE);
            patientFirstName.setEnabled(true);

            patient_mobile_tv.setVisibility(View.INVISIBLE);
            patientMobileNumber.setVisibility(View.INVISIBLE);
            patientMobileNumber.setEnabled(true);

            patient_dob_tv.setVisibility(View.INVISIBLE);
            patientDOB.setVisibility(View.INVISIBLE);
            patientDOB.setEnabled(true);

            patientSexGroup.setVisibility(View.INVISIBLE);
            male.setEnabled(true);
            female.setEnabled(true);
        }


    }

    public void animateFrameCard(View v) {
        CardView frontCard = findViewById(R.id.frontCard);
        CardView hiddenCard = findViewById(R.id.hiddenCard);
        if (frontCard.getVisibility() == View.VISIBLE) {
            frontCard.setVisibility(View.INVISIBLE);
            hiddenCard.setVisibility(View.VISIBLE);
        } else if (hiddenCard.getVisibility() == View.VISIBLE) {
            frontCard.setVisibility(View.VISIBLE);
            hiddenCard.setVisibility(View.INVISIBLE);
        }
    }

    public void animateCard(View v) {
        switch (v.getId()) {
            case R.id.add_wireless_network_card:
                CardView add_wireless_network_card_options = findViewById(R.id.add_wireless_network_card_options);
                if (add_wireless_network_card_options.getVisibility() == GONE) {
                    add_wireless_network_card_options.setVisibility(View.VISIBLE);
                    //add_wireless_network_card_options.animate().translationY(-(add_wireless_network_card_options.getHeight()));
                    //add_wireless_network_card_options.startAnimation(slide_down);
                } else
                    add_wireless_network_card_options.setVisibility(GONE);
                break;
            case R.id.add_email_account_card:
                CardView add_email_account_card_options = findViewById(R.id.add_email_account_card_options);
                if (add_email_account_card_options.getVisibility() == GONE)
                    add_email_account_card_options.setVisibility(View.VISIBLE);
                else
                    add_email_account_card_options.setVisibility(GONE);
                break;
            case R.id.add_printer_card:
                CardView add_wired_printer_card_options = findViewById(R.id.add_wired_printer_card_options);
                if (add_wired_printer_card_options.getVisibility() == GONE)
                    add_wired_printer_card_options.setVisibility(View.VISIBLE);
                else
                    add_wired_printer_card_options.setVisibility(GONE);
                break;
            default:
                ImageView arrow = findViewById(R.id.showHide);
                CardView hiddenCrad = findViewById(R.id.hiddenCard);
                hiddenCrad.setElevation(0);
                hiddenCrad.setBackgroundColor(getResources().getColor(R.color.light_grey));
                hiddenCrad.setMaxCardElevation(0);
                if (hiddenCrad.getVisibility() == GONE) {
                    hiddenCrad.setVisibility(View.VISIBLE);
                    arrow.animate().rotation(180).start();
                } else {
                    hiddenCrad.setVisibility(GONE);
                    arrow.animate().rotation(360).start();
                }
                break;

        }
    }

    private void showToast(String message) {
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
        //showBigToast(toast);
    }

    private void setResult() {
        Log.d("SetResult", "Called");
        finishActivity(30);
    }

    private boolean isDeviceHasGoogleAccount() {
        AccountManager accMan = AccountManager.get(this);
        Account[] accArray = accMan.getAccountsByType("com.google");
        return accArray.length >= 1;
    }

    public void datePicker(final View v) {
        CommonUtils.hideKeypad(v, MyApplication.getInstance());
        final EditText dob = v.findViewById(R.id.Patient_DOB_editText_value);
        final Calendar cal = Calendar.getInstance();
        try {
            String selectedDate = dob.getText().toString();
            if (!selectedDate.isEmpty()) {
                SimpleDateFormat sdf = CommonUtils.getDateFormat();
                cal.setTime(sdf.parse(selectedDate));
            }
        } catch (ParseException e) {
            Log.e("datePicker Exception", e.getMessage());
        }
        final Calendar myCalendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                Log.d("onDateSet", "Year " + year + " Month " + monthOfYear + " " + dayOfMonth);
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "yyyy-MM-dd";
                SimpleDateFormat sdf = CommonUtils.getDateFormat();

                dob.setText(sdf.format(myCalendar.getTime()));
                dob.setError(null);
            }
        };
        DatePickerDialog dialog = new DatePickerDialog(MainActivity.this, AlertDialog.THEME_HOLO_DARK, date,
                cal.get(Calendar.YEAR), cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH));
        dialog.getDatePicker().setMaxDate(CommonUtils.tenYearsBack());
        dialog.setTitle("Select the date");
        dialog.setCancelable(false);
        dialog.show();
    }

    private void startBackgroundWorks() {
        try {
            WorkCreator workCreator = new WorkCreator(this);
            workCreator.runSyncDbWorker();
            workCreator.runDatabaseBackUpWork();
            // workCreator.runCheckForUpdatesWorker();
            workCreator.runDailyNumberOfReports();
            //workCreator.runImmediateJsonWork();
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
        }
       /* IntentFilter packageReceiver = new IntentFilter();
        this.registerReceiver(new PackageWatcher(), packageReceiver);*/
    }

    private class BatteryBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            int status = intent.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
            isBatteryLevelSet = true;
            boolean isCharging = status == BatteryManager.BATTERY_STATUS_CHARGING ||
                    status == BatteryManager.BATTERY_STATUS_FULL;
            boolean hmd_status = MyApplication.getInstance().is_HMD_CONNECTED();
            /*if (isHotSpotOn)
                menu.getItem(2).setIcon(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_wifi_tethering_on));
            else
                menu.getItem(2).setIcon(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_wifi_tethering_off));*/
            if (!isCharging) {
                if (level <= 20)
                    menu.getItem(1).setIcon(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_battery_20_white_24dp));
                else if (level > 20 & level <= 30)
                    menu.getItem(1).setIcon(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_battery_30_white_24dp));
                else if (level > 30 & level <= 50)
                    menu.getItem(1).setIcon(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_battery_50_white_24dp));
                else if (level > 50 & level <= 60)
                    menu.getItem(1).setIcon(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_battery_60_white_24dp));
                else if (level > 60 & level <= 80)
                    menu.getItem(1).setIcon(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_battery_80_white_24dp));
                else if (level > 80 & level <= 90)
                    menu.getItem(1).setIcon(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_battery_90_white_24dp));
                else
                    menu.getItem(1).setIcon(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_battery_full_white_24dp));
            } else {
                if (level <= 20)
                    menu.getItem(1).setIcon(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_battery_charging_20_white_24dp));
                else if (level > 20 & level <= 30)
                    menu.getItem(1).setIcon(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_battery_charging_30_white_24dp));
                else if (level > 30 & level <= 50)
                    menu.getItem(1).setIcon(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_battery_charging_50_white_24dp));
                else if (level > 50 & level <= 60)
                    menu.getItem(1).setIcon(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_battery_charging_60_white_24dp));
                else if (level > 60 & level <= 80)
                    menu.getItem(1).setIcon(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_battery_charging_80_white_24dp));
                else if (level > 80 & level <= 90)
                    menu.getItem(1).setIcon(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_battery_charging_90_white_24dp));
                else
                    menu.getItem(1).setIcon(ContextCompat.getDrawable(MainActivity.this, R.drawable.ic_battery_charging_full_white_24dp));
            }
        }

    }

    private static class UpdateVisibilityAsyncTask extends AsyncTask<Void, Void, Integer> {

        private WeakReference<MainActivity> weakActivity;

        UpdateVisibilityAsyncTask(MainActivity activity) {
            weakActivity = new WeakReference<>(activity);
        }

        @Override
        protected Integer doInBackground(Void... params) {
            List<PatientTestResult> patientTestResults = getUnSyncedData(AppDatabase.getAppDatabase(MyApplication.getInstance()));
            return patientTestResults.size();
        }

        @Override
        protected void onPostExecute(Integer unsyncedCount) {
            MainActivity activity = weakActivity.get();
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

    private static void turnOffOrRebootHMD(final Activity activity) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(new ContextThemeWrapper(activity, R.style.myDialog));
        builder.setTitle("Action alert")
                .setMessage("Are you sure?")
                .setCancelable(false)
                .setPositiveButton("TurnOff", (dialog, which) -> {
                    Actions.beginShutDown();
                    Store.newTestVisibility = false;
                })
                .setNegativeButton("Reboot", (dialog, which) -> {
                    Actions.beginReboot();
                    Store.newTestVisibility = false;
                }).setNeutralButton("Cancel", (dialog, which) -> {
        });
        androidx.appcompat.app.AlertDialog alert = builder.create();
        alert.show();
    }

    void dialogOne() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(new ContextThemeWrapper(this, R.style.myDialog));
        builder.setTitle("Are You Sure ?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, which) -> {
                    Actions.beginTestControllerSettings();
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                });
        androidx.appcompat.app.AlertDialog alert = builder.create();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alert.show();
    }

    void cancelDownloadAndGoToSettingsScreen() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(new ContextThemeWrapper(this, R.style.myDialog));
        builder.setTitle("Are You Sure ?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, which) -> {
                    PRDownloader.cancelAll();
                    Actions.beginTestControllerSettings();
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                });
        androidx.appcompat.app.AlertDialog alert = builder.create();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alert.show();
    }

    void dialogTwo() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(new ContextThemeWrapper(this, R.style.myDialog));
        builder.setTitle("Are You Sure ?")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, which) -> {
                    Actions.beginTestControllerSettings();
                    updateHandler.removeCallbacks(oneMinuteTimerRunnable);
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                });
        androidx.appcompat.app.AlertDialog alert = builder.create();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alert.show();
    }

    void dialogThree() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(new ContextThemeWrapper(this, R.style.myDialog));
        builder.setMessage("Permission must be granted to go ahead. Installation cannot proceed otherwise.")
                .setCancelable(false)
                .setPositiveButton("Proceed", (dialog, which) -> {
                    isUpdateCalled = false;
                    updateHandler.removeCallbacks(threeMinuteTimerRunnable);
                    isTcUpdateSuccess[0] = CommonUtils.packageInstaller(MainActivity.this);
                });
        androidx.appcompat.app.AlertDialog alert = builder.create();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alert.show();
    }

    void versionMismatchDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(new ContextThemeWrapper(this, R.style.myDialog));
        builder.setTitle("System Alert")
                .setMessage("There is a mismatch of software versions. This could have been caused during a previous software update. Click proceed to complete the software update.")
                .setCancelable(false)
                .setPositiveButton("Proceed", (dialog, which) -> {
                    if (CommonUtils.is_HMD_Version_High())
                        Actions.beginTcUpdateAlready();
                    else
                        Actions.beginHMDDownloadedAlready();
                }).setNegativeButton("Back", (dialog, which) -> {
            cannotDoTest();
        });

        androidx.appcompat.app.AlertDialog alert = builder.create();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alert.show();
    }

    void cannotDoTest() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(new ContextThemeWrapper(this, R.style.myDialog));
        builder.setTitle("System Alert")
                .setMessage("New tests cannot be done unless software version mismatch is fixed.")
                .setCancelable(false)
                .setPositiveButton("Yes", (dialog, which) -> {
                    if (CommonUtils.is_HMD_Version_High())
                        Actions.beginTcUpdateAlready();
                    else
                        Actions.beginHMDDownloadedAlready();
                })
                .setNegativeButton("Cancel", (dialog, which) -> {
                });
        androidx.appcompat.app.AlertDialog alert = builder.create();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alert.show();
    }

    void prbValidityReached() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(new ContextThemeWrapper(this, R.style.myDialog));
        builder.setTitle("System Alert")
                .setMessage("PRB reaches it limit, please contact customer care")
                .setCancelable(false)
                .setPositiveButton("Okay", (dialog, which) -> {
                    int count = CommonUtils.getPRBCount(MainActivity.this);
                    String msg = "PRB reaches the limit " + count;
                    FirebaseCrashlytics.getInstance().recordException(new PrbException(msg));
                });

        androidx.appcompat.app.AlertDialog alert = builder.create();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alert.show();
    }

    TextWatcher enter_user_password_Watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int
                count, int after) {
            re_enter_user_password.setEnabled(false);
            set_user_password_proceed_button.setVisibility(View.INVISIBLE);
            re_enter_user_password_layout.setErrorEnabled(false);
            re_enter_user_password_layout.setError(null);
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!isUserPasswordSizeQualifies(enter_user_password)) {
                enter_user_password_layout.setError("Password should be 4 digits!");
                re_enter_user_password.setEnabled(false);
                if (isPasswordEmpty(enter_user_password))
                    re_enter_user_password_layout.setError(null);
            } else {
                re_enter_user_password.setEnabled(true);
                enter_user_password_layout.setErrorEnabled(false);
                enter_user_password_layout.setError(null);
                re_enter_user_password_layout.setErrorEnabled(false);
                re_enter_user_password_layout.setError(null);

            }
        }
    };

    TextWatcher re_enter_user_password_Watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int
                count, int after) {
            re_enter_user_password_layout.setErrorEnabled(false);
            re_enter_user_password_layout.setError(null);
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {

            if (isPasswordEmpty(re_enter_user_password)) {
                re_enter_user_password_layout.setError(null);
                set_user_password_proceed_button.setVisibility(View.INVISIBLE);
            } else if (!isUserPasswordMatches(enter_user_password, re_enter_user_password)) {
                set_user_password_proceed_button.setVisibility(View.INVISIBLE);
                enter_user_password_layout.setError("Password doesn't match!");
                re_enter_user_password_layout.setError("Password doesn't match!");
            } else {
                set_user_password_proceed_button.setVisibility(View.VISIBLE);
                enter_user_password_layout.setErrorEnabled(false);
                enter_user_password_layout.setError(null);
                re_enter_user_password_layout.setErrorEnabled(false);
                re_enter_user_password_layout.setError(null);
            }
        }
    };

    TextWatcher enter_admin_password_Watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int
                count, int after) {
            enter_admin_password_layout.setErrorEnabled(false);
            enter_admin_password_layout.setError(null);
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (isPasswordEmpty(enter_admin_password)) {
                enter_admin_password_layout.setError(null);
                set_admin_password_proceed_button.setVisibility(View.INVISIBLE);
            } else if (enter_admin_password.getText().toString().equals(CommonUtils.getHotSpotId())) {
                enter_admin_password_layout.setError(null);
                re_enter_admin_password.setEnabled(true);
            } else if (!isValidPassword(enter_admin_password.getText().toString())) {
                enter_admin_password_layout.setError("Must contain at least one character of 0-9 a-z A-Z [@#$%^&+=]");
                re_enter_admin_password.setEnabled(false);
                set_admin_password_proceed_button.setVisibility(View.INVISIBLE);
            } else if (!isAdminPasswordSizeQualifies(enter_admin_password)) {
                enter_admin_password_layout.setError("Password length should be at least 8");
                re_enter_admin_password.setEnabled(false);
                set_admin_password_proceed_button.setVisibility(View.INVISIBLE);
            } else {
                enter_admin_password_layout.setError(null);
                re_enter_admin_password.setEnabled(true);
            }
        }
    };

    TextWatcher re_enter_admin_password_Watcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int
                count, int after) {
            re_enter_admin_password_layout.setErrorEnabled(false);
            re_enter_admin_password_layout.setError(null);
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            if (!isItNewPassword()) {
                re_enter_admin_password_layout.setError("New Password doesn't match");
                enter_admin_password_layout.setError("New Password doesn't match");
            } else if (isPasswordEmpty(re_enter_admin_password)) {
                re_enter_admin_password_layout.setError(null);
                enter_admin_password_layout.setError(null);
            } else {
                re_enter_admin_password_layout.setError(null);
                enter_admin_password_layout.setError(null);
                set_admin_password_proceed_button.setVisibility(View.VISIBLE);
            }
        }
    };

    void showHotSpotStatusDialog(boolean status) {
        Context context = new ContextThemeWrapper(MainActivity.this, R.style.AppTheme2);
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

