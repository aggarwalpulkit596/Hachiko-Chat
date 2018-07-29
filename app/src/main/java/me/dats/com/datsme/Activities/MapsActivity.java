package me.dats.com.datsme.Activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.RotateAnimation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.theartofdev.edmodo.cropper.CropImage;

import butterknife.BindView;
import butterknife.ButterKnife;
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

    @BindView(R.id.map_view_pager)
    ViewPager viewPager;

    PagerViewAdapter mPagerViewdapter;
    private boolean doubleBackToExitPressedOnce = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
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
            viewPager.setCurrentItem(1);
        }

        messages.setOnClickListener(this);
        discover.setOnClickListener(this);
        myprofile.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.messages:
                //setsize(1);
                viewPager.setCurrentItem(0);
                break;
            case R.id.discover:
                //setsize(2);
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
                // setsize(3);
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
                        } else {
                            viewPager.setAdapter(mPagerViewdapter);
                            viewPager.setCurrentItem(1);
                        }
                    }
                }
                break;
        }
    }

    @Override
    public void onBackPressed() {
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
}