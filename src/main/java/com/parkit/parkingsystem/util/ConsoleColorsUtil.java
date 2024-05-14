package com.parkit.parkingsystem.util;


import static com.parkit.parkingsystem.util.NumbersUtil.doubleToStringWithZero;

public class ConsoleColorsUtil {
    public static final String RESET = "\033[0m";  // Text Reset

    public static final String BLUE = "\033[0;34m";
    public static final String PURPLE = "\033[0;35m";
    public static final String BLACK_BOLD = "\033[1;30m";
    public static final String BLUE_BACKGROUND = "\033[44m";
    public static final String PURPLE_BACKGROUND = "\033[45m";


    public static String colorString(String message, String ...color) {
        return String.join("", color) + message + RESET;
    }

    public static String colorIntNumber(int value, String ...color) {
        return String.join("", color) + String.valueOf(value) + RESET;
    }

    public static String colorDoubleNumber(double value, String ...color) {
        return String.join("", color) + doubleToStringWithZero(value) + RESET;
    }
}
