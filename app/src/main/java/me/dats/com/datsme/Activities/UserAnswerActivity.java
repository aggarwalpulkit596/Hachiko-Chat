package me.dats.com.datsme.Activities;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.dats.com.datsme.Adapters.ExpendableRecyclerViewAdapter;
import me.dats.com.datsme.Models.UserAnswers;
import me.dats.com.datsme.R;

public class UserAnswerActivity extends AppCompatActivity {


    List<UserAnswers> userAnswers=new ArrayList<>();
    ExpendableRecyclerViewAdapter adapter;
    @BindView(R.id.toolbar_Answers)
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_answer);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        checkRecyclerView();
        DatabaseReference db= FirebaseDatabase.getInstance().getReference().child("Answers")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid().toString());
        db.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                UserAnswers user=dataSnapshot.getValue(UserAnswers.class);
                userAnswers.add(user);
                adapter.notifyDataSetChanged();
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
//        db.addValueEventListener(new ValueEventListener() {
//            @Override
//            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
//
//                for(DataSnapshot dsp:dataSnapshot.getChildren())
//                {
//                    Log.d("TAGrahhul", "onDataChange: "+dsp.getValue());
//                    adapter.notifyDataSetChanged();
//                }
//            }
//
//            @Override
//            public void onCancelled(@NonNull DatabaseError databaseError) {
//
//            }
//        });
    }

    private void checkRecyclerView() {
        final RecyclerView recyclerView = findViewById(R.id.expendableRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));



        final List<UserAnswers> data = new ArrayList<>();

        for (int i = 0; i < 5; i++)
           // data.add(new UserAnswers(120, "LjmmrgMi7hRaaYrga2R6DpPM0BB3", "fjkjh6", false, false, "Do you smoke ?", "public"));

        adapter=new ExpendableRecyclerViewAdapter(userAnswers);
        recyclerView.setAdapter(adapter);
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
