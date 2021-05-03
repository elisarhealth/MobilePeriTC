package com.agyohora.mobileperitc.data.database;

import androidx.room.ColumnInfo;

/**
 * Created by Invent on 3-3-18.
 * Getter setter for the patient details search in Report list
 */

public class PatientRecordFound {
    public String name;

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


    public String getPatientMrn() {
        return patientMrn;
    }

    public void setPatientMrn(String patientMrn) {
        this.patientMrn = patientMrn;
    }

    public String getPatientName() {
        return patientName;
    }

    public void setPatientName(String patientName) {
        this.patientName = patientName;
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
