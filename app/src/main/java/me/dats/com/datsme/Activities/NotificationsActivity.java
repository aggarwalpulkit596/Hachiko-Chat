package me.dats.com.datsme.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import me.dats.com.datsme.Models.Users;
import me.dats.com.datsme.Models.notifications;
import me.dats.com.datsme.R;

public class NotificationsActivity extends AppCompatActivity {

    FirebaseAuth mAuth;
    @BindView(R.id.toolbar_notifications)
    Toolbar toolbar;
    FirebaseUser mUser;
    @BindView(R.id.notificationlist)
    RecyclerView mNotificationlist;
    @BindView(R.id.rootlayout_notifications)
    RelativeLayout rootlayout;



    private FirebaseRecyclerAdapter firebaseRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notifications);

        ButterKnife.bind(this);


        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);

        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("notifications").child(mUser.getUid().toString());
        Query query=mRef;
        mNotificationlist.setHasFixedSize(true);
        final FirebaseRecyclerOptions<notifications> options =
                new FirebaseRecyclerOptions.Builder<notifications>()
                        .setQuery(query, notifications.class)
                        .build();

        firebaseRecyclerAdapter = new FirebaseRecyclerAdapter<notifications, NotificationsViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull final NotificationsViewHolder holder, int position, @NonNull final notifications model) {
                Log.d("TAG", "onDataChange: " + model.getType());

                DatabaseReference user = FirebaseDatabase.getInstance().getReference().child("Users").child(model.getFrom().toString());
                user.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        Users otherUser = dataSnapshot.getValue(Users.class);
                        holder.bind(otherUser);
                        holder.setNotification(model);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent i = new Intent(NotificationsActivity.this, Others_profile.class);
                        i.putExtra("from_user_id", holder.userId);
                        i.putExtra("userName", holder.other_user.getName());
                        startActivity(i);
                    }
                });

            }

            @NonNull
            @Override
            public NotificationsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
                Log.i("TAG", "onCreateViewHolder: " + firebaseRecyclerAdapter.getItemCount());
                return new NotificationsViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.notification, parent, false));
            }
        };

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        mNotificationlist.setLayoutManager(linearLayoutManager);
        mNotificationlist.setAdapter(firebaseRecyclerAdapter);
        firebaseRecyclerAdapter.startListening();
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static class NotificationsViewHolder extends RecyclerView.ViewHolder {

        View mView;
        String userId;
        Users other_user;
        TextView textnotification;
        String name;
        CircleImageView image;

        NotificationsViewHolder(View itemView) {
            super(itemView);
            mView = itemView;
        }

        public void bind(Users OtherUser) {
            other_user = OtherUser;
            textnotification = mView.findViewById(R.id.text_notification);
            image = mView.findViewById(R.id.image_notification);
            Picasso.get()
                    .load(OtherUser.getThumb_image())
                    .placeholder(R.drawable.default_avatar)
                    .centerCrop()
                    .fit()
                    .into(image);
            name = OtherUser.getName().toString().toUpperCase();

        }

        public void setNotification(notifications model) {
            userId = model.getFrom().toString();
            String type = model.getType();
            if (type.equals("request")) {
                textnotification.setText(name + " has sent you a Friend Request.");
            }
        }
    }
}
