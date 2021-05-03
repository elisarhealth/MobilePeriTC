package com.agyohora.mobileperitc.data.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.agyohora.mobileperitc.utils.TimestampConverter;

import java.util.Date;

/**
 * Created by Invent on 28-12-17.
 *
 * @see Entity
 */

@Entity(tableName = "PatientTestResult")
public class PatientTestResult {
    @PrimaryKey(autoGenerate = true)
    private int id;

    @ColumnInfo(name = "PatientInfoId")
    private int patientInfoId;

    @ColumnInfo(name = "PatientName")
    private String patientName;

    @ColumnInfo(name = "PatientMrn")
    private String patientMrn;

    @ColumnInfo(name = "PatientMobile")
    private String patientMobile;

    @ColumnInfo(name = "PatientDob")
    private String patientDOB;

    @ColumnInfo(name = "PatientSex")
    private String patientSex;

    @ColumnInfo(name = "TestEye")
    private String testEye;

    public String getTestType() {
        return testType;
    }

    public void setTestType(String testType) {
        this.testType = testType;
    }

    public String getTestPattern() {
        return testPattern;
    }

    public void setTestPattern(String testPattern) {
        this.testPattern = testPattern;
    }

    @ColumnInfo(name = "TestType")
    private String testType;

    @ColumnInfo(name = "TestPattern")
    private String testPattern;

    @ColumnInfo(name = "TestSuggestion")
    private String testSuggestion;

    @ColumnInfo(name = "Active")
    private int active;

    @ColumnInfo(name = "SyncStatus")
    private int status;

    @ColumnInfo(name = "result", typeAffinity = ColumnInfo.BLOB)
    private byte[] result;

    @ColumnInfo(name = "created_date")
    @TypeConverters({TimestampConverter.class})
    private Date createDate;

    @ColumnInfo(name = "PerimeteryObjectVersion")
    private int PerimeteryObjectVersion;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getPatientInfoId() {
        return patientInfoId;
    }

    public void setPatientInfoId(int id) {
        this.patientInfoId = id;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
    }

    public String getPatientMrn() {
        return patientMrn;
    }

    public void setPatientMrn(String patientMrn) {
        this.patientMrn = patientMrn;
    }

    public String getPatientMobile() {
        return patientMobile;
    }

    public void setPatientMobile(String patientMobile) {
        this.patientMobile = patientMobile;
    }

    public String getPatientDOB() {
        return patientDOB;
    }

    public void setPatientDOB(String patientDOB) {
        this.patientDOB = patientDOB;
    }

    public String getPatientSex() {
        return patientSex;
    }

    public void setPatientSex(String patientSex) {
        this.patientSex = patientSex;
    }

    public String getTestEye() {
        return testEye;
    }

    public void setTestEye(String testEye) {
        this.testEye = testEye;
    }

    public String getTestSuggestion() {
        return testSuggestion;
    }

    public void setTestSuggestion(String testSuggestion) {
        this.testSuggestion = testSuggestion;
    }

    public int getActive() {
        return active;
    }

    public void setActive(int active) {
        this.active = active;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public byte[] getResult() {
        return result;
    }

    public void setResult(byte[] result) {
        this.result = result;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public int getPerimeteryObjectVersion() {
        return PerimeteryObjectVersion;
    }

    public void setPerimeteryObjectVersion(int PerimeteryObjectVersion) {
        this.PerimeteryObjectVersion = PerimeteryObjectVersion;
    }

}
