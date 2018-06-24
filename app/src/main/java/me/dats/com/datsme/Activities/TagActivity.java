package me.dats.com.datsme.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.roughike.swipeselector.SwipeItem;
import com.roughike.swipeselector.SwipeSelector;

import me.dats.com.datsme.R;


public class TagActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag);

        final SwipeSelector swipeSelector = findViewById(R.id.worldSelector);
        swipeSelector.setItems(
                // The first argument is the value for that item, and should in most cases be unique for the
                // current SwipeSelector, just as you would assign values to radio buttons.
                // You can use the value later on to check what the selected item was.
                // The value can be any Object, here we're using ints.
                new SwipeItem(0, "Worldview", "Lets have a meaningful life"),
                new SwipeItem(1, "Worldview", "Lets change the world"),
                new SwipeItem(2, "Worldview", "Lets have a fun life"),
                new SwipeItem(3, "Worldview", "Lets have a beautiful life")

        );
        SwipeSelector swipeSelector2 = findViewById(R.id.commmunicationselector);
        swipeSelector2.setItems(
                // The first argument is the value for that item, and should in most cases be unique for the
                // current SwipeSelector, just as you would assign values to radio buttons.
                // You can use the value later on to check what the selected item was.
                // The value can be any Object, here we're using ints.
                new SwipeItem(0, "Communication", "Texting"),
                new SwipeItem(1, "Communication", "Calling"),
                new SwipeItem(2, "Communication", "Face to face conversation")
        );
        SwipeSelector swipeSelector3 = findViewById(R.id.spiritualSelector);
        swipeSelector3.setItems(
                // The first argument is the value for that item, and should in most cases be unique for the
                // current SwipeSelector, just as you would assign values to radio buttons.
                // You can use the value later on to check what the selected item was.
                // The value can be any Object, here we're using ints.
                new SwipeItem(0, "Spirituality", "Atheist"),
                new SwipeItem(1, "Spirituality", "Spiritual"),
                new SwipeItem(2, "Spirituality", "Religious")
        );
        SwipeSelector swipeSelector4= findViewById(R.id.emotionalSelector);
        swipeSelector4.setItems(
                // The first argument is the value for that item, and should in most cases be unique for the
                // current SwipeSelector, just as you would assign values to radio buttons.
                // You can use the value later on to check what the selected item was.
                // The value can be any Object, here we're using ints.
                new SwipeItem(0, "Emotional Quotient", "Yes"),
                new SwipeItem(1, "Emotional Quotient", "No"),
                new SwipeItem(2, "Emotional Quotient", "Depends on Situation")
        );
        SwipeSelector swipeSelector5= findViewById(R.id.professionSelector);
        swipeSelector5.setItems(
                // The first argument is the value for that item, and should in most cases be unique for the
                // current SwipeSelector, just as you would assign values to radio buttons.
                // You can use the value later on to check what the selected item was.
                // The value can be any Object, here we're using ints.
                new SwipeItem(0, "Professional Attitude", "Procrastinator"),
                new SwipeItem(1, "Professional Attitude", "Easy Going"),
                new SwipeItem(2, "Professional Attitude", "Workaholic")
        );
        findViewById(R.id.sendButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TagActivity.this, MapsActivity.class));

            }
        });
    }
}
