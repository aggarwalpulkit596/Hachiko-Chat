package me.dats.com.datsme.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetDialogFragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import me.dats.com.datsme.Activities.Others_profile;
import me.dats.com.datsme.Models.Users;
import me.dats.com.datsme.R;

public class BottomSheetProfileFragment extends BottomSheetDialogFragment {

    //Views
    @BindView(R.id.user_image)
    CircleImageView mProfileImage;
    @BindView(R.id.user_displayname)
    TextView mProfileName;
    @BindView(R.id.user_aboutyou)
    TextView mProfileAbout;
    @BindView(R.id.userplace)
    TextView mProfilePlace;
    @BindView(R.id.usercollege)
    TextView mProfileCollege;
    @BindView(R.id.Other_compatibility)
    TextView compatibility;
    @BindView(R.id.user_age)
    TextView mProfileAge;
    @BindView(R.id.view_Other_profile)
    Button view_Other_Profile;

    Users user;
    private String user_id;
    private HashMap<String, String> culist = new HashMap<>();
    private HashMap<String, String> oulist = new HashMap<>();
    float count = 0;
    private DatabaseReference mCurrentUserDatabase;
    DatabaseReference mOtherUserDatabase;


    public BottomSheetProfileFragment() {
        // Required empty public constructor
    }

    public static BottomSheetProfileFragment newInstance(String user_id) {
        BottomSheetProfileFragment bottomSheetFragment = new BottomSheetProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putString("user_id", user_id);
        Log.d("TAG", "newInstance: " + user_id);
        bottomSheetFragment.setArguments(bundle);

        return bottomSheetFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.bottom_sheet_profile, container, false);
        user_id = getArguments().getString("user_id");
        ButterKnife.bind(this, root);
        mOtherUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        mOtherUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    bindData(dataSnapshot);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mCurrentUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getUid()).child("Tags");
        if (!user_id.equals(FirebaseAuth.getInstance().getUid()))
            findCompatibility();
        return root;
    }

    private void bindData(DataSnapshot documentSnapshot) throws Exception {

        user = documentSnapshot.getValue(Users.class);

        Log.d("TAGonbottomsheet page", "bindData: " + user_id + "  " + FirebaseAuth.getInstance().getCurrentUser().getUid().toString());
        if (user_id.equals(FirebaseAuth.getInstance().getCurrentUser().getUid().toString())) {
            view_Other_Profile.setEnabled(false);
            view_Other_Profile.setVisibility(View.GONE);
        } else {
            view_Other_Profile.setEnabled(true);
            view_Other_Profile.setVisibility(View.VISIBLE);
        }

        String age = user.getDateofbirth();
        Date date1 = new SimpleDateFormat("dd/MM/yyyy").parse(age);
        Date now = new Date();
        long timeBetween = now.getTime() - date1.getTime();
        double yearsBetween = timeBetween / 3.15576e+10;
        int age1 = (int) Math.floor(yearsBetween);
        String age_yrs = Integer.toString(age1);
        mProfileName.setText(user.getName());
        mProfileAbout.setText(user.getAbout());
        mProfilePlace.setText(user.getPlace());
        mProfileCollege.setText(user.getCollege());
        // mProfileAge.setText(", " + age_yrs);
        Picasso.get()
                .load(user.getThumb_image())
                .placeholder(R.drawable.default_avatar)
                .into(mProfileImage);

    }


    @OnClick(R.id.view_Other_profile)
    public void ViewProfile() {
        Intent i = new Intent(getContext(), Others_profile.class);
        i.putExtra("from_user_id", user_id);
        i.putExtra("userName", user.getName());
        startActivity(i);
        this.dismiss();
    }

    @Override
    public void onResume() {
        super.onResume();

        // Resize bottom sheet dialog so it doesn't span the entire width past a particular measurement
        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int width = metrics.widthPixels - 100;
        int height = -1; // MATCH_PARENT

        getDialog().getWindow().setLayout(width, height);
    }

    public void findCompatibility() {
        mCurrentUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {

                    culist.put(dsp.getKey(), dsp.getValue().toString());

                }
                mOtherUserDatabase.child("Tags").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                            oulist.put(dsp.getKey(), dsp.getValue().toString());
                        }
                        try {
                            for (String k : oulist.keySet()) {
                                if (culist.get(k).equals(oulist.get(k))) {
                                    ++count;
                                }
                            }
                        } catch (NullPointerException np) {
                            Log.i("TAG", "findCompatibility: " + np.getLocalizedMessage());
                        } finally {
                            int size = oulist.size() > culist.size() ? culist.size() : oulist.size();
                            int comp = (int) (count / size * 100);

                            int min;
                            int max;
                            if (comp <= 20 && comp >= 0) {
                                min = 50;
                                max = 60;
                                comp = new Random().nextInt((max - min) + 1) + min;
                            } else if (comp <= 40 && comp > 20) {
                                min = 60;
                                max = 70;
                                comp = new Random().nextInt((max - min) + 1) + min;
                            } else if (comp <= 60 && comp > 40) {
                                min = 70;
                                max = 80;
                                comp = new Random().nextInt((max - min) + 1) + min;
                            } else if (comp <= 80 && comp > 60) {
                                min = 80;
                                max = 90;
                                comp = new Random().nextInt((max - min) + 1) + min;
                            } else if (comp <= 100 && comp > 80) {
                                min = 90;
                                max = 98;
                                comp = new Random().nextInt((max - min) + 1) + min;
                            }
                            compatibility.setText(comp + "%");
                            compatibility.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}