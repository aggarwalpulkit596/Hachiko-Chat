package me.dats.com.datsme.Fragments;


import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;

import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
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

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import de.hdodenhof.circleimageview.CircleImageView;
import me.dats.com.datsme.Activities.LoginActivity;
import me.dats.com.datsme.Activities.MapsActivity;
import me.dats.com.datsme.Activities.Setting;
import me.dats.com.datsme.R;
import utils.BlurImage;

/**
 * A simple {@link Fragment} subclass.
 */
public class My_Profile extends Fragment  {

    @BindView(R.id.toolbar_myprofile)
    Toolbar toolbar;

    @BindView(R.id.setting)
    ImageButton setting;
    @BindView(R.id.backdrop)
    ImageView pic;

    @BindView(R.id.above_backdrop)
    CircleImageView above_backdrop;

    @BindView(R.id.edit_image)
    TextView edit_img;

    @BindView(R.id.edit_abt_u)
    EditText abtU;

    @BindView(R.id.user_name)
    TextView user_name;

    @BindView(R.id.edit_ur_clg)
    EditText college;

    @BindView(R.id.radio_gender)
    RadioGroup gender;

    @BindView(R.id.settings_edit)
    CircleImageView edit;

    @BindView(R.id.save)
    TextView save;

    @BindView(R.id.cancel)
    TextView cancel;

    String name,about_u,ur_clg,ur_gender,ur_image;
    private FirebaseUser mUser;
    private String uid;
    private DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("Users");

    public ProgressDialog dialog;
    public DatabaseReference newRef;
    private int BLUR_PRECENTAGE = 95;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_my__profile, container, false);
        ButterKnife.bind(this,view);

        //Dialog Setup
        dialog = new ProgressDialog(getActivity());
        dialog.setCancelable(false);
        dialog.setMessage("Please Wait....");
        dialog.setTitle("Loading");
        dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);


//           context.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        mUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = mUser.getUid();
        newRef = mRef.child(uid);
        newRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                dialog.show();

                name = String.valueOf(dataSnapshot.child("name").getValue());
                about_u = String.valueOf(dataSnapshot.child("about").getValue());
                ur_clg = String.valueOf(dataSnapshot.child("college").getValue());
                ur_gender = String.valueOf(dataSnapshot.child("gender").getValue());
                ur_image = String.valueOf(dataSnapshot.child("image").getValue());

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

                switch (ur_gender)
                {
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
                dialog.dismiss();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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

                edit.setEnabled(false);
                edit.setVisibility(View.GONE);

                edit_img.setVisibility(View.VISIBLE);

                SelectRadioForSetting(true);
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                save.setVisibility(View.GONE);
                cancel.setVisibility(View.GONE);

                setting.setVisibility(View.VISIBLE);
                edit.setEnabled(true);
                edit.setVisibility(View.VISIBLE);

                edit_img.setVisibility(View.GONE);

                abtU.setInputType(InputType.TYPE_NULL);
                college.setInputType(InputType.TYPE_NULL);

                SelectRadioForSetting(false);
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(abtU.getText().toString().isEmpty() || college.getText().toString().isEmpty())
                {
                    if(abtU.getText().toString().isEmpty())
                    {
                        Toast.makeText(getActivity(),"ABOUT YOU CAN'T BE EMPTY",Toast.LENGTH_SHORT).show();
                    }
                    else if(college.getText().toString().isEmpty())
                    {
                        Toast.makeText(getActivity(),"COLLEGE CAN'T BE EMPTY",Toast.LENGTH_SHORT).show();
                    }
                }
                else {
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

                    edit.setEnabled(true);
                    edit.setVisibility(View.VISIBLE);

                    abtU.setInputType(InputType.TYPE_NULL);
                    college.setInputType(InputType.TYPE_NULL);

                    edit_img.setVisibility(View.GONE);

                    SelectRadioForSetting(false);
                }
            }
        });
        edit_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // start picker to get image for cropping and then use the image in cropping activity
                CropImage.activity()
                        .setAspectRatio(1, 1)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setMinCropWindowSize(500, 500)
                        .start(getActivity());

            }
        });
        return view;
    }
    public void SelectRadioForSetting(boolean y)
    {
        if(!y) {
            for (int i = 0; i < gender.getChildCount(); i++) {
                if (gender.getCheckedRadioButtonId() != gender.getChildAt(i).getId())
                    gender.getChildAt(i).setVisibility(View.GONE);
            }
        }
        else
        {
            for (int i = 0; i < gender.getChildCount(); i++) {
                gender.getChildAt(i).setVisibility(View.VISIBLE);
            }
        }
    }
    @OnClick(R.id.settings_logout)
    void logout() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();
        mAuth = null;
        Intent i = new Intent(getActivity(), LoginActivity.class);
        startActivity(i);
        getActivity().finish();
    }
}
