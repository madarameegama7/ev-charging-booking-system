package com.example.evchargingapp.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class DateTimeUtils {

    private static final String UTC_FORMAT = "yyyy-MM-dd'T'HH:mm:ss'Z'";
    private static final String LOCAL_FORMAT = "yyyy-MM-dd HH:mm";

    public static String utcToLocal(String utcTime) {
        SimpleDateFormat utcSdf = new SimpleDateFormat(UTC_FORMAT);
        utcSdf.setTimeZone(TimeZone.getTimeZone("UTC"));
        SimpleDateFormat localSdf = new SimpleDateFormat(LOCAL_FORMAT);
        localSdf.setTimeZone(TimeZone.getDefault());

        try {
            Date date = utcSdf.parse(utcTime);
            return localSdf.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return utcTime;
        }
    }

    public static String localToUtc(String localTime) {
        SimpleDateFormat localSdf = new SimpleDateFormat(LOCAL_FORMAT);
        localSdf.setTimeZone(TimeZone.getDefault());
        SimpleDateFormat utcSdf = new SimpleDateFormat(UTC_FORMAT);
        utcSdf.setTimeZone(TimeZone.getTimeZone("UTC"));

        try {
            Date date = localSdf.parse(localTime);
            return utcSdf.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return localTime;
        }
    }
}
