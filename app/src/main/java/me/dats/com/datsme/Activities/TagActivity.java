package me.dats.com.datsme.Activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
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
import com.mindorks.placeholderview.SwipeDecor;
import com.mindorks.placeholderview.SwipePlaceHolderView;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.swipe.SwipeCancelState;
import com.mindorks.placeholderview.annotations.swipe.SwipeIn;
import com.mindorks.placeholderview.annotations.swipe.SwipeInState;
import com.mindorks.placeholderview.annotations.swipe.SwipeOut;
import com.mindorks.placeholderview.annotations.swipe.SwipeOutState;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.ButterKnife;
import me.dats.com.datsme.R;


public class TagActivity extends AppCompatActivity {

    List<String> list = new ArrayList<>();
    private DatabaseReference mDatabase;
    private DatabaseReference mQuestionDatabase;
    private FirebaseUser mCurrentUser;
    private SwipePlaceHolderView mSwipeView;
    private Context mContext;
    static Map<String, String> answers = new HashMap<>();
    static int i = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag);
        ButterKnife.bind(this);
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mQuestionDatabase = FirebaseDatabase.getInstance().getReference().child("tag");
        mQuestionDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    list.add(dsp.getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid()).child("Tags");

        mSwipeView = findViewById(R.id.swipeView);
        mContext = getApplicationContext();

        mSwipeView.getBuilder()
                .setDisplayViewCount(3)
                .setSwipeDecor(new SwipeDecor()
                        .setPaddingTop(20)
                        .setRelativeScale(0.01f)
                        .setSwipeInMsgLayoutId(R.layout.msg_swipe_in)
                        .setSwipeOutMsgLayoutId(R.layout.msg_swipe_out));


        for (String profile : list) {
            mSwipeView.addView(new QuestionCard(mContext, profile, mSwipeView));
        }


        findViewById(R.id.sendButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("TAG", "onClick: " + answers.size());
                mDatabase.setValue(answers)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    startActivity(new Intent(TagActivity.this, MapsActivity.class));
                                } else {
                                    Log.i("TAG", "onComplete: " + task.getResult().toString());
                                }
                            }
                        });

            }
        });
    }

    @Layout(R.layout.question_card_view)
    public class QuestionCard {
        @com.mindorks.placeholderview.annotations.View(R.id.questionView)
        TextView profileImageView;

        private String mProfile;
        private Context mContext;
        private SwipePlaceHolderView mSwipeView;

        public QuestionCard(Context context, String profile, SwipePlaceHolderView swipeView) {
            mContext = context;
            mProfile = profile;
            mSwipeView = swipeView;
        }

        @Resolve
        private void onResolved() {
            profileImageView.setText(mProfile);
        }

        @SwipeOut
        private void onSwipedOut() {
            Log.d("EVENT", "onSwipedOut");
            answers.put(list.get(i++), "No");

        }

        @SwipeCancelState
        private void onSwipeCancelState() {
            Log.d("EVENT", "onSwipeCancelState");
        }

        @SwipeIn
        private void onSwipeIn() {
            Log.d("EVENT", "onSwipedIn");
            answers.put(list.get(i++), "Yes");
            Log.i("TAG", "onSwipeIn: " + answers.size());
        }

        @SwipeInState
        private void onSwipeInState() {
            Log.d("EVENT", "onSwipeInState");
        }

        @SwipeOutState
        private void onSwipeOutState() {
            Log.d("EVENT", "onSwipeOutState");
        }

    }
}
