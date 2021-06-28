package com.agyohora.mobileperitc.actions;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.agyohora.mobileperitc.myapplication.MyApplication;
import com.agyohora.mobileperitc.ui.MainActivity;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 */
public class Actions extends IntentService {
    //IntentService always dispatches to the following location only
    public static final String DISPATCH_TOSTORE = "com.agyohora.mobileperitc.actions.actions.DispatchToStore";
    //IntentService always dispatches a payload
    public static final String DISPATCH_PAYLOAD = "com.agyohora.mobileperitc.actions.actions.DispatchPayload";
    // IntentService can perform the following actions.
    public static final String ACTION_INITIALIZE_COMMUNICATION = "com.agyohora.mobileperitc.actions.actions.name.initialize_communication";
    public static final String ACTION_COMMUNICATION_CONTINUE = "com.agyohora.mobileperitc.actions.actions.name.communication_Continue";
    public static final String ACTION_BACK_TO_HOME_SCREEN = "com.agyohora.mobileperitc.actions.actions.name.BackToInitScreen";
    public static final String ACTION_GO_HOME_IMMEDIATELY = "com.agyohora.mobileperitc.actions.actions.name.GoHomeImmediately";
    public static final String ACTION_ABORT_CALIB_AND_GO_HOME_IMMEDIATELY = "com.agyohora.mobileperitc.actions.actions.name.abort_calib_GoHomeImmediately";
    public static final String ACTION_HMD_DETAILS = "com.agyohora.mobileperitc.actions.actions.name.HmdDetails";
    public static final String ACTION_BACK_TO_SET_TEST = "com.agyohora.mobileperitc.actions.actions.name.backToSetTest";
    public static final String ACTION_ABORT = "com.agyohora.mobileperitc.actions.actions.name.abort";
    public static final String ACTION_IDLE_SHUTDOWN_LATER = "com.agyohora.mobileperitc.actions.actions.name.ildeShutdownLater";
    public static final String ACTION_IDLE_SHUTDOWN_OKAY = "com.agyohora.mobileperitc.actions.actions.name.ildeShutdownOkay";
    public static final String ACTION_ABORT_CALIBRATION = "com.agyohora.mobileperitc.actions.actions.name.abortcalib";
    public static final String ACTION_BEGIN_TEST = "com.agyohora.mobileperitc.actions.actions.name.beginTest";
    public static final String ACTION_BEGIN_PRODUCTION_SYNC = "com.agyohora.mobileperitc.actions.actions.name.begin_production_sync";
    public static final String ACTION_BEGIN_USER_SYNC = "com.agyohora.mobileperitc.actions.actions.name.begin_user_sync";
    public static final String ACTION_BEGIN_REBOOT = "com.agyohora.mobileperitc.actions.actions.name.beginreboot";
    public static final String ACTION_TAKE_SCREENSHOT = "com.agyohora.mobileperitc.actions.actions.name.take_screenshot";
    public static final String ACTION_BEGIN_SHUTDOWN = "com.agyohora.mobileperitc.actions.actions.name.beginshutdown";
    public static final String ACTION_CALIBRATION_DONE = "com.agyohora.mobileperitc.actions.actions.name.CalibrationDone";
    public static final String ACTION_COMMUNICATION_STATUS = "com.agyohora.mobileperitc.actions.actions.name.CommunicationStatus";
    public static final String ACTION_DATA_FROM_HMD = "com.agyohora.mobileperitc.actions.actions.name.DataFromHMD";
    public static final String ACTION_DO_CALIBRATION = "com.agyohora.mobileperitc.actions.actions.name.doCalibration";
    public static final String ACTION_SKIP_CALIBRATION = "com.agyohora.mobileperitc.actions.actions.name.skipCalibration";
    public static final String ACTION_HMD_CONNECTED = "com.agyohora.mobileperitc.actions.actions.name.HMDConnected";
    public static final String ACTION_HMD_NOT_CONNECTED = "com.agyohora.mobileperitc.actions.actions.name.HMDNOTConnected";
    public static final String ACTION_CAMERA_FREEZE = "com.agyohora.mobileperitc.actions.actions.name.camera_freeze";
    public static final String ACTION_REGISTER_TEST = "com.agyohora.mobileperitc.actions.actions.name.registerTest";
    public static final String ACTION_RESET_TOAST = "com.agyohora.mobileperitc.actions.actions.name.resetToast";
    public static final String ACTION_RESET_TIP = "com.agyohora.mobileperitc.actions.actions.name.resetTip";
    public static final String ACTION_START_NEW_TEST = "com.agyohora.mobileperitc.actions.actions.name.StartNewTest";
    public static final String ACTION_START_TEST = "com.agyohora.mobileperitc.actions.actions.name.startTest";
    public static final String ACTION_SAVE_STATE = "com.agyohora.mobileperitc.actions.actions.name.SaveState";
    public static final String ACTION_INSTALL_COMPLETE = "com.agyohora.mobileperitc.INSTALL_COMPLETE";
    public static final String ACTION_PATIENT_DETAILS = "com.agyohora.mobileperitc.actions.actions.name.patientDetails";
    public static final String ACTION_PATIENT_DETAILS_WITHOUT_EI = "com.agyohora.mobileperitc.actions.actions.name.patientDetailsWithoutEi";

    public static final String ACTION_TEST_DETAILS = "com.agyohora.mobileperitc.actions.actions.name.testDetails";
    public static final String ACTION_BEGIN_HMD_UPDATE = "com.agyohora.mobileperitc.actions.actions.name.beginHMDUpdate";
    public static final String ACTION_SHOW_TEST_DETAILS_AND_STOP_TEST = "com.agyohora.mobileperitc.actions.actions.name.show_test_details_and_stop_test";
    public static final String ACTION_LENS_SETTINGS_DETAILS = "com.agyohora.mobileperitc.actions.actions.name.lensSettingsDetails";
    public static final String ACTION_ADJUST_IPD_DETAILS = "com.agyohora.mobileperitc.actions.actions.name.ipdSettingsDetails";
    public static final String ACTION_PATIENT_TEST_PROFILE = "com.agyohora.mobileperitc.actions.actions.name.patientProfileDetails";
    public static final String ACTION_CALIBRATION_SETTINGS = "com.agyohora.mobileperitc.actions.actions.name.calibration";
    public static final String ACTION_BACK_TO_PATIENT_PRIMARY_DETAILS = "com.agyohora.mobileperitc.actions.actions.name.backToPrimaryPatientDetais";
    public static final String ACTION_BACK_TO_TEST_PRIMARY_DETAILS = "com.agyohora.mobileperitc.actions.actions.name.backToPrimaryTestDetais";
    public static final String ACTION_BACK_TO_LENS_POWER_SETTINGS = "com.agyohora.mobileperitc.actions.actions.name.backToLensPowerSettings";
    public static final String ACTION_BACK_TO_TEST_PROFILE_SETTINGS = "com.agyohora.mobileperitc.actions.actions.name.backToTestProfileSettings";
    public static final String ACTION_BACK_TO_CALIBRATION_SETTINGS = "com.agyohora.mobileperitc.actions.actions.name.backToCalibrationSettings";
    public static final String ACTION_BACK_TO_IPD_SETTINGS = "com.agyohora.mobileperitc.actions.actions.name.backToIPDSettings";
    public static final String ACTION_BACK_TO_LAUNCH_SCREEN = "com.agyohora.mobileperitc.actions.actions.name.backToLaunchScreen";
    public static final String ACTION_PAUSE_TEST = "com.agyohora.mobileperitc.actions.actions.name.pauseTest";
    public static final String ACTION_UNPAUSE_TEST = "com.agyohora.mobileperitc.actions.actions.name.unPauseTest";
    public static final String ACTION_START_INIT_SETTINGS = "com.agyohora.mobileperitc.actions.actions.name.initSettings";
    public static final String ACTION_START_HOME = "com.agyohora.mobileperitc.actions.actions.name.startHome";
    public static final String ACTION_START_UPDATE_TRANSFER = "com.agyohora.mobileperitc.actions.actions.name.startHmdUpdateTransfer";
    public static final String ACTION_FOVEA_PROCEED = "com.agyohora.mobileperitc.actions.actions.name.foveaProceed";
    public static final String ACTION_FOVEA_REDO = "com.agyohora.mobileperitc.actions.actions.name.foveaRedo";
    public static final String ACTION_START_EI = "com.agyohora.mobileperitc.actions.actions.name.startei";
    public static final String ACTION_CHECK_BATTERY = "com.agyohora.mobileperitc.actions.actions.name.check_battery";
    public static final String ACTION_CHECK_ACCESSIBILITY_STATUS = "com.agyohora.mobileperitc.actions.actions.name.check_accessibility_status";
    public static final String ACTION_START_CHECKING_INTERFACES = "com.agyohora.mobileperitc.actions.actions.name.start_checking_interfaces";
    public static final String ACTION_SILENTLY_START_EI = "com.agyohora.mobileperitc.actions.actions.name.silentlystartei";
    public static final String ACTION_STOP_EI = "com.agyohora.mobileperitc.actions.actions.name.stopei";
    public static final String ACTION_STOP_TEST_SILENTLY = "com.agyohora.mobileperitc.actions.actions.name.action_stop_test_silently";
    public static final String ACTION_UPDATE_MODE = "com.agyohora.mobileperitc.actions.actions.name.silentModeUpdate";
    public static final String ACTION_CHECK_FOR_PREVIOUS_RESULTS = "com.agyohora.mobileperitc.actions.actions.name.previous_result";
    public static final String ACTION_CHECK_FOR_ALPHA_BETA = "com.agyohora.mobileperitc.actions.actions.name.alpha_beta_check";
    public static final String ACTION_START_EI_COMPONENT_FAILURE = "com.agyohora.mobileperitc.actions.actions.name.ei_component_failure";
    public static final String ACTION_SHOW_ACCESSORY_STATUS = "com.agyohora.mobileperitc.actions.actions.name_accessory_status";
    public static final String ACTION_SHOW_ACCESSORY_STATUS_WAITSCREEN = "com.agyohora.mobileperitc.actions.actions.name.waitscreen_accessories";
    public static final String ACTION_START_DOWNLOAD_UPDATES = "com.agyohora.mobileperitc.actions.actions.name.downloadUpdates";
    public static final String ACTION_HMD_UPDATE_DON_START_TC_UPDATE = "com.agyohora.mobileperitc.actions.actions.name.startTCUpdate";
    public static final String ACTION_BATTERY_NOT_OK_NOT_CHARGING = "com.agyohora.mobileperitc.actions.actions.name.battery_not_okay_not_charging";
    public static final String ACTION_BATTERY_LOW_KEEP_CHARGING = "com.agyohora.mobileperitc.actions.actions.name.battery_low_keep_charging";
    public static final String ACTION_PLUG_OUT_CHARGE_OK = "com.agyohora.mobileperitc.actions.actions.name.plug_out_charge_ok";
    public static final String ACTION_SHOW_PRE_PRODUCTION_SCREEN = "com.agyohora.mobileperitc.actions.actions.name.screen_pre_production";
    public static final String ACTION_SHOW_START_EI_SCREEN = "com.agyohora.mobileperitc.actions.actions.name.start_ei_screen";
    public static final String ACTION_SHOW_CANT_EI_SCREEN = "com.agyohora.mobileperitc.actions.actions.name.cant_start_ei_screen";
    public static final String ACTION_SEND_VECTOR_DATA = "com.agyohora.mobileperitc.actions.actions.name.send_vector_data";
    public static final String ACTION_UPDATATE_HMD_BATTERY_STATUS = "com.agyohora.mobileperitc.actions.actions.name.hmd_battery_status";
    public static final String ACTION_SAVE_VECTOR_DATA_IN_HMD = "com.agyohora.mobileperitc.actions.actions.name.save_vector_data_in_hmd";
    public static final String ACTION_SAVE_VECTOR_DATA_IN_HMD_AND_GET_FEEDBACK = "com.agyohora.mobileperitc.actions.actions.name.save_vector_data_in_hmd_and_get_feed_back";
    public static final String ACTION_GET_VECTOR_DATA = "com.agyohora.mobileperitc.actions.actions.name.get_vector_data";
    public static final String ACTION_GET_PRB_STATUS = "com.agyohora.mobileperitc.actions.actions.name.get_prb_status";
    public static final String ACTION_CALIB_UP = "com.agyohora.mobileperitc.actions.actions.name.calib_up";
    public static final String ACTION_CALIB_DOWN = "com.agyohora.mobileperitc.actions.actions.name.calib_down";
    public static final String ACTION_UPDATE_HMD_SYNC = "com.agyohora.mobileperitc.actions.actions.name.hmd_sync_update";
    public static final String ACTION_UPDATE_CAMERA_ALPHA_BETA = "com.agyohora.mobileperitc.actions.actions.name.hmd_camera_alpha_beta";
    public static final String ACTION_STOP_IDLE_TIMERS = "com.agyohora.mobileperitc.actions.actions.name.stop_idle_timers";
    public static final String ACTION_SETTINGS = "com.agyohora.mobileperitc.actions.actions.name.settings_screen";
    public static final String ACTION_SHOW_HMD_DETAILS = "com.agyohora.mobileperitc.actions.actions.name.hmd_details_screen";
    public static final String ACTION_OPEN_USER_PROFILE = "com.agyohora.mobileperitc.actions.actions.name.user_profile";
    public static final String ACTION_OPEN_ADMIN_PROFILE = "com.agyohora.mobileperitc.actions.actions.name.admin_profile";
    public static final String ACTION_SET_ADMIN_PASSWORD = "com.agyohora.mobileperitc.actions.actions.name.set_admin_password";
    public static final String ACTION_SET_USER_PASSWORD = "com.agyohora.mobileperitc.actions.actions.name.set_user_password";
    public static final String ACTION_CHECK_FOR_UPDATES = "com.agyohora.mobileperitc.actions.actions.name.check_for_updates";
    public static final String ACTION_SET_SW_UPTO_DATE = "com.agyohora.mobileperitc.actions.actions.name.software_upto_date";
    public static final String ACTION_UPDATE_AVAILABLE = "com.agyohora.mobileperitc.actions.actions.name.update_available";
    public static final String ACTION_UPDATE_CONNECTED_TO_HMD = "com.agyohora.mobileperitc.actions.actions.name.update_connected_to_hmd";
    public static final String ACTION_BEGIN_HMD_UPDATE_ALREADY = "com.agyohora.mobileperitc.actions.actions.name.beginHMDUpdateAlready";
    public static final String ACTION_BEGIN_HMD_UPDATE_ALONE = "com.agyohora.mobileperitc.actions.actions.name.beginHMDUpdateAlone";
    public static final String ACTION_BEGIN_TC_UPDATE_ALREADY = "com.agyohora.mobileperitc.actions.actions.name.beginTCUpdateAlready";
    public static final String ACTION_BEGIN_TC_UPDATE_ALONE = "com.agyohora.mobileperitc.actions.actions.name.beginTCUpdateAlone";
    public static final String ACTION_CLOSE_THE_TC = "com.agyohora.mobileperitc.actions.actions.name.close_tc";
    public static final String ACTION_START_LOGOUT_DECOY = "com.agyohora.mobileperitc.actions.actions.name.logout_decoy";
    public static final String ACTION_START_DATABASE_RESTORE = "com.agyohora.mobileperitc.actions.actions.name.check.database_restore";
    public static final String ACTION_DATABASE_RESTORE_AVAILABLE = "com.agyohora.mobileperitc.actions.actions.name.database_restore_available";
    public static final String ACTION_START_DATABASE_DOWNLOAD = "com.agyohora.mobileperitc.actions.actions.name.start_database_download";
    public static final String ACTION_START_DATABASE_MERGING = "com.agyohora.mobileperitc.actions.actions.name.start_database_merging";
    public static final String ACTION_UPLOAD_DATABASE_RESTORE_LOGS = "com.agyohora.mobileperitc.actions.actions.name.upload_database_restore_logs";
    public static final String ACTION_FINISH_DATABASE_RESTORE = "com.agyohora.mobileperitc.actions.actions.name.finish_database_restore";
    public static final String ACTION_FINISH_DATABASE_RESTORE_WITHOUT_DATA = "com.agyohora.mobileperitc.actions.actions.name.finish_database_restore_data";
    public static final String ACTION_UPDATE_PRB = "com.agyohora.mobileperitc.actions.actions.name.update_prb";
    public static final String ACTION_OPEN_HMD_SYNC = "com.agyohora.mobileperitc.actions.actions.name.show_hmd_sync_screen";

    public Actions() {
        super("Actions");
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void initCommunication() {
        Bundle payload = actionCreator(ACTION_INITIALIZE_COMMUNICATION);
        payload.putString("data", "NA");
        fireTheIntent(payload);
    }

    public static void hmdSyncCheckUpdate() {
        Bundle payload = actionCreator(ACTION_UPDATE_HMD_SYNC);
        payload.putString("data", "NA");
        fireTheIntent(payload);
    }

    public static void openAdminProfile() {
        Bundle payload = actionCreator(ACTION_OPEN_ADMIN_PROFILE);
        payload.putString("data", "NA");
        fireTheIntent(payload);
    }

    public static void openUserProfile() {
        Bundle payload = actionCreator(ACTION_OPEN_USER_PROFILE);
        payload.putString("data", "NA");
        fireTheIntent(payload);
    }

    public static void sendHMDCameraAlphaBeta(String camDetails) {
        Bundle payload = actionCreator(ACTION_UPDATE_CAMERA_ALPHA_BETA);
        payload.putString("data", camDetails);
        fireTheIntent(payload);
    }

    public static void sendStopIdleTimers() {
        Bundle payload = actionCreator(ACTION_STOP_IDLE_TIMERS);
        payload.putString("data", "NA");
        fireTheIntent(payload);
    }

    public static void backToHomeScreen() {

        Bundle payload = actionCreator(ACTION_BACK_TO_HOME_SCREEN);
        payload.putString("data", "NA");
        fireTheIntent(payload);
    }

    public static void goHomeImmediately() {
        Bundle payload = actionCreator(ACTION_GO_HOME_IMMEDIATELY);
        payload.putString("data", "NA");
        fireTheIntent(payload);
    }

    public static void abortCalibAndGoHomeImmediately() {
        Bundle payload = actionCreator(ACTION_ABORT_CALIB_AND_GO_HOME_IMMEDIATELY);
        payload.putString("data", "NA");
        fireTheIntent(payload);
    }

    public static void backToPrimaryPatientDetailsScreen() {
        Bundle payload = actionCreator(ACTION_BACK_TO_PATIENT_PRIMARY_DETAILS);
        payload.putString("data", "NA");
        fireTheIntent(payload);
    }

    public static void backToPrimaryTestDetailsScreen() {
        Bundle payload = actionCreator(ACTION_BACK_TO_TEST_PRIMARY_DETAILS);
        payload.putString("data", "NA");
        fireTheIntent(payload);
    }

    public static void backToLensPowerSettings() {
        Bundle payload = actionCreator(ACTION_BACK_TO_LENS_POWER_SETTINGS);
        payload.putString("data", "NA");
        fireTheIntent(payload);
    }

    public static void backToSetTest() {
        Bundle payload = actionCreator(ACTION_BACK_TO_SET_TEST);
        payload.putString("data", "NA");
        fireTheIntent(payload);
    }

    public static void abortTest() {
        Bundle payload = actionCreator(ACTION_ABORT);
        payload.putString("data", "NA");
        fireTheIntent(payload);
    }

    public static void setIdleShutDownLater() {
        Bundle payload = actionCreator(ACTION_IDLE_SHUTDOWN_LATER);
        payload.putString("data", "NA");
        fireTheIntent(payload);
    }

    public static void setIdleShutDownOkay() {
        Bundle payload = actionCreator(ACTION_IDLE_SHUTDOWN_OKAY);
        payload.putString("data", "NA");
        fireTheIntent(payload);
    }

    public static void abortCalibration() {
        Bundle payload = actionCreator(ACTION_ABORT_CALIBRATION);
        payload.putString("data", "NA");
        fireTheIntent(payload);
    }

    public static void beginPatientDetails() {
        Bundle payload = actionCreator(ACTION_PATIENT_DETAILS);
        payload.putString("data", "NA");
        fireTheIntent(payload);
    }

    public static void beginPatientDetailsWithoutEIStart() {
        Bundle payload = actionCreator(ACTION_PATIENT_DETAILS_WITHOUT_EI);
        payload.putString("data", "NA");
        fireTheIntent(payload);
    }

    public static void beginTestControllerSettings() {
        Bundle payload = actionCreator(ACTION_SETTINGS);
        payload.putString("data", "NA");
        fireTheIntent(payload);
    }

    public static void beginOtherSettings() {
        Bundle payload = actionCreator(ACTION_START_INIT_SETTINGS);
        payload.putString("data", "NA");
        fireTheIntent(payload);
    }

    public static void setAdminPassword() {
        Bundle payload = actionCreator(ACTION_SET_ADMIN_PASSWORD);
        payload.putString("data", "NA");
        fireTheIntent(payload);
    }

    public static void setUserPassword() {
        Bundle payload = actionCreator(ACTION_SET_USER_PASSWORD);
        payload.putString("data", "NA");
        fireTheIntent(payload);
    }

    public static void setCheckForUpdateScreen() {
        Bundle payload = actionCreator(ACTION_CHECK_FOR_UPDATES);
        payload.putString("data", "NA");
        fireTheIntent(payload);
    }

    public static void setSoftwareUptoDateScreen() {
        Bundle payload = actionCreator(ACTION_SET_SW_UPTO_DATE);
        payload.putString("data", "NA");
        fireTheIntent(payload);
    }

    public static void showUpdateAvailable() {
        Bundle payload = actionCreator(ACTION_UPDATE_AVAILABLE);
        payload.putString("data", "NA");
        fireTheIntent(payload);
    }

    public static void showUpdateConnectedToHMD() {
        Bundle payload = actionCreator(ACTION_UPDATE_CONNECTED_TO_HMD);
        payload.putString("data", "NA");
        fireTheIntent(payload);
    }

    public static void beginHome() {
        Bundle payload = actionCreator(ACTION_START_HOME);
        payload.putString("data", "NA");
        fireTheIntent(payload);
    }

    public static void beginTestDetails() {
        Bundle payload = actionCreator(ACTION_TEST_DETAILS);
        payload.putString("data", "NA");
        fireTheIntent(payload);
    }

    public static void beginHMDUpdate() {
        Bundle payload = actionCreator(ACTION_BEGIN_HMD_UPDATE);
        payload.putString("data", "NA");
        fireTheIntent(payload);
    }

    public static void beginHMDDownloadedAlready() {
        Bundle payload = actionCreator(ACTION_BEGIN_HMD_UPDATE_ALREADY);
        payload.putString("data", "NA");
        fireTheIntent(payload);
    }

    public static void beginHMDUpdateAlone() {
        Bundle payload = actionCreator(ACTION_BEGIN_HMD_UPDATE_ALONE);
        payload.putString("data", "NA");
        fireTheIntent(payload);
    }

    public static void beginTcUpdateAlready() {
        Bundle payload = actionCreator(ACTION_BEGIN_TC_UPDATE_ALREADY);
        payload.putString("data", "NA");
        fireTheIntent(payload);
    }

    public static void beginTcUpdateAlone() {
        Bundle payload = actionCreator(ACTION_BEGIN_TC_UPDATE_ALONE);
        payload.putString("data", "NA");
        fireTheIntent(payload);
    }

    public static void closeTheTC() {
        Bundle payload = actionCreator(ACTION_CLOSE_THE_TC);
        payload.putString("data", "NA");
        fireTheIntent(payload);
    }

    public static void showLogOutDecoy() {
        Bundle payload = actionCreator(ACTION_START_LOGOUT_DECOY);
        payload.putString("data", "NA");
        fireTheIntent(payload);
    }

    public static void showDataBaseRestore() {
        Bundle payload = actionCreator(ACTION_START_DATABASE_RESTORE);
        payload.putString("data", "NA");
        fireTheIntent(payload);
    }

    public static void showDatabaseAvailable() {
        Bundle payload = actionCreator(ACTION_DATABASE_RESTORE_AVAILABLE);
        payload.putString("data", "NA");
        fireTheIntent(payload);
    }

    public static void showTestDetailsAndStopTest() {
        Bundle payload = actionCreator(ACTION_SHOW_TEST_DETAILS_AND_STOP_TEST);
        payload.putString("data", "NA");
        fireTheIntent(payload);
    }

    public static void beginLensSettings() {
        Bundle payload = actionCreator(ACTION_LENS_SETTINGS_DETAILS);
        payload.putString("data", "NA");
        fireTheIntent(payload);
    }

    public static void beginCalibrationSettings() {
        Bundle payload = actionCreator(ACTION_CALIBRATION_SETTINGS);
        payload.putString("data", "NA");
        fireTheIntent(payload);
    }

    public static void beginTestProfile() {
        //Bundle payload = actionCreator(ACTION_PATIENT_TEST_PROFILE);
        Bundle payload = actionCreator(ACTION_REGISTER_TEST);
        payload.putString("data", "NA");
        fireTheIntent(payload);
    }

    public static void beginTest(boolean calibDone, String caller) {
        Log.e("beginTest", "caller " + caller);
        Bundle payload = actionCreator(ACTION_BEGIN_TEST);
        payload.putBoolean("data", calibDone);
        fireTheIntent(payload);
    }

    public static void beginIPD() {
        Bundle payload = actionCreator(ACTION_ADJUST_IPD_DETAILS);
        payload.putString("data", "NA");
        fireTheIntent(payload);
    }

    public static void beginProductionSync() {
        Bundle payload = actionCreator(ACTION_BEGIN_PRODUCTION_SYNC);
        payload.putString("data", null);
        fireTheIntent(payload);
    }

    public static void beginUserSync() {
        Bundle payload = actionCreator(ACTION_BEGIN_USER_SYNC);
        payload.putString("data", null);
        fireTheIntent(payload);
    }

    public static void beginReboot() {
        Bundle payload = actionCreator(ACTION_BEGIN_REBOOT);
        payload.putString("data", null);
        fireTheIntent(payload);
    }

    public static void beginScreenshot() {
        Bundle payload = actionCreator(ACTION_TAKE_SCREENSHOT);
        payload.putString("data", null);
        fireTheIntent(payload);
    }

    public static void beginShutDown() {
        Bundle payload = actionCreator(ACTION_BEGIN_SHUTDOWN);
        payload.putString("data", null);
        fireTheIntent(payload);
    }

    public static void beginEiComponentFailure() {
        Bundle payload = actionCreator(ACTION_START_EI_COMPONENT_FAILURE);
        payload.putString("data", null);
        fireTheIntent(payload);
    }

    public static void showAccessoryStatus() {
        Bundle payload = actionCreator(ACTION_SHOW_ACCESSORY_STATUS);
        payload.putString("data", null);
        fireTheIntent(payload);
    }

    public static void showAccessoryCheckWaitScreen() {
        Bundle payload = actionCreator(ACTION_SHOW_ACCESSORY_STATUS_WAITSCREEN);
        payload.putString("data", null);
        fireTheIntent(payload);
    }

    public static void startDownloadUpdates() {
        Bundle payload = actionCreator(ACTION_START_DOWNLOAD_UPDATES);
        payload.putString("data", null);
        fireTheIntent(payload);

    }

    public static void startDownloadDatabase() {
        Bundle payload = actionCreator(ACTION_START_DATABASE_DOWNLOAD);
        payload.putString("data", null);
        fireTheIntent(payload);

    }

    public static void startDownloadMerging() {
        Bundle payload = actionCreator(ACTION_START_DATABASE_MERGING);
        payload.putString("data", null);
        fireTheIntent(payload);
    }

    public static void uploadDataRestoreLogs(String data) {
        Log.e("uploadDataRestoreLogs", "called");
        Bundle payload = actionCreator(ACTION_UPLOAD_DATABASE_RESTORE_LOGS);
        payload.putString("data", data);
        fireTheIntent(payload);
    }

    public static void dataRestoreFinished(String data) {
        Log.e("dataRestoreFinished", "called");
        Bundle payload = actionCreator(ACTION_FINISH_DATABASE_RESTORE);
        payload.putString("data", data);
        fireTheIntent(payload);
    }

    public static void dataRestoreFinished() {
        Bundle payload = actionCreator(ACTION_FINISH_DATABASE_RESTORE_WITHOUT_DATA);
        payload.putString("data", null);
        fireTheIntent(payload);
    }

    public static void startTCUpdate() {
        Bundle payload = actionCreator(ACTION_HMD_UPDATE_DON_START_TC_UPDATE);
        payload.putString("data", null);
        fireTheIntent(payload);
    }

    public static void calibUp() {
        Bundle payload = actionCreator(ACTION_CALIB_UP);
        payload.putString("data", null);
        fireTheIntent(payload);

    }

    public static void calibDown() {
        Bundle payload = actionCreator(ACTION_CALIB_DOWN);
        payload.putString("data", null);
        fireTheIntent(payload);
    }

    public static void showPreProductionScreen() {
        Bundle payload = actionCreator(ACTION_SHOW_PRE_PRODUCTION_SCREEN);
        payload.putString("data", null);
        fireTheIntent(payload);
    }

    public static void showStartEIScreen() {
        Bundle payload = actionCreator(ACTION_SHOW_START_EI_SCREEN);
        payload.putString("data", null);
        fireTheIntent(payload);
    }

    public static void cantStartEIScreen() {
        Bundle payload = actionCreator(ACTION_SHOW_CANT_EI_SCREEN);
        payload.putString("data", null);
        fireTheIntent(payload);
    }

    public static void setActionSendVectorData() {
        Bundle payload = actionCreator(ACTION_SEND_VECTOR_DATA);
        payload.putString("data", null);
        fireTheIntent(payload);
    }

    public static void updateHMDBatteryStatus() {
        Bundle payload = actionCreator(ACTION_UPDATATE_HMD_BATTERY_STATUS);
        payload.putString("data", null);
        fireTheIntent(payload);
    }

    public static void setActionSaveVectorDatainHMD() {
        Bundle payload = actionCreator(ACTION_SAVE_VECTOR_DATA_IN_HMD);
        payload.putString("data", null);
        fireTheIntent(payload);
    }

    public static void setActionSaveVectorDataInHmdAndGetFeedback() {
        Bundle payload = actionCreator(ACTION_SAVE_VECTOR_DATA_IN_HMD_AND_GET_FEEDBACK);
        payload.putString("data", null);
        fireTheIntent(payload);
    }

    public static void setActionGetVectorData() {
        Bundle payload = actionCreator(ACTION_GET_VECTOR_DATA);
        payload.putString("data", null);
        fireTheIntent(payload);
    }

    public static void getActionGetPrbStatus() {
        Bundle payload = actionCreator(ACTION_GET_PRB_STATUS);
        payload.putString("data", null);
        fireTheIntent(payload);
    }

    public static void batteryNotOkayNotCharging() {
        Bundle payload = actionCreator(ACTION_BATTERY_NOT_OK_NOT_CHARGING);
        payload.putString("data", null);
        fireTheIntent(payload);
    }

    public static void showHmdDetailsScreen() {
        Bundle payload = actionCreator(ACTION_SHOW_HMD_DETAILS);
        payload.putString("data", null);
        fireTheIntent(payload);
    }

    public static void batteryLowKeepCharging() {
        Bundle payload = actionCreator(ACTION_BATTERY_LOW_KEEP_CHARGING);
        payload.putString("data", null);
        fireTheIntent(payload);
    }

    public static void plugOutChargeOkay() {
        Bundle payload = actionCreator(ACTION_PLUG_OUT_CHARGE_OK);
        payload.putString("data", null);
        fireTheIntent(payload);
    }

    public static void startHmdUpdateTransfer() {

        Bundle payload = actionCreator(ACTION_START_UPDATE_TRANSFER);
        payload.putString("data", null);
        //todo : we will need to update this based on what we actually send from communication
        fireTheIntent(payload);
    }

    public static void pauseTest() {
        Bundle payload = actionCreator(ACTION_PAUSE_TEST);
        payload.putString("data", null);
        //todo : we will need to update this based on what we actually send from communication
        fireTheIntent(payload);
    }

    public static void unPauseTest() {
        Bundle payload = actionCreator(ACTION_UNPAUSE_TEST);
        payload.putString("data", null);
        //todo : we will need to update this based on what we actually send from communication
        fireTheIntent(payload);
    }

    public static void commStatus(String status) {
        Bundle payload = actionCreator(ACTION_COMMUNICATION_STATUS);
        Log.d("NampuComm Actions", "sending comm status to store");
        payload.putString("data", status);
        fireTheIntent(payload);
    }

    public static void dataFromHMD(String data) {
        //CommonUtils.writeToMyDebugFile("\ndatafromhmd before actions " + data);
        Bundle payload = actionCreator(ACTION_DATA_FROM_HMD);
        Log.d("NampuCommTC_ACTIONS", data);
        payload.putString("data", data);
        fireTheIntent(payload);
    }

    public static void doCalibration() {
        Bundle payload = actionCreator(ACTION_DO_CALIBRATION);
        payload.putString("data", "NA");
        fireTheIntent(payload);
    }

    public static void HMDConnected() {
        Bundle payload = actionCreator(ACTION_HMD_CONNECTED);
        payload.putString("data", "NA");
        fireTheIntent(payload);
    }

    public static void HMDNotConnected() {
        Bundle payload = actionCreator(ACTION_HMD_NOT_CONNECTED);
        payload.putString("data", "NA");
        fireTheIntent(payload);
    }

    public static void cameraFreeze() {
        Bundle payload = actionCreator(ACTION_CAMERA_FREEZE);
        payload.putString("data", "NA");
        fireTheIntent(payload);
    }

    public static void resetToast() {
        Bundle payload = actionCreator(ACTION_RESET_TOAST);
        payload.putString("data", "NA");
        fireTheIntent(payload);
    }

    public static void saveState(Bundle state) {
        Log.d("ActionTracker", "This got called");
        Bundle payload = actionCreator(ACTION_SAVE_STATE);
        payload.putBundle("data", state);
        fireTheIntent(payload);
    }

    public static void startTest() {
        Bundle payload = actionCreator(ACTION_START_TEST);
        payload.putString("data", "NA");
        fireTheIntent(payload);
    }

    public static void foveaFeedback(boolean isFoveaDone) {
        Bundle payload = isFoveaDone ? actionCreator(ACTION_FOVEA_PROCEED) : actionCreator(ACTION_FOVEA_REDO);
        payload.putString("data", null);
        fireTheIntent(payload);
    }

    public static void silentlyStartEI() {
        Bundle payload = actionCreator(ACTION_SILENTLY_START_EI);
        payload.putString("data", null);
        fireTheIntent(payload);
    }

    public static void startEI() {
        Bundle payload = actionCreator(ACTION_START_EI);
        payload.putString("data", null);
        fireTheIntent(payload);
    }

    public static void checkBattery() {
        Bundle payload = actionCreator(ACTION_CHECK_BATTERY);
        payload.putString("data", null);
        fireTheIntent(payload);
    }

    public static void checkAccessibilityStatus() {
        Bundle payload = actionCreator(ACTION_CHECK_ACCESSIBILITY_STATUS);
        payload.putString("data", null);
        fireTheIntent(payload);
    }

    public static void startCheckingInterface() {
        Bundle payload = actionCreator(ACTION_START_CHECKING_INTERFACES);
        payload.putString("data", null);
        fireTheIntent(payload);
    }

    public static void stopEI() {
        Bundle payload = actionCreator(ACTION_STOP_EI);
        payload.putString("data", null);
        fireTheIntent(payload);
    }

    public static void startHMDSync() {
        Bundle payload = actionCreator(ACTION_OPEN_HMD_SYNC);
        payload.putString("data", null);
        fireTheIntent(payload);
    }

    public static void silentlyStopTest() {
        Bundle payload = actionCreator(ACTION_STOP_TEST_SILENTLY);
        payload.putString("data", null);
        fireTheIntent(payload);
    }

    public static void checkAlphaBeta() {
        Bundle payload = actionCreator(ACTION_CHECK_FOR_ALPHA_BETA);
        payload.putString("data", null);
        fireTheIntent(payload);
    }

    public static void silentlyUpdateMode(String modeNo) {
        Bundle payload = actionCreator(ACTION_UPDATE_MODE);
        payload.putString("data", modeNo);
        fireTheIntent(payload);
    }

    public static void setActionCheckForPreviousResults() {
        Bundle payload = actionCreator(ACTION_CHECK_FOR_PREVIOUS_RESULTS);
        payload.putString("data", null);
        fireTheIntent(payload);
    }

    public static void doPrbUpdate() {
        Bundle payload = actionCreator(ACTION_UPDATE_PRB);
        payload.putString("data", "NA");
        fireTheIntent(payload);
    }

    private static void fireTheIntent(Bundle payload) {
        Context context = MainActivity.applicationContext;
        if (context == null) {
            context = MyApplication.getInstance();
        }
        Intent intent = new Intent(context, Actions.class);
        intent.putExtra(DISPATCH_PAYLOAD, payload);
        context.startService(intent);
    }

    private static Bundle actionCreator(String name) {
        Bundle payload = new Bundle();
        payload.putString("name", name);
        return payload;
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            Intent intent2send = new Intent();
            intent2send.setAction(DISPATCH_TOSTORE);
            intent2send.putExtra(DISPATCH_PAYLOAD, intent.getBundleExtra(DISPATCH_PAYLOAD));
            Log.d("ActionGen", intent2send.getBundleExtra(DISPATCH_PAYLOAD).getString("name"));
            Log.d("ActionGen", "Sent some action ma");
            sendBroadcast(intent2send);
        } catch (Exception e) {
            FirebaseCrashlytics.getInstance().recordException(e);
        }
    }
}
