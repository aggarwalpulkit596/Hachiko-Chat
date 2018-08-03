package me.dats.com.datsme.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.dats.com.datsme.Adapters.QuestionAdapter;
import me.dats.com.datsme.R;
import me.dats.com.datsme.Utils.SpacesItemDecoration;

public class Friendsquestions extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.friends_quuestionToolbar)
    Toolbar toolbar;
    @BindView(R.id.my_questions_list)
    RecyclerView recyclerView;
    @BindView(R.id.noQuestions)
    RelativeLayout noQuestions;
    @BindView(R.id.addButton)
    ImageButton addButton;
    QuestionAdapter QuestionAdapter;
    private List<String> myQuestionsList = new ArrayList<>();

    private DatabaseReference mDatabase;
    private FirebaseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friendsquestions);

        ButterKnife.bind(this);

        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        init();
        addButton.setOnClickListener(this);

    }

    @Override
    protected void onStart() {
        super.onStart();
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid()).child("Questions");
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                myQuestionsList.clear();
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    myQuestionsList.add(dsp.getValue().toString());
                }
                QuestionAdapter.notifyDataSetChanged();
                if (myQuestionsList.isEmpty()) {
                    addButton.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.INVISIBLE);
                    noQuestions.setVisibility(View.VISIBLE);
                } else if (myQuestionsList.size() == 5) {
                    addButton.setVisibility(View.INVISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);
                    noQuestions.setVisibility(View.INVISIBLE);
                } else if (myQuestionsList.size() < 5) {
                    addButton.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.VISIBLE);
                    noQuestions.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void init() {
        QuestionAdapter = new QuestionAdapter(myQuestionsList, getApplicationContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        int spacingInPixels = 10;
        recyclerView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
        recyclerView.setAdapter(QuestionAdapter);

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.addButton:
                Intent i = new Intent(Friendsquestions.this, generateQuestions.class);
                startActivity(i);
                break;
        }
    }
}
