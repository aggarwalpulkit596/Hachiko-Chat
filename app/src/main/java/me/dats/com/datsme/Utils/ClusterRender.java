package me.dats.com.datsme.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

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

    @Override
    protected void onBeforeClusterRendered(Cluster<MyItem> cluster, MarkerOptions markerOptions) {
        super.onBeforeClusterRendered(cluster, markerOptions);

    }

    @Override
    public void setAnimation(boolean animate) {
        super.setAnimation(animate);
    }

    @Override
    protected void onBeforeClusterItemRendered(final MyItem item, final MarkerOptions markerOptions) {
        super.onBeforeClusterItemRendered(item,markerOptions);
        Log.d(TAG, "onBeforeClusterItemRendered:" + item.getBitmap());
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(item.getBitmap()));

    }
    @Override
    protected void onClusterItemRendered(MyItem clusterItem, Marker marker) {

        super.onClusterItemRendered(clusterItem, marker);
    }
}
