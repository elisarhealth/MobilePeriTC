package com.agyohora.mobileperitc.data.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

/**
 *  Created by Invent
 *  @see Entity
 */

@Entity(tableName = "PatientInfo")/*, indices = {@Index(value = {"PatientMrn"},
        unique = true)})*/
public class PatientInfo {

    @PrimaryKey(autoGenerate = true)
    private int Id;

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


    public int getId() {
        return Id;
    }

    public void setId(int id) {
        this.Id = id;
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

}
