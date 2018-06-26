package me.dats.com.datsme.Activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookSdk;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import me.dats.com.datsme.Models.Users;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.dats.com.datsme.Adapters.AuthAdapter;
import me.dats.com.datsme.R;
import me.dats.com.datsme.Widgets.AnimatedViewPager;

public class LoginActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    private FirebaseAuth mAuth;
    @BindView(R.id.btn_gSignIn)
    SignInButton signInButton;
    private CallbackManager callbackManager;
//    private SignInButton signInButton;
    private static final int RC_SIGN_IN = 9001;
    private DatabaseReference mDatabase;
    public ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        signInButton.setOnClickListener(this);

        //Dialog Setup
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setMessage("Please Wait....");
        dialog.setTitle("Loading");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

    }

    public void gSignIn() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // [END config_signin]

        GoogleApiClient mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("TAG", "gSignIn:" + requestCode);
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Log.i("TAG", "onActivityResult: " + requestCode);
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            Log.i("TAG", "onActivityResult: " + result.getStatus());
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                Log.i("TAG", "onActivityResult: ");
                GoogleSignInAccount account = result.getSignInAccount();
                dialog.show();
                firebaseAuthWithGoogle(account);
            } else {
                // Google Sign In failed, update UI appropriately
                // [START_EXCLUDE]
                Log.i("TAG", "gSignIn: kbsldblds" + "here it is3");
                // [END_EXCLUDE]
            }
        }
    }
    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.i("TAG", "gSignIn: kbsldblds" + "here it is3");
        final AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            final String userId = mAuth.getCurrentUser().getUid();
                           // Users user = new Users();
                           // mDatabase.child("users").child(userId).setValue(user);
                            dialog.hide();
                            Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            //dialog.hide();
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(LoginActivity.this, "Email id Exist.Try using any other method", Toast.LENGTH_SHORT).show();
                            } else
                                Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        Toast.makeText(this, "Google Play Services error.", Toast.LENGTH_SHORT).show();


    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.btn_gSignIn:
                gSignIn();
                break;
        }


        // @Override
//    public void onStart() {
//        super.onStart();
//        // Check if user is signed in (non-null) and update UI accordingly.
//        FirebaseUser currentUser = mAuth.getCurrentUser();
//        if(currentUser!=null && currentUser.isEmailVerified()){
//            startActivity(new Intent(LoginActivity.this, MapsActivity.class));
//        }else if(currentUser!=null && !currentUser.isEmailVerified()){
//            Toast.makeText(this, "Please Verify Your Account First.", Toast.LENGTH_SHORT).show();
//
//        }
//    }
    }
}
