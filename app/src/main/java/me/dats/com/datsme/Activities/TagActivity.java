package me.dats.com.datsme.Activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
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
import com.mindorks.placeholderview.SwipeDirection;
import com.mindorks.placeholderview.SwipeDirectionalView;
import com.mindorks.placeholderview.SwipePlaceHolderView;
import com.mindorks.placeholderview.annotations.Layout;
import com.mindorks.placeholderview.annotations.Resolve;
import com.mindorks.placeholderview.annotations.swipe.SwipeCancelState;
import com.mindorks.placeholderview.annotations.swipe.SwipeIn;
import com.mindorks.placeholderview.annotations.swipe.SwipeInDirectional;
import com.mindorks.placeholderview.annotations.swipe.SwipeInState;
import com.mindorks.placeholderview.annotations.swipe.SwipeOut;
import com.mindorks.placeholderview.annotations.swipe.SwipeOutDirectional;
import com.mindorks.placeholderview.annotations.swipe.SwipeOutState;
import com.mindorks.placeholderview.annotations.swipe.SwipeTouch;
import com.mindorks.placeholderview.annotations.swipe.SwipingDirection;

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
    private SwipeDirectionalView mSwipeView;
    private Context mContext;
    static Map<String, String> answers = new HashMap<>();
    static int i = 0;
    private ProgressDialog loadingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag);
        ButterKnife.bind(this);
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mQuestionDatabase = FirebaseDatabase.getInstance().getReference().child("tag");
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid()).child("Tags");

        mSwipeView = findViewById(R.id.swipeView);
        mContext = getApplicationContext();
        loadingBar = new ProgressDialog(this);
        loadingBar.setTitle("Loading");
        loadingBar.setCancelable(false);
        loadingBar.setMessage("Please wait, while your question are coming ");
        loadingBar.show();
        mSwipeView.getBuilder()
                .setDisplayViewCount(3)
                .setSwipeDecor(new SwipeDecor()
                        .setPaddingTop(0)
                        .setRelativeScale(0.01f));
//                        .setSwipeInMsgLayoutId(R.layout.msg_swipe_in)
//                        .setSwipeOutMsgLayoutId(R.layout.msg_swipe_out));

        mQuestionDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                loadingBar.dismiss();
                for (DataSnapshot dsp : dataSnapshot.getChildren()) {
                    list.add(dsp.getValue().toString());
                    mSwipeView.addView(new QuestionCard(mContext, dsp.getValue().toString(), mSwipeView));

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

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
        @com.mindorks.placeholderview.annotations.View(R.id.neutralTextView)
        TextView neutral;
        @com.mindorks.placeholderview.annotations.View(R.id.yesTextView)
        TextView yes;
        @com.mindorks.placeholderview.annotations.View(R.id.noTextView)
        TextView no;

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

        @SwipeOutDirectional
        public void onSwipeOutDirectional(SwipeDirection direction) {
            Log.d("DEBUG", "SwipeOutDirectional " + direction.name());
            if (direction.getDirection() == SwipeDirection.TOP.getDirection()) {


//                mCallback.onSwipeUp();
            }
        }

        @SwipeCancelState
        public void onSwipeCancelState() {
            Log.d("DEBUG", "onSwipeCancelState");
            no.setVisibility(View.INVISIBLE);
            yes.setVisibility(View.INVISIBLE);
            neutral.setVisibility(View.INVISIBLE);
            mSwipeView.setAlpha(1);

        }

        @SwipeInDirectional
        public void onSwipeInDirectional(SwipeDirection direction) {
//            Log.d("DEBUG", "SwipeInDirectional " + direction.name());
        }

        @SwipingDirection
        public void onSwipingDirection(SwipeDirection direction) {
            Log.d("DEBUG", "SwipingDirection " + direction.name());
            if(direction.getDirection()==SwipeDirection.RIGHT.getDirection() ||
                    direction.getDirection()==SwipeDirection.RIGHT_TOP.getDirection()||
                    direction.getDirection()==SwipeDirection.RIGHT_BOTTOM.getDirection()
                    )
            {
                no.setVisibility(View.INVISIBLE);
                yes.setVisibility(View.VISIBLE);
                neutral.setVisibility(View.INVISIBLE);
            }
            else if(direction.getDirection()==SwipeDirection.LEFT.getDirection() ||
                    direction.getDirection()==SwipeDirection.LEFT_TOP.getDirection()||
                    direction.getDirection()==SwipeDirection.LEFT_BOTTOM.getDirection()){
                no.setVisibility(View.VISIBLE);
                yes.setVisibility(View.INVISIBLE);
                neutral.setVisibility(View.INVISIBLE);
            }
            else if(direction.getDirection()==SwipeDirection.BOTTOM.getDirection()
                    ||direction.getDirection()==SwipeDirection.TOP.getDirection())
            {
                no.setVisibility(View.INVISIBLE);
                yes.setVisibility(View.INVISIBLE);
                neutral.setVisibility(View.VISIBLE);
            }
            else{
                no.setVisibility(View.INVISIBLE);
                yes.setVisibility(View.INVISIBLE);
                neutral.setVisibility(View.INVISIBLE);
            }
        }

        @SwipeTouch
        public void onSwipeTouch(float xStart, float yStart, float xCurrent, float yCurrent) {
//
//            float cardHolderDiagonalLength =
//                    (float) Math.sqrt(Math.pow(mCardViewHolderSize.x, 2) + (Math.pow(mCardViewHolderSize.y, 2)));
//            float distance = (float) Math.sqrt(Math.pow(xCurrent - xStart, 2) + (Math.pow(yCurrent - yStart, 2)));
//
//            float alpha = 1 - distance / cardHolderDiagonalLength;

//            Log.d("DEBUG", "onSwipeTouch "
//                    + " xStart : " + xStart
//                    + " yStart : " + yStart
//                    + " xCurrent : " + xCurrent
//                    + " yCurrent : " + yCurrent
////                    + " distance : " + distance
////                    + " TotalLength : " + cardHolderDiagonalLength
////                    + " alpha : " + alpha
//            );

//            ((FrameLayout)mSwipeView).setAlpha(alpha);
        }


    }
}