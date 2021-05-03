package com.agyohora.mobileperitc.data.database;

import android.content.Context;
import android.util.Log;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.agyohora.mobileperitc.data.database.dao.PatientInfoDao;
import com.agyohora.mobileperitc.data.database.dao.PatientTestResultDao;
import com.agyohora.mobileperitc.data.database.entity.PatientInfo;
import com.agyohora.mobileperitc.data.database.entity.PatientTestResult;
import com.agyohora.mobileperitc.myapplication.MyApplication;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

@Database(entities = {PatientTestResult.class, PatientInfo.class}, version = 3, exportSchema = true)
public abstract class OldDatabase extends RoomDatabase {

    private static OldDatabase INSTANCE;

    public static OldDatabase getAppDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE = getOldDatabase(context);
        }
        return INSTANCE;
    }

    private static OldDatabase getOldDatabase(Context context) {
        Log.e("TrackMe", "getOldDatabase Called");
        context = context != null ? context : MyApplication.getInstance();
        File dbFile = context.getDatabasePath("old-patient-database");
        File backedUpFile = new File(context.getFilesDir().getAbsolutePath(), "old-patient-database.db");
        if (dbFile.exists()) {
            return Room.databaseBuilder(context.getApplicationContext(),
                    OldDatabase.class, "old-patient-database")
                    .build();

        } else if (backedUpFile.exists()) {
            return copyDataBase(context);
        }
        return Room.databaseBuilder(context.getApplicationContext(),
                OldDatabase.class, "old-patient-database")
                .build();
    }

    private static OldDatabase copyDataBase(Context context) {
        {
            boolean bCopyOk = false;
            File inputFile = new File(context.getFilesDir().getAbsolutePath(), "old-patient-database.db");
            File outputFile = context.getDatabasePath("old-patient-database");
            try {
                Log.e("copyDataBase", "dbFile exists");
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
                Log.e("copyDataBase", "Exception " + e.getMessage());
                outputFile.delete();
            } finally {
                if (bCopyOk)
                    Log.e("copyDataBase", "success");
            }
            return Room.databaseBuilder(context.getApplicationContext(),
                    OldDatabase.class, "old-patient-database")
                    .build();
        }
    }

    public abstract PatientTestResultDao testResultDao();

    public abstract PatientInfoDao patientInfoDao();

}
