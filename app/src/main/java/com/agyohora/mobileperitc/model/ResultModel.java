package com.agyohora.mobileperitc.model;

import java.util.Date;

/**
 * Created by Invent on 28-1-18.
 * Getter setter for ReportDataAdapter
 */

public class ResultModel {

    private String id;
    private String version;
    private String patSex;
    private String patName;
    private String patMRN;
    private String patMobile;
    private String patMobileUnformatted;
    private String testEye;
    private String testSuggestion;
    private Date testDate;


    public ResultModel(String id, String version, String sex, String patName, String mrn, String patMobile, String patMobileUnformatted, String testEye, String testSuggestion, Date testDate) {
        this.id = id;
        this.version = version;
        this.patSex = sex;
        this.patMRN = mrn;
        this.patName = patName;
        this.patMobile = patMobile;
        this.patMobileUnformatted = patMobileUnformatted;
        this.testDate = testDate;
        this.testEye = testEye;
        this.testSuggestion = testSuggestion;
    }

    public String getId() {
        return id;
    }

    public String getVersion() {
        return version;
    }

    public String getPatName() {
        return patName;
    }

    public String getPatSex() {
        return patSex;
    }

    public String getPatMRN() {
        return patMRN;
    }

    public String getPatMobile() {
        return patMobile;
    }

    public String getPatMobileUnformatted() {
        return patMobileUnformatted;
    }

    public String getTestEye() {
        return testEye;
    }

    public String getTestSuggestion() {
        return testSuggestion;
    }

    public Date getTestDate() {
        return testDate;
    }
}
