package me.dats.com.datsme.Activities;

import android.content.Intent;
import android.graphics.Bitmap;
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
    String User,userName,oabout,ocollege,oplace,odob,ogender;
    Bitmap bitmap;
    private DatabaseReference mRootRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_others_profile);
        ButterKnife.bind(this);
        User = getIntent().getStringExtra("user_id");
        userName = getIntent().getStringExtra("name");
        bitmap = (Bitmap) getIntent().getParcelableExtra("bitmap");

        image.setImageBitmap(bitmap);
        mRootRef = FirebaseDatabase.getInstance().getReference();
        DatabaseReference mRef=mRootRef.child("Users").child(User);
        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                oabout=String.valueOf(dataSnapshot.child("about").getValue());
                ocollege=String.valueOf(dataSnapshot.child("college").getValue());
                oplace=String.valueOf(dataSnapshot.child("place").getValue());
                odob=String.valueOf(dataSnapshot.child("dob").getValue());
                ogender=String.valueOf(dataSnapshot.child("gender").getValue());
                name.setText(userName);
                place.setText(oplace);
                gender.setText(ogender);
                about.setText(oabout);
                college.setText(ocollege);
                dob.setText(odob);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
