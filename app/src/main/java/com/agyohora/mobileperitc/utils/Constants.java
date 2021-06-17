package com.agyohora.mobileperitc.utils;

import android.os.Environment;

/**
 * Created by Invent on 29-1-18.
 */

public class Constants {

    public static final String DOB_FORMAT = "yyyy-MM-dd";
    public static final String DB_NAME = "patient-database";
    public static final String DB_BACK_UP_FOLDER = Environment.getExternalStorageDirectory() + "/AVA/backup/";
    public static final String UPDATES_FOLDER = Environment.getExternalStorageDirectory() + "/AVA/updates/";
    public static final String JSON_FOLDER = Environment.getExternalStorageDirectory() + "/AVA/results/";
    public static final String DISPLAY_LOGS_FOLDER = Environment.getExternalStorageDirectory() + "/AVA/logs/display/";
    public static final String MY_DEBUG_LOGS_FOLDER = Environment.getExternalStorageDirectory() + "/AVA/logs/mydebug/";
    public static final String TEST_TIME_LOGS_FOLDER = Environment.getExternalStorageDirectory() + "/AVA/logs/test/";

    public static final String TEN_DASH_TWO = Environment.getExternalStorageDirectory() + "/AVA/logs/tendashtwo/";
    public static final String TD_LOGS_FOLDER = Environment.getExternalStorageDirectory() + "/AVA/logs/TD_Logs/";
    public static final String SYS_LOGS_FOLDER = Environment.getExternalStorageDirectory() + "/AVA/logs/System_Logs/";


    public static final String BUG_FIX_LOGS_FOLDER = Environment.getExternalStorageDirectory() + "/AVA/logs/bugfix/";
    public static final String DATABASE_RESTORE_LOGS_FOLDER = Environment.getExternalStorageDirectory() + "/AVA/logs/database-restore/";
    public static final String CHRONOMETER_LOGS_FOLDER = Environment.getExternalStorageDirectory() + "/AVA/logs/chronometer/bugfix";
    public static final String BUG_FIX_CLICK_ACK_FOLDER = Environment.getExternalStorageDirectory() + "/AVA/logs/bugfix/ack";

    public static final String CONFIG_FILE_PATH = "AVA/settings/config.json";
    public static final String VECTOR_FILE_PATH = "AVA/vector/vector";
    public static final String VECTOR_FILE_PATH_ROOT = "/AVA/vector/";
    public static final String CONFIG_FILE_PATH_ROOT = "/AVA/settings/";
    public static final String DB_SYNC_WORK = "DBSYNC";
    public static final String DB_BACKUP_WORK = "DBBACKUP";
    public static final String DAILY_NUMBER_OF_REPORT = "NumberOfReportWork";
    public static final String APP_UPDATER_WORK = "APPUPDATE";
    public static final String AVA_IMG_FOLDER = Environment.getExternalStorageDirectory() + "/AVA/logs/images/";
    public static final String AVA_ZIP_FOLDER = Environment.getExternalStorageDirectory() + "/AVA/logs/zip/";


}
