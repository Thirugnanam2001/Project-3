
package com.jts.movie_ticket_booking_system.utils;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class TimeFormatter {

    public static String formatTime(Time time) {
        if (time == null) return "N/A";
        try {
            // Convert java.sql.Time to LocalTime for better formatting
            LocalTime localTime = time.toLocalTime();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("hh:mm a");
            return localTime.format(formatter);
        } catch (Exception e) {
            // Fallback to SimpleDateFormat
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
                return sdf.format(time);
            } catch (Exception ex) {
                return time.toString();
            }
        }
    }

    public static String formatTime(Time time, String pattern) {
        if (time == null) return "N/A";
        try {
            LocalTime localTime = time.toLocalTime();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            return localTime.format(formatter);
        } catch (Exception e) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat(pattern);
                return sdf.format(time);
            } catch (Exception ex) {
                return time.toString();
            }
        }
    }

    // Additional utility method for Thymeleaf compatibility
    public static String formatTimeForDisplay(Time time) {
        return formatTime(time, "hh:mm a");
    }
}
