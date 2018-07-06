package me.dats.com.datsme.Adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import me.dats.com.datsme.Fragments.Discover_people;
import me.dats.com.datsme.Fragments.Messages;
import me.dats.com.datsme.Fragments.My_Profile;

public class PagerViewAdapter extends FragmentPagerAdapter {

    public PagerViewAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public int getCount() {
        return 3;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                return new Messages();
            case 1:
                return new Discover_people();
            case 2:
                return new My_Profile();
            default:
                return null;
        }
    }
}