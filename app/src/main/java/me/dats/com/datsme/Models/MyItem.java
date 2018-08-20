package me.dats.com.datsme.Models;

import android.graphics.Bitmap;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.maps.android.clustering.ClusterItem;

import java.io.Serializable;

public class MyItem implements ClusterItem, Serializable {
    private String URL;
    private transient LatLng mPosition;
    private String mTitle;
    private String mSnippet;
    private transient Marker marker;
    private transient Bitmap bitmap;

    public MyItem(double lat, double lng, String title, String mSnippet, String s) {
        Log.d("TAG", "item: " + title);
        mPosition = new LatLng(lat, lng);
        mTitle = title;
        URL = s;
        this.mSnippet = mSnippet;
    }

    public Marker getMyItemMarker() {
        return this.marker;
    }

    public void setMyItemMarker(Marker marker) {
        this.marker = marker;
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

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
