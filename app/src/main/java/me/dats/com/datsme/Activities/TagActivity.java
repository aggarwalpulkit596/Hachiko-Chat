package me.dats.com.datsme.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.roughike.swipeselector.SwipeItem;
import com.roughike.swipeselector.SwipeSelector;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.BindViews;
import butterknife.ButterKnife;
import me.dats.com.datsme.R;


public class TagActivity extends AppCompatActivity {

    @BindViews({R.id.worldSelector,R.id.commmunicationselector,R.id.spiritualSelector,R.id.emotionalSelector,R.id.professionSelector})
    SwipeSelector[] swipeSelectors;

    private DatabaseReference mDatabase;
    private FirebaseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag);
        ButterKnife.bind(this);
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());

        swipeSelectors[0].setItems(
                // The first argument is the value for that item, and should in most cases be unique for the
                // current SwipeSelector, just as you would assign values to radio buttons.
                // You can use the value later on to check what the selected item was.
                // The value can be any Object, here we're using ints.
                new SwipeItem(0, "Worldview", "Lets have a meaningful life"),
                new SwipeItem(1, "Worldview", "Lets change the world"),
                new SwipeItem(2, "Worldview", "Lets have a fun life"),
                new SwipeItem(3, "Worldview", "Lets have a beautiful life")

        );
        swipeSelectors[1].setItems(

                new SwipeItem(0, "Communication", "Texting"),
                new SwipeItem(1, "Communication", "Calling"),
                new SwipeItem(2, "Communication", "Face to face conversation")
        );
        swipeSelectors[2].setItems(

                new SwipeItem(0, "Spirituality", "Atheist"),
                new SwipeItem(1, "Spirituality", "Spiritual"),
                new SwipeItem(2, "Spirituality", "Religious")
        );
        swipeSelectors[3].setItems(
                new SwipeItem(0, "Emotional Quotient", "Yes"),
                new SwipeItem(1, "Emotional Quotient", "No"),
                new SwipeItem(2, "Emotional Quotient", "Depends on Situation")
        );
        swipeSelectors[4].setItems(
                new SwipeItem(0, "Professional Attitude", "Procrastinator"),
                new SwipeItem(1, "Professional Attitude", "Easy Going"),
                new SwipeItem(2, "Professional Attitude", "Workaholic")
        );
        findViewById(R.id.sendButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> userMap = new HashMap<>();
                Map<String,String> tags = new HashMap<>();
                userMap.put("Tags", tags);
                tags.put("Worldview", "Lets have a meaningful life");
                tags.put("Communication", "Texting");
                tags.put("Spirituality", "Atheist");
                tags.put("Professional Attitude", "Easy Going");
                mDatabase.updateChildren(userMap)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                startActivity(new Intent(TagActivity.this, MapsActivity.class));
                                }
                            }
                        });

            }
        });
    }
}
