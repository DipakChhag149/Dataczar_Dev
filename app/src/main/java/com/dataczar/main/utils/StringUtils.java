package com.dataczar.main.utils;

public class StringUtils {

    public static final String dataValue = "date";
    public static final String timeValue = "time";

    public static boolean isNotEmpty(CharSequence str) {
        if (str == null || str.length() == 0 || str.equals("null") || str.equals(" ") || str.equals("")) {
            return false;
        } else {
            return true;
        }
    }

    public static boolean isNotEmpty(CharSequence... str) {
        for (int i = 0; i < str.length; i++) {
            if (str[i] != null) {
                str[i] = str[i].toString().trim();
            }

            if (str[i] == null || str[i].length() == 0 || str[i].equals("null") || str[i].equals("")) {
                return false;
            }
        }

        return true;
    }



}