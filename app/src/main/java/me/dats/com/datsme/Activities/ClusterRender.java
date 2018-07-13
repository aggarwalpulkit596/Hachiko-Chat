package me.dats.com.datsme.Activities;

import android.content.ClipData;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import me.dats.com.datsme.Adapters.BubbleTransformation;

public class ClusterRender extends DefaultClusterRenderer<MyItem> {

    public ClusterRender(Context context, GoogleMap map, ClusterManager clusterManager,MyItem myItem) {

        super(context, map, clusterManager);
    }

public String TAG="LETS";



    @Override
    protected void onBeforeClusterItemRendered(final MyItem item, final MarkerOptions markerOptions) {
        Log.d(TAG, "onBeforeClusterItemRendered:"+item.URL);

        super.onBeforeClusterItemRendered(item, markerOptions);
        Target t=new Target() {

                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        Log.d(TAG, "onBeforeClusterItemRendered:"+bitmap);
                        markerOptions.icon(BitmapDescriptorFactory.fromBitmap(bitmap));
                        }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                };

                Picasso.get().load(item.URL).resize(150, 150)
                        .centerInside()
                        .transform(new BubbleTransformation(10))
                        .into(t);



    }
}
