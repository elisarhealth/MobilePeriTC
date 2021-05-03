package com.agyohora.mobileperitc.worksheduler.workcreator;

import android.content.Context;
import android.util.Log;

import androidx.work.Constraints;
import androidx.work.ExistingPeriodicWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.agyohora.mobileperitc.utils.Constants;
import com.agyohora.mobileperitc.worksheduler.works.AppUpdaterWorker;
import com.agyohora.mobileperitc.worksheduler.works.CheckForUpdatesWorker;
import com.agyohora.mobileperitc.worksheduler.works.DailyReportWork;
import com.agyohora.mobileperitc.worksheduler.works.DatabaseBackupWorker;
import com.agyohora.mobileperitc.worksheduler.works.RunImmediatelyWorker;
import com.agyohora.mobileperitc.worksheduler.works.SaveAsJsonWorker;
import com.agyohora.mobileperitc.worksheduler.works.SyncDbWorker;

import java.util.concurrent.TimeUnit;

/**
 * Created by Invent
 * Class used to define methods to create different works
 */

public class WorkCreator {

    Context workContext;

    public WorkCreator(Context context) {
        this.workContext = context;
    }

    public void runSyncDbWorker() {
        PeriodicWorkRequest.Builder syncDbWorkBuilder =
                new PeriodicWorkRequest.Builder(SyncDbWorker.class, 15, TimeUnit.MINUTES);
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED).build();
        syncDbWorkBuilder.setConstraints(constraints);
        PeriodicWorkRequest dbSyncPeriodicWorkRequest = syncDbWorkBuilder.build();
        WorkManager.getInstance(workContext).enqueueUniquePeriodicWork(Constants.DB_SYNC_WORK, ExistingPeriodicWorkPolicy.REPLACE, dbSyncPeriodicWorkRequest);
    }

    public void runAppUpdateWorker() {
        PeriodicWorkRequest.Builder appUpdaterWorkBuilder =
                new PeriodicWorkRequest.Builder(AppUpdaterWorker.class, 8, TimeUnit.HOURS);
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED).build();
        appUpdaterWorkBuilder.setConstraints(constraints);
        PeriodicWorkRequest appUpdaterPeriodicWorkRequest = appUpdaterWorkBuilder.build();
        WorkManager.getInstance(workContext).enqueueUniquePeriodicWork(Constants.APP_UPDATER_WORK, ExistingPeriodicWorkPolicy.REPLACE, appUpdaterPeriodicWorkRequest);
    }

    public void runCheckForUpdatesWorker() {
        OneTimeWorkRequest.Builder checkForUpdatesWorkBuilder = new OneTimeWorkRequest.Builder(CheckForUpdatesWorker.class)
                .addTag("CheckForUpdatesWorker");
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED).build();
        checkForUpdatesWorkBuilder.setConstraints(constraints);
        OneTimeWorkRequest oneTimeWorkRequest = checkForUpdatesWorkBuilder.build();
        WorkManager.getInstance(workContext).enqueue(oneTimeWorkRequest);
    }

    public void runImmediateSyncWork() {
        Log.e("runImmediateSyncWork", "called");
        OneTimeWorkRequest.Builder immediateSyncWorkBuilder = new OneTimeWorkRequest.Builder(RunImmediatelyWorker.class)
                .addTag("ImmediateSync");
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED).build();
        immediateSyncWorkBuilder.setConstraints(constraints);
        OneTimeWorkRequest oneTimeWorkRequest = immediateSyncWorkBuilder.build();
        WorkManager.getInstance(workContext).enqueue(oneTimeWorkRequest);
    }

    public void runImmediateSentNumberOfTestWork() {
        Log.e("runningNumberOfTestWork", "called");
        OneTimeWorkRequest.Builder immediateSyncWorkBuilder = new OneTimeWorkRequest.Builder(DailyReportWork.class)
                .addTag("DailyReportWork");
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED).build();
        immediateSyncWorkBuilder.setConstraints(constraints);
        OneTimeWorkRequest oneTimeWorkRequest = immediateSyncWorkBuilder.build();
        WorkManager.getInstance(workContext).enqueue(oneTimeWorkRequest);
    }

    public void runDatabaseBackUpWork() {
        PeriodicWorkRequest.Builder runDatabaseBackUpWorkBuilder = new PeriodicWorkRequest.Builder(DatabaseBackupWorker.class, 12, TimeUnit.HOURS);
        Constraints constraints = new Constraints.Builder()
                .setRequiresDeviceIdle(true)
                .build();
        runDatabaseBackUpWorkBuilder.setConstraints(constraints);
        PeriodicWorkRequest runDatabaseBackUpWork = runDatabaseBackUpWorkBuilder.build();
        WorkManager.getInstance(workContext).enqueueUniquePeriodicWork(Constants.DB_BACKUP_WORK, ExistingPeriodicWorkPolicy.REPLACE, runDatabaseBackUpWork);
    }

    public void runDailyNumberOfReports() {
        PeriodicWorkRequest.Builder runDatabaseBackUpWorkBuilder = new PeriodicWorkRequest.Builder(DailyReportWork.class, 24, TimeUnit.HOURS);
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED).build();
        runDatabaseBackUpWorkBuilder.setConstraints(constraints);
        PeriodicWorkRequest runDatabaseBackUpWork = runDatabaseBackUpWorkBuilder.build();
        WorkManager.getInstance(workContext).enqueueUniquePeriodicWork(Constants.DAILY_NUMBER_OF_REPORT, ExistingPeriodicWorkPolicy.REPLACE, runDatabaseBackUpWork);
    }


    public void runImmediateJsonWork() {
        OneTimeWorkRequest.Builder immediateSyncWorkBuilder = new OneTimeWorkRequest.Builder(SaveAsJsonWorker.class)
                .addTag("JsonCreation");
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED).build();
        immediateSyncWorkBuilder.setConstraints(constraints);
        OneTimeWorkRequest oneTimeWorkRequest = immediateSyncWorkBuilder.build();
        WorkManager.getInstance(workContext).enqueue(oneTimeWorkRequest);
    }

    public void runImmediateUpdate() {
        OneTimeWorkRequest.Builder immediateSyncWorkBuilder = new OneTimeWorkRequest.Builder(AppUpdaterWorker.class)
                .addTag("ImmediateAPPUPDATE");
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED).build();
        immediateSyncWorkBuilder.setConstraints(constraints);
        OneTimeWorkRequest oneTimeWorkRequest = immediateSyncWorkBuilder.build();
        WorkManager.getInstance(workContext).enqueue(oneTimeWorkRequest);
    }

    public void runImmediateDBBackUp() {
        OneTimeWorkRequest.Builder immediateSyncWorkBuilder = new OneTimeWorkRequest.Builder(DatabaseBackupWorker.class)
                .addTag("ImmediateDatabaseBackUp");
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.NOT_REQUIRED).build();
        immediateSyncWorkBuilder.setConstraints(constraints);
        OneTimeWorkRequest oneTimeWorkRequest = immediateSyncWorkBuilder.build();
        WorkManager.getInstance(workContext).enqueue(oneTimeWorkRequest);
    }


}
