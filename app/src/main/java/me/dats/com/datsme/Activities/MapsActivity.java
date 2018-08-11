package me.dats.com.datsme.Activities;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.view.animation.Transformation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;


import com.github.pwittchen.reactivenetwork.library.rx2.ReactiveNetwork;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.theartofdev.edmodo.cropper.CropImage;



import butterknife.BindView;
import butterknife.ButterKnife;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import me.dats.com.datsme.Adapters.PagerViewAdapter;
import me.dats.com.datsme.Datsme;
import me.dats.com.datsme.R;
import me.dats.com.datsme.Utils.MyPreference;

public class MapsActivity extends AppCompatActivity implements View.OnClickListener {

    public final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    @BindView(R.id.messages)
    ImageView messages;

    @BindView(R.id.discover)
    ImageView discover;

    @BindView(R.id.myprofile)
    ImageView myprofile;
    @BindView(R.id.mapactivity_toolbar)
    Toolbar toolbar;

    @BindView(R.id.map_view_pager)
   public ViewPager viewPager;

    public PagerViewAdapter mPagerViewdapter;
    private boolean doubleBackToExitPressedOnce = false;
    public boolean connected = true;


    @BindView(R.id.rootlayout)
    public RelativeLayout relativeLayout;
    ActionBar actionBar;
    private Animation animShow;
    private Animation animHide;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);


        animShow = AnimationUtils.loadAnimation(this, R.anim.show_from_side);
        animHide = AnimationUtils.loadAnimation(this, R.anim.hide_from_side);
        //        InternetOb
        // servingSettings settings = InternetObservingSettings
//                .initialInterval(initialInterval)
//                .interval(interval)
//                .host(host)
//                .port(port)
//                .timeout(timeout)
//                .errorHandler(testErrorHandler)
//                .strategy(strategy)
//                .build();

        ReactiveNetwork.observeInternetConnectivity()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean isConnectedToInternet) {
                        // do something with isConnectedToInternet value
                        showSnack(isConnectedToInternet);
                    }
                });
        DatabaseReference database;
        database = FirebaseDatabase.getInstance().getReference();
        database.child("Users").child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    Datsme.getPreferenceManager().clearLoginData();
                    startActivity(new Intent(MapsActivity.this, LoginActivity.class));
                    finish();
                } else {
                    //shared preference tokens
                    Datsme.getPreferenceManager().putBoolean(MyPreference.ProfileId, true);
                    Datsme.getPreferenceManager().putBoolean(MyPreference.CompleteProfileId, true);
                    SetmyviewPager();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void SetmyviewPager() {
        String user_id = getIntent().getStringExtra("From");
        mPagerViewdapter = new PagerViewAdapter(getSupportFragmentManager(), user_id);
        Log.i("Notification", "PagerViewAdapter: " + user_id);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        } else {

            viewPager.setAdapter(mPagerViewdapter);
            viewPager.setOffscreenPageLimit(3);
            viewPager.setCurrentItem(1);
            toolbar.setVisibility(View.GONE);
        }

        messages.setOnClickListener(this);
        discover.setOnClickListener(this);
        myprofile.setOnClickListener(this);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, final float positionOffset, int positionOffsetPixels) {
                Log.d("TAG", "onPageScrolled: "+position+" position offset "+positionOffset);

            }

            @Override
            public void onPageSelected(int position) {
                View decorView = getWindow().getDecorView();
                if(position==0 && toolbar.getVisibility()!=View.VISIBLE)
                {
                    actionBar.show();
//                    toolbar.setVisibility(View.VISIBLE);
//                    toolbar.startAnimation(animShow);
                    showSystemUI();
//                    int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
//                    decorView.setSystemUiVisibility(uiOptions);
//                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                        getWindow().setStatusBarColor(Color.parseColor("#67D0C5"));
//                    }

                }
                else if(position==1 && toolbar.getVisibility()!=View.GONE)
                {
                    actionBar.hide();
//                    toolbar.startAnimation(animHide);
//                    toolbar.setVisibility(View.GONE);
                    hideSystemUI();
//                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//                    decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
//                    getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                        getWindow().setStatusBarColor(Color.parseColor("#00ffffff"));
//                    }


                }
                else if(position==2  && toolbar.getVisibility()!=View.VISIBLE)
                {
                    actionBar.show();
//                    toolbar.setVisibility(View.VISIBLE);
//                    toolbar.startAnimation(animShow);
                    showSystemUI();
//                    int uiOptions = View.SYSTEM_UI_FLAG_VISIBLE;
//                    decorView.setSystemUiVisibility(uiOptions);
//                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
//                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
//                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                        getWindow().setStatusBarColor(Color.parseColor("#67D0C5"));
//                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.messages:
                viewPager.setCurrentItem(0);
                break;
            case R.id.discover:
                setAnimations();
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(this,
                            new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                            PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
                } else {
                    viewPager.setCurrentItem(1);
                }
                break;
            case R.id.myprofile:
                viewPager.setCurrentItem(2);
                break;

        }
    }

    public void getDiscoverFragment() {
        setAnimations();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        } else {
            viewPager.setCurrentItem(1);
        }
    }

    private void setAnimations() {
        AnimationSet animationSet;
        animationSet = new AnimationSet(true);

        ScaleAnimation a = new ScaleAnimation(
                1f, 1.2f, // Start and end values for the X axis scaling
                1f, 1.2f, // Start and end values for the Y axis scaling
                Animation.RELATIVE_TO_SELF, 0.5f, // Pivot point of X scaling
                Animation.RELATIVE_TO_SELF, 0.5f); // Pivot point of Y scaling
        a.setDuration(500);
        a.setRepeatCount(1);
        a.setRepeatMode(ScaleAnimation.REVERSE);

        RotateAnimation r = new RotateAnimation(0f, 720f, RotateAnimation.RELATIVE_TO_SELF, 0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);
        r.setRepeatCount(1);
        r.setRepeatMode(RotateAnimation.REVERSE);
        r.setDuration(500);

        animationSet.addAnimation(a);
        animationSet.addAnimation(r);

        //  animationSet.addAnimation(mAnimation);
        discover.startAnimation(animationSet);

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION:

                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {
                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            viewPager.setAdapter(mPagerViewdapter);
                            viewPager.setCurrentItem(0);
                            toolbar.setVisibility(View.VISIBLE);
                        } else {
                            viewPager.setAdapter(mPagerViewdapter);
                            viewPager.setCurrentItem(1);
                            toolbar.setVisibility(View.GONE);
                        }
                    }
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (viewPager.getCurrentItem() == 1) {

            if (doubleBackToExitPressedOnce) {
                finishAffinity();
                return;
            }
            this.doubleBackToExitPressedOnce = true;
            Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    doubleBackToExitPressedOnce = false;
                }
            }, 2000);
        } else {
            viewPager.setCurrentItem(1);
        }


    }

    public void getProfileFragment() {
        viewPager.setCurrentItem(2);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            //find the current fragment by tag or id
//            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.);
//            fragment.onActivityResult(requestCode, resultCode, data);
        }
    }

    private void showSnack(boolean isConnected) {
        String message;
        if (isConnected) {
            if (!connected) {
                message = "Good! You're now connected.";
                Snackbar snackBar = Snackbar.make(relativeLayout
                        , message, Snackbar.LENGTH_SHORT);
                View sbView = snackBar.getView();
                sbView.setBackgroundColor(Color.parseColor("#d63f3a"));
                snackBar.show();
            }
        } else {
            connected = false;
            message = "Sorry! No internet connection.";
            Snackbar snackBar = Snackbar.make(relativeLayout
                    , message, Snackbar.LENGTH_INDEFINITE);
            View sbView = snackBar.getView();
            sbView.setBackgroundColor(Color.parseColor("#d63f3a"));
            snackBar.show();

        }

    }
    private void hideSystemUI() {
        // Enables regular immersive mode.
        // For "lean back" mode, remove SYSTEM_UI_FLAG_IMMERSIVE.
        // Or for "sticky immersive," replace it with SYSTEM_UI_FLAG_IMMERSIVE_STICKY
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
//                         Set the content to appear under the system bars so that the
//                         content doesn't resize when the system bars hide and show.
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
//                         Hide the nav bar and status bar
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        |View.SYSTEM_UI_FLAG_LOW_PROFILE
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }
    private void showSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
    }
}