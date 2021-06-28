package com.agyohora.mobileperitc.data.preferences;

import android.content.Context;
import android.content.SharedPreferences;

import com.agyohora.mobileperitc.utils.AppConstants;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Sankar on 23-11-17.
 * class contains methods to set and get persisted data
 */

public class AppPreferencesHelper implements PreferencesHelper {

    private static final String PREF_KEY_DT_TIMEBASE = "pref_key_dt_timebase";
    private static final String PREF_PRODUCTION_DEVICE_CONFIGURATION_STATUS = "pref_production_device_configuration_status";
    private static final String PREF_KEY_PRODUCTION_FIRST_TIME_CONNECTION = "pref_key_production_first_time_connection";
    private static final String PREF_USER_DEVICE_CONFIGURATION_STATUS = "pref_user_device_configuration_status";
    private static final String PREF_CONFIG_FILE_COPIED_STATUS = "pref_config_file_copied_status";
    private static final String PREF_VECTOR_FILE_COPIED_STATUS = "pref_vector_file_copied_status";
    private static final String PREF_KEY_USER_FIRST_TIME_CONNECTION = "pref_key_user_first_time_connection";
    private static final String PREF_KEY_PRODUCTION_TESTING_STATUS = "pref_key_production_testing_status";
    private static final String PREF_DEVICE_ID = "pref_device_id";
    private static final String PREF_ORG_ID = "pref_org_id";

    private static final String PREF_KEY_RESULT_DATA = "pref_key_result_data";
    private static final String PREF_KEY_SEARCHABLE_PATIENT_NAME = "pref_key_searchable_patient_name";
    private static final String PREF_KEY_SEARCHABLE_MR_NUMBER = "pref_key_searchable_patient_mr_number";
    private static final String PREF_KEY_SEARCHABLE_MOBILE = "pref_key_searchable_patient_mobile";
    private static final String PREF_KEY_SEARCHABLE_AGE = "pref_key_searchable_patient_age";
    private static final String PREF_KEY_SEARCHABLE_SEX = "pref_key_searchable_patient_sex";
    private static final String PREF_KEY_SEARCHABLE_EYE = "pref_key_searchable_patient_eye";
    private static final String PREF_KEY_SEARCHABLE_TEST_TYPE = "pref_key_searchable_test_type";
    private static final String PREF_KEY_SEARCHABLE_TEST_PATTERN = "pref_key_searchable_test_pattern";
    private static final String PREF_KEY_CRITICAL_UPDATE = "pref_key_critical_update";
    private static final String PREF_KEY_CRITICAL_COUNTER = "pref_key_critical_counter";

    private static final String PREF_KEY_PATIENT_FIRST_NAME = "pref_key_patient_first_name";
    private static final String PREF_KEY_PATIENT_MRN_NUMBER = "pref_key_patient_mrn_number";
    private static final String PREF_KEY_PATIENT_DOB = "pref_key_patient_dob";
    private static final String PREF_KEY_PATIENT_SEX = "pref_key_patient_sex";
    private static final String PREF_KEY_PATIENT_MOBILE_NUMBER = "pref_key_mobile_number";
    private static final String PREF_KEY_PATIENT_TEST_EYE = "pref_key_test_eye";
    private static final String PREF_KEY_TEST_PATTERN = "pref_key_test_pattern";
    private static final String PREF_KEY_TEST_STRATEGY = "pref_key_test_strategy";
    private static final String PREF_KEY_TEST_SPHERICAL_POWER_INPUT = "pref_key_spherical_power_input";
    private static final String PREF_KEY_TEST_CYLINDRICAL_POWER_INPUT = "pref_key_cylindrical_power_input";
    private static final String PREF_KEY_TEST_CYLINDRICAL_AXIS_INPUT = "pref_key_cylindrical_axis_input";
    private static final String PREF_KEY_TEST_SPHERICAL_POWER = "pref_key_spherical_power";
    private static final String PREF_KEY_TEST_CYLINDRICAL_POWER = "pref_key_cylindrical_power";
    private static final String PREF_KEY_TEST_CYLINDRICAL_AXIS = "pref_key_cylindrical_axis";
    private static final String PREF_KEY_PATIENT_DETAILS_VIEW_VISIBILITY = "pref_key_patient_details_view_visibility";


    private static final String PREF_KEY_LATEST_HMD_VERSION_CODE = "pref_key_latest_hmd_version_code";
    private static final String PREF_KEY_LATEST_HMD_VERSION_NAME = "pref_key_latest_hmd_version_name";
    private static final String PREF_KEY_INSTALLED_HMD_VERSION_CODE = "pref_key_installed_hmd_version_code";
    private static final String PREF_KEY_INSTALLED_HMD_VERSION_NAME = "pref_key_installed_hmd_version_name";

    private static final String PREF_KEY_LATEST_TC_VERSION_CODE = "pref_key_latest_tc_version_code";
    private static final String PREF_KEY_LATEST_TC_VERSION_NAME = "pref_key_latest_tc_version_name";


    private static final String PREF_KEY_TC_UPDATE_DOWNLOAD_LINK = "pref_key_downloaded_tc_download_link";
    private static final String PREF_KEY_HMD_UPDATE_DOWNLOAD_LINK = "pref_key_downloaded_hmd_download_link";

    private static final String PREF_KEY_HMD_MODEL_NAME = "pref_key_hmd_model_name";


    private static final String PREF_KEY_LAST_INSERTED_ROW = "pref_key_last_inserted_row";

    private static final String PREF_RIGHT_CAMERA_ALPHA = "right_camera_alpha";
    private static final String PREF_RIGHT_CAMERA_BETA = "right_camera_beta";
    private static final String PREF_LEFT_CAMERA_ALPHA = "left_camera_alpha";
    private static final String PREF_LEFT_CAMERA_BETA = "left_camera_beta";


    private static final String PREF_LAST_FIVE_DISPLAY_STATUS = "last_five_display_status";
    private static final String PREF_LAST_FIVE_DISPLAY_STATUS_AS_STRING = "last_five_display_status_string";

    private static final String PREF_USER_PASSWORD = "pref_user_password";
    private static final String PREF_ADMIN_PASSWORD = "pref_admin_password";
    private static final String PREF_IS_SIGNED_IN = "pref_is_signed_in";
    private static final String PREF_ROLE = "pref_role";
    private static final String PREF_LAST_UPDATED_ON = "pref_last_updated_on";
    private static final String PREF_DATABASE_VERSION = "pref_database_versiion";
    private static final String PREF_ORG_UPDATE = "pref_org_update";
    private static final String PREF_PRB_COUNT = "prb_count";
    private static final String PREF_CYCLE_STATUS = "pref_cycle_status";
    private static final String PREF_CLEAR = "pref_clear";



    private final SharedPreferences mPrefs;

    public AppPreferencesHelper(Context context,
                                String prefFileName) {
        mPrefs = context.getSharedPreferences(prefFileName, Context.MODE_PRIVATE);
    }


    @Override
    public void clearPreferences() {
        mPrefs.edit().clear().apply();
    }

    @Override
    public void setUSB(String usb) {
        mPrefs.edit().putString(PREF_KEY_RESULT_DATA, usb).apply();
    }

    @Override
    public void setTimeBase(long timeBase) {
        mPrefs.edit().putLong(PREF_KEY_DT_TIMEBASE, timeBase).apply();
    }

    @Override
    public long getTimeBase() {
        return mPrefs.getLong(PREF_KEY_DT_TIMEBASE, AppConstants.NULL_INDEX);
    }

    @Override
    public void setResultData(String resultData) {
        mPrefs.edit().putString(PREF_KEY_RESULT_DATA, resultData).apply();
    }

    @Override
    public String getResultData() {
        return mPrefs.getString(PREF_KEY_RESULT_DATA, null);
    }

    @Override
    public void setSearchablePatientName(String resultData) {
        mPrefs.edit().putString(PREF_KEY_SEARCHABLE_PATIENT_NAME, resultData).apply();
    }

    @Override
    public String getSearchablePatientName() {
        return mPrefs.getString(PREF_KEY_SEARCHABLE_PATIENT_NAME, null);
    }

    @Override
    public void setSearchablePatientMrNumber(String resultData) {
        mPrefs.edit().putString(PREF_KEY_SEARCHABLE_MR_NUMBER, resultData).apply();
    }

    @Override
    public String getSearchablePatientMrNumber() {
        return mPrefs.getString(PREF_KEY_SEARCHABLE_MR_NUMBER, null);
    }

    @Override
    public void setSearchablePatientMobileNumber(String mobileNumber) {
        mPrefs.edit().putString(PREF_KEY_SEARCHABLE_MOBILE, mobileNumber).apply();
    }

    @Override
    public String getSearchablePatientMobileNumber() {
        return mPrefs.getString(PREF_KEY_SEARCHABLE_MOBILE, null);
    }

    @Override
    public void setSearchablePatientDOB(String age) {
        mPrefs.edit().putString(PREF_KEY_SEARCHABLE_AGE, age).apply();
    }

    @Override
    public String getSearchablePatientDOB() {
        return mPrefs.getString(PREF_KEY_SEARCHABLE_AGE, null);
    }

    @Override
    public void setSearchablePatientSex(String sex) {
        mPrefs.edit().putString(PREF_KEY_SEARCHABLE_SEX, sex).apply();
    }

    @Override
    public String getSearchablePatientSex() {
        return mPrefs.getString(PREF_KEY_SEARCHABLE_SEX, null);
    }

    @Override
    public String getSearchablePatientEye() {
        return mPrefs.getString(PREF_KEY_SEARCHABLE_EYE, null);
    }

    @Override
    public void setSearchablePatientEye(String eye) {
        mPrefs.edit().putString(PREF_KEY_SEARCHABLE_EYE, eye).apply();
    }

    @Override
    public String getSearchableTestType() {
        return mPrefs.getString(PREF_KEY_SEARCHABLE_TEST_TYPE, null);
    }

    @Override
    public void setSearchableTestType(String testType) {
        mPrefs.edit().putString(PREF_KEY_SEARCHABLE_TEST_TYPE, testType).apply();
    }

    @Override
    public String getSearchableTestPattern() {
        return mPrefs.getString(PREF_KEY_SEARCHABLE_TEST_PATTERN, null);
    }

    @Override
    public void setSearchableTestPattern(String testPattern) {
        mPrefs.edit().putString(PREF_KEY_SEARCHABLE_TEST_PATTERN, testPattern).apply();
    }

    //Patient Details

    @Override
    public void setPatientFirstName(String name) {
        mPrefs.edit().putString(PREF_KEY_PATIENT_FIRST_NAME, name).apply();
    }

    @Override
    public String getPatientFirstName() {
        return mPrefs.getString(PREF_KEY_PATIENT_FIRST_NAME, null);
    }

    @Override
    public void setPatientMrnNumber(String number) {
        mPrefs.edit().putString(PREF_KEY_PATIENT_MRN_NUMBER, number).apply();
    }

    @Override
    public String getPatientMrnNumber() {
        return mPrefs.getString(PREF_KEY_PATIENT_MRN_NUMBER, null);
    }

    @Override
    public void setPatientDOB(String dob) {
        mPrefs.edit().putString(PREF_KEY_PATIENT_DOB, dob).apply();
    }

    @Override
    public String getPatientDOB() {
        return mPrefs.getString(PREF_KEY_PATIENT_DOB, null);
    }

    @Override
    public String getPatientMobileNumber() {
        return mPrefs.getString(PREF_KEY_PATIENT_MOBILE_NUMBER, null);
    }

    @Override
    public void setPatientMobileNumber(String number) {
        mPrefs.edit().putString(PREF_KEY_PATIENT_MOBILE_NUMBER, number).apply();
    }

    @Override
    public void setPatientSex(String sex) {
        mPrefs.edit().putString(PREF_KEY_PATIENT_SEX, sex).apply();
    }

    @Override
    public String getPatientSex() {
        return mPrefs.getString(PREF_KEY_PATIENT_SEX, null);
    }

    @Override
    public void setPatientTestEye(String eye) {
        mPrefs.edit().putString(PREF_KEY_PATIENT_TEST_EYE, eye).apply();
    }

    @Override
    public String getPatientTestEye() {
        return mPrefs.getString(PREF_KEY_PATIENT_TEST_EYE, null);
    }

    @Override
    public void setTestPattern(String pattern) {
        mPrefs.edit().putString(PREF_KEY_TEST_PATTERN, pattern).apply();
    }

    @Override
    public String getTestPattern() {
        return mPrefs.getString(PREF_KEY_TEST_PATTERN, null);
    }

    @Override
    public void setTestStrategy(String strategy) {
        mPrefs.edit().putString(PREF_KEY_TEST_STRATEGY, strategy).apply();
    }

    @Override
    public String getTestStrategy() {
        return mPrefs.getString(PREF_KEY_TEST_STRATEGY, null);
    }

    @Override
    public void setTestSphericalPower(String sphericalPower) {
        mPrefs.edit().putString(PREF_KEY_TEST_SPHERICAL_POWER, sphericalPower).apply();
    }

    @Override
    public String getTestSphericalPower() {
        return mPrefs.getString(PREF_KEY_TEST_SPHERICAL_POWER, null);
    }

    @Override
    public void setTestCylindricalPower(String cylindricalPower) {
        mPrefs.edit().putString(PREF_KEY_TEST_CYLINDRICAL_POWER, cylindricalPower).apply();
    }

    @Override
    public String getTestCylindricalPower() {
        return mPrefs.getString(PREF_KEY_TEST_CYLINDRICAL_POWER, null);
    }

    @Override
    public void setTestCylindricalAxis(String cylindricalAxis) {
        mPrefs.edit().putString(PREF_KEY_TEST_CYLINDRICAL_AXIS, cylindricalAxis).apply();
    }

    @Override
    public String getTestCylindricalAxis() {
        return mPrefs.getString(PREF_KEY_TEST_CYLINDRICAL_AXIS, null);
    }

    @Override
    public void setTestSphericalPowerInput(String sphericalPower) {
        mPrefs.edit().putString(PREF_KEY_TEST_SPHERICAL_POWER_INPUT, sphericalPower).apply();
    }

    @Override
    public String getTestSphericalPowerInput() {
        return mPrefs.getString(PREF_KEY_TEST_SPHERICAL_POWER_INPUT, null);
    }

    @Override
    public void setTestCylindricalPowerInput(String cylindricalPower) {
        mPrefs.edit().putString(PREF_KEY_TEST_CYLINDRICAL_POWER_INPUT, cylindricalPower).apply();
    }

    @Override
    public String getTestCylindricalPowerInput() {
        return mPrefs.getString(PREF_KEY_TEST_CYLINDRICAL_POWER_INPUT, null);
    }

    @Override
    public void setTestCylindricalAxisInput(String cylindricalAxis) {
        mPrefs.edit().putString(PREF_KEY_TEST_CYLINDRICAL_AXIS_INPUT, cylindricalAxis).apply();
    }

    @Override
    public String getTestCylindricalAxisInput() {
        return mPrefs.getString(PREF_KEY_TEST_CYLINDRICAL_AXIS_INPUT, null);
    }

    // Id of last inserted row

    @Override
    public void setLastInsertedRow(String insertedRow) {
        mPrefs.edit().putString(PREF_KEY_LAST_INSERTED_ROW, insertedRow).apply();
    }

    @Override
    public String getLastInsertedRow() {
        return mPrefs.getString(PREF_KEY_LAST_INSERTED_ROW, null);
    }


    //Patient data found or not

    @Override
    public void setPatientDetailsViewVisibility(boolean visibility) {
        mPrefs.edit().putBoolean(PREF_KEY_PATIENT_DETAILS_VIEW_VISIBILITY, visibility).apply();
    }

    @Override
    public boolean getPatientDetailsViewVisibility() {
        return mPrefs.getBoolean(PREF_KEY_PATIENT_DETAILS_VIEW_VISIBILITY, false);
    }

    // Critical Counters for update

    @Override
    public void setCriticalUpdate(boolean trigger) {
        mPrefs.edit().putBoolean(PREF_KEY_CRITICAL_UPDATE, trigger).apply();
    }

    @Override
    public boolean getCriticalUpdate() {
        return mPrefs.getBoolean(PREF_KEY_CRITICAL_UPDATE, false);
    }

    @Override
    public int getCriticalCounter() {
        return mPrefs.getInt(PREF_KEY_CRITICAL_COUNTER, 0);
    }

    @Override
    public void resetCriticalCounter() {
        mPrefs.edit().putInt(PREF_KEY_CRITICAL_COUNTER, AppConstants.NULL_INDEX).apply();
    }

    @Override
    public void updateCriticalCounter(int count) {
        //Toast.makeText(this.context,"update called "+count,Toast.LENGTH_SHORT).show();
        mPrefs.edit().putInt(PREF_KEY_CRITICAL_COUNTER, count).apply();
    }


    //Downloaded TC details

    @Override
    public void setLatestTcVersionCode(int viewId) {
        mPrefs.edit().putInt(PREF_KEY_LATEST_TC_VERSION_CODE, viewId).apply();
    }

    @Override
    public int getLatestTcVersionCode() {
        return mPrefs.getInt(PREF_KEY_LATEST_TC_VERSION_CODE, AppConstants.NULL_INDEX);
    }

    @Override
    public void setLatestTcVersionName(String versionName) {
        mPrefs.edit().putString(PREF_KEY_LATEST_TC_VERSION_NAME, versionName).apply();
    }

    @Override
    public String getLatestTcVersionName() {
        return mPrefs.getString(PREF_KEY_LATEST_TC_VERSION_NAME, null);
    }

    @Override
    public void setTCDownloadLink(String url) {
        mPrefs.edit().putString(PREF_KEY_TC_UPDATE_DOWNLOAD_LINK, url).apply();
    }

    @Override
    public String getTCDownloadLink() {
        return mPrefs.getString(PREF_KEY_TC_UPDATE_DOWNLOAD_LINK, null);
    }

    @Override
    public void setHmdDownloadLink(String url) {
        mPrefs.edit().putString(PREF_KEY_HMD_UPDATE_DOWNLOAD_LINK, url).apply();
    }

    @Override
    public String getHmdDownloadLink() {
        return mPrefs.getString(PREF_KEY_HMD_UPDATE_DOWNLOAD_LINK, null);
    }

    @Override
    public void setLatestHmdVersionCode(int versionName) {
        mPrefs.edit().putInt(PREF_KEY_LATEST_HMD_VERSION_CODE, versionName).apply();
    }

    @Override
    public int getLatestHmdVersionCode() {
        return mPrefs.getInt(PREF_KEY_LATEST_HMD_VERSION_CODE, AppConstants.NULL_INDEX);
    }

    @Override
    public void setLatestHmdVersionName(String versionName) {
        mPrefs.edit().putString(PREF_KEY_LATEST_HMD_VERSION_NAME, versionName).apply();
    }

    @Override
    public String getLatestHmdVersionName() {
        return mPrefs.getString(PREF_KEY_LATEST_HMD_VERSION_NAME, null);
    }

    @Override
    public void setInstalledHMDVersionCode(int hmdVersionCode) {
        mPrefs.edit().putInt(PREF_KEY_INSTALLED_HMD_VERSION_CODE, hmdVersionCode).apply();
    }

    @Override
    public int getInstalledHMDVersionCode() {
        return mPrefs.getInt(PREF_KEY_INSTALLED_HMD_VERSION_CODE, AppConstants.NULL_INDEX);
    }

    @Override
    public void setInstalledHMDVersionName(String hmdVersionName) {
        mPrefs.edit().putString(PREF_KEY_INSTALLED_HMD_VERSION_NAME, hmdVersionName).apply();
    }

    @Override
    public String getInstalledHMDVersionName() {
        return mPrefs.getString(PREF_KEY_INSTALLED_HMD_VERSION_NAME, null);
    }


    // Config Details

    @Override
    public void setHMDModelName(String hmdModelName) {
        mPrefs.edit().putString(PREF_KEY_HMD_MODEL_NAME, hmdModelName).apply();
    }

    @Override
    public String getHMDModelName() {
        return mPrefs.getString(PREF_KEY_HMD_MODEL_NAME, null);
    }

    @Override
    public void setDeviceId(String resultData) {
        mPrefs.edit().putString(PREF_DEVICE_ID, resultData).apply();
    }

    @Override
    public String getDeviceId() {
        return mPrefs.getString(PREF_DEVICE_ID, null);
    }

    @Override
    public void setOrgId(String resultData) {
        mPrefs.edit().putString(PREF_ORG_ID, resultData).apply();
    }

    @Override
    public String getOrgId() {
        return mPrefs.getString(PREF_ORG_ID, null);
    }

    @Override
    public void setSiteId(String resultData) {
        mPrefs.edit().putString(PREF_ORG_ID, resultData).apply();
    }

    @Override
    public String getSiteId() {
        return mPrefs.getString(PREF_ORG_ID, null);
    }


    // Status of device

    @Override
    public void setProductionSetUpStatus(boolean status) {
        mPrefs.edit().putBoolean(PREF_PRODUCTION_DEVICE_CONFIGURATION_STATUS, status).apply();
    }

    @Override
    public boolean getProductionSetUpStatus() {
        return mPrefs.getBoolean(PREF_PRODUCTION_DEVICE_CONFIGURATION_STATUS, false);
    }

    @Override
    public void setUserSetUpStatus(boolean status) {
        mPrefs.edit().putBoolean(PREF_USER_DEVICE_CONFIGURATION_STATUS, status).apply();
    }

    @Override
    public boolean getUserSetUpStatus() {
        return mPrefs.getBoolean(PREF_USER_DEVICE_CONFIGURATION_STATUS, false);
    }

    @Override
    public void putConfigFileCopiedStatus(boolean bool) {
        mPrefs.edit().putBoolean(PREF_CONFIG_FILE_COPIED_STATUS, bool).apply();
    }

    @Override
    public boolean getConfigFileCopiedStatus() {
        return mPrefs.getBoolean(PREF_CONFIG_FILE_COPIED_STATUS, false);
    }

    @Override
    public void putVectorFileCopiedStatus(boolean bool) {
        mPrefs.edit().putBoolean(PREF_VECTOR_FILE_COPIED_STATUS, bool).apply();
    }

    @Override
    public boolean getVectorFileCopiedStatus() {
        return mPrefs.getBoolean(PREF_VECTOR_FILE_COPIED_STATUS, false);
    }

    @Override
    public boolean getProductionTestingStatus() {
        return mPrefs.getBoolean(PREF_KEY_PRODUCTION_TESTING_STATUS, false);
    }

    @Override
    public void setProductionTestingStatus(boolean status) {
        mPrefs.edit().putBoolean(PREF_KEY_PRODUCTION_TESTING_STATUS, status).apply();
    }

    @Override
    public void setFirstTimeProductionConnection(boolean trigger) {
        mPrefs.edit().putBoolean(PREF_KEY_PRODUCTION_FIRST_TIME_CONNECTION, trigger).apply();
    }

    @Override
    public boolean getFirstTimeProductionConnection() {
        return mPrefs.getBoolean(PREF_KEY_PRODUCTION_FIRST_TIME_CONNECTION, false);
    }

    @Override
    public void setFirstTimeUserConnection(boolean trigger) {
        mPrefs.edit().putBoolean(PREF_KEY_USER_FIRST_TIME_CONNECTION, trigger).apply();
    }

    @Override
    public boolean getFirstTimeUserConnection() {
        return mPrefs.getBoolean(PREF_KEY_USER_FIRST_TIME_CONNECTION, false);
    }


    //HMD Camera Details

    @Override
    public void setLeftCameraAlpha(float alpha) {
        mPrefs.edit().putFloat(PREF_LEFT_CAMERA_ALPHA, alpha).apply();
    }

    @Override
    public float getLeftCameraAlpha() {
        return mPrefs.getFloat(PREF_LEFT_CAMERA_ALPHA, 1.5f);
    }

    @Override
    public void setRightCameraAlpha(float alpha) {
        mPrefs.edit().putFloat(PREF_RIGHT_CAMERA_ALPHA, alpha).apply();
    }

    @Override
    public float getRightCameraAlpha() {
        return mPrefs.getFloat(PREF_RIGHT_CAMERA_ALPHA, 1.5f);
    }

    @Override
    public void setLeftCameraBeta(float beta) {
        mPrefs.edit().putFloat(PREF_LEFT_CAMERA_BETA, beta).apply();
    }

    @Override
    public float getLeftCameraBeta() {
        return mPrefs.getFloat(PREF_LEFT_CAMERA_BETA, 45f);
    }

    @Override
    public void setRightCameraBeta(float beta) {
        mPrefs.edit().putFloat(PREF_RIGHT_CAMERA_BETA, beta).apply();
    }

    @Override
    public float getRightCameraBeta() {
        return mPrefs.getFloat(PREF_RIGHT_CAMERA_BETA, 45f);
    }

    @Override
    public Set<String> getLastFiveDisplayStatus() {
        return mPrefs.getStringSet(PREF_LAST_FIVE_DISPLAY_STATUS, new HashSet<>());
    }

    @Override
    public void setLastFiveDisplayStatus(Set<String> status) {
        mPrefs.edit().remove(PREF_LAST_FIVE_DISPLAY_STATUS).apply();
        mPrefs.edit().putStringSet(PREF_LAST_FIVE_DISPLAY_STATUS, status).apply();
    }

    @Override
    public String getLastFiveDisplayStatusString() {
        return mPrefs.getString(PREF_LAST_FIVE_DISPLAY_STATUS_AS_STRING, null);
    }

    @Override
    public void setLastFiveDisplayStatusString(String status) {
        mPrefs.edit().putString(PREF_LAST_FIVE_DISPLAY_STATUS_AS_STRING, status).apply();
    }

    @Override
    public void setUserPassword(String userPassword) {
        mPrefs.edit().putString(PREF_USER_PASSWORD, userPassword).apply();
    }

    @Override
    public String getUserPassword() {
        return mPrefs.getString(PREF_USER_PASSWORD, null);
    }

    @Override
    public void setAdminPassword(String adminPassword) {
        mPrefs.edit().putString(PREF_ADMIN_PASSWORD, adminPassword).apply();
    }

    @Override
    public String getAdminPassword() {
        return mPrefs.getString(PREF_ADMIN_PASSWORD, null);
    }

    @Override
    public boolean getLoginStatus() {
        return mPrefs.getBoolean(PREF_IS_SIGNED_IN, false);
    }

    @Override
    public void setLoginStatus(boolean status) {
        mPrefs.edit().putBoolean(PREF_IS_SIGNED_IN, status).apply();
    }

    @Override
    public int getRole() {
        return mPrefs.getInt(PREF_ROLE, 0);
    }

    @Override
    public void setRole(int role) {
        mPrefs.edit().putInt(PREF_ROLE, role).apply();
    }

    @Override
    public String updatedOn() {
        return mPrefs.getString(PREF_LAST_UPDATED_ON, "Last updated data not available.");
    }

    @Override
    public void setLastUpdatedOn(String date) {
        mPrefs.edit().putString(PREF_LAST_UPDATED_ON, date).apply();
    }

    @Override
    public int getDatabaseVersion() {
        return mPrefs.getInt(PREF_DATABASE_VERSION, 2);
    }

    @Override
    public void setDatabaseVersion(int version) {
        mPrefs.edit().putInt(PREF_DATABASE_VERSION, version).apply();
    }

    @Override
    public boolean getOrgSiteUpdateStatus() {
        return mPrefs.getBoolean(PREF_ORG_UPDATE, false);
    }

    @Override
    public void setOrgSiteUpdateStatus(boolean status) {
        mPrefs.edit().putBoolean(PREF_ORG_UPDATE, status).apply();
    }

    @Override
    public void setPRBCount(int count) {
        mPrefs.edit().putInt(PREF_PRB_COUNT, count).apply();
    }

    @Override
    public int getPRBCount() {
        return mPrefs.getInt(PREF_PRB_COUNT, 0);
    }

    @Override
    public void setCycleStatus(boolean status) {
        mPrefs.edit().putBoolean(PREF_CYCLE_STATUS, status).apply();
    }

    @Override
    public boolean getCycleStatus() {
        return mPrefs.getBoolean(PREF_CYCLE_STATUS, false);
    }

    @Override
    public void setPrefClear() {
        SharedPreferences.Editor editor =  mPrefs.edit();
        editor.clear();
        editor.apply();
    }

}
