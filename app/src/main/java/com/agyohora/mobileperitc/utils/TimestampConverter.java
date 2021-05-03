package com.agyohora.mobileperitc.utils;

import android.util.Log;

import androidx.room.TypeConverter;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Invent on 29-1-18.
 * Date formatter for room database
 */

public class TimestampConverter {
    //static DateFormat df = new SimpleDateFormat(Constants.TIME_STAMP_FORMAT);
    private static DateFormat df = new SimpleDateFormat("MMM dd, yyyy hh:mm:ss aaa", Locale.US);

    @TypeConverter
    public static Date fromTimestamp(String value) {
        if (value != null) {
            try {
                return df.parse(value);
            } catch (ParseException e) {
                Log.e("Exception","TimestampConverter "+e.getMessage());
            }
            return null;
        } else {
            return null;
        }
    }

    @TypeConverter
    public static String dateToTimestamp(Date value) {

        return value == null ? null : df.format(value);
    }
}


