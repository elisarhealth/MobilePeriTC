package com.agyohora.mobileperitc.data.database.entity;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.agyohora.mobileperitc.utils.TimestampConverter;

import java.util.Date;

@Entity(tableName = "ClickerHistory")
public class ClickerHistory {

    public int getId() {
        return Id;
    }

    public void setId(int id) {
        Id = id;
    }

    public String getNature() {
        return nature;
    }

    public void setNature(String nature) {
        this.nature = nature;
    }

    public String getService_no() {
        return service_no;
    }

    public void setService_no(String service_no) {
        this.service_no = service_no;
    }

    public String getSerial_number() {
        return serial_number;
    }

    public void setSerial_number(String serial_number) {
        this.serial_number = serial_number;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    @PrimaryKey(autoGenerate = true)
    private int Id;

    @ColumnInfo(name = "NatureOfChange")
    private String nature;

    @ColumnInfo(name = "ServiceReportNumber")
    private String service_no;

    @ColumnInfo(name = "PrbSerialNumber")
    private String serial_number;

    @ColumnInfo(name = "PrbCount")
    private int count;

    @ColumnInfo(name = "created_date")
    @TypeConverters({TimestampConverter.class})
    private Date createDate;
}

