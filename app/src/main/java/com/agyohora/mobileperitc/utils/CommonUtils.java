package com.agyohora.mobileperitc.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageInfo;
import android.content.pm.PackageInstaller;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.pdf.PdfDocument;
import android.media.ExifInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintManager;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.Html;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.print.PrintHelper;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.agyohora.mobileperitc.BuildConfig;
import com.agyohora.mobileperitc.R;
import com.agyohora.mobileperitc.actions.Actions;
import com.agyohora.mobileperitc.asynctasks.DeleteRecord;
import com.agyohora.mobileperitc.communication.WifiCommunicationManager;
import com.agyohora.mobileperitc.data.database.AppDatabase;
import com.agyohora.mobileperitc.data.database.entity.PatientTestResult;
import com.agyohora.mobileperitc.data.network.ApiEndPoint;
import com.agyohora.mobileperitc.data.preferences.AppPreferencesHelper;
import com.agyohora.mobileperitc.inappupdate.InAppUpdate;
import com.agyohora.mobileperitc.inappupdate.InAppVersion;
import com.agyohora.mobileperitc.myapplication.MyApplication;
import com.agyohora.mobileperitc.store.Interpolation;
import com.agyohora.mobileperitc.store.PerimetryObject_V2;
import com.agyohora.mobileperitc.store.Store;
import com.agyohora.mobileperitc.store.StoreTransmitter;
import com.agyohora.mobileperitc.ui.MainActivity;
import com.agyohora.mobileperitc.ui.UserProfileActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.google.common.io.ByteStreams;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.crashlytics.FirebaseCrashlytics;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.Deflater;

import es.dmoral.toasty.Toasty;

import static com.agyohora.mobileperitc.communication.WifiCommunicationManager.commInitialized;
import static com.agyohora.mobileperitc.data.database.DatabaseInitializer.getResultData;
import static com.agyohora.mobileperitc.store.Store.activeView_number;
import static com.agyohora.mobileperitc.store.Store.isHotSpotOn;
import static com.agyohora.mobileperitc.ui.MainActivity.resetCalibrationData;
import static com.agyohora.mobileperitc.utils.AppConstants.DEVICE_PREF;
import static com.agyohora.mobileperitc.utils.AppConstants.PREF_NAME;
import static com.agyohora.mobileperitc.utils.Constants.BUG_FIX_CLICK_ACK_FOLDER;
import static com.agyohora.mobileperitc.utils.Constants.BUG_FIX_LOGS_FOLDER;
import static com.agyohora.mobileperitc.utils.Constants.CHRONOMETER_LOGS_FOLDER;
import static com.agyohora.mobileperitc.utils.Constants.DATABASE_RESTORE_LOGS_FOLDER;
import static com.agyohora.mobileperitc.utils.Constants.DISPLAY_LOGS_FOLDER;
import static com.agyohora.mobileperitc.utils.Constants.MY_DEBUG_LOGS_FOLDER;
import static com.agyohora.mobileperitc.utils.Constants.SYS_LOGS_FOLDER;
import static com.agyohora.mobileperitc.utils.Constants.TD_LOGS_FOLDER;
import static com.agyohora.mobileperitc.utils.Constants.TEN_DASH_TWO;
import static com.agyohora.mobileperitc.utils.Constants.TEST_TIME_LOGS_FOLDER;
import static com.agyohora.mobileperitc.utils.Constants.UPDATES_FOLDER;

/**
 * Created by Invent on 23-12-17.
 */

@SuppressWarnings({"TryWithIdenticalCatches", "AccessStaticViaInstance"})
public final class CommonUtils {
    private static final String TAG = "CommonUtils";
    private static View mappedView;

    private CommonUtils() {
        // This utility class is not publicly instantiable
    }

    /*
     * Used for Study Instances
     * */

    public static String getUUID() {
        return UUID.randomUUID().toString();
        // return String.format("%040d", new BigInteger(UUID.randomUUID().toString().replace("-", ""), 16));
    }

    public static boolean nullCheck(String stringToCheck) {
        //Log.d("stringToCheck", " " + stringToCheck);
        return stringToCheck != null && !stringToCheck.isEmpty();
    }

    /*
     * Test results will be saved as blob in local DB. Blob is a kind of encoded bytes.
     * To change the blob into JsonObject this method will be used
     * */

    public static JSONObject bytesToJsonObject(byte[] bytes) {
        try {
            String data = new String(Base64.decode(bytes, Base64.NO_WRAP));
            writeToFile(data, "TrackMe");
            return new JSONObject(data);
        } catch (JSONException e) {
            Log.e("bytesToJsonObject", "Exception " + e.getMessage());
            return null;
        }
    }

    /*
     * Minimum age of the patient has to be ten years.
     * And we will be restricting the minimum age selection on date picker dialog
     * */

    public static long tenYearsBack() {
        Date currentDate = new Date();
        long milliseconds = (long) 10 * 365 * 24 * 60 * 60 * 1000;
        return currentDate.getTime() - milliseconds;
    }

    /*
     * UI related functions
     * startKiosk is to start the kiosk mode with delay 1500ms.
     * Without time delay error is thrown it seems to be a restriction in kiosk
     * */

    public static void startKiosk(final Activity activity) {
        try {
            ActivityManager am = (ActivityManager) activity.getSystemService(
                    Context.ACTIVITY_SERVICE);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                if (am != null && !am.isInLockTaskMode()) {
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.postDelayed(() -> {
                        try {
                            activity.startLockTask();
                        } catch (Exception exception) {
                            Log.v("KioskActivity", "startLockTask - Invalid task, not in foreground");
                        }
                    }, 1500);
                    //startLockTask();
                }
            } else {
                if (am != null && am.getLockTaskModeState() ==
                        ActivityManager.LOCK_TASK_MODE_NONE) {
                    try {
                        activity.startLockTask();
                    } catch (Exception exception) {
                        Log.v("KioskActivity", "startLockTask - Invalid task, not in foreground");
                    }
                }
            }
        } catch (NullPointerException e) {
            Log.e("Exception", "startKiosk in commonutils " + e.getMessage());
        }
    }

    public static void stopKiosk(Activity activity) {
        try {
            ActivityManager am = (ActivityManager) activity.getSystemService(
                    Context.ACTIVITY_SERVICE);
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                if (am != null && am.isInLockTaskMode()) {
                    Log.d("InitSettingsActivity ", "Stopping Kiosk");
                    activity.stopLockTask();
                }
            }
        } catch (IllegalStateException e) {
            Log.e("IllegalStateException", " " + e.getMessage());
        }
    }

    public static boolean isKioskNotActive(ActivityManager am) {
        try {

            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
                Log.d("Within", "M");
                return !am.isInLockTaskMode();
            } else {
                if (am.getLockTaskModeState() ==
                        ActivityManager.LOCK_TASK_MODE_NONE) {
                    return true;
                }
            }
        } catch (NullPointerException e) {
            Log.e("NullPointerException", " " + e.getMessage());
        }
        return false;

    }

    public static void hideKeypad(View view, Context context) {
        try {
            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (imm != null) {
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        } catch (Exception e) {
            Log.e("HidePad", " " + e.getMessage());
        }
    }

    public static void showBigToast(Toast toast) {
        ViewGroup group = (ViewGroup) toast.getView();
        TextView messageTextView = (TextView) group.getChildAt(0);
        messageTextView.setTextSize(20);
        toast.show();
    }

    public static void showToasty(Context context, String message, boolean isShort, char type) {
        switch (type) {
            case 'E':
                Toasty.error(context, message, isShort ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG, true).show();
                break;
            case 'S':
                Toasty.success(context, message, isShort ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG, true).show();
                break;
            case 'I':
                Toasty.info(context, message, isShort ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG, true).show();
                break;
            case 'W':
                Toasty.warning(context, message, isShort ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG, true).show();
                break;
            case 'N':
                Toasty.normal(context, message, isShort ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG).show();
                break;
        }
    }

    private static Point getRealScreenSize(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = null;
        if (windowManager != null) {
            display = windowManager.getDefaultDisplay();
        }
        Point size = new Point();

        if (Build.VERSION.SDK_INT >= 17) {
            display.getRealSize(size);
        } else if (Build.VERSION.SDK_INT >= 14) {
            try {
                size.x = (Integer) Display.class.getMethod("getRawWidth").invoke(display);
                size.y = (Integer) Display.class.getMethod("getRawHeight").invoke(display);
            } catch (IllegalAccessException e) {
                Log.e("Exception", e.getMessage());
            } catch (InvocationTargetException e) {
                Log.e("Exception", e.getMessage());
            } catch (NoSuchMethodException e) {
                Log.e("Exception", e.getMessage());
            }
        }

        return size;
    }

    /*\
     * New IP logic froms serial number
     * */

    public static String getDetailsFromSerialNumber(String serial, String tag) {
        Log.e("serial", " " + serial);
        String returnValue = "";
        String deviceName;
        String serialNumber;
        String serialId;
        String assemblyDate;
        String staticIp;
        String deviceId;
        if (serial != null && !serial.equals("")) {
            char[] P = serial.toCharArray();
            if (P.length == 12) {
                deviceName = findModel(P[0], P[2]);
                serialId = generateSerialId(P[1], P[3], P[5], P[7], P[9]);
                serialNumber = generateSerialNumber(P[1], P[3], P[5], P[7], P[9]);
                assemblyDate = findCheckPostDate(P[4], P[6], P[8], P[10], P[11]);
                staticIp = calculateStaticIp(serialNumber);
                deviceId = generateDeviceID(serial);

                Log.e("DetailsFromSerialNumber", "deviceName " + deviceName);
                Log.e("DetailsFromSerialNumber", "serialId " + serialId);
                Log.e("DetailsFromSerialNumber", "serialNumber " + serialNumber);
                Log.e("DetailsFromSerialNumber", "assemblyDate " + assemblyDate);
                Log.e("DetailsFromSerialNumber", "staticIp " + staticIp);
                Log.e("DetailsFromSerialNumber", "deviceId " + deviceId);

                switch (tag) {
                    case "DeviceName":
                        returnValue = deviceName;
                        break;
                    case "SerialNumber":
                        returnValue = serialNumber;
                        break;
                    case "AssemblyDate":
                        returnValue = assemblyDate;
                        break;
                    case "StaticIP":
                        returnValue = staticIp;
                        break;
                    case "DeviceId":
                        returnValue = deviceId;
                        break;
                    default:
                        returnValue = "";
                        break;
                }
            } else {
                returnValue = "";
            }
        }
        Log.e("DetailsFromSerialNumber", "returned value is " + returnValue);
        return returnValue;

    }

    private static String findModel(char p0, char p2) {
        String deviceName = "";
        if (p0 == 'I') {
            deviceName = "AVA";
        }
        if (p2 == '1') {
            deviceName = deviceName + "100";
        }
        return deviceName;
    }

    private static String generateSerialId(char p1, char p3, char p5, char p7, char p9) {
        return "" + p1 + p3 + p5 + p7 + p9;
    }

    private static String findCheckPostDate(char p4, char p6, char p8, char p10, char p11) {
        String strDate = "" + p4 + p6 + p8 + p10 + p11;
        Log.e("generateSerialId", "strDate " + strDate);
        int foundDate = Integer.parseInt(strDate, 16);
        return "" + foundDate;
    }

    private static String generateSerialNumber(char p1, char p3, char p5, char p7, char p9) {
        long serial = Long.parseLong("" + p1 + p3 + p5 + p7 + p9, 16);
        return String.format("%04d", serial);
    }

    private static String calculateStaticIp(String serialNumber) {
        int serial = Integer.parseInt(serialNumber);
        Log.e("calculateStaticIp", " serialNumber " + serialNumber);
        int modulo = serial % 155;
        Log.e("calculateStaticIp", " Moduolo " + modulo);
        modulo = 99 + modulo;
        Log.e("calculateStaticIp", " after additionModuolo " + modulo);
        return "192.168.43." + modulo;
    }

    private static String generateDeviceID(String serial) {
        return "AVA" + "-" + serial + "-PVT-NETWORK";
    }

    /*
     * Helper Dialogues
     * */

    public static void showContactDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.myDialog));
        builder.setTitle("Contact Customer Care");
        builder.setView(R.layout.contact_us_dialog)
                .setCancelable(true)
                .setPositiveButton("OK", (dialog, id) -> {
                    //do things
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public static void showAcknowlegmentDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.myDialog));
        builder.setTitle("Are you sure?");
        builder.setMessage("By clicking YES, you are acknowledging that Hotpsot Name and Password is created manually.")
                .setCancelable(false)
                .setPositiveButton("YES", (dialog, id) -> {
                    Actions.startHMDSync();
                })
                .setNegativeButton("No", (dialog, id) -> {
                    //do things
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public static void deleteReportConfirmationDialog(final Context context, final Activity activity, final String resultId, final MenuItem menuItem, final boolean closeActivity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.myDialog));
        builder.setTitle("Delete Report?");
        builder.setMessage("This action can't be undone!")
                .setCancelable(true)
                .setPositiveButton("Delete", (dialog, id) -> {
                    //int records = DatabaseInitializer.deleteRecord(AppDatabase.getAppDatabase(context), resultId);

                    new DeleteRecord(bool -> {
                        if (bool) {
                            // activity.finish();
                            menuItem.setEnabled(false);
                            menuItem.getIcon().setAlpha(130);
                            Toast.makeText(context, "Record Deleted!", Toast.LENGTH_SHORT).show();
                            if (closeActivity)
                                activity.finish();
                            else
                                Actions.goHomeImmediately();
                        } else
                            Toast.makeText(context, "Error in Deleting Record!", Toast.LENGTH_SHORT).show();
                        return bool;
                    }, context).execute(resultId);

                })
                .setNegativeButton("No", (dialog, which) -> {
                    //do nothing
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public static void abortTestConfirmationDialog(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.myDialog));
        builder.setTitle("Abort Test?");
        builder.setMessage("This action cant't be undone!")
                .setCancelable(false)
                .setPositiveButton("Abort", (dialog, id) -> abortTest(context))
                .setNegativeButton("No", (dialog, which) -> {
                    //do nothing
                });
        AlertDialog alert = builder.create();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alert.show();
    }

    public static void abortCalibrationConfirmationDialog(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.myDialog));
        builder.setTitle("Abort Calibration?");
        builder.setMessage("This action cant't be undone!")
                .setCancelable(false)
                .setPositiveButton("Abort", (dialog, id) -> Actions.abortCalibration())
                .setNegativeButton("No", (dialog, which) -> {
                    //do nothing
                });
        AlertDialog alert = builder.create();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alert.show();
    }

    public static void abortTest(Context context) {
        AppPreferencesHelper appPreferencesHelper = new AppPreferencesHelper(context, PREF_NAME);
        appPreferencesHelper.setTimeBase(0);
        appPreferencesHelper.setPatientDetailsViewVisibility(false);
        appPreferencesHelper.clearPreferences();
        resetCalibrationData();
        MainActivity.isChronometerRunning = false;
        Actions.abortTest();
    }


    /*
     * Internet Utils
     * */

    public static boolean haveNetworkConnection(Context context) {
        boolean haveConnectedWifi = false;
        boolean haveConnectedMobile = false;

        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] netInfo = new NetworkInfo[0];
        if (cm != null) {
            netInfo = cm.getAllNetworkInfo();
        }
        for (NetworkInfo ni : netInfo) {
            if (ni.getTypeName().equalsIgnoreCase("WIFI"))
                if (ni.isConnected())
                    haveConnectedWifi = true;
            if (ni.getTypeName().equalsIgnoreCase("MOBILE"))
                if (ni.isConnected())
                    haveConnectedMobile = true;
        }
        return haveConnectedWifi || haveConnectedMobile;
    }

    public static void initiateNetworkOptions(final Context context, final Activity activity, String caller) {
        final Bundle bundle = new Bundle();
        bundle.putString("data", "NA");

        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(activity, R.style.myDialog));
        builder.setTitle("No Internet!")
                .setMessage("Connect to internet and try again.")
                .setCancelable(false)
                .setPositiveButton("WiFi", (dialog, which) -> {
                    if (caller.equalsIgnoreCase("applicable")) {
                        Actions.sendStopIdleTimers();
                        new Handler().postDelayed(() -> stopCommAndOpenWifi(bundle, activity, context), 2000);
                    } else {
                        Log.e("initiateNetworkOptions", "NA");
                        stopCommAndOpenWifi(bundle, activity, context);
                    }
                })
                .setNegativeButton("Mobile Data", (dialog, which) -> {
                    Intent intent = new Intent();
                    intent.setComponent(new ComponentName("com.android.settings", "com.android.settings.Settings$DataUsageSummaryActivity"));
                    //StoreTransmitter.doCommFunction(StoreTransmitter.COMM_FUNCTION_STOP, bundle);
                    //switchOffHotSpot(context);
                    activity.startActivity(intent);
                }).setNeutralButton("Cancel", (dialog, which) -> {
            if (activeView_number == R.layout.check_for_updates_screen) {
                Actions.beginTestControllerSettings();
                ;
            }

        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    private static void stopCommAndOpenWifi(Bundle bundle, Activity activity, Context context) {
        StoreTransmitter.doCommFunction(StoreTransmitter.COMM_FUNCTION_STOP, bundle);
        MyApplication.getInstance().set_HMD_CONNECTION_NEED(false);
        MyApplication.getInstance().set_HMD_CONNECTED(false);
        Toast.makeText(context, "Hotspot Turned Off", Toast.LENGTH_SHORT).show();
        activity.startActivity(new Intent(WifiManager.ACTION_PICK_WIFI_NETWORK));
    }

    public static boolean isMobileDataOn(Context context) {
        boolean mobileDataEnabled = false; // Assume disabled
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            Class cmClass = Class.forName(cm.getClass().getName());
            Method method = cmClass.getDeclaredMethod("getMobileDataEnabled");
            method.setAccessible(true); // Make the method callable
            // get the setting for "mobile data"
            mobileDataEnabled = (Boolean) method.invoke(cm);
        } catch (Exception e) {
            return false;
        }
        return mobileDataEnabled;
    }

    public static void openMobileData(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(activity, R.style.myDialog));
        builder.setTitle("Alert")
                .setMessage("2G Mobile Data should be turned off to proceed the test!")
                .setCancelable(false)
                .setPositiveButton("Turn Off Mobile Data", (dialog, which) -> {
                    Intent intent = new Intent();
                    intent.setComponent(new ComponentName("com.android.settings", "com.android.settings.Settings$DataUsageSummaryActivity"));
                    activity.startActivity(intent);
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * To get device consuming netowkr type is 2g,3g,4g
     *
     * @param context
     * @return "2g","3g","4g" as a String based on the network type
     */
    public static String getNetworkType(Context context) {
        TelephonyManager mTelephonyManager = (TelephonyManager)
                context.getSystemService(Context.TELEPHONY_SERVICE);
        int networkType = mTelephonyManager.getNetworkType();
        switch (networkType) {
            case TelephonyManager.NETWORK_TYPE_GPRS:
            case TelephonyManager.NETWORK_TYPE_EDGE:
            case TelephonyManager.NETWORK_TYPE_CDMA:
            case TelephonyManager.NETWORK_TYPE_1xRTT:
            case TelephonyManager.NETWORK_TYPE_IDEN:
                return "2g";
            case TelephonyManager.NETWORK_TYPE_UMTS:
            case TelephonyManager.NETWORK_TYPE_EVDO_0:
            case TelephonyManager.NETWORK_TYPE_EVDO_A:
                /**
                 From this link https://en.wikipedia.org/wiki/Evolution-Data_Optimized ..NETWORK_TYPE_EVDO_0 & NETWORK_TYPE_EVDO_A
                 EV-DO is an evolution of the CDMA2000 (IS-2000) standard that supports high data rates.

                 Where CDMA2000 https://en.wikipedia.org/wiki/CDMA2000 .CDMA2000 is a family of 3G[1] mobile technology standards for sending voice,
                 data, and signaling data between mobile phones and cell sites.
                 */
            case TelephonyManager.NETWORK_TYPE_HSDPA:
            case TelephonyManager.NETWORK_TYPE_HSUPA:
            case TelephonyManager.NETWORK_TYPE_HSPA:
            case TelephonyManager.NETWORK_TYPE_EVDO_B:
            case TelephonyManager.NETWORK_TYPE_EHRPD:
            case TelephonyManager.NETWORK_TYPE_HSPAP:
                //Log.d("Type", "3g");
                //For 3g HSDPA , HSPAP(HSPA+) are main  networktype which are under 3g Network
                //But from other constants also it will 3g like HSPA,HSDPA etc which are in 3g case.
                //Some cases are added after  testing(real) in device with 3g enable data
                //and speed also matters to decide 3g network type
                //https://en.wikipedia.org/wiki/4G#Data_rate_comparison
                return "3g";
            case TelephonyManager.NETWORK_TYPE_LTE:
                //No specification for the 4g but from wiki
                //I found(LTE (Long-Term Evolution, commonly marketed as 4G LTE))
                //https://en.wikipedia.org/wiki/LTE_(telecommunication)
                return "4g";
            default:
                return "Notfound";
        }
    }


    public static void installUpdateDialog(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.myDialog));
        builder.setTitle("Critical Update!")
                .setMessage("You have ignored the updates for five times. Please update the app to continue")
                .setCancelable(false)
                .setPositiveButton("Install", (dialog, which) -> {
                    packageInstaller(context);
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public static Boolean isOnline() {
        try {
            Process process = java.lang.Runtime.getRuntime().exec("ping -c 1 www.google.com");
            int returnVal = process.waitFor();
            return (returnVal == 0);
        } catch (Exception e) {
            Log.e("IsOnline", " " + e.getMessage());
        }
        return false;
    }

    public static boolean switchOffHotSpot(Context context) {
        Log.d("switchOffHotSpot", "Called");
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WifiCommunicationManager.stopTethering(context);
        } else {
            WifiManager wifimanager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
            WifiConfiguration wificonfiguration = null;
            try {
                Method wifi = wifimanager.getClass().getDeclaredMethod("isWifiApEnabled");
                wifi.setAccessible(true);
                boolean stat = (Boolean) wifi.invoke(wifimanager);
                // if WiFi is on, turn it off
                if (stat) {
                    wifimanager.setWifiEnabled(false);
                }
                Method method = wifimanager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
                method.invoke(wifimanager, wificonfiguration, !stat);
                commInitialized = false;
                return true;
            } catch (Exception e) {
                Log.e("switchOffHotSpot", " Exception LOLLIPOP_MR1 " + e.getMessage());
            }
        }

        return false;
    }

    public static boolean isItUserLoggedIn(Context context) {
        AppPreferencesHelper appPreferencesHelper = new AppPreferencesHelper(context, DEVICE_PREF);
        return appPreferencesHelper.getRole() == 2;
    }

    public static void switchToAdminProfileToDeleteReport(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(activity, R.style.myDialog));
        builder.setTitle("Delete Permission Denied!");
        builder.setMessage("Go to your profile and switch to Admin Profile.");
        builder.setCancelable(false)
                .setPositiveButton("Open Profile", (dialog, id) -> {
                    activity.finish();
                    activity.startActivity(new Intent(activity, UserProfileActivity.class));
                })
                .setNegativeButton("Cancel", (dialog, id) -> {
                });
        AlertDialog alert = builder.create();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alert.show();

    }

    public static boolean switchOffHotSpotAndStartWifi(Context context, Activity activity, Intent intent) {
        Log.d("switchOffHotSpot", "Called");
        WifiManager wifimanager = (WifiManager) context.getSystemService(context.WIFI_SERVICE);
        WifiConfiguration wificonfiguration = null;
        try {
            Method wifi = wifimanager.getClass().getDeclaredMethod("isWifiApEnabled");
            wifi.setAccessible(true);
            boolean stat = (Boolean) wifi.invoke(wifimanager);
            // if WiFi is on, turn it off
            if (stat) {
                wifimanager.setWifiEnabled(false);
            }
            Method method = wifimanager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            method.invoke(wifimanager, wificonfiguration, !stat);
            commInitialized = false;
            activity.startActivity(intent);
            return true;
        } catch (Exception e) {
            Log.e("switchOffHotSpot", " Exception " + e.getMessage());
            return false;

        }

    }

    public static void showHMDDisconnectedDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.myDialog));
        builder.setTitle("Connection lost!")
                .setMessage("No connection with HMD!")
                .setPositiveButton("Okay", (dialog, which) -> {
                })
                .setCancelable(true);
        AlertDialog alert = builder.create();
        alert.show();
    }

    public static void showEiModuleDisconnected(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.myDialog));
        builder.setTitle("Alert")
                .setMessage("Interface Connection lost, Please restart the device.Apologies for the inconvenience")
                .setPositiveButton("Abort", (dialog, which) -> {
                    Actions.abortTest();
                })
                .setCancelable(false);
        AlertDialog alert = builder.create();
        alert.show();
    }

    public static void rebootHMD(final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(activity, R.style.myDialog));
        builder.setTitle("Action Alert")
                .setMessage("Are you sure?")
                .setNegativeButton("Cancel", (dialog, which) -> {

                })
                .setPositiveButton("Reboot", (dialog, which) -> {
                    Actions.beginReboot();
                    Store.newTestVisibility = false;
                    Actions.goHomeImmediately();
                })
                .setCancelable(false);
        AlertDialog alert = builder.create();
        alert.show();
    }

    /*
     * Date Utils
     * */

    public static String currentYear() {
        return "" + Calendar.getInstance().get(Calendar.YEAR);
    }

    public static SimpleDateFormat getDateFormat() {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.US);
    }

    public static Format getDateTimeFormat() {
        return new SimpleDateFormat("dd-MMMM-yyyy hh:mm a", Locale.US);
    }

    public static String getPdfReportFooterDate(String createdDate) {
        Log.d("Before Format", " " + createdDate);
        Date date;
        String formattedDate = "";
        DateFormat originalFormat = new SimpleDateFormat("dd-MMMM-yyyy hh:mm a", Locale.US);
        DateFormat targetFormat = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss", Locale.US);
        try {
            date = originalFormat.parse(createdDate);
            formattedDate = targetFormat.format(date);
        } catch (Exception e) {
            Log.e("CreatedDateException", "" + e.getMessage());
        }

        return formattedDate;
    }

    public static Format getStandardFormat() {
        return new SimpleDateFormat("EEEE dd MMMM yyyy hh:mm a", Locale.US);
    }

    public static Format getPatientCopyFormat() {
        return new SimpleDateFormat("dd MMMM yyyy hh:mm a", Locale.US);
    }

    public static Format getPatientCopyFooterFormat() {
        return new SimpleDateFormat("dd MMMM yyyy", Locale.US);
    }

    public static String durationCalculation(String startTime, String endTime) {
        if (Integer.parseInt(startTime) == 0) {
            int min1 = 15;
            int max1 = 25;

            int min2 = 5;
            int max2 = 10;

            Random rand = new Random();
            int value = rand.nextInt(50);

            int stTime = rand.nextInt((max1 - min1) + 1) + min1;
            int edTime = rand.nextInt((max2 - min2) + 1) + min2;
            return "" + (stTime - edTime);
        } else {
            DateFormat df = new SimpleDateFormat("hhmmss", Locale.US);
            Date date1;
            Date date2;
            long diff = 0;
            try {
                date1 = df.parse(startTime);
                date2 = df.parse(endTime);
                diff = date2.getTime() - date1.getTime();
            } catch (ParseException e) {
                FirebaseCrashlytics.getInstance().recordException(e);
            }
            int hours = (int) (diff / 3600000);
            int minutes = (int) (diff - hours * 3600000) / 60000;
            int seconds = (int) (diff - hours * 3600000 - minutes * 60000) / 1000;
            return minutes + ":" + seconds;
        }
    }

    public static String randomTestDuration() {
        int min1 = 15;
        int max1 = 25;

        int min2 = 5;
        int max2 = 10;

        Random rand = new Random();
        int value = rand.nextInt(50);

        int startTime = rand.nextInt((max1 - min1) + 1) + min1;
        int endTime = rand.nextInt((max2 - min2) + 1) + min2;
        return "" + (startTime - endTime);
    }

    public static String duringCalculationFromMillis(long millis) {
        int h = (int) (millis / 3600000);
        int m = (int) (millis - h * 3600000) / 60000;
        int s = (int) (millis - h * 3600000 - m * 60000) / 1000;
        String minute = m < 10 ? "0" + m : "" + m;
        String seconds = s < 10 ? "0" + s : "" + s;
        return h > 0 ? h + ":" + minute + ":" + seconds : minute + ":" + seconds;
    }

    public static void showUpdateAvailableDialog(final Context context) {

        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.myDialog));
        builder.setIcon(R.mipmap.ic_launcher_round_new);
        builder.setTitle("Mobile Perimeter")
                .setMessage("\n\t\tUpdate Available")
                .setCancelable(false)
                .setPositiveButton("Install", (dialog, id) -> packageInstaller(context))
                .setNegativeButton("Later", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        int count = new AppPreferencesHelper(context, DEVICE_PREF).getCriticalCounter();
                        ++count;
                        new AppPreferencesHelper(context, DEVICE_PREF).updateCriticalCounter(count);
                    }
                });
        AlertDialog alert = builder.create();
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            alert.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        alert.show();
    }

    public static String getCurrentDate() {
        return new SimpleDateFormat(AppConstants.DATE_FORMAT, Locale.US).format(new Date());
    }

    public static String getCurrentTime() {
        return new SimpleDateFormat(AppConstants.TIME_FORMAT, Locale.US).format(new Date());
    }

    public static boolean isValidJson(String jsonString) {
        try {
            new JSONObject(jsonString);
            return true;
        } catch (Exception exception) {
            writeToFile(jsonString, "somename");
            return false;
        }
    }

    /*
     * Report view utils
     * */

    public static Bundle createReportBundle(String id, String createdDateTime, PerimetryObject_V2.FinalPerimetryResultObject finalPerimetryTestFinalResultObject, String duration) {
        ArrayList<String> dt_new_result = new ArrayList<>();
        ArrayList<String> dt_result_sensitivity = new ArrayList<>();
        ArrayList<String> dt_result_deviation = new ArrayList<>();
        ArrayList<String> dt_result_probabilityDeviationValue = new ArrayList<>();
        ArrayList<String> dt_result_generalizedDefectCorrectedSensitivityDeviationValue = new ArrayList<>();
        ArrayList<String> dt_result_generalizedDefectCorrectedSensitivityDeviationProbabilityValue = new ArrayList<>();
        ArrayList<String> dt_result_seen = new ArrayList<>();
        Bundle bundle = new Bundle();
        bundle.putString("ResultId", id);
        bundle.putString("TestedAt", createdDateTime);
        Interpolation interpolation = new Interpolation();
        double[][][] quadrants = interpolation.getGreyScaleVals(finalPerimetryTestFinalResultObject);
        bundle.putSerializable("quadrants", quadrants);
        bundle.putString("setPatientTestEyeVal", finalPerimetryTestFinalResultObject.Series.Laterality.equals("L") ? "Left Eye" : "Right Eye");
        bundle.putString("setPatientName", finalPerimetryTestFinalResultObject.Patient.PatientName);
        Log.d("Name", " " + finalPerimetryTestFinalResultObject.Patient.PatientName);
        bundle.putString("setPatientSexVal", finalPerimetryTestFinalResultObject.Patient.PatientSex);
        bundle.putString("setPatientMrnNumberVal", finalPerimetryTestFinalResultObject.Patient.PatientUID);
        bundle.putString("setPatientDOBVal", finalPerimetryTestFinalResultObject.Patient.PatientBirthDate);
        bundle.putString("setPatientTestStrategyVal", finalPerimetryTestFinalResultObject.Series.StrategySequence.CodeMeaning);
        String pattern = finalPerimetryTestFinalResultObject.Series.PatternSequence.CodeMeaning;
        bundle.putString("setPatientTestPatternVal", pattern);
        int FL_Denominator = finalPerimetryTestFinalResultObject.Measurements.VisualFieldStaticPerimetryTestReliability.FixationSequence.FixationCheckedQuantity;
        int FL_Numerator = finalPerimetryTestFinalResultObject.Measurements.VisualFieldStaticPerimetryTestReliability.FixationSequence.PatientNotProperlyFixatedQuantity;

        int FP_Denominator = finalPerimetryTestFinalResultObject.Measurements.VisualFieldStaticPerimetryTestReliability.VisualFieldCatchTrialSequence.PositiveCatchTrialsQuantity;
        int FP_Numerator = finalPerimetryTestFinalResultObject.Measurements.VisualFieldStaticPerimetryTestReliability.VisualFieldCatchTrialSequence.FalsePositivesQuantity;

        int FN_Denominator = finalPerimetryTestFinalResultObject.Measurements.VisualFieldStaticPerimetryTestReliability.VisualFieldCatchTrialSequence.NegativeCatchTrialsQuantity;
        int FN_Numerator = finalPerimetryTestFinalResultObject.Measurements.VisualFieldStaticPerimetryTestReliability.VisualFieldCatchTrialSequence.FalseNegativesQuantity;
        bundle.putString("FL", FL_Numerator + "/" + FL_Denominator); //+ "%");
        bundle.putString("FP", FP_Numerator + "/" + FP_Denominator);// + "%");
        bundle.putString("FN", FN_Numerator + "/" + FN_Denominator); //+ "%");
        dt_new_result.clear();
        dt_result_sensitivity.clear();
        int dt_new_result_length = finalPerimetryTestFinalResultObject.Measurements.VisualFieldStaticPerimetryTestMeasurements.VisualFieldTestPointSequence.length;
        for (int i = 0; i < dt_new_result_length; i++) {
            String xVal, yVal, xyVal;
            int x = (int) finalPerimetryTestFinalResultObject.Measurements.VisualFieldStaticPerimetryTestMeasurements.VisualFieldTestPointSequence[i].VisualFieldTestPointXCoordinate;
            int y = (int) finalPerimetryTestFinalResultObject.Measurements.VisualFieldStaticPerimetryTestMeasurements.VisualFieldTestPointSequence[i].VisualFieldTestPointYCoordinate;
            int s = (int) finalPerimetryTestFinalResultObject.Measurements.VisualFieldStaticPerimetryTestMeasurements.VisualFieldTestPointSequence[i].SensitivityValue;
            long tempTotalDeviation = Math.round(finalPerimetryTestFinalResultObject.Measurements.VisualFieldStaticPerimetryTestMeasurements.VisualFieldTestPointSequence[i].VisualFieldTestPointNormalSequence.AgeCorrectedSensitivityDeviationValue);
            int deviation = (int) tempTotalDeviation;
            double probabilityDeviationValue = finalPerimetryTestFinalResultObject.Measurements.VisualFieldStaticPerimetryTestMeasurements.VisualFieldTestPointSequence[i].VisualFieldTestPointNormalSequence.AgeCorrectedSensitivityProbabilityDeviationValue;
            long tempPatternlDeviation = Math.round(finalPerimetryTestFinalResultObject.Measurements.VisualFieldStaticPerimetryTestMeasurements.VisualFieldTestPointSequence[i].VisualFieldTestPointNormalSequence.GeneralizedDefectCorrectedSensitivityDeviationValue);
            int generalizedDefectCorrectedSensitivityDeviationValue = (int) tempPatternlDeviation;
            double generalizedDefectCorrectedSensitivityDeviationProbabilityValue = finalPerimetryTestFinalResultObject.Measurements.VisualFieldStaticPerimetryTestMeasurements.VisualFieldTestPointSequence[i].VisualFieldTestPointNormalSequence.GeneralizedDefectCorrectedSensitivityDeviationProbabilityValue;
            String seen = finalPerimetryTestFinalResultObject.Measurements.VisualFieldStaticPerimetryTestMeasurements.VisualFieldTestPointSequence[i].StimulusResults;
            if (!(x == 0 & y == 0)) {
                if (pattern.equals("24-2") || pattern.equals("30-2")) {
                    xVal = x > 0 ? "x" + ((x + 3) / 6) : "-x" + ((-x + 3) / 6);
                    yVal = y > 0 ? "y" + ((y + 3) / 6) : "-y" + ((-y + 3) / 6);
                    xyVal = xVal.concat(yVal);
                } else {
                    xVal = x > 0 ? "x" + ((x + 1) / 2) : "-x" + ((-x + 1) / 2);
                    yVal = y > 0 ? "y" + ((y + 1) / 2) : "-y" + ((-y + 1) / 2);
                    xyVal = xVal.concat(yVal);
                }
                dt_new_result.add(xyVal);
                dt_result_sensitivity.add("" + s);
                dt_result_deviation.add("" + deviation);
                dt_result_probabilityDeviationValue.add("" + probabilityDeviationValue);
                Log.d("Seen", "" + seen);
                dt_result_seen.add("" + seen);
                dt_result_generalizedDefectCorrectedSensitivityDeviationValue.add("" + generalizedDefectCorrectedSensitivityDeviationValue);
                dt_result_generalizedDefectCorrectedSensitivityDeviationProbabilityValue.add("" + generalizedDefectCorrectedSensitivityDeviationProbabilityValue);
            }
        }

        String backGround = "Background: " + finalPerimetryTestFinalResultObject.Measurements.VisualFieldStaticPerimeteryTestParameters.BackgroundIlluminationColorCodeSequence.CodeMeaning;
        bundle.putString("BackgroundIlluminationColorCodeSequence", backGround);

        String md = Double.toString(finalPerimetryTestFinalResultObject.Measurements.VisualFieldStaticPerimetryTestResults.ResultNormalSequence.GlobalDeviationFromNormal);
        //md  md.substring(0, 2);
        String prob = " P < 0.5";
        //DecimalFormat dec = new DecimalFormat("#0.00");
        //String psd = dec.format(Double.toString(finalPerimetryTestFinalResultObject.Measurements.VisualFieldStaticPerimetryTestResults.ResultNormalSequence.LocalizedDeviationFromNormal));
        String psd = (Double.toString(finalPerimetryTestFinalResultObject.Measurements.VisualFieldStaticPerimetryTestResults.ResultNormalSequence.LocalizedDeviationFromNormal));
        //psd = psd.substring(0, 2);
        String meanDeviation = md;// + "<font color=#ff0000>" + prob + "</font>";
        bundle.putString("meanDeviation", meanDeviation);
        bundle.putString("psd", psd);

        if (finalPerimetryTestFinalResultObject.TestResultsInfo.ResultNormalSequence.GlobalDeviationProbabilityNormalsFlag.equalsIgnoreCase("yes")) {
            // bundle.putString("GHT", finalPerimetryTestFinalResultObject.TestResultsInfo.VisualFieldGlobalResultsIndexSequence[0].DataObservationSequence.TextValue);
            bundle.putString("GHT", finalPerimetryTestFinalResultObject.TestResultsInfo.VisualFieldGlobalResultsIndexSequence[0].DataObservationSequence.TextValue);
            bundle.putDouble("MDProbability", finalPerimetryTestFinalResultObject.TestResultsInfo.ResultNormalSequence.GlobalDeviationProbabilitySequence.GlobalDeviationProbability);
            bundle.putDouble("PDProbability", finalPerimetryTestFinalResultObject.TestResultsInfo.ResultNormalSequence.LocalDeviationProbabilitySequence.LocalDeviationProbability);
        } else {
            bundle.putString("GHT", " ");
            bundle.putDouble("MDProbability", 0);
            bundle.putDouble("PDProbability", 0);
        }
        bundle.putString("VFI", finalPerimetryTestFinalResultObject.TestResultsInfo.VisualFieldGlobalResultsIndexSequence[1].DataObservationSequence.TextValue);
        double foveaValue = finalPerimetryTestFinalResultObject.Measurements.VisualFieldStaticPerimetryTestMeasurements.FovealSensitivity;
        String fovea = finalPerimetryTestFinalResultObject.Measurements.VisualFieldStaticPerimetryTestMeasurements.FovealSensitivityMeasured;
        String foveaText = fovea.equalsIgnoreCase("yes") ? "Fovea: " + foveaValue : "Fovea: Off";
        bundle.putString("fovea", foveaText);

        String cylindricalAxis = Double.toString(finalPerimetryTestFinalResultObject.Measurements.OphthalmicPatientClinicalInformationAndTestLensParameters.OphthalmicPatientClinicalInformationLeftEyeSequence.RefractiveParametersUsedOnPatientSequence.CylinderAxis);
        String cylindricalPower = Double.toString(finalPerimetryTestFinalResultObject.Measurements.OphthalmicPatientClinicalInformationAndTestLensParameters.OphthalmicPatientClinicalInformationLeftEyeSequence.RefractiveParametersUsedOnPatientSequence.CylindricalLensPower);
        String sphericalPower = Double.toString(finalPerimetryTestFinalResultObject.Measurements.OphthalmicPatientClinicalInformationAndTestLensParameters.OphthalmicPatientClinicalInformationLeftEyeSequence.RefractiveParametersUsedOnPatientSequence.SphericalLensPower);
        String visualAcuity = "RX: " + sphericalPower + " DS " + cylindricalPower + " DC X " + cylindricalAxis;
        bundle.putString("visualAcuity", visualAcuity);

        //bundle.putString("TestDuration", durationCalculation(finalPerimetryTestFinalResultObject.Series.PerformedProcedureStepStartTime, finalPerimetryTestFinalResultObject.Series.PerformedProcedureStepEndTime));
        bundle.putString("TestDuration", duration);


        String dia = "Pupil Diameter: " + finalPerimetryTestFinalResultObject.Measurements.OphthalmicPatientClinicalInformationAndTestLensParameters.OphthalmicPatientClinicalInformationRightEyeSequence.PupilSize;
        bundle.putString("PupilSize", dia);

        bundle.putStringArrayList("dt_new_result", dt_new_result);
        bundle.putStringArrayList("dt_result_seen", dt_result_seen);
        bundle.putStringArrayList("dt_result_sensitivity", dt_result_sensitivity);
        bundle.putStringArrayList("dt_result_deviation", dt_result_deviation);
        bundle.putStringArrayList("dt_result_probabilityDeviationValue", dt_result_probabilityDeviationValue);
        bundle.putStringArrayList("dt_result_generalizedDefectCorrectedSensitivityDeviationValue", dt_result_generalizedDefectCorrectedSensitivityDeviationValue);
        bundle.putStringArrayList("dt_result_generalizedDefectCorrectedSensitivityDeviationProbabilityValue", dt_result_generalizedDefectCorrectedSensitivityDeviationProbabilityValue);
        Format formatter = CommonUtils.getDateTimeFormat();
        String createdDate = formatter.format(new Date());
        bundle.putString("CreatedDate", createdDate);
        return bundle;
    }

    public static View createGreyScale(double[][][] quad, Activity activity, String eye) {
        int[] right = new int[]{1, 0, 3, 2};
        int[] left = new int[]{0, 1, 2, 3};
        int[] quadrant;
        String tag;
        if (eye.equals("Left Eye"))
            quadrant = left;
        else
            quadrant = right;
        View greyScale = activity.getLayoutInflater().inflate(R.layout.greyscale, null);
        for (int i = 0; i < quad.length; i++) {
            for (int j = 0; j < quad[i].length; j++) {
                for (int k = 0; k < quad[i][j].length; k++) {
                    int val = (int) quad[i][k][j];
                    tag = "q" + (quadrant[i] + 1) + "r" + (j + 1) + "c" + (k + 1);
                    Log.i("createGreyScale", tag + " " + val);
                    ImageView view = greyScale.findViewById(R.id.tableParent).findViewWithTag(tag);
                    if (val != 1000) {
                        if (val <= 0) {
                            view.setImageResource(R.drawable.gs_less_than_zero);
                        } else if (val <= 5) {
                            view.setImageResource(R.drawable.gs_one_to_five);
                        } else if (val <= 10) {
                            view.setImageResource(R.drawable.gs_six_to_ten);
                        } else if (val <= 15) {
                            view.setImageResource(R.drawable.gs_eleven_to_fifteen);
                        } else if (val <= 20) {
                            view.setImageResource(R.drawable.gs_sixteen_to_twenty);
                        } else if (val <= 25) {
                            view.setImageResource(R.drawable.gs_twentyone_to_twentyfive);
                        } else if (val <= 30) {
                            view.setImageResource(R.drawable.gs_twentysix_to_thirty);
                        } else if (val <= 35) {
                            view.setImageResource(R.drawable.gs_thirtyone_to_thirtyfive);
                        } else if (val <= 40) {
                            view.setImageResource(R.drawable.gs_thirtysix_to_forty_new);
                        }
                    }
                }
            }
        }
        return greyScale;
    }

    public static View createGreyScaleForTenDashTwo(double[][][] quad, Activity activity, String eye) {
        int[] right = new int[]{1, 0, 3, 2};
        int[] left = new int[]{0, 1, 2, 3};
        int[] quadrant;
        String tag;
        if (eye.equals("Left Eye"))
            quadrant = left;
        else
            quadrant = right;
        View greyScale = activity.getLayoutInflater().inflate(R.layout.greyscale, null);
        for (int i = 0; i < quad.length; i++) {
            for (int j = 0; j < quad[i].length; j++) {
                for (int k = 0; k < quad[i][j].length; k++) {
                    int val = (int) quad[i][k][j];
                    tag = "q" + (quadrant[i] + 1) + "r" + (j + 1) + "c" + (k + 1);
                    Log.i("createGreyScale", tag + " " + val);
                    ImageView view = greyScale.findViewById(R.id.tableParent).findViewWithTag(tag);
                    if (val != 1000) {
                        if (val <= 0) {
                            view.setImageResource(R.drawable.gs_less_than_zero);
                        } else if (val < 8) {
                            view.setImageResource(R.drawable.gs_one_to_five);
                        } else if (val == 8 || val == 9) {
                            view.setImageResource(R.drawable.gs_six_to_ten);
                        } else if (val <= 15) {
                            view.setImageResource(R.drawable.gs_eleven_to_fifteen);
                        } else if (val <= 20) {
                            view.setImageResource(R.drawable.gs_sixteen_to_twenty);
                        } else if (val <= 25) {
                            view.setImageResource(R.drawable.gs_twentyone_to_twentyfive);
                        } else if (val <= 30) {
                            view.setImageResource(R.drawable.gs_twentysix_to_thirty);
                        } else if (val <= 35) {
                            view.setImageResource(R.drawable.gs_thirtyone_to_thirtyfive);
                        } else if (val <= 40) {
                            view.setImageResource(R.drawable.gs_thirtysix_to_forty_new);
                        }
                    }
                }
            }
        }
        return greyScale;
    }

    public static View getMappedIconView(Activity activity, String pattern, String eye, final ArrayList<String> coordinates, final ArrayList<String> pointValues) {
        switch (pattern) {
            case "24-2":
                if (eye.equals("Left Eye")) {
                    mappedView = activity.getLayoutInflater().inflate(R.layout.fifty_four_left_white_without_marker, null);
                } else {
                    mappedView = activity.getLayoutInflater().inflate(R.layout.fifty_four_right_white_without_marker, null);
                }
                break;
            case "30-2":
                if (eye.equals("Left Eye")) {
                    mappedView = activity.getLayoutInflater().inflate(R.layout.seventy_six_left_white_without_marker, null);
                } else {
                    mappedView = activity.getLayoutInflater().inflate(R.layout.seventy_six_right_white_without_marker, null);
                }
                break;
            case "10-2":
                if (eye.equals("Left Eye")) {
                    mappedView = activity.getLayoutInflater().inflate(R.layout.ten_cross_two_left_white_without_marker, null);
                } else {
                    mappedView = activity.getLayoutInflater().inflate(R.layout.ten_cross_two_right_white_without_marker, null);
                }
                break;
            case "Macula":
                if (eye.equals("Left Eye")) {
                    mappedView = activity.getLayoutInflater().inflate(R.layout.macula_left_white_without_marker, null);
                } else {
                    mappedView = activity.getLayoutInflater().inflate(R.layout.macula_right_white_without_marker, null);
                }
                break;
        }

        if (!coordinates.isEmpty()) {
            for (int i = 0; i < coordinates.size(); i++) {
                float point = Float.parseFloat(pointValues.get(i));
                Log.d("getMappedIconView", "coordinates " + coordinates.get(i) + " i " + i + " point " + point);
                ((TextView) mappedView.findViewById(R.id.tableParent).findViewWithTag(coordinates.get(i))).setText("");
                if (point < 0.5f) {
                    Log.d("getMappedIconView", "Less than 0.5");
                    ((TextView) mappedView.findViewById(R.id.tableParent).findViewWithTag(coordinates.get(i))).setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_05, 0, 0, 0);
                } else if (point >= 0.5f && point < 1f) {
                    Log.d("getMappedIconView", "great than or equal to 0.5 and less than 1");
                    ((TextView) mappedView.findViewById(R.id.tableParent).findViewWithTag(coordinates.get(i))).setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_1, 0, 0, 0);
                } else if (point >= 1f && point < 2f) {
                    Log.d("getMappedIconView", "great than or equal to 1 and less than 2");
                    ((TextView) mappedView.findViewById(R.id.tableParent).findViewWithTag(coordinates.get(i))).setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_2, 0, 0, 0);
                } else if (point >= 2f && point < 5f) {
                    Log.d("getMappedIconView", "great than or equal to 2 and less than 5");
                    ((TextView) mappedView.findViewById(R.id.tableParent).findViewWithTag(coordinates.get(i))).setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_5, 0, 0, 0);
                } else if (point > 5f) {
                    Log.d("getMappedIconView", "great than or equal to 5");
                    ((TextView) mappedView.findViewById(R.id.tableParent).findViewWithTag(coordinates.get(i))).setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_0, 0, 0, 0);
                }
            }

        }
        if (pattern.equals("24-2")) {
            ((TextView) mappedView.findViewById(R.id.tableParent).findViewWithTag("-x3y1")).setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            ((TextView) mappedView.findViewById(R.id.tableParent).findViewWithTag("-x3-y1")).setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        } else if (pattern.equals("30-2")) {
            ((TextView) mappedView.findViewById(R.id.tableParent).findViewWithTag("-x3y1")).setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
            ((TextView) mappedView.findViewById(R.id.tableParent).findViewWithTag("-x3-y1")).setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
        }
        return mappedView;
    }

    public static View getDefectivePointColoredGraph(Activity activity, String pattern, String eye, final ArrayList<String> coordinates, final ArrayList<String> pointValues) {
        Drawable redView, orangeView, yellowView, greenView, whiteView;
        switch (pattern) {
            case "24-2":
                if (eye.equals("Left Eye")) {
                    mappedView = activity.getLayoutInflater().inflate(R.layout.fifty_four_left_patient, null);
                } else {
                    mappedView = activity.getLayoutInflater().inflate(R.layout.fifty_four_right_patient, null);
                }
                break;
            case "30-2":
                if (eye.equals("Left Eye")) {
                    mappedView = activity.getLayoutInflater().inflate(R.layout.seventy_six_left_patient, null);
                } else {
                    mappedView = activity.getLayoutInflater().inflate(R.layout.seventy_six_right_patient, null);
                }
                break;
            case "10-2":
                if (eye.equals("Left Eye")) {
                    mappedView = activity.getLayoutInflater().inflate(R.layout.ten_cross_two_left_patient, null);
                } else {
                    mappedView = activity.getLayoutInflater().inflate(R.layout.ten_cross_two_right_patient, null);
                }
                break;
            case "Macula":
                if (eye.equals("Left Eye")) {
                    mappedView = activity.getLayoutInflater().inflate(R.layout.macula_left_patient, null);
                } else {
                    mappedView = activity.getLayoutInflater().inflate(R.layout.macula_right_patient, null);
                }
                break;
        }
        redView = activity.getDrawable(R.drawable.circular_view_red_small);
        orangeView = activity.getDrawable(R.drawable.circular_view_orange_small);
        yellowView = activity.getDrawable(R.drawable.circular_view_yellow_small);
        greenView = activity.getDrawable(R.drawable.circular_view_green_small);
        whiteView = activity.getDrawable(R.drawable.circular_view_white_small);


        /*if (!coordinates.isEmpty()) {
            for (int i = 0; i < coordinates.size(); i++) {
                float point = Integer.parseInt(pointValues.get(i));
                //((TextView) mappedView.findViewById(R.id.tableParent).findViewWithTag(coordinates.get(i))).setText("");
                if (point < 0.5)
                    ((ImageView) mappedView.findViewById(R.id.tableParent).findViewWithTag(coordinates.get(i))).setBackground(redView);
                else if (point > 0.5 && point < 1)
                    ((ImageView) mappedView.findViewById(R.id.tableParent).findViewWithTag(coordinates.get(i))).setBackground(orangeView);
                else if (point > 1 && point < 2)
                    ((ImageView) mappedView.findViewById(R.id.tableParent).findViewWithTag(coordinates.get(i))).setBackground(yellowView);
                else if (point > 2 && point < 5)
                    ((ImageView) mappedView.findViewById(R.id.tableParent).findViewWithTag(coordinates.get(i))).setBackground(greenView);
                else
                    ((ImageView) mappedView.findViewById(R.id.tableParent).findViewWithTag(coordinates.get(i))).setBackground(whiteView);
            }

        }*/

        if (!coordinates.isEmpty()) {
            for (int i = 0; i < coordinates.size(); i++) {
                int width = mappedView.findViewById(R.id.tableParent).findViewWithTag(coordinates.get(i)).getWidth();
                width = width / 2;
                float point = Float.parseFloat(pointValues.get(i));
                ((TextView) mappedView.findViewById(R.id.tableParent).findViewWithTag(coordinates.get(i))).setText("");
                if (point < 0.5)
                    ((TextView) mappedView.findViewById(R.id.tableParent).findViewWithTag(coordinates.get(i))).setCompoundDrawablesWithIntrinsicBounds(width, 0, R.drawable.circular_view_red_small, 0);
                else if (point >= 0.5 && point < 1)
                    ((TextView) mappedView.findViewById(R.id.tableParent).findViewWithTag(coordinates.get(i))).setCompoundDrawablesWithIntrinsicBounds(width, 0, R.drawable.circular_view_orange_small, 0);
                else if (point >= 1 && point < 2)
                    ((TextView) mappedView.findViewById(R.id.tableParent).findViewWithTag(coordinates.get(i))).setCompoundDrawablesWithIntrinsicBounds(width, 0, R.drawable.circular_view_green_small, 0);
                else if (point >= 2 && point < 5)
                    ((TextView) mappedView.findViewById(R.id.tableParent).findViewWithTag(coordinates.get(i))).setCompoundDrawablesWithIntrinsicBounds(width, 0, R.drawable.circular_view_yellow_small, 0);
                else
                    ((TextView) mappedView.findViewById(R.id.tableParent).findViewWithTag(coordinates.get(i))).setCompoundDrawablesWithIntrinsicBounds(width, 0, R.drawable.circular_view_white_small, 0);
            }

        }

        /*if (!coordinates.isEmpty()) {
            for (int i = 0; i < coordinates.size(); i++) {
                float point = Integer.parseInt(pointValues.get(i));
                ((TextView) mappedView.findViewById(R.id.tableParent).findViewWithTag(coordinates.get(i))).setText("");
                if (point < 0.5)
                    ((TextView) mappedView.findViewById(R.id.tableParent).findViewWithTag(coordinates.get(i))).setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.circular_view_red_small, 0,0);
                else if (point > 0.5 && point < 1)
                    ((TextView) mappedView.findViewById(R.id.tableParent).findViewWithTag(coordinates.get(i))).setCompoundDrawablesWithIntrinsicBounds( 0, R.drawable.circular_view_orange_small, 0,0);
                else if (point > 1 && point < 2)
                    ((TextView) mappedView.findViewById(R.id.tableParent).findViewWithTag(coordinates.get(i))).setCompoundDrawablesWithIntrinsicBounds( 0, R.drawable.circular_view_yellow_small, 0,0);
                else if (point > 2 && point < 5)
                    ((TextView) mappedView.findViewById(R.id.tableParent).findViewWithTag(coordinates.get(i))).setCompoundDrawablesWithIntrinsicBounds( 0, R.drawable.circular_view_green_small, 0,0);
                else
                    ((TextView) mappedView.findViewById(R.id.tableParent).findViewWithTag(coordinates.get(i))).setCompoundDrawablesWithIntrinsicBounds( 0, R.drawable.circular_view_red_small, 0,0);
            }

        }*/

        /*if (!coordinates.isEmpty()) {
            for (int i = 0; i < coordinates.size(); i++) {
                float point = Integer.parseInt(pointValues.get(i));
                //((TextView) mappedView.findViewById(R.id.tableParent).findViewWithTag(coordinates.get(i))).setText("");
                if (point < 0.5)
                    ((TextView) mappedView.findViewById(R.id.tableParent).findViewWithTag(coordinates.get(i))).setBackgroundResource(R.drawable.circular_view_orange_small);
                else if (point > 1 && point < 2)
                    ((TextView) mappedView.findViewById(R.id.tableParent).findViewWithTag(coordinates.get(i))).setBackgroundResource(R.drawable.circular_view_yellow_small);
                else if (point > 2 && point < 5)
                    ((TextView) mappedView.findViewById(R.id.tableParent).findViewWithTag(coordinates.get(i))).setBackgroundResource(R.drawable.circular_view_green_small);
                else
                    ((TextView) mappedView.findViewById(R.id.tableParent).findViewWithTag(coordinates.get(i))).setBackgroundResource(R.drawable.circular_view_white_small);
            }

        }*/

        return mappedView;
    }

    public static View getScreeningDefectivePointGraph(Activity activity, String pattern, String eye, final ArrayList<String> coordinates, final ArrayList<String> pointValues) {
        switch (pattern) {
            case "24-2":
                if (eye.equals("Left Eye")) {
                    mappedView = activity.getLayoutInflater().inflate(R.layout.fifty_four_left_patient, null);
                } else {
                    mappedView = activity.getLayoutInflater().inflate(R.layout.fifty_four_right_patient, null);
                }
                break;
            case "30-2":
                if (eye.equals("Left Eye")) {
                    mappedView = activity.getLayoutInflater().inflate(R.layout.seventy_six_left_patient, null);
                } else {
                    mappedView = activity.getLayoutInflater().inflate(R.layout.seventy_six_right_patient, null);
                }
                break;
            case "10-2":
                if (eye.equals("Left Eye")) {
                    mappedView = activity.getLayoutInflater().inflate(R.layout.ten_cross_two_left_patient, null);
                } else {
                    mappedView = activity.getLayoutInflater().inflate(R.layout.ten_cross_two_right_patient, null);
                }
                break;
            case "Macula":
                if (eye.equals("Left Eye")) {
                    mappedView = activity.getLayoutInflater().inflate(R.layout.macula_left_patient, null);
                } else {
                    mappedView = activity.getLayoutInflater().inflate(R.layout.macula_right_patient, null);
                }
                break;
        }

        //if (strategy.equalsIgnoreCase("screening")) {
        if (!pointValues.isEmpty()) {
            for (int i = 0; i < pointValues.size(); i++) {
                int width = mappedView.findViewById(R.id.tableParent).findViewWithTag(coordinates.get(i)).getWidth();
                width = width / 2;
                ((TextView) mappedView.findViewById(R.id.tableParent).findViewWithTag(coordinates.get(i))).setText("");
                if (pointValues.get(i).equals("SEEN"))
                    ((TextView) mappedView.findViewById(R.id.tableParent).findViewWithTag(coordinates.get(i))).setCompoundDrawablesWithIntrinsicBounds(width, 0, R.drawable.circular_view_white_small, 0);
                else
                    ((TextView) mappedView.findViewById(R.id.tableParent).findViewWithTag(coordinates.get(i))).setCompoundDrawablesWithIntrinsicBounds(width, 0, R.drawable.circular_view_red_small, 0);
            }

        }
        // }

        return mappedView;
    }

    public static View getDefectivePointGreyGraph(Activity activity, String pattern, String eye, final ArrayList<String> coordinates, final ArrayList<String> pointValues, String strategy) {
        pattern = pattern.trim();
        eye = eye.trim();
        View mappedView = null;
        // mappedView = activity.getLayoutInflater().inflate(R.layout.fifty_four_left_patient_transparent_temp, null);
        Log.d("Pattern", " " + pattern);
        Log.d("Eye", " " + eye);
        switch (pattern) {
            case "24-2":
                if (eye.equals("Left Eye")) {
                    Log.d("Tracking", "Left Eye 24-2");
                    //mappedView = activity.getLayoutInflater().inflate(R.layout.fifty_four_left_patient_taj_view, null);
                    mappedView = activity.getLayoutInflater().inflate(R.layout.fifty_four_left_patient_taj_view, null);
                } else {
                    Log.d("Tracking", "Right Eye 24-2");
                    //mappedView = activity.getLayoutInflater().inflate(R.layout.fifty_four_right_patient_transparent, null);
                    mappedView = activity.getLayoutInflater().inflate(R.layout.fifty_four_right_temp, null);
                }
                break;
            case "30-2":
                if (eye.equals("Left Eye")) {
                    Log.d("Tracking", "Left Eye 30-2");
                    mappedView = activity.getLayoutInflater().inflate(R.layout.seventy_six_left_patient_transparent, null);
                } else {
                    Log.d("Tracking", "Right Eye 30-2");
                    mappedView = activity.getLayoutInflater().inflate(R.layout.seventy_six_right_patient_transparent, null);
                }
                break;
            case "10-2":
                if (eye.equals("Left Eye")) {
                    Log.d("Tracking", "Left Eye 10-2");
                    mappedView = activity.getLayoutInflater().inflate(R.layout.ten_cross_two_left_patient_transparent, null);
                } else {
                    Log.d("Tracking", "Right Eye 10-2");
                    mappedView = activity.getLayoutInflater().inflate(R.layout.ten_cross_two_right_patient_transparent, null);
                }
                break;
            case "Macula":
                if (eye.equals("Left Eye")) {
                    Log.d("Tracking", "Left Eye Macula");
                    mappedView = activity.getLayoutInflater().inflate(R.layout.macula_left_patient_transparent, null);
                } else {
                    Log.d("Tracking", "Right Eye Macula");
                    mappedView = activity.getLayoutInflater().inflate(R.layout.macula_right_patient_transparent, null);
                }
                break;
            default:
                Log.d("Tracking", "Default");
                mappedView = activity.getLayoutInflater().inflate(R.layout.fifty_four_left_patient_transparent_temp, null);
                break;
        }
       /* int first = activity.getResources().getColor(R.color.first);
        int second = activity.getResources().getColor(R.color.second);
        int third = activity.getResources().getColor(R.color.third);
        int fourth = activity.getResources().getColor(R.color.fourth);*/
        ColorDrawable colorDrawable = new ColorDrawable(ContextCompat.getColor(activity, R.color.first));
        ColorDrawable first = new ColorDrawable(ContextCompat.getColor(activity, R.color.first));
        ColorDrawable second = new ColorDrawable(ContextCompat.getColor(activity, R.color.second));
        ColorDrawable third = new ColorDrawable(ContextCompat.getColor(activity, R.color.third));
        ColorDrawable fourth = new ColorDrawable(ContextCompat.getColor(activity, R.color.fourth));
        //Drawable colorDrawable = activity.getResources().getDrawable(R.drawable.unseen_zep);
        Log.d("strategy", " " + strategy);
        if (strategy.equalsIgnoreCase("screening")) {
            Drawable drawable = activity.getDrawable(R.drawable.patient_view_grey);
            if (!pointValues.isEmpty()) {
                for (int i = 0; i < pointValues.size(); i++) {
                    if (!pointValues.get(i).equals("SEEN")) {
                        mappedView.findViewById(R.id.tableParent).findViewWithTag(coordinates.get(i)).setBackground(colorDrawable);
                    }

                }
            }
        } else {
            if (!coordinates.isEmpty()) {
                for (int i = 0; i < coordinates.size(); i++) {
                    float point = Float.parseFloat(pointValues.get(i));
                    //( mappedView.findViewById(R.id.tableParent).findViewWithTag(coordinates.get(i))).setText("");
                    if (point < 0.5)
                        mappedView.findViewById(R.id.tableParent).findViewWithTag(coordinates.get(i)).setBackground(first);
                    else if (point >= 0.5 && point < 1)
                        mappedView.findViewById(R.id.tableParent).findViewWithTag(coordinates.get(i)).setBackground(second);
                    else if (point >= 1 && point < 2)
                        mappedView.findViewById(R.id.tableParent).findViewWithTag(coordinates.get(i)).setBackground(third);
                    else if (point >= 2 && point < 5)
                        mappedView.findViewById(R.id.tableParent).findViewWithTag(coordinates.get(i)).setBackground(fourth);
                }

            }

        }
        return mappedView;
    }

    public static View getMappedView(Activity activity, String pattern, String eye, final ArrayList<String> coordinates, final ArrayList<String> pointValues) {
        switch (pattern) {
            case "24-2":
                if (eye.equals("Left Eye")) {
                    mappedView = activity.getLayoutInflater().inflate(R.layout.fifty_four_left_white_with_marker, null);
                    Log.e("SelectedLayout", "fifty_four_left_white_with_marker");
                } else {
                    mappedView = activity.getLayoutInflater().inflate(R.layout.fifty_four_right_white_with_marker, null);
                    Log.e("SelectedLayout", "fifty_four_right_white_with_marker");
                }
                break;
            case "30-2":
                if (eye.equals("Left Eye")) {
                    mappedView = activity.getLayoutInflater().inflate(R.layout.seventy_six_left_white_with_marker, null);
                    Log.e("SelectedLayout", "seventy_six_left_white_with_marker");
                } else {
                    mappedView = activity.getLayoutInflater().inflate(R.layout.seventy_six_right_white_with_marker, null);
                    Log.e("SelectedLayout", "seventy_six_right_white_with_marker");
                }
                break;
            case "10-2":
                if (eye.equals("Left Eye")) {
                    mappedView = activity.getLayoutInflater().inflate(R.layout.ten_cross_two_left_white_with_marker, null);
                } else {
                    mappedView = activity.getLayoutInflater().inflate(R.layout.ten_cross_two_right_white_with_marker, null);
                }
                break;
            case "Macula":
                if (eye.equals("Left Eye")) {
                    mappedView = activity.getLayoutInflater().inflate(R.layout.macula_left_white_with_marker, null);
                } else {
                    mappedView = activity.getLayoutInflater().inflate(R.layout.macula_right_white_with_marker, null);
                }
                break;
        }

        if (!coordinates.isEmpty()) {
            Log.e("Size of Result ", " " + coordinates.size() + " Sensitivity " + pointValues.size());
            if (!pattern.equals("10-2")) {
                for (int i = 0; i < coordinates.size(); i++) {
                    TextView textView = mappedView.findViewById(R.id.tableParent).findViewWithTag(coordinates.get(i));
                    textView.setTextColor(Color.BLACK);
                    textView.setText(pointValues.get(i));
                    Log.d("getMapppedView", "Co-ord " + coordinates.get(i) + " values " + pointValues.get(i));
                }
            } else {
                for (int i = 0; i < coordinates.size(); i++) {
                    Log.e("getMapppedView", "Co-ord " + coordinates.get(i) + " values " + pointValues.get(i));
                    TextView textView = mappedView.findViewById(R.id.tableParent).findViewWithTag(coordinates.get(i));
                    textView.setTextColor(Color.BLACK);
                    try {
                        int val = Integer.parseInt(pointValues.get(i));
                        if (val > 0 && val < 8) {
                            textView.setText("<8");
                        } else {
                            textView.setText(pointValues.get(i));
                        }
                        Log.d("getMapppedView", "Co-ord " + coordinates.get(i) + " values " + pointValues.get(i));
                    } catch (NumberFormatException e) {
                        Log.e("Exception", " " + e.getMessage());
                    }
                }
            }
            // ((TextView) mappedView.findViewById(R.id.tableParent).findViewWithTag(coordinates.get(i))).setText(pointValues.get(i));
            if (pattern.equals("24-2") || (pattern.equals("30-2"))) {
                mappedView.findViewById(R.id.tableParent).findViewWithTag("blindspot").setBackground(activity.getDrawable(R.drawable.traingle_with_white_bg));
                //((TextView) mappedView.findViewById(R.id.tableParent).findViewWithTag("blindspot")).setBackground(activity.getDrawable(R.drawable.axis_horizontal_with_triangle_transparent_bg));
            }
        }
        return mappedView;
    }

    public static View getMappedViewLargeText(Activity activity, String pattern, String eye, final ArrayList<String> coordinates, final ArrayList<String> pointValues) {
        Log.d("getMappedViewLargeText", "Eye " + eye);
        Log.d("getMappedViewLargeText", "Pattern " + pattern);
        switch (pattern) {
            case "24-2":
                if (eye.equals("Left Eye")) {
                    mappedView = activity.getLayoutInflater().inflate(R.layout.fifty_four_left_white_without_marker, null);
                } else {
                    mappedView = activity.getLayoutInflater().inflate(R.layout.fifty_four_right_white_without_marker, null);
                }
                break;
            case "30-2":
                if (eye.equals("Left Eye")) {
                    Log.d("getMappedViewLargeText", "30-2 Left Eye");
                    mappedView = activity.getLayoutInflater().inflate(R.layout.seventy_six_left_white_without_marker, null);
                } else {
                    Log.d("getMappedViewLargeText", "30-2 Right Eye");
                    mappedView = activity.getLayoutInflater().inflate(R.layout.seventy_six_right_white_without_marker, null);
                }
                break;
            case "10-2":
                if (eye.equals("Left Eye")) {
                    mappedView = activity.getLayoutInflater().inflate(R.layout.ten_cross_two_left_white_without_marker, null);
                } else {
                    mappedView = activity.getLayoutInflater().inflate(R.layout.ten_cross_two_right_white_without_marker, null);
                }
                break;
            case "Macula":
                if (eye.equals("Left Eye")) {
                    mappedView = activity.getLayoutInflater().inflate(R.layout.macula_left_white_without_marker, null);
                } else {
                    mappedView = activity.getLayoutInflater().inflate(R.layout.macula_right_white_without_marker, null);
                }
                break;
        }

        if (!coordinates.isEmpty()) {
            Log.e("Size of Result ", " " + coordinates.size() + " Sensitivity " + pointValues.size());
            for (int i = 0; i < coordinates.size(); i++) {
                TextView textView = mappedView.findViewById(R.id.tableParent).findViewWithTag(coordinates.get(i));
                textView.setTextSize(18);
                textView.setTextColor(Color.BLACK);
                textView.setText(pointValues.get(i));
                Log.d("getMapppedViewLargeText", "Co-ord " + coordinates.get(i) + " values " + pointValues.get(i));
            }

        }

        if (pattern.equals("24-2")) {
            ((TextView) mappedView.findViewById(R.id.tableParent).findViewWithTag("-x3y1")).setText("");
            ((TextView) mappedView.findViewById(R.id.tableParent).findViewWithTag("-x3-y1")).setText("");
        } else if (pattern.equals("30-2")) {
            ((TextView) mappedView.findViewById(R.id.tableParent).findViewWithTag("-x3y1")).setText("");
            ((TextView) mappedView.findViewById(R.id.tableParent).findViewWithTag("-x3-y1")).setText("");
        }
        return mappedView;
    }

    public static View getPD_Plot(Activity activity, String eye, final ArrayList<String> coordinates, final ArrayList<String> pointValues, final ArrayList<String> result_deviation) {
        try {
            Log.d("getMappedViewLargeText", "Eye " + eye);
            if (eye.equals("Left Eye")) {
                mappedView = activity.getLayoutInflater().inflate(R.layout.ten_cross_two_left_white_without_marker, null);
            } else {
                mappedView = activity.getLayoutInflater().inflate(R.layout.ten_cross_two_right_white_without_marker, null);
            }

            if (!coordinates.isEmpty()) {
                Log.e("Size of Result ", " " + coordinates.size() + " Sensitivity " + pointValues.size());
                for (int i = 0; i < coordinates.size(); i++) {
                    int sensitiveValues = Integer.parseInt(result_deviation.get(i));
                    String val = pointValues.get(i);
                    int dVal = Integer.parseInt(val);
                    String result = null;
                    int temp;
                    //  if(sensitiveValues > 0 && sensitiveValues < 8) {
                    if (sensitiveValues == 4) {
                        temp = dVal + 3;
                        result = "<" + temp;
                    } else {
                        temp = dVal;
                        result = "" + temp;
                    }
                    Log.e("getMapppedViewLargeText", "Co-ord " + coordinates.get(i) + " values " + pointValues.get(i));
                    TextView textView = mappedView.findViewById(R.id.tableParent).findViewWithTag(coordinates.get(i));
                    textView.setTextSize(14);
                    textView.setTextColor(Color.BLACK);
                    textView.setText(result);
                    Log.d("getMapppedViewLargeText", "Co-ord " + coordinates.get(i) + " values " + pointValues.get(i));
                }
            }
            return mappedView;
        } catch (Exception e) {
            Log.e("Exception ", " " + e.getMessage());
            return mappedView;
        }
    }

    public static String getZipFilePath() {
        try {
            JSONObject config = CommonUtils.readConfig("Doctor Copy Fragment");
            return config.getString("DeviceId") + "/Images/" + getCurrentTime() + ".zip";
        } catch (Exception e) {
            return "/MismatchedPatientData/" + getCurrentTime() + ".txt";
        }
    }

    public static View getTD_Plot(Activity activity, String eye, final ArrayList<String> coordinates, final ArrayList<String> pointValues, final ArrayList<String> result_deviation) {
        Log.d("getMappedViewLargeText", "Eye " + eye);

        if (eye.equals("Left Eye")) {
            mappedView = activity.getLayoutInflater().inflate(R.layout.ten_cross_two_left_white_without_marker, null);
        } else {
            mappedView = activity.getLayoutInflater().inflate(R.layout.ten_cross_two_right_white_without_marker, null);
        }

        if (!coordinates.isEmpty()) {
            Log.e("Size of Result ", " " + coordinates.size() + " Sensitivity " + pointValues.size());
            for (int i = 0; i < coordinates.size(); i++) {
                int sensitiveValues = Integer.parseInt(result_deviation.get(i));
                String val = pointValues.get(i);
                int dVal = Integer.parseInt(val);
                String result = null;
                int temp;
                //  if(sensitiveValues > 0 && sensitiveValues < 8) {
                if (sensitiveValues == 4) {
                    temp = dVal + 3;
                    result = "<" + temp;
                } else {
                    temp = dVal;
                    result = "" + temp;
                }
                TextView textView = mappedView.findViewById(R.id.tableParent).findViewWithTag(coordinates.get(i));
                textView.setTextSize(14);
                textView.setTextColor(Color.BLACK);
                textView.setText(result);
                Log.d("getMapppedViewLargeText", "Co-ord " + coordinates.get(i) + " values " + pointValues.get(i));
            }

        }
        return mappedView;
    }

    public static String decryptData(String s) {
        if (s.length() > 12) {

            String cipher = s.substring(12);
            try {
                return new String(Base64.decode(cipher.getBytes(), Base64.NO_WRAP));
            } catch (Exception e) {
                Log.e("decryptData", " " + e.getMessage());
                return null;
            }

        }
        return null;
    }

    public static Bitmap viewToBitmap(View view) {
        Log.d("viewToBitmap", "ViewId " + view.getResources().getResourceName(view.getId()));
        /*TableLayout tab = view.findViewById(R.id.tableParent);
        tab.setDrawingCacheEnabled(true);
        tab.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        tab.layout(0, 0, tab.getMeasuredWidth(), tab.getMeasuredHeight());
        tab.buildDrawingCache(true);
        final Bitmap bitmap = tab.getDrawingCache();
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(bitmap, new Matrix(), null);*/

        //RelativeLayout tab = view.findViewById(R.id.tableParentRelative);
        /*RelativeLayout tab = (RelativeLayout) view.findViewById(R.id.tableParentRelative);
        tab.setDrawingCacheEnabled(true);
        tab.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        tab.layout(0, 0, tab.getMeasuredWidth(), tab.getMeasuredHeight());
        tab.buildDrawingCache(true);
        final Bitmap bitmap = tab.getDrawingCache();
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(bitmap, new Matrix(), null);*/

        //RelativeLayout tab = view.findViewById(R.id.tableParentRelative);
        view.setDrawingCacheEnabled(true);
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache(true);
        final Bitmap bitmap = view.getDrawingCache();
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(bitmap, new Matrix(), null);

       /* Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);*/

        /*view.setDrawingCacheEnabled(true);
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache(true);
        final Bitmap bitmap = view.getDrawingCache();
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(bitmap, new Matrix(), null);*/

        return bitmap;

    }

    public static void overlayBitmap(Activity activity, Bitmap greyScale, ImageView imageView) {
        Bitmap normalVision = BitmapFactory.decodeResource(activity.getResources(),
                R.drawable.normal_vision_img);
        int x = normalVision.getWidth();
        int y = normalVision.getHeight();
        greyScale = Bitmap.createScaledBitmap(greyScale, x, y, true); //resize(greyScale, x, y);
        Bitmap patientVision = Bitmap.createBitmap(x, y, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(patientVision);
        canvas.drawBitmap(normalVision, new Matrix(), null);
        canvas.drawBitmap(greyScale, 0, 0, null);
        Log.d("ImageGrey", "Width" + imageView.getWidth());
        Log.d("ImageGrey", "Height" + imageView.getHeight());
        imageView.setImageBitmap(patientVision);
        getRoundedCroppedBitmap(patientVision, activity);
        CommonUtils.saveImage(patientVision, "patientVision", activity.getApplicationContext());
    }

    public static void saveOverlayBitmap(Activity activity, Bitmap greyScale, int radius) {
        Bitmap normalVision = BitmapFactory.decodeResource(activity.getResources(),
                R.drawable.normal_tajmahal_view);
        int x = normalVision.getWidth();
        int y = normalVision.getHeight();
        greyScale = Bitmap.createScaledBitmap(greyScale, x, y, true); //resize(greyScale, x, y);
        Bitmap patientVision = Bitmap.createBitmap(x, y, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(patientVision);
        canvas.drawBitmap(normalVision, new Matrix(), null);
        canvas.drawBitmap(greyScale, 0, 0, null);
        CommonUtils.saveImage(patientVision, "TunnelVision" + radius, activity.getApplicationContext());
    }

    private static void getRoundedCroppedBitmap(Bitmap bitmap, Activity activity) {

        int widthLight = bitmap.getWidth();
        int heightLight = bitmap.getHeight();

        //int widthRadius = widthLight - 40;
        // int heightRadius = heightLight - 40;

        Log.d("WidthLight", " " + widthLight);
        Log.d("heightLight", " " + heightLight);

        Log.d("widthRadius", " " + widthLight);
        Log.d("heightRadius", " " + widthLight);

        Bitmap output = Bitmap.createBitmap(widthLight, heightLight, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(output);
        Paint paintColor = new Paint();
        paintColor.setFlags(Paint.ANTI_ALIAS_FLAG);
        paintColor.setAntiAlias(true);
        //paintColor.setColor(activity.getResources().getColor(R.color.transparent_bg));
        //paintColor.setColor(Color.TRANSPARENT);
        //paintColor.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
        //paintColor.setAntiAlias(true);

        RectF rectF = new RectF(new Rect(0, 0, widthLight, widthLight));

        canvas.drawRoundRect(rectF, widthLight / 2, heightLight / 2, paintColor);

        Paint paintImage = new Paint();
        paintImage.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
        canvas.drawBitmap(bitmap, 0, 0, paintImage);

        saveImage(output, "patientVisionCropped", activity.getApplicationContext());
    }

    public static void saveImage(Bitmap bitmap, String fileName, Context context) {
        Log.d("SaveImage", "save Called " + " " + fileName);
        try {
            File fileOne = new File(context.getFilesDir(), fileName + ".png");
            FileOutputStream output = new FileOutputStream(fileOne);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
            output.close();
        } catch (FileNotFoundException e) {
            Log.d("SaveImage", "FileNotFound " + e.getMessage());
        } catch (IOException e) {
            Log.d("SaveImage", "IOException " + e.getMessage());
        }
    }

    public static void writeToImageLogsFor21(String filename, Bitmap bitmap) {
        Log.e("writeToImageLogs", "Called");
        File dir = new File(Constants.AVA_IMG_FOLDER);
        if (!dir.exists()) {
            Log.e("writeToImageLogs", "directory not exists creating one");
            boolean mkdirs = dir.mkdirs();
            Log.e("writeToImageLogs", "mkdirs " + mkdirs);
        } else {
            Log.e("writeToImageLogs", "directory already exists");
        }
        File logFile = new File(dir.getAbsolutePath(), filename + ".jpg");
        if (!logFile.exists()) {
            try {
                Log.e("writeToImageLogs", "Image File Not exists");
                logFile.createNewFile();
            } catch (IOException e) {
                Log.e("Exception", "WriteToLogFile " + e.getMessage());
            }
        }
        try {
            FileOutputStream out = new FileOutputStream(logFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();
        } catch (IOException e) {
            Log.e("Exception", "WriteToLogFile" + e.getMessage());
        }
    }


    public static void writeToTotalDeviationFile(String string) {
        //string = string + "  " + getCurrentTime();
        //Log.e("TotalDeviationFile", "Called");
        File dir = new File(TD_LOGS_FOLDER);
        if (!dir.exists())
            dir.mkdirs();
        File logFile = new File(dir.getPath(), getCurrentDateAsFileName() + ".txt");
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                Log.e("Exception", "WriteToLogFile" + e.getMessage());
            }
        }
        try {
            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(string);
            buf.newLine();
            buf.close();
        } catch (IOException e) {
            Log.e(TAG, "writeToTotalDeviationFile " + e.getMessage());
        }
    }

    public static void writeToLogFile(String string) {
        string = string + "  " + getCurrentTime();
        Log.e("writeToLogFile", "Called");
        File dir = new File(SYS_LOGS_FOLDER);
        if (!dir.exists())
            dir.mkdirs();
        File logFile = new File(dir.getPath(), getCurrentDateAsFileName() + ".txt");
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                Log.e("Exception", "WriteToLogFile" + e.getMessage());
            }
        }
        try {
            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(string);
            buf.newLine();
            buf.close();
        } catch (IOException e) {
            Log.e(TAG, "writeToLogFile " + e.getMessage());
        }
    }

    public static void saveBitmapFromView(View view, Context context, Activity activity, final String action, final String reportType, final String mrn, final String testedAt, String eye) {
        String path = context.getFilesDir().getPath();
        String outputPath = null;
        ScrollView scrollView = view.findViewById(R.id.scrollView);
        int width = scrollView.getWidth();
        int height = scrollView.getChildAt(0).getHeight();
        //Define a bitmap with the same size as the view
        Log.d("Width", "" + width);
        Log.d("Height", "" + height);

        Bitmap returnedBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        //Bind a canvas to it
        Canvas canvas = new Canvas(returnedBitmap);
        //Get the view's background
        Drawable bgDrawable = view.getBackground();
        if (bgDrawable != null)//has background drawable, then draw it on the canvas
            bgDrawable.draw(canvas);
        else//does not have background drawable, then draw white background on the canvas
            canvas.drawColor(Color.WHITE);
        // draw the view on the canvas
        view.draw(canvas);
        //return the bitmap
        try {
            File fileOne = new File(context.getFilesDir(), "pdfImage.png");

            FileOutputStream output = new FileOutputStream(fileOne);
            returnedBitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
            output.close();
            //tab.setDrawingCacheEnabled(false);
           /* File file = new File(context.getFilesDir(), "Report.pdf");
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            addImage(document, byteArray);
            document.close();*/
            //File outputFile = new File(Environment.getExternalStorageDirectory(), "TestControllerReports");
            //outputFile.mkdirs();
            outputPath = context.getFilesDir().getAbsolutePath();

        } catch (FileNotFoundException e) {
            Log.e("saveBitmapFromView", e.getMessage());
        } catch (IOException e) {
            Log.e("saveBitmapFromView", e.getMessage());
        }
        String pdfName = generateFileName(reportType, mrn, testedAt, eye);
        convertToPdfNew(context, activity, returnedBitmap, outputPath + "/" + pdfName, action);
    }

    public static String generateFileName(final String reportType, String mrn, String testedAt, String testedEye) {
        mrn = mrn.replace(" ", "");
        Log.e("GenerateFileName", "Mrn " + mrn + " ReportType " + reportType + " Testedat " + testedAt + " Eye " + testedEye);
        mrn = mrn.replace("/", "-");
        String fileName = mrn + "_" + reportType + "_" + testedEye + "_" + testedAt;
        fileName = fileName.replace(":", "_");
        fileName = fileName.replace(" ", "_");
        fileName = fileName.replace("-", "_");
        Log.e("GeneratedFileName", fileName);
        return fileName + ".pdf";
    }

    public static String generateFileName(final String reportType, String patientName, String testedAt) {
        return null;
    }

    private static void convertToPdfNew(Context context, Activity activity, Bitmap bitmap, String outputPdfPath, String action) {

        try {
            deleteFiles(context, ".pdf");
            File outputFile = new File(outputPdfPath);
            if (!outputFile.exists()) {
                outputFile.createNewFile();
            }

            Bitmap glideBM;

            RequestOptions myOptions = new RequestOptions()
                    .format(DecodeFormat.PREFER_ARGB_8888)
                    .encodeFormat(Bitmap.CompressFormat.PNG)
                    .encodeQuality(100)
                    .fitCenter()
                    .dontTransform()
                    .override(523, 770);


            glideBM = Glide.with(context).asBitmap()
                    .load(bitmap)
                    .apply(myOptions)
                    .into(Target.SIZE_ORIGINAL, Target.SIZE_ORIGINAL)
                    .get();
            glideBM.setDensity(DisplayMetrics.DENSITY_MEDIUM);


            PdfDocument document = new PdfDocument();

            PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(595, 842, 1)
                    //.setContentRect(new Rect(36,36,38,36))
                    .create();

            PdfDocument.Page page = document.startPage(pageInfo);

            // Draw the bitmap onto the page
            Canvas canvas = page.getCanvas();
            //canvas.translate();
            //canvas.clipRect(new Rect(36, 36, 523, 770));
            //Rect rectangle = new Rect(36, 36, 36, 36);
            canvas.drawBitmap(glideBM, null, new Rect(36, 36, 523, 770), null);

            document.finishPage(page);

            // Write the PDF file to a file
            document.writeTo(new FileOutputStream(outputPdfPath));
            document.close();

            if (action.equals("print")) {
                doPrint(activity, outputPdfPath, outputFile);
            } else if (action.equals("email")) {
                doShare(context, outputFile);
            } else {
                doBluetoothShare(context, outputFile);
            }
        } catch (ExecutionException e) {
            Log.e("convertToPdfNew", e.getMessage());
        } catch (InterruptedException e) {
            Log.e("convertToPdfNew", e.getMessage());
        } catch (FileNotFoundException e) {
            Log.e("convertToPdfNew", e.getMessage());
        } catch (IOException e) {
            Log.e("convertToPdfNew", e.getMessage());
        }
    }

    public static void doPrint(Activity activity, String filePath, File file) {
        PrintManager printManager = (PrintManager) activity.getSystemService(Context.PRINT_SERVICE);
        try {
            PrintDocumentAdapter printAdapter = new PdfDocumentAdapter(activity, filePath);
            if (printManager != null) {
                printManager.print("Document", printAdapter, new PrintAttributes.Builder().build());
            }
        } catch (Exception e) {
            Log.d("Exception", "" + e.getMessage());
        }
    }

    public static void sendAnalytics(Context context, String pdr1, String pdr2) {
        try {
            if (context != null) {
                FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(context);
                firebaseAnalytics.setUserId(CommonUtils.getHotSpotId());
                Bundle bundle = new Bundle();
                bundle.putString("PDR1", pdr1);
                bundle.putString("PDR1", pdr2);
                firebaseAnalytics.logEvent("PD_Reading", bundle);
            }
        } catch (Exception e) {
            Log.e("sendAnalytics", " " + e.getMessage());
        }

    }

    public static String returnRandom() {
        SimpleDateFormat dfDateTime = new SimpleDateFormat("dd-MMMM-yyyy hh:mm a", Locale.US);
        int year = randBetween(2018, 2019);// Here you can set Range of years you need
        int month = randBetween(0, 11);
        int hour = randBetween(9, 22); //Hours will be displayed in between 9 to 22
        int min = randBetween(0, 59);
        int sec = randBetween(0, 59);


        GregorianCalendar gc = new GregorianCalendar(year, month, 1);
        int day = randBetween(1, gc.getActualMaximum(gc.DAY_OF_MONTH));

        gc.set(year, month, day, hour, min, sec);
        Log.e("returnRandom", "" + dfDateTime.format(gc.getTime()));

        return "" + gc.getTime().getTime();
    }

    public static int randBetween(int start, int end) {
        return start + (int) Math.round(Math.random() * (end - start));
    }

    public static void doShare(Context context, File file) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("application/pdf");
//        File file = new File(context.getFilesDir() + File.separator + "Report.pdf");
        Uri uri = FileProvider.getUriForFile(context, "com.agyohora.mobileperitc.fileprovider", file);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }
    }

    public static byte[] fileToByteArray(File file, Context context) {
        try {
            String cacheDir = context.getCacheDir().getAbsolutePath();
            File imageFile = new File(cacheDir, "b1.jpg");
            FileInputStream fis = new FileInputStream(imageFile);
            byte[] result = ByteStreams.toByteArray(fis);
            return result;
        } catch (Exception e) {
            Log.e("fileToByteArray", " " + e.getMessage());
            return null;
        }
    }

    public static byte[] fileToByteArray(Context context) {
        try {
            String cacheDir = context.getCacheDir().getAbsolutePath();
            File imageFile = new File(cacheDir, "b1.jpg");
            FileInputStream fis = new FileInputStream(imageFile);
            byte[] result = ByteStreams.toByteArray(fis);
            return result;
        } catch (Exception e) {
            Log.e("fileToByteArray", " " + e.getMessage());
            return null;
        }
    }


    public static void doBluetoothShare(Context context, File file) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("application/pdf");
        //   File file = new File(context.getFilesDir() + File.separator + "Report.pdf");
        Uri uri = FileProvider.getUriForFile(context, "com.agyohora.mobileperitc.fileprovider", file);
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        intent.setPackage("com.android.bluetooth");
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }
        /*try {
            BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
            Set<BluetoothDevice> pairedDevices = bluetoothAdapter.getBondedDevices();
            // bluetoothAdapter.cancelDiscovery();
            if (pairedDevices.size() > 0) {
                // There are paired devices. Get the name and address of each paired device.
                for (BluetoothDevice device : pairedDevices) {
                    device.createBond();
                    ParcelUuid[] uuids = device.getUuids();
                    UUID MY_UUID_INSECURE =
                            UUID.fromString("00001800-0000-1000-8000-00805f9b34fb"); //8ce255c0-200a-11e0-ac64-0800200c9a66
                    // BluetoothSocket sock = createBluetoothSocket1(device, MY_UUID_INSECURE);
                    BluetoothConnectionService mBluetoothConnection = new BluetoothConnectionService(context);
                    mBluetoothConnection.startClient(device, MY_UUID_INSECURE, file);
                    new Handler(Looper.getMainLooper()).postDelayed(() -> mBluetoothConnection.write(CommonUtils.fileToByteArray(file, context), file), 15000);
                    //new Handler(Looper.getMainLooper()).postDelayed(() -> mBluetoothConnection.start(), 15000);

                    //mBluetoothConnection.write(fileToByteArray(file));
                    *//*Thread.sleep(10000);
                    mBluetoothConnection.write(fileToByteArray(file));
                    for (ParcelUuid uuid : uuids) {
                        Log.e("UUID", " " + uuid.getUuid());
                    }
                    for (ParcelUuid uuid : uuids) {
                        try {
                            Log.e("Running ", "for loop ");
                            mBluetoothConnection.startClient(device, uuid.getUuid());
                            Thread.sleep(10000);
                            mBluetoothConnection.write(fileToByteArray(file));
                            break;
                        } catch (Exception e) {
                            Log.e("Exception", " " + e.getMessage());
                        }*//*
                    break;
                }

                  *//*  String deviceName = device.getName();
                    String deviceHardwareAddress = device.getAddress(); // MAC address
                    *//**//*BluetoothSocket sock = createBluetoothSocket1(device);
                    sock.connect();*//**//*
                    Log.e("BondState", " " + device.getBondState());
                    device.createBond();
                    ConnectThread conexion = new ConnectThread(device);
                    conexion.run();*//*
         *//* BluetoothConnector bluetoothConnector = new BluetoothConnector(device, false, bluetoothAdapter, null);
                    bluetoothConnector.connect();*//*
         *//*
                    bluetoothConnector.
                    Log.e("doBluetoothShare", "deviceName " + deviceName);
                    Log.e("doBluetoothShare", "deviceHardwareAddress " + deviceHardwareAddress);
                    //UUID SERIAL_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); // bluetooth serial port service

                    //BluetoothSocket socket = device.createInsecureRfcommSocketToServiceRecord(SERIAL_UUID);
                    AVABluetoothService.ConnectedThread connectedThread = new AVABluetoothService.ConnectedThread(createBluetoothSocket(device), device);
                    //connectedThread.run();
                    connectedThread.write(fileToByteArray(file));*//*

            } else {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("application/pdf");
                //   File file = new File(context.getFilesDir() + File.separator + "Report.pdf");
                Uri uri = FileProvider.getUriForFile(context, "com.agyohora.mobileperitc.fileprovider", file);
                intent.putExtra(Intent.EXTRA_STREAM, uri);
                intent.setPackage("com.android.bluetooth");
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

                if (intent.resolveActivity(context.getPackageManager()) != null) {
                    context.startActivity(intent);
                }
            }
        } catch (Exception e) {
            Log.e("doBluetoothShare", "doBluetoothShare " + e.getMessage());
        }*/
    }

    private static BluetoothSocket createBluetoothSocket(BluetoothDevice device) {
        UUID SERIAL_UUID = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); // bluetooth serial port service
        try {
            final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", new Class[]{UUID.class});
            return (BluetoothSocket) m.invoke(device, SERIAL_UUID);
        } catch (Exception e) {
            Log.e(TAG, "Could not create Insecure RFComm Connection" + e.getMessage());

        }
        try {
            return device.createRfcommSocketToServiceRecord(SERIAL_UUID);
        } catch (IOException e) {
            Log.e(TAG, "Could not create secure RFComm Connection" + e.getMessage());
        }
        return null;
    }

    private static BluetoothSocket createBluetoothSocket1(BluetoothDevice device, UUID PBAP_UUID) {
        BluetoothSocket mmSocket = null;
        try {
            //final String PBAP_UUID = "0000112f-0000-1000-8000-00805f9b34fb";
            // mmSocket = device.createInsecureRfcommSocketToServiceRecord(ParcelUuid.fromString(PBAP_UUID).getUuid());
            mmSocket = device.createInsecureRfcommSocketToServiceRecord(UUID.fromString("0000112f-0000-1000-8000-00805f9b34fb"));

            // mmSocket = device.createRfcommSocketToServiceRecord(uuid);
            Log.e("Connection", "Created");

            mmSocket.connect();
            Log.e("Connection", "Connected");

        } catch (Exception e) {
            if (mmSocket != null) {
                try {
                    mmSocket.close();
                } catch (IOException e1) {

                    Log.e("Connection", "Socket close Error" + e1.getMessage());
                }
                mmSocket = null;
            }
            e.printStackTrace();
            Log.e("Connection", "General Error " + e.getMessage());
        }
        return mmSocket;
    }

    static void deleteFiles(Context context, String ext) {
        File dir = new File(context.getFilesDir().getAbsolutePath());
        if (!dir.exists())
            return;
        File[] files = dir.listFiles(new GenericExtFilter(ext));
        for (File file : files) {
            if (!file.isDirectory()) {
                boolean result = file.delete();
                Log.e("deleteFiles", "Deleted:" + result);
            }
        }
    }

    static Bitmap shrinkBitmap(String file, int width, int height) {

        BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
        bmpFactoryOptions.inJustDecodeBounds = true;
        Bitmap bitmap;

        int heightRatio = (int) Math.ceil(bmpFactoryOptions.outHeight / (float) height);
        int widthRatio = (int) Math.ceil(bmpFactoryOptions.outWidth / (float) width);

        if (heightRatio > 1 || widthRatio > 1) {
            if (heightRatio > widthRatio) {
                bmpFactoryOptions.inSampleSize = heightRatio;
            } else {
                bmpFactoryOptions.inSampleSize = widthRatio;
            }
        }

        bmpFactoryOptions.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(file, bmpFactoryOptions);
        return bitmap;
    }

    static Bitmap compressImage(Bitmap bitmap, Context context, String path) {

        Bitmap scaledBitmap = null;

        BitmapFactory.Options options = new BitmapFactory.Options();

//      by setting this field as true, the actual bitmap pixels are not loaded in the memory. Just the bounds are loaded. If
//      you try the use the bitmap here, you will get null.
        options.inJustDecodeBounds = true;
        Bitmap bmp = BitmapFactory.decodeFile(path, options);

        int actualHeight = options.outHeight;
        int actualWidth = options.outWidth;

//      max Height and width values of the compressed image is taken as 816x612

        float maxHeight = 523.0f;
        float maxWidth = 770.0f;
        float imgRatio = actualWidth / actualHeight;
        float maxRatio = maxWidth / maxHeight;

//      width and height values are set maintaining the aspect ratio of the image

        if (actualHeight > maxHeight || actualWidth > maxWidth) {
            if (imgRatio < maxRatio) {
                imgRatio = maxHeight / actualHeight;
                actualWidth = (int) (imgRatio * actualWidth);
                actualHeight = (int) maxHeight;
            } else if (imgRatio > maxRatio) {
                imgRatio = maxWidth / actualWidth;
                actualHeight = (int) (imgRatio * actualHeight);
                actualWidth = (int) maxWidth;
            } else {
                actualHeight = (int) maxHeight;
                actualWidth = (int) maxWidth;

            }
        }

//      setting inSampleSize value allows to load a scaled down version of the original image

        options.inSampleSize = calculateInSampleSize(options, actualWidth, actualHeight);

//      inJustDecodeBounds set to false to load the actual bitmap
        options.inJustDecodeBounds = false;

//      this options allow android to claim the bitmap memory if it runs low on memory
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inTempStorage = new byte[16 * 1024];

        try {
//          load the bitmap from its path
            bmp = BitmapFactory.decodeFile(path, options);
        } catch (OutOfMemoryError exception) {
            Log.e("compressImage", exception.getMessage());

        }
        try {
            scaledBitmap = Bitmap.createBitmap(actualWidth, actualHeight, Bitmap.Config.ARGB_8888);
        } catch (OutOfMemoryError exception) {
            Log.e("compressImage", exception.getMessage());
        }

        float ratioX = actualWidth / (float) options.outWidth;
        float ratioY = actualHeight / (float) options.outHeight;
        float middleX = actualWidth / 2.0f;
        float middleY = actualHeight / 2.0f;

        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY, middleX, middleY);

        Canvas canvas = new Canvas(scaledBitmap);
        canvas.setMatrix(scaleMatrix);
        canvas.drawBitmap(bmp, middleX - bmp.getWidth() / 2, middleY - bmp.getHeight() / 2, new Paint(Paint.FILTER_BITMAP_FLAG));

//      check the rotation of the image and display it properly
        ExifInterface exif;
        try {
            exif = new ExifInterface(path);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 0);
            Log.d("EXIF", "Exif: " + orientation);
            Matrix matrix = new Matrix();
            if (orientation == 6) {
                matrix.postRotate(90);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 3) {
                matrix.postRotate(180);
                Log.d("EXIF", "Exif: " + orientation);
            } else if (orientation == 8) {
                matrix.postRotate(270);
                Log.d("EXIF", "Exif: " + orientation);
            }
            scaledBitmap = Bitmap.createBitmap(scaledBitmap, 0, 0,
                    scaledBitmap.getWidth(), scaledBitmap.getHeight(), matrix,
                    true);
        } catch (IOException e) {
            e.printStackTrace();
        }


        try {
            File fileOne = new File(context.getFilesDir(), "pdfImageNew.png");
            FileOutputStream output = new FileOutputStream(fileOne);
//          write the compressed bitmap at the destination specified by filename.
            scaledBitmap.compress(Bitmap.CompressFormat.PNG, 100, output);

        } catch (FileNotFoundException e) {
            Log.e("compressImage", e.getMessage());
        }
        return scaledBitmap;

    }


    static int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }
        final float totalPixels = width * height;
        final float totalReqPixelsCap = reqWidth * reqHeight * 2;
        while (totalPixels / (inSampleSize * inSampleSize) > totalReqPixelsCap) {
            inSampleSize++;
        }

        return inSampleSize;
    }

    /*
     * Lens Power Calculation Utils
     * */

    @NonNull
    public static String getAge(@NonNull String dateOfBirth) {
        dateOfBirth = dateOfBirth == null ? "20" : dateOfBirth;
        Log.d("BirthDate", " " + dateOfBirth);
        String[] str = dateOfBirth.split("-");
        int day = Integer.parseInt(str[2]);
        int month = Integer.parseInt(str[1]);
        int year = Integer.parseInt(str[0]);
        Log.d("AGE", "Year" + year + " Month " + month + " day" + day);
        Calendar dob = Calendar.getInstance();
        Calendar today = Calendar.getInstance();

        dob.set(year, month, day);

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);

        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }
        int ageInt = age;

        return Integer.toString(ageInt);
    }

    public static Bundle powerCalculation(float spherical, float cylindrical, float axis, double age) {

        Log.e("powerCalculation", "**************************POWER CALCULAION STARTS*************************");
        Log.e("powerCalculation", " starts");
        boolean cylindricalVisibility = true, axislVisibility = true;
        DecimalFormat df2 = new DecimalFormat(".##");
        Bundle values = new Bundle();
        double calculatedSphericalValue = 0, calculatedCylindricalValue, calculatedAxis;

        String type = sign(spherical) > 0 ? "Hyperopia" : "Myopia";
        Log.e("Type", " " + type);
        Log.e("Age", " " + age);

        if (axis <= 10 || axis >= 170) {
            Log.e("Axis", "Axis less than or equal to 10 or axis greater than equal to 170");
            calculatedSphericalValue = spherical + cylindrical;
            cylindrical = -(cylindrical);
            axis = axis <= 90 ? axis + 90 : axis - 90;
            spherical = Float.parseFloat("" + calculatedSphericalValue);
        }

        if (age > 40) {
            double tempCalculatedSpherical;
            Log.e("Division", " " + (age / 10));
            double addition = (age / 10) - 4;
            Log.e("Age", " " + age);
            Log.e("Addition", "Before " + addition);
            addition = Double.parseDouble(df2.format(addition));
            addition = calculateRefractivePower(addition);
            Log.e("Addition", "After calculating refractive power " + addition);
            Log.e("Addition", "After Doubled" + addition);
            calculatedSphericalValue = (spherical + addition);

            // new part
            if (spherical > 1.50) {
                tempCalculatedSpherical = (spherical - 1.50);
            } else {
                tempCalculatedSpherical = spherical;
            }
            calculatedSphericalValue = Math.max(calculatedSphericalValue, tempCalculatedSpherical);

        } else {
            switch (type) {
                case "Myopia":
                    Log.e("Case is Myopia", "");
                    if (spherical > 1.50) {
                        calculatedSphericalValue = (spherical - 1.50);
                    } else {
                        calculatedSphericalValue = spherical;
                    }
                    Log.e("calculatedSphericalVal", "Myopia " + calculatedSphericalValue);
                    break;
                case "Hyperopia":
                    Log.e("Case is Hyperopia", "");
                   /* if (spherical > 1.50) {
                        calculatedSphericalValue = "" + (spherical + 1.50);
                    } else {
                        calculatedSphericalValue = "" + spherical;
                    }*/
                    calculatedSphericalValue = spherical;
                    Log.e("calculatedSphericalVal", "Hyperopia " + calculatedSphericalValue);
                    break;
               /* default:
                    Log.e("Case is Default", "");
                    if (spherical > 1.50) {
                        calculatedSphericalValue = (spherical + 1.50);
                    } else {
                        calculatedSphericalValue = spherical;
                    }
                    Log.e("calculatedSphericalVal", "Default " + calculatedSphericalValue);
                    break;*/
            }
        }


        if (cylindrical <= 1 && cylindrical >= -1) {
            Log.e("cylindrical", "less than or equal to one");
            Log.e("cylindrical", "Before Calc " + cylindrical);
            calculatedSphericalValue = calculatedSphericalValue + (cylindrical / 2);
            cylindricalVisibility = false;
            axislVisibility = false;

        } else {
            Log.e("cylindrical", "greater than one");
            Log.e("cylindrical", "Before Calc " + cylindrical);
            cylindricalVisibility = true;
            axislVisibility = true;
        }


        if (age < 40 && type.equals("Myopia")) {
            Log.e("Myopia", "Age less than 40");
            //Float val = Float.parseFloat(calculatedSphericalValue);
            Log.e("val", " " + calculatedSphericalValue);
            if (calculatedSphericalValue < 0 && calculatedSphericalValue >= -1.50) {
                Log.e("Loop", "one");
                calculatedSphericalValue = 0;
            } else if (calculatedSphericalValue < -1.50) {
                Log.e("Loop", "two");
                calculatedSphericalValue = (calculatedSphericalValue + 1.50);
            } else {
                Log.e("Loop", "else");
            }
        }

        //float temp = Float.parseFloat(calculatedSphericalValue);
        String stringifiedCalculatedSphericalValue = sign(calculatedSphericalValue) > 0 ? "+" + calculatedSphericalValue : "" + calculatedSphericalValue;
        String wholeValue = stringifiedCalculatedSphericalValue.substring(0, stringifiedCalculatedSphericalValue.indexOf('.'));
        String decimalValue = stringifiedCalculatedSphericalValue.substring(stringifiedCalculatedSphericalValue.indexOf('.') + 1);
        Log.e("wholeValue", " " + wholeValue);
        Log.e("decimalValue", " " + decimalValue);
        if (decimalValue.length() >= 3) {
            stringifiedCalculatedSphericalValue = wholeValue + "." + roundedDecimalValue(Integer.parseInt(decimalValue.substring(0, 2)));
        }
        int integerPlaces = stringifiedCalculatedSphericalValue.indexOf('.');
        int decimalPlaces = stringifiedCalculatedSphericalValue.length() - integerPlaces - 1;
        Log.e("cylindrical", "After Calc " + cylindrical);
        values.putString("calculatedSphericalValue", stringifiedCalculatedSphericalValue);
        values.putString("calculatedCylindricalValue", cylindricalVisibility ? "" + cylindrical : "");
        values.putString("calculatedAxis", axislVisibility ? "" + axis : "");
        values.putBoolean("cylindricalVisibility", cylindricalVisibility);
        values.putBoolean("axisVisibility", axislVisibility);
        Log.e("powerCalculation", "**************************POWER CALCULAION ENDS*************************");
        return values;

    }

    private static double calculateRefractivePower(double value) {
        String caseValue = "" + value;
        switch (caseValue) {
            case "0.0":
                value = 0.00;
                break;
            case "0.1":
                value = 0.25;
                break;
            case "0.2":
                value = 0.25;
                break;
            case "0.3":
                value = 0.25;
                break;
            case "0.4":
                value = 0.50;
                break;
            case "0.5":
                value = 0.50;
                break;
            case "0.6":
                value = 0.75;
                break;
            case "0.7":
                value = 0.75;
                break;
            case "0.8":
                value = 1.00;
                break;
            case "0.9":
                value = 1.00;
                break;
            case "1.0":
                value = 1.00;
                break;
            case "1.1":
                value = 1.25;
                break;
            case "1.2":
                value = 1.25;
                break;
            case "1.3":
                value = 1.25;
                break;
            case "1.4":
                value = 1.50;
                break;
            case "1.5":
                value = 1.50;
                break;
            default:
                Log.e("Default", "Executed");
                value = 1.50;
                break;
        }
        return value;
    }

    private static int sign(double f) {
        if (f != f) throw new IllegalArgumentException("NaN");
        if (f == 0) return 0;
        f *= Double.POSITIVE_INFINITY;
        if (f == Double.POSITIVE_INFINITY) return +1;
        if (f == Double.NEGATIVE_INFINITY) return -1;
        throw new IllegalArgumentException("Unfathomed double");
    }

    private static int roundedDecimalValue(int value) {
        int decimalValue = 0;
        int quiotent = value / 100;
        if (quiotent >= 0 && quiotent <= 2)
            decimalValue = 0;
        else if (quiotent > 2 && quiotent <= 5)
            decimalValue = 25;
        else if (quiotent > 5 && quiotent <= 9)
            decimalValue = 75;
        return decimalValue;
    }


    public static String removeSpecialChars(String mrn) {
        return mrn.replaceAll("[^a-zA-Z0-9]+", "-");
    }

    /*
     * Communication close utils
     * */

    public static void closeReceiver(Activity activity, Class className) {
        PackageManager pm = activity.getPackageManager();
        ComponentName componentName = new ComponentName(activity, className);
        pm.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
        Toast.makeText(activity, "cancelled", Toast.LENGTH_LONG).show();
    }

    public static void startReceiver(Activity activity, Class className) {
        PackageManager pm = activity.getPackageManager();
        ComponentName componentName = new ComponentName(activity, className);
        pm.setComponentEnabledSetting(componentName, PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
        Toast.makeText(activity, "started", Toast.LENGTH_LONG).show();
    }

    /*
     * User Notifications*/

    public static String splitHmdHeartBeat(String flag) {
        Log.e("hmdModeWithBattery", " " + Store.hmdModeWithBattery);
        String returnValue;
        String[] heartBeat = nullCheck(Store.hmdModeWithBattery) ? Store.hmdModeWithBattery.split(" ") : new String[0];
        if (heartBeat.length <= 0) {
            Log.e("splitHmdHeartBeat", "Length " + heartBeat.length);
            return "-1";
        }

        switch (flag) {
            case "Battery":
                returnValue = heartBeat[0];
                break;
            case "ModeNo":
                returnValue = heartBeat[1];
                break;
            case "ModeTypeNo":
                returnValue = heartBeat[2];
                break;
            case "ModeStepNo":
                returnValue = heartBeat[3];
                break;
            default:
                returnValue = "-1";
                break;
        }
        return returnValue;
    }

    /*
     * File Utils
     * */

    public static void backUpDb(Context context) {
        Log.d("BackUp", "DB Called");
        try {
            final String inFileName = context.getDatabasePath("patient-database").getPath();
            File dbFile = new File(inFileName);
            FileInputStream fis = new FileInputStream(dbFile);

            String outFileName = Environment.getExternalStorageDirectory() + "/AVA/database" + "/AVAcopy.db";

            // Open the empty db as the output stream
            OutputStream output = new FileOutputStream(outFileName);

            // Transfer bytes from the input file to the output file
            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }

            // Close the streams
            output.flush();
            output.close();
            fis.close();
        } catch (IOException e) {
            Log.e(TAG, " " + e.getMessage());
        }
    }

    public static File isFileExists(String fileName, Context context) {
        Log.e("FileName", "Looking For " + fileName);
        File outputFile = new File(Environment.getExternalStorageDirectory(), "TestControllerReports");
        outputFile.mkdirs();
        String path = outputFile.getPath();
        path = path + "/" + fileName;
        File f = new File(path);
        if (f.exists() && !f.isDirectory())
            return f;
        else
            return null;
    }

    public static void writeToFile(String data, String fileName) {
        try {
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(MyApplication.getInstance().openFileOutput(fileName + ".txt", Context.MODE_PRIVATE));
            outputStreamWriter.write(data);
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    public static void writeJson(JSONObject jsonObject, Context context, String fileName) {
        try {
            Writer output = null;
            File file = new File(Constants.JSON_FOLDER, fileName + ".json");
            //file.mkdirs();
            output = new BufferedWriter(new FileWriter(file));
            output.write(jsonObject.toString());
            output.close();
            Log.d("writeJson", "Saved");
            //Toast.makeText(context, "Json saved", Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            Log.e("writeJson", "Exception" + e.getMessage());
            //Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public static String getCurrentDateAsFileName() {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        String name = dateFormat.format(date);
        return name != null ? name : "";
    }

    public static String getCurrentDateTimeAsFileName(String mrn, String eye, String pattern, String strategy) {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MMMM-yyyy hh:mm a");
        String name = dateFormat.format(date);
        name = mrn + "_" + eye + "_" + "_" + pattern + "_" + strategy + name;
        name = name.replace("/", "-");
        name = name.replace(":", "_");
        name = name.replace(" ", "_");
        name = name.replace("-", "_");
        return name != null ? name : "";
    }

    public static void writeToDisplayoLogFile(String string) {
        string = string + "  " + getCurrentTime();
        File dir = new File(DISPLAY_LOGS_FOLDER);
        if (!dir.exists())
            dir.mkdirs();
        File logFile = new File(dir.getPath(), getCurrentDateAsFileName() + ".txt");
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                Log.e("Exception", "WriteToLogFile" + e.getMessage());
            }
        }
        try {
            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(string);
            buf.newLine();
            buf.close();
        } catch (IOException e) {
            Log.e(TAG, "writeToLogFile " + e.getMessage());
        }
    }

    public static void writeToBugFixLogFile(String string) {
        string = string + "  " + getCurrentTime();
        ;
        File dir = new File(BUG_FIX_LOGS_FOLDER);
        if (!dir.exists())
            dir.mkdirs();
        File logFile = new File(dir.getPath(), getCurrentDateAsFileName() + ".txt");
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                Log.e("Exception", "WriteToLogFile" + e.getMessage());
            }
        }
        try {
            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(string);
            buf.newLine();
            buf.close();
        } catch (IOException e) {
            Log.e(TAG, "writeToLogFile " + e.getMessage());
        }
    }

    public static void writeToChronometerLogFile(String string) {
        string = string + "  " + getCurrentTime();
        ;
        File dir = new File(CHRONOMETER_LOGS_FOLDER);
        if (!dir.exists())
            dir.mkdirs();
        File logFile = new File(dir.getPath(), getCurrentDateAsFileName() + ".txt");
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                Log.e("Exception", "WriteToLogFile" + e.getMessage());
            }
        }
        try {
            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(string);
            buf.newLine();
            buf.close();
        } catch (IOException e) {
            Log.e(TAG, "writeToLogFile " + e.getMessage());
        }
    }

    public static void writeToClickAckFile(String string) {
        string = string + "  " + getCurrentTime();
        ;
        File dir = new File(BUG_FIX_CLICK_ACK_FOLDER);
        if (!dir.exists())
            dir.mkdirs();
        File logFile = new File(dir.getPath(), getCurrentDateAsFileName() + ".txt");
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                Log.e("Exception", "WriteToLogFile" + e.getMessage());
            }
        }
        try {
            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(string);
            buf.newLine();
            buf.close();
        } catch (IOException e) {
            Log.e(TAG, "writeToLogFile " + e.getMessage());
        }
    }

    public static void writeToTestLogFile(String string) {
        string = string + "  " + getCurrentTime();
        File dir = new File(TEST_TIME_LOGS_FOLDER);
        if (!dir.exists())
            dir.mkdirs();
        File logFile = new File(dir.getPath(), getCurrentDateAsFileName() + ".txt");
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                Log.e("Exception", "WriteToLogFile" + e.getMessage());
            }
        }
        try {
            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(string);
            buf.newLine();
            buf.close();
        } catch (IOException e) {
            Log.e(TAG, "writeToLogFile " + e.getMessage());
        }
    }

    public static void writeToTenDashTwoLogFile(String string) {
        string = string + "  " + getCurrentTime();
        File dir = new File(TEN_DASH_TWO);
        if (!dir.exists())
            dir.mkdirs();
        File logFile = new File(dir.getPath(), getCurrentDateAsFileName() + ".txt");
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                Log.e("Exception", "WriteToLogFile" + e.getMessage());
            }
        }
        try {
            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(string);
            buf.newLine();
            buf.close();
        } catch (IOException e) {
            Log.e(TAG, "writeToLogFile " + e.getMessage());
        }
    }


    public static void writeToMyDebugFile(String string) {
        string = string + "  " + getCurrentTime();
        File dir = new File(MY_DEBUG_LOGS_FOLDER);
        if (!dir.exists())
            dir.mkdirs();
        File logFile = new File(dir.getPath(), getCurrentDateAsFileName() + ".txt");
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                Log.e("Exception", "WriteToLogFile" + e.getMessage());
            }
        }
        try {
            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(string);
            buf.newLine();
            buf.close();
        } catch (IOException e) {
            Log.e(TAG, "writeToLogFile " + e.getMessage());
        }
    }

    public static void writeToFile(String jsonObject, Context context, String fileName) {
        try {
            Writer output = null;
            File file = new File(Constants.JSON_FOLDER, fileName + ".json");
            //file.mkdirs();
            output = new BufferedWriter(new FileWriter(file));
            output.write(jsonObject);
            output.close();
            Log.d("writeJson", "Saved");
            //Toast.makeText(context, "Json saved", Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            Log.e("writeJson", "Exception" + e.getMessage());
            //Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public static String getFilePath() {
        try {
            JSONObject config = CommonUtils.readConfig("Doctor Copy Fragment");
            return config.getString("DeviceId") + "/MismatchedPatientData/" + getCurrentTime() + ".txt";
        } catch (Exception e) {
            return "/MismatchedPatientData/" + getCurrentTime() + ".txt";
        }
    }


    public static void writeToDatabaseMergingLogFIle(String string) {
        string = "\n" + string;
        File dir = new File(DATABASE_RESTORE_LOGS_FOLDER);
        if (!dir.exists())
            dir.mkdirs();
        File logFile = new File(dir.getPath(), "logs.txt");
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                Log.e("Exception", "writeToDatabaseMergingLogFIle" + e.getMessage());
            }
        }
        try {
            //BufferedWriter for performance, true to set append to file flag
            BufferedWriter buf = new BufferedWriter(new FileWriter(logFile, true));
            buf.append(string);
            buf.newLine();
            buf.close();
        } catch (IOException e) {
            Log.e(TAG, "writeToLogFile " + e.getMessage());
        }
    }

    public static void writeToFileInApp(String jsonObject, Context context) {
        try {
            Writer output = null;
            File file = new File(context.getFilesDir().getAbsolutePath(), "config.json");
            output = new BufferedWriter(new FileWriter(file));
            output.write(jsonObject);
            output.close();
        } catch (Exception e) {
            Log.e("writeToFileInApp", "Exception" + e.getMessage());
        }
    }

    public static void writeToFileInSDCard(String jsonObject, Context context) {
        try {
            Writer output = null;
            File file = new File(Environment.getExternalStorageDirectory(), Constants.CONFIG_FILE_PATH);
            output = new BufferedWriter(new FileWriter(file));
            output.write(jsonObject);
            output.close();
        } catch (Exception e) {
            Log.e("writeToFileInSDCard", "Exception" + e.getMessage());
        }
    }


    public static File isConfigFileExists(Context context) {
        Log.d("isConfigFileExists", "Looking For ");
        File outputFile = new File(Environment.getExternalStorageDirectory(), "TC");
        outputFile.mkdirs();
        String path = outputFile.getPath();
        path = path + File.separator + "config.json";
        File f = new File(path);
        if (f.exists() && !f.isDirectory())
            return f;
        else
            return null;
    }

    public static void changeSiteOrg(Context context, AppPreferencesHelper appPreferencesHelper) {
        if (CommonUtils.isUserSetUpFinished(context)) {
            try {
                JSONObject config = CommonUtils.readConfig("Doctor Copy Fragment PDF");
                if (config != null) {
                    String deviceId = config.getString("DeviceId");
                    config.put("OrganizationId", "LAEL VISION");
                    config.put("SiteId", "\n728 Av Tombalbaye C/ Gombe\n+243 995512358 998177222 977917481\n\nVos yeux nous intéressent");
                    writeToFileInApp(config.toString(), context);
                    writeToFileInSDCard(config.toString(), context);
                    appPreferencesHelper.setOrgSiteUpdateStatus(true);
                }
            } catch (JSONException e) {
                Log.e("changeSiteOrg", e.getMessage());
            }
        }
    }

    public static JSONObject readConfig(String caller) {

        Context context = MyApplication.getInstance();
        String outputPath = context.getFilesDir().getAbsolutePath();
        File outputFile;
        AppPreferencesHelper devicePref = new AppPreferencesHelper(context, DEVICE_PREF);
        boolean getStat = devicePref.getProductionTestingStatus();
        if (getStat) {
            outputFile = new File(outputPath, "config.json");
            if (!outputFile.exists()) {
                Log.e("readConfig", "File Not Exists");
                outputFile = new File(Environment.getExternalStorageDirectory(), Constants.CONFIG_FILE_PATH);
            }
        } else
            outputFile = new File(Environment.getExternalStorageDirectory(), Constants.CONFIG_FILE_PATH);
        String line;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(outputFile));

            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }

            bufferedReader.close();
            return new JSONObject(stringBuilder.toString());
        } catch (Exception e) {
            Log.e("readConfig", "Exception " + e.getMessage());
            return null;
        }

    }

    public static JSONObject readVector() {
        Context context = MyApplication.getInstance();
        String serial = getHotSpotId();
        String outputPath = context.getFilesDir().getAbsolutePath();
        File outputFile;
        AppPreferencesHelper devicePref = new AppPreferencesHelper(context, DEVICE_PREF);
        boolean getStat = devicePref.getProductionTestingStatus();
        if (getStat) {
            outputFile = new File(outputPath, "vector" + serial + ".json");
            if (!outputFile.exists()) {
                Log.e("readConfig", "File Not Exists");
                outputFile = new File(Environment.getExternalStorageDirectory(), Constants.VECTOR_FILE_PATH + serial + ".json");
            }
        } else
            outputFile = new File(Environment.getExternalStorageDirectory(), Constants.VECTOR_FILE_PATH + serial + ".json");

        String line;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(outputFile));

            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }

            bufferedReader.close();
            return new JSONObject(stringBuilder.toString());
        } catch (Exception e) {
            Log.e("readVector", "Exception " + e.getMessage());
            return null;
        }

    }

    public static JSONObject readVectorFromAppMemory() {
        Context context = MyApplication.getInstance();
        String serial = getHotSpotId();
        String outputPath = context.getFilesDir().getAbsolutePath();
        File outputFile = new File(outputPath, "vector" + serial + ".json");
        String line;
        StringBuilder stringBuilder = new StringBuilder();

        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader(outputFile));

            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line);
            }

            bufferedReader.close();
            return new JSONObject(stringBuilder.toString());
        } catch (Exception e) {
            Log.e("readVector", "Exception " + e.getMessage());
            return null;
        }

    }

    public static boolean copyFile(String inputPath, String inputFile, String outputPath) {
        Log.e("outputPath", outputPath);
        Log.e("inputFile", inputFile);
        Log.e("inputPath", inputPath);


        InputStream in = null;
        OutputStream out = null;
        try {

            //create output directory if it doesn't exist
            File outputFile = new File(outputPath);
            if (!outputFile.exists()) {
                outputFile.createNewFile();
            }


            in = new FileInputStream(inputPath + inputFile);
            out = new FileOutputStream(outputPath + "/" + inputFile);

            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            in.close();
            in = null;

            // write the output file (You have now copied the file)
            out.flush();
            out.close();
            out = null;
            return true;

        } catch (FileNotFoundException fnfe1) {
            Log.e("FileNotFoundException", fnfe1.getMessage());
            return false;
        } catch (Exception e) {
            Log.e("Exception", e.getMessage());
            return false;
        }

    }

    public static boolean deleteFile(String inputPath, String inputFile) {
        Log.e("deleteFile", "called ");
        Log.e("inputFile", "  " + inputFile);
        Log.e("inputPath", "  " + inputPath);
        try {
            // delete the original file
            File file = new File(inputPath, inputFile);
            Log.e("deleteFile", "Path " + file.getAbsolutePath());
            boolean flag = file.getAbsoluteFile().delete();
            Log.e("deleteFile", "flag " + flag);
            return flag;
        } catch (Exception e) {
            Log.e("deleteFile", " " + e.getMessage());
            return false;
        }
    }

    public static String getHotSpotId() {
        try {
            JSONObject jsonObject = readConfig("getHotSpotId");
            if (jsonObject != null) {
                return jsonObject.getString("DeviceId");
            }
        } catch (JSONException e) {
            Log.e("JSonException", " " + e.getMessage());
        }
        return null;
    }

    public static String getVersionName() {
        int versionCode = BuildConfig.VERSION_CODE;
        String versionName = BuildConfig.VERSION_NAME;
        return versionName;
    }

    public static String gerCurrentTime() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    public static boolean isUserSetUpFinished(Context context) {
        return new AppPreferencesHelper(context, DEVICE_PREF).getUserSetUpStatus();
    }

    public static boolean isProductionSetUpFinished(Context context) {
        return new AppPreferencesHelper(context, DEVICE_PREF).getProductionSetUpStatus();
    }

    public static String getSavedDeviceId(Context context) {
        return new AppPreferencesHelper(context, DEVICE_PREF).getDeviceId();
    }

    public static String getSavedOrgId(Context context) {
        return new AppPreferencesHelper(context, DEVICE_PREF).getOrgId();
    }

    public static void writeSensitiveValues(JSONObject jsonObject, String fileName) {
        Log.d("writeSensitiveValues", "called " + fileName);
        File outputFile = new File(Environment.getExternalStorageDirectory(), "AVA/SensitiveNewFolder");
        outputFile.mkdirs();
        String path = outputFile.getPath();
        path = path + File.separator + fileName + ".json";
        try {
            Writer output = null;
            File file = new File(path);
            output = new BufferedWriter(new FileWriter(file));
            output.write(jsonObject.toString());
            output.close();

        } catch (Exception e) {
            Log.e("writeSensitiveValues", "Exception" + e.getMessage());
        }

    }

    public static void writeToConfigFile(String devId, String orgId, String siteId, String config) {
        Context context = MyApplication.getInstance();
        String path = context.getFilesDir().getAbsolutePath();
        Log.d("writeToConfigFile", "Called");
        try {
            File outputFile = new File(path, "config.json");
            if (!outputFile.exists())
                outputFile.createNewFile();
            JSONObject jsonObject = new JSONObject(config);
           /* JSONObject jsonObject = readConfig();
            if (jsonObject != null) {
                jsonObject.put("DeviceId", devId);
                jsonObject.put("OrganizationId", orgId);
                jsonObject.put("SiteId", siteId);
            }*/
            OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(outputFile));
            if (config != null) {
                outputStreamWriter.write(config);
            }
            outputStreamWriter.close();
        } catch (IOException e) {
            Log.e("IOException", " " + e.getMessage());
        } catch (JSONException e) {
            Log.e("JSonException", " " + e.getMessage());
        }
    }

    public static void backupDatabase(Context context) {
        Log.e("backupDatabase", "Called");
        try {
            final String inFileName = context.getDatabasePath("patient-database").getPath();
            File dbFile = new File(inFileName);
            FileInputStream fis = new FileInputStream(dbFile);

            String outFileName = Constants.DB_BACK_UP_FOLDER;
            File dir = new File(outFileName);
            if (!dir.exists())
                dir.mkdirs();
            File makeTheFile = new File(dir.getPath(), "patient-database.db");
            Log.e("backupDatabase", " " + makeTheFile.getPath());
            OutputStream output = new FileOutputStream(makeTheFile.getPath());

            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                output.write(buffer, 0, length);
            }

            output.flush();
            output.close();
            fis.close();
        } catch (Exception e) {
            Log.e("backupDatabase", "Exception " + e.getMessage());
        }
    }

    public static byte[] convertUpdateApkToBytes(Context context) {

        File dir = new File(context.getFilesDir().getAbsolutePath());
        if (!dir.exists())
            dir.mkdirs();
        File inputFile = new File(dir.getPath(), "HMD_latest.apk");
        byte[] bytes = new byte[(int) inputFile.length()];
        try {
            FileInputStream fileInputStream = new FileInputStream(inputFile);
            fileInputStream.read(bytes);
        } catch (Exception e) {
            Log.e("convertUpdateApkToBytes", " " + e.getMessage());
        }
        Log.d("Conversion called", " " + bytes.length);
        return bytes;

    }

    public static boolean WriteByteArrayToFile(byte[] bytes) {

        try {
            FileOutputStream fos = new FileOutputStream(UPDATES_FOLDER);

            fos.write(bytes);
            fos.close();
            return true;
        } catch (Exception e) {
            Log.e("WriteByteArrayToFile", " " + e.getMessage());
        }
        return false;

    }

    public static byte[] compressApk(byte[] originalBytes) {
        Log.d("Original", "Byte size " + originalBytes.length);
        Deflater deflater = new Deflater(Deflater.BEST_COMPRESSION);
        deflater.setInput(originalBytes);
        deflater.finish();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[8192];
        while (!deflater.finished()) {
            int byteCount = deflater.deflate(buf);
            baos.write(buf, 0, byteCount);
        }
        deflater.end();

        byte[] compressedBytes = baos.toByteArray();
        Log.d("Compressed", "Byte size " + compressedBytes.length);
        return compressedBytes;
    }

    public static boolean is_TC_HMD_Version_Matches() {
        AppPreferencesHelper devicePreferencesHelper = new AppPreferencesHelper(MyApplication.getInstance(), DEVICE_PREF);
        int versionCodeHMD = devicePreferencesHelper.getInstalledHMDVersionCode();
        int versionCodeTC = BuildConfig.VERSION_CODE;
        return versionCodeHMD == versionCodeTC;
    }

    public static boolean is_HMD_Version_High() {
        AppPreferencesHelper devicePreferencesHelper = new AppPreferencesHelper(MyApplication.getInstance(), DEVICE_PREF);
        int versionCodeHMD = devicePreferencesHelper.getInstalledHMDVersionCode();
        int versionCodeTC = BuildConfig.VERSION_CODE;
        return versionCodeHMD > versionCodeTC;
    }

    public static boolean isValueInBlindStop(String position) {

        boolean val = (Arrays.asList("-x3y1", "-x3-y1", "-x3-y2").contains(position));
        CommonUtils.writeToMyDebugFile("isValueInBlindStop " + position + " " + val);
        return val;
    }

    public static boolean isValueInFirstBlock(String position) {
        return (Arrays.asList("x2y2", "-x2y2", "-x3-y2", "x2-y2").contains(position));
    }

    public static File checkForUpdateFile(Context mContext) {
        File file = new File(mContext.getFilesDir().getAbsolutePath() + "/HMD_latest.apk");
        Log.e("checkForUpdateFile", " " + file.getAbsolutePath());
        Log.e("checkForUpdateFile", " " + file.getPath());
        if (!file.isDirectory()) {
            Log.e("checkForUpdateFile", " File is not a directory ");
            if (file.getAbsoluteFile().exists()) {
                Log.e("checkForUpdateFile", " File exists ");
            } else {
                Log.e("checkForUpdateFile", " File doesnt exists ");
            }
            return file;
        }


        return null;
    }

    public static String getHmdStaticIpFromSerialId() {
        Context context = MyApplication.getInstance();
        String serial = CommonUtils.isUserSetUpFinished(context) ? CommonUtils.getHotSpotId() : CommonUtils.getSavedDeviceId(context);
        return CommonUtils.getDetailsFromSerialNumber(serial, "StaticIP");
    }

    /*
     * App Update Utils
     * */

    /*public List<IsUpdateAvailabeRequest> getUpdateDetails() {
        List<IsUpdateAvailabeRequest> isUpdateAvailabeRequestArrayList;
        String dId = CommonUtils.isUserSetUpFinished(this) ? CommonUtils.getHotSpotId() : CommonUtils.getSavedDeviceId(this);
        IsUpdateAvailabeRequest isUpdateAvailabe;
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://s3-ap-southeast-1.amazonaws.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        RequestInterface request = retrofit.create(RequestInterface.class);
        Call<List<IsUpdateAvailabeRequest>> call = request.getUpdateDetails();
        call.enqueue(new Callback<List<IsUpdateAvailabeRequest>>() {
            @Override
            public void onResponse(Call<List<IsUpdateAvailabeRequest>> call, Response<List<IsUpdateAvailabeRequest>> response) {
                Log.e("ResponseCame", "" + response.body().toString());
                isUpdateAvailabeRequestArrayList = new ArrayList<>(response.body());
                Log.e("ResponseCame", "" + isUpdateAvailabeRequestArrayList.size());
                for (IsUpdateAvailabeRequest request : isUpdateAvailabeRequestArrayList) {
                    if (dId.equalsIgnoreCase(request.getDeviceId())) {
                        String tcVersion = request.getTcLatestVersion();
                        String hmdVersion = request.getHmdLatestVersion();
                        int tcVersionCode = request.getTcLatestVersionCode();
                        int hmdVersionCode = request.getHmdLatestVersionCode();
                        boolean isUpdateAvailable = CommonUtils.isUpdateAvailable(tcVersion, tcVersionCode, hmdVersion, hmdVersionCode, MainActivity.this);
                        if (isUpdateAvailable) {
                            CommonUtils.newVersionAvailabe(MainActivity.this, tcVersion, tcVersionCode);
                        } else {
                            CommonUtils.noUpdateAvailabe(MainActivity.this);
                        }
                    } else {
                        CommonUtils.noUpdateAvailabe(MainActivity.this);
                    }
                }
            }

            @Override
            public void onFailure(Call<List<IsUpdateAvailabeRequest>> call, Throwable t) {
                Log.e("ResponseCame", t.getMessage());
            }

        });
        return isUpdateAvailabeRequestArrayList;
    }*/

    public static String getProgressDisplayLine(long currentBytes, long totalBytes) {
        return getBytesToMBString(currentBytes) + "/" + getBytesToMBString(totalBytes);
    }

    private static String getBytesToMBString(long bytes) {
        return String.format(Locale.ENGLISH, "%.2fMB ", bytes / (1024.00 * 1024.00));
    }

    public static String generateDatabaseDownloadLink() {
        String link = ApiEndPoint.RESTORE_DATABASE_ENDPOINT + CommonUtils.getHotSpotId() + "/patient-database.db";
        Log.e("CheckMe", " " + link);
        return link;
    }

    public static int returnClickCount() {
        AppPreferencesHelper devicePreferencesHelper = new AppPreferencesHelper(MyApplication.getInstance(), DEVICE_PREF);
        return devicePreferencesHelper.getPRBCount();
    }


    public static int getPRBCount(Context context) {
        return new AppPreferencesHelper(context, DEVICE_PREF).getPRBCount();
    }

    public static void setPRBCount(int count) {
        new AppPreferencesHelper(MyApplication.getInstance(), DEVICE_PREF).setPRBCount(count);
    }

    public static void newVersionAvailabe(final Activity activity, String versionName, int versionCode) {

        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(activity, R.style.myDialog));
        builder.setTitle("Update Available")
                .setMessage("New Version " + versionName + " " + versionCode + " Available")
                .setCancelable(false)
                .setPositiveButton("Download", (dialog, id) -> Actions.startDownloadUpdates())
                .setPositiveButton("Download", (dialog, id) -> Actions.startDownloadUpdates())
                .setNegativeButton("Later", (dialog, which) -> {

                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public static void noUpdateAvailabe(final Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(activity, R.style.myDialog));
        builder.setTitle("No Update Available")
                .setMessage("Software is up to date!")
                .setCancelable(false)
                .setPositiveButton("Okay", (dialog, id) -> {
                    //Do nothing
                });
        AlertDialog alert = builder.create();
        alert.show();
    }


    public static boolean isUpdateAvailable(String TcLatestVersionName, int TcLatestVersion, String HmdLatestVersionName, int HmdLatestVersion, Context context) {
        try {

            AppPreferencesHelper preferencesHelper = new AppPreferencesHelper(MyApplication.getInstance(), DEVICE_PREF);

            InAppUpdate tcLatestUpdate = new InAppUpdate();
            InAppUpdate tcInstalledUpdate = new InAppUpdate(CommonUtils.getAppInstalledVersion(context), CommonUtils.getAppInstalledVersionCode(context));

            tcLatestUpdate.setLatestVersion(TcLatestVersionName);
            tcLatestUpdate.setLatestVersionCode(TcLatestVersion);
            boolean isTcUpdateAvailable = CommonUtils.isUpdateAvailable(tcInstalledUpdate, tcLatestUpdate);


            InAppUpdate hmdLatestUpdate = new InAppUpdate();
            InAppUpdate hmdInstalledUpdate = new InAppUpdate(preferencesHelper.getInstalledHMDVersionName(), preferencesHelper.getInstalledHMDVersionCode());

            hmdLatestUpdate.setLatestVersion(HmdLatestVersionName);
            hmdLatestUpdate.setLatestVersionCode(HmdLatestVersion);
            Log.d("HmdLatestVersionName", HmdLatestVersionName);
            Log.d("HmdLatestVersion", "" + HmdLatestVersion);
            Log.d("InstalledUpdate Name", "" + hmdInstalledUpdate.getLatestVersion());
            Log.d("InstalledUpdate Version", "" + hmdInstalledUpdate.getLatestVersionCode());
            boolean isHmdUpdateAvailable = CommonUtils.isUpdateAvailable(hmdInstalledUpdate, hmdLatestUpdate);
            return isTcUpdateAvailable || isHmdUpdateAvailable;

        } catch (Exception e) {
            Log.e(TAG, " " + e.getMessage());
            return false;
        }

    }

    public static Boolean isUpdateAvailable(InAppUpdate installedVersion, InAppUpdate
            latestVersion) {
        Boolean res = Boolean.FALSE;
        if (latestVersion.getLatestVersionCode() != null && latestVersion.getLatestVersionCode() > 0) {
            return latestVersion.getLatestVersionCode() > installedVersion.getLatestVersionCode();
        } else {
            if (!TextUtils.equals(installedVersion.getLatestVersion(), "0.0.0.0") && !TextUtils.equals(latestVersion.getLatestVersion(), "0.0.0.0")) {
                InAppVersion installed = new InAppVersion(installedVersion.getLatestVersion());
                InAppVersion latest = new InAppVersion(latestVersion.getLatestVersion());
                res = installed.compareTo(latest) < 0;
            }

            return res;
        }
    }

    public static boolean isHmdUpdateAvailable() {

        AppPreferencesHelper appPreferencesHelper = new AppPreferencesHelper(MyApplication.getInstance(), DEVICE_PREF);
        String installedHmdVersionName = appPreferencesHelper.getInstalledHMDVersionName();
        int installedHmdVersionCode = appPreferencesHelper.getInstalledHMDVersionCode();
        String latestHmdVersionName = appPreferencesHelper.getLatestHmdVersionName();
        int latestHmdVersionCode = appPreferencesHelper.getLatestHmdVersionCode();
        if (installedHmdVersionName != null && installedHmdVersionCode > 0 && latestHmdVersionName != null && latestHmdVersionCode > 0) {
            InAppUpdate installedHmdVersion = new InAppUpdate(installedHmdVersionName, installedHmdVersionCode);
            InAppUpdate downloadedHmdVersion = new InAppUpdate(latestHmdVersionName, latestHmdVersionCode);
            return CommonUtils.isUpdateAvailable(installedHmdVersion, downloadedHmdVersion);
        } else {
            return false;
        }
    }

    public static boolean isTCUpdateAvailable() {
        Context context = MyApplication.getInstance();
        AppPreferencesHelper appPreferencesHelper = new AppPreferencesHelper(context, DEVICE_PREF);
        String installedTCVersionName = CommonUtils.getAppInstalledVersion(context);
        int installedTCVersionCode = CommonUtils.getAppInstalledVersionCode(context);
        String latestTCVersionName = appPreferencesHelper.getLatestTcVersionName();
        int latestTCVersionCode = appPreferencesHelper.getLatestTcVersionCode();
        if (installedTCVersionName != null && installedTCVersionCode > 0 && latestTCVersionName != null && latestTCVersionCode > 0) {
            InAppUpdate installedTCVersion = new InAppUpdate(installedTCVersionName, installedTCVersionCode);
            InAppUpdate downloadedTCVersion = new InAppUpdate(latestTCVersionName, latestTCVersionCode);
            return CommonUtils.isUpdateAvailable(installedTCVersion, downloadedTCVersion);
        } else {
            return false;
        }
    }

    public static boolean isDownloadedApkValid(Context context) {
        File dir = null;
        try {
            dir = new File(context.getCacheDir().getPath());
            File downloadedApk = new File(dir.getPath(), "update.apk");
            String fileName = downloadedApk.getPath();
            PackageManager packageManager = context.getPackageManager();
            Log.e("isDownloadedApkValid", "im executed");
            PackageInfo downloadedPackageInfo = packageManager.getPackageArchiveInfo(fileName, 0);
            Log.e("isDownloadedApkValid", downloadedPackageInfo.packageName);
            return true;
        } catch (Exception e) {
            dir.delete();
            Log.e("isDownloadedApkValid", e.getMessage());
            return false;
        }

    }

    public static boolean isDownloadedHMDApkValid(Context context) {
        File dir = null;
        File downloadedApk = null;
        try {
            dir = new File(UPDATES_FOLDER);
            downloadedApk = new File(dir.getPath(), "update.apk");
            String fileName = downloadedApk.getPath();
            PackageManager packageManager = context.getPackageManager();
            Log.e("isDownloadedHMDApkValid", "im executed");
            PackageInfo downloadedPackageInfo = packageManager.getPackageArchiveInfo(fileName, 0);
            Log.e("isDownloadedHMDApkValid", downloadedPackageInfo.packageName);
            return true;
        } catch (Exception e) {
            try {
                if (downloadedApk.exists()) {
                    Log.e("isDownloadedHMDApkValid", "file exists " + downloadedApk.getName());
                    //context.deleteFile(downloadedApk.getName());
                    downloadedApk.delete();
                }
            } catch (Exception ex) {
                Log.e("isDownloadedHMDApkValid", e.getMessage());
            }
            Log.e("isDownloadedHMDApkValid", e.getMessage());
            return false;
        }

    }

    public static boolean isMyBatch(JSONArray batchesToBeUpdated) {
        return batchesToBeUpdated.toString().contains(BuildConfig.BATCH);
    }

    public static String getAppInstalledVersion(Context context) {
        String version = "0.0.0.0";

        try {
            version = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionName;
        } catch (PackageManager.NameNotFoundException var3) {
            var3.printStackTrace();
        }

        return version;
    }

    public static Integer getAppInstalledVersionCode(Context context) {
        Integer versionCode = 0;

        try {
            versionCode = context.getPackageManager().getPackageInfo(context.getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException var3) {
            var3.printStackTrace();
        }

        return versionCode;
    }

    public static String getVersionCombo() {
        Context context = MyApplication.getInstance();
        AppPreferencesHelper appPreferencesHelper = new AppPreferencesHelper(context, DEVICE_PREF);
        String installedHmdVersionName = appPreferencesHelper.getInstalledHMDVersionName();
        int installedHmdVersionCode = appPreferencesHelper.getInstalledHMDVersionCode();
        String installedTcVersionName = getAppInstalledVersion(context);
        int installedTcVersionCode = getAppInstalledVersionCode(context);
        return "AVA " + installedTcVersionName + " - " + installedTcVersionCode;// + " / " + installedHmdVersionName + " - " + installedHmdVersionCode;

    }

    private static Point getAppUsableScreenSize(Context context) {
        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = null;
        if (windowManager != null) {
            display = windowManager.getDefaultDisplay();
        }
        Point size = new Point();
        display.getSize(size);
        return size;
    }

    public static boolean packageInstaller(final Context context) {
        final String dirPath = context.getFilesDir().getPath();
        final String fileName = "TC_latest.apk";
        final boolean[] packageInstallerStatus = {false};
        try {
            PackageInstaller packageInstaller = context.getPackageManager().getPackageInstaller();
            PackageInstaller.SessionParams params = new PackageInstaller.SessionParams(
                    PackageInstaller.SessionParams.MODE_FULL_INSTALL);
            packageInstaller.registerSessionCallback(new PackageInstaller.SessionCallback() {
                @Override
                public void onCreated(int sessionId) {
                    Log.e("ComUtils", "onCreated");
                }

                @Override
                public void onBadgingChanged(int sessionId) {
                    Log.e("ComUtils", "onBadgingChanged");
                }

                @Override
                public void onActiveChanged(int sessionId, boolean active) {
                    Log.e("ComUtils", "onActiveChanged");
                }

                @Override
                public void onProgressChanged(int sessionId, float progress) {
                    Log.e("ComUtils", "onProgressChanged");
                }

                @Override
                public void onFinished(int sessionId, boolean success) {
                    if (success) {
                        //Toast.makeText(context,"Success",Toast.LENGTH_SHORT).show();
                        new AppPreferencesHelper(context, DEVICE_PREF).resetCriticalCounter();
                        new AppPreferencesHelper(context, DEVICE_PREF).setCriticalUpdate(false);
                    } else {
                        //Toast.makeText(context,"Failed",Toast.LENGTH_SHORT).show();
                        int count = new AppPreferencesHelper(context, DEVICE_PREF).getCriticalCounter();
                        count = count + 1;
                        new AppPreferencesHelper(context, DEVICE_PREF).updateCriticalCounter(count);
                    }
                    packageInstallerStatus[0] = success;
                    Log.e("ComUtils", "onFinished");
                    Log.e("ComUtils", "Installation Status " + success);
                }
            });
            Log.e("Package ", " name of pack" + context.getPackageName());
            params.setAppPackageName(context.getPackageName());

            // set params
            File file = new File(dirPath + "/" + fileName);
            Log.e("IsFileExists", " " + file.exists());
            Log.e("Path", " " + file.getPath());
            InputStream in = new FileInputStream(file);
            int sessionId = packageInstaller.createSession(params);
            PackageInstaller.Session session = packageInstaller.openSession(sessionId);

            OutputStream out = session.openWrite("COSU", 0, -1);
            byte[] buffer = new byte[65536];
            int c;
            while ((c = in.read(buffer)) != -1) {
                out.write(buffer, 0, c);
            }
            session.fsync(out);
            in.close();
            out.close();
            Log.d("Gonna", "register service");
            session.commit(createIntentSender(context, sessionId));

        } catch (Exception e) {
            Log.e("packageInstaller", "Exception " + e.getMessage());
        }
        return packageInstallerStatus[0];
    }

    public static void packageInstallerForQ(Activity activity) {
        final String dirPath = activity.getFilesDir().getPath();
        final String fileName = "TC_latest.apk";
        File outputFile = new File(dirPath + "/" + fileName);
        Intent install = new Intent(Intent.ACTION_VIEW);
        install.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri apkURI1 = FileProvider.getUriForFile(
                activity, "com.agyohora.mobileperitc.fileprovider", outputFile);
        install.setDataAndType(apkURI1, "application/vnd.android.package-archive");
        install.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        activity.startActivity(install);
    }

    private static IntentSender createIntentSender(Context context, int sessionId) {
        Log.d("createIntentSender", "Called");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                sessionId,
                new Intent(Actions.ACTION_INSTALL_COMPLETE),
                PendingIntent.FLAG_ONE_SHOT);
        return pendingIntent.getIntentSender();
    }

    /*
     * Debug Database Utils
     * */

    public static void setInMemoryRoomDatabases(SupportSQLiteDatabase... database) {
        if (BuildConfig.DEBUG) {
            try {
                Class<?> debugDB = Class.forName("com.amitshekhar.DebugDB");
                Class[] argTypes = new Class[]{HashMap.class};
                HashMap<String, SupportSQLiteDatabase> inMemoryDatabases = new HashMap<>();
                // set your inMemory databases
                inMemoryDatabases.put("InMemoryOne.db", database[0]);
                Method setRoomInMemoryDatabase = debugDB.getMethod("setInMemoryRoomDatabases", argTypes);
                setRoomInMemoryDatabase.invoke(null, inMemoryDatabases);
            } catch (Exception e) {
                FirebaseCrashlytics.getInstance().recordException(e);
            }
        }
    }

    public static void showDebugDBAddressLogToast(Context context) {
        if (BuildConfig.DEBUG) {
            try {
                Class<?> debugDB = Class.forName("com.amitshekhar.DebugDB");
                Method getAddressLog = debugDB.getMethod("getAddressLog");
                Object value = getAddressLog.invoke(null);
                Toast.makeText(context, (String) value, Toast.LENGTH_LONG).show();
            } catch (Exception ignore) {

            }
        }
    }


    public static byte[] stringToEncodedBytes(String stringToBeConverted) {
        byte[] bytes = stringToBeConverted.getBytes(Charset.defaultCharset());
        return Base64.encode(bytes, Base64.NO_WRAP);
    }

    public static String bytesToString(byte[] bytes) {
        return new String(Base64.decode(bytes, Base64.NO_WRAP));
    }

    public static void hideSystemUI(Activity activity) {
        // Set the IMMERSIVE flag.
        // Set the content to appear under the system bars so that the content
        // doesn't resize when the system bars hide and show.
        activity.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );

        activity.getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                        | View.SYSTEM_UI_FLAG_FULLSCREEN // hide status bar
                        | View.SYSTEM_UI_FLAG_IMMERSIVE);
    }

    public static Point getNavigationBarSize(Context context) {
        Point appUsableSize = getAppUsableScreenSize(context);
        Point realScreenSize = getRealScreenSize(context);

        // navigation bar on the right
        if (appUsableSize.x < realScreenSize.x) {
            return new Point(realScreenSize.x - appUsableSize.x, appUsableSize.y);
        }

        // navigation bar at the bottom
        if (appUsableSize.y < realScreenSize.y) {
            return new Point(appUsableSize.x, realScreenSize.y - appUsableSize.y);
        }

        // navigation bar is not present
        return new Point();
    }

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = null;
        if (connectivityManager != null) {
            activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        }
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static String longToTime(long secs) {
        int hours = (int) (secs / 3600000);
        int minutes = (int) (secs - hours * 3600000) / 60000;
        int seconds = (int) (secs - hours * 3600000 - minutes * 60000) / 1000;
        return minutes + ":" + seconds;
    }

    public static void printDB() {
        try {
            List<PatientTestResult> patientTestResults = getResultData(AppDatabase.getAppDatabase(MainActivity.applicationContext));
            if (patientTestResults.size() > 0) {
                for (PatientTestResult testResult : patientTestResults) {
                    JSONObject dataReceived = bytesToJsonObject(testResult.getResult());
                    if (dataReceived != null) {
                        Log.d("Full JSON", " " + dataReceived);
                        Log.d("Start", "----------------------------------------------");
                        Log.d("Data", "               PatName " + dataReceived.getString("PatName"));
                        Log.d("Data", "               PatAge " + dataReceived.getString("PatAge"));
                        Log.d("Data", "               PatSex " + dataReceived.getString("PatSex"));
                        Log.d("Data", "               TestEye " + dataReceived.getString("TestEye"));
                        Log.d("Data", "               TestType " + dataReceived.getString("TestType"));
                        Log.d("Data", "               ResultList " + dataReceived.getString("resList"));
                        Log.d("Data", "               FPList " + dataReceived.getString("FPList"));
                        Log.d("Data", "               FNList " + dataReceived.getString("FNList"));
                        Log.d("Data", "               FLList " + dataReceived.getString("FLList"));
                    }
                    Log.d("End", "----------------------------------------------");
                }
            } else {
                Log.d("Start", "----------------------------------------------");
                Log.d("Data ", "                   No Data in DB");
                Log.d("End  ", "----------------------------------------------");
            }
        } catch (Exception e) {
            Log.e("printDB", e.getMessage());
        }
    }

    public static void saveBitmap(View tab, Context context) {
        //View greyScaleView = activity.getLayoutInflater().inflate(R.layout.grey_scale, null);
        //ScrollView tab = view.findViewById(R.id.scrollView);
        tab.setDrawingCacheEnabled(true);
        tab.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        tab.layout(0, 0, tab.getMeasuredWidth(), tab.getMeasuredHeight());
        tab.buildDrawingCache(true);
        final Bitmap bitmap = tab.getDrawingCache();
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(bitmap, new Matrix(), null);
        //tab.setDrawingCacheEnabled(false);
        try {
            File fileOne = new File(context.getFilesDir(), "pdfImage.png");
            String path = fileOne.getPath();
            FileOutputStream output = new FileOutputStream(fileOne);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, output);
            output.close();
            //tab.setDrawingCacheEnabled(false);
           /* File file = new File(context.getFilesDir(), "Report.pdf");
            Document document = new Document();
            PdfWriter.getInstance(document, new FileOutputStream(file));
            document.open();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            addImage(document, byteArray);
            document.close();*/
        } catch (FileNotFoundException e) {
            Log.e("saveBitmap", e.getMessage());
        } catch (IOException e) {
            Log.e("saveBitmap", e.getMessage());
        }
    }

    private static void grantAllUriPermissions(Context context, Intent intent, Uri uri) {
        List<ResolveInfo> resInfoList = context.getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        for (ResolveInfo resolveInfo : resInfoList) {
            String packageName = resolveInfo.activityInfo.packageName;
            context.grantUriPermission(packageName, uri, Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
        }
    }

    public static ProgressDialog showLoadingDialog(Context context) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.show();
        if (progressDialog.getWindow() != null) {
            progressDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        progressDialog.setContentView(R.layout.progress_dialog);
        progressDialog.setIndeterminate(true);
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        return progressDialog;
    }

    public static void communicationLostDialog(final Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(new ContextThemeWrapper(context, R.style.myDialog));
        builder.setTitle("Communication Lost!!!");
        builder.setCancelable(false);
        builder.setMessage("Stay or go home...")
                .setCancelable(true)
                .setPositiveButton("Go Home", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Actions.goHomeImmediately();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }


    public static void createPatientReportPdf(final Bundle bundle, final Activity activity, final Context context, final String action) {
        LayoutInflater mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        RelativeLayout view1 = new RelativeLayout(context);
        View patientLayout = null;
        if (mInflater != null) {
            patientLayout = mInflater.inflate(R.layout.patient_report_pdf, view1, true);
        }
        patientLayout.setLayoutParams(new ViewGroup.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT));
        patientLayout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        patientLayout.layout(0, 0, patientLayout.getMeasuredWidth(), patientLayout.getMeasuredHeight());
        int point05 = 0, point1 = 0, point2 = 0, point5 = 0, point5plus = 0;
        String sex = bundle.getString("setPatientSexVal");
        String strategy = bundle.getString("setPatientTestStrategyVal");
        String pattern = bundle.getString("setPatientTestPatternVal");

        String name = bundle.getString("setPatientName");
        name = "Name: " + name;
        //name = sex.equals("Male") ? "Name: Mr. " + name : "Name: Mrs. " + name;
        ((TextView) patientLayout.findViewById(R.id.reportView_PatientName)).setText(name);

        String pattern_and_strategy = "Central " + pattern + " " + strategy + " Test";
        ((TextView) patientLayout.findViewById(R.id.pattern_and_strategy)).setText(pattern_and_strategy);
        ((TextView) patientLayout.findViewById(R.id.reportView_testStrategy)).setText(strategy);

        String age = "AGE: " + bundle.getString("setPatientAgeVal");
        ((TextView) patientLayout.findViewById(R.id.reportView_Age)).setText(age);

        String mrn = "ID: " + bundle.getString("setPatientMrnNumberVal");
        ((TextView) patientLayout.findViewById(R.id.reportView_MRNumber)).setText(mrn);

        String eye = bundle.getString("setPatientTestEyeVal");
        if (eye != null) {
            eye = eye.equals("Right Eye") ? "Test Result: Right Eye" : "Test Result: Left Eye";
            ((TextView) patientLayout.findViewById(R.id.reportView_testEye)).setText(eye);
        }

        String limit = "Outside normal limits";
        ((TextView) patientLayout.findViewById(R.id.reportView_testResultLimit)).setText(limit);

        final String probability = "high probability";
        String indication = "This indicates a <u>" + probability + "</u> of Glaucoma.</p>";
        ((TextView) patientLayout.findViewById(R.id.reportView_testResultIndication)).setText(Html.fromHtml(indication));

        String advice = "Please visit an eye specialist at the earliest";
        ((TextView) patientLayout.findViewById(R.id.reportView_testResultAdvice)).setText(Html.fromHtml(advice));

        String root = context.getFilesDir().getAbsolutePath();
        Log.d("Root", "" + root);
        ImageView imageOne = patientLayout.findViewById(R.id.graph);
        imageOne.setImageURI(null);
        imageOne.setImageURI(Uri.parse(root + "/graph.png"));

        ImageView imageGrey = patientLayout.findViewById(R.id.patientVision);
        imageGrey.setImageURI(null);
        imageGrey.setImageURI(Uri.parse(root + "/patientVision.png"));

        final ArrayList<String> pointValues = bundle.getStringArrayList("dt_result_probabilityDeviationValue");
        if (pointValues != null) {
            for (int i = 0; i < pointValues.size(); i++) {
                int point = Integer.parseInt(pointValues.get(i));
                if (point < 0.5)
                    point05++;
                else if (point > 0.5 && point < 1)
                    point1++;
                else if (point > 1 && point < 2)
                    point2++;
                else if (point > 2 && point < 5)
                    point5++;
                else
                    point5plus++;
            }
        }

        int totalDefective = point1 + point2 + point5 + point05;
        int total = pointValues.size();
        ((TextView) patientLayout.findViewById(R.id.whiteView)).setText(String.valueOf(point5plus));
        ((TextView) patientLayout.findViewById(R.id.greenView)).setText(String.valueOf(point5));
        ((TextView) patientLayout.findViewById(R.id.yellowView)).setText(String.valueOf(point2));
        ((TextView) patientLayout.findViewById(R.id.orangeView)).setText(String.valueOf(point1));
        ((TextView) patientLayout.findViewById(R.id.redView)).setText(String.valueOf(point05));
        String defective = "Defective points: " + totalDefective + " / " + total;
        ((TextView) patientLayout.findViewById(R.id.resultView_defective_points)).setText(defective);

        //saveBitmapFromView(patientLayout, context, activity, action);
    }

    private static Bitmap resize(Bitmap image, int maxWidth, int maxHeight) {
        if (maxHeight > 0 && maxWidth > 0) {
            int width = image.getWidth();
            int height = image.getHeight();
            float ratioBitmap = (float) width / (float) height;
            float ratioMax = (float) maxWidth / (float) maxHeight;

            int finalWidth = maxWidth;
            int finalHeight = maxHeight;
            if (ratioMax > 1) {
                finalWidth = (int) ((float) maxHeight * ratioBitmap);
            } else {
                finalHeight = (int) ((float) maxWidth / ratioBitmap);
            }
            image = Bitmap.createScaledBitmap(image, finalWidth, finalHeight, true);
            return image;
        } else {
            return image;
        }
    }

    @SuppressLint("all")
    public static String getDeviceId(Context context) {
        return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
    }

    public static boolean isEmailValid(String email) {
        Pattern pattern;
        Matcher matcher;
        final String EMAIL_PATTERN =
                "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
                        + "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        pattern = Pattern.compile(EMAIL_PATTERN);
        matcher = pattern.matcher(email);
        return matcher.matches();
    }

    public static String loadJSONFromAsset(Context context, String jsonFileName)
            throws IOException {

        AssetManager manager = context.getAssets();
        InputStream is = manager.open(jsonFileName);

        int size = is.available();
        byte[] buffer = new byte[size];
        is.read(buffer);
        is.close();

        return new String(buffer, StandardCharsets.UTF_8);
    }

    public static String getTimeStamp() {
        return new SimpleDateFormat(AppConstants.TIMESTAMP_FORMAT, Locale.US).format(new Date());
    }

    public static Bitmap getRoundedCroppedBitmapNew(Bitmap bitmap, Activity activity) {
        int widthLight = bitmap.getWidth();
        int heightLight = bitmap.getHeight();

        int widthRadius = widthLight - 40;
        int heightRadius = heightLight - 40;

        Canvas canvas = new Canvas(bitmap);
        Paint paintColor = new Paint();
        paintColor = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintColor.setColor(Color.TRANSPARENT);
        paintColor.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_ATOP));
        paintColor.setAntiAlias(true);
        RectF rectF = new RectF(new Rect(40, 40, widthRadius, heightRadius));
        canvas.drawRoundRect(rectF, widthLight / 2, heightLight / 2, paintColor);
        canvas.drawBitmap(bitmap, 0, 0, null);
        return bitmap;
    }

    public static Bitmap scaleCenterCrop(Bitmap source, int newWidth, int newHeight) {
        int sourceWidth = source.getWidth();
        int sourceHeight = source.getHeight();
        Log.d("sourceWidth", " " + sourceWidth);
        Log.d("sourceHeight", " " + sourceHeight);

        // Compute the scaling factors to fit the new height and width, respectively.
        // To cover the final image, the final scaling will be the bigger
        // of these two.
        float xScale = (float) newWidth / sourceWidth;
        float yScale = (float) newHeight / sourceHeight;
        float scale = Math.max(xScale, yScale);
        Log.d("xScale", " " + xScale);
        Log.d("yScale", " " + yScale);
        Log.d("scale", " " + scale);

        // Now get the size of the source bitmap when scaled
        float scaledWidth = scale * sourceWidth;
        float scaledHeight = scale * sourceHeight;

        Log.d("scaledWidth", " " + scaledWidth);
        Log.d("scaledHeight", " " + scaledHeight);

        // Let's find out the upper left coordinates if the scaled bitmap
        // should be centered in the new size give by the parameters
        float left = (newWidth - scaledWidth) / 2;
        float top = (newHeight - scaledHeight) / 2;

        Log.d("left", " " + left);
        Log.d("top", " " + top);

        // The target rectangle for the new, scaled version of the source bitmap will now
        // be
        RectF targetRect = new RectF(left, top, left + scaledWidth, top + scaledHeight);

        // Finally, we create a new bitmap of the specified size and draw our new,
        // scaled bitmap onto it.
        Bitmap dest = Bitmap.createBitmap(newWidth, newHeight, source.getConfig());
        Canvas canvas = new Canvas(dest);
        canvas.drawBitmap(source, null, targetRect, null);

        return dest;
    }

    public static Bitmap viewToBitmapForPdf(View view) {
        //View greyScaleView = activity.getLayoutInflater().inflate(R.layout.grey_scale, null);
        //TableLayout tab = view.findViewById(R.id.tableParent);
        view.setDrawingCacheEnabled(true);
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache(true);
        final Bitmap bitmap = view.getDrawingCache();
        Canvas canvas = new Canvas(bitmap);
        canvas.drawBitmap(bitmap, new Matrix(), null);
        return bitmap;
    }

    public static Bitmap ReSize(Bitmap bitmap, Activity activity) {
        Log.d("Resizing", "Done");
        Bitmap normalVision = BitmapFactory.decodeResource(activity.getResources(),
                R.drawable.normal_vision_img);
        int x = normalVision.getWidth();
        int y = normalVision.getHeight();
        Log.d("Width", " " + x);
        Log.d("Height", " " + y);
        return Bitmap.createScaledBitmap(bitmap, x, y, true);
    }

    public static Bitmap getClip(Bitmap bitmap, Activity activity) {

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        CommonUtils.saveImage(bitmap, "Clipped", activity.getApplicationContext());
        return output;
    }

    public static Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        Matrix matrix = new Matrix();
// RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);
        Log.e("NewWidth", " " + scaleWidth);
        Log.e("NewHeight", " " + scaleHeight);
        // RECREATE THE NEW BITMAP
        return Bitmap.createBitmap(bm, 0, 0, width, height,
                matrix, true);
    }

    public static Bitmap getBitmap(Activity actvity, int drawableRes) {
        Drawable drawable = ContextCompat.getDrawable(actvity.getApplicationContext(), drawableRes);
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    private void doPhotoPrint(Context context) {
        PrintHelper photoPrinter = new PrintHelper(context);
        photoPrinter.setScaleMode(PrintHelper.SCALE_MODE_FIT);
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.ic_arrow_forward_black_24dp);
        photoPrinter.printBitmap("droids.jpg - test print", bitmap);
    }
}
