package com.agyohora.mobileperitc.model;

import java.util.Date;

public class ClickerHistoryModel {

    private String Id;
    private String nature;
    private String service_no;
    private String serial_number;
    private String count;
    private String createDate;

    public String getId() {
        return Id;
    }

    public String getNature() {
        return nature;
    }

    public String getService_no() {
        return service_no;
    }

    public String getSerial_number() {
        return serial_number;
    }

    public String getCount() {
        return count;
    }

    public String getCreateDate() {
        return createDate;
    }


    public ClickerHistoryModel(String id, String nature, String service_no, String serial_number, String count, String createDate) {
        this.Id = id;
        this.nature = nature;
        this.service_no = service_no;
        this.serial_number = serial_number;
        this.count = count;
        this.createDate = createDate;
    }
}
