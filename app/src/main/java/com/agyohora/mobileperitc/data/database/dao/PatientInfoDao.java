package com.agyohora.mobileperitc.data.database.dao;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.RoomWarnings;

import com.agyohora.mobileperitc.data.database.PatientRecordFound;
import com.agyohora.mobileperitc.data.database.entity.PatientInfo;
import com.agyohora.mobileperitc.data.database.entity.PatientTestResult;

import java.util.List;

/**
 * Created by Invent
 *
 * @see Dao
 */

@Dao
public interface PatientInfoDao {

    @SuppressWarnings(RoomWarnings.CURSOR_MISMATCH)
    @Query("SELECT * FROM PatientInfo where PatientMrn =  :mrn")
    PatientRecordFound findByMrn(String mrn);

    @Insert
    long[] insertAll(PatientInfo... infos);

    @Query("UPDATE PatientInfo SET PatientName = :name, PatientMobile =:mobile, PatientDob =:dob, PatientSex = :sex WHERE PatientMrn = :mrn")
    int updateRecord(String mrn, String name, String mobile, String dob, String sex);

    @Delete
    void delete(PatientInfo info);

    @Query("SELECT COUNT(*) from PatientInfo where PatientMrn =  :mrn")
    int countRows(String mrn);

    @Query("SELECT * FROM PatientInfo")
    List<PatientInfo> getAllPatientInfo();

    @Query("SELECT PatientName FROM PatientInfo where PatientMrn =  :mrn")
    String getPatientName(String mrn);

    @Query("SELECT * FROM PatientInfo where PatientMrn= :mrn")
    List<PatientInfo> getAllPatientInfoByMrn(String mrn);
}
