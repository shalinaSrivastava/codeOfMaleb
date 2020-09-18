package com.trainor.controlandmeasurement.HelperClass;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class SharedPreferenceClass {

    private Context context;

    public SharedPreferenceClass(Context context) {
        this.context = context;
    }

    public void removePref(String prefrenceName) {
        SharedPreferences.Editor edit = context.getSharedPreferences(prefrenceName, MODE_PRIVATE).edit();
        edit.clear();
        edit.commit();
    }

    public String getLoginInfoValueByKeyName(String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("LoginInfoPref", 0);
        return sharedPreferences.getString(key, "");
    }

    public void saveLoginInfoValueByKeyName(String key, String value, SharedPreferences.Editor editor) {
        editor.putString(key, value);
        editor.commit();
    }

    public String getGeneralInfoValueByKeyName(String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("GeneralInfoPref", 0);
        return sharedPreferences.getString(key, "");
    }

    public void saveGeneralInfoValueByKeyName(String key, String value, SharedPreferences.Editor editor) {
        editor.putString(key, value);
        editor.commit();
    }

    public String getVariablerValueByKeyName(String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("VariablerPref", 0);
        return sharedPreferences.getString(key, "0");
    }

    public void saveVariablerValueByKeyName(String key, String value, SharedPreferences.Editor editor) {
        editor.putString(key, value);
        editor.commit();
    }

    public String getCalculationValueByKeyName(String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("CalculationPref", 0);
        return sharedPreferences.getString(key, "");
    }

    public void saveCalculationValueByKeyName(String key, String value, SharedPreferences.Editor editor) {
        editor.putString(key, value);
        editor.commit();
    }

    public String getGraphValueByKeyName(String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("MeasuredValuesPref", 0);
        return sharedPreferences.getString(key, "");
    }

    public String getCompanyValueByKeyName(String key) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("CompanyPref", 0);
        return sharedPreferences.getString(key, "");
    }

    public void saveGraphValueByKeyName(String key, String value, SharedPreferences.Editor editor) {
        editor.putString(key, value);
        editor.commit();
    }
}
