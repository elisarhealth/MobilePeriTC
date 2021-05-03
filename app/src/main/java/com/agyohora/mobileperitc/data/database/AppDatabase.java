package com.agyohora.mobileperitc.data.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabaseCorruptException;
import android.util.Log;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.migration.Migration;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.agyohora.mobileperitc.data.database.dao.PatientInfoDao;
import com.agyohora.mobileperitc.data.database.dao.PatientTestResultDao;
import com.agyohora.mobileperitc.data.database.entity.PatientInfo;
import com.agyohora.mobileperitc.data.database.entity.PatientTestResult;
import com.agyohora.mobileperitc.myapplication.MyApplication;
import com.agyohora.mobileperitc.utils.Constants;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

/**
 * Created by Invent on 20-12-17.
 *
 * @see RoomDatabase
 * Here we look for is any database backup present,
 * if not we create new db instance or we restore the exisiting one
 */

@Database(entities = {PatientTestResult.class, PatientInfo.class}, version = 3, exportSchema = true)
public abstract class AppDatabase extends RoomDatabase {

    private static AppDatabase INSTANCE;

    public static AppDatabase getAppDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = restoreDataBase(context);
        }
        return INSTANCE;
    }

    public static void destroyInstance() {
        INSTANCE = null;
    }

    private static AppDatabase restoreDataBase(Context context) {
        Log.e("TrackMe", "restoreDataBase Called");
        context = context != null ? context : MyApplication.getInstance();
        File dbFile = context.getDatabasePath("patient-database");
        File backedUpFile = new File(Constants.DB_BACK_UP_FOLDER, Constants.DB_NAME + ".db");
        if (dbFile.exists()) {
            Log.e("TrackMe", "dbFile exists");

            return Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase.class, "patient-database")
                    .addMigrations(MIGRATION_2_3)
                    .build();

        } else if (backedUpFile.exists()) {
            return copyDataBase(context);
        }
        return Room.databaseBuilder(context.getApplicationContext(),
                AppDatabase.class, "patient-database")
                .addMigrations(MIGRATION_2_3)
                .build();
    }

    private static AppDatabase copyDataBase(Context context) {
        {
            boolean bCopyOk = false;
            File inputFile = new File(Constants.DB_BACK_UP_FOLDER, Constants.DB_NAME + ".db");
            File outputFile = context.getDatabasePath("patient-database");
            try {
                Log.e("restoreDataBase", "dbFile exists");
                FileInputStream fis = new FileInputStream(inputFile);
                OutputStream os = new FileOutputStream(outputFile);
                byte[] buffer = new byte[1024];
                int length;
                while ((length = fis.read(buffer)) > 0) {
                    os.write(buffer, 0, length);
                }
                os.flush();
                os.close();
                fis.close();
                bCopyOk = true;
            } catch (Exception e) {
                Log.e("restoreDataBase", "Exception " + e.getMessage());
                outputFile.delete();
            } finally {
                if (bCopyOk)
                    Log.e("restoreDataBase", "success");
            }
            return Room.databaseBuilder(context.getApplicationContext(),
                    AppDatabase.class, "patient-database")
                    .addMigrations(MIGRATION_2_3)
                    .build();
        }
    }

    static final Migration MIGRATION_2_3 = new Migration(2, 3) {
        @Override
        public void migrate(SupportSQLiteDatabase database) {
            Log.e("TrackMe", "MIGRATION_3_4 called");
            database.execSQL("ALTER TABLE PatientTestResult ADD COLUMN PerimeteryObjectVersion INTEGER NOT NULL DEFAULT 1");
            /*if (EssentialDataActivity.essentialDataActivityReference == null) {
                Log.e("TrackMe", " migrate is triggered and context  is null");
                new SilentlyUpdateTheVFI().execute();
            } else {
                Log.e("TrackMe", " migrate is triggered and context is not null");
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(() -> new FillTheVFI(patientTestResults -> {
                }, EssentialDataActivity.essentialDataActivityReference).execute());

            }*/

        }
    };


    private static boolean openDataBase(final Context context) {
        Log.e("openDataBase", "called");
        final File dbFile = context.getDatabasePath("patient-database");
        SQLiteDatabase sqLiteDatabase = null;
        try {
            sqLiteDatabase = SQLiteDatabase.openDatabase(dbFile.getPath(), null, SQLiteDatabase.NO_LOCALIZED_COLLATORS);
        } catch (SQLiteDatabaseCorruptException e) {
            sqLiteDatabase.close();
            context.deleteDatabase("patient-database");
            dbFile.delete();
            Log.e("openDataBase", "openDataBase " + e.getMessage());
            return false;
        }
        return true;
    }

    public abstract PatientTestResultDao testResultDao();

    public abstract PatientInfoDao patientInfoDao();
}
