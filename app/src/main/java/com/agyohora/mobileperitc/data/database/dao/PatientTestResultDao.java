package com.agyohora.mobileperitc.data.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.RoomWarnings;

import com.agyohora.mobileperitc.data.database.PatientRecordFound;
import com.agyohora.mobileperitc.data.database.entity.PatientTestResult;

import java.util.List;

/**
 * Created by Invent on 28-12-17.
 *
 * @see Dao
 */

@Dao
public interface PatientTestResultDao {

    @Query("SELECT * FROM PatientTestResult")
    List<PatientTestResult> getAll();

    @Query("SELECT * FROM PatientTestResult WHERE SyncStatus = 0")
    List<PatientTestResult> getUnSynced();

    @Query("SELECT count(*) FROM PatientTestResult WHERE PerimeteryObjectVersion = 1")
    int getNotMigratedCount();

    @Query("SELECT * FROM PatientTestResult WHERE PerimeteryObjectVersion = 1")
    List<PatientTestResult> getNotMigrated();

    @Query("SELECT * FROM PatientTestResult order by id DESC LIMIT 4 ")
    List<PatientTestResult> getLastSixty();

    @Query("SELECT * FROM PatientTestResult where Id =  :id")
    PatientTestResult findById(String id);

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT Id, PatientInfoId, PatientName, PatientMrn, PatientMobile, PatientSex, TestEye, TestPattern, TestType, TestSuggestion, created_date, active, SyncStatus, PerimeteryObjectVersion FROM PatientTestResult WHERE Active = 1")
    List<PatientTestResult> listAbstractDetails();

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT id, PatientInfoId, PatientMrn, active, SyncStatus, PerimeteryObjectVersion FROM PatientTestResult")
    List<PatientTestResult> listMrnDetails();

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT PatientName, PatientMobile, PatientDob, PatientSex FROM PatientTestResult WHERE PatientMrn = :mrn LIMIT 1")
    PatientRecordFound findUserDetails(String mrn);

    @Query("SELECT COUNT(*) from PatientTestResult")
    int countRows();

    @Query("UPDATE PatientTestResult SET Active = 0 WHERE Id = :id")
    int deleteRecord(String id);

    @Query("UPDATE PatientTestResult SET SyncStatus = 1 WHERE Id = :id")
    int syncedRecord(String id);

    @Insert
    long[] insertAll(PatientTestResult... results);

    @Delete
    void delete(PatientTestResult result);

    @Query("UPDATE PatientTestResult SET result = :resultData, PerimeteryObjectVersion = :version  WHERE Id = :id")
    int updateTestData(String id, byte[] resultData, int version);
}
