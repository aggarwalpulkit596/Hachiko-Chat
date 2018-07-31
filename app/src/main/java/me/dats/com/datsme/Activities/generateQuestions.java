package me.dats.com.datsme.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import me.dats.com.datsme.Adapters.QuestionAdapter;
import butterknife.BindView;
import butterknife.ButterKnife;
import me.dats.com.datsme.R;
import me.dats.com.datsme.Utils.SpacesItemDecoration;

public class generateQuestions extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.generate_questionToolbar)
    Toolbar toolbar;
    @BindView(R.id.my_questions_list)
    RecyclerView recyclerView;
    @BindView(R.id.customQuestiontitle)
    TextView customQuestionTitle;
    @BindView(R.id.saveCustomQuestion)
    Button saveCustomQuestion;
    @BindView(R.id.CustomQuestionText)
    EditText customQuestionText;
    @BindView(R.id.customQuestionlayout)
    RelativeLayout customQuestionLayout;
    Boolean customQuestionLayoutIsVisible;
    private List<String> questionsList = new ArrayList<>();
    private List<String> saveList = new ArrayList<>();

    QuestionAdapter QuestionAdapter;
    DatabaseReference quesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_questions);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);

        init();
        questionsList.add("Do you smoke ?");
        questionsList.add("Do you like waking up early or staying up late? ");
        questionsList.add("Which is better: asking for permission or asking for forgiveness?");
        questionsList.add("You only get 3 words to describe yourself – what are they?");
        questionsList.add("Which is better to listen to – your heart or your brain?");

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        quesRef = FirebaseDatabase.getInstance().getReference().child("Users").child(user.getUid()).child("Questions");
        quesRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    saveList.add(dsp.getValue().toString());
                }
                questionsList.removeAll(saveList);
                QuestionAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void init() {
        QuestionAdapter = new QuestionAdapter(questionsList, saveList, getApplicationContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        int spacingInPixels = 10;
        recyclerView.addItemDecoration(new SpacesItemDecoration(spacingInPixels));
        recyclerView.setAdapter(QuestionAdapter);


        customQuestionTitle.setOnClickListener(this);
        saveCustomQuestion.setOnClickListener(this);
        customQuestionLayoutIsVisible=false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.generate_menu, menu);
        return super.onCreateOptionsMenu(menu);

    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
            case R.id.save:
                saveItems();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void saveItems() {
        List<String> list = QuestionAdapter.getSaveList();
        Map<String, Object> userMap = new HashMap<>();
        for (int i = 0; i < list.size(); i++) {
            userMap.put("" + i, list.get(i));
        }
        quesRef.updateChildren(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.d("TAG", "onComplete: " + task.isSuccessful());
                onBackPressed();
            }
        });

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.saveCustomQuestion:
                String question=customQuestionText.getText().toString().trim();
                if(question.length()>0){
                    if(saveList.size()<5)
                    {

                    }
                    else{

                    }
                }
                break;
            case R.id.customQuestiontitle:
                    if(customQuestionLayoutIsVisible)
                    {
                        customQuestionLayout.setVisibility(View.GONE);
                    }
                    else{
                        customQuestionLayout.setVisibility(View.VISIBLE);
                    }
                break;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
