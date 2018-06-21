package me.dats.com.datsme.Fragments;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.BottomSheetDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
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
import de.hdodenhof.circleimageview.CircleImageView;
import me.dats.com.datsme.Activities.ChatActivity;
import me.dats.com.datsme.R;

public class BottomSheetProfileFragment extends BottomSheetDialogFragment {

    private DatabaseReference mUserDatabase, mFriendReqDatabse, mFriendsDatabase;
    private DatabaseReference mRootRef;
    private DatabaseReference mUserRef;
    private FirebaseUser mCurrentUser;


    //Views
    @BindView(R.id.user_image)
    CircleImageView mProfileImage;
    @BindView(R.id.user_displayname)
    TextView mProfileName;
    @BindView(R.id.user_sendrequest)
    Button mProfileReqBtn;
    @BindView(R.id.user_cancelrequest)
    Button mDeclineReqBtn;
    @BindView(R.id.user_startchat)
    Button mStartChat;

    private String mCurrent_State;
    private String current_uid;
    private ProgressDialog mLoadProcess;
    private String user_id;
    private String image;
    private String name;


    public BottomSheetProfileFragment() {
        // Required empty public constructor
    }

    public static BottomSheetProfileFragment newInstance(String user_id) {
        BottomSheetProfileFragment bottomSheetFragment = new BottomSheetProfileFragment();
        Bundle bundle = new Bundle();
        bundle.putString("user_id", user_id);
        bottomSheetFragment.setArguments(bundle);

        return bottomSheetFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.bottom_sheet_profile, container, false);
        user_id = getArguments().getString("user_id");
        ButterKnife.bind(this, root);
        init();
        mDeclineReqBtn.setVisibility(View.INVISIBLE);
        mDeclineReqBtn.setEnabled(false);

        mLoadProcess = new ProgressDialog(getContext());
        mLoadProcess.setTitle("Getting Info");
        mLoadProcess.setMessage("PLease Wait...");
        mLoadProcess.setCanceledOnTouchOutside(false);
        mLoadProcess.show();


        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                bindData(dataSnapshot);

                if (mCurrentUser.getUid().equals(user_id)) {

                    mDeclineReqBtn.setEnabled(false);
                    mDeclineReqBtn.setVisibility(View.INVISIBLE);

                    mProfileReqBtn.setEnabled(false);
                    mProfileReqBtn.setVisibility(View.INVISIBLE);

                }
                //----------FRIEND LIST/REQUEST --------------
                mFriendReqDatabse.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.hasChild(user_id)) {

                            String req_type = dataSnapshot.child(user_id).child("request_type").getValue().toString();

                            if (req_type.equals("received")) {

                                mCurrent_State = "req_recieved";
                                mProfileReqBtn.setText("Accept Friend Request");

                                mDeclineReqBtn.setVisibility(View.VISIBLE);
                                mDeclineReqBtn.setEnabled(true);
                            } else if (req_type.equals("sent")) {
                                Log.i("TAG", "onSuccess:");
                                mCurrent_State = "req_sent";
                                mProfileReqBtn.setText("Cancel Friend Request");
                                mDeclineReqBtn.setVisibility(View.INVISIBLE);
                                mDeclineReqBtn.setEnabled(false);
                            }
                            mLoadProcess.dismiss();

                        } else {

                            mFriendsDatabase.child(mCurrentUser.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.hasChild(user_id)) {

                                        mCurrent_State = "friends";
                                        mProfileReqBtn.setText("Unfriend");
                                        mStartChat.setVisibility(View.VISIBLE);


                                        mDeclineReqBtn.setVisibility(View.INVISIBLE);
                                        mDeclineReqBtn.setEnabled(false);

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

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        return root;
    }

    private void init() {
        //Firebase Instance
        mRootRef = FirebaseDatabase.getInstance().getReference();
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        mFriendReqDatabse = FirebaseDatabase.getInstance().getReference().child("Friend_req");
        mFriendsDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
        mUserRef = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());

        //Constants
        current_uid = mCurrentUser.getUid();
        mCurrent_State = "not friends";
    }

    private void bindData(DataSnapshot documentSnapshot) {

        name = documentSnapshot.child("name").getValue().toString();
        image = documentSnapshot.child("thumb_image").getValue().toString();

        mProfileName.setText(name);

        if (!image.equals("default"))
            Picasso.get()
                    .load(image)
                    .placeholder(R.drawable.default_avatar)
                    .into(mProfileImage);

    }

    @OnClick(R.id.user_sendrequest)
    public void SendFriendRequest() {

        mProfileReqBtn.setEnabled(false);

        //-----------------NOT FRIENDS STATE---------


        if (mCurrent_State.equals("not friends")) {

            Map<String, String> notificationData = new HashMap<>();
            notificationData.put("from", current_uid);
            notificationData.put("type", "request");

            DatabaseReference newNotificationref = mRootRef.child("notifications").child(user_id).push();
            String newNotificationId = newNotificationref.getKey();

            Map<String, Object> requestMap = new HashMap<>();
            requestMap.put("Friend_req/" + current_uid + "/" + user_id + "/request_type", "sent");
            requestMap.put("Friend_req/" + user_id + "/" + current_uid + "/request_type", "received");
            requestMap.put("notifications/" + user_id + "/" + newNotificationId, notificationData);

            mRootRef.updateChildren(requestMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError != null) {

                        Toast.makeText(getActivity(), "There was some error in sending request", Toast.LENGTH_SHORT).show();

                    } else {

                        mCurrent_State = "req_sent";
                        mProfileReqBtn.setText("Cancel Friend Request");

                    }
                    mProfileReqBtn.setEnabled(true);
                }
            });

        }

        // - -------------- CANCEL REQUEST STATE ------------

        if (mCurrent_State.equals("req_sent")) {

            mFriendReqDatabse.child(current_uid).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                    mFriendReqDatabse.child(user_id).child(current_uid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            mCurrent_State = "not_friends";
                            mProfileReqBtn.setEnabled(true);
                            mProfileReqBtn.setText("Send Friend Request");
                            mDeclineReqBtn.setVisibility(View.INVISIBLE);
                            mDeclineReqBtn.setEnabled(false);
                        }
                    });
                }
            });
        }

        //-----------------REQUEST RECIEVED STATE---------
        if (mCurrent_State.equals("req_recieved")) {
            final String currentDate = DateFormat.getDateTimeInstance().format(new Date());

            Map<String, Object> friendsMap = new HashMap<>();
            friendsMap.put("Friends/" + current_uid + "/" + user_id + "/date", currentDate);
            friendsMap.put("Friends/" + user_id + "/" + current_uid + "/date", currentDate);


            friendsMap.put("Friend_req/" + current_uid + "/" + user_id, null);
            friendsMap.put("Friend_req/" + user_id + "/" + current_uid, null);

            mRootRef.updateChildren(friendsMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError == null) {
                        mProfileReqBtn.setEnabled(true);
                        mCurrent_State = "friends";
                        mProfileReqBtn.setText("Unfriend");

                        mDeclineReqBtn.setVisibility(View.INVISIBLE);
                        mDeclineReqBtn.setEnabled(false);
                    } else {
                        String error = databaseError.getMessage();

                        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }

        // ------------ UNFRIENDS ---------
        if (mCurrent_State.equals("friends")) {
            mStartChat.setVisibility(View.GONE);
            Map<String, Object> unfriendMap = new HashMap<>();
            unfriendMap.put("Friends/" + current_uid + "/" + user_id, null);
            unfriendMap.put("Friends/" + user_id + "/" + current_uid, null);

            mRootRef.updateChildren(unfriendMap, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                    if (databaseError == null) {
                        mCurrent_State = "not_friends";
                        mProfileReqBtn.setText("Send Friend Request");

                        mDeclineReqBtn.setVisibility(View.INVISIBLE);
                        mDeclineReqBtn.setEnabled(false);
                    } else {
                        String error = databaseError.getMessage();

                        Toast.makeText(getContext(), error, Toast.LENGTH_SHORT).show();
                    }
                    mProfileReqBtn.setEnabled(true);

                }
            });
        }

    }
    @OnClick(R.id.user_startchat)
    public void startchat(){
        Intent chatintent = new Intent(getContext(), ChatActivity.class);
        chatintent.putExtra("user_id", user_id);
        chatintent.putExtra("name", name);
        chatintent.putExtra("image", image);
        startActivity(chatintent);
    }
}
