package me.dats.com.datsme.Fragments;


import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.dats.com.datsme.Activities.LoginActivity;
import me.dats.com.datsme.Adapters.UsersViewAdapter;
import me.dats.com.datsme.Datsme;
import me.dats.com.datsme.Models.MyItem;
import me.dats.com.datsme.Models.Users;
import me.dats.com.datsme.R;
import me.dats.com.datsme.Utils.BubbleTransformation;
import me.dats.com.datsme.Utils.ClusterRender;
import me.dats.com.datsme.Utils.SpacesItemDecoration;

/**
 * A simple {@link Fragment} subclass.
 */
public class Discover_people extends Fragment implements OnMapReadyCallback, ClusterManager.OnClusterClickListener, GoogleMap.OnCameraIdleListener, GoogleMap.OnCameraMoveStartedListener, GoogleMap.OnCameraMoveListener {

    protected static final int REQUEST_CHECK_SETTINGS = 0x1;
    public float Timer = 0;
    public CountDownTimer countDownTimer;
    @BindView(R.id.toggle_profile_button)
    ImageButton toggle_profile_button;
    @BindView(R.id.profile_box)
    RelativeLayout Profile_box;
    boolean IsProfileVisible = false;
    @BindView(R.id.rv)
    RecyclerView mRecyclerView;
    FusedLocationProviderClient mFusedLocationProviderClient;
    LocationCallback mLocationCallback;
    LocationRequest mLocationRequest;
    boolean mRequestingLocationUpdates;
    android.location.Location location;
    HashMap<String, MyItem> ItemsMap = new HashMap<>();
    HashMap<String, LatLng> userMap = new HashMap<>();
    HashMap<String, Target> targets = new HashMap<>();
    float MaxZoom = 19.05f;
    float zoomLevel = 15.0f; //This goes up to 21\
    boolean firstlauch = true;
    View view;
    private ClusterManager<MyItem> mClusterManager;
    private ClusterRender clusterRender;
    private Animation animShow, animHide;
    private DatabaseReference mUserRef, mBlocklist;
    private GoogleMap mMap;
    private String TAG = "MapsActivity";

    public Discover_people() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_discover_people, container, false);
        ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);


        toggle_profile_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (IsProfileVisible) {
                    IsProfileVisible = false;
                    Profile_box.startAnimation(animHide);
                    toggle_profile_button.setImageResource(R.drawable.ic_expand_more_black_24dp);
                    Profile_box.setVisibility(View.INVISIBLE);
                } else {
                    if (Timer > 0) {
                        countDownTimer.cancel();
                    }
                    IsProfileVisible = true;
                    toggle_profile_button.setImageResource(R.drawable.ic_expand_less_black_24dp);
                    Profile_box.setVisibility(View.VISIBLE);
                    Profile_box.startAnimation(animShow);
                }
            }
        });

        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        mUserRef.keepSynced(true);
        mBlocklist = FirebaseDatabase.getInstance().getReference().child("Blocklist").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        mBlocklist.keepSynced(true);


        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(final LocationResult locationResult) {
                super.onLocationResult(locationResult);
                location = locationResult.getLastLocation();
                final Map<String, Object> locationMap = new HashMap<>();
                locationMap.put("lattitude", location.getLatitude());
                locationMap.put("longitude", location.getLongitude());

                DatabaseReference r = FirebaseDatabase.getInstance().getReference().child("Users").child(mUserRef.getKey());
                r.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()) {
                            mUserRef.updateChildren(locationMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "onComplete: " + location);
                                    }
                                }
                            });
                        } else {
                            mLocationCallback = null;
                            mLocationRequest = null;
                            mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    Datsme.getPreferenceManager().clearLoginData();
                                    Intent i = new Intent(getActivity(), LoginActivity.class);
                                    getActivity().startActivity(i);
                                    getActivity().finish();
                                }
                            });

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

                if (firstlauch) {
                    firstlauch = false;
                    moveCamera();
                }
            }
        };
        mapFragment.getMapAsync(this);
        fetchusers();
        fetchlocation();
        initAnimation();
        super.onActivityCreated(savedInstanceState);
    }

    private void moveCamera() {
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), zoomLevel));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setOnCameraIdleListener(this);
        mMap.setOnCameraMoveStartedListener(this);
        mMap.setOnCameraMoveListener(this);
        mMap.setMaxZoomPreference(MaxZoom);

        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.getUiSettings().setZoomControlsEnabled(false);

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mFusedLocationProviderClient
                .requestLocationUpdates(mLocationRequest,
                        mLocationCallback, null);
        mRequestingLocationUpdates = true;

        MapStyleOptions.loadRawResourceStyle(getActivity(), R.raw.mymapstyle);
        setUpClusterer();
        setMarkers();
    }

    private void setMarkers() {

        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Users");
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                final Users mUser = dataSnapshot.getValue(Users.class);
                final String user_id = dataSnapshot.getKey();

                mBlocklist.child(user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (!dataSnapshot.exists()) {
                            if (userMap.get(user_id) == null && ItemsMap.get(user_id) == null) {

                                Log.d(TAG, "onDataChange:1 "+mUser.getName().toString());
                                userMap.put(user_id, new LatLng(mUser.getLattitude(), mUser.getLongitude()));
                                final MyItem myItem = new MyItem(mUser.getLattitude(), mUser.getLongitude(), mUser.getName(), dataSnapshot.getKey(), mUser.thumb_image);
                                ItemsMap.put(user_id, myItem);

                                targets.put(user_id, new Target() {
                                    @Override
                                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {

                                        myItem.setBitmap(bitmap);
                                        mClusterManager.addItem(myItem);
                                        mClusterManager.cluster();

                                    }

                                    @Override
                                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                                    }

                                    @Override
                                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                                    }
                                });
                                Picasso.get().load(mUser.getThumb_image()).resize(150, 150)
                                        .centerInside()
                                        .transform(new BubbleTransformation(10))
                                        .into(targets.get(user_id));
                            }

                        } else {
                            if (userMap.get(user_id) != null && ItemsMap.get(user_id) != null) {

                                MyItem item = ItemsMap.get(user_id);
                                Marker marker = item.getMyItemMarker();
                                mClusterManager.getMarkerManager().remove(marker);
                                mClusterManager.removeItem(item);
                                mClusterManager.cluster();
                                userMap.remove(user_id);
                                ItemsMap.remove(user_id);

                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                final Users mUser = dataSnapshot.getValue(Users.class);
                final String user_id = dataSnapshot.getKey();

                mBlocklist.child(user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(!dataSnapshot.exists())
                        {
                            if (userMap.get(user_id)==null && ItemsMap.get(user_id)==null) {
                                Log.d(TAG, "onChildChanged: not exists");
                            } else {
                                Log.d(TAG, "onDataChange:2 "+mUser.getName().toString());
                                MyItem item = ItemsMap.get(user_id);
                                Marker marker = item.getMyItemMarker();
                                mClusterManager.getMarkerManager().remove(marker);
                                mClusterManager.removeItem(item);
                                mClusterManager.cluster();

                                final MyItem myItem = new MyItem(mUser.getLattitude(), mUser.getLongitude(), mUser.getName(), dataSnapshot.getKey(), mUser.thumb_image);

                                ItemsMap.put(user_id, myItem);
                                userMap.put(user_id, new LatLng(mUser.getLattitude(), mUser.getLongitude()));

                                targets.put(user_id, new Target() {
                                    @Override
                                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                        myItem.setBitmap(bitmap);
                                        mClusterManager.addItem(myItem);
                                        mClusterManager.cluster();
                                    }

                                    @Override
                                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                                    }

                                    @Override
                                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                                    }
                                });
                                Picasso.get().load(mUser.getThumb_image()).resize(150, 150)
                                        .centerInside()
                                        .transform(new BubbleTransformation(10))
                                        .into(targets.get(user_id));
                            }

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }

                });

            }


            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void initAnimation() {
        animShow = AnimationUtils.loadAnimation(getActivity(), R.anim.show_from_side);
        animHide = AnimationUtils.loadAnimation(getActivity(), R.anim.hide_from_side);
    }

    private void fetchlocation() {

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        mLocationRequest = new LocationRequest();

        mLocationRequest.setInterval(10000)
                .setFastestInterval(5000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        GoogleApiClient googleApiClient = new GoogleApiClient.Builder(getActivity())
                .addApi(LocationServices.API).build();
        googleApiClient.connect();
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(mLocationRequest);
        builder.setAlwaysShow(true);
        PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:

                        try {
                            status.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:

                        break;
                }
            }
        });

    }

    private void fetchusers() {

        mRecyclerView.setHasFixedSize(true);
        int spacingInPixels = 10;

        mRecyclerView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.HORIZONTAL, false));
        mRecyclerView.setItemViewCacheSize(10);
        final UsersViewAdapter usersViewAdapter;
        final List<String> mUserKey = new ArrayList<>();
        final List<Users> mUsersList = new ArrayList<>();
        usersViewAdapter = new UsersViewAdapter(mUsersList, mUserKey, getActivity());

        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Users");
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                final Users mUser = dataSnapshot.getValue(Users.class);
                final String user_id = dataSnapshot.getKey();
                mBlocklist.child(user_id).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if (!dataSnapshot.exists()) {
                            mUsersList.add(mUser);
                            mUserKey.add(user_id);
                            usersViewAdapter.notifyDataSetChanged();
                        } else {

                            mUsersList.remove(mUser);
                            mUserKey.remove(user_id);
                            usersViewAdapter.notifyDataSetChanged();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mRecyclerView.setAdapter(usersViewAdapter);


    }

    private void setUpClusterer() {

        mClusterManager = new ClusterManager(getActivity(), mMap);
        mClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<MyItem>() {
            @Override
            public boolean onClusterItemClick(MyItem myItem) {
                BottomSheetProfileFragment bottomSheetFragment = new BottomSheetProfileFragment();
                BottomSheetProfileFragment.newInstance(myItem.getSnippet()).show(getActivity().getSupportFragmentManager(), bottomSheetFragment.getTag());
                return true;
            }
        });
        mMap.setOnMarkerClickListener(mClusterManager);
        mClusterManager.setOnClusterClickListener(this);
        mClusterManager.setOnClusterClickListener(this);

        if (getActivity() != null)//added
            clusterRender = new ClusterRender(getActivity(), mMap, mClusterManager);//added

        mClusterManager.setRenderer(clusterRender);


    }

    @Override
    public boolean onClusterClick(Cluster cluster) {

        if (zoomLevel < MaxZoom) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(cluster.getPosition(), 19.05f));
        } else {
            ArrayList<MyItem> markeritems = new ArrayList<>(cluster.getItems());
            BottomSheetListFragment bottomSheetFragment = new BottomSheetListFragment();
            BottomSheetListFragment.newInstance(markeritems).show(getActivity().getSupportFragmentManager(), bottomSheetFragment.getTag());
        }


        return true;
    }

    @Override
    public void onCameraIdle() {

        mClusterManager.onCameraIdle();
        if (mMap.getMapType() == GoogleMap.MAP_TYPE_HYBRID && mMap.getCameraPosition().zoom < 17.05f) {
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
            zoomLevel = mMap.getCameraPosition().zoom;
        } else if (mMap.getMapType() == GoogleMap.MAP_TYPE_NORMAL && mMap.getCameraPosition().zoom > 17.05f) {
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            zoomLevel = mMap.getCameraPosition().zoom;
        }
        if (Timer > 0) {
            countDownTimer.cancel();
        }
        countDownTimer = new CountDownTimer(3000, 1000) {
            @Override
            public void onTick(long l) {
                Timer = l / 1000;

            }

            @Override
            public void onFinish() {
                IsProfileVisible = true;
                toggle_profile_button.setImageResource(R.drawable.ic_expand_less_black_24dp);
                Profile_box.setVisibility(View.VISIBLE);
                Profile_box.startAnimation(animShow);
            }
        }.start();
    }

    @Override
    public void onCameraMoveStarted(int i) {

        if (Timer > 0) {
            countDownTimer.cancel();
        }
        if (IsProfileVisible) {
            IsProfileVisible = false;
            Profile_box.startAnimation(animHide);
            toggle_profile_button.setImageResource(R.drawable.ic_expand_more_black_24dp);
            Profile_box.setVisibility(View.INVISIBLE);
        }

    }

    @Override
    public void onCameraMove() {
        zoomLevel = mMap.getCameraPosition().zoom;
    }

    @OnClick(R.id.location_icon)
    public void goToMyLocation() {
        if (location != null) {
            LatLng coordinate = new LatLng(location.getLatitude(), location.getLongitude()); //Store these lat lng values somewhere. These should be constant.
            mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);
            mMap.animateCamera(CameraUpdateFactory.newCameraPosition(
                    new CameraPosition.Builder()
                            .target(coordinate)
                            .tilt(90)
                            .zoom(17.05f)
                            .build()));
            zoomLevel = mMap.getCameraPosition().zoom;
        }
    }

    @Override
    public void onDestroy() {

        mFusedLocationProviderClient.removeLocationUpdates(mLocationCallback).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Discover_people.super.onDestroy();
            }
        });
    }

}