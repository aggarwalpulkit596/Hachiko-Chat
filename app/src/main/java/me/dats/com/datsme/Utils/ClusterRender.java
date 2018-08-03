package me.dats.com.datsme.Utils;

import android.content.Context;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

import me.dats.com.datsme.Models.MyItem;

public class ClusterRender extends DefaultClusterRenderer<MyItem> {

    public String TAG = "LETS";

    public ClusterRender(Context context, GoogleMap map, ClusterManager clusterManager) {

        super(context, map, clusterManager);
    }


    @Override
    protected boolean shouldRenderAsCluster(Cluster<MyItem> cluster) {
        //start clustering if at least 2 items overlap
        return cluster.getSize() > 1;
    }
//
//    @Override
//    public void setAnimation(boolean animate) {
//        super.setAnimation(animate);
//    }

    @Override
    protected void onBeforeClusterItemRendered(MyItem item, MarkerOptions markerOptions) {

        Log.d(TAG, "onBeforeClusterItemRendered:" + item.getBitmap());
        item.setMyItemMarker(getMarker(item));
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(item.getBitmap()));


    }

    @Override
    protected void onClusterItemRendered(MyItem clusterItem, Marker marker) {
        clusterItem.setMyItemMarker(marker);
        super.onClusterItemRendered(clusterItem, marker);
    }
}