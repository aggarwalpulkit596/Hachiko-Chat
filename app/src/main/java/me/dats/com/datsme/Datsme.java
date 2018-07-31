package me.dats.com.datsme;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDexApplication;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import me.dats.com.datsme.Activities.LoginActivity;
import me.dats.com.datsme.Utils.MyPreference;

public class Datsme extends MultiDexApplication {
    public static Datsme myApp;
    public static MyPreference myPreferenceManager;

    public static MyPreference getPreferenceManager() {
        if (myPreferenceManager == null) {
            myPreferenceManager = new MyPreference(getInstance());
        }

        return myPreferenceManager;
    }

    private static Context mContext;

    public static Context getAppContext() {
        return mContext;
    }

    public static synchronized Datsme getInstance() {
        return myApp;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        myApp = this;
        mContext = this.getApplicationContext();
        // Initialize the SDK before executing any other operations,
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);
    }

//    public static void checkAuth() {
//        DatabaseReference database;
//        database = FirebaseDatabase.getInstance().getReference();
//        database.child("Users").child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//                if (!dataSnapshot.exists()) {
//                    Datsme.getPreferenceManager().clearLoginData();
//                    mContext.startActivity(new Intent(mContext, LoginActivity.class));
//
//                } else {
//                    //shared preference tokens
//                    Datsme.getPreferenceManager().putBoolean(MyPreference.ProfileId, true);
//                    Datsme.getPreferenceManager().putBoolean(MyPreference.CompleteProfileId, true);
//
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
//    }
}
