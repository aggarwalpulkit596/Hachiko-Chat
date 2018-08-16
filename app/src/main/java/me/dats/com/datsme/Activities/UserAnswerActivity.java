package me.dats.com.datsme.Activities;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.dats.com.datsme.Adapters.ExpendableRecyclerViewAdapter;
import me.dats.com.datsme.Models.UserAnswers;
import me.dats.com.datsme.R;

public class UserAnswerActivity extends AppCompatActivity {


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
    }

    private void checkRecyclerView() {
        final RecyclerView recyclerView = findViewById(R.id.expendableRecyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        final List<UserAnswers> data = new ArrayList<>();
        for (int i = 0; i < 5; i++)
            data.add(new UserAnswers(120, "LjmmrgMi7hRaaYrga2R6DpPM0BB3", "fjkjh6", false, false, "Do you smoke ?", "public"));
        recyclerView.setAdapter(new ExpendableRecyclerViewAdapter(data));
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
