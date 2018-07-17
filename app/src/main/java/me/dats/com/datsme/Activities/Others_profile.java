package me.dats.com.datsme.Activities;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.dats.com.datsme.Models.Users;
import me.dats.com.datsme.R;

public class Others_profile extends AppCompatActivity {
    @BindView(R.id.other_image)
    ImageView image;
    @BindView(R.id.other_name)
    TextView name;
    @BindView(R.id.other_about)
    TextView about;
    @BindView(R.id.other_college)
    TextView college;
    @BindView(R.id.other_place)
    TextView place;
    @BindView(R.id.other_dob)
    TextView dob;
    @BindView(R.id.other_gender)
    TextView gender;
    String userId,userName;
    private DatabaseReference mRootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_others_profile);
        ButterKnife.bind(this);
        userId = getIntent().getStringExtra("from_user_id");
        userName = getIntent().getStringExtra("userName");



        mRootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference mRef=mRootRef.child("Users").child(userId);
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Users user = dataSnapshot.getValue(Users.class);
                name.setText(user.getName());
                gender.setText(user.getGender());
                about.setText(user.getAbout());
                college.setText(user.getCollege());
                dob.setText(user.getDateofbirth());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
