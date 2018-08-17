package me.dats.com.datsme.Activities;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import me.dats.com.datsme.Adapters.OtherProfileQuestionsListAdapter;
import me.dats.com.datsme.Adapters.ShowQuestionsDialogeAdapter;
import me.dats.com.datsme.Models.Users;
import me.dats.com.datsme.R;

public class Others_profile extends AppCompatActivity implements View.OnClickListener {
    @BindView(R.id.Other_image)
    ImageView image;
    @BindView(R.id.Other_name)
    TextView name;
    @BindView(R.id.Other_compatibility)
    TextView compatibility;
    @BindView(R.id.rvOtherProfile_userQuestion)
    RecyclerView rv_userQuestions;


    RecyclerView friendQuestionsList;
    String user_id, userName;

    @BindViews({R.id.Other_firstButton, R.id.Other_secondButton})
    RelativeLayout[] button;

    @BindViews({R.id.Other_firstText, R.id.Other_secondText})
    TextView[] textView;

    @BindViews({R.id.Other_firstIcon, R.id.Other_secondIcon})
    ImageView[] imageView;

    @BindView(R.id.profile_shown)
    LinearLayout profileshown;
    @BindView(R.id.profile_hidden)
    LinearLayout profilehidden;

    @BindView(R.id.toolbar_myprofile)
    Toolbar toolbar;
    @BindView(R.id.unfriend)
    Button unfriend;
    @BindView(R.id.chat)
    Button chat;
    private HashMap<String, String> culist = new HashMap<>();
    private HashMap<String, String> oulist = new HashMap<>();
    float count = 0;
    AlertDialog alertDialog;

    private DatabaseReference mOtherUserDatabase, mFriendReqDatabse, mFriendsDatabase, mCurrentUserDatabase, mBlockDatabase;
    private FirebaseUser mCurrentUser;
    private String mCurrent_State;
    private String current_uid;
    private ProgressDialog mLoadProcess;
    private DatabaseReference mRootRef;
    private DatabaseReference mUserDatabase;
    private ShowQuestionsDialogeAdapter adapter;
    private ArrayList<String> myquestions = new ArrayList<>();
    private Menu menu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_others_profile);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);

        user_id = getIntent().getStringExtra("from_user_id");
        userName = getIntent().getStringExtra("userName");

        init();
        bindData();

        chat.setOnClickListener(this);
        unfriend.setOnClickListener(this);
        button[0].setOnClickListener(this);
        button[1].setOnClickListener(this);

        setOtherQuestionsBlock();
    }

    private void setOtherQuestionsBlock() {

        final Map<String, String> mykeyquestionpair = new HashMap<>();

        final OtherProfileQuestionsListAdapter adapterquestionview = new OtherProfileQuestionsListAdapter(mykeyquestionpair);
        rv_userQuestions.setLayoutManager(new LinearLayoutManager(this));
        rv_userQuestions.setAdapter(adapterquestionview);

        DatabaseReference dbref = FirebaseDatabase.getInstance().getReference().child("Answers").child(mOtherUserDatabase.getKey()).child("MyQuestionskey");
        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                        mykeyquestionpair.put(dsp.getKey().toString(), dsp.getValue().toString());
                    }
                    adapterquestionview.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


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

                    if (req_type.equals("received")) {//if request recieved

                        mCurrent_State = "req_recieved";

                        textView[0].setText("Accept Request");
                        imageView[0].setImageDrawable(getResources().getDrawable(R.drawable.ic_person_add_black_24dp));

                        profileshown.setVisibility(View.GONE);
                        profilehidden.setVisibility(View.VISIBLE);
                        setvisibility(1, true);
                        setvisibility(0, true);

                    } else if (req_type.equals("sent")) {//if request sent
                        mCurrent_State = "req_sent";

                        textView[0].setText("Cancel Request");
                        imageView[0].setImageDrawable(getResources().getDrawable(R.drawable.cancel_request));

                        profileshown.setVisibility(View.GONE);
                        profilehidden.setVisibility(View.VISIBLE);
                        setvisibility(1, false);
                        setvisibility(0, true);
                    }
                    mLoadProcess.dismiss();

                } else {
                    mFriendsDatabase.child(mCurrentUser.getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            if (dataSnapshot.hasChild(user_id)) { //if already friends

                                mCurrent_State = "friends";

                                button[0].setOnClickListener(null);

                                textView[0].setText("Friends");
                                imageView[0].setImageDrawable(getResources().getDrawable(R.drawable.friends));
                                setvisibility(1, false);


                                profileshown.setVisibility(View.VISIBLE);
                                profilehidden.setVisibility(View.GONE);

                            } else {//if no friend
                                mCurrent_State = "not friends";

                                textView[0].setText("Add Friend");
                                imageView[0].setImageDrawable(getResources().getDrawable(R.drawable.ic_person_add_black_24dp));
                                setvisibility(1, false);
                                setvisibility(0, true);

                                profileshown.setVisibility(View.GONE);
                                profilehidden.setVisibility(View.VISIBLE);
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
                mLoadProcess.dismiss();
            }
        });


    }

    private void init() {
        //Constants
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        current_uid = mCurrentUser.getUid();
        mCurrent_State = "not friends";

        //Firebase Instance
        mRootRef = FirebaseDatabase.getInstance().getReference();

        mOtherUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
        mCurrentUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(current_uid).child("Tags");
        mFriendReqDatabse = FirebaseDatabase.getInstance().getReference().child("Friend_req");
        mFriendsDatabase = FirebaseDatabase.getInstance().getReference().child("Friends");
        mUserDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());
        mBlockDatabase = FirebaseDatabase.getInstance().getReference().child("Blocklist");


        mLoadProcess = new ProgressDialog(this);
        mLoadProcess.setTitle("Getting Info");
        mLoadProcess.setMessage("PLease Wait...");
        mLoadProcess.setCanceledOnTouchOutside(false);
        mLoadProcess.show();
        findCompatibility();

        setAlertDailogeforQuestions();
    }

    private void setAlertDailogeforQuestions() {

        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View promptsView = inflater.inflate(R.layout.friendquestionsdialog, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Others_profile.this);
        alertDialogBuilder.setCancelable(true);
        alertDialogBuilder.setView(promptsView);
        alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(true);

        friendQuestionsList = promptsView.findViewById(R.id.friendQuestionlist_otherprofile);
        friendQuestionsList.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        adapter = new ShowQuestionsDialogeAdapter(myquestions, this, alertDialog, user_id, current_uid);
        friendQuestionsList.setAdapter(adapter);
        setQuestions();
    }

    private void setQuestions() {
        DatabaseReference dbref = mOtherUserDatabase.child("Questions");
        dbref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    myquestions.add(dsp.getValue().toString());
                }
                Log.d("myquestions", "onDataChange: " + dataSnapshot);
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void findCompatibility() {
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

    public void setvisibility(int i, boolean t) {
        if (!t) {
            button[i].setVisibility(View.INVISIBLE);
            button[i].setOnClickListener(null);
        } else {
            button[i].setVisibility(View.VISIBLE);
            button[i].setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.unfriend:
                unfriend.setEnabled(false);
                if (mCurrent_State.equals("friends")) {
                    final Map<String, Object> unfriendMap = new HashMap<>();
                    unfriendMap.put("Friends/" + current_uid + "/" + user_id, null);
                    unfriendMap.put("Friends/" + user_id + "/" + current_uid, null);

                    mRootRef.updateChildren(unfriendMap, new DatabaseReference.CompletionListener() {
                        @Override
                        public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                            if (databaseError == null) {
                                mCurrent_State = "not friends";
                                unfriend.setEnabled(true);
                                profileshown.setVisibility(View.GONE);
                                profilehidden.setVisibility(View.VISIBLE);
                                setvisibility(0, true);
                                setvisibility(1, false);
                                textView[0].setText("Add Friend");
                                imageView[0].setImageDrawable(getResources().getDrawable(R.drawable.ic_person_add_black_24dp));
                            } else {
                                String error = databaseError.getMessage();
                                Toast.makeText(Others_profile.this, error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                break;

            case R.id.chat:
                Intent i = new Intent(Others_profile.this, ChatActivity.class);
                i.putExtra("from_user_id", user_id);
                i.putExtra("userName", userName);
                startActivity(i);
                break;
            case R.id.Other_firstButton:
                button[0].setOnClickListener(null);
                mLoadProcess.show();

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

                                Toast.makeText(Others_profile.this, "There was some error in sending request", Toast.LENGTH_SHORT).show();

                            } else {//if request sent

                                mCurrent_State = "req_sent";
                                Toast.makeText(Others_profile.this, "Request sent", Toast.LENGTH_SHORT).show();
                                textView[0].setText("Cancel Request");
                                imageView[0].setImageDrawable(getResources().getDrawable(R.drawable.cancel_request));
                                setvisibility(1, false);
                                setvisibility(0, true);
                                mLoadProcess.dismiss();

                            }
                        }
                    });

                }

                // - -------------- CANCEL REQUEST STATE ------------

                else if (mCurrent_State.equals("req_sent")) {

                    mFriendReqDatabse.child(current_uid).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            mFriendReqDatabse.child(user_id).child(current_uid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    mCurrent_State = "not friends";

                                    textView[0].setText("Add Friend");
                                    imageView[0].setImageDrawable(getResources().getDrawable(R.drawable.ic_person_add_black_24dp));
                                    setvisibility(1, false);
                                    setvisibility(0, true);
                                    mLoadProcess.dismiss();
                                }
                            });
                        }
                    });
                }

                //-----------------REQUEST RECIEVED STATE---------

                else if (mCurrent_State.equals("req_recieved")) {
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
                                mCurrent_State = "friends";
                                textView[0].setText("Friends");
                                imageView[0].setImageDrawable(getResources().getDrawable(R.drawable.friends));
                                setvisibility(1, false);
                                setvisibility(0, true);
                                mLoadProcess.dismiss();

                            } else {
                                String error = databaseError.getMessage();
                                Toast.makeText(Others_profile.this, error, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
                break;
            case R.id.Other_secondButton:
                button[1].setOnClickListener(null);

                if (mCurrent_State.equals("req_recieved")) {

                    mFriendReqDatabse.child(current_uid).child(user_id).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {

                            mFriendReqDatabse.child(user_id).child(current_uid).removeValue().addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {

                                    mCurrent_State = "not friends";
                                    textView[0].setText("Add friend");
                                    imageView[0].setImageDrawable(getResources().getDrawable(R.drawable.ic_person_add_black_24dp));
                                    setvisibility(1, false);
                                    setvisibility(0, true);
                                    mLoadProcess.dismiss();
                                }
                            });
                        }
                    });
                }

                break;
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.report:
                break;
            case R.id.block:
                blockUser();
                break;
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void blockUser() {
        final String currentDate = DateFormat.getDateTimeInstance().format(new Date());
        Map<String, Object> friendsMap = new HashMap<>();
        friendsMap.put("Blocklist/" + current_uid + "/" + user_id + "/blocked", currentDate);
        friendsMap.put("Blocklist/" + user_id + "/" + current_uid + "/blockedby", currentDate);
        if (mCurrent_State.equals("req_recieved")) {
            friendsMap.put("Friend_req/" + current_uid + "/" + user_id, null);
            friendsMap.put("Friend_req/" + user_id + "/" + current_uid, null);
        }
        if (mCurrent_State.equals("friends")) {
            friendsMap.put("Friends/" + current_uid + "/" + user_id, null);
            friendsMap.put("Friends/" + user_id + "/" + current_uid, null);
        }

        mRootRef.updateChildren(friendsMap, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError == null) {
                    finish();
                } else {
                    String error = databaseError.getMessage();
                    Toast.makeText(Others_profile.this, error, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menuotherprofile, menu);
        this.menu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    public void openbox(View view) {
        mOtherUserDatabase.child("Questions").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    alertDialog.show();
                } else {
                    Toast.makeText(Others_profile.this, "no questions", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


}


