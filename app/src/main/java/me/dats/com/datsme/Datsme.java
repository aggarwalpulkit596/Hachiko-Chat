package me.dats.com.datsme;

import android.support.multidex.MultiDexApplication;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import me.dats.com.datsme.utils.MyPreference;

public class Datsme extends MultiDexApplication {
    public static Datsme myApp;
    public static MyPreference myPreferenceManager;

    public static MyPreference getPreferenceManager() {
        if (myPreferenceManager == null) {
            myPreferenceManager = new MyPreference(getInstance());
        }

        return myPreferenceManager;
    }

    public static synchronized Datsme getInstance() {
        return myApp;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        myApp = this;
        // Initialize the SDK before executing any other operations,
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
    }
}
