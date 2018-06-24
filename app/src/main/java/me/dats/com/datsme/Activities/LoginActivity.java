package me.dats.com.datsme.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.dats.com.datsme.Adapters.AuthAdapter;
import me.dats.com.datsme.R;
import me.dats.com.datsme.Widgets.AnimatedViewPager;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.pager)
    AnimatedViewPager viewPager;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();

        viewPager.post(new Runnable() {
            @Override
            public void run() {
                AuthAdapter adapter = new AuthAdapter(getSupportFragmentManager(), viewPager);
                viewPager.setAdapter(adapter);
            }
        });
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser!=null && currentUser.isEmailVerified()){
            startActivity(new Intent(LoginActivity.this, MapsActivity.class));
        }else if(currentUser!=null && !currentUser.isEmailVerified()){
            Toast.makeText(this, "Please Verify Your Account First.", Toast.LENGTH_SHORT).show();

        }
    }
}
