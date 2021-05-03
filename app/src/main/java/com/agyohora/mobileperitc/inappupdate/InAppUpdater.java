package com.agyohora.mobileperitc.inappupdate;

import android.content.Context;
import android.util.Log;

import com.agyohora.mobileperitc.myapplication.MyApplication;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;

import org.json.JSONArray;

/**
 * Created by Invent on 19-1-18.
 * Apps owns app updater logics
 */

public class InAppUpdater implements JSONArrayRequestListener {

    private static String TAG = InAppUpdater.class.getName();

    @Override
    public void onResponse(JSONArray jsonArray) {
        final Context context = MyApplication.getInstance();

        Log.d(TAG, " response from server " + jsonArray.toString());
        /*try {
            JSONObject response = new JSONObject();
            boolean isDeviceIdFound = false;
            *//*JSONObject response = new JSONObject();
            InputStream is = context.getApplicationContext().getResources().openRawResource(R.raw.latestversiondetails);
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "utf-8"), 8); //old charset iso-8859-1
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            reader.close();*//*
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
                final int TcLatestVersion = response.optInt("TcLatestVersionCode");
                tcLatestUpdate.setLatestVersion(TcLatestVersionName);
                tcLatestUpdate.setLatestVersionCode(TcLatestVersion);
                boolean isTcUpdateAvailable = CommonUtils.isUpdateAvailable(tcInstalledUpdate, tcLatestUpdate);


                InAppUpdate hmdLatestUpdate = new InAppUpdate();
                InAppUpdate hmdInstalledUpdate = new InAppUpdate(preferencesHelper.getInstalledHMDVersionName(), preferencesHelper.getInstalledHMDVersionCode());
                final String HmdLatestVersionName = response.getString("HmdLatestVersion").trim();
                final int HmdLatestVersion = response.optInt("HmdLatestVersionCode");
                hmdLatestUpdate.setLatestVersion(HmdLatestVersionName);
                hmdLatestUpdate.setLatestVersionCode(HmdLatestVersion);
                Log.d("HmdLatestVersionName", HmdLatestVersionName);
                Log.d("HmdLatestVersion", "" + HmdLatestVersion);
                Log.d("InstalledUpdate Name", "" + hmdInstalledUpdate.getLatestVersion());
                Log.d("InstalledUpdate Version", "" + hmdInstalledUpdate.getLatestVersionCode());
                boolean isHmdUpdateAvailable = CommonUtils.isUpdateAvailable(hmdInstalledUpdate, hmdLatestUpdate);


                final AppPreferencesHelper appPreferencesHelper = new AppPreferencesHelper(context, DEVICE_PREF);

                int tcDownloadedVersion = appPreferencesHelper.getDownloadedTcVersionCode();
                boolean tcDownloadCheck = tcDownloadedVersion != TcLatestVersion;

                int hmdDownloadedVersion = appPreferencesHelper.getDownloadedHmdVersionCode();
                boolean hmdDownloadCheck = hmdDownloadedVersion != HmdLatestVersion;


                //isCritical ? (count < 5 ? appPreferencesHelper.updateCriticalCounter() : appPreferencesHelper.setCriticalUpdate(true)) : {};
                //Log.d("isMyBatch", " " + isMyBatch);
                Log.d("isTcUpdateAvailable", " " + isTcUpdateAvailable);
                Log.d("tcDownloadCheck", " " + tcDownloadCheck);
                Log.d("isHmdUpdateAvailable", " " + isHmdUpdateAvailable);
                Log.d("hmdDownloadCheck", " " + hmdDownloadCheck);
                //int count = appPreferencesHelper.getCriticalCounter();
                //Toast.makeText(context,"Count Value "+count,Toast.LENGTH_SHORT).show();
                final String dirPath = context.getCacheDir().getPath();

                final String TC_URL = response.getString("TC_URL").trim();
                final String HMD_URL = response.getString("HMD_URL").trim();


                if (isTcUpdateAvailable && tcDownloadCheck) {
                    appPreferencesHelper.resetCriticalCounter();
                    appPreferencesHelper.setCriticalUpdate(false);
                    Toast.makeText(MyApplication.getInstance(), "Update Available. Started Downloading...", Toast.LENGTH_LONG).show();
                    PRDownloader.download(TC_URL, dirPath, "latest.apk")
                            .build()
                            .start(new OnDownloadListener() {
                                @Override
                                public void onDownloadComplete() {
                                    Log.d("Download ", "Completed");
                                    appPreferencesHelper.updateCriticalCounter(0);
                                    appPreferencesHelper.setDownloadedTcVersionCode(TcLatestVersion);
                                    appPreferencesHelper.setDownloadedTcVersionName(TcLatestVersionName);
                                }

                                @Override
                                public void onError(Error error) {
                                    Toast.makeText(MyApplication.getInstance(), "Error while downloading update", Toast.LENGTH_LONG).show();
                                    appPreferencesHelper.setDownloadedTcVersionCode(0);
                                    appPreferencesHelper.setDownloadedTcVersionName("");
                                }
                            });


                } else if ((isTcUpdateAvailable)) {
                    int count = appPreferencesHelper.getCriticalCounter();
                    //Toast.makeText(context,"Count Value "+count,Toast.LENGTH_SHORT).show();
                    if (count >= 5)
                        appPreferencesHelper.setCriticalUpdate(true);

                } else {
                    new AppPreferencesHelper(context, DEVICE_PREF).resetCriticalCounter();
                    new AppPreferencesHelper(context, DEVICE_PREF).setCriticalUpdate(false);
                    Toast.makeText(context, "TC Update Not Available...", Toast.LENGTH_LONG).show();
                    Log.d(InAppUpdate.class.getName(), " Update Not Available");
                }

                if (isHmdUpdateAvailable && hmdDownloadCheck) {
                    Toast.makeText(MyApplication.getInstance(), "Hmd Update Available. Started Downloading...", Toast.LENGTH_LONG).show();
                    PRDownloader.download(HMD_URL, UPDATES_FOLDER, "update.apk")
                            .build()
                            .start(new OnDownloadListener() {
                                @Override
                                public void onDownloadComplete() {
                                    Log.d("Download ", "Completed");
                                    appPreferencesHelper.setDownloadedHmdVersionCode(HmdLatestVersion);
                                    appPreferencesHelper.setDownloadedHmdVersionName(HmdLatestVersionName);
                                }

                                @Override
                                public void onError(Error error) {
                                    Toast.makeText(MyApplication.getInstance(), "Error while downloading update", Toast.LENGTH_LONG).show();
                                    appPreferencesHelper.setDownloadedHmdVersionCode(0);
                                    appPreferencesHelper.setDownloadedHmdVersionName("");/*try {
            JSONObject response = new JSONObject();
            boolean isDeviceIdFound = false;
            *//*JSONObject response = new JSONObject();
            InputStream is = context.getApplicationContext().getResources().openRawResource(R.raw.latestversiondetails);
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    is, "utf-8"), 8); //old charset iso-8859-1
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            is.close();
            reader.close();*//*
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
                final int TcLatestVersion = response.optInt("TcLatestVersionCode");
                tcLatestUpdate.setLatestVersion(TcLatestVersionName);
                tcLatestUpdate.setLatestVersionCode(TcLatestVersion);
                boolean isTcUpdateAvailable = CommonUtils.isUpdateAvailable(tcInstalledUpdate, tcLatestUpdate);


                InAppUpdate hmdLatestUpdate = new InAppUpdate();
                InAppUpdate hmdInstalledUpdate = new InAppUpdate(preferencesHelper.getInstalledHMDVersionName(), preferencesHelper.getInstalledHMDVersionCode());
                final String HmdLatestVersionName = response.getString("HmdLatestVersion").trim();
                final int HmdLatestVersion = response.optInt("HmdLatestVersionCode");
                hmdLatestUpdate.setLatestVersion(HmdLatestVersionName);
                hmdLatestUpdate.setLatestVersionCode(HmdLatestVersion);
                Log.d("HmdLatestVersionName", HmdLatestVersionName);
                Log.d("HmdLatestVersion", "" + HmdLatestVersion);
                Log.d("InstalledUpdate Name", "" + hmdInstalledUpdate.getLatestVersion());
                Log.d("InstalledUpdate Version", "" + hmdInstalledUpdate.getLatestVersionCode());
                boolean isHmdUpdateAvailable = CommonUtils.isUpdateAvailable(hmdInstalledUpdate, hmdLatestUpdate);


                final AppPreferencesHelper appPreferencesHelper = new AppPreferencesHelper(context, DEVICE_PREF);

                int tcDownloadedVersion = appPreferencesHelper.getDownloadedTcVersionCode();
                boolean tcDownloadCheck = tcDownloadedVersion != TcLatestVersion;

                int hmdDownloadedVersion = appPreferencesHelper.getDownloadedHmdVersionCode();
                boolean hmdDownloadCheck = hmdDownloadedVersion != HmdLatestVersion;


                //isCritical ? (count < 5 ? appPreferencesHelper.updateCriticalCounter() : appPreferencesHelper.setCriticalUpdate(true)) : {};
                //Log.d("isMyBatch", " " + isMyBatch);
                Log.d("isTcUpdateAvailable", " " + isTcUpdateAvailable);
                Log.d("tcDownloadCheck", " " + tcDownloadCheck);
                Log.d("isHmdUpdateAvailable", " " + isHmdUpdateAvailable);
                Log.d("hmdDownloadCheck", " " + hmdDownloadCheck);
                //int count = appPreferencesHelper.getCriticalCounter();
                //Toast.makeText(context,"Count Value "+count,Toast.LENGTH_SHORT).show();
                final String dirPath = context.getCacheDir().getPath();

                final String TC_URL = response.getString("TC_URL").trim();
                final String HMD_URL = response.getString("HMD_URL").trim();


                if (isTcUpdateAvailable && tcDownloadCheck) {
                    appPreferencesHelper.resetCriticalCounter();
                    appPreferencesHelper.setCriticalUpdate(false);
                    Toast.makeText(MyApplication.getInstance(), "Update Available. Started Downloading...", Toast.LENGTH_LONG).show();
                    PRDownloader.download(TC_URL, dirPath, "latest.apk")
                            .build()
                            .start(new OnDownloadListener() {
                                @Override
                                public void onDownloadComplete() {
                                    Log.d("Download ", "Completed");
                                    appPreferencesHelper.updateCriticalCounter(0);
                                    appPreferencesHelper.setDownloadedTcVersionCode(TcLatestVersion);
                                    appPreferencesHelper.setDownloadedTcVersionName(TcLatestVersionName);
                                }

                                @Override
                                public void onError(Error error) {
                                    Toast.makeText(MyApplication.getInstance(), "Error while downloading update", Toast.LENGTH_LONG).show();
                                    appPreferencesHelper.setDownloadedTcVersionCode(0);
                                    appPreferencesHelper.setDownloadedTcVersionName("");
                                }
                            });


                } else if ((isTcUpdateAvailable)) {
                    int count = appPreferencesHelper.getCriticalCounter();
                    //Toast.makeText(context,"Count Value "+count,Toast.LENGTH_SHORT).show();
                    if (count >= 5)
                        appPreferencesHelper.setCriticalUpdate(true);

                } else {
                    new AppPreferencesHelper(context, DEVICE_PREF).resetCriticalCounter();
                    new AppPreferencesHelper(context, DEVICE_PREF).setCriticalUpdate(false);
                    Toast.makeText(context, "TC Update Not Available...", Toast.LENGTH_LONG).show();
                    Log.d(InAppUpdate.class.getName(), " Update Not Available");
                }

                if (isHmdUpdateAvailable && hmdDownloadCheck) {
                    Toast.makeText(MyApplication.getInstance(), "Hmd Update Available. Started Downloading...", Toast.LENGTH_LONG).show();
                    PRDownloader.download(HMD_URL, UPDATES_FOLDER, "update.apk")
                            .build()
                            .start(new OnDownloadListener() {
                                @Override
                                public void onDownloadComplete() {
                                    Log.d("Download ", "Completed");
                                    appPreferencesHelper.setDownloadedHmdVersionCode(HmdLatestVersion);
                                    appPreferencesHelper.setDownloadedHmdVersionName(HmdLatestVersionName);
                                }

                                @Override
                                public void onError(Error error) {
                                    Toast.makeText(MyApplication.getInstance(), "Error while downloading update", Toast.LENGTH_LONG).show();
                                    appPreferencesHelper.setDownloadedHmdVersionCode(0);
                                    appPreferencesHelper.setDownloadedHmdVersionName("");
                                }
                            });


                } else if ((isHmdUpdateAvailable)) {
                    Toast.makeText(context, "Hmd Update Available...", Toast.LENGTH_LONG).show();
                    Log.d(TAG, "Hmd Update Available");
                } else {
                    Toast.makeText(context, "Hmd Update Not Available...", Toast.LENGTH_LONG).show();
                    Log.d(TAG, "Hmd Update Not Available");
                }
            }
        } catch (Exception e) {
            Log.e(TAG, " " + e.getMessage());
        }

                                }
                            });


                } else if ((isHmdUpdateAvailable)) {
                    Toast.makeText(context, "Hmd Update Available...", Toast.LENGTH_LONG).show();
                    Log.d(TAG, "Hmd Update Available");
                } else {
                    Toast.makeText(context, "Hmd Update Not Available...", Toast.LENGTH_LONG).show();
                    Log.d(TAG, "Hmd Update Not Available");
                }
            }
        } catch (Exception e) {
            Log.e(TAG, " " + e.getMessage());
        }
*/

    }

    @Override
    public void onError(ANError error) {
        Log.e(TAG, " error from server " + error.toString());
    }

   /* private static IntentSender createIntentSender(Context context, int sessionId) {
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                sessionId,
                new Intent(Actions.ACTION_INSTALL_COMPLETE),
                PendingIntent.FLAG_ONE_SHOT);
        return pendingIntent.getIntentSender();
    }*/

}
