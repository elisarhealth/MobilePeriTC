package com.agyohora.mobileperitc.myapplication;

import android.app.Application;
import android.util.Log;

import com.agyohora.mobileperitc.utils.CommonUtils;
import com.downloader.PRDownloader;
import com.downloader.PRDownloaderConfig;
import com.google.firebase.FirebaseApp;
import com.google.firebase.crashlytics.FirebaseCrashlytics;


/**
 * Created by Invent on 26-11-17.
 * class to store temporary values like cache for a session
 */

public class MyApplication extends Application {

    private static MyApplication mInstance;

    private boolean is_OTG;
    private boolean is_HMD_CONNECTED;
    private boolean is_HMD_CONNECTION_NEEDED;
   // private RxBleClient rxBleClient;


    public static synchronized MyApplication getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;
        PRDownloaderConfig prDownloaderConfig = PRDownloaderConfig.newBuilder()
                .setDatabaseEnabled(true)
                .build();
        PRDownloader.initialize(getApplicationContext(), prDownloaderConfig);
        try {
            FirebaseApp.initializeApp(this);
            FirebaseCrashlytics crashlytics = FirebaseCrashlytics.getInstance();
            crashlytics.setCrashlyticsCollectionEnabled(true);
            if (CommonUtils.isProductionSetUpFinished(this)) {
                String deviceSerialId = CommonUtils.isUserSetUpFinished(this) ? CommonUtils.getHotSpotId() : CommonUtils.getSavedDeviceId(this);
                crashlytics.setUserId(deviceSerialId);
            }
        } catch (Exception e) {
            Log.e("Exception", " " + e.getMessage());
        }
       /* rxBleClient = RxBleClient.create(this);
        RxBleClient.updateLogOptions(new LogOptions.Builder()
                .setLogLevel(LogConstants.INFO)
                .setMacAddressLogSetting(LogConstants.MAC_ADDRESS_FULL)
                .setUuidsLogSetting(LogConstants.UUIDS_FULL)
                .setShouldLogAttributeValues(true)
                .build()
        );
        RxJavaPlugins.setErrorHandler(throwable -> {
            if (throwable instanceof UndeliverableException && throwable.getCause() instanceof BleException) {
                Log.v("SampleApplication", "Suppressed UndeliverableException: " + throwable.toString());
                return; // ignore BleExceptions as they were surely delivered at least once
            }
            // add other custom handlers if needed
            throw new RuntimeException("Unexpected Throwable in RxJavaPlugins error handler", throwable);
        });

        */
    }

    /*public static RxBleClient getRxBleClient(Context context) {
        MyApplication application = (MyApplication) context.getApplicationContext();
        return application.rxBleClient;
    }*/

    public boolean is_OTG() {
        return is_OTG;
    }

    public void setIs_OTG(boolean is_OTG) {
        this.is_OTG = is_OTG;
    }

    public boolean is_HMD_CONNECTED() {
        return is_HMD_CONNECTED;
    }

    public void set_HMD_CONNECTED(boolean is_HMD_CONNECTED) {
        this.is_HMD_CONNECTED = is_HMD_CONNECTED;
    }

    public boolean is_HMD_CONNECTION_NEEDED() {
        return is_HMD_CONNECTION_NEEDED;
    }

    public void set_HMD_CONNECTION_NEED(boolean is_HMD_CONNECTION_NEEDED) {
        this.is_HMD_CONNECTION_NEEDED = is_HMD_CONNECTION_NEEDED;
    }

}
