package me.dats.com.datsme.Utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import me.dats.com.datsme.Models.MyItem;

public class ClusterRender extends DefaultClusterRenderer<MyItem> {

    public String TAG = "LETS";

    public ClusterRender(Context context, GoogleMap map, ClusterManager clusterManager) {

        super(context, map, clusterManager);
        Log.d(TAG, "onBeforeClusterItemRendered: it is called soon" );
    }

    @Override
    protected void onBeforeClusterItemRendered(final MyItem item, final MarkerOptions markerOptions) {
        Log.d(TAG, "onBeforeClusterItemRendered:" + item.getBitmap());
        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(item.getBitmap()));
    }

}
