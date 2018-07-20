package me.dats.com.datsme.Activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.dats.com.datsme.Datsme;
import me.dats.com.datsme.R;
import me.dats.com.datsme.Utils.MyPreference;

public class SplashScreen extends AppCompatActivity {

    @BindView(R.id.splashlogo)
    ImageView splashLogo;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        ButterKnife.bind(this);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {

            @Override
            public void run() {
                // Check if user is signed in (non-null) and update UI accordingly.
                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                Log.i("TAG", "onStart");
                if (currentUser != null) {

                    if (Datsme.getPreferenceManager().getBoolean(MyPreference.ProfileId)) {

                        if (Datsme.getPreferenceManager().getBoolean(MyPreference.CompleteProfileId)) {
                            startActivity(new Intent(SplashScreen.this, MapsActivity.class));
                            finish();
                        } else {
                            startActivity(new Intent(SplashScreen.this, CompleteProfileActivity.class));
                            finish();
                        }
                    } else {
                        Intent intent = new Intent(SplashScreen.this, LoginActivity.class);
                        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(SplashScreen.this, splashLogo, ViewCompat.getTransitionName(splashLogo));
                        startActivity(intent, options.toBundle());
                    }

                } else {
                    Intent intent = new Intent(SplashScreen.this, LoginActivity.class);
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(SplashScreen.this, splashLogo, ViewCompat.getTransitionName(splashLogo));
                    startActivity(intent, options.toBundle());
                }


            }
        }, 3000);

    }
}
