package me.dats.com.datsme.Activities;

import android.app.ProgressDialog;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import me.dats.com.datsme.Models.Users;
import me.dats.com.datsme.R;

public class Others_profile extends AppCompatActivity {
    @BindView(R.id.Other_image)
    ImageView image;
    @BindView(R.id.Other_name)
    TextView name;


    String user_id, userName;

    @BindViews({R.id.Other_firstButton, R.id.Other_secondButton})
    RelativeLayout[] button;

    @BindViews({R.id.Other_firstText, R.id.Other_secondText})
    TextView[] textView;

    @BindViews({R.id.Other_firstIcon, R.id.Other_secondIcon})
    ImageView[] imageView;

    private DatabaseReference mOtherUserDatabase, mFriendReqDatabse, mFriendsDatabase;
    private FirebaseUser mCurrentUser;
    private String mCurrent_State;
    private String current_uid;
    private ProgressDialog mLoadProcess;
    private DatabaseReference mRootRef;
    private DatabaseReference mUserDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_others_profile);
        ButterKnife.bind(this);

        user_id = getIntent().getStringExtra("from_user_id");
        userName = getIntent().getStringExtra("userName");

        init();

        if (mCurrentUser.getUid().equals(user_id)) {
            setvisibility(0, false);
            setvisibility(1, false);
        }

        bindData();


    }

    private void bindData() {
        mOtherUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    Users otherUser = dataSnapshot.getValue(Users.class);

                    String age = otherUser.getDateofbirth();
                    Date date1 = new SimpleDateFormat("dd/MM/yyyy").parse(age);
                    Date now = new Date();
                    long timeBetween = now.getTime() - date1.getTime();
                    double yearsBetween = timeBetween / 3.15576e+10;
                    int age1 = (int) Math.floor(yearsBetween);
                    String age_yrs = Integer.toString(age1);
                    name.setText(otherUser.getName());
                    Picasso.get()
                            .load(otherUser.getThumb_image())
                            .placeholder(R.drawable.default_avatar)
                            .centerCrop()
                            .fit()
                            .into(image);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                friendlist();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void friendlist() {
        //----------FRIEND LIST/REQUEST --------------
        mFriendReqDatabse.child(mCurrentUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild(user_id)) {

                    String req_type = dataSnapshot.child(user_id).child("request_type").getValue().toString();

                    if (req_type.equals("received")) {

                        mCurrent_State = "req_recieved";
                        setvisibility(0,true);
                        textView[0].setText("Accept Request");
                        imageView[0].setImageDrawable(getResources().getDrawable(R.drawable.ic_person_add_black_24dp));
                        setvisibility(1,true);

                    } else if (req_type.equals("sent")) {
                        mCurrent_State = "req_sent";
                        setvisibility(0,true);
                        textView[0].setText("Cancel Request");
                        imageView[0].setImageDrawable(getResources().getDrawable(R.drawable.cancel_request));
                        setvisibility(1,false);
                    }
                    mLoadProcess.dismiss();

                } else {

                    mFriendsDatabase.child(mCurrentUser.getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild(user_id)) {

                                mCurrent_State = "friends";
                                setvisibility(0,true);
                                button[0].setClickable(false);//we dont want to click here when we are friends its not unfriend button
                                textView[0].setText("Friends");
                                imageView[0].setImageDrawable(getResources().getDrawable(R.drawable.friends));
                                setvisibility(1,false);

                            } else {
                                mCurrent_State = "not friends";
                                setvisibility(0,true);
                                textView[0].setText("add friend");
                                imageView[0].setImageDrawable(getResources().getDrawable(R.drawable.ic_person_add_black_24dp));
                                setvisibility(1,false);
                            }
                            mLoadProcess.dismiss();

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                            mLoadProcess.dismiss();


                        }
                    });


                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void init() {
        //Firebase Instance
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mOtherUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        mFriendReqDatabse = FirebaseDatabase.getInstance().getReference().child("Friend_req");
        mFriendsDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());

        //Constants
        current_uid = mCurrentUser.getUid();
        mCurrent_State = "not friends";


        mLoadProcess = new ProgressDialog(this);
        mLoadProcess.setTitle("Getting Info");
        mLoadProcess.setMessage("PLease Wait...");
        mLoadProcess.setCanceledOnTouchOutside(false);
        mLoadProcess.show();
    }

    private void setvisibility(int i, boolean t) {
        if (!t) {
            button[i].setVisibility(View.INVISIBLE);
            button[i].setClickable(false);
        } else {
            button[i].setVisibility(View.VISIBLE);
            button[i].setClickable(true);
        }
    }
}