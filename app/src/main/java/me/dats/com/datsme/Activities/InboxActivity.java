package me.dats.com.datsme.Activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

import me.dats.com.datsme.Adapters.InboxAdapter;
import me.dats.com.datsme.Models.Messages;
import me.dats.com.datsme.R;
import me.dats.com.datsme.Utils.SpacesItemDecoration;

public class InboxActivity extends AppCompatActivity {

    Toolbar toolbar;
    RecyclerView recyclerView;
    Messages messages;
    InboxAdapter inboxAdapter;
    private ArrayList<Messages> inboxList = new ArrayList<>();
    private HashMap<String, Messages> inbox = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inbox);


        toolbar = findViewById(R.id.inbox_toolbar);
        recyclerView = findViewById(R.id.inbox_recyclerview);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);


        inboxAdapter = new InboxAdapter(inboxList,InboxActivity.this);

        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        int spacingInPixels = 10;
        recyclerView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
        recyclerView.setAdapter(inboxAdapter);

        setInbox();
    }

    private void setInbox() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase.getInstance().getReference().child("messages").child(user.getUid())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot snapshot : dataSnapshot.getChildren()) {

                            final String userId = snapshot.getRef().getKey();
                            Log.d("TAG", "onDataChange: userkey" + snapshot.getRef().getKey());

                            snapshot.getRef().orderByKey().limitToLast(1).addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                                    messages = dataSnapshot.getValue(Messages.class);
                                    messages.fuid = userId;
                                    if (inbox.get(userId) != null) {
                                        inboxList.remove(inbox.get(userId));
                                        inbox.put(userId, messages);
                                        inboxList.add(messages);
                                    } else {
                                        inbox.put(userId, messages);
                                        inboxList.add(messages);

                                    }
//                                    inboxList.sort((e1, e2) -> Long.compare(e1.getTime(), e2.getTime()));
                                    Collections.sort(inboxList, new Comparator<Messages>() {
                                        @Override
                                        public int compare(Messages o1, Messages o2) {
                                            return Long.compare(o2.getTime(), o1.getTime());
                                        }
                                    });
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

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}


