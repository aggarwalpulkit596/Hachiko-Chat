package me.dats.com.datsme.Fragments;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
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
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.dats.com.datsme.Activities.LoginActivity;
import me.dats.com.datsme.Activities.MapsActivity;
import me.dats.com.datsme.Activities.Setting;
import me.dats.com.datsme.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class My_Profile extends Fragment {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.toolbar_layout)
    CollapsingToolbarLayout toolbarLayout;
    @BindView(R.id.backdrop)
    ImageView pic;
    @BindView(R.id.edit_image)
    ImageButton edit_img;
    @BindView(R.id.edit_abt_u)
    EditText abtU;
    @BindView(R.id.edit_ur_clg)
    EditText college;
    @BindView(R.id.radio_gender)
    RadioGroup gender;
    @BindView(R.id.settings_edit)
    Button edit;
    @BindView(R.id.settings_save)
    Button save;
    @BindView(R.id.settings_cancel)
    Button cancel;
    String name,about_u,ur_clg,ur_gender,ur_image;
    private FirebaseUser mUser;
    private String uid;
    private DatabaseReference mRef = FirebaseDatabase.getInstance().getReference().child("Users");



    public My_Profile() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_my__profile, container, false);
        ButterKnife.bind(this,view);
        mUser = FirebaseAuth.getInstance().getCurrentUser();
        uid = mUser.getUid();
        DatabaseReference newRef = mRef.child(uid);
        newRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                name = String.valueOf(dataSnapshot.child("name").getValue());
                about_u = String.valueOf(dataSnapshot.child("about").getValue());
                ur_clg = String.valueOf(dataSnapshot.child("college").getValue());
                ur_gender = String.valueOf(dataSnapshot.child("gender").getValue());
                ur_image = String.valueOf(dataSnapshot.child("image").getValue());
                abtU.setInputType(InputType.TYPE_NULL);
                college.setInputType(InputType.TYPE_NULL);
                abtU.setText(about_u);
                college.setText(ur_clg);
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
                Picasso.get()
                        .load(ur_image).into(pic);

                toolbarLayout.setTitle(name);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        init(newRef);
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
    public void init(final DatabaseReference ref){

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save.setEnabled(true);
                save.setVisibility(View.VISIBLE);
                abtU.setInputType(InputType.TYPE_CLASS_TEXT);
                college.setInputType(InputType.TYPE_CLASS_TEXT);
                cancel.setEnabled(true);
                cancel.setVisibility(View.VISIBLE);
                edit.setEnabled(false);
                SelectRadioForSetting(true);
                edit.setVisibility(View.GONE);
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save.setVisibility(View.GONE);
                cancel.setVisibility(View.GONE);
                edit.setEnabled(true);
                edit.setVisibility(View.VISIBLE);
                abtU.setInputType(InputType.TYPE_NULL);
                college.setInputType(InputType.TYPE_NULL);
                SelectRadioForSetting(false);
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String,Object> user=new HashMap<>();
                user.put("about",abtU.getText().toString());
                user.put("college",college.getText().toString());
                String s= ((RadioButton)getView().findViewById(gender.getCheckedRadioButtonId())).getText().toString();

                user.put("gender",s);
                ref.updateChildren(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getActivity(),"updated", Toast.LENGTH_SHORT).show();
                    }
                });
                cancel.setVisibility(View.GONE);
                save.setVisibility(View.GONE);
                edit.setVisibility(View.VISIBLE);
                abtU.setInputType(InputType.TYPE_NULL);
                college.setInputType(InputType.TYPE_NULL);
                SelectRadioForSetting(false);
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
