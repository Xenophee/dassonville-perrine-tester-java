package com.parkit.parkingsystem.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DatesUtil {

    public static String FULL_ENGLISH_FORMAT_DATE = "MMMM dd, yyyy 'at' HH:mm:ss";

    public static String formatDate(LocalDateTime date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(FULL_ENGLISH_FORMAT_DATE);
        return date.format(formatter);
    }

}
