package ru.slatinin.serverinfotcp;

import android.annotation.SuppressLint;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtil{

    public static String formatMillisToHours(long millis) {
        @SuppressLint("SimpleDateFormat")
        DateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date(millis);
        return formatter.format(date);
    }

    public static String formatMillisToMinutes(long millis) {
        @SuppressLint("SimpleDateFormat")
        DateFormat formatter = new SimpleDateFormat("mm:ss");
        Date date = new Date(millis);
        return formatter.format(date);
    }

    @SuppressLint("SimpleDateFormat")
    public static String formatTimeToMinutes(String toBeFormatted) {
        if (toBeFormatted.isEmpty()){
            return formatMillisToMinutes(System.currentTimeMillis());
        }
        String newString = "";
        Date date;
        try {
            if (toBeFormatted.contains("T")){
                toBeFormatted = toBeFormatted.replace("T", " ");
            }
            date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(toBeFormatted);
            if (date != null) {
                newString = new SimpleDateFormat("mm:ss").format(date);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return newString;
    }
}
