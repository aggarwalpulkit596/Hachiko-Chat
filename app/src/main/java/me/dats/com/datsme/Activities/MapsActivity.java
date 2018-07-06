package me.dats.com.datsme.Activities;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.dats.com.datsme.Adapters.PagerViewAdapter;
import me.dats.com.datsme.R;

public class MapsActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.messages)
    LinearLayout messages;
    @BindView(R.id.discover)
    LinearLayout discover;
    @BindView(R.id.myprofile)
    LinearLayout myprofile;
    @BindView(R.id.map_view_pager)
    ViewPager viewPager;
    PagerViewAdapter mPagerViewdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        ButterKnife.bind(this);
        SetmyviewPager();
    }

    private void SetmyviewPager() {

        viewPager.setOffscreenPageLimit(2);

        mPagerViewdapter = new PagerViewAdapter(getSupportFragmentManager());

        viewPager.setAdapter(mPagerViewdapter);

        messages.setOnClickListener(this);
        discover.setOnClickListener(this);
        myprofile.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId())
        {
            case R.id.messages:
                viewPager.setCurrentItem(0);
                break;
            case R.id.discover:
                viewPager.setCurrentItem(1);
                break;
            case R.id.myprofile:
                viewPager.setCurrentItem(2);
                break;

        }
    }
}