package me.dats.com.datsme.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
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

    Users user;

    @BindView(R.id.user_age)
    TextView mProfileAge;

    @BindView(R.id.view_Other_profile)
    Button view_Other_Profile;

    private String user_id;

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
        DatabaseReference mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        mUserDatabase.addValueEventListener(new ValueEventListener() {
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


        return root;
    }

    private void bindData(DataSnapshot documentSnapshot) throws Exception {

        user = documentSnapshot.getValue(Users.class);

        Log.d("TAGonbottomsheet page", "bindData: "+user_id+"  "+FirebaseAuth.getInstance().getCurrentUser().getUid().toString());
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
    }

    @Override
    public void onResume() {
        super.onResume();

        // Resize bottom sheet dialog so it doesn't span the entire width past a particular measurement
        WindowManager wm = (WindowManager) getActivity().getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);
        int width = metrics.widthPixels;
        int height = -1; // MATCH_PARENT

        getDialog().getWindow().setLayout(width, height);
    }
}