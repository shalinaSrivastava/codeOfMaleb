package com.trainor.controlandmeasurement.HelperClass;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectionDetector {
    public static volatile ConnectionDetector instance;
    private Context _context;

    public ConnectionDetector(Context Context) {
        this._context = Context;
        instance = this;
    }

    public static synchronized ConnectionDetector getInstance(Context context) {
        if (instance == null) {
            instance = new ConnectionDetector(context);
        }
        return instance;
    }

    public boolean isConnectingToInternet() {
        ConnectivityManager connectivity = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getActiveNetworkInfo();
            if (info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_WIFI) {
                return true;
            } else {
                return info != null && info.isConnected() && info.getType() == ConnectivityManager.TYPE_MOBILE;
            }
        }
        return false;
    }
}
