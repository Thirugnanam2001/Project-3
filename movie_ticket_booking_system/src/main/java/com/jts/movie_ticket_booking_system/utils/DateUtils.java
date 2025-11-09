package com.jts.movie_ticket_booking_system.utils;

import java.text.SimpleDateFormat;
import java.sql.Date;

public class DateUtils {

    public static String formatDate(Date date) {
        if (date == null) return "Coming Soon";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
            return sdf.format(date);
        } catch (Exception e) {
            return date.toString();
        }
    }

    public static String formatDate(Date date, String format) {
        if (date == null) return "Coming Soon";
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(format);
            return sdf.format(date);
        } catch (Exception e) {
            return date.toString();
        }
    }
}