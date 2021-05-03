package com.agyohora.mobileperitc.inappupdate;

import android.content.Context;
import android.util.Log;

import com.agyohora.mobileperitc.data.preferences.AppPreferencesHelper;
import com.agyohora.mobileperitc.myapplication.MyApplication;
import com.agyohora.mobileperitc.utils.CommonUtils;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;

import org.json.JSONArray;
import org.json.JSONObject;

import static com.agyohora.mobileperitc.utils.AppConstants.DEVICE_PREF;

public class CheckForUpdates implements JSONArrayRequestListener {
    private static String TAG = CheckForUpdates.class.getName();

    @Override
    public void onResponse(JSONArray jsonArray) {
        final Context context = MyApplication.getInstance();

        Log.d(TAG, " response from server " + jsonArray.toString());
        try {
            JSONObject response = new JSONObject();
            boolean isDeviceIdFound = false;

            String dId = CommonUtils.isUserSetUpFinished(context) ? CommonUtils.getHotSpotId() : CommonUtils.getSavedDeviceId(context);

            for (int i = 0; i < jsonArray.length(); i++) {
                response = (JSONObject) jsonArray.get(i);
                String deviceId = response.getString("DeviceId");
                if (deviceId.equals(dId)) {
                    isDeviceIdFound = true;
                    break;
                }
                Log.e("Searching", "DevId " + i);
            }

            if (isDeviceIdFound) {
                Log.e(TAG, " Data Found " + response.toString());
                AppPreferencesHelper preferencesHelper = new AppPreferencesHelper(MyApplication.getInstance(), DEVICE_PREF);

                InAppUpdate tcLatestUpdate = new InAppUpdate();
                InAppUpdate tcInstalledUpdate = new InAppUpdate(CommonUtils.getAppInstalledVersion(context), CommonUtils.getAppInstalledVersionCode(context));
                final String TcLatestVersionName = response.getString("TcLatestVersion").trim();
                final int TcLatestVersionCode = response.optInt("TcLatestVersionCode");
                tcLatestUpdate.setLatestVersion(TcLatestVersionName);
                tcLatestUpdate.setLatestVersionCode(TcLatestVersionCode);
                boolean isTcUpdateAvailable = CommonUtils.isUpdateAvailable(tcInstalledUpdate, tcLatestUpdate);


                InAppUpdate hmdLatestVersion = new InAppUpdate();
                InAppUpdate hmdInstalled = new InAppUpdate(preferencesHelper.getInstalledHMDVersionName(), preferencesHelper.getInstalledHMDVersionCode());
                final String HmdLatestVersionName = response.getString("HmdLatestVersion").trim();
                final int HmdLatestVersionCode = response.optInt("HmdLatestVersionCode");
                hmdLatestVersion.setLatestVersion(HmdLatestVersionName);
                hmdLatestVersion.setLatestVersionCode(HmdLatestVersionCode);
                boolean isHmdUpdateAvailable = CommonUtils.isUpdateAvailable(hmdInstalled, hmdLatestVersion);


                final AppPreferencesHelper appPreferencesHelper = new AppPreferencesHelper(context, DEVICE_PREF);
                final String TC_URL = response.getString("TC_URL").trim();
                final String HMD_URL = response.getString("HMD_URL").trim();

                if (isHmdUpdateAvailable && isTcUpdateAvailable) {
                    appPreferencesHelper.setLatestHmdVersionCode(HmdLatestVersionCode);
                    appPreferencesHelper.setLatestHmdVersionName(HmdLatestVersionName);
                    appPreferencesHelper.setHmdDownloadLink(HMD_URL);
                    appPreferencesHelper.setLatestTcVersionCode(TcLatestVersionCode);
                    appPreferencesHelper.setLatestTcVersionName(TcLatestVersionName);
                    appPreferencesHelper.setTCDownloadLink(TC_URL);
                    //Toast.makeText(context, "Update Available, Go to Settings and click check for updates.", Toast.LENGTH_LONG).show();
                }


                Log.e(TAG,"HmdLatestVersionName " +  HmdLatestVersionName);
                Log.e(TAG,"HmdLatestVersionCode " + HmdLatestVersionCode);
                Log.e(TAG,"InstalledHmdVersionName " + hmdInstalled.getLatestVersion());
                Log.e(TAG,"InstalledHmdVersionCode " + hmdInstalled.getLatestVersionCode());
                Log.e(TAG,"isTcUpdateAvailable " + isTcUpdateAvailable);
                Log.e(TAG,"isHmdUpdateAvailable " + isHmdUpdateAvailable);
            }
        } catch (Exception e) {
            Log.e(TAG, " " + e.getMessage());
        }

    }

    @Override
    public void onError(ANError error) {
        Log.e(TAG, " error from server " + error.toString());
    }
}