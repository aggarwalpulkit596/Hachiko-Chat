package me.dats.com.datsme.Activities;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.dats.com.datsme.Models.Users;
import me.dats.com.datsme.R;

public class Others_profile extends AppCompatActivity {
    @BindView(R.id.Other_image)
    ImageView image;
    @BindView(R.id.Other_name)
    TextView name;


    String userId,userName;

    @BindView(R.id.Other_addFriend)
    RelativeLayout addFriend;

    @BindView(R.id.Other_requestSent)
    RelativeLayout requestSent;

    @BindView(R.id.Other_cancelrequest)
    RelativeLayout cancelRequest;

    @BindView(R.id.Other_Friends)
    RelativeLayout friends;

    private DatabaseReference mRootRef;
    private FirebaseUser mCurrentUser;
    private DatabaseReference mRef;
    private DatabaseReference mFriendReqDatabse,mFriendsDatabase,mUserRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_others_profile);
        ButterKnife.bind(this);

        mRootRef = FirebaseDatabase.getInstance().getReference();

        mCurrentUser
                = FirebaseAuth.getInstance().getCurrentUser();
        current_uid = mCurrentUser.getUid();

        mFriendReqDatabse = FirebaseDatabase.getInstance().getReference().child("Friend_req");
        mFriendsDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());


        mRef=mRootRef.child("Users").child(userId);


        mCurrent_State = "not friends";

        cancelRequest.setVisibility(View.GONE);

        userId = getIntent().getStringExtra("from_user_id");
        userName = getIntent().getStringExtra("userName");




        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Users user = dataSnapshot.getValue(Users.class);

                name.setText(user.getName());
                Picasso.get().load(user.getImage()).centerCrop().fit().into(image);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }
    private String mCurrent_State;
    private String current_uid;
    @OnClick(R.id.Other_requestSent)
    public void SendFriendRequest() {

       // mProfileReqBtn.setEnabled(false);

        //-----------------NOT FRIENDS STATE---------


        if (mCurrent_State.equals("not friends")) {

            Map<String, String> notificationData = new HashMap<>();
            notificationData.put("from", current_uid);
            notificationData.put("type", "request");

            DatabaseReference newNotificationref = mRootRef.child("notifications").child(userId).push();
            String newNotificationId = newNotificationref.getKey();

            Map<String, Object> requestMap = new HashMap<>();
            requestMap.put("Friend_req/" + current_uid + "/" + userId + "/request_type", "sent");
            requestMap.put("Friend_req/" + userId + "/" + current_uid + "/request_type", "received");
            requestMap.put("notifications/" + userId + "/" + newNotificationId, notificationData);

            mRootRef.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError != null) {

                        Toast.makeText(Others_profile.this, "There was some error in sending request", Toast.LENGTH_SHORT).show();

                    } else {

                        mCurrent_State = "req_sent";
              //          mProfileReqBtn.setText("Cancel Friend Request");

                    }
               //     mProfileReqBtn.setEnabled(true);
                }
            });

        }

        // - -------------- CANCEL REQUEST STATE ------------

        if (mCurrent_State.equals("req_sent")) {

            mFriendReqDatabse.child(current_uid).child(userId).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    mFriendReqDatabse.child(userId).child(current_uid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            mCurrent_State = "not_friends";
//                            mProfileReqBtn.setEnabled(true);
//                            mProfileReqBtn.setText("Send Friend Request");
//                            mDeclineReqBtn.setVisibility(View.INVISIBLE);
//                            mDeclineReqBtn.setEnabled(false);
                        }
                    });
                }
            });
        }

        //-----------------REQUEST RECIEVED STATE---------
        if (mCurrent_State.equals("req_recieved")) {
            final String currentDate = DateFormat.getDateTimeInstance().format(new Date());

            Map<String, Object> friendsMap = new HashMap<>();
            friendsMap.put("Friends/" + current_uid + "/" + userId + "/date", currentDate);
            friendsMap.put("Friends/" + userId + "/" + current_uid + "/date", currentDate);


            friendsMap.put("Friend_req/" + current_uid + "/" + userId, null);
            friendsMap.put("Friend_req/" + userId + "/" + current_uid, null);

            mRootRef.updateChildren(friendsMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError == null) {
                    //    mProfileReqBtn.setEnabled(true);
//                        mCurrent_State = "friends";
//                        mProfileReqBtn.setText("Unfriend");
//
//                        mDeclineReqBtn.setVisibility(View.INVISIBLE);
//                        mDeclineReqBtn.setEnabled(false);
                    } else {
                        String error = databaseError.getMessage();

                        Toast.makeText(Others_profile.this, error, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        // ------------ UNFRIENDS ---------
        if (mCurrent_State.equals("friends")) {
          //  mStartChat.setVisibility(View.GONE);
            Map<String, Object> unfriendMap = new HashMap<>();
            unfriendMap.put("Friends/" + current_uid + "/" + userId, null);
            unfriendMap.put("Friends/" + userId + "/" + current_uid, null);

            mRootRef.updateChildren(unfriendMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError == null) {
                        mCurrent_State = "not_friends";
//                        mProfileReqBtn.setText("Send Friend Request");
//
//                        mDeclineReqBtn.setVisibility(View.INVISIBLE);
//                        mDeclineReqBtn.setEnabled(false);
//                    } else {
                        String error = databaseError.getMessage();

                        Toast.makeText(Others_profile.this, error, Toast.LENGTH_SHORT).show();
                    }
                  //  mProfileReqBtn.setEnabled(true);

                }
            });
        }

    }

}
