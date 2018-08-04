package me.dats.com.datsme.Fragments;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Scroller;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;
import me.dats.com.datsme.Activities.Friendsquestions;
import me.dats.com.datsme.Activities.LoginActivity;
import me.dats.com.datsme.Activities.MapsActivity;
import me.dats.com.datsme.Datsme;
import me.dats.com.datsme.R;
import me.dats.com.datsme.Utils.BlurImage;

import static android.app.Activity.RESULT_OK;

/**
 * A simple {@link Fragment} subclass.
 */
public class My_Profile extends Fragment implements View.OnClickListener {

    MapsActivity mapsActivity;
    public ProgressDialog dialog;
    public DatabaseReference newRef;
    Menu Mymenu;
    Boolean checkMenu;
    @BindView(R.id.toolbar_myprofile)
    Toolbar toolbar;
    @BindView(R.id.setting)
    ImageButton setting;
    @BindView(R.id.backdrop)
    ImageView pic;
    @BindView(R.id.above_backdrop)
    CircleImageView above_backdrop;
    @BindView(R.id.edit_abt_u)
    EditText abtU;
    @BindView(R.id.user_name)
    TextView user_name;
    @BindView(R.id.edit_ur_clg)
    EditText college;
    @BindView(R.id.radio_gender)
    RadioGroup gender;
    @BindView(R.id.settings_edit)
    CircleImageView edit_image;
    @BindView(R.id.save)
    TextView save;
    @BindView(R.id.cancel)
    TextView cancel;
    @BindView(R.id.my_questions)
    Button myquestions;
    View view;
    String name, about_u, ur_clg, ur_gender, ur_image;
    String thumb_downloadurl = null;
    String download_url = null;
    private FirebaseUser mUser;
    private String uid;
    private DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("Users");
    private int BLUR_PRECENTAGE = 20;
    private StorageReference mStorageRef;


    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_my__profile, container, false);
        ButterKnife.bind(this, view);

        mapsActivity = (MapsActivity) getActivity();
        mapsActivity.setSupportActionBar(toolbar);

        setHasOptionsMenu(true);

        //Dialog Setup
        dialog = new ProgressDialog(getActivity());
        dialog.setCancelable(false);
        dialog.setMessage("Please Wait....");
        dialog.setTitle("Loading");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);


        myquestions.setOnClickListener(this);
        cancel.setOnClickListener(this);
        edit_image.setOnClickListener(this);
        save.setOnClickListener(this);
        setMyProfiledata();

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseAuth.AuthStateListener authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    //Do anything here which needs to be done after signout is complete

                    Datsme.getPreferenceManager().clearLoginData();
                    Intent i = new Intent(getActivity(), LoginActivity.class);
                    startActivity(i);
                    if (getActivity() != null)
                        getActivity().finish();

                }
            }
        };
        firebaseAuth.addAuthStateListener(authStateListener);
        return view;
    }

    private void setMyProfiledata() {
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = mUser.getUid();
        newRef = mRef.child(uid);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        newRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                name = String.valueOf(dataSnapshot.child("name").getValue());
                about_u = String.valueOf(dataSnapshot.child("about").getValue());
                ur_clg = String.valueOf(dataSnapshot.child("college").getValue());
                ur_gender = String.valueOf(dataSnapshot.child("gender").getValue());
                ur_image = String.valueOf(dataSnapshot.child("thumb_image").getValue());

                abtU.setInputType(InputType.TYPE_NULL);
                abtU.setLines(4);
                abtU.setMaxLines(4);
                abtU.setScroller(new Scroller(getActivity()));
                abtU.setVerticalScrollBarEnabled(true);
                abtU.setSingleLine(false);

                college.setInputType(InputType.TYPE_NULL);
                abtU.setText(about_u);
                college.setText(ur_clg);
                user_name.setText(name);

                switch (ur_gender) {
                    case "Male":
                        gender.check(gender.getChildAt(0).getId());
                        SelectRadioForSetting(false);
                        break;
                    case "Female":
                        gender.check(gender.getChildAt(1).getId());
                        SelectRadioForSetting(false);
                        break;
                    case "Other":
                        gender.check(gender.getChildAt(2).getId());
                        SelectRadioForSetting(false);
                        break;
                }
                Target target = new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        pic.setImageBitmap(BlurImage.fastblur(bitmap, 1f, BLUR_PRECENTAGE));
                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {

                    }
                };
                Picasso.get().load(ur_image).into(above_backdrop);
                Picasso.get()
                        .load(ur_image).into(target);
                pic.setTag(target);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.myprofile, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getTitle().toString()) {
            case "Settings":
//                dialog.show();
//                Toast.makeText(getActivity(), "aslkfnlsanfas", Toast.LENGTH_SHORT).show();
//                FirebaseUser user=FirebaseAuth.getInstance().getCurrentUser();
//                if(user==null)
//                {
//                    Toast.makeText(getActivity(), "", Toast.LENGTH_SHORT).show();
//                }
//                user.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
//                    @Override
//                    public void onComplete(@NonNull Task<Void> task) {
//                        if (task.isSuccessful()) {
//                            dialog.dismiss();
//                            Toast.makeText(getActivity(), "succefully deleted", Toast.LENGTH_SHORT).show();
//                        }
//                        else {
//                            dialog.dismiss();
//                            Toast.makeText(getActivity(), "aslndfklahlf", Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
                break;
            case "Edit":
                setting.setVisibility(View.GONE);
                save.setVisibility(View.VISIBLE);

                abtU.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
                abtU.setLines(4);
                abtU.setMaxLines(4);
                abtU.setScroller(new Scroller(getActivity()));
                abtU.setVerticalScrollBarEnabled(true);
                abtU.setSingleLine(false);

                college.setInputType(InputType.TYPE_CLASS_TEXT);

                cancel.setVisibility(View.VISIBLE);

                edit_image.setVisibility(View.VISIBLE);

                SelectRadioForSetting(true);

                break;
            case "Log Out":
                newRef.child("device_token").setValue(null).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            FirebaseAuth.getInstance().signOut();

                        } else {
                            Toast.makeText(getActivity(), "Try Again", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                break;

        }
        return super.onOptionsItemSelected(item);
    }


    public void SelectRadioForSetting(boolean y) {
        if (!y) {
            for (int i = 0; i < gender.getChildCount(); i++) {
                if (gender.getCheckedRadioButtonId() != gender.getChildAt(i).getId())
                    gender.getChildAt(i).setVisibility(View.GONE);
            }
        } else {
            for (int i = 0; i < gender.getChildCount(); i++) {
                gender.getChildAt(i).setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            final CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                dialog = new ProgressDialog(getActivity());
                dialog.setMessage("Please wait while we upload the image");
                dialog.setTitle("Uploading Image...");
                dialog.setCancelable(false);
                dialog.show();


                Uri resultUri = result.getUri();

                File thumb_file = new File(resultUri.getPath());

                final String current_userid = uid;

                Bitmap file = null;

                Bitmap thumb_bitmap = null;
                final byte[] thumb_byte;
                final byte[] file_byte;

                try {
                    thumb_bitmap = new Compressor(getActivity())
                            .setMaxHeight(200)
                            .setMaxWidth(200)
                            .setQuality(30)
                            .compressToBitmap(thumb_file);

                    file = new Compressor(getActivity())
                            .setMaxHeight(200)
                            .setMaxWidth(200)
                            .setQuality(75)
                            .compressToBitmap(thumb_file);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ByteArrayOutputStream baos1 = new ByteArrayOutputStream();

                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                file.compress(Bitmap.CompressFormat.JPEG, 100, baos1);
                thumb_byte = baos.toByteArray();
                file_byte = baos1.toByteArray();

                StorageReference filepath = mStorageRef.child("profile_images").child(current_userid + ".jpg");
                final StorageReference thumb_filepath = mStorageRef.child("profile_images").child("thumbs").child(current_userid + ".jpg");

                final StorageReference ref = mStorageRef.child("profile_images").child(current_userid + ".jpg");
                UploadTask uploadTask = ref.putBytes(file_byte);

                Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                    @Override
                    public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                        if (!task.isSuccessful()) {
                            throw task.getException();
                        }

                        // Continue with the task to get the download URL
                        return ref.getDownloadUrl();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {

                            download_url = task.getResult().toString();
                            UploadTask uploadTask = thumb_filepath.putBytes(thumb_byte);
                            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                @Override
                                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                    if (!task.isSuccessful()) {
                                        throw task.getException();
                                    }

                                    // Continue with the task to get the download URL
                                    return ref.getDownloadUrl();
                                }
                            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> thumb_task) {
                                    if (thumb_task.isSuccessful()) {
                                        if (thumb_task.isSuccessful()) {

                                            thumb_downloadurl = thumb_task.getResult().toString();

                                            dialog.dismiss();
                                            edit_image.setImageURI(result.getUri());

                                        } else {
                                            dialog.dismiss();
                                            Toast.makeText(getActivity(), "Try Again Later", Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                }
                            });
                        } else {
                            dialog.dismiss();
                            Toast.makeText(getActivity(), "Try Again Later", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.my_questions:
                Intent i = new Intent(getActivity(), Friendsquestions.class);
                startActivity(i);
                break;
            case R.id.cancel:
                save.setVisibility(View.GONE);
                cancel.setVisibility(View.GONE);

                setting.setVisibility(View.VISIBLE);

                edit_image.setVisibility(View.GONE);


                abtU.setInputType(InputType.TYPE_NULL);
                college.setInputType(InputType.TYPE_NULL);

                SelectRadioForSetting(false);
                break;
            case R.id.save:
                if (abtU.getText().toString().isEmpty() || college.getText().toString().isEmpty()) {
                    if (abtU.getText().toString().isEmpty()) {
                        Toast.makeText(getActivity(), "ABOUT YOU CAN'T BE EMPTY", Toast.LENGTH_SHORT).show();
                    } else if (college.getText().toString().isEmpty()) {
                        Toast.makeText(getActivity(), "COLLEGE CAN'T BE EMPTY", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Map<String, Object> user = new HashMap<>();
                    user.put("about", abtU.getText().toString());
                    user.put("college", college.getText().toString());
                    String s = ((RadioButton) getView().findViewById(gender.getCheckedRadioButtonId())).getText().toString();

                    user.put("gender", s);

                    newRef.updateChildren(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(getActivity(), "updated", Toast.LENGTH_SHORT).show();
                        }
                    });

                    cancel.setVisibility(View.GONE);
                    save.setVisibility(View.GONE);

                    setting.setVisibility(View.VISIBLE);

                    edit_image.setVisibility(View.GONE);

                    abtU.setInputType(InputType.TYPE_NULL);
                    college.setInputType(InputType.TYPE_NULL);

                    SelectRadioForSetting(false);
                }
                break;
            case R.id.settings_edit:
                // start picker to get image for cropping and then use the image in cropping activity
                CropImage.activity()
                        .setAspectRatio(1, 1)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setMinCropWindowSize(500, 500)
                        .start(getActivity());
                break;
        }
    }
}
