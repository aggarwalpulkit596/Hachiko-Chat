package me.dats.com.datsme;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.multidex.MultiDexApplication;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;
import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import me.dats.com.datsme.Activities.LoginActivity;
import me.dats.com.datsme.Utils.MyPreference;

public class Datsme extends MultiDexApplication {
    public static Datsme myApp;
   // public static boolean connected=true;
    public static MyPreference myPreferenceManager;

    //
    // public static RelativeLayout v;


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

//    public static void setView(RelativeLayout view)
//    {
//        v= view;
//    }
    @Override
    public void onCreate() {
        super.onCreate();
        myApp = this;
        mContext = this.getApplicationContext();
        // Initialize the SDK before executing any other operations,
        FacebookSdk.sdkInitialize(getApplicationContext());
        AppEventsLogger.activateApp(this);

//        ReactiveNetwork.observeInternetConnectivity()
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe(new Consumer<Boolean>() {
//                    @Override
//                    public void accept(Boolean isConnectedToInternet) {
//                        // do something with isConnectedToInternet value
//                        showSnack(isConnectedToInternet);
//                    }
//                });
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
//private static void showSnack(boolean isConnected) {
//    String message;
//
//    if(v!=null)
//    {
//        if (isConnected) {
//            if (!connected) {
//                message = "Good! You're now connected.";
//                Snackbar snackBar = Snackbar.make(v
//                        , message, Snackbar.LENGTH_SHORT);
//                View sbView = snackBar.getView();
//                sbView.setBackgroundColor(Color.parseColor("#d63f3a"));
//                snackBar.show();
//            }
//        } else {
//            connected = false;
//            message = "Sorry! No internet connection.";
//            Snackbar snackBar = Snackbar.make(v
//                    , message, Snackbar.LENGTH_INDEFINITE);
//            View sbView = snackBar.getView();
//            sbView.setBackgroundColor(Color.parseColor("#d63f3a"));
//            snackBar.show();
//
//        }
//
//    }
//
//
//}

}
