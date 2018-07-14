package me.dats.com.datsme.Activities;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import me.dats.com.datsme.R;

public class LoginActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener, View.OnClickListener {

    //    private SignInButton signInButton;
    private static final int RC_SIGN_IN = 9001;
    public ProgressDialog dialog;
    Dialog dialog1;
    @BindView(R.id.btn_gSignIn)
    SignInButton signInButton;
    @BindView(R.id.btn_fbSignIn)
    LoginButton fbSignInBtn;
    @BindView(R.id.btn_verify)
    Button phoneSignIn;
    @BindView(R.id.edittext_phoneno)
    EditText mPhoneNumberField;
    EditText otp1, otp2, otp3, otp4, otp5, otp6;
    TextView timer;
    TextView resend;
    Button cancel, otp_submit;
    ProgressBar otp_progressbar;
    CountDownTimer cdt;
    private FirebaseAuth mAuth;
    private CallbackManager mCallbackManager;
    //For PhoneAuth
    private PhoneAuthProvider.ForceResendingToken mResendToken;
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;
    private boolean mVerificationInProgress = false;
    private String mVerificationId;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        signInButton.setOnClickListener(this);
        phoneSignIn.setOnClickListener(this);
        //Dialog Setup
        dialog = new ProgressDialog(this);
        dialog.setCancelable(false);
        dialog.setMessage("Please Wait....");
        dialog.setTitle("Loading");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        //PhoneAuth Reciever
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

            @Override
            public void onVerificationCompleted(PhoneAuthCredential credential) {
                // This callback will be invoked in two situations:
                // 1 - Instant verification. In some cases the phone number can be instantly
                //     verified without needing to send or enter a verification code.
                // 2 - Auto-retrieval. On some devices Google Play services can automatically
                //     detect the incoming verification SMS and perform verification without
                //     user action.
                Log.d("TAG", "onVerificationCompleted:" + credential);
                // [START_EXCLUDE silent]
                mVerificationInProgress = false;
                // [END_EXCLUDE]

                // [START_EXCLUDE silent]
                // Update the UI and attempt sign in with the phone credential
//                updateUI(STATE_VERIFY_SUCCESS, credential);
                // [END_EXCLUDE]
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(FirebaseException e) {
                // This callback is invoked in an invalid request for verification is made,
                // for instance if the the phone number format is not valid.
                Log.w("TAG", "onVerificationFailed", e);
                // [START_EXCLUDE silent]
                mVerificationInProgress = false;
                // [END_EXCLUDE]

                if (e instanceof FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    // [START_EXCLUDE]
                    mPhoneNumberField.setError("Invalid phone number.");
                    // [END_EXCLUDE]
                } else if (e instanceof FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    // [START_EXCLUDE]
                    Snackbar.make(findViewById(android.R.id.content), "Quota exceeded.",
                            Snackbar.LENGTH_SHORT).show();
                    // [END_EXCLUDE]
                }

                // Show a message and update the UI
                // [START_EXCLUDE]
//                updateUI(STATE_VERIFY_FAILED);
                // [END_EXCLUDE]
            }

            @Override
            public void onCodeSent(String verificationId,
                                   PhoneAuthProvider.ForceResendingToken token) {
                // The SMS verification code has been sent to the provided phone number, we
                // now need to ask the user to enter the code and then construct a credential
                // by combining the code with a verification ID.
                Log.d("TAG", "onCodeSent:" + verificationId);

                // Save verification ID and resending token so we can use them later
                mVerificationId = verificationId;
                mResendToken = token;

                // [START_EXCLUDE]
                // Update UI
//                updateUI(STATE_CODE_SENT);
                // [END_EXCLUDE]
            }
        };
        // [END phone_auth_callbacks]

        //Facebook Intialization
        mCallbackManager = CallbackManager.Factory.create();
        fbSignInBtn.setReadPermissions("email", "public_profile");
        fbSignInBtn.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("TAG", "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d("TAG", "facebook:onCancel");
                // [START_EXCLUDE]
//                updateUI(null);
                // [END_EXCLUDE]
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("TAG", "facebook:onError", error);
                // [START_EXCLUDE]
//                updateUI(null);
                // [END_EXCLUDE]
            }
        });


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

    // [START sign_in_with_phone]
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithCredential:success");
                            updateUI();
                            dialog1.dismiss();
                            cdt.cancel();
                            otp_progressbar.setVisibility(View.GONE);
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                                // [START_EXCLUDE silent]
//                                mVerificationField.setError("Invalid code.");
                                // [END_EXCLUDE]
                                Toast.makeText(LoginActivity.this, "Invalid OTP", Toast.LENGTH_SHORT).show();
                                otp1.setText("");
                                otp2.setText("");
                                otp3.setText("");
                                otp4.setText("");
                                otp5.setText("");
                                otp6.setText("");
                                otp1.requestFocus();
                                otp_progressbar.setVisibility(View.GONE);
                            }
                            // [START_EXCLUDE silent]
                            // Update UI
//                            updateUI(STATE_SIGNIN_FAILED);
                            // [END_EXCLUDE]
                        }
                    }
                });
    }
    // [END sign_in_with_phone]

    // [START auth_with_facebook]
    private void handleFacebookAccessToken(AccessToken token) {
        Log.d("TAG", "handleFacebookAccessToken:" + token);
        // [START_EXCLUDE silent]
        // dialog.show();
        // [END_EXCLUDE]

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithCredential:success");
                            updateUI();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("TAG", "signInWithCredential:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // [START_EXCLUDE]
                        //     dialog.dismiss();
                        // [END_EXCLUDE]
                    }
                });
    }

    private void updateUI() {
        final String userId = mAuth.getCurrentUser().getUid();
        final String device_token = FirebaseInstanceId.getInstance().getToken();
        // Users user = new Users();
        mDatabase.child("Users").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(userId)) {
                    mDatabase.child("Users").child(userId).child("device_token").setValue(device_token);
                    Intent intent = new Intent(LoginActivity.this, MapsActivity.class);
                    dialog.dismiss();
                    startActivity(intent);
                    finish();
                } else {
                    Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
                    startActivity(intent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    // [END auth_with_facebook]

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("TAG", "gSignIn:" + requestCode);

        mCallbackManager.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Log.i("TAG", "onActivityResult: " + requestCode);
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            Log.i("TAG", "onActivityResult: " + result.getStatus());
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                Log.i("TAG", "onActivityResult: ");
                GoogleSignInAccount account = result.getSignInAccount();
                // dialog.show();
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

        final AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            updateUI();
                        } else {
                            //dialog.hide();
                            if (task.getException() instanceof FirebaseAuthUserCollisionException) {
                                Toast.makeText(LoginActivity.this, "Email id Exist.Try using any other method", Toast.LENGTH_SHORT).show();
                            } else
                                Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            Log.i("TAG", "gSignIn:" + task.getResult());
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
            case R.id.btn_verify:
                if (!validatePhoneNumber()) {
                    return;
                }
                phoneAuth("+91" + mPhoneNumberField.getText().toString());
                enterOTP();
                break;
            case R.id.text_resend:
                cdt.start();
                resendVerificationCode("+91" + mPhoneNumberField.getText().toString(), mResendToken);
                resend.setVisibility(View.GONE);
                break;
            case R.id.btn_cancel:
                cdt.cancel();
                dialog1.hide();
                break;
            case R.id.otp_submit:
                otp_progressbar.setVisibility(View.VISIBLE);

                String otp = otp1.getText().toString() + otp2.getText().toString() + otp3.getText().toString() +
                        otp4.getText().toString() + otp5.getText().toString() + otp6.getText().toString();
                if (otp.length() == 6) {
                    PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId, otp);
                    signInWithPhoneAuthCredential(credential);
                } else {
                    otp_progressbar.setVisibility(View.GONE);
                    Toast.makeText(LoginActivity.this, "Invalid OTP", Toast.LENGTH_SHORT).show();
                }
                break;

        }

    }

    public void enterOTP() {
        // dialog.show();


        dialog1 = new Dialog(this);
        dialog1.setContentView(R.layout.otpdialog);
        dialog1.setCancelable(false);
        dialog1.show();
        dialog1.setCanceledOnTouchOutside(false);
        dialog1.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        resend = dialog1.findViewById(R.id.text_resend);
        resend.setVisibility(View.GONE);
        cancel = dialog1.findViewById(R.id.btn_cancel);

        cancel.setOnClickListener(this);
        resend.setOnClickListener(this);

        otp1 = dialog1.findViewById(R.id.edit_otp1);
        otp2 = dialog1.findViewById(R.id.edit_otp2);
        otp3 = dialog1.findViewById(R.id.edit_otp3);
        otp4 = dialog1.findViewById(R.id.edit_otp4);
        otp5 = dialog1.findViewById(R.id.edit_otp5);
        otp6 = dialog1.findViewById(R.id.edit_otp6);
        timer = dialog1.findViewById(R.id.text_timer);
        otp_progressbar = dialog1.findViewById(R.id.otp_progressBar);
        otp_submit = dialog1.findViewById(R.id.otp_submit);

        otp_submit.setOnClickListener(this);

        cdt = new CountDownTimer(60000, 1000) {

            public void onTick(long millisUntilFinished) {
                timer.setText("00:" + millisUntilFinished / 1000);
            }

            public void onFinish() {
                timer.setText("00:00");
                resend.setVisibility(View.VISIBLE);
            }
        }.start();
        otp1.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {

                if (s.length() == 1) {
                    otp2.requestFocus();
                } else if (s.length() == 0) {
                    otp1.requestFocus();
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }
        });
        otp2.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {

                if (s.length() == 1) {
                    otp3.requestFocus();
                } else if (s.length() == 0) {
                    otp1.requestFocus();
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }
        });
        otp3.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {

                if (s.length() == 1) {
                    otp4.requestFocus();
                } else if (s.length() == 0) {
                    otp2.requestFocus();
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }
        });
        otp4.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {

                if (s.length() == 1) {
                    otp5.requestFocus();
                } else if (s.length() == 0) {
                    otp3.requestFocus();
                }

            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }
        });
        otp5.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
                if (s.length() == 1) {
                    otp6.requestFocus();
                } else if (s.length() == 0) {
                    otp4.requestFocus();
                }

            }

            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
            }
        });
        otp6.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 1) {
                    otp6.requestFocus();
                } else if (s.length() == 0) {
                    otp5.requestFocus();
                }
            }
        });


        dialog1.show();
    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        Log.i("TAG", "onStart");
        if (currentUser != null) {
            //  dialog.show();
            updateUI();
//            if(Datsme.getPreferenceManager().getString(MyPreference.USERNAME).equals("true")&&Datsme.getPreferenceManager().getString(MyPreference.COMPPRO).equals("true"))
//            {
//                Log.i("TAG","Maps");
//                Intent intent=new Intent(LoginActivity.this,MapsActivity.class);
//                startActivity(intent);
//            }
//            else if(Datsme.getPreferenceManager().getString(MyPreference.USERNAME).equals("true"))
//            {
//                Log.i("TAG","Complete");
//                Intent intent=new Intent(LoginActivity.this,CompleteProfileActivity.class);
//                startActivity(intent);
//            }
////            else{
////                Intent intent = new Intent(LoginActivity.this, ProfileActivity.class);
////                dialog.dismiss();
////                startActivity(intent);}
////            else
////                updateUI();
        }
    }

    private boolean validatePhoneNumber() {
        String phoneNumber = mPhoneNumberField.getText().toString();
        if (TextUtils.isEmpty(phoneNumber)) {
            mPhoneNumberField.setError("Invalid phone number.");
            return false;
        }

        return true;
    }

    private void phoneAuth(String phoneNumber) {
        // [START start_phone_auth]
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks);        // OnVerificationStateChangedCallbacks
        // [END start_phone_auth]

        mVerificationInProgress = true;
    }

    // [START resend_verification]
    private void resendVerificationCode(String phoneNumber,
                                        PhoneAuthProvider.ForceResendingToken token) {
        Toast.makeText(this, "OTP Request sent", Toast.LENGTH_SHORT).show();
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                phoneNumber,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                mCallbacks,         // OnVerificationStateChangedCallbacks
                token);             // ForceResendingToken from callbacks
    }


    // [END resend_verification]

}