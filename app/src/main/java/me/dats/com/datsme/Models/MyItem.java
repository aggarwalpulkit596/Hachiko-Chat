package me.dats.com.datsme.Models;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class MyItem implements ClusterItem {
    public String URL;
    private LatLng mPosition;
    private String mTitle;
    private String mSnippet;

    public MyItem(double lat, double lng) {
        mPosition = new LatLng(lat, lng);
    }

    public MyItem(double lat, double lng, String title, String mSnippet, String s) {
        mPosition = new LatLng(lat, lng);
        mTitle = title;
        URL = s;
        this.mSnippet = mSnippet;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    @Override
    public LatLng getPosition() {
        return mPosition;
    }


    @Override
    public String getTitle() {
        return mTitle;
    }

    @Override
    public String getSnippet() {
        return mSnippet;
    }
}
