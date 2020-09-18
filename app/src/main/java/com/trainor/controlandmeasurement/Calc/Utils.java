package com.trainor.controlandmeasurement.Calc;

public class Utils {

    public static double roundTo(double d, double dec) {
        double rounded = dec * (double)Math.round(d / dec);
        String decString = String.valueOf(dec);
        int decimals = decString.substring(decString.indexOf(".") + 1).length();
        String temp = String.valueOf(rounded);
        String before = temp.substring(0, temp.indexOf("."));
        temp = temp.substring(temp.indexOf(".") + 1);
        if (temp.length() > decimals) {
            temp = before + "." + temp.substring(0, decimals);
            return Double.parseDouble(temp);
        } else {
            return rounded;
        }
    }
}
