package me.dats.com.datsme.Fragments;


import android.Manifest;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
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
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
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
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import me.dats.com.datsme.Adapters.BubbleTransformation;
import me.dats.com.datsme.Adapters.SpacesItemDecoration;
import me.dats.com.datsme.Models.Users;
import me.dats.com.datsme.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class Discover_people extends Fragment implements OnMapReadyCallback {

    @BindView(R.id.toggle_profile_button)
    ImageButton toggle_profile_button;

    @BindView(R.id.profile_box)
    RelativeLayout Profile_box;

    public float Timer = 0;
    public CountDownTimer countDownTimer;
    private Animation animShow, animHide;

    boolean IsProfileVisible = false;

    @BindView(R.id.rv)
    RecyclerView mRecyclerView;
    @BindView(R.id.seekBar)
    SeekBar seekBar;
    private DatabaseReference mUserRef;

    private GoogleMap mMap;
    private boolean doubleBackToExitPressedOnce = false;

    FusedLocationProviderClient mFusedLocationProviderClient;
    LocationCallback mLocationCallback;
    LocationRequest mLocationRequest;
    boolean mRequestingLocationUpdates;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    android.location.Location location;
    View thumbView;
    HashMap<String, LatLng> userMap;
    HashMap<String, Marker> markers;
    BitmapDescriptor bitmap1;
    int[][] worldview = new int[][]{
            {100, 50, 25, 50, 75},
            {50, 100, 25, 50, 75},
            {25, 25, 100, 50, 50},
            {30, 50, 50, 100, 50},
            {75, 75, 50, 50, 100}
    };
    Users mUser;


    protected static final int REQUEST_CHECK_SETTINGS = 0x1;

    public Discover_people() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_discover_people, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.map);
        thumbView = LayoutInflater.from(getActivity()).inflate(R.layout.thumb, null, false);
        userMap = new HashMap<>();
        markers = new HashMap<>();
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

                // You can have your own calculation for progress
//                seekBar.setThumb(getThumb(progress));progress
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                ((TextView) thumbView.findViewById(R.id.tvProgress)).setText(0 + "");
            }
        });

        mapFragment.getMapAsync(this);
        fetchusers();
        initAnimation();
        fetchlocation();
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
        Log.i("TAG", "onCreate: " + mUserRef.toString());
        mLocationCallback = new LocationCallback() {
            boolean firstlauch = true;

            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                location = locationResult.getLastLocation();
                Map<String, Object> locationMap = new HashMap<>();
                locationMap.put("lattitude", location.getLatitude());
                locationMap.put("longitude", location.getLongitude());
                mUserRef.updateChildren(locationMap);
                if (firstlauch) {
                    firstlauch = false;
                    float zoomLevel = 16.0f; //This goes up to 21
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), zoomLevel));
                }
            }
        };

        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        mMap.setMyLocationEnabled(true);
        mFusedLocationProviderClient
                .requestLocationUpdates(mLocationRequest,
                        mLocationCallback, null);
        mRequestingLocationUpdates = true;

        //for changing design of map
        try {
            Calendar calendar = Calendar.getInstance();
            int hourOfDay = calendar.get(Calendar.HOUR_OF_DAY);
            if (hourOfDay > 5 && hourOfDay < 21) {
                boolean success = mMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(getActivity(), R.raw.mymapstyle));
            } else {
                boolean success = mMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(getActivity(), R.raw.mymapsnight));
            }
        } catch (Resources.NotFoundException e) {
            // Oops, looks like the map style resource couldn't be found!
        }


        mMap.setOnCameraMoveStartedListener(new GoogleMap.OnCameraMoveStartedListener() {
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

        });
        mMap.setOnCameraIdleListener(new GoogleMap.OnCameraIdleListener() {
            @Override
            public void onCameraIdle() {

                if (Timer > 0) {
                    countDownTimer.cancel();
                }
                countDownTimer = new CountDownTimer(2000, 1000) {
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
        });
        setMarkers();
    }

    private void setMarkers() {

        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Users");
        query.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                mUser = dataSnapshot.getValue(Users.class);
                final String user_id = dataSnapshot.getKey();
                final LatLng latLng1 = new LatLng(mUser.getLattitude(), mUser.getLongitude());
                MarkerOptions mo = new MarkerOptions().position(latLng1).title(mUser.getName()).snippet(user_id);
                final Marker userMarker = mMap.addMarker(mo);
                Log.i("TAG", "onChildAdded: " + dataSnapshot.getValue(Users.class).getName() + userMarker);
//                Picasso.get()
//                        .load(mUser.getThumb_image())
//                        .resize(100, 100)
//                        .centerInside()
//                        .transform(new BubbleTransformation(10))
//                        .into(new Target() {
//                            @Override
//                            public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
//                                Log.i("TAG", "onBitmapLoaded: " + mUser.getName());
//                                bitmap1 = BitmapDescriptorFactory.fromBitmap(bitmap);
//                                MarkerOptions mo = new MarkerOptions().position(latLng1).title(mUser.getName()).snippet(user_id).icon(bitmap1);
//                                LatLng name = userMap.get(mUser.getName());
//                                if (name == null) {
//                                    userMap.put(mUser.getName(), latLng1);
//                                    final Marker userMarker = mMap.addMarker(mo);
//                                    markers.put(mUser.getName(), userMarker);
//                                    mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
//                                        @Override
//                                        public boolean onMarkerClick(Marker marker) {
//                                            Log.i("TAG", "onMarkerClick: " + marker.getTitle());
//                                            BottomSheetProfileFragment bottomSheetFragment = new BottomSheetProfileFragment();
//                                            BottomSheetProfileFragment.newInstance(marker.getSnippet()).show(getActivity().getSupportFragmentManager(), bottomSheetFragment.getTag());
//                                            return true;
//                                        }
//                                    });

//                                } else {
//                                    Marker marker = markers.get(mUser.getName());
//                                    marker.remove();
//                                    marker.setPosition(latLng1);
//                                    marker = mMap.addMarker(mo);
//                                    markers.put(mUser.getName(), marker);
//                                    mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
//                                        @Override
//                                        public boolean onMarkerClick(Marker marker) {
//                                            BottomSheetProfileFragment bottomSheetFragment = new BottomSheetProfileFragment();
//                                            BottomSheetProfileFragment.newInstance(marker.getSnippet()).show(getActivity().getSupportFragmentManager(), bottomSheetFragment.getTag());
//                                            return true;
//                                        }
//                                    });
            }


//            @Override
//            public void onBitmapFailed(Exception e, Drawable errorDrawable) {
//                Log.i("TAG", "onBitmapLoaded2: " + mUser.getName());
//
//            }
//
//            @Override
//            public void onPrepareLoad(Drawable placeHolderDrawable) {
//                Log.i("TAG", "onBitmapLoaded3: " + mUser.getName());
//            }
//        });
//
//        Log.i("TAG", "onBitmapLoaded4: " + mUser.getName());

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


        Query query = FirebaseDatabase.getInstance()
                .getReference()
                .child("Users");
        final FirebaseRecyclerOptions<Users> options =
                new FirebaseRecyclerOptions.Builder<Users>()
                        .setQuery(query, Users.class)
                        .build();

        FirebaseRecyclerAdapter firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<Users, UsersViewHolder>(options) {
            @NonNull
            @Override
            public UsersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

                return new UsersViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.user_layout, parent, false));
            }

            @Override
            protected void onBindViewHolder(@NonNull final UsersViewHolder holder, final int position, @NonNull final Users model) {
                holder.bind(model);
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        BottomSheetProfileFragment bottomSheetFragment = new BottomSheetProfileFragment();
                        BottomSheetProfileFragment.newInstance(getRef(position).getKey()).show(getActivity().getSupportFragmentManager(), bottomSheetFragment.getTag());

                    }
                });

            }
        };
        mRecyclerView.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();

    }

    private BitmapDescriptor getMarkerIconFromDrawable(Drawable drawable) {
        Canvas canvas = new Canvas();
        Bitmap bitmap = Bitmap.createBitmap(60, 60, Bitmap.Config.ARGB_8888);
        canvas.setBitmap(bitmap);
        drawable.setBounds(0, 0, 60, 60);
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);

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
            String[] arr = name1.split(" ");
            String fname = arr[0];
            setName(fname);
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