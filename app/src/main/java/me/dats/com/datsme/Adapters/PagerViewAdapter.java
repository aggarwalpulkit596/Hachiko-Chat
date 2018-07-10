package me.dats.com.datsme.Adapters;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;

import me.dats.com.datsme.Fragments.Discover_people;
import me.dats.com.datsme.Fragments.Messages;
import me.dats.com.datsme.Fragments.My_Profile;

public class PagerViewAdapter extends FragmentPagerAdapter {

    Bundle bundle;

    public PagerViewAdapter(FragmentManager fm, String user_id) {
        super(fm);
        bundle = new Bundle();
        bundle.putString("request_uid", user_id);
        Log.i("Notification", "PagerViewAdapter: "+user_id);
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
                Discover_people people = new Discover_people();
                people.setArguments(bundle);
                return people;
            case 2:
                return new My_Profile();
            default:
                return null;
        }
    }
}