package me.dats.com.datsme.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import me.dats.com.datsme.Activities.MapsActivity;
import me.dats.com.datsme.Fragments.BottomSheetProfileFragment;
import me.dats.com.datsme.Models.Users;
import me.dats.com.datsme.R;
import me.dats.com.datsme.Utils.BubbleTransformation;

public class UsersViewAdapter extends RecyclerView.Adapter<UsersViewAdapter.UsersViewHolder> {

    HashMap<String, LatLng> userMap = new HashMap<>();
    HashMap<String, Marker> markers = new HashMap<>();
    Context mContext;
    private List<Users> mUsersList = new ArrayList<>();
    private List<String> mUsersUid = new ArrayList<>();
    private GoogleMap mMap;

    public UsersViewAdapter(List<Users> mUsers, List<String> mUsersUid, FragmentActivity activity, GoogleMap mMap) {
        this.mUsersList = mUsers;
        this.mUsersUid = mUsersUid;
        this.mContext = activity;
        this.mMap = mMap;
    }


    @NonNull
    @Override
    public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        return new UsersViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.user_layout, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull UsersViewHolder holder, int position) {
        final Users mUser = mUsersList.get(position);
        final String userId = mUsersUid.get(position);
        holder.bind(mUser);
        LatLng latLng1 = new LatLng(mUser.getLattitude(), mUser.getLongitude());
        MarkerOptions mo = new MarkerOptions().position(latLng1).title(mUser.getName()).snippet(userId);
        ;
        LatLng name = userMap.get(mUser.getName());
        if (name == null) {
            userMap.put(mUser.getName(), latLng1);
            final Marker userMarker = mMap.addMarker(mo);
            markers.put(mUser.getName(), userMarker);
            Picasso.get()
                    .load(mUser.getThumb_image())
                    .resize(250, 250)
                    .centerInside()
                    .transform(new BubbleTransformation(10))

                    .into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                            userMarker.setIcon(BitmapDescriptorFactory.fromBitmap(bitmap));

                        }

                        @Override
                        public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {
                        }
                    });
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    Log.i("TAG", "onMarkerClick: " + marker.getTitle());
                    BottomSheetProfileFragment bottomSheetFragment = new BottomSheetProfileFragment();
                    BottomSheetProfileFragment.newInstance(marker.getSnippet()).show(((MapsActivity) mContext).getSupportFragmentManager(), bottomSheetFragment.getTag());
                    return true;
                }
            });

        } else {
            Marker marker = markers.get(mUser.getName());
            marker.remove();
            marker.setPosition(latLng1);
            marker = mMap.addMarker(mo);
            final Marker finalMarker = marker;
            Picasso.get()
                    .load(mUser.getThumb_image())
                    .resize(250, 250)
                    .centerInside()
                    .transform(new BubbleTransformation(10))

                    .into(new Target() {
                        @Override
                        public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                            finalMarker.setIcon(BitmapDescriptorFactory.fromBitmap(bitmap));
                        }

                        @Override
                        public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                        }

                        @Override
                        public void onPrepareLoad(Drawable placeHolderDrawable) {
                        }
                    });
            markers.put(mUser.getName(), marker);
            mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    BottomSheetProfileFragment bottomSheetFragment = new BottomSheetProfileFragment();
                    BottomSheetProfileFragment.newInstance(marker.getSnippet()).show(((MapsActivity) mContext).getSupportFragmentManager(), bottomSheetFragment.getTag());
                    return true;
                }
            });
        }
//        if(notification_uid!=null){
//            BottomSheetProfileFragment bottomSheetFragment = new BottomSheetProfileFragment();
//            BottomSheetProfileFragment.newInstance(notification_uid).show(getActivity().getSupportFragmentManager(), bottomSheetFragment.getTag());
//        }
        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BottomSheetProfileFragment bottomSheetFragment = new BottomSheetProfileFragment();
                BottomSheetProfileFragment.newInstance(userId).show(((MapsActivity) mContext).getSupportFragmentManager(), bottomSheetFragment.getTag());

            }
        });


    }

    @Override
    public int getItemCount() {
        return mUsersList.size();
    }

    public static class UsersViewHolder extends RecyclerView.ViewHolder {

        View mView;

        UsersViewHolder(final View itemView) {
            super(itemView);
            mView = itemView;

        }

        public void setName(String name) {
            TextView userNameView = mView.findViewById(R.id.name);
            userNameView.setText(name);
        }

        void bind(Users model) {
            String name1 = model.getName();
//            String[] arr = name1.split(" ");
//            String fname = arr[0];
            setName(name1);
            setThumbImage(model.getThumb_image());
        }

        void setThumbImage(String thumbImage) {
            CircleImageView userImageView = mView.findViewById(R.id.image);
            if (!thumbImage.equals("default"))
                Picasso.get()
                        .load(thumbImage)
                        .placeholder(R.drawable.default_avatar)
                        .into(userImageView);

        }

    }
}
