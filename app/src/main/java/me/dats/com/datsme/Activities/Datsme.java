package me.dats.com.datsme.Activities;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDexApplication;
import me.dats.com.datsme.MyPreference;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

public class Datsme extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        myApp = this;
        // Initialize the SDK before executing any other operations,
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
    }
    public static MyPreference getPreferenceManager() {
        if (myPreferenceManager == null) {
            myPreferenceManager = new MyPreference(getInstance());
        }

        return myPreferenceManager;
    }
    public static Datsme myApp;
    public static MyPreference myPreferenceManager;
    public static synchronized Datsme getInstance()
    {
        return myApp;
    }
}
