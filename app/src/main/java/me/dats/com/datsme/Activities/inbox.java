package me.dats.com.datsme.Activities;

import android.graphics.Movie;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;
import me.dats.com.datsme.Models.Messages;
import me.dats.com.datsme.Models.Users;
import me.dats.com.datsme.R;
import me.dats.com.datsme.Utils.SpacesItemDecoration;

public class inbox extends AppCompatActivity {

    private ArrayList<Messages> inboxList = new ArrayList<>();
    private HashMap<String,Messages> inbox=new HashMap<>();
    Toolbar toolbar;
    RecyclerView recyclerView;

    inboxAdapter inboxAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);


        toolbar=findViewById(R.id.inbox_toolbar);
        recyclerView=findViewById(R.id.inbox_recyclerview);

        setSupportActionBar(toolbar);
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);



        inboxAdapter=new inboxAdapter(inboxList);

        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        int spacingInPixels = 10;
        recyclerView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
        recyclerView.setAdapter(inboxAdapter);

        setInbox();
    }

    private void setInbox() {
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase.getInstance().getReference().child("messages").child(user.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()){

                            final String userId=snapshot.getRef().getKey();
                            Log.d("TAG", "onDataChange: userkey"+snapshot.getRef().getKey());

                            snapshot.getRef().orderByKey().limitToLast(1).addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                                    Messages m;
                                    m=dataSnapshot.getValue(Messages.class);
                                    if(inbox.get(userId)!=null)
                                    {
                                        inboxList.remove(inbox.get(userId));
                                        inbox.put(userId,m);
                                        inboxList.add(m);
                                    }
                                    else{
                                        inbox.put(userId,m);
                                        inboxList.add(m);

                                    }
                                    Log.d("TAG", "onChildAdded: "+dataSnapshot.getRef().getKey());
                                    Log.d("TAG", "onChildAdded: "+dataSnapshot.getValue(Messages.class).getMessage());
                                    Collections.sort(inboxList);//checking
                                    inboxAdapter.notifyDataSetChanged();
                                }

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
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


    }
}
class inboxAdapter extends RecyclerView.Adapter<inboxAdapter.MyViewHolder> {

    private ArrayList<Messages> messages;

    public String otherUserid;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView message,name;
        public CircleImageView imageView;

        public MyViewHolder(View view) {
            super(view);
            message = (TextView) view.findViewById(R.id.user_single_status);
            name = (TextView) view.findViewById(R.id.user_single_name);
            imageView=(CircleImageView) view.findViewById(R.id.user_image);
        }
    }


    public inboxAdapter(ArrayList<Messages> messages) {
        this.messages = messages;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_layout2, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        Messages m=messages.get(position);
        DatabaseReference mref=FirebaseDatabase.getInstance().getReference().child("Users").child(m.getFrom());
        mref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                Users u=dataSnapshot.getValue(Users.class);
                Picasso.get().load(u.getThumb_image()).centerCrop().fit().placeholder(R.drawable.defaultavatar).into(holder.imageView);
                holder.name.setText(u.getName().toUpperCase());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        holder.message.setText(m.getMessage());
    }

    public void getUserId()
    {

    }
    @Override
    public int getItemCount() {
        return messages.size();
    }
}

