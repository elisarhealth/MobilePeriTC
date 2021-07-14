package com.agyohora.mobileperitc.store;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.BatteryManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.agyohora.mobileperitc.BuildConfig;
import com.agyohora.mobileperitc.R;
import com.agyohora.mobileperitc.actions.Actions;
import com.agyohora.mobileperitc.asynctasks.InsertRecordIntoDb;
import com.agyohora.mobileperitc.communication.CommunicationService;
import com.agyohora.mobileperitc.communication.WifiCommunicationManager;
import com.agyohora.mobileperitc.data.database.AppDatabase;
import com.agyohora.mobileperitc.data.database.DatabaseInitializer;
import com.agyohora.mobileperitc.data.preferences.AppPreferencesHelper;
import com.agyohora.mobileperitc.exceptions.ComponentFailureException;
import com.agyohora.mobileperitc.exceptions.HmdCameraRestartedException;
import com.agyohora.mobileperitc.exceptions.ScreenBrightnessException;
import com.agyohora.mobileperitc.filetransfer.FileServer;
import com.agyohora.mobileperitc.myapplication.MyApplication;
import com.agyohora.mobileperitc.ui.MainActivity;
import com.agyohora.mobileperitc.utils.ClickerStatus;
import com.agyohora.mobileperitc.utils.CommonUtils;
import com.agyohora.mobileperitc.utils.DisplayStatus;
import com.agyohora.mobileperitc.utils.ElectronicInterfaceStatus;
import com.agyohora.mobileperitc.utils.EyeTrackingStatus;
import com.agyohora.mobileperitc.utils.HmdBatteryStatus;
import com.google.android.gms.common.util.ArrayUtils;
import com.google.firebase.crashlytics.FirebaseCrashlytics;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static android.content.Context.BATTERY_SERVICE;
import static com.agyohora.mobileperitc.actions.Actions.abortTest;
import static com.agyohora.mobileperitc.actions.Actions.beginTest;
import static com.agyohora.mobileperitc.actions.Actions.foveaFeedback;
import static com.agyohora.mobileperitc.communication.WifiCommunicationManager.isHotspotOn;
import static com.agyohora.mobileperitc.ui.MainActivity.applicationContext;
import static com.agyohora.mobileperitc.ui.MainActivity.chronometerForDurationCalculation;
import static com.agyohora.mobileperitc.ui.MainActivity.isChronometerRunning;
import static com.agyohora.mobileperitc.ui.MainActivity.timeWhenStopped;
import static com.agyohora.mobileperitc.utils.AppConstants.DEVICE_PREF;
import static com.agyohora.mobileperitc.utils.AppConstants.PREF_NAME;
import static com.agyohora.mobileperitc.utils.AppConstants.RESULT_PREF;
import static com.agyohora.mobileperitc.utils.CommonUtils.abortTestConfirmationDialog;
import static com.agyohora.mobileperitc.utils.CommonUtils.getCurrentDate;
import static com.agyohora.mobileperitc.utils.CommonUtils.getCurrentTime;
import static com.agyohora.mobileperitc.utils.CommonUtils.isValidJson;
import static com.agyohora.mobileperitc.utils.CommonUtils.nullCheck;
import static com.agyohora.mobileperitc.utils.CommonUtils.writeToFile;
import static com.agyohora.mobileperitc.utils.ElectronicInterfaceStatus.EI_MODULE_CONNECTED;
import static com.agyohora.mobileperitc.utils.ElectronicInterfaceStatus.EI_MODULE_DISCONNECTED;
import static com.agyohora.mobileperitc.utils.ElectronicInterfaceStatus.INITIALIZATION_FAILED;
import static com.agyohora.mobileperitc.utils.ElectronicInterfaceStatus.SILENTLY_FAILED;
import static com.agyohora.mobileperitc.utils.ElectronicInterfaceStatus.SILENTLY_STARTED;
import static com.agyohora.mobileperitc.utils.ElectronicInterfaceStatus.UNDEFINED;
import static com.agyohora.mobileperitc.utils.HmdBatteryStatus.BATTERY_LOW_KEEP_CHARGING;
import static com.agyohora.mobileperitc.utils.HmdBatteryStatus.BATTERY_LOW_PLEASE_CHARGE_HMD;
import static com.agyohora.mobileperitc.utils.HmdBatteryStatus.BATTERY_OK;
import static com.agyohora.mobileperitc.utils.HmdBatteryStatus.PLUG_OUT_CHARGE_OK;
import static com.google.common.base.Predicates.not;

/**
 * Created by namputo on 09/06/17.
 */

@SuppressWarnings("AccessStaticViaInstance")
public class Store {

    private static final String TAG = "Store";

    private static String transferMessage = "Please wait...";

    public static int activeView_number = R.layout.activity_home_screen;
    public static int previousView_number = 0;
    public static boolean newTestVisibility = false;
    public static String hmdModeWithBattery = "0 0 0 0 0";
    public static boolean isHotSpotOn = false;
    public static String batteryLevel = null;
    public static boolean batteryLevelVisibility = false;
    //state variables related to calibration screen
    public static String calibrationStatus = null;
    public static int calibrationSaveProceedVisibility = View.GONE;
    public static String calibrationRecalibration = "Calibrate";
    public static String toastMessage = null;
    public static int tipId = 0;
    public static int recordsInserted = 0, recordMismatched = 0, recordsDuplicated = 0, totalNumberOfRecords = 0;
    public static boolean showTip = false;
    public static boolean isCommInitializationOver = false;
    public static boolean communicationActive = false;
    public static boolean isAbortClicked = false;
    public static boolean isHmdAccessibilityEnabled = false;
    public static ElectronicInterfaceStatus electronicInterfaceStatus;
    public static HmdBatteryStatus hmdBatteryStatus;
    public static EyeTrackingStatus eyeTrackingStatus;
    public static ClickerStatus clickerStatus;
    public static DisplayStatus displayStatus;
    public static Set<String> mySet;
    public static CircularFifoQueue<String> lastFiveDisplayStatus = new CircularFifoQueue<>(5);
    public static String accessoriesChecked = "";
    public static String displayStatusFromHMD;
    public static double screenBrightnessLevelPD1;
    public static double screenBrightnessLevelPD2;
    public static String screenBrightnessLevelPD1String;
    public static String screenBrightnessLevelPD2String;
    public static String screenBrightnessCombo;
    public static boolean isPhotoDiode1Working = false;
    public static boolean isPhotoDiode2Working = false;
    private static long dt_timebase = SystemClock.elapsedRealtime();
    private static Bundle stateBundle = new Bundle();
    private static String patientName = null;
    private static String calibData = null;
    private static int preTestPrbCounter = 0;
    private static String patientPhoneNumberVal = null;
    private static String patientMrnNumberVal = null;
    private static String patientDOBVal = null;
    private static String patientSexVal = "";
    private static String patientTestEyeVal = "Left Eye";
    private static String patientTestPatternVal = "Select test pattern";
    private static String patientTestStrategyVal = "Select test strategy";
    private static String testSphericalInput = null;
    private static String testCylindricalInput = null;
    private static String testCylindricalAxisInput = null;
    private static String testSphericalVal = null;
    private static String testCylindricalVal = null;
    private static String testCylindricalAxisVal = null;
    private static String setPatientName = null;
    private static String setPatientPhoneNumberVal = null;
    private static String setPatientMrnNumberVal = null;
    private static String setPatientDOBVal = null;
    private static String setPatientSexVal = "";
    private static String setPatientTestEyeVal = "Left Eye";
   // private static String IPD_button_Visiblity = "Not_Visibile";
    private static String setPatientTestPatternVal = "Select test pattern";
    private static String setPatientTestStrategyVal = "Select test strategy";
    private static String setTestSphericalVal = null;
    private static String setTestCylindricalVal = null;
    private static String setTestCylindricalAxis = null;
    private static String insertedResultID = null;
    private static String insertedDateTime = null;
    private static String fovea = null;
    public static boolean isFoveaProceeded = false;
    private static PerimetryObject_V2.FinalPerimetryResultObject finalPerimetryTestFinalResultObject;
    //during test specific
    private static boolean dt_chronometerOn;
    private static boolean dt_testOver = false;
    private static ArrayList<String> dt_new_result = new ArrayList<>();
    private static ArrayList<String> dt_result_sensitivity = new ArrayList<>();
    private static ArrayList<String> dt_result_seen = new ArrayList<>();
    private static ArrayList<String> dt_result_seen_temp = new ArrayList<>();
    private static ArrayList<String> dt_result_deviation = new ArrayList<>();
    private static ArrayList<String> dt_result_probabilityDeviationValue = new ArrayList<>();
    private static ArrayList<String> dt_result_generalizedDefectCorrectedSensitivityDeviationValue = new ArrayList<>();
    private static ArrayList<String> dt_result_generalizedDefectCorrectedSensitivityDeviationProbabilityValue = new ArrayList<>();
    private static double[][][] quadrants;
    private static String psd = null;
    private static String duration = null;
    private static String meanDeviation = null;
    private static String dia = null;
    private static String backGround = null;
    private static String visualAcuity = null;
    private static int FP_Numerator = 0;
    private static int FP_Denominator = 0;
    private static int FL_Numerator = 0;
    private static int FL_Denominator = 0;
    private static int FN_Numerator = 0;
    private static int FN_Denominator = 0;
    public static boolean isDisplayStatusUpdated = false;
    private static String ght = " ";
    private static String vfi = " ";
    private static double PD_Probabiltiy = 0;
    private static double MD_Probabiltiy = 0;
    //generic toast flag
    private static boolean showToast = false;
    private static testInformation testInfo = new testInformation();
    private static TCButtonData buttonClickData = new TCButtonData();
    private static int clickDataHashCode;
    private static String sentData;
    private static Bundle clickData = new Bundle();
    static AlertDialog alert;
    private static String imageFileName = "DUMMY";
    private static Gson gson = new GsonBuilder().excludeFieldsWithModifiers(Modifier.TRANSIENT)
            .serializeSpecialFloatingPointValues()
            .create();
    public static BroadcastReceiver storeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, Intent intent) {
            try {
                Bundle receivedPayload = intent.getBundleExtra(Actions.DISPATCH_PAYLOAD);
                String actionName = nullCheck(receivedPayload.getString("name")) ? receivedPayload.getString("name") : "";
                Object actionData = receivedPayload.get("data");
                Log.d("InstoreReceiver", " storeReceiver");
                Log.d("InstoreReceiver", " Action Name " + actionName);
                decideNext(actionName, actionData);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
            }

        }
    };

    Store() {
        AppPreferencesHelper devicePreferencesHelper = new AppPreferencesHelper(MyApplication.getInstance(), DEVICE_PREF);
        if (devicePreferencesHelper.getProductionSetUpStatus() && devicePreferencesHelper.getUserSetUpStatus())
            activeView_number = R.layout.activity_home_screen;
        else
            activeView_number = R.layout.hmd_sync_check_activity;
    }

    private static void decideNext(String actionName, Object actionData) {
        AppPreferencesHelper appPreferencesHelper = new AppPreferencesHelper(MainActivity.applicationContext, PREF_NAME);

        switch (actionName != null ? actionName : "") {

            case Actions.ACTION_BACK_TO_LAUNCH_SCREEN:
                activeView_number = R.layout.activity_launcher_screen;
                StoreTransmitter.updatedUIState("ACTION_BACK_TO_LAUNCH_SCREEN");
                break;

            case Actions.ACTION_START_INIT_SETTINGS:
                activeView_number = R.layout.other_settings_activity;
                StoreTransmitter.updatedUIState("ACTION_START_INIT_SETTINGS");
                break;
            case Actions.ACTION_SET_ADMIN_PASSWORD:
                activeView_number = R.layout.set_admin_password_during_setup;
                StoreTransmitter.updatedUIState("ACTION_SET_ADMIN_PASSWORD");
                break;
            case Actions.ACTION_SET_USER_PASSWORD:
                activeView_number = R.layout.set_user_password_during_setup;
                StoreTransmitter.updatedUIState("ACTION_SET_ADMIN_PASSWORD");
                break;
            case Actions.ACTION_CHECK_FOR_UPDATES:
                activeView_number = R.layout.check_for_updates_screen;
                StoreTransmitter.updatedUIState("ACTION_CHECK_FOR_UPDATES");
                break;
            case Actions.ACTION_SET_SW_UPTO_DATE:
                activeView_number = R.layout.software_upto_date;
                StoreTransmitter.updatedUIState("ACTION_SET_SW_UPTO_DATE");
                break;
            case Actions.ACTION_UPDATE_AVAILABLE:
                activeView_number = R.layout.software_update_available;
                StoreTransmitter.updatedUIState("ACTION_UPDATE_AVAILABLE");
                break;
            case Actions.ACTION_UPDATE_CONNECTED_TO_HMD:
                activeView_number = R.layout.connected_to_hmd_during_update;
                StoreTransmitter.updatedUIState("ACTION_UPDATE_CONNECTED_TO_HMD");
                break;

            case Actions.ACTION_START_HOME:
                activeView_number = R.layout.activity_home_screen;
                StoreTransmitter.updatedUIState("ACTION_START_HOME");
                break;

            case Actions.ACTION_GO_HOME_IMMEDIATELY:
                activeView_number = R.layout.activity_home_screen;
                StoreTransmitter.updatedUIState("ACTION_GO_HOME_IMMEDIATELY");
                break;

            case Actions.ACTION_OPEN_ADMIN_PROFILE:
                activeView_number = R.layout.activity_admin_profile;
                StoreTransmitter.updatedUIState("ACTION_OPEN_ADMIN_PROFILE");
                break;

            case Actions.ACTION_OPEN_USER_PROFILE:
                activeView_number = R.layout.activity_user_profile;
                StoreTransmitter.updatedUIState("ACTION_OPEN_USER_PROFILE");
                break;

            case Actions.ACTION_ABORT_CALIB_AND_GO_HOME_IMMEDIATELY:
                // Actions.abortCalibration();
                Actions.goHomeImmediately();
                break;

            case Actions.ACTION_HMD_DETAILS:
                activeView_number = R.layout.hmd_settings;
                StoreTransmitter.updatedUIState("ACTION_HMD_DETAILS");
                break;

            case Actions.ACTION_BACK_TO_HOME_SCREEN:
                buttonClickData.buttonName = "BackToHomeScreen";
                buttonClickData.buttonData = "Home";
                clickData.putString("onPass", "HomePass");
                clickData.putString("onFail", "HomeFail");
                sentData = gson.toJson(buttonClickData);
                clickData.putInt("hashcode", sentData.hashCode());
                clickData.putString("data", sentData);
                if (communicationActive) {
                    StoreTransmitter.doCommFunction(StoreTransmitter.COMM_FUNCTION_CLICKONTC, clickData);
                    showToast = false;
                } else {
                    showToast = true;
                    toastMessage = "Connection with HMD Lost.Please Wait";
                }
                break;

            case Actions.ACTION_INITIALIZE_COMMUNICATION:
                Log.e("INTIALIZE_COMMUNICATION", " " + communicationActive);
                Log.e("INTIALIZE_COMMUNICATION", " " + isCommInitializationOver);
                if (!communicationActive || !isCommInitializationOver) {
                    isHotSpotOn = true;
                    setClickData("Button", "NA");
                    StoreTransmitter.doCommFunction(StoreTransmitter.COMM_FUNCTION_INITIALIZE, clickData);
                }
                /*if (!communicationActive) {
                    if (!isCommInitializationOver)
                        StoreTransmitter.doCommFunction(StoreTransmitter.COMM_FUNCTION_INITIALIZE, clickData);
                } else {
                    if (!isCommInitializationOver) {
                        setClickData("Button", "NA");
                        StoreTransmitter.doCommFunction(StoreTransmitter.COMM_FUNCTION_INITIALIZE, clickData);
                    }

                }*/
                //StoreTransmitter.updatedUIState();
                break;

            case Actions.ACTION_BACK_TO_PATIENT_PRIMARY_DETAILS:
                RuntimeMode.modeNo = 0;
                RuntimeMode.modeTypeNo = 1000;
                new AppPreferencesHelper(applicationContext, PREF_NAME).setPatientDetailsViewVisibility(true);
                activeView_number = R.layout.activity_primary_patient_details;
                Log.e(TAG, "ACTION_BACK_TO_PATIENT_PRIMARY_DETAILS called");
                StoreTransmitter.updatedUIState("ACTION_BACK_TO_PATIENT_PRIMARY_DETAILS");
                break;

            case Actions.ACTION_BACK_TO_TEST_PRIMARY_DETAILS:
                RuntimeMode.modeNo = 0;
                RuntimeMode.modeTypeNo = 1100;
                activeView_number = R.layout.activity_primary_test_details;
                StoreTransmitter.updatedUIState("ACTION_BACK_TO_TEST_PRIMARY_DETAILS");
                break;

            case Actions.ACTION_BACK_TO_LENS_POWER_SETTINGS:
                RuntimeMode.modeNo = 0;
                RuntimeMode.modeTypeNo = 1200;
                activeView_number = R.layout.activity_lens_power_settings;
                StoreTransmitter.updatedUIState("ACTION_BACK_TO_LENS_POWER_SETTINGS");
                break;


            case Actions.ACTION_BACK_TO_CALIBRATION_SETTINGS:
                RuntimeMode.modeNo = 3;
                activeView_number = R.layout.activity_calibiration;
                StoreTransmitter.updatedUIState("ACTION_BACK_TO_CALIBRATION_SETTINGS");
                break;

            case Actions.ACTION_BACK_TO_IPD_SETTINGS:
                RuntimeMode.modeNo = 2;
                activeView_number = R.layout.activity_ipd_settings;
                StoreTransmitter.updatedUIState("ACTION_BACK_TO_IPD_SETTINGS");
                break;

            case Actions.ACTION_OPEN_HMD_SYNC:
                activeView_number = R.layout.hmd_sync_check_activity;
                StoreTransmitter.updatedUIState("ACTION_OPEN_HMD_SYNC");
                break;

            case Actions.ACTION_COMMUNICATION_CONTINUE:
                StoreTransmitter.doCommFunction(StoreTransmitter.COMM_FUNCTION_CONTINUE, clickData);
                break;

            case Actions.ACTION_DO_CALIBRATION:
                updateBehaviour();
                buttonClickData.buttonName = "Calibrate";
                buttonClickData.buttonData = "Calibrate";
                clickData.putString("onPass", "CalibrationPass");
                clickData.putString("onFail", "CalibrationFail");
                sentData = gson.toJson(buttonClickData);
                clickData.putInt("hashcode", sentData.hashCode());
                clickData.putString("data", sentData);
                if (communicationActive) {
                    StoreTransmitter.doCommFunction(StoreTransmitter.COMM_FUNCTION_CLICKONTC, clickData);
                    showToast = false;
                } else {
                    showToast = true;
                    toastMessage = "Connection with HMD Lost.Please Wait";
                }
                StoreTransmitter.updatedUIState("ACTION_DO_CALIBRATION");
                break;

            case Actions.ACTION_SKIP_CALIBRATION:
                updateBehaviour();
                buttonClickData.buttonName = "SkipCalibration";
                buttonClickData.buttonData = "SkipCalibration";
                clickData.putString("onPass", "SkipCalibrationPass");
                clickData.putString("onFail", "SkipCalibrationFail");
                sentData = gson.toJson(buttonClickData);
                clickData.putInt("hashcode", sentData.hashCode());
                clickData.putString("data", sentData);
                if (communicationActive) {
                    StoreTransmitter.doCommFunction(StoreTransmitter.COMM_FUNCTION_CLICKONTC, clickData);
                    showToast = false;
                } else {
                    showToast = true;
                    toastMessage = "Connection with HMD Lost.Please Wait";
                }
                StoreTransmitter.updatedUIState("ACTION_SKIP_CALIBRATION");
                break;

            case Actions.ACTION_BEGIN_PRODUCTION_SYNC:
                //CommonUtils.writeToMyDebugFile("ProductionSync initiated");
                //CommonUtils.writeToMyDebugFile("ProductionSync data");
                buttonClickData.buttonName = "ProductionSync";
                buttonClickData.buttonData = CommonUtils.readConfig("ACTION_BEGIN_PRODUCTION_SYNC").toString();
                //CommonUtils.writeToMyDebugFile("ProductionSync data " + CommonUtils.readConfig().toString());
                clickData.putString("onPass", "ProductionSyncPass");
                clickData.putString("onFail", "ProductionSyncFail");
                sentData = gson.toJson(buttonClickData);
                clickData.putInt("hashcode", sentData.hashCode());
                clickData.putString("data", sentData);
                StoreTransmitter.doCommFunction(StoreTransmitter.COMM_FUNCTION_CLICKONTC, clickData);
                break;

            case Actions.ACTION_BEGIN_USER_SYNC:
                buttonClickData.buttonName = "UserSync";
                buttonClickData.buttonData = CommonUtils.readConfig("ACTION_BEGIN_USER_SYNC").toString();
                clickData.putString("onPass", "UserSyncPass");
                clickData.putString("onFail", "UserSyncFail");
                sentData = gson.toJson(buttonClickData);
                clickData.putInt("hashcode", sentData.hashCode());
                clickData.putString("data", sentData);
                StoreTransmitter.doCommFunction(StoreTransmitter.COMM_FUNCTION_CLICKONTC, clickData);
                break;

            case Actions.ACTION_BEGIN_REBOOT:
                buttonClickData.buttonName = "reboot";
                buttonClickData.buttonData = "reboot";
                clickData.putString("onPass", "RebootPass");
                clickData.putString("onFail", "RebootFail");
                sentData = gson.toJson(buttonClickData);
                clickData.putInt("hashcode", sentData.hashCode());
                clickData.putString("data", sentData);
                StoreTransmitter.doCommFunction(StoreTransmitter.COMM_FUNCTION_CLICKONTC, clickData);
                break;
            case Actions.ACTION_TAKE_SCREENSHOT:
                buttonClickData.buttonName = "TakeScreenshot";
                buttonClickData.buttonData = "TakeScreenshot";
                clickData.putString("onPass", "TakeScreenshotPass");
                clickData.putString("onFail", "TakeScreenshotFail");
                sentData = gson.toJson(buttonClickData);
                clickData.putInt("hashcode", sentData.hashCode());
                clickData.putString("data", sentData);
                StoreTransmitter.doCommFunction(StoreTransmitter.COMM_FUNCTION_CLICKONTC, clickData);
                break;

            case Actions.ACTION_BEGIN_SHUTDOWN:
                buttonClickData.buttonName = "shutdown";
                buttonClickData.buttonData = "shutdown";
                clickData.putString("onPass", "ShutDownPass");
                clickData.putString("onFail", "ShutDownFail");
                sentData = gson.toJson(buttonClickData);
                clickData.putInt("hashcode", sentData.hashCode());
                clickData.putString("data", sentData);
                StoreTransmitter.doCommFunction(StoreTransmitter.COMM_FUNCTION_CLICKONTC, clickData);
                break;

            case Actions.ACTION_TEST_DETAILS:
                prepareForNewTest();
                updateBehaviour();
                break;
            case Actions.ACTION_BEGIN_HMD_UPDATE:
                Actions.startHmdUpdateTransfer();
                activeView_number = R.layout.installing_hmd_software;
                StoreTransmitter.updatedUIState("ACTION_BEGIN_HMD_UPDATE");
                break;
            case Actions.ACTION_BEGIN_HMD_UPDATE_ALREADY:
                activeView_number = R.layout.connected_to_hmd_during_hmd_version_mismatch;
                StoreTransmitter.updatedUIState("ACTION_BEGIN_HMD_UPDATE_ALONE");
                break;
            case Actions.ACTION_BEGIN_TC_UPDATE_ALREADY:
                activeView_number = R.layout.connected_to_hmd_during_tc_version_mismatch;
                StoreTransmitter.updatedUIState("ACTION_BEGIN_TC_UPDATE_ALONE");
                break;
            case Actions.ACTION_BEGIN_HMD_UPDATE_ALONE:
                Actions.startHmdUpdateTransfer();
                activeView_number = R.layout.installing_hmd_alone;
                StoreTransmitter.updatedUIState("ACTION_BEGIN_HMD_UPDATE_ALONE");
                break;
            case Actions.ACTION_BEGIN_TC_UPDATE_ALONE:
                Actions.startTCUpdate();
                activeView_number = R.layout.installing_test_controller_alone;
                StoreTransmitter.updatedUIState("ACTION_BEGIN_TC_UPDATE_ALONE");
                break;
            case Actions.ACTION_CLOSE_THE_TC:
                activeView_number = R.layout.closing_the_app;
                StoreTransmitter.updatedUIState("ACTION_BEGIN_TC_UPDATE_ALONE");
                break;
            case Actions.ACTION_START_LOGOUT_DECOY:
                activeView_number = R.layout.logout_decoy;
                StoreTransmitter.updatedUIState("ACTION_START_LOGOUT_DECOY");
                break;

            case Actions.ACTION_SHOW_TEST_DETAILS_AND_STOP_TEST:
                activeView_number = R.layout.activity_primary_test_details;
                Actions.silentlyStopTest();
                StoreTransmitter.updatedUIState("ACTION_SHOW_TEST_DETAILS_AND_STOP_EI");
                break;

            case Actions.ACTION_START_EI_COMPONENT_FAILURE:
                activeView_number = R.layout.error_component_failure;
                StoreTransmitter.updatedUIState("ACTION_START_EI_COMPONENT_FAILURE");
                break;

            case Actions.ACTION_PATIENT_DETAILS:
                validateTestDetailsScreen();
                updateBehaviour();
                break;
            case Actions.ACTION_PATIENT_DETAILS_WITHOUT_EI:
                activeView_number = R.layout.activity_primary_patient_details;
                StoreTransmitter.updatedUIState("ACTION_PATIENT_DETAILS_WITHOUT_EI");
                break;
            case Actions.ACTION_SETTINGS:
                activeView_number = R.layout.test_controller_settings;
                StoreTransmitter.updatedUIState("ACTION_SETTINGS");
                break;

            case Actions.ACTION_LENS_SETTINGS_DETAILS:
                startLensScreen();
                updateBehaviour();
                break;

            case Actions.ACTION_PATIENT_TEST_PROFILE:
                updateBehaviour();
                if (communicationActive) {
                    activeView_number = R.layout.activity_test_profile;
                    StoreTransmitter.updatedUIState("ACTION_PATIENT_TEST_PROFILE");
                } else {
                    activeView_number = R.layout.activity_waitscreen_test_profile;
                    if (!isCommInitializationOver)
                        StoreTransmitter.doCommFunction(StoreTransmitter.COMM_FUNCTION_INITIALIZE, clickData);
                }
                break;

            case Actions.ACTION_ADJUST_IPD_DETAILS:
                Log.e("Looking", "ACTION_ADJUST_IPD_DETAILS called");
                buttonClickData.buttonName = "ShowIPD";
                buttonClickData.buttonData = CommonUtils.readVector().toString();
                clickData.putString("onPass", "ShowIPDPass");
                clickData.putString("onFail", "ShowIPDFail");
                sentData = gson.toJson(buttonClickData);
                clickData.putInt("hashcode", sentData.hashCode());
                clickData.putString("data", sentData);
                StoreTransmitter.doCommFunction(StoreTransmitter.COMM_FUNCTION_CLICKONTC, clickData);
                updateBehaviour();
                activeView_number = R.layout.activity_ipd_settings;
                StoreTransmitter.updatedUIState("ACTION_ADJUST_IPD_DETAILS");
                break;

            case Actions.ACTION_SEND_VECTOR_DATA:
                buttonClickData.buttonName = "Vector_Data";
                buttonClickData.buttonData = CommonUtils.readVector().toString();
                clickData.putString("onPass", "VectorDataPass");
                clickData.putString("onFail", "VectorDataFail");
                sentData = gson.toJson(buttonClickData);
                clickData.putInt("hashcode", sentData.hashCode());
                clickData.putString("data", sentData);
                StoreTransmitter.doCommFunction(StoreTransmitter.COMM_FUNCTION_CLICKONTC, clickData);
                updateBehaviour();
                StoreTransmitter.updatedUIState("ACTION_ADJUST_IPD_DETAILS");
                break;
            case Actions.ACTION_UPDATATE_HMD_BATTERY_STATUS:
                buttonClickData.buttonName = "UpdateBatteryStatus";
                buttonClickData.buttonData = "UpdateBatteryStatus";
                clickData.putString("onPass", "UpdateBatteryStatusPass");
                clickData.putString("onFail", "UpdateBatteryStatusFail");
                sentData = gson.toJson(buttonClickData);
                clickData.putInt("hashcode", sentData.hashCode());
                clickData.putString("data", sentData);
                StoreTransmitter.doCommFunction(StoreTransmitter.COMM_FUNCTION_CLICKONTC, clickData);
                StoreTransmitter.updatedUIState("ACTION_UPDATATE_HMD_BATTERY_STATUS");
                break;
            case Actions.ACTION_SAVE_VECTOR_DATA_IN_HMD:
                buttonClickData.buttonName = "Vector_Data_Save";
                if (new AppPreferencesHelper(MyApplication.getInstance(), DEVICE_PREF).getProductionTestingStatus())
                    buttonClickData.buttonData = CommonUtils.readVector().toString();
                else
                    buttonClickData.buttonData = CommonUtils.readVector().toString();
                clickData.putString("onPass", "VectorDataPass");
                clickData.putString("onFail", "VectorDataFail");
                sentData = gson.toJson(buttonClickData);
                clickData.putInt("hashcode", sentData.hashCode());
                clickData.putString("data", sentData);
                StoreTransmitter.doCommFunction(StoreTransmitter.COMM_FUNCTION_CLICKONTC, clickData);
                updateBehaviour();
                // StoreTransmitter.updatedUIState("ACTION_ADJUST_IPD_DETAILS");
                break;

            case Actions.ACTION_GET_PRB_STATUS:
                buttonClickData.buttonName = "GET_PRB_STATUS";
                buttonClickData.buttonData = "GET_PRB_STATUS";
                clickData.putString("onPass", "GetPrbStatusPass");
                clickData.putString("onFail", "GetPrbStatusFail");
                sentData = gson.toJson(buttonClickData);
                clickData.putInt("hashcode", sentData.hashCode());
                clickData.putString("data", sentData);
                StoreTransmitter.doCommFunction(StoreTransmitter.COMM_FUNCTION_CLICKONTC, clickData);
                break;

            case Actions.ACTION_START_CHECKING_INTERFACES:
                activeView_number = R.layout.checking_interfaces_screen;
                StoreTransmitter.updatedUIState("ACTION_START_CHECKING_INTERFACES");
                break;

            case Actions.ACTION_CALIBRATION_SETTINGS:
                updateBehaviour();
                setClickData("IpdSettingsContinueButton", "NA");
                activeView_number = R.layout.activity_waitscreen_calibration_live_feed;
                if (communicationActive) {
                    StoreTransmitter.doCommFunction(StoreTransmitter.COMM_FUNCTION_CLICKONTC, clickData);
                    StoreTransmitter.updatedUIState("ACTION_CALIBRATION_SETTINGS");
                }
                break;

            case Actions.ACTION_BACK_TO_SET_TEST:
                RuntimeMode.modeNo = 1;
                setClickData("BackToStartTest", "NA");
                activeView_number = R.layout.activity_test_profile;
                if (communicationActive) {
                    StoreTransmitter.doCommFunction(StoreTransmitter.COMM_FUNCTION_CLICKONTC, clickData);
                    showToast = true;
                    toastMessage = "Okay!Going back to Start Test";
                } else {
                    showToast = true;
                    toastMessage = "Lost connection to HMD! Please try again";
                }
                StoreTransmitter.updatedUIState("ACTION_BACK_TO_SET_TEST");
                break;


            case Actions.ACTION_IDLE_SHUTDOWN_LATER:
                buttonClickData.buttonName = "Idle_Shutdown_Later";
                buttonClickData.buttonData = "Idle_Shutdown_Later";
                clickData.putString("onPass", "IdleShutdownLaterPass");
                clickData.putString("onFail", "IdleShutdownLaterFail");
                sentData = gson.toJson(buttonClickData);
                clickData.putInt("hashcode", sentData.hashCode());
                clickData.putString("data", sentData);
                StoreTransmitter.doCommFunction(StoreTransmitter.COMM_FUNCTION_CLICKONTC, clickData);
                //activeView_number = R.layout.activity_home_screen;
                ///StoreTransmitter.updatedUIState("ACTION_IDLE_SHUTDOWN_LATER");
                break;

            case Actions.ACTION_IDLE_SHUTDOWN_OKAY:
                buttonClickData.buttonName = "Idle_Shutdown_Okay";
                buttonClickData.buttonData = "Idle_Shutdown_Okay";
                clickData.putString("onPass", "IdleShutdownOkayPass");
                clickData.putString("onFail", "IdleShutdownOkayFail");
                sentData = gson.toJson(buttonClickData);
                clickData.putInt("hashcode", sentData.hashCode());
                clickData.putString("data", sentData);
                StoreTransmitter.doCommFunction(StoreTransmitter.COMM_FUNCTION_CLICKONTC, clickData);
                //activeView_number = R.layout.activity_home_screen;
                //StoreTransmitter.updatedUIState("ACTION_IDLE_SHUTDOWN_OKAY");
                break;

            case Actions.ACTION_ABORT:
                isAbortClicked = true;
                dt_chronometerOn = false;
                RuntimeMode.modeNo = -1;
                buttonClickData.buttonName = "AbortTest";
                buttonClickData.buttonData = "Abort";
                clickData.putString("onPass", "AbortPass");
                clickData.putString("onFail", "AbortFail");
                sentData = gson.toJson(buttonClickData);
                clickData.putInt("hashcode", sentData.hashCode());
                clickData.putString("data", sentData);
                if (communicationActive) {
                    if (activeView_number == R.layout.activity_during_test)
                        activeView_number = R.layout.response_waitscreen_abort_during_test;
                    else if (activeView_number == R.layout.activity_ipd_settings)
                        activeView_number = R.layout.response_waitscreen_abort_ipd;
                    else if (activeView_number == R.layout.accessories_status)
                        activeView_number = R.layout.response_waitscreen_accessory_abort;
                    StoreTransmitter.doCommFunction(StoreTransmitter.COMM_FUNCTION_CLICKONTC, clickData);
                    showToast = false;
                } else {
                    showToast = true;
                    toastMessage = "Connection with HMD Lost Going Home";
                    Actions.goHomeImmediately();
                }
                StoreTransmitter.updatedUIState("ACTION_ABORT");
                break;

            case Actions.ACTION_ABORT_CALIBRATION:
                buttonClickData.buttonName = "AbortCalibration";
                buttonClickData.buttonData = "AbortCalibration";
                clickData.putString("onPass", "AbortCalibrationPass");
                clickData.putString("onFail", "AbortCalibrationFail");
                sentData = gson.toJson(buttonClickData);
                clickData.putInt("hashcode", sentData.hashCode());
                clickData.putString("data", sentData);
                StoreTransmitter.doCommFunction(StoreTransmitter.COMM_FUNCTION_CLICKONTC, clickData);
                activeView_number = R.layout.activity_home_screen;
                StoreTransmitter.updatedUIState("ACTION_ABORT_CALIBRATION");

                break;

            case Actions.ACTION_BEGIN_TEST:
                isFoveaProceeded = false;
                Boolean fixationMonitoring = (boolean) actionData;
                setClickData("BeginTest", String.valueOf(fixationMonitoring));
                if (communicationActive) {
                    StoreTransmitter.doCommFunction(StoreTransmitter.COMM_FUNCTION_CLICKONTC, clickData);
                    showToast = false;
                    activeView_number = R.layout.activity_waitscreen_during_test;
                } else {
                    showToast = true;
                    toastMessage = "Lost connection to HMD! Please try again";
                }
                StoreTransmitter.updatedUIState("ACTION_BEGIN_TEST");
                break;

            case Actions.ACTION_CALIBRATION_DONE:
                calibrationRecalibration = "Recalibrate";
                calibrationStatus = "Calibration is done";
                calibrationSaveProceedVisibility = View.VISIBLE;
                StoreTransmitter.updatedUIState("ACTION_CALIBRATION_DONE");
                break;

            case Actions.ACTION_UPDATE_PRB:

                buttonClickData.buttonData = "" + new AppPreferencesHelper(applicationContext, DEVICE_PREF).getPRBCount();
                buttonClickData.buttonName = "PRB_Update";
                clickData.putString("onPass", "PrbUpdatePass");
                clickData.putString("onFail", "PrbUpdateFail");
                sentData = gson.toJson(buttonClickData);
                clickData.putInt("hashcode", sentData.hashCode());
                clickData.putString("data", sentData);
                if (communicationActive) {
                    StoreTransmitter.doCommFunction(StoreTransmitter.COMM_FUNCTION_CLICKONTC, clickData);
                }
                break;

            case Actions.ACTION_START_UPDATE_TRANSFER:
                Log.d("Store", "ACTION_START_UPDATE_TRANSFER called");
                buttonClickData.buttonName = "HMD_UPDATE";
                buttonClickData.buttonData = "HMD_UPDATE";
                clickData.putString("onPass", "HmdUpdateSentPass");
                clickData.putString("onFail", "HmdUpdateSentFail");
                sentData = gson.toJson(buttonClickData);
                clickData.putInt("hashcode", sentData.hashCode());
                clickData.putString("data", sentData);
                StoreTransmitter.doCommFunction(StoreTransmitter.COMM_FUNCTION_CLICKONTC, clickData);
                break;
            case Actions.ACTION_STOP_IDLE_TIMERS:
                Log.d("Store", "ACTION_STOP_IDLE_TIMERS called");
                buttonClickData.buttonName = "STOP_IDLE_CHECK";
                buttonClickData.buttonData = "STOP_IDLE_CHECK";
                clickData.putString("onPass", "Stop_Idle_Check_Pass");
                clickData.putString("onFail", "Stop_Idle_Check_Fail");
                sentData = gson.toJson(buttonClickData);
                clickData.putInt("hashcode", sentData.hashCode());
                clickData.putString("data", sentData);
                StoreTransmitter.doCommFunction(StoreTransmitter.COMM_FUNCTION_CLICKONTC, clickData);
                break;

            case Actions.ACTION_UPDATE_CAMERA_ALPHA_BETA:
                Log.e("CAMERA_DETAILS", (String) actionData);
                setClickData("HMD_CAMERA_ALPHA_BETA_DETAILS", (String) actionData);
                if (communicationActive) {
                    StoreTransmitter.doCommFunction(StoreTransmitter.COMM_FUNCTION_CLICKONTC, clickData);
                }
                break;

            case Actions.ACTION_FOVEA_PROCEED:
                buttonClickData.buttonName = "FOVEA";
                buttonClickData.buttonData = "proceed";
                clickData.putString("onPass", "FoveaSentPass");
                clickData.putString("onFail", "FoveaSentFail");
                sentData = gson.toJson(buttonClickData);
                clickData.putInt("hashcode", sentData.hashCode());
                clickData.putString("data", sentData);
                isFoveaProceeded = true;
                StoreTransmitter.updatedUIState("ACTION_FOVEA_PROCEED");
                StoreTransmitter.doCommFunction(StoreTransmitter.COMM_FUNCTION_CLICKONTC, clickData);
                break;

            case Actions.ACTION_FOVEA_REDO:
                preTestPrbCounter = 0;
                buttonClickData.buttonName = "FOVEA";
                buttonClickData.buttonData = "redo";
                clickData.putString("onPass", "FoveaSentPass");
                clickData.putString("onFail", "FoveaSentFail");
                sentData = gson.toJson(buttonClickData);
                clickData.putInt("hashcode", sentData.hashCode());
                clickData.putString("data", sentData);
                StoreTransmitter.doCommFunction(StoreTransmitter.COMM_FUNCTION_CLICKONTC, clickData);
                break;

            case Actions.ACTION_CHECK_BATTERY:
                buttonClickData.buttonName = "CHECK_BATTERY";
                buttonClickData.buttonData = "CHECK_BATTERY";
                clickData.putString("onPass", "CheckBatteryPass");
                clickData.putString("onFail", "CheckBatteryFail");
                sentData = gson.toJson(buttonClickData);
                clickData.putInt("hashcode", sentData.hashCode());
                clickData.putString("data", sentData);
                StoreTransmitter.doCommFunction(StoreTransmitter.COMM_FUNCTION_CLICKONTC, clickData);
                break;
            case Actions.ACTION_CHECK_ACCESSIBILITY_STATUS:
                buttonClickData.buttonName = "CHECK_ACCESSIBILITY_STATUS";
                buttonClickData.buttonData = "CHECK_ACCESSIBILITY_STATUS";
                clickData.putString("onPass", "CheckAccessibilityPass");
                clickData.putString("onFail", "CheckAccessibilityFail");
                sentData = gson.toJson(buttonClickData);
                clickData.putInt("hashcode", sentData.hashCode());
                clickData.putString("data", sentData);
                StoreTransmitter.doCommFunction(StoreTransmitter.COMM_FUNCTION_CLICKONTC, clickData);
                break;

            case Actions.ACTION_CALIB_DOWN:
                buttonClickData.buttonName = "diagnosticDown";
                buttonClickData.buttonData = "diagnosticDown";
                clickData.putString("onPass", "diagnosticDownPass");
                clickData.putString("onFail", "diagnosticDownFail");
                sentData = gson.toJson(buttonClickData);
                clickData.putInt("hashcode", sentData.hashCode());
                clickData.putString("data", sentData);
                if (communicationActive) {
                    StoreTransmitter.doCommFunction(StoreTransmitter.COMM_FUNCTION_CLICKONTC, clickData);
                    showToast = false;
                } else {
                    showToast = true;
                    toastMessage = "Connection with HMD Lost.Please Wait";
                }
                break;

            case Actions.ACTION_CALIB_UP:
                buttonClickData.buttonName = "diagnosticUp";
                buttonClickData.buttonData = "diagnosticUp";
                clickData.putString("onPass", "diagnosticUpPass");
                clickData.putString("onFail", "diagnosticUpFail");
                sentData = gson.toJson(buttonClickData);
                clickData.putInt("hashcode", sentData.hashCode());
                clickData.putString("data", sentData);
                if (communicationActive) {
                    StoreTransmitter.doCommFunction(StoreTransmitter.COMM_FUNCTION_CLICKONTC, clickData);
                    showToast = false;
                } else {
                    showToast = true;
                    toastMessage = "Connection with HMD Lost.Please wait";
                }
                break;

            case Actions.ACTION_START_EI:
                buttonClickData.buttonName = "START_EI";
                buttonClickData.buttonData = "START_EI";
                clickData.putString("onPass", "StartEIPass");
                clickData.putString("onFail", "StartEIFail");
                sentData = gson.toJson(buttonClickData);
                clickData.putInt("hashcode", sentData.hashCode());
                clickData.putString("data", sentData);
                StoreTransmitter.doCommFunction(StoreTransmitter.COMM_FUNCTION_CLICKONTC, clickData);
                break;

            case Actions.ACTION_SILENTLY_START_EI:
                buttonClickData.buttonName = "SILENTLY_START_EI";
                buttonClickData.buttonData = "SILENTLY_START_EI";
                clickData.putString("onPass", "StartEIPass");
                clickData.putString("onFail", "StartEIFail");
                sentData = gson.toJson(buttonClickData);
                clickData.putInt("hashcode", sentData.hashCode());
                clickData.putString("data", sentData);
                StoreTransmitter.doCommFunction(StoreTransmitter.COMM_FUNCTION_CLICKONTC, clickData);
                break;
            case Actions.ACTION_STOP_EI:
                buttonClickData.buttonName = "STOP_EI";
                buttonClickData.buttonData = "STOP_EI";
                clickData.putString("onPass", "StopEIPass");
                clickData.putString("onFail", "StopEIFail");
                sentData = gson.toJson(buttonClickData);
                clickData.putInt("hashcode", sentData.hashCode());
                clickData.putString("data", sentData);
                StoreTransmitter.doCommFunction(StoreTransmitter.COMM_FUNCTION_CLICKONTC, clickData);
                break;
            case Actions.ACTION_STOP_TEST_SILENTLY:
                buttonClickData.buttonName = "STOP_TEST_SILENTLY";
                buttonClickData.buttonData = "STOP_TEST_SILENTLY";
                clickData.putString("onPass", "StopTestSilentPass");
                clickData.putString("onFail", "StopTestSilentFail");
                sentData = gson.toJson(buttonClickData);
                clickData.putInt("hashcode", sentData.hashCode());
                clickData.putString("data", sentData);
                StoreTransmitter.doCommFunction(StoreTransmitter.COMM_FUNCTION_CLICKONTC, clickData);
                break;
            case Actions.ACTION_CHECK_FOR_ALPHA_BETA:
                buttonClickData.buttonName = "CHECK_FOR_ALPHA_BETA";
                buttonClickData.buttonData = "CHECK_FOR_ALPHA_BETA";
                clickData.putString("onPass", "AlphaBetaPass");
                clickData.putString("onFail", "AlphaBetaFail");
                sentData = gson.toJson(buttonClickData);
                clickData.putInt("hashcode", sentData.hashCode());
                clickData.putString("data", sentData);
                StoreTransmitter.doCommFunction(StoreTransmitter.COMM_FUNCTION_CLICKONTC, clickData);
                break;
            case Actions.ACTION_UPDATE_MODE:
                String modeToUpdate = (String) actionData;
                buttonClickData.buttonName = "SILENT_MODE_UPDATE";
                buttonClickData.buttonData = modeToUpdate;
                clickData.putString("onPass", "ModeUpdatePass");
                clickData.putString("onFail", "ModeUpdateFail");
                sentData = gson.toJson(buttonClickData);
                clickData.putInt("hashcode", sentData.hashCode());
                clickData.putString("data", sentData);
                StoreTransmitter.doCommFunction(StoreTransmitter.COMM_FUNCTION_CLICKONTC, clickData);
                break;
            case Actions.ACTION_CHECK_FOR_PREVIOUS_RESULTS:
                buttonClickData.buttonName = "PREV_RESULT";
                buttonClickData.buttonData = "PREV_RESULT";
                clickData.putString("onPass", "PrevResultPass");
                clickData.putString("onFail", "PrevResultFail");
                sentData = gson.toJson(buttonClickData);
                clickData.putInt("hashcode", sentData.hashCode());
                clickData.putString("data", sentData);
                StoreTransmitter.doCommFunction(StoreTransmitter.COMM_FUNCTION_CLICKONTC, clickData);
                break;

            case Actions.ACTION_UPDATE_HMD_SYNC:
                if (activeView_number == R.layout.hmd_sync_check_activity)
                    StoreTransmitter.updatedUIState("ACTION_UPDATE_HMD_SYNC");
                break;


            case Actions.ACTION_COMMUNICATION_STATUS:
                String commState = (String) actionData;
                commState = nullCheck(commState) ? commState : "";
                Log.d("commState", " " + commState);
                switch (commState) {

                    case "RegisterTestPass":
                        if (activeView_number == R.layout.activity_waitscreen_test_profile) {
                            showToast = false;
                            //StoreTransmitter.updatedUIState("RegisterTestPass");
                        }
                        break;

                    case "RegisterTestFail":
                        if (activeView_number == R.layout.activity_waitscreen_test_profile) {
                            if (communicationActive) {
                                StoreTransmitter.doCommFunction(StoreTransmitter.COMM_FUNCTION_CLICKONTC, clickData);
                            } else {
                                Log.d("NampuComm Store", "Do nothing. The lost connection handler will go back to the setup page");
                            }
                        }
                        break;

                    case "StartTestPass":
                            /*if (activeView_number == R.layout.activity_waitscreen_ipd_settings) {
                                activeView_number = R.layout.activity_waitscreen_ipd_live_feed;
                                StoreTransmitter.updatedUIState();
                            }*/
                        break;

                    case "StartTestFail":
                            /*if (activeView_number == R.layout.activity_waitscreen_ipd_settings) {
                                if (communicationActive) {
                                    StoreTransmitter.doCommFunction(StoreTransmitter.COMM_FUNCTION_CLICKONTC, clickData);
                                } else {
                                    Log.d("NampuComm Store", "Do nothing. The lost connection handler will go back to the start screen");

                                }
                            }*/
                        break;

                    case "PauseTestPass":
                        if (activeView_number == R.layout.activity_during_test) {
                            showToast = true;
                            toastMessage = "Test paused";
                            StoreTransmitter.updatedUIState("PauseTestPass");
                        }
                        break;
                    case "UnPauseTestPass":
                        if (activeView_number == R.layout.activity_during_test) {
                            dt_timebase = SystemClock.elapsedRealtime() + appPreferencesHelper.getTimeBase();
                            showToast = true;
                            toastMessage = "Continuing Test...";
                            StoreTransmitter.updatedUIState("UnPauseTestPass");
                        }
                        break;

                    case "PauseTestFail":
                        if (activeView_number == R.layout.activity_during_test) {
                            showToast = true;
                            toastMessage = "Test Unpause failed. Please try again";
                            StoreTransmitter.updatedUIState("PauseTestFail");
                        }
                        break;

                    case "UnPauseTestFail":
                        if (activeView_number == R.layout.activity_during_test) {
                            showToast = true;
                            toastMessage = "Test pause failed. Please try again";
                            StoreTransmitter.updatedUIState("UnPauseTestFail");
                        }
                        break;

                    case "BackToStartTestPass":
                        activeView_number = R.layout.activity_test_profile;
                        StoreTransmitter.updatedUIState("BackToStartTestPass");
                        break;

                    case "BackToStartTestFail":
                        if (communicationActive) {
                            StoreTransmitter.doCommFunction(StoreTransmitter.COMM_FUNCTION_CLICKONTC, clickData);
                        } else {
                            Log.d("NampuComm Store", "Do nothing. the lost connection handler will take care");
                        }
                        break;

                    case "BeginTestPass":
                        if (activeView_number == R.layout.activity_waitscreen_during_test) {
                            activeView_number = R.layout.activity_during_test;
                           /* dt_chronometerOn = true;
                            //dt_timebase = session ? (SystemClock.elapsedRealtime() + appPreferencesHelper.getTimeBase()) : SystemClock.elapsedRealtime();
                            dt_timebase = SystemClock.elapsedRealtime();*/
                            StoreTransmitter.updatedUIState("BeginTestPass");
                        }
                        break;

                    case "BeginTestFail":
                        Log.d("NampuCommBeginTest", "Begin test is failing");
                        if (activeView_number == R.layout.activity_waitscreen_during_test) {
                            if (communicationActive) {
                                StoreTransmitter.doCommFunction(StoreTransmitter.COMM_FUNCTION_CLICKONTC, clickData);
                            } else {
                                Log.d("NampuComm Store", "No Connection. Go back to set test and wait");
                                //TODO Check this out
                                // activeView_number = R.layout.activity_calibration;
                                showToast = true;
                                toastMessage = "Lost Connection with HMD. Please try again after 2 seconds";
                                StoreTransmitter.updatedUIState("BeginTestFail");
                            }
                        }
                        break;

                    case "HomePass":
                        activeView_number = R.layout.activity_home_screen;
                        StoreTransmitter.updatedUIState("HomePass");
                        break;

                    case "HomeFail":
                        showToast = true;
                        toastMessage = "Failed try again";
                        break;

                    case "JumpToStartTestPass":
                        Log.e(TAG, "JumpToStartTestPass called");
                        activeView_number = R.layout.activity_primary_patient_details;
                        StoreTransmitter.updatedUIState("JumpToStartTestPass");
                        break;

                    case "JumpToStartTestFail":
                        showToast = true;
                        toastMessage = "Failed try again";
                        break;

                    case "CalibrationPass":
                        toastMessage = "Calibration done...";
                        break;

                    case "CalibrationFail":
                        showToast = true;
                        toastMessage = "Failed try again";
                        break;

                    case "SkipCalibrationPass":
                        beginTest(false, "SkipCalibrationPass");
                        break;

                    case "SkipCalibrationFail":
                        showToast = true;
                        toastMessage = "Failed try again";
                        break;
                    case "AbortPass":

                        break;
                    case "AbortFail":
                        if (activeView_number == R.layout.activity_home_screen) {
                            disableNewTest();
                        }
                        break;
                    case "HmdUpdateSentPass":
                        new Handler().postDelayed(() -> {
                            Thread fileServer = new Thread(new FileServer(applicationContext));
                            fileServer.start();
                        }, 5000);
                        if (activeView_number == R.layout.hmd_update_tranfer_screen) {
                            Log.e("HmdUpdateSentPass ", " hmd_update_tranfer_screen");
                            transferMessage = "HMD Update in progress please wait...";
                            StoreTransmitter.updatedUIState("HmdUpdateSentPass");
                        }
                        break;

                    case "StopEIPass":
                        //enableNewTest();
                        break;

                    case "StopEIFail":
                        //disableNewTest();
                        break;
                }
                break;

            case Actions.ACTION_CAMERA_FREEZE:
                CommonUtils.showEiModuleDisconnected(MainActivity.dialogReference);
                break;

            case Actions.ACTION_DATA_FROM_HMD:
                String dataFromHMD = (String) actionData;
                dataFromHMD(dataFromHMD);
                break;

            case Actions.ACTION_HMD_CONNECTED:
                Log.e("ACTION_HMD_CONNECTED", "Called");
                newTestVisibility = true;
                communicationActive = true;
                isCommInitializationOver = true;
                MyApplication.getInstance().set_HMD_CONNECTED(true);

                if (activeView_number == R.layout.hmd_sync_check_activity) {
                    Log.e("ACTION_HMD_CONNECTED", "hmd_sync_check_activity");
                    StoreTransmitter.updatedUIState("ACTION_HMD_CONNECTED");
                } else if (activeView_number == R.layout.activity_home_screen) {
                    Log.e("ACTION_HMD_CONNECTED", "activity_home_screen");
                    StoreTransmitter.updatedUIState("ACTION_HMD_CONNECTED");
                } else if (activeView_number == R.layout.download_update_screen) {
                    Log.e("ACTION_HMD_CONNECTED", "download_update_screen");
                    //Actions.beginHMDUpdate();
                    MainActivity.isHMDConnected = true;
                    Actions.showUpdateConnectedToHMD();
                } else {
                    Log.e("ACTION_HMD_CONNECTED", "hmd_sync_check_activity else executed");
                    //Store.modeSync(false, true, false);
                }
                break;


            case Actions.ACTION_HMD_NOT_CONNECTED:
                Log.e("HMD_NOT_CONNECTED", "Called");
                communicationActive = false;
                MyApplication.getInstance().set_HMD_CONNECTED(false);
                int[] acceptedScreens = {
                        R.layout.checking_interfaces_screen,
                        R.layout.warning_bno_charging,
                        R.layout.warning_bno_not_charging,
                        R.layout.warning_bo_charging,
                        R.layout.error_component_failure,
                        R.layout.activity_primary_patient_details,
                        R.layout.activity_primary_test_details,
                        R.layout.activity_lens_power_settings,
                        R.layout.activity_waitscreen_test_profile,
                        R.layout.activity_test_profile};

                if (activeView_number == R.layout.activity_home_screen) {
                    disableNewTest();
                } else if (ArrayUtils.contains(acceptedScreens, activeView_number)) {
                    newTestVisibility = false;
                    activeView_number = R.layout.error_hmd_disconnected;
                    StoreTransmitter.updatedUIState("ACTION_HMD_NOT_CONNECTED");
                } else {
                    //Store.modeSync(true, false, false);
                }

                break;

            case Actions.ACTION_REGISTER_TEST:
                PerimetryObject_V2.FinalPerimetryResultObject finalPerimetryResultObject = new PerimetryObject_V2.FinalPerimetryResultObject();
                if (TextUtils.isEmpty(testSphericalVal)) {
                    showToast = true;
                    toastMessage = "Please Enter Spherical Value";
                } else {
                    showTip = false;
                    tipId = 0;
                    Log.d("StoreSays", "register test does  reach here");
                    testInfo.PatName = "XXX";
                    testInfo.PatDOB = "20";
                    testInfo.PatSex = "Male";
                    testInfo.TestEye = "Left";
                    testInfo.TestType = "Screening";

                    testInfo.PatFirstName = patientName;
                    testInfo.PatMRNNumber = patientMrnNumberVal;
                    testInfo.PatMobile = patientPhoneNumberVal;
                    testInfo.PatSex = patientSexVal;
                    testInfo.PatDOB = patientDOBVal;
                    testInfo.TestEye = patientTestEyeVal;
                    testInfo.TestStrategy = patientTestStrategyVal;
                    testInfo.TestPattern = patientTestPatternVal;
                    testInfo.CylindricalValue = testCylindricalVal;
                    testInfo.CylindricalAxisValue = testCylindricalAxisVal;
                    testInfo.SphericalValue = testSphericalVal;
                    boolean isItJarvis = Pattern.compile(Pattern.quote("jarvis"), Pattern.CASE_INSENSITIVE).matcher(patientName).find();
                    finalPerimetryResultObject.Patient.PatientName = isItJarvis ? patientName : "NO ONE";
                    finalPerimetryResultObject.Patient.PatientSex = patientSexVal;
                    finalPerimetryResultObject.Patient.PatientID = patientMrnNumberVal;
                    finalPerimetryResultObject.Patient.PatientBirthDate = patientDOBVal;
                    finalPerimetryResultObject.Patient.PatientUID = patientMrnNumberVal;
                    finalPerimetryResultObject.Patient.PatientID = patientPhoneNumberVal;

                    finalPerimetryResultObject.Study.StudyInstanceUID = CommonUtils.getUUID();
                    finalPerimetryResultObject.Study.StudyID = CommonUtils.getUUID();

                    finalPerimetryResultObject.Study.StudyDate = getCurrentDate();
                    finalPerimetryResultObject.Study.StudyTime = getCurrentTime();

                    finalPerimetryResultObject.Series.SeriesInstanceUID = "SeriesInst001";
                    finalPerimetryResultObject.Series.SeriesNumber = CommonUtils.getUUID();
                    finalPerimetryResultObject.Series.Laterality = patientTestEyeVal.equalsIgnoreCase("left eye") ? "L" : "R";

                    finalPerimetryResultObject.Measurements.OphthalmicPatientClinicalInformationAndTestLensParameters.OphthalmicPatientClinicalInformationLeftEyeSequence.RefractiveParametersUsedOnPatientSequence.CylindricalLensPower = TextUtils.isEmpty(testCylindricalVal) ? 0 : Double.parseDouble(testCylindricalVal);
                    finalPerimetryResultObject.Measurements.OphthalmicPatientClinicalInformationAndTestLensParameters.OphthalmicPatientClinicalInformationLeftEyeSequence.RefractiveParametersUsedOnPatientSequence.SphericalLensPower = TextUtils.isEmpty(testSphericalVal) ? 0 : Double.parseDouble(testSphericalVal);
                    finalPerimetryResultObject.Measurements.OphthalmicPatientClinicalInformationAndTestLensParameters.OphthalmicPatientClinicalInformationLeftEyeSequence.RefractiveParametersUsedOnPatientSequence.CylinderAxis = TextUtils.isEmpty(testCylindricalAxisVal) ? 0 : Double.parseDouble(testCylindricalAxisVal);

                    finalPerimetryResultObject.Series.PatternSequence.CodeMeaning = patientTestPatternVal;
                    finalPerimetryResultObject.Series.StrategySequence.CodeMeaning = patientTestStrategyVal;

                    String testInfo = gson.toJson(finalPerimetryResultObject);
                    writeToFile(testInfo, "beforeAck");
                    buttonClickData.buttonName = "RegisterTest";
                    buttonClickData.buttonData = testInfo;
                    String sendData = gson.toJson(buttonClickData);
                    clickData.putInt("hashcode", sendData.hashCode());
                    clickData.putString("data", sendData);
                    clickData.putString("onPass", "RegisterTestPass");
                    clickData.putString("onFail", "RegisterTestFail");
                    if (communicationActive) {
                        StoreTransmitter.doCommFunction(StoreTransmitter.COMM_FUNCTION_CLICKONTC, clickData);
                        showToast = false;
                        activeView_number = R.layout.activity_waitscreen_test_profile;
                    } else {
                        showToast = true;
                        toastMessage = "No connection to HMD! Please try again";
                    }
                }
                StoreTransmitter.updatedUIState("ACTION_REGISTER_TEST");
                break;

            case Actions.ACTION_RESET_TOAST:
                showToast = false;
                break;

            case Actions.ACTION_RESET_TIP:
                Log.d("ACTION_RESET_TIP", "called");
                showTip = false;
                tipId = 0;
                break;

            case Actions.ACTION_SAVE_STATE:
                try {
                    Bundle recvdState = (Bundle) actionData;
                    if (recvdState != null) {
                        activeView_number = recvdState.getInt("viewID");
                        switch (activeView_number) {
                            case R.layout.activity_primary_patient_details:
                                Log.d("viewID", " activity_primary_patient_details");
                                patientName = recvdState.getString("PatientFirstName");
                                patientPhoneNumberVal = recvdState.getString("PatientMobileNumber");
                                patientMrnNumberVal = recvdState.getString("PatientMrnNumber");
                                patientDOBVal = recvdState.getString("PatientDob");
                                patientSexVal = recvdState.getString("PatientSex");
                                break;
                            case R.layout.activity_primary_test_details:
                                Log.d("viewID", " activity_primary_test_details_screen");
                                patientTestEyeVal = recvdState.getString("TestEye");
                                patientTestPatternVal = recvdState.getString("TestPattern");
                                patientTestStrategyVal = recvdState.getString("TestStrategy");
                                break;
                            case R.layout.activity_lens_power_settings:
                                Log.d("viewID", " activity_lens_power_settings");

                                testSphericalInput = recvdState.getString("PatientSphericalInput");
                                testCylindricalInput = recvdState.getString("PatientCylindricalInput");
                                testCylindricalAxisInput = recvdState.getString("PatientCylindricalAxisInput");

                                testSphericalVal = recvdState.getString("SphericalPower");
                                testCylindricalVal = recvdState.getString("CylindricalPower");
                                testCylindricalAxisVal = recvdState.getString("CylindricalAxisPower");
                                break;
                        }
                    }
                } catch (ClassCastException e) {
                    Log.e("NampuCommFromHMD", "ClassCastException " + e.getMessage());
                }
                break;

            case Actions.ACTION_START_NEW_TEST:
                Log.e(TAG, "ACTION_START_NEW_TEST called");
                if (communicationActive) {
                    patientName = "";
                    patientPhoneNumberVal = "";
                    patientMrnNumberVal = "";
                    patientDOBVal = "";
                    patientSexVal = "";
                    activeView_number = R.layout.activity_primary_patient_details;
                    dt_new_result = new ArrayList<>();
                    dt_result_sensitivity = new ArrayList<>();
                    dt_result_seen = new ArrayList<>();
                    FP_Numerator = 0;
                    FP_Denominator = 0;
                    FL_Numerator = 0;
                    FL_Denominator = 0;
                    FN_Numerator = 0;
                    FN_Denominator = 0;
                } else {
                    activeView_number = R.layout.activity_waitscreen_primary_patient_details;
                    if (!isCommInitializationOver)
                        StoreTransmitter.doCommFunction(StoreTransmitter.COMM_FUNCTION_INITIALIZE, clickData);
                }
                StoreTransmitter.updatedUIState("ACTION_START_NEW_TEST");
                break;

            case Actions.ACTION_START_TEST:
                PerimetryObject_V2.FinalPerimetryResultObject perimetryResultObject = new PerimetryObject_V2.FinalPerimetryResultObject();
                perimetryResultObject.Series.Laterality = patientTestEyeVal.equalsIgnoreCase("left eye") ? "L" : "R";
                perimetryResultObject.Series.PatternSequence.CodeMeaning = patientTestPatternVal;
                perimetryResultObject.Series.StrategySequence.CodeMeaning = patientTestStrategyVal;
                String info = gson.toJson(perimetryResultObject);
                buttonClickData.buttonName = "StartTest";
                buttonClickData.buttonData = info;
                resetArrays();
                resetFixations();
                String startTestData = gson.toJson(buttonClickData);
                clickData.putInt("hashcode", startTestData.hashCode());
                clickData.putString("data", startTestData);
                clickData.putString("onPass", "StartTestPass");
                clickData.putString("onFail", "StartTestFail");
                if (communicationActive) {
                    StoreTransmitter.doCommFunction(StoreTransmitter.COMM_FUNCTION_CLICKONTC, clickData);
                    showToast = false;
                    //activeView_number = R.layout.waitscreen_checking_accessories;
                } else {
                    showToast = true;
                    toastMessage = "Connection with HMD Lost.Please Wait_STARTTEST";
                }
                //StoreTransmitter.updatedUIState();
                break;

            case Actions.ACTION_SHOW_ACCESSORY_STATUS:
                Log.e("Looking", "ACTION_SHOW_ACCESSORY_STATUS called");
                activeView_number = R.layout.accessories_status;
                StoreTransmitter.updatedUIState("ACTION_SHOW_ACCESSORY_STATUS");
                break;
            case Actions.ACTION_SHOW_ACCESSORY_STATUS_WAITSCREEN:
                Log.e("Looking", "ACTION_SHOW_ACCESSORY_STATUS_WAITSCREEN called");
                activeView_number = R.layout.activity_waitscreen_accessory_status;
                StoreTransmitter.updatedUIState("ACTION_SHOW_ACCESSORY_STATUS_WAITSCREEN");
                break;

            case Actions.ACTION_START_DOWNLOAD_UPDATES:
                activeView_number = R.layout.download_update_screen;
                StoreTransmitter.updatedUIState("ACTION_START_DOWNLOAD_UPDATES");
                break;
            case Actions.ACTION_START_DATABASE_RESTORE:
                activeView_number = R.layout.checking_for_database_in_remote;
                StoreTransmitter.updatedUIState("ACTION_START_DATABASE_RESTORE");
                break;
            case Actions.ACTION_DATABASE_RESTORE_AVAILABLE:
                activeView_number = R.layout.database_restore_available;
                StoreTransmitter.updatedUIState("ACTION_DATABASE_RESTORE_AVAILABLE");
                break;
            case Actions.ACTION_START_DATABASE_DOWNLOAD:
                activeView_number = R.layout.download_database;
                StoreTransmitter.updatedUIState("ACTION_START_DATABASE_MERGING");
                break;
            case Actions.ACTION_START_DATABASE_MERGING:
                activeView_number = R.layout.merging_database;
                StoreTransmitter.updatedUIState("ACTION_START_DATABASE_MERGING");
                break;
            case Actions.ACTION_UPLOAD_DATABASE_RESTORE_LOGS:
                Log.e("DATABASE_RESTORE_LOG", "called");
                String d = (String) actionData;
                String[] flags = d.split(" ");
                Log.e("flags", "Length " + flags.length);
                recordsInserted = Integer.parseInt(flags[0]);
                recordMismatched = Integer.parseInt(flags[1]);
                recordsDuplicated = Integer.parseInt(flags[2]);
                totalNumberOfRecords = Integer.parseInt(flags[3]);
                activeView_number = R.layout.uploading_database_restore_logs;
                Log.e("DATABASE_RESTORE_LOG", "recordsInserted " + recordsInserted + " recordMismatched " + recordMismatched + " recordsDuplicated " + recordsDuplicated + " totalNumberOfRecords " + totalNumberOfRecords);
                StoreTransmitter.updatedUIState("ACTION_UPLOAD_DATABASE_RESTORE_LOGS");
                break;
            case Actions.ACTION_FINISH_DATABASE_RESTORE:
                Log.e("FINISH_DATABASE_RESTORE", "called");
                String d1 = (String) actionData;
                String[] flags1 = d1.split(" ");
                Log.e("flags1", "Length " + flags1.length);
                recordsInserted = Integer.parseInt(flags1[0]);
                recordMismatched = Integer.parseInt(flags1[1]);
                recordsDuplicated = Integer.parseInt(flags1[2]);
                totalNumberOfRecords = Integer.parseInt(flags1[3]);
                activeView_number = R.layout.restore_database_finished;
                Log.e("FINISH_DATABASE_RESTORE", "recordsInserted " + recordsInserted + " recordMismatched " + recordMismatched + " recordsDuplicated " + recordsDuplicated + " totalNumberOfRecords " + totalNumberOfRecords);
                StoreTransmitter.updatedUIState("ACTION_FINISH_DATABASE_RESTORE");
                break;
            case Actions.ACTION_FINISH_DATABASE_RESTORE_WITHOUT_DATA:
                activeView_number = R.layout.restore_database_finished;
                StoreTransmitter.updatedUIState("ACTION_FINISH_DATABASE_RESTORE");
                break;
            case Actions.ACTION_HMD_UPDATE_DON_START_TC_UPDATE:
                activeView_number = R.layout.installing_test_controller;
                StoreTransmitter.updatedUIState("ACTION_HMD_UPDATE_DON_START_TC_UPDATE");
                break;

            case Actions.ACTION_BATTERY_NOT_OK_NOT_CHARGING:
                activeView_number = R.layout.warning_bno_not_charging;
                StoreTransmitter.updatedUIState("ACTION_BATTERY_NOT_OK_NOT_CHARGING");
                break;

            case Actions.ACTION_SHOW_HMD_DETAILS:
                activeView_number = R.layout.new_hmd_details_activity;
                StoreTransmitter.updatedUIState("ACTION_SHOW_HMD_DETAILS");
                break;

            case Actions.ACTION_SHOW_PRE_PRODUCTION_SCREEN:
                buttonClickData.buttonName = "DiagnosticNewTest";
                buttonClickData.buttonData = "DiagnosticNewTest";
                clickData.putString("onPass", "DiagnosticNewTestPass");
                clickData.putString("onFail", "DiagnosticNewTestFail");
                sentData = gson.toJson(buttonClickData);
                clickData.putInt("hashcode", sentData.hashCode());
                clickData.putString("data", sentData);
                if (communicationActive) {
                    StoreTransmitter.doCommFunction(StoreTransmitter.COMM_FUNCTION_CLICKONTC, clickData);
                    showToast = false;
                } else {
                    showToast = true;
                    toastMessage = "Connection with HMD Lost.Please Wait";
                }
                activeView_number = R.layout.activity_pre_production;
                StoreTransmitter.updatedUIState("ACTION_SHOW_PRE_PRODUCTION_SCREEN");
                break;

            case Actions.ACTION_SHOW_START_EI_SCREEN:
                activeView_number = R.layout.activity_starting_ei;
                StoreTransmitter.updatedUIState("ACTION_SHOW_START_EI_SCREEN");
                break;

            case Actions.ACTION_SHOW_CANT_EI_SCREEN:
                activeView_number = R.layout.error_cant_start_ei;
                StoreTransmitter.updatedUIState("ACTION_SHOW_CANT_EI_SCREEN");
                break;

            case Actions.ACTION_BATTERY_LOW_KEEP_CHARGING:
                activeView_number = R.layout.warning_bno_charging;
                StoreTransmitter.updatedUIState("ACTION_BATTERY_LOW_KEEP_CHARGING");
                break;

            case Actions.ACTION_PLUG_OUT_CHARGE_OK:
                activeView_number = R.layout.warning_bo_charging;
                StoreTransmitter.updatedUIState("ACTION_PLUG_OUT_CHARGE_OK");
                break;


            case Actions.ACTION_PAUSE_TEST:
                buttonClickData.buttonName = "PauseTest";
                buttonClickData.buttonData = "Pause";
                clickData.putString("onPass", "PauseTestPass");
                clickData.putString("onFail", "PauseTestFail");
                sentData = gson.toJson(buttonClickData);
                clickData.putInt("hashcode", sentData.hashCode());
                clickData.putString("data", sentData);
                if (communicationActive) {
                    StoreTransmitter.doCommFunction(StoreTransmitter.COMM_FUNCTION_CLICKONTC, clickData);
                    showToast = false;
                } else {
                    showToast = true;
                    toastMessage = "Connection with HMD Lost.Please Wait_PAUSETEST";
                }
                StoreTransmitter.updatedUIState("ACTION_PAUSE_TEST");
                break;

            case Actions.ACTION_UNPAUSE_TEST:
                buttonClickData.buttonName = "UnPauseTest";
                buttonClickData.buttonData = "UnPause";
                clickData.putString("onPass", "UnPauseTestPass");
                clickData.putString("onFail", "UnPauseTestFail");
                sentData = gson.toJson(buttonClickData);
                clickData.putInt("hashcode", sentData.hashCode());
                clickData.putString("data", sentData);
                if (communicationActive) {
                    StoreTransmitter.doCommFunction(StoreTransmitter.COMM_FUNCTION_CLICKONTC, clickData);
                    showToast = false;
                } else {
                    showToast = true;
                    toastMessage = "Connection with HMD Lost.Please Wait_UNPAUSE_TEST";
                }
                StoreTransmitter.updatedUIState("ACTION_UNPAUSE_TEST");
                break;
        }
    }

    private static void sendProductionSetUpStatus() {
        Log.e("ProductionSetUpStatus", "called");
        AppPreferencesHelper appPreferencesHelper = new AppPreferencesHelper(MyApplication.getInstance(), DEVICE_PREF);
        buttonClickData.buttonName = "ProductionSetupStatus";
        buttonClickData.buttonData = "" + appPreferencesHelper.getProductionSetUpStatus();
        clickData.putString("onPass", "ProductionSetupStatusPass");
        clickData.putString("onFail", "ProductionSetupStatusFail");
        sentData = gson.toJson(buttonClickData);
        clickData.putInt("hashcode", sentData.hashCode());
        clickData.putString("data", sentData);
        if (communicationActive) {
            StoreTransmitter.doCommFunction(StoreTransmitter.COMM_FUNCTION_CLICKONTC, clickData);
        }
    }

    private static void prepareForNewTest() {
        electronicInterfaceStatus = UNDEFINED;
        preTestPrbCounter = 0;
        stateBundle.putBoolean("dt_chronometerOn", false);
        stateBundle.clear();
        if (BuildConfig.IN21_012_PRB_Status) {
            isDisplayStatusUpdated = false;
            lastFiveDisplayStatus.clear();
        }
        // lastFiveDisplayStatus = new CircularFifoQueue<>(5);
        Log.d("InStore", "ACTION_PATIENT_DETAILS reached");
        if (communicationActive) {
            Log.d("InStore", "communicationActive");
            patientName = "";
            patientPhoneNumberVal = "";
            patientMrnNumberVal = "";
            patientDOBVal = "";
            patientSexVal = "";
            patientTestEyeVal = "Left Eye";
            patientTestPatternVal = "Select test pattern";
            patientTestStrategyVal = "Select test strategy";
            testSphericalInput = "";
            testCylindricalInput = "";
            testCylindricalAxisInput = "";
            testSphericalVal = "";
            testCylindricalVal = "";
            testCylindricalAxisVal = "";
            activeView_number = R.layout.activity_primary_test_details;
        } else {
            Log.d("InStore", "communication not Active");
            newTestVisibility = false;
            if (!isCommInitializationOver) {
                Log.d("InStore", "isCommInitializationOver not Active");
                StoreTransmitter.doCommFunction(StoreTransmitter.COMM_FUNCTION_INITIALIZE, clickData);
            }
        }
        StoreTransmitter.updatedUIState("prepareForNewTest");
    }

    private static void validateTestDetailsScreen() {
        if (communicationActive) {
            if (patientTestPatternVal.equals("Select test pattern")) {
                showToast = true;
                toastMessage = "Please select test pattern";
                showTip = true;
                tipId = R.id.test_pattern_spinner;
            } else if (patientTestStrategyVal.equals("Select test strategy")) {
                showToast = true;
                toastMessage = "Please select test strategy";
                showTip = true;
                tipId = R.id.test_strategy_spinner;
            } else {
                tipId = 0;
                showTip = false;
                Log.e(TAG, "validateTestDetailsScreen called");
                activeView_number = R.layout.activity_primary_patient_details;
                Actions.startEI();
            }
            StoreTransmitter.updatedUIState("validateTestDetailsScreen");
        }
    }

    private static void startLensScreen() {
        Actions.getActionGetPrbStatus();
        DatabaseInitializer.insertPatientInfo(AppDatabase.getAppDatabase(MainActivity.applicationContext), patientName, patientMrnNumberVal, patientPhoneNumberVal, patientDOBVal, patientSexVal);
        activeView_number = R.layout.activity_lens_power_settings;
        StoreTransmitter.updatedUIState("startLensScreen");
    }

    private static void dataFromHMD(String incomingData) {
        AppPreferencesHelper appPreferencesHelper = new AppPreferencesHelper(MainActivity.applicationContext, PREF_NAME);
        AppPreferencesHelper devicePreferencesHelper = new AppPreferencesHelper(MyApplication.getInstance(), DEVICE_PREF);
        JSONObject dataFromHMDBundle; //this is the fully wrapped data bundle
        String dataTypeName;
        String data;
        //CommonUtils.writeToMyDebugFile("\ndatafromhmd incomingData " + incomingData);
        try {
            dataFromHMDBundle = new JSONObject(incomingData);
            dataTypeName = dataFromHMDBundle.getString("dataName");
            data = dataFromHMDBundle.getString("data");
            Log.e("dataName", "" + dataTypeName);
            //CommonUtils.writeToMyDebugFile("\ndataTypeName " + dataTypeName);
            //CommonUtils.writeToMyDebugFile("\ndataName " + data);
            switch (dataTypeName) {

                case "DiagnosticColVal":
                    calibData = data;
                    if (activeView_number == R.layout.activity_pre_production)
                        StoreTransmitter.updatedUIState("DiagnosticColVal");
                    break;

                case "HMD_IDLE_TURNING_OFF":
                    showHMDIdleTurnOffWarning(MainActivity.dialogReference);
                    break;

                case "HMD_BATTERY_BELOW_20":
                    hmdBatteryBelow20(MainActivity.dialogReference);
                    break;
                case "HMD_TURNING_OFF_DUE_TO_IDLENESS":
                    hmdTurnedOffDueToIdleness(MainActivity.dialogReference);
                    break;

                case "PRB_CLICK_RECEIVED":
                    CommonUtils.writeToBugFixLogFile("PRB_CLICK_RECEIVED is triggered from HMD and preTestPrbCounter is " + preTestPrbCounter);
                    Log.e("PRB_CLICK_RECEIVED", "reached");
                    preTestPrbCounter = preTestPrbCounter + 1;
                    Log.e("PRB_CLICK_RECEIVED", "preTestPrbCounter " + preTestPrbCounter);
                    if (setPatientTestStrategyVal.equalsIgnoreCase("screening")) {
                        Log.e("PRB_CLICK_RECEIVED", "In Screening");
                        if (preTestPrbCounter == 1) {
                            dt_chronometerOn = true;
                            dt_timebase = SystemClock.elapsedRealtime();
                            StoreTransmitter.updatedUIState("PRB_CLICK_RECEIVED");
                        }
                    } else {
                        Log.e("PRB_CLICK_RECEIVED", "In other Screening");
                        if (preTestPrbCounter > 1) {
                            dt_chronometerOn = true;
                            dt_timebase = SystemClock.elapsedRealtime();
                            StoreTransmitter.updatedUIState("PRB_CLICK_RECEIVED");
                        }
                    }
                    break;
                case "NEW_DIAGNOSTIC_RESULT":
                    calibData = data;
                    if (activeView_number == R.layout.activity_pre_production)
                        StoreTransmitter.updatedUIState("DiagnosticColVal");
                    break;

                case "FOVEA":
                    //JSONObject foveaDetails = new JSONObject(data);
                    Log.e("foveaDetails", data);
                    if (activeView_number == R.layout.activity_during_test)
                        showFoveaDialog(MainActivity.dialogReference, data);
                    break;

                case "CAMERA_STATUS":
                    Log.e("CAMERA_STATUS", " " + data);
                    accessoriesChecked = "Camera";
                    if (data.equals("CameraOkay"))
                        eyeTrackingStatus = EyeTrackingStatus.EYE_TRACKING_OK;
                    else if (data.equals("CameraNotOkay")) {
                        eyeTrackingStatus = EyeTrackingStatus.EYE_TRACKING_NOT_OK;
                    }
                    break;

                case "CLICKER_STATUS":
                    if (BuildConfig.IN21_012_PRB_Status) {
                        Log.e("CLICKER_STATUS", " " + data);
                        accessoriesChecked = "Clicker";
                        if (data.equals("ALIVE"))
                            clickerStatus = ClickerStatus.CLICKER_OK;
                        else if (data.equals("DISC")) {
                            clickerStatus = ClickerStatus.CLICKER_NOT_OK;
                            Actions.getActionGetPrbStatus();
                        }
                        if (activeView_number == R.layout.accessories_status) {
                            StoreTransmitter.updatedUIState("CLICKER_STATUS");
                        }
                    } else {
                        Log.e("CLICKER_STATUS", " " + data);
                        accessoriesChecked = "Clicker";
                        if (data.equals("ALIVE"))
                            clickerStatus = ClickerStatus.CLICKER_OK;
                        else if (data.equals("DISC"))
                            clickerStatus = ClickerStatus.CLICKER_NOT_OK;
                        if (activeView_number == R.layout.accessories_status) {
                            StoreTransmitter.updatedUIState("CLICKER_STATUS");
                        }
                    }
                    break;
                case "DISPLAY_STATUS":
                    if (BuildConfig.IN21_012_PRB_Status) {
                        String lastFiveStatuses = devicePreferencesHelper.getLastFiveDisplayStatusString();
                        Log.e("lastFiveDisplayStatus", " " + lastFiveDisplayStatus.size());
                        if (lastFiveStatuses != null && !isDisplayStatusUpdated) {
                            String[] text = gson.fromJson(lastFiveStatuses, String[].class);
                            List<String> statuses = Arrays.asList(text);
                            for (String e : statuses) {
                                Log.e("lastFiveDisplayStatus", "statuses " + e);
                            }
                            lastFiveDisplayStatus.addAll(statuses);
                        }
                        Log.e("lastFiveDisplayStatus", " " + lastFiveDisplayStatus.size());
                        Log.e("lastFiveDisplayStatus", " " + isDisplayStatusUpdated);
                        accessoriesChecked = "Display";
                        displayStatusFromHMD = (data.split(" ")[0]);
                        screenBrightnessLevelPD1String = data.split(" ")[1];
                        screenBrightnessLevelPD2String = data.split(" ")[2];
                        Log.e("DISPLAY_STATUS", " screenBrightnessLevelPD1String " + screenBrightnessLevelPD1String + " screenBrightnessLevelPD2String " + screenBrightnessLevelPD2String);
                        screenBrightnessLevelPD1 = Double.parseDouble(screenBrightnessLevelPD1String);
                        screenBrightnessLevelPD2 = Double.parseDouble(screenBrightnessLevelPD2String);
                        screenBrightnessCombo = "" + screenBrightnessLevelPD1String + "_" + screenBrightnessLevelPD2String;
                        isPhotoDiode1Working = Boolean.parseBoolean(data.split(" ")[3]);
                        isPhotoDiode2Working = Boolean.parseBoolean(data.split(" ")[4]);
                        Log.e("DISPLAY_STATUS", "displayStatusFromHMD " + displayStatusFromHMD + " isPhotoDiode1Working " + isPhotoDiode1Working + " isPhotoDiode2Working "
                                + isPhotoDiode2Working + " screenBrightnessLevelPD1 " + screenBrightnessLevelPD1 + " screenBrightnessLevelPD2 " + screenBrightnessLevelPD2);
                        if (displayStatusFromHMD.equalsIgnoreCase("SENSOR_WORKING")) {
                            if (screenBrightnessLevelPD1 >= 100 && screenBrightnessLevelPD2 >= 100) {
                                displayStatus = DisplayStatus.DISPLAY_OK;
                                if (!isDisplayStatusUpdated)
                                    lastFiveDisplayStatus.add("DISPLAY_OK");
                                isDisplayStatusUpdated = true;
                                Log.e("lastFiveDisplayStatus", "after adding " + lastFiveDisplayStatus.size());
                            } else {
                                displayStatus = DisplayStatus.DISPLAY_NOT_OK;
                                if (!isDisplayStatusUpdated)
                                    lastFiveDisplayStatus.add("DISPLAY_NOT_OK");
                                isDisplayStatusUpdated = true;
                                FirebaseCrashlytics.getInstance().recordException(new ScreenBrightnessException("PDR1_PDR2 " + screenBrightnessCombo));
                                CommonUtils.sendAnalytics(MyApplication.getInstance(), screenBrightnessLevelPD1String, screenBrightnessLevelPD2String);
                                Log.e("lastFiveDisplayStatus", "after adding " + lastFiveDisplayStatus.size());
                            }
                        } else if (displayStatusFromHMD.equalsIgnoreCase("SENSOR_NOT_WORKING")) {
                            displayStatus = DisplayStatus.DISPLAY_NOT_OK;
                            if (!isDisplayStatusUpdated)
                                lastFiveDisplayStatus.add("DISPLAY_NOT_OK");
                            isDisplayStatusUpdated = true;
                            FirebaseCrashlytics.getInstance().recordException(new ScreenBrightnessException("DISPLAY_NOT_OK PDR1_PDR2 " + screenBrightnessCombo));
                            CommonUtils.sendAnalytics(MyApplication.getInstance(), screenBrightnessLevelPD1String, screenBrightnessLevelPD2String);
                            Log.e("lastFiveDisplayStatus", "after adding " + lastFiveDisplayStatus.size());
                        }
                        String jsonText = gson.toJson(lastFiveDisplayStatus);
                        devicePreferencesHelper.setLastFiveDisplayStatusString(jsonText);

                        if (activeView_number == R.layout.activity_waitscreen_accessory_status) {
                            if (displayStatus == DisplayStatus.DISPLAY_OK && clickerStatus == ClickerStatus.CLICKER_OK
                                    && eyeTrackingStatus == EyeTrackingStatus.EYE_TRACKING_OK)
                                Actions.beginTestProfile();
                            else
                                Actions.showAccessoryStatus();
                        }
                    } else {
                        String lastFiveStatuses = devicePreferencesHelper.getLastFiveDisplayStatusString();
                        if (lastFiveStatuses != null) {
                            String[] text = gson.fromJson(lastFiveStatuses, String[].class);
                            List<String> statuses = Arrays.asList(text);
                            for (String e : statuses) {
                                Log.e("Statuses", "" + e);
                            }
                            lastFiveDisplayStatus.addAll(statuses);
                        }

                        accessoriesChecked = "Display";
                        displayStatusFromHMD = (data.split(" ")[0]);
                        screenBrightnessLevelPD1String = data.split(" ")[1];
                        screenBrightnessLevelPD2String = data.split(" ")[2];
                        Log.e("DISPLAY_STATUS", " screenBrightnessLevelPD1String " + screenBrightnessLevelPD1String + " screenBrightnessLevelPD2String " + screenBrightnessLevelPD2String);
                        screenBrightnessLevelPD1 = Double.parseDouble(screenBrightnessLevelPD1String);
                        screenBrightnessLevelPD2 = Double.parseDouble(screenBrightnessLevelPD2String);
                        screenBrightnessCombo = "" + screenBrightnessLevelPD1String + "_" + screenBrightnessLevelPD2String;
                        isPhotoDiode1Working = Boolean.parseBoolean(data.split(" ")[3]);
                        isPhotoDiode2Working = Boolean.parseBoolean(data.split(" ")[4]);
                        Log.e("DISPLAY_STATUS", "displayStatusFromHMD " + displayStatusFromHMD + " isPhotoDiode1Working " + isPhotoDiode1Working + " isPhotoDiode2Working "
                                + isPhotoDiode2Working + " screenBrightnessLevelPD1 " + screenBrightnessLevelPD1 + " screenBrightnessLevelPD2 " + screenBrightnessLevelPD2);
                        if (displayStatusFromHMD.equalsIgnoreCase("SENSOR_WORKING")) {
                            if (screenBrightnessLevelPD1 >= 100 && screenBrightnessLevelPD2 >= 100) {
                                displayStatus = DisplayStatus.DISPLAY_OK;
                                lastFiveDisplayStatus.add("DISPLAY_OK");
                                Log.e("lastFiveSize", "after adding " + lastFiveDisplayStatus.size());
                            } else {
                                displayStatus = DisplayStatus.DISPLAY_NOT_OK;
                                lastFiveDisplayStatus.add("DISPLAY_NOT_OK");
                                FirebaseCrashlytics.getInstance().recordException(new ScreenBrightnessException("PDR1_PDR2 " + screenBrightnessCombo));
                                CommonUtils.sendAnalytics(MyApplication.getInstance(), screenBrightnessLevelPD1String, screenBrightnessLevelPD2String);
                                Log.e("lastFiveSize", "after adding " + lastFiveDisplayStatus.size());
                            }
                        } else if (displayStatusFromHMD.equalsIgnoreCase("SENSOR_NOT_WORKING")) {
                            displayStatus = DisplayStatus.DISPLAY_NOT_OK;
                            lastFiveDisplayStatus.add("DISPLAY_NOT_OK");
                            FirebaseCrashlytics.getInstance().recordException(new ScreenBrightnessException("DISPLAY_NOT_OK PDR1_PDR2 " + screenBrightnessCombo));
                            CommonUtils.sendAnalytics(MyApplication.getInstance(), screenBrightnessLevelPD1String, screenBrightnessLevelPD2String);
                            Log.e("lastFiveSize", "after adding " + lastFiveDisplayStatus.size());
                        }
                        String jsonText = gson.toJson(lastFiveDisplayStatus);
                        devicePreferencesHelper.setLastFiveDisplayStatusString(jsonText);

                        if (activeView_number == R.layout.activity_waitscreen_accessory_status) {
                            if (displayStatus == DisplayStatus.DISPLAY_OK && clickerStatus == ClickerStatus.CLICKER_OK
                                    && eyeTrackingStatus == EyeTrackingStatus.EYE_TRACKING_OK)
                                Actions.beginTestProfile();
                            else
                                Actions.showAccessoryStatus();
                        }
                    }
                    break;

                case "HMD_VERSION":
                    try {
                        Actions.setActionSaveVectorDatainHMD();
                        JSONObject hmdDetails = new JSONObject(data);
                        updateInstalledHMDVersion(hmdDetails.getString("VersionName"), hmdDetails.getInt("VersionCode"), hmdDetails.getString("ModelName"));
                    } catch (JSONException e) {
                        Log.e(TAG, " " + e.getMessage());
                        FirebaseCrashlytics.getInstance().recordException(e);
                    }
                    Actions.HMDConnected();
                    break;
                case "HMD_BATTERY_LEVEL":
                    BatteryManager bm = (BatteryManager) MyApplication.getInstance().getSystemService(BATTERY_SERVICE);
                    int batLevel = bm.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);
                    if (activeView_number != R.layout.download_update_screen && activeView_number != R.layout.connected_to_hmd_during_update)
                        showBatteryStatus(MainActivity.dialogReference, data, batLevel);
                    else
                        StoreTransmitter.updatedUIState("HMD_BATTERY_LEVEL");
                    sendProductionSetUpStatus();
                    break;
                case "PRB_COUNT":
                    try {
                        CommonUtils.setPRBCount(Integer.parseInt(data));
                    } catch (Exception e) {
                        Log.e("Exception", "PRB_COUNT " + e.getMessage());
                    }
                    break;
                case "HMD_CAMERA_AB_VALUE":
                    try {
                        JSONObject hmdCameraDetails = new JSONObject(data);
                        updateHMDCameraABDetails((float) hmdCameraDetails.getDouble("RightCameraAlpha"), (float) hmdCameraDetails.getDouble("RightCameraBeta"),
                                (float) hmdCameraDetails.getDouble("LeftCameraAlpha"), (float) hmdCameraDetails.getDouble("LeftCameraBeta"));
                    } catch (JSONException e) {
                        Log.e(TAG, " " + e.getMessage());
                        FirebaseCrashlytics.getInstance().recordException(e);
                    }
                    break;

                case "USERSYNC":
                    if (data.equals("Sync_Success")) {
                        devicePreferencesHelper.setFirstTimeUserConnection(true);
                        StoreTransmitter.updatedUIState("USERSYNC");
                    } else {
                        devicePreferencesHelper.setFirstTimeUserConnection(false);
                        StoreTransmitter.updatedUIState("USERSYNC");
                    }
                    break;
                case "PRODUCTIONSYNC":
                    if (data.equals("Sync_Success")) {
                        devicePreferencesHelper.setFirstTimeProductionConnection(true);
                        StoreTransmitter.updatedUIState("PRODUCTIONSYNC");
                    } else {
                        devicePreferencesHelper.setFirstTimeProductionConnection(false);
                        StoreTransmitter.updatedUIState("PRODUCTIONSYNC");
                    }
                    break;
                case "NEW_TEST_INFORMATION":
                    resetArrays();
                    //dataReceived = new JSONObject(data);
                    //CommonUtils.writeJson(dataReceived, MyApplication.getInstance());
                    new AppPreferencesHelper(MyApplication.getInstance(), PREF_NAME).setLastInsertedRow("-1");
                    PerimetryObject_V2.FinalPerimetryResultObject finalPerimetryResultObject = gson.fromJson(data, PerimetryObject_V2.FinalPerimetryResultObject.class);

                    setPatientName = appPreferencesHelper.getPatientFirstName();
                    setPatientSexVal = finalPerimetryResultObject.Patient.PatientSex;
                    setPatientDOBVal = finalPerimetryResultObject.Patient.PatientBirthDate;
                    setPatientPhoneNumberVal = finalPerimetryResultObject.Patient.PatientID;
                    setPatientMrnNumberVal = finalPerimetryResultObject.Patient.PatientUID;
                    setPatientTestEyeVal = finalPerimetryResultObject.Series.Laterality.equals("L") ? "Left Eye" : "Right Eye";
                    setPatientTestPatternVal = finalPerimetryResultObject.Series.PatternSequence.CodeMeaning;
                    setPatientTestStrategyVal = finalPerimetryResultObject.Series.StrategySequence.CodeMeaning;

                    setTestCylindricalAxis = Double.toString(finalPerimetryResultObject.Measurements.OphthalmicPatientClinicalInformationAndTestLensParameters.OphthalmicPatientClinicalInformationLeftEyeSequence.RefractiveParametersUsedOnPatientSequence.CylinderAxis);
                    setTestCylindricalVal = Double.toString(finalPerimetryResultObject.Measurements.OphthalmicPatientClinicalInformationAndTestLensParameters.OphthalmicPatientClinicalInformationLeftEyeSequence.RefractiveParametersUsedOnPatientSequence.CylindricalLensPower);
                    setTestSphericalVal = Double.toString(finalPerimetryResultObject.Measurements.OphthalmicPatientClinicalInformationAndTestLensParameters.OphthalmicPatientClinicalInformationLeftEyeSequence.RefractiveParametersUsedOnPatientSequence.SphericalLensPower);

                    activeView_number = R.layout.activity_test_profile;
                    showToast = false;

                    StoreTransmitter.updatedUIState("NEW_TEST_INFORMATION");
                    break;

                case "START_CALIB":
                    Log.d("InTag", "START_CALIB");
                    if (activeView_number == R.layout.activity_waitscreen_calibration_live_feed) {
                        activeView_number = R.layout.activity_calibiration;
                        StoreTransmitter.updatedUIState("START_CALIB");
                    }
                    break;
               // case "IPD_BUTTON_STATUS":
                    //Log.d("InTag", "IPD_BUTTON_STATUS");
                   // if (activeView_number == R.layout.activity_ipd_settings) {
                       // IPD_button_Visiblity = data;
                       // StoreTransmitter.updatedUIState("IPD_BUTTON_STATUS");
                   // }
                   // break;
                case "HMD_SHUT_DOWN":
                    Log.e("HMD_SHUT_DOWN", "Called");
                    Actions.HMDNotConnected();
                    break;
                case "EI_MODULE_STATUS":
                    switch (data) {
                        case "SILENTLY_STARTED":
                            electronicInterfaceStatus = SILENTLY_STARTED;
                            Log.e("EI_MODULE_STATUS", "SilentlyStarted");
                            break;
                        case "SILENTLY_FAILED":
                            electronicInterfaceStatus = SILENTLY_FAILED;
                            StoreTransmitter.updatedUIState("EI_MODULE_STATUS");
                            Log.e("EI_MODULE_STATUS", "SilentlyFailed");
                            break;
                        case "EI_MODULE_CONNECTED":
                            electronicInterfaceStatus = EI_MODULE_CONNECTED;
                            if (activeView_number == R.layout.checking_interfaces_screen || activeView_number == R.layout.error_component_failure) {
                                Actions.beginLensSettings();
                                StoreTransmitter.updatedUIState("EI_MODULE_CONNECTED");
                            }
                            Actions.startTest();
                            Log.e("EI_MODULE_STATUS", "EIModuleStarted, in store");
                            break;
                        case "EI_MODULE_DISCONNECTED":
                            electronicInterfaceStatus = EI_MODULE_DISCONNECTED;
                            Log.e("EI_MODULE_STATUS", "EI_MODULE_DISCONNECTED, in store");
                            break;
                        case "INITIALIZING_EI":
                            reInitializeStatuses();
                            Log.e("EI_MODULE_STATUS", "Checking the EI modules in background. Please wait..");
                            break;
                        case "INITIALIZATION_FAILED":
                            electronicInterfaceStatus = INITIALIZATION_FAILED;
                            if (activeView_number == R.layout.checking_interfaces_screen) {
                                activeView_number = R.layout.error_cant_start_ei;
                                StoreTransmitter.updatedUIState("INITIALIZATION_FAILED");
                            }
                            Log.e("EI_MODULE_STATUS", "ERROR : Failed to initiate accessory modules.");
                            break;
                    }
                    break;
                case "HMD_CAMERA_RESTARTED":
                    Log.e("HMD_CAMERA_RESTARTED", "called");
                    electronicInterfaceStatus = EI_MODULE_CONNECTED;
                    if (activeView_number == R.layout.activity_ipd_settings || activeView_number == R.layout.activity_during_test
                            || activeView_number == R.layout.accessories_status) {
                        toastMessage = data;
                        showToast = true;
                        StoreTransmitter.updatedUIState("HMD_CAMERA_RESTARTED");
                        FirebaseCrashlytics.getInstance().recordException(new HmdCameraRestartedException("HMD_CAMERA_RESTARTED during " + activeView_number));
                    } else if (activeView_number == R.layout.checking_interfaces_screen) {
                        if (previousView_number == R.layout.activity_primary_patient_details) {
                            Log.e("HMD_CAMERA_RESTARTED", "Called previousView_number == R.layout.activity_primary_patient_details");
                            activeView_number = R.layout.activity_lens_power_settings;
                            StoreTransmitter.updatedUIState("HMD_CAMERA_RESTARTED previousView_number == R.layout.activity_primary_patient_details ");
                        } else if (previousView_number == R.layout.activity_lens_power_settings) {
                            Log.e("HMD_CAMERA_RESTARTED", "Called previousView_number == R.layout.activity_lens_power_settings");
                        }
                    }
                    break;
                case "HMD_UPDATE_INSTALLED":
                    if (activeView_number == R.layout.installing_hmd_software) {
                        Actions.startTCUpdate();
                    } else if (activeView_number == R.layout.installing_hmd_alone) {
                        closeTheTestController(MainActivity.dialogReference);
                    }
                    break;
                case "HMD_UPDATE_FAILED":
                    if (activeView_number == R.layout.hmd_update_tranfer_screen) {
                        activeView_number = R.layout.error_hmd_update_failed;
                        devicePreferencesHelper.setLatestHmdVersionCode(0);
                        devicePreferencesHelper.setLatestHmdVersionName("");
                        StoreTransmitter.updatedUIState("HMD_UPDATE_FAILED");
                    }
                    break;
                case "HMD_BATTERY_STATUS":
                    Log.e("HMD_BATTERY_STATUS", data);
                    switch (data) {
                        case "BATTERY_OK":
                            hmdBatteryStatus = BATTERY_OK;
                            if (activeView_number == R.layout.warning_bo_charging) {
                                Actions.beginPatientDetails();
                            }
                            break;
                        case "BATTERY_LOW_KEEP_CHARGING":
                            hmdBatteryStatus = BATTERY_LOW_KEEP_CHARGING;
                            Actions.batteryLowKeepCharging();
                            Log.e("EI_MODULE_STATUS", "Battery Level Low. Continue charging");
                            break;
                        case "BATTERY_LOW_PLEASE_CHARGE_HMD":
                            hmdBatteryStatus = BATTERY_LOW_PLEASE_CHARGE_HMD;
                            Actions.batteryNotOkayNotCharging();
                            Log.e("EI_MODULE_STATUS", "Battery Level Low. Please charge headset");
                            break;
                        case "PLUG_OUT_CHARGE_OK":
                            hmdBatteryStatus = PLUG_OUT_CHARGE_OK;
                            Log.e("EI_MODULE_STATUS", "Plug the device out from power supply and click below");
                            break;
                    }
                    break;

                case "HMD_ACCESSIBILITY_STATUS":
                    isHmdAccessibilityEnabled = data.equalsIgnoreCase("enabled");
                    toastMessage = isHmdAccessibilityEnabled ? "Accessibility Service Enabled" : "Accessibility Service Diasabled";
                    showToast = true;
                    StoreTransmitter.updatedUIState("HMD_ACCESSIBILITY_STATUS");
                    break;

                case "HMD_BATT":
                    batteryLevel = data;
                    batteryLevelVisibility = true;
                    //StoreTransmitter.updatedUIState();
                    Log.d("HMD_BATT", " " + data);
                    break;
                case "TEST_ABORTED":
                    Log.e("InTag", "TEST_ABORTED");
                    if (activeView_number == R.layout.activity_home_screen) {
                        enableNewTest();
                    } else if (activeView_number == R.layout.response_waitscreen_abort_during_test) {
                        MainActivity.DT_Image_Update_Thread.interrupt();
                        MainActivity.testImageViewLinked = false;
                        activeView_number = R.layout.activity_home_screen;
                    } else if (activeView_number == R.layout.response_waitscreen_abort_ipd) {
                        MainActivity.ipd_Image_Update_Thread.interrupt();
                        MainActivity.ipdImageViewLinked = false;
                        activeView_number = R.layout.activity_home_screen;
                    } else if (activeView_number == R.layout.response_waitscreen_accessory_abort) {
                        activeView_number = R.layout.activity_home_screen;
                    } else if (activeView_number == R.layout.activity_primary_test_details) {
                        //Do nothing
                    } else {
                        activeView_number = R.layout.activity_home_screen;
                    }
                    StoreTransmitter.updatedUIState("TEST_ABORTED");
                    break;
                case "EI_COMPONENT_FAILURE":
                    String[] flags = data.split(" ");
                    Log.e("EI_COMPONENT_FAILURE", "isUsbNull " + flags[1]);
                    Log.e("EI_COMPONENT_FAILURE", "isUsbPermissionGotSet " + flags[2]);
                    FirebaseCrashlytics.getInstance().recordException(new ComponentFailureException("Component Failure IsUSBNull " + flags[1] + " isUsbPermissionGotSet " + flags[2]));
                    Actions.beginEiComponentFailure();
                    break;
                case "START_IPD":
                    sendProductionSetUpStatus();
                    Log.d("InTag", "START_IPD");
                    //CommunicationService.videoFrame = null;
                    if (activeView_number == R.layout.activity_waitscreen_ipd_live_feed) {
                        activeView_number = R.layout.activity_ipd_settings;
                        StoreTransmitter.updatedUIState("START_IPD");
                    }
                    break;

                case "NEW_RESULT":
                    Log.d("NewResult", "Received");
                    //dataReceived = new JSONObject(data);
                    PerimetryObject_V2.FinalPerimetryResultObject finalPerimetryTestResultObject = gson.fromJson(data, PerimetryObject_V2.FinalPerimetryResultObject.class);
                    String pattern = finalPerimetryTestResultObject.Series.PatternSequence.CodeMeaning;
                    int dt_new_result_length = finalPerimetryTestResultObject.Measurements.VisualFieldStaticPerimetryTestMeasurements.VisualFieldTestPointSequence.length;
                    dt_new_result.clear();
                    dt_result_sensitivity.clear();
                    dt_result_seen.clear();


                    setPatientName = appPreferencesHelper.getPatientFirstName();
                    setPatientSexVal = finalPerimetryTestResultObject.Patient.PatientSex;
                    setPatientDOBVal = finalPerimetryTestResultObject.Patient.PatientBirthDate;
                    setPatientPhoneNumberVal = finalPerimetryTestResultObject.Patient.PatientID;
                    setPatientMrnNumberVal = finalPerimetryTestResultObject.Patient.PatientUID;
                    setPatientTestEyeVal = finalPerimetryTestResultObject.Series.Laterality.equals("L") ? "Left Eye" : "Right Eye";
                    setPatientTestPatternVal = finalPerimetryTestResultObject.Series.PatternSequence.CodeMeaning;
                    setPatientTestStrategyVal = finalPerimetryTestResultObject.Series.StrategySequence.CodeMeaning;

                    setTestCylindricalAxis = Double.toString(finalPerimetryTestResultObject.Measurements.OphthalmicPatientClinicalInformationAndTestLensParameters.OphthalmicPatientClinicalInformationLeftEyeSequence.RefractiveParametersUsedOnPatientSequence.CylinderAxis);
                    setTestCylindricalVal = Double.toString(finalPerimetryTestResultObject.Measurements.OphthalmicPatientClinicalInformationAndTestLensParameters.OphthalmicPatientClinicalInformationLeftEyeSequence.RefractiveParametersUsedOnPatientSequence.CylindricalLensPower);
                    setTestSphericalVal = Double.toString(finalPerimetryTestResultObject.Measurements.OphthalmicPatientClinicalInformationAndTestLensParameters.OphthalmicPatientClinicalInformationLeftEyeSequence.RefractiveParametersUsedOnPatientSequence.SphericalLensPower);


                    FL_Denominator = finalPerimetryTestResultObject.Measurements.VisualFieldStaticPerimetryTestReliability.FixationSequence.FixationCheckedQuantity;
                    FL_Numerator = finalPerimetryTestResultObject.Measurements.VisualFieldStaticPerimetryTestReliability.FixationSequence.PatientNotProperlyFixatedQuantity;

                    FP_Denominator = finalPerimetryTestResultObject.Measurements.VisualFieldStaticPerimetryTestReliability.VisualFieldCatchTrialSequence.PositiveCatchTrialsQuantity;
                    FP_Numerator = finalPerimetryTestResultObject.Measurements.VisualFieldStaticPerimetryTestReliability.VisualFieldCatchTrialSequence.FalsePositivesQuantity;

                    FN_Denominator = finalPerimetryTestResultObject.Measurements.VisualFieldStaticPerimetryTestReliability.VisualFieldCatchTrialSequence.NegativeCatchTrialsQuantity;
                    FN_Numerator = finalPerimetryTestResultObject.Measurements.VisualFieldStaticPerimetryTestReliability.VisualFieldCatchTrialSequence.FalseNegativesQuantity;

                    Log.e("Relaibility BugTCResult", "FL_Denominator" + FL_Denominator);
                    Log.e("Relaibility BugTCResult", "FL_Numerator" + FL_Numerator);
                    Log.e("Relaibility BugTCResult", "FP_Denominator" + FP_Denominator);
                    Log.e("Relaibility BugTCResult", "FP_Numerator" + FP_Numerator);
                    Log.e("Relaibility BugTCResult", "FN_Denominator" + FN_Denominator);
                    Log.e("Relaibility BugTCResult", "FN_Numerator" + FN_Numerator);


                    for (int i = 0; i < dt_new_result_length; i++) {
                        String xVal, yval, xyval;
                        int x = (int) finalPerimetryTestResultObject.Measurements.VisualFieldStaticPerimetryTestMeasurements.VisualFieldTestPointSequence[i].VisualFieldTestPointXCoordinate;
                        int y = (int) finalPerimetryTestResultObject.Measurements.VisualFieldStaticPerimetryTestMeasurements.VisualFieldTestPointSequence[i].VisualFieldTestPointYCoordinate;
                        int s = (int) finalPerimetryTestResultObject.Measurements.VisualFieldStaticPerimetryTestMeasurements.VisualFieldTestPointSequence[i].SensitivityValue;
                        String seen = finalPerimetryTestResultObject.Measurements.VisualFieldStaticPerimetryTestMeasurements.VisualFieldTestPointSequence[i].StimulusResults;

                        if (!(x == 0 & y == 0)) {
                            if (pattern.equals("24-2") || pattern.equals("30-2")) {
                                xVal = x > 0 ? "x" + ((x + 3) / 6) : "-x" + ((-x + 3) / 6);
                                yval = y > 0 ? "y" + ((y + 3) / 6) : "-y" + ((-y + 3) / 6);
                                xyval = xVal.concat(yval);
                            } else {
                                xVal = x > 0 ? "x" + ((x + 1) / 2) : "-x" + ((-x + 1) / 2);
                                yval = y > 0 ? "y" + ((y + 1) / 2) : "-y" + ((-y + 1) / 2);
                                xyval = xVal.concat(yval);
                            }
                            dt_new_result.add(xyval);
                            dt_result_sensitivity.add("" + s);
                            dt_result_seen.add(seen);
                        }
                    }
                    dt_result_seen_temp = dt_result_seen;
                    if (Build.VERSION.SDK_INT == Build.VERSION_CODES.LOLLIPOP_MR1 && BuildConfig.IN21_011_Saving_Pupil_Images && (setPatientTestStrategyVal.equals("Custom Zest") ||setPatientTestStrategyVal.equals("Custom Zest1")|| setPatientTestStrategyVal.equals("Custom FT") || setPatientTestStrategyVal.equals("Custom Fast"))) {
                      /*  List<String> result = listB.stream()
                                .filter(not(new HashSet<>(listA)::contains))
                                .collect(Collectors.toList());*/

                        List<String> result = new ArrayList(dt_new_result);
                        result.removeAll(new HashSet<>(dt_result_seen_temp));

                        imageFileName = CommonUtils.getCurrentDateTimeAsFileName(patientMrnNumberVal, patientTestEyeVal, patientTestPatternVal, patientTestStrategyVal);
                        CommonUtils.writeToImageLogsFor21(imageFileName + "_" + result.get(0), CommunicationService.videoFrame);

                    }
                    if (activeView_number != R.layout.activity_during_test && !isAbortClicked)
                        activeView_number = R.layout.activity_during_test;
                    StoreTransmitter.updatedUIState("NEW_RESULT");
                    break;

                case "NEW_FINAL_RESULT":

                    isChronometerRunning = false;
                    Log.d("Final Result", "Received");
                    dt_timebase = SystemClock.elapsedRealtime();
                    //dataReceived = new JSONObject(data);
                    Log.d("InFinal", " " + data);
                    if (isValidJson(data)) {
                        finalPerimetryTestFinalResultObject = gson.fromJson(data, PerimetryObject_V2.FinalPerimetryResultObject.class);
                        finalPerimetryTestFinalResultObject.Patient.PatientName = appPreferencesHelper.getPatientFirstName();
                        setPatientName = appPreferencesHelper.getPatientFirstName();
                        setPatientMrnNumberVal = finalPerimetryTestFinalResultObject.Patient.PatientUID;
                        setPatientPhoneNumberVal = finalPerimetryTestFinalResultObject.Patient.PatientID;
                        setPatientDOBVal = finalPerimetryTestFinalResultObject.Patient.PatientBirthDate;
                        setPatientSexVal = finalPerimetryTestFinalResultObject.Patient.PatientSex;
                        setPatientTestEyeVal = finalPerimetryTestFinalResultObject.Series.Laterality.equals("L") ? "Left Eye" : "Right Eye";
                        setPatientTestStrategyVal = finalPerimetryTestFinalResultObject.Series.StrategySequence.CodeMeaning;
                        setPatientTestPatternVal = finalPerimetryTestFinalResultObject.Series.PatternSequence.CodeMeaning;
                        long mills = SystemClock.elapsedRealtime() - chronometerForDurationCalculation.getBase();
                        CommonUtils.writeToTestLogFile("\nMills NFR " + CommonUtils.duringCalculationFromMillis(mills));
                        duration = CommonUtils.duringCalculationFromMillis(mills);
                        finalPerimetryTestFinalResultObject.Series.duration = duration;
                        CommonUtils.writeToTestLogFile("\nDuration NFR " + finalPerimetryTestFinalResultObject.Series.duration);
                        timeWhenStopped = 0;
                        insertedDateTime = "" + new Date().getTime();
                        new InsertRecordIntoDb(b -> {
                            insertedResultID = "" + b;
                            Log.d("InsertedID", "Store " + b);
                            return b;
                        }).execute(setPatientName, setPatientMrnNumberVal, setPatientPhoneNumberVal, setPatientDOBVal, setPatientSexVal, setPatientTestEyeVal,
                                setPatientTestStrategyVal, setPatientTestPatternVal, "Suggestion", gson.toJson(finalPerimetryTestFinalResultObject), insertedDateTime);
                        AppPreferencesHelper resultAppPreferencesHelper = new AppPreferencesHelper(applicationContext, RESULT_PREF);
                        new AppPreferencesHelper(applicationContext, PREF_NAME).setPatientDetailsViewVisibility(false);
                        resultAppPreferencesHelper.setSearchablePatientName(setPatientName);
                        resultAppPreferencesHelper.setSearchablePatientMrNumber(setPatientMrnNumberVal);
                        resultAppPreferencesHelper.setSearchablePatientMobileNumber(setPatientPhoneNumberVal);
                        resultAppPreferencesHelper.setSearchablePatientDOB(setPatientDOBVal);
                        resultAppPreferencesHelper.setSearchablePatientSex(setPatientSexVal);
                        resultAppPreferencesHelper.setSearchablePatientEye(setPatientTestEyeVal);
                        resultAppPreferencesHelper.setSearchableTestType(setPatientTestStrategyVal);
                        resultAppPreferencesHelper.setSearchableTestPattern(setPatientTestPatternVal);
                        resultAppPreferencesHelper.setResultData(data);

                        String patternFinal = finalPerimetryTestFinalResultObject.Series.PatternSequence.CodeMeaning;
                        Log.d("Length", "Of Gson" + finalPerimetryTestFinalResultObject.Measurements.VisualFieldStaticPerimetryTestMeasurements.VisualFieldTestPointSequence.length);
                        int dt_new_result_lengthFinal = finalPerimetryTestFinalResultObject.Measurements.VisualFieldStaticPerimetryTestMeasurements.VisualFieldTestPointSequence.length;
                        resetArrays();
                        dia = "Pupil Diameter: " + finalPerimetryTestFinalResultObject.Measurements.OphthalmicPatientClinicalInformationAndTestLensParameters.OphthalmicPatientClinicalInformationRightEyeSequence.PupilSize;
                        backGround = "Background: " + finalPerimetryTestFinalResultObject.Measurements.VisualFieldStaticPerimeteryTestParameters.BackgroundIlluminationColorCodeSequence.CodeMeaning;
                        String cylindricalAxis = Double.toString(finalPerimetryTestFinalResultObject.Measurements.OphthalmicPatientClinicalInformationAndTestLensParameters.OphthalmicPatientClinicalInformationLeftEyeSequence.RefractiveParametersUsedOnPatientSequence.CylinderAxis);
                        String cylindricalPower = Double.toString(finalPerimetryTestFinalResultObject.Measurements.OphthalmicPatientClinicalInformationAndTestLensParameters.OphthalmicPatientClinicalInformationLeftEyeSequence.RefractiveParametersUsedOnPatientSequence.CylindricalLensPower);
                        String sphericalPower = Double.toString(finalPerimetryTestFinalResultObject.Measurements.OphthalmicPatientClinicalInformationAndTestLensParameters.OphthalmicPatientClinicalInformationLeftEyeSequence.RefractiveParametersUsedOnPatientSequence.SphericalLensPower);
                        visualAcuity = "RX: " + sphericalPower + " D/S " + cylindricalPower + " DC X " + cylindricalAxis;
                        fovea = finalPerimetryTestFinalResultObject.Measurements.VisualFieldStaticPerimetryTestMeasurements.FovealSensitivityMeasured;
                        String md = Double.toString(finalPerimetryTestFinalResultObject.Measurements.VisualFieldStaticPerimetryTestResults.ResultNormalSequence.GlobalDeviationFromNormal);
                        //String prob = " P < 0.5";
                        psd = Double.toString(finalPerimetryTestFinalResultObject.Measurements.VisualFieldStaticPerimetryTestResults.ResultNormalSequence.LocalizedDeviationFromNormal);
                        //psd = psd.format("%.2f");
                        meanDeviation = md;// + "<font color=#ff0000>" + prob + "</font>";
                        //duration = durationCalculation(finalPerimetryTestFinalResultObject.Series.PerformedProcedureStepStartTime, finalPerimetryTestFinalResultObject.Series.PerformedProcedureStepEndTime);

                        FL_Denominator = finalPerimetryTestFinalResultObject.Measurements.VisualFieldStaticPerimetryTestReliability.FixationSequence.FixationCheckedQuantity;
                        FL_Numerator = finalPerimetryTestFinalResultObject.Measurements.VisualFieldStaticPerimetryTestReliability.FixationSequence.PatientNotProperlyFixatedQuantity;

                        FP_Denominator = finalPerimetryTestFinalResultObject.Measurements.VisualFieldStaticPerimetryTestReliability.VisualFieldCatchTrialSequence.PositiveCatchTrialsQuantity;
                        FP_Numerator = finalPerimetryTestFinalResultObject.Measurements.VisualFieldStaticPerimetryTestReliability.VisualFieldCatchTrialSequence.FalsePositivesQuantity;

                        FN_Denominator = finalPerimetryTestFinalResultObject.Measurements.VisualFieldStaticPerimetryTestReliability.VisualFieldCatchTrialSequence.NegativeCatchTrialsQuantity;
                        FN_Numerator = finalPerimetryTestFinalResultObject.Measurements.VisualFieldStaticPerimetryTestReliability.VisualFieldCatchTrialSequence.FalseNegativesQuantity;

                        if (finalPerimetryTestFinalResultObject.TestResultsInfo.ResultNormalSequence.GlobalDeviationProbabilityNormalsFlag.equalsIgnoreCase("yes")) {
                            // ght = finalPerimetryTestFinalResultObject.TestResultsInfo.VisualFieldGlobalResultsIndexSequence[0].DataObservationSequence.TextValue;
                            ght = finalPerimetryTestFinalResultObject.TestResultsInfo.VisualFieldGlobalResultsIndexSequence[0].DataObservationSequence.TextValue;
                            MD_Probabiltiy = finalPerimetryTestFinalResultObject.TestResultsInfo.ResultNormalSequence.GlobalDeviationProbabilitySequence.GlobalDeviationProbability;
                            PD_Probabiltiy = finalPerimetryTestFinalResultObject.TestResultsInfo.ResultNormalSequence.LocalDeviationProbabilitySequence.LocalDeviationProbability;
                        }
                        CommonUtils.writeToBugFixLogFile("VFI " + finalPerimetryTestFinalResultObject.TestResultsInfo.VisualFieldGlobalResultsIndexSequence[1].DataObservationSequence.TextValue);
                        vfi = finalPerimetryTestFinalResultObject.TestResultsInfo.VisualFieldGlobalResultsIndexSequence[1].DataObservationSequence.TextValue;
                        for (int i = 0; i < dt_new_result_lengthFinal; i++) {
                            String xVal, yVal, xyVal;
                            int x = (int) finalPerimetryTestFinalResultObject.Measurements.VisualFieldStaticPerimetryTestMeasurements.VisualFieldTestPointSequence[i].VisualFieldTestPointXCoordinate;
                            int y = (int) finalPerimetryTestFinalResultObject.Measurements.VisualFieldStaticPerimetryTestMeasurements.VisualFieldTestPointSequence[i].VisualFieldTestPointYCoordinate;
                            int s = (int) finalPerimetryTestFinalResultObject.Measurements.VisualFieldStaticPerimetryTestMeasurements.VisualFieldTestPointSequence[i].SensitivityValue;
                            int deviation = -((int) finalPerimetryTestFinalResultObject.Measurements.VisualFieldStaticPerimetryTestMeasurements.VisualFieldTestPointSequence[i].VisualFieldTestPointNormalSequence.AgeCorrectedSensitivityDeviationValue);
                            int probabilityDeviationValue = (int) finalPerimetryTestFinalResultObject.Measurements.VisualFieldStaticPerimetryTestMeasurements.VisualFieldTestPointSequence[i].VisualFieldTestPointNormalSequence.AgeCorrectedSensitivityProbabilityDeviationValue;
                            int generalizedDefectCorrectedSensitivityDeviationValue = ((int) finalPerimetryTestFinalResultObject.Measurements.VisualFieldStaticPerimetryTestMeasurements.VisualFieldTestPointSequence[i].VisualFieldTestPointNormalSequence.GeneralizedDefectCorrectedSensitivityDeviationValue);
                            int generalizedDefectCorrectedSensitivityDeviationProbabilityValue = (int) finalPerimetryTestFinalResultObject.Measurements.VisualFieldStaticPerimetryTestMeasurements.VisualFieldTestPointSequence[i].VisualFieldTestPointNormalSequence.GeneralizedDefectCorrectedSensitivityDeviationProbabilityValue;
                            String seen = finalPerimetryTestFinalResultObject.Measurements.VisualFieldStaticPerimetryTestMeasurements.VisualFieldTestPointSequence[i].StimulusResults;
                            if (!(x == 0 & y == 0)) {
                                if (patternFinal.equals("24-2") || patternFinal.equals("30-2")) {
                                    xVal = x > 0 ? "x" + ((x + 3) / 6) : "-x" + ((-x + 3) / 6);
                                    yVal = y > 0 ? "y" + ((y + 3) / 6) : "-y" + ((-y + 3) / 6);
                                    xyVal = xVal.concat(yVal);
                                } else {
                                    //  xVal = x > 0 ? "x" + (x / 2) : "-x" + (-(x) / 2);
                                    //yVal = y > 0 ? "y" + (y / 2) : "-y" + (-(y) / 2);

                                    xVal = x > 0 ? "x" + ((x + 1) / 2) : "-x" + ((-x + 1) / 2);
                                    yVal = y > 0 ? "y" + ((y + 1) / 2) : "-y" + ((-y + 1) / 2);
                                    xyVal = xVal.concat(yVal);
                                }
                                dt_new_result.add(xyVal);
                                dt_result_sensitivity.add("" + s);
                                dt_result_deviation.add("" + deviation);
                                dt_result_seen.add(seen);
                                dt_result_probabilityDeviationValue.add("" + probabilityDeviationValue);
                                dt_result_generalizedDefectCorrectedSensitivityDeviationValue.add("" + generalizedDefectCorrectedSensitivityDeviationValue);
                                dt_result_generalizedDefectCorrectedSensitivityDeviationProbabilityValue.add("" + generalizedDefectCorrectedSensitivityDeviationProbabilityValue);
                            }
                        }
                        Interpolation interpolation = new Interpolation();
                        quadrants = interpolation.getGreyScaleVals(finalPerimetryTestFinalResultObject);
                        showToast = true;
                        dt_chronometerOn = false;
                        toastMessage = "Test is complete. Results will load shortly";
                        dt_testOver = true;
                        StoreTransmitter.updatedUIState("NEW_FINAL_RESULT");
                        final Handler handler = new Handler();

                        handler.postDelayed(() -> {
                            Log.d("POSTDELAYED", "Running");
                            if (setPatientTestStrategyVal.equals("Screening"))
                                activeView_number = R.layout.post_test_screening_result;
                            else if (setPatientTestPatternVal.equals("24-2") | setPatientTestPatternVal.equals("30-2") | setPatientTestPatternVal.equals("10-2"))
                                activeView_number = R.layout.decoy;
                            else
                                activeView_number = R.layout.post_test_doctors_copy_result;
                            StoreTransmitter.updatedUIState("NEW_FINAL_RESULT");
                        }, 5000);
                    } else {
                        Log.e("JSON", "JSON Not valid");
                        writeToFile(data, "invalidjson");
                        if (activeView_number != R.layout.activity_home_screen) {
                            activeView_number = R.layout.activity_home_screen;
                            StoreTransmitter.updatedUIState("NEW_FINAL_RESULT");
                        }
                    }
                    appPreferencesHelper.clearPreferences();
                    break;


                case "NEW_FINAL_RESULT_SAVE_ONLY":

                    Log.e("RESULT_SAVE_ONLY", "Received");
                    appPreferencesHelper.clearPreferences();
                    dt_timebase = SystemClock.elapsedRealtime();
                    Log.e("InFinal", " " + data);
                    if (isValidJson(data)) {
                        finalPerimetryTestFinalResultObject = gson.fromJson(data, PerimetryObject_V2.FinalPerimetryResultObject.class);
                        setPatientName = finalPerimetryTestFinalResultObject.Patient.PatientName;
                        setPatientMrnNumberVal = finalPerimetryTestFinalResultObject.Patient.PatientUID;
                        setPatientPhoneNumberVal = finalPerimetryTestFinalResultObject.Patient.PatientID;
                        setPatientDOBVal = finalPerimetryTestFinalResultObject.Patient.PatientBirthDate;
                        setPatientSexVal = finalPerimetryTestFinalResultObject.Patient.PatientSex.equalsIgnoreCase("M") ? "Male" : "Female";
                        setPatientTestEyeVal = finalPerimetryTestFinalResultObject.Series.Laterality.equals("L") ? "Left Eye" : "Right Eye";
                        setPatientTestStrategyVal = finalPerimetryTestFinalResultObject.Series.StrategySequence.CodeMeaning;
                        setPatientTestPatternVal = finalPerimetryTestFinalResultObject.Series.PatternSequence.CodeMeaning;
                        // String dte = "" + new Date(Math.abs(System.currentTimeMillis() - new Random().nextLong())).getTime();
                        String dte = "" + CommonUtils.returnRandom();

                        Log.e("InsertedDate", " " + dte);
                        new InsertRecordIntoDb(b -> {
                            insertedResultID = "" + b;
                            Log.e("InsertedID", "Store " + b);
                            return b;
                        }).execute(setPatientName, setPatientMrnNumberVal, setPatientPhoneNumberVal, setPatientDOBVal, setPatientSexVal, setPatientTestEyeVal, setPatientTestStrategyVal, setPatientTestPatternVal, "Suggestion", data, dte);
                    } else {
                        Log.e("isValidJson", "failed");
                        Random rand = new Random();
                        writeToFile(data, "newresult " + rand.nextInt());
                    }
                    break;
            }
        } catch (Exception e) {
            Log.e("NampuCommFromHMD", "JSON Unwrap Failed " + e.getMessage());
        }
    }

    /*private static void validateLensDetailsScreen(){
        if(communicationActive){
            if (TextUtils.isEmpty(patient.getText())) {
                showTip = true;
                tipId = R.id.editable_spherical_power;
                Bundle b = new Bundle();
                b.putInt("tipId", tipId);
                b.putString("toastMessage", "Please enter spherical power");
                showMeTip(b);
            } else {
                calculate.performClick();
                saveUI(root);
                Actions.beginTestProfile();
            }
        }
    }*/

    public static Bundle getState() {
        AppPreferencesHelper appPreferencesHelper = new AppPreferencesHelper(MainActivity.applicationContext, PREF_NAME);
        stateBundle.putInt("viewID", activeView_number);
        stateBundle.putBoolean("showToasty", showToast);
        stateBundle.putBoolean("showTip", showTip);
        stateBundle.putInt("tipId", tipId);
        if (showToast) {
            stateBundle.putString("toastMessage", toastMessage);
        }

        switch (activeView_number) {

            case R.layout.restore_database_finished:
                stateBundle.putInt("recordMismatched", recordMismatched);
                stateBundle.putInt("recordsInserted", recordsInserted);
                stateBundle.putInt("recordsDuplicated", recordsDuplicated);
                stateBundle.putInt("totalNumberOfRecords", totalNumberOfRecords);
                break;
            case R.layout.activity_pre_production:
                stateBundle.putString("calibData", calibData);
                break;
            case R.layout.hmd_update_tranfer_screen:
                stateBundle.putString("transferMessage", transferMessage);
                break;

            case R.layout.hmd_sync_check_activity:
                boolean boo = WifiCommunicationManager.isConnected();
                Log.e("getstate", "called " + boo);
                Context context = MyApplication.getInstance();
                boolean isHotspotOn = isHotspotOn(context);
                boolean isHmdConnected = WifiCommunicationManager.isConnected();
                Log.d("isHotspotOn", " " + isHotspotOn);
                Log.d("isHmdConnected", " " + isHmdConnected);
                stateBundle.putBoolean("isHmdConnected", isHmdConnected);
                stateBundle.putString("hotspotstatus", context.getResources().getString(isHotspotOn ? R.string.hotspot_initialized : R.string.initializing_hotspot));
                stateBundle.putString("hmdstatus", context.getResources().getString(isHmdConnected ? R.string.hmd_connected : R.string.waiting_for_hmd_connection));
                stateBundle.putString("syncstatus", context.getResources().getString(isHmdConnected && new AppPreferencesHelper(MyApplication.getInstance(), DEVICE_PREF).getFirstTimeUserConnection() ? R.string.synced : R.string.syncing));
                stateBundle.putBoolean("hmdVisibility", isHotspotOn);
                stateBundle.putBoolean("syncVisibility", isHotspotOn && isHmdConnected);

                // stateBundle.putString("sync", context.getResources().getString(isHotspotOn ? R.string.initialize_hotspot : R.string.hotspot_initialized));
                break;

            case R.layout.activity_primary_patient_details:
                stateBundle.putString("PatientFirstName", patientName);
                stateBundle.putString("PatientSex", patientSexVal);
                stateBundle.putString("PatientMobile", patientPhoneNumberVal);
                stateBundle.putString("PatientMrnNumber", patientMrnNumberVal);
                stateBundle.putString("PatientDOB", patientDOBVal);
                break;

            case R.layout.activity_primary_test_details:
                stateBundle.putString("PatientTestEye", patientTestEyeVal);
                stateBundle.putString("PatientTestPattern", patientTestPatternVal);
                stateBundle.putString("PatientTestStrategy", patientTestStrategyVal);
                break;

            case R.layout.activity_lens_power_settings:
                stateBundle.putString("PatientSphericalInput", testSphericalInput);
                stateBundle.putString("PatientCylindricalInput", testCylindricalInput);
                stateBundle.putString("PatientCylindricalAxisInput", testCylindricalAxisInput);
                stateBundle.putString("PatientSphericalValue", testSphericalVal);
                stateBundle.putString("PatientCylindricalValue", testCylindricalVal);
                stateBundle.putString("PatientCylindricalAxisValue", testCylindricalAxisVal);
                stateBundle.putString("PatientDOB", patientDOBVal);
                break;

            case R.layout.activity_test_profile:
                stateBundle.putString("SetPatientFirstName", setPatientName);
                stateBundle.putString("SetPatientSex", setPatientSexVal);
                stateBundle.putString("SetPatientMobile", setPatientPhoneNumberVal);
                stateBundle.putString("SetPatientMrnNumber", setPatientMrnNumberVal);
                stateBundle.putString("SetPatientDOB", setPatientDOBVal);
                stateBundle.putString("SetPatientTestEye", setPatientTestEyeVal);
                stateBundle.putString("SetPatientTestPattern", setPatientTestPatternVal);
                stateBundle.putString("SetPatientTestStrategy", setPatientTestStrategyVal);
                stateBundle.putString("SetPatientSphericalValue", setTestSphericalVal);
                stateBundle.putString("SetPatientCylindricalValue", setTestCylindricalVal);
                stateBundle.putString("SetPatientCylindricalAxisValue", setTestCylindricalAxis);
                break;

           // case R.layout.activity_ipd_settings:
               // stateBundle.putString("SetPatientTestEye", setPatientTestEyeVal);
               // if (setPatientTestStrategyVal.equals("Custom Zest") || setPatientTestStrategyVal.equals("Custom FT") || setPatientTestStrategyVal.equals("Custom Fast"))
                   // stateBundle.putString("ipd_button_status", IPD_button_Visiblity);
               // else
                   // stateBundle.putString("ipd_button_status", "Visible");
                //stateBundle.putString("ipd_button_status", "Visible");
              //  break;

            case R.layout.activity_during_test:

                stateBundle.putString("SetPatientFirstName", setPatientName);
                stateBundle.putString("SetPatientSex", setPatientSexVal);
                stateBundle.putString("SetPatientMobile", setPatientPhoneNumberVal);
                stateBundle.putString("SetPatientMrnNumber", setPatientMrnNumberVal);
                stateBundle.putString("SetPatientDOB", setPatientDOBVal);
                stateBundle.putString("SetPatientTestEye", setPatientTestEyeVal);
                stateBundle.putString("SetPatientTestPattern", setPatientTestPatternVal);
                stateBundle.putString("SetPatientTestStrategy", setPatientTestStrategyVal);
                stateBundle.putString("SetPatientSphericalValue", setTestSphericalVal);
                stateBundle.putString("SetPatientCylindricalValue", setTestCylindricalVal);
                stateBundle.putString("SetPatientCylindricalAxisValue", setTestCylindricalAxis);

                stateBundle.putBoolean("dt_chronometerOn", dt_chronometerOn);
                stateBundle.putLong("dt_timebase", dt_timebase);
                long currentReading = dt_timebase - SystemClock.elapsedRealtime();
                appPreferencesHelper.setTimeBase(currentReading);
                stateBundle.putBoolean("dt_testOver", dt_testOver);
                stateBundle.putStringArrayList("dt_new_result", dt_new_result);
                stateBundle.putStringArrayList("dt_result_deviation", dt_result_deviation);
                stateBundle.putStringArrayList("dt_result_sensitivity", dt_result_sensitivity);
                stateBundle.putStringArrayList("dt_result_seen", dt_result_seen);
                stateBundle.putString("FP", FP_Numerator + "/" + FP_Denominator);
                stateBundle.putString("FL", FL_Numerator + "/" + FL_Denominator);
                stateBundle.putString("FN", FN_Numerator + "/" + FN_Denominator);
                stateBundle.putSerializable("quadrants", quadrants);
                break;

            case R.layout.post_test_doctors_copy_result:
                stateBundle.putString("setPatientName", setPatientName);
                stateBundle.putString("setPatientSex", setPatientSexVal);
                stateBundle.putString("SetPatientMobile", setPatientPhoneNumberVal);
                stateBundle.putString("setPatientMrnNumber", setPatientMrnNumberVal);
                stateBundle.putString("setPatientDOB", setPatientDOBVal);
                stateBundle.putString("setPatientTestEye", setPatientTestEyeVal);
                stateBundle.putString("setPatientTestPattern", setPatientTestPatternVal);
                Log.d("setPatientTestStrategy", " " + setPatientTestStrategyVal);
                stateBundle.putString("setPatientTestStrategy", setPatientTestStrategyVal);
                stateBundle.putBoolean("dt_chronometerOn", dt_chronometerOn);
                stateBundle.putLong("dt_timebase", dt_timebase);
                stateBundle.putString("meanDeviation", meanDeviation);
                stateBundle.putString("PupilSize", dia);
                stateBundle.putString("psd", psd);
                stateBundle.putString("BackgroundIlluminationColorCodeSequence", backGround);
                stateBundle.putString("visualAcuity", visualAcuity);
                long finalReadings = dt_timebase - SystemClock.elapsedRealtime();
                appPreferencesHelper.setTimeBase(finalReadings);
                stateBundle.putBoolean("dt_testOver", dt_testOver);
                stateBundle.putString("TestDuration", duration);
                stateBundle.putStringArrayList("dt_new_result", dt_new_result);
                stateBundle.putStringArrayList("dt_result_deviation", dt_result_deviation);
                stateBundle.putStringArrayList("dt_result_sensitivity", dt_result_sensitivity);
                stateBundle.putStringArrayList("dt_result_seen", dt_result_seen);
                stateBundle.putStringArrayList("dt_result_probabilityDeviationValue", dt_result_probabilityDeviationValue);
                stateBundle.putStringArrayList("dt_result_generalizedDefectCorrectedSensitivityDeviationValue", dt_result_generalizedDefectCorrectedSensitivityDeviationValue);
                stateBundle.putStringArrayList("dt_result_generalizedDefectCorrectedSensitivityDeviationProbabilityValue", dt_result_generalizedDefectCorrectedSensitivityDeviationProbabilityValue);
                stateBundle.putString("FP", FP_Numerator + "/" + FP_Denominator);
                stateBundle.putString("FL", FL_Numerator + "/" + FL_Denominator);
                stateBundle.putString("FN", FN_Numerator + "/" + FN_Denominator);
                double foveaValue = finalPerimetryTestFinalResultObject.Measurements.VisualFieldStaticPerimetryTestMeasurements.FovealSensitivity;
                String foveaText = fovea.equalsIgnoreCase("yes") ? "Fovea: " + foveaValue : "Fovea: Off";
                stateBundle.putString("fovea", foveaText);
                stateBundle.putInt("FL_Numerator", FL_Numerator);
                stateBundle.putInt("FL_Denominator", FL_Denominator);
                stateBundle.putInt("FP_Numerator", FP_Numerator);
                stateBundle.putInt("FP_Denominator", FP_Denominator);
                stateBundle.putInt("FN_Numerator", FN_Numerator);
                stateBundle.putInt("FN_Denominator", FN_Denominator);
                stateBundle.putString("GHT", ght);
                stateBundle.putString("VFI", vfi);
                stateBundle.putDouble("PDProbability", PD_Probabiltiy);
                stateBundle.putDouble("MDProbability", MD_Probabiltiy);
                stateBundle.putSerializable("quadrants", quadrants);
                break;

            case R.layout.post_test_screening_result:
                stateBundle.putString("setPatientName", setPatientName);
                stateBundle.putString("setPatientSex", setPatientSexVal);
                stateBundle.putString("SetPatientMobile", setPatientPhoneNumberVal);
                stateBundle.putString("setPatientMrnNumber", setPatientMrnNumberVal);
                stateBundle.putString("setPatientDOB", setPatientDOBVal);
                stateBundle.putString("setPatientTestEye", setPatientTestEyeVal);
                stateBundle.putString("setPatientTestPattern", setPatientTestPatternVal);
                stateBundle.putString("CreatedDate", insertedDateTime);
                Log.d("setPatientTestStrategy", " " + setPatientTestStrategyVal);
                stateBundle.putString("setPatientTestStrategy", setPatientTestStrategyVal);
                stateBundle.putBoolean("dt_chronometerOn", dt_chronometerOn);
                stateBundle.putLong("dt_timebase", dt_timebase);
                long finalReading = dt_timebase - SystemClock.elapsedRealtime();
                appPreferencesHelper.setTimeBase(finalReading);
                stateBundle.putBoolean("dt_testOver", dt_testOver);
                stateBundle.putStringArrayList("dt_new_result", dt_new_result);
                stateBundle.putStringArrayList("dt_result_deviation", dt_result_deviation);
                stateBundle.putStringArrayList("dt_result_sensitivity", dt_result_sensitivity);
                stateBundle.putStringArrayList("dt_result_seen", dt_result_seen);
                stateBundle.putStringArrayList("dt_result_probabilityDeviationValue", dt_result_probabilityDeviationValue);
                stateBundle.putStringArrayList("dt_result_generalizedDefectCorrectedSensitivityDeviationValue", dt_result_generalizedDefectCorrectedSensitivityDeviationValue);
                stateBundle.putStringArrayList("dt_result_generalizedDefectCorrectedSensitivityDeviationProbabilityValue", dt_result_generalizedDefectCorrectedSensitivityDeviationProbabilityValue);
                stateBundle.putString("FP", FP_Numerator + "/" + FP_Denominator);
                stateBundle.putString("FL", FL_Numerator + "/" + FL_Denominator);
                stateBundle.putString("FN", FN_Numerator + "/" + FN_Denominator);
                stateBundle.putInt("FL_Numerator", FL_Numerator);
                stateBundle.putInt("FL_Denominator", FL_Denominator);
                stateBundle.putInt("FP_Numerator", FP_Numerator);
                stateBundle.putInt("FP_Denominator", FP_Denominator);
                stateBundle.putInt("FN_Numerator", FN_Numerator);
                stateBundle.putInt("FN_Denominator", FN_Denominator);
                stateBundle.putString("GHT", ght);
                stateBundle.putString("VFI", vfi);
                stateBundle.putDouble("PDProbability", PD_Probabiltiy);
                stateBundle.putDouble("MDProbability", MD_Probabiltiy);
                stateBundle.putSerializable("quadrants", quadrants);
                break;

            case R.layout.decoy:
                //stateBundle.putString("insertedResultID",insertedResultID);
                CommonUtils.writeToTestLogFile("\nduration decoy " + duration);
                stateBundle.putBundle("finalPerimetryTestFinalResultObject", CommonUtils.createReportBundle(insertedResultID, insertedDateTime, finalPerimetryTestFinalResultObject, duration));
                break;
        }

        return stateBundle;
    }

    private static void resetArrays() {
        dt_new_result.clear();
        dt_result_sensitivity.clear();
        dt_result_seen.clear();
        dt_result_deviation.clear();
        dt_result_probabilityDeviationValue.clear();
        dt_result_generalizedDefectCorrectedSensitivityDeviationValue.clear();
        dt_result_generalizedDefectCorrectedSensitivityDeviationProbabilityValue.clear();
    }

    private static void resetFixations() {
        FP_Numerator = 0;
        FP_Denominator = 0;
        FL_Numerator = 0;
        FL_Denominator = 0;
        FN_Numerator = 0;
        FN_Denominator = 0;
    }

    public static void setValues() {

        AppPreferencesHelper appPreferencesHelper = new AppPreferencesHelper(MainActivity.applicationContext, PREF_NAME);
        patientName = appPreferencesHelper.getPatientFirstName();
        patientPhoneNumberVal = appPreferencesHelper.getPatientMobileNumber();
        patientMrnNumberVal = appPreferencesHelper.getPatientMrnNumber();
        patientDOBVal = appPreferencesHelper.getPatientDOB();
        patientSexVal = appPreferencesHelper.getPatientSex();
        patientTestEyeVal = appPreferencesHelper.getPatientTestEye();
        patientTestPatternVal = appPreferencesHelper.getTestPattern();
        patientTestStrategyVal = appPreferencesHelper.getTestStrategy();

    }

    private static void updateBehaviour() {

        switch (RuntimeMode.modeNo) {
            case 0:
                switch (RuntimeMode.modeTypeNo) {
                    case 1000:
                        //Patient Details Input
                        break;
                    case 1001:
                        // Test Details Input
                        break;
                    case 1002:
                        //Lens Power Settings
                        RuntimeMode.modeNo++;
                }
                break;
            case 1:
                //Test Profile
                RuntimeMode.modeNo++;
                break;
            case 2:
                //IPD Settings
                RuntimeMode.modeNo++;
                break;
            case 3:
                //Calibration
                RuntimeMode.modeNo++;
                break;
            case 4:
                //During Test
                RuntimeMode.modeNo++;
                break;
            case 5:
                //Post Test
                //RuntimeMode.modeNo = 0;
                resetModes();
                break;
        }

    }

    private static void resetModes() {
        RuntimeMode.modeNo = 0;
        RuntimeMode.modeTypeNo = 1000;
        RuntimeMode.modeStepNo = 0;
    }

    private static void setClickData(String ButtonName, String ButtonData) {
        buttonClickData.buttonName = ButtonName;
        buttonClickData.buttonData = ButtonData;
        String sendData = gson.toJson(buttonClickData);
        clickData.putInt("hashcode", sendData.hashCode());
        clickData.putString("data", sendData);
        clickData.putString("onPass", ButtonName + "Pass");
        clickData.putString("onFail", ButtonName + "Fail");
    }

    public static void showBigToast(Toast toast) {
        ViewGroup group = (ViewGroup) toast.getView();
        TextView messageTextView = (TextView) group.getChildAt(0);
        messageTextView.setTextSize(20);
        toast.show();
    }

    private static void updateInstalledHMDVersion(String versionName, int versionCode, String modelName) {
        AppPreferencesHelper devicePreferencesHelper = new AppPreferencesHelper(MyApplication.getInstance(), DEVICE_PREF);
        if (versionName != null && versionCode > 0) {
            Log.d("HMD_VERSION", " versionName" + versionName + " versionCode " + versionCode);
            devicePreferencesHelper.setInstalledHMDVersionCode(versionCode);
            devicePreferencesHelper.setInstalledHMDVersionName(versionName);
            devicePreferencesHelper.setHMDModelName(modelName);
        }
    }

    private static void updateHMDCameraABDetails(float rightAlpha, float rightBeta, float leftAlpha, float leftBeta) {
        AppPreferencesHelper devicePreferencesHelper = new AppPreferencesHelper(MyApplication.getInstance(), DEVICE_PREF);
        devicePreferencesHelper.setRightCameraAlpha(rightAlpha);
        devicePreferencesHelper.setRightCameraBeta(rightBeta);
        devicePreferencesHelper.setLeftCameraAlpha(leftAlpha);
        devicePreferencesHelper.setLeftCameraBeta(leftBeta);
    }

    private static void showFoveaDialog(Context context, String value) {
        /*new MaterialDialog.Builder(activity)
                .title("")
                .content("Fovea value is " + value)
                .positiveText("Proceed")
                .negativeText("Redo")
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        foveaFeedback(true);
                    }
                })
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        foveaFeedback(false);
                    }
                })
                .show();*/

        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.myDialog));
        builder.setTitle("");
        builder.setMessage("Fovea value is " + value)
                .setCancelable(false)
                .setPositiveButton("Proceed", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        foveaFeedback(true);
                    }
                })
                .setNegativeButton("Redo", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        foveaFeedback(false);
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private static void closeTheTestController(Context context) {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(new ContextThemeWrapper(context, R.style.myDialog));
        builder.setTitle("System Alert")
                .setMessage("HMD software updated. Please close the Test Controller and proceed again.")
                .setCancelable(false)
                .setPositiveButton("Close", (dialog, which) -> {
                    Actions.closeTheTC();
                });

        androidx.appcompat.app.AlertDialog alert = builder.create();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alert.show();
    }

    private static void showBatteryStatus(Context context, String hmdBatteryLevel, int tcBatteryLevel) {
        if (alert != null && alert.isShowing()) {
            alert.dismiss();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.myDialog));
        builder.setTitle("Battery Status");
        builder.setIcon(R.drawable.battery_level_dialog_icon);
        builder.setMessage("\nHMD Battery Level " + hmdBatteryLevel + "% and TC Battery Level " + tcBatteryLevel + "%")
                .setCancelable(true)
                .setPositiveButton("Okay", (dialog, id) -> {
                });
        alert = builder.create();
        alert.show();
    }

    private static void showHMDIdleTurnOffWarning(Context context) {
        Log.e("ILDE", "showHMDIdleTurnOffWarning called");
        if (alert != null && alert.isShowing()) {
            alert.dismiss();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.myDialog));
        builder.setTitle("Warning");
        builder.setIcon(R.drawable.ic_warning);
        builder.setMessage("HMD is idle is for too long and will be turned off in a minute")
                .setCancelable(false)
                .setPositiveButton("Turn Off Now", (dialog, id) -> {
                    Actions.setIdleShutDownOkay();
                })
                .setNegativeButton("Skip For Now", ((dialog, id) -> {
                    Actions.setIdleShutDownLater();
                }));
        alert = builder.create();
        alert.show();
    }

    private static void hmdBatteryBelow20(Context context) {
        if (alert != null && alert.isShowing()) {
            alert.dismiss();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.myDialog));
        builder.setTitle("Warning Battery Low");
        builder.setIcon(R.drawable.ic_warning);
        builder.setMessage("HMD Battery is below 20%")
                .setCancelable(true)
                .setPositiveButton("Okay", (dialog, id) -> {
                });
        alert = builder.create();
        alert.show();
    }

    private static void hmdTurnedOffDueToIdleness(Context context) {
        if (alert != null && alert.isShowing()) {
            alert.dismiss();
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.myDialog));
        builder.setTitle("Alert");
        builder.setIcon(R.drawable.ic_warning);
        builder.setMessage("HMD is turned off for being idle for too long")
                .setCancelable(false)
                .setPositiveButton("Okay", (dialog, id) -> {
                    Actions.showLogOutDecoy();
                });
        alert = builder.create();
        alert.show();
    }

    private static void disableNewTest() {
        newTestVisibility = false;
        activeView_number = R.layout.activity_home_screen;
        //MainActivity.menu.getItem(2).setIcon(ContextCompat.getDrawable(applicationContext, R.drawable.ic_vr_headset_off));
        StoreTransmitter.updatedUIState("disableNewTest");
    }

    private static void enableNewTest() {
        activeView_number = R.layout.activity_home_screen;
        //MainActivity.menu.getItem(2).setIcon(ContextCompat.getDrawable(applicationContext, R.drawable.ic_vr_headset));
        StoreTransmitter.updatedUIState("enableNewTest");
    }

    private static void ensureIpdScreen() {
        MainActivity.ipdImageViewLinked = true;
        activeView_number = R.layout.activity_ipd_settings;
        StoreTransmitter.updatedUIState("ensureIpdScreen");
    }

    private static void ensureCalibrationScreen() {
        MainActivity.calibrationImageViewLinked = true;
        activeView_number = R.layout.activity_calibiration;
        StoreTransmitter.updatedUIState("ensureCalibrationScreen");
    }

    private static void ensureDuringTestScreen() {
        MainActivity.testImageViewLinked = true;
        activeView_number = R.layout.activity_during_test;
        StoreTransmitter.updatedUIState("ensureDuringTestScreen");
    }

    public static void modeSync(boolean onCL, boolean onRC, boolean onBP) {

        Context context = MainActivity.applicationContext;
        int hmdMode = Integer.parseInt(CommonUtils.splitHmdHeartBeat("ModeNo"));
        String modeTypeNo = CommonUtils.splitHmdHeartBeat("ModeTypeNo");
        Log.e("modeSync", "HMD MODE " + hmdMode + " ModeTypeNo " + modeTypeNo);
        switch (activeView_number) {

            case R.layout.activity_home_screen:
                switch (hmdMode) {
                    case 0:
                        if (onCL)
                            disableNewTest();
                        else if (onRC)
                            enableNewTest();
                        else if (onBP)
                            Log.d("From home", "do not go back");
                        break;
                    case 1:
                        disableNewTest();
                        Actions.stopEI();
                        break;
                    case 2:
                        disableNewTest();
                        abortTest();
                        break;
                    case 3:
                        disableNewTest();
                        abortTest();
                        break;
                    case 4:
                        ensureDuringTestScreen();
                        break;
                    case 5:
                        disableNewTest();
                        break;
                }
                break;
            case R.layout.checking_interfaces_screen:
                switch (hmdMode) {
                    case 0:
                        if (onCL)
                            disableNewTest();
                        else if (onBP) { //check if a connection is there
                            if (communicationActive)
                                Actions.stopEI();
                            else
                                disableNewTest();
                        }
                        break;
                    case 1:
                        Actions.stopEI();
                        break;
                    case 2:
                        ensureIpdScreen();
                        break;
                    case 3:
                        ensureCalibrationScreen();
                        break;
                    case 4:
                        ensureDuringTestScreen();
                        break;
                    case 5:
                        Actions.setActionCheckForPreviousResults();
                        break;
                }
                break;
            case R.layout.warning_bno_charging:
                switch (hmdMode) {
                    case 0:
                        if (onCL)
                            disableNewTest();
                        else if (onBP) {
                            Actions.stopEI();
                        }
                        break;
                    case 1:
                        Actions.stopEI();
                        break;
                    case 2:
                        ensureIpdScreen();
                        break;
                    case 3:
                        ensureCalibrationScreen();
                        break;
                    case 4:
                        ensureCalibrationScreen();
                        break;
                    case 5:
                        Actions.setActionCheckForPreviousResults();
                        break;
                }
                break;
            case R.layout.warning_bno_not_charging:
                switch (hmdMode) {
                    case 0:
                        if (onCL) {
                            showBigToast(Toast.makeText(context, "Connection Lost", Toast.LENGTH_SHORT));
                            disableNewTest();
                        } else if (onBP) {
                            enableNewTest();
                        }
                        break;
                    case 1:
                        Actions.stopEI();
                        break;
                    case 2:
                        ensureIpdScreen();
                        break;
                    case 3:
                        ensureCalibrationScreen();
                        break;
                    case 4:
                        ensureDuringTestScreen();
                        break;
                    case 5:
                        Actions.setActionCheckForPreviousResults();
                        break;
                }
                break;
            case R.layout.warning_bo_charging:
                switch (hmdMode) {
                    case 0:
                        if (onCL)
                            disableNewTest();
                        else if (onBP)
                            enableNewTest();
                        break;
                    case 1:
                        disableNewTest();
                        Actions.stopEI();
                        break;
                    case 2:
                        ensureIpdScreen();
                        break;
                    case 3:
                        ensureCalibrationScreen();
                        break;
                    case 4:
                        ensureDuringTestScreen();
                        break;
                    case 5:
                        Actions.setActionCheckForPreviousResults();
                        break;
                }
                break;
            case R.layout.error_component_failure:
                switch (hmdMode) {
                    case 0:
                        if (onBP) {
                            disableNewTest();
                        }
                        break;
                    case 1:
                        disableNewTest();
                        Actions.stopEI();
                        break;
                    case 2:
                        ensureIpdScreen();
                        break;
                    case 3:
                        ensureCalibrationScreen();
                        break;
                    case 4:
                        ensureDuringTestScreen();
                        break;
                    case 5:
                        Actions.setActionCheckForPreviousResults();
                        break;
                }
                break;
            case R.layout.activity_primary_patient_details:
                switch (hmdMode) {
                    case 0:
                        if (!modeTypeNo.equals("1002"))
                            Actions.silentlyStartEI();
                        if (onBP)
                            Actions.stopEI();
                        break;
                    case 1:
                        if (onBP) {
                            Actions.stopEI();
                        } else if (onCL)
                            Log.e(TAG, "HMD tries to re-connect. No change in state or behavior as there is no imminent state change expected.");
                        else if (onRC)
                            Log.e(TAG, "No change");
                        break;
                    case 2:
                        ensureIpdScreen();
                        break;
                    case 3:
                        ensureCalibrationScreen();
                        break;
                    case 4:
                        ensureDuringTestScreen();
                        break;
                    case 5:
                        Actions.setActionCheckForPreviousResults();
                        break;
                }
                break;
            case R.layout.activity_primary_test_details:
                switch (hmdMode) {
                    case 0:
                        if (!modeTypeNo.equals("1002"))
                            Actions.silentlyStartEI();
                        if (onBP)
                            Actions.backToPrimaryPatientDetailsScreen();
                        break;
                    case 1:
                        if (onBP)
                            Actions.backToPrimaryPatientDetailsScreen();
                        else if (onCL)
                            Log.e(TAG, "HMD tries to re-connect. No change in state or behavior as there is no imminent state change expected.");
                        else if (onRC)
                            Log.e(TAG, "No change");
                        break;
                    case 2:
                        ensureIpdScreen();
                        break;
                    case 3:
                        ensureCalibrationScreen();
                        break;
                    case 4:
                        ensureDuringTestScreen();
                        break;
                    case 5:
                        Actions.setActionCheckForPreviousResults();
                        break;
                }
                break;
            case R.layout.activity_lens_power_settings:
                switch (hmdMode) {
                    case 0:
                        if (!modeTypeNo.equals("1002"))
                            Actions.silentlyStartEI();
                        if (onBP)
                            Actions.backToPrimaryTestDetailsScreen();
                        break;
                    case 1:
                        if (onBP)
                            Actions.backToPrimaryTestDetailsScreen();
                        else if (onCL)
                            Log.e(TAG, "HMD tries to re-connect. No change in state or behavior as there is no imminent state change expected.");
                        else if (onRC)
                            Log.e(TAG, "No change");
                        break;
                    case 2:
                        ensureIpdScreen();
                        break;
                    case 3:
                        ensureCalibrationScreen();
                        break;
                    case 4:
                        ensureDuringTestScreen();
                        break;
                    case 5:
                        Actions.setActionCheckForPreviousResults();
                        break;
                }
                break;
            case R.layout.activity_waitscreen_test_profile:
                switch (hmdMode) {
                    case 0:
                        if (!modeTypeNo.equals("1002"))
                            Actions.silentlyStartEI();
                        if (onBP)
                            Actions.backToLensPowerSettings();
                        break;
                    case 1:
                        if (onCL) {
                            showBigToast(Toast.makeText(applicationContext, "Connection Lost", Toast.LENGTH_SHORT));
                            Actions.backToLensPowerSettings();
                            Log.e(TAG, "HMD tries to re-connect. No change in state or behavior as there is no imminent state change expected.");
                        } else if (onBP) {
                            Actions.backToLensPowerSettings();
                        } else if (onRC)
                            Log.e(TAG, "No change");
                        break;
                    case 2:
                        ensureIpdScreen();
                        break;
                    case 3:
                        ensureCalibrationScreen();
                        break;
                    case 4:
                        ensureDuringTestScreen();
                        break;
                    case 5:
                        Actions.setActionCheckForPreviousResults();
                        break;
                }
                break;
            case R.layout.activity_test_profile:
                switch (hmdMode) {
                    case 0:
                        //Trying silent update to mode 1 by sending new test message from TC
                        showBigToast(Toast.makeText(applicationContext, "Initiating HMD from Mode 0 to 1", Toast.LENGTH_SHORT));
                        Actions.silentlyUpdateMode("1");
                        break;
                    case 1:
                        if (onCL) {
                            activeView_number = R.layout.error_hmd_disconnected;
                            StoreTransmitter.updatedUIState("activity_test_profile");
                        } else if (onBP) {
                            Actions.backToLensPowerSettings();
                        }
                        break;
                    case 2:
                        ensureIpdScreen();
                        break;
                    case 3:
                        ensureCalibrationScreen();
                        break;
                    case 4:
                        ensureDuringTestScreen();
                        break;
                    case 5:
                        Actions.setActionCheckForPreviousResults();
                        break;
                }
                break;
            case R.layout.activity_waitscreen_ipd_live_feed:
                if (onBP) {
                    abortTestConfirmationDialog(context);
                } else {
                    switch (hmdMode) {
                        case 0:
                            Actions.beginTestProfile();
                            break;
                        case 1:
                            if (onCL) {
                                Actions.beginLensSettings();
                                Actions.stopEI();
                            }
                            break;
                        case 2:
                            ensureIpdScreen();
                            break;
                        case 3:
                            ensureCalibrationScreen();
                            break;
                        case 4:
                            ensureDuringTestScreen();
                            break;
                        case 5:
                            Actions.setActionCheckForPreviousResults();
                            break;
                    }
                }
                break;
            case R.layout.activity_ipd_settings:
                switch (hmdMode) {
                    case 0:
                        Actions.goHomeImmediately();
                        showBigToast(Toast.makeText(context, "Mode Mismatch Going Home", Toast.LENGTH_SHORT));
                        break;
                    case 1:
                        Actions.beginLensSettings();
                        break;
                    case 2:
                        if (onCL) {
                            showBigToast(Toast.makeText(context, "HMD Disconnected Going Home", Toast.LENGTH_SHORT));
                            disableNewTest();
                        } else if (onBP)
                            abortTestConfirmationDialog(context);
                        break;
                    case 3:
                        ensureCalibrationScreen();
                        break;
                    case 4:
                        ensureDuringTestScreen();
                        break;
                    case 5:
                        Actions.setActionCheckForPreviousResults();
                        break;
                }
                break;
            case R.layout.activity_waitscreen_calibration_live_feed:
                if (onBP) {
                    abortTestConfirmationDialog(applicationContext);
                } else {
                    switch (hmdMode) {
                        case 0:
                            Actions.goHomeImmediately();
                            showBigToast(Toast.makeText(applicationContext, "Mode Mismatch", Toast.LENGTH_SHORT));
                            break;
                        case 1:
                            Actions.beginLensSettings();
                            break;
                        case 2:
                            //Actions.startTest();
                            break;
                        case 3:
                            ensureCalibrationScreen();
                            break;
                        case 4:
                            ensureDuringTestScreen();
                            break;
                        case 5:
                            Actions.setActionCheckForPreviousResults();
                            break;
                    }
                }
                break;
            case R.layout.activity_calibiration:
                switch (hmdMode) {
                    case 0:
                        enableNewTest();
                        showBigToast(Toast.makeText(applicationContext, "Mode Mismatch", Toast.LENGTH_SHORT));
                        break;
                    case 1:
                        Actions.beginLensSettings();
                        break;
                    case 2:
                        ensureIpdScreen();
                        break;
                    case 3:
                        if (onCL) {
                            MainActivity.calibrationImageViewLinked = true;
                            StoreTransmitter.updatedUIState("activity_calibiration");
                        } else if (onBP && !communicationActive) {
                            showBigToast(Toast.makeText(applicationContext, "HMD Disconnnected", Toast.LENGTH_SHORT));
                        } else if (onBP) {
                            abortTestConfirmationDialog(context);
                        } else if (onRC) {
                            ensureCalibrationScreen();
                        }
                        break;
                    case 4:
                        ensureDuringTestScreen();
                        break;
                    case 5:
                        Actions.setActionCheckForPreviousResults();
                        break;
                }
                break;
            case R.layout.activity_waitscreen_during_test:
                if (onBP) {
                    abortTestConfirmationDialog(context);
                } else {
                    switch (hmdMode) {
                        case 0:
                            enableNewTest();
                            showBigToast(Toast.makeText(applicationContext, "Mode Mismatch", Toast.LENGTH_SHORT));
                            break;
                        case 1:
                            Actions.beginLensSettings();
                            break;
                        case 2:
                            ensureIpdScreen();
                            break;
                        case 3:
                            if (onCL) {
                                ensureCalibrationScreen();
                            }
                            break;
                        case 4:
                            ensureDuringTestScreen();
                            break;
                        case 5:
                            Actions.setActionCheckForPreviousResults();
                            break;
                    }
                }
                break;
            case R.layout.activity_during_test:
                switch (hmdMode) {
                    case 0:
                        enableNewTest();
                        showBigToast(Toast.makeText(applicationContext, "Mode Mismatch", Toast.LENGTH_SHORT));
                        break;
                    case 1:
                        Actions.beginLensSettings();
                        break;
                    case 2:
                        ensureIpdScreen();
                        break;
                    case 3:
                        ensureCalibrationScreen();
                        break;
                    case 4:
                        if (onRC) {
                            activeView_number = R.layout.activity_during_test;
                            StoreTransmitter.updatedUIState("activity_during_test");
                        } else if (onBP && !communicationActive) {
                            showBigToast(Toast.makeText(applicationContext, "Connection Loss Please wait", Toast.LENGTH_SHORT));
                        } else if (onBP) {
                            abortTestConfirmationDialog(context);
                        }
                        break;
                    case 5:
                        if (onBP && !communicationActive) {
                            showBigToast(Toast.makeText(applicationContext, "Connection Loss Please wait", Toast.LENGTH_SHORT));
                        } else if (onBP && communicationActive) {
                        }
                        break;
                }
                break;
            case R.layout.decoy:
                switch (hmdMode) {
                    case 0:
                        break;
                    case 1:
                        Actions.beginLensSettings();
                        break;
                    case 2:
                        ensureIpdScreen();
                        break;
                    case 3:
                        ensureDuringTestScreen();
                        break;
                    case 4:
                        if (onRC) {
                            activeView_number = R.layout.activity_during_test;
                            StoreTransmitter.updatedUIState("decoy");
                        } else if (onBP && !communicationActive) {
                            showBigToast(Toast.makeText(applicationContext, "Connection Loss Please wait", Toast.LENGTH_SHORT));
                        } else if (onBP && communicationActive) {
                        }
                        break;
                    case 5:

                        break;
                }
                break;

        }
    }


    private static void reInitializeStatuses() {
        eyeTrackingStatus = EyeTrackingStatus.UNDEFINED;
        clickerStatus = ClickerStatus.UNDEFINED;
        displayStatus = DisplayStatus.UNDEFINED;
    }

    public static boolean isAllLastFiveDisplayStatusAreOkay() {
        int i = 0;
        for (String s : lastFiveDisplayStatus) {
            Log.e("LastFiveDisplayStatus", "Values " + i + " at " + s);
            i++;
        }
        Log.e("LastFiveDisplayStatus", "isFull " + lastFiveDisplayStatus.isFull());
        Log.e("LastFiveDisplayStatus", "isAtFullCapacity " + lastFiveDisplayStatus.isAtFullCapacity());
        Log.e("LastFiveDisplayStatus", "contains " + lastFiveDisplayStatus.contains("DISPLAY_OK"));
        if (lastFiveDisplayStatus.isAtFullCapacity())
            return lastFiveDisplayStatus.contains("DISPLAY_OK");
        else
            return true;
    }

    //test information object which is set on entering patient information
    public static class testInformation {
        String PatName;
        String PatFirstName;
        String PatLastName;
        String PatMRNNumber;
        String PatDOB;
        String PatSex;
        String PatMobile;
        String TestType;
        String TestPattern;
        String TestEye;
        String TestStrategy;
        String SphericalValue;
        String CylindricalValue;
        String CylindricalAxisValue;


        testInformation() {
            PatName = null;
            PatDOB = null;
            PatSex = null;
            TestType = null;
            TestEye = null;
            TestStrategy = null;
            TestPattern = null;
            PatFirstName = null;
            PatLastName = null;
            PatMRNNumber = null;
            PatMobile = null;
            SphericalValue = null;
            CylindricalValue = null;
            CylindricalAxisValue = null;

        }
    }

    //object that sends button strokes
    public static class TCButtonData {
        String buttonName;
        String buttonData;

        TCButtonData() {
            buttonName = null;
            buttonData = null;
        }
    }

    public static class RuntimeMode {

        static int modeNo = -1;
        static int modeTypeNo = 1000;
        static int modeStepNo = 0;

    }


}


