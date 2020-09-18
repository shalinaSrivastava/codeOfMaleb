package com.trainor.controlandmeasurement.Activities;

import android.app.Application;

public class ApplicationClass extends Application {
    public static ApplicationClass instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    @Override
    public void onTerminate() {
        super.onTerminate();
    }

    public static ApplicationClass getInstance() {
        if (instance == null) {
            instance = new ApplicationClass();
        }
        return instance;
    }
}
