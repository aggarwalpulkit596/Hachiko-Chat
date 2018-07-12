package me.dats.com.datsme.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import me.dats.com.datsme.MyPreference;
import me.dats.com.datsme.R;

public class CompleteProfileActivity extends AppCompatActivity {

    @BindView(R.id.completeprofile_root)
    RelativeLayout rootlayout;
    @BindView(R.id.user_college)
    EditText userCollege;
    @BindView(R.id.user_place)
    EditText userPlace;
    @BindView(R.id.user_aboutyou)
    EditText userAbout;

    private DatabaseReference mDatabase;
    private FirebaseUser mCurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complete_profile);
        ButterKnife.bind(this);
        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());
    }

    public void addToDatBase(View view) {
        if (userAbout.getText().toString().isEmpty() || userCollege.getText().toString().isEmpty() ||userPlace.getText().toString().isEmpty()) {
            Snackbar snackBar;
            if (userCollege.getText().toString().isEmpty()) {
                snackBar = Snackbar.make(rootlayout
                        , "College Cannot Be Empty", Snackbar.LENGTH_SHORT);
                snackBar.show();
            } else if (userPlace.getText().toString().isEmpty()) {
                snackBar = Snackbar.make(rootlayout
                        , "Place Cannot Be Empty", Snackbar.LENGTH_SHORT);
                snackBar.show();
            } else if (userAbout.getText().toString().isEmpty()) {
                snackBar = Snackbar.make(rootlayout
                        , "Write Something About yourself", Snackbar.LENGTH_SHORT);
                snackBar.show();
            }
        } else {

            Datsme.getPreferenceManager().putString(MyPreference.COMPPRO,"true");
            Map<String, Object> userMap = new HashMap<>();
            userMap.put("about", userAbout.getText().toString());
            userMap.put("place", userPlace.getText().toString());
            userMap.put("college", userCollege.getText().toString());
            mDatabase.updateChildren(userMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                startActivity(new Intent(CompleteProfileActivity.this, TagActivity.class));
                                finish();
                            }
                        }
                    });
        }
    }
}
