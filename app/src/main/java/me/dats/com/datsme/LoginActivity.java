package me.dats.com.datsme;

import android.support.annotation.ColorRes;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.dats.com.datsme.Adapters.AuthAdapter;
import me.dats.com.datsme.Widgets.AnimatedViewPager;

public class LoginActivity extends AppCompatActivity {

    @BindView(R.id.pager)
    AnimatedViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        viewPager.post(new Runnable() {
            @Override
            public void run() {
                AuthAdapter adapter = new AuthAdapter(getSupportFragmentManager(), viewPager);
                viewPager.setAdapter(adapter);
            }
        });




    }
}
