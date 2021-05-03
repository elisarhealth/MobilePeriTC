package com.agyohora.mobileperitc.data.preferences;

import java.util.Set;

/**
 * Created by Invent on 23-11-17.
 */

interface PreferencesHelper {

    //current View

    void setTimeBase(long time);

    long getTimeBase();

    void clearPreferences();

    void setUSB(String usb);

    void setDeviceId(String deviceId);

    String getDeviceId();

    void setOrgId(String deviceId);

    String getOrgId();

    void setSiteId(String deviceId);

    String getSiteId();

    void setProductionSetUpStatus(boolean bool);

    boolean getProductionSetUpStatus();

    void setUserSetUpStatus(boolean bool);

    boolean getUserSetUpStatus();

    void putConfigFileCopiedStatus(boolean bool);

    boolean getConfigFileCopiedStatus();

    void putVectorFileCopiedStatus(boolean bool);

    boolean getVectorFileCopiedStatus();

    void setFirstTimeProductionConnection(boolean bool);

    boolean getFirstTimeProductionConnection();

    void setFirstTimeUserConnection(boolean bool);

    boolean getFirstTimeUserConnection();

    void setProductionTestingStatus(boolean bool);

    boolean getProductionTestingStatus();

    String getResultData();

    void setResultData(String resultData);

    String getSearchablePatientName();

    void setSearchablePatientName(String patientName);

    String getSearchablePatientMrNumber();

    void setSearchablePatientMrNumber(String mrNumber);

    String getSearchablePatientMobileNumber();

    void setSearchablePatientMobileNumber(String mobile);

    String getSearchablePatientDOB();

    void setSearchablePatientDOB(String dob);

    String getSearchablePatientSex();

    String getSearchablePatientEye();

    void setSearchablePatientEye(String eye);

    void setSearchablePatientSex(String sex);

    String getSearchableTestType();

    void setSearchableTestType(String testType);

    String getSearchableTestPattern();

    void setSearchableTestPattern(String testPattern);

    //new functions

    void setPatientFirstName(String name);

    String getPatientFirstName();

    void setPatientMrnNumber(String number);

    String getPatientMrnNumber();

    void setPatientDOB(String dob);

    String getPatientDOB();

    void setPatientSex(String sex);

    String getPatientSex();

    void setPatientMobileNumber(String number);

    String getPatientMobileNumber();

    void setPatientTestEye(String eye);

    String getPatientTestEye();

    void setTestPattern(String pattern);

    String getTestPattern();

    void setTestStrategy(String strategy);

    String getTestStrategy();

    void setTestSphericalPower(String sphericalPower);

    String getTestSphericalPower();

    void setTestCylindricalPower(String cylindricalPower);

    String getTestCylindricalPower();

    void setTestCylindricalAxis(String cylindricalAxis);

    String getTestCylindricalAxis();

    void setTestSphericalPowerInput(String sphericalPower);

    String getTestSphericalPowerInput();

    void setTestCylindricalPowerInput(String cylindricalPower);

    String getTestCylindricalPowerInput();

    void setTestCylindricalAxisInput(String cylindricalAxis);

    String getTestCylindricalAxisInput();

    void setLastInsertedRow(String id);

    String getLastInsertedRow();

    void setPatientDetailsViewVisibility(boolean bool);

    boolean getPatientDetailsViewVisibility();

    void setCriticalUpdate(boolean trigger);

    boolean getCriticalUpdate();

    void updateCriticalCounter(int count);

    int getCriticalCounter();

    void setLatestHmdVersionCode(int version);

    int getLatestHmdVersionCode();

    void setLatestHmdVersionName(String version);

    String getLatestHmdVersionName();

    void setLatestTcVersionCode(int version);

    int getLatestTcVersionCode();

    void setLatestTcVersionName(String version);

    String getLatestTcVersionName();

    void setTCDownloadLink(String url);

    String getTCDownloadLink();

    void setHmdDownloadLink(String url);

    String getHmdDownloadLink();

    void resetCriticalCounter();

    int getInstalledHMDVersionCode();

    void setInstalledHMDVersionCode(int versionCode);

    String getInstalledHMDVersionName();

    void setInstalledHMDVersionName(String versionName);

    void setHMDModelName(String versionName);

    String getHMDModelName();

    void setLeftCameraAlpha(float alpha);

    float getLeftCameraAlpha();

    void setRightCameraAlpha(float alpha);

    float getRightCameraAlpha();

    void setLeftCameraBeta(float beta);

    float getLeftCameraBeta();

    void setRightCameraBeta(float beta);

    float getRightCameraBeta();

    Set<String> getLastFiveDisplayStatus();

    void setLastFiveDisplayStatus(Set<String> status);


    String getLastFiveDisplayStatusString();

    void setLastFiveDisplayStatusString(String status);


    //For Login

    void setUserPassword(String userPassword);

    String getUserPassword();

    void setAdminPassword(String adminPassword);

    String getAdminPassword();

    boolean getLoginStatus();

    void setLoginStatus(boolean status);

    int getRole();

    void setRole(int role);

    String updatedOn();

    void setLastUpdatedOn(String date);

    int getDatabaseVersion();

    void setDatabaseVersion(int role);

    boolean getOrgSiteUpdateStatus();

    void setOrgSiteUpdateStatus(boolean status);


}
