package me.dats.com.datsme.Activities;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;
import me.dats.com.datsme.Datsme;
import me.dats.com.datsme.Models.Zodiac;
import me.dats.com.datsme.R;
import me.dats.com.datsme.Utils.MyPreference;

public class ProfileActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    @BindView(R.id.profile_root)
    RelativeLayout rootlayout;
    @BindView(R.id.user_image)
    CircleImageView userimamge;
    @BindView(R.id.user_displayname)
    EditText user_displayname;
    @BindView(R.id.user_gender)
    Spinner user_gender;
    @BindView(R.id.user_dob)
    TextView user_dob;
    @BindView(R.id.button2)
    Button cont;
    String thumb_downloadurl = null;
    String download_url = null;
    Calendar myCalendar;
    String userzodiac;
    String elements;
    int Numerlogy, age1;
    private DatabaseReference mDatabase;
    private FirebaseUser mCurrentUser;
    private StorageReference mStorageRef;
    private ProgressDialog mProgessDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        ButterKnife.bind(this);

        mCurrentUser = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference().child("Users").child(mCurrentUser.getUid());
        mDatabase.keepSynced(true);
        mStorageRef = FirebaseStorage.getInstance().getReference();
        userimamge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // start picker to get image for cropping and then use the image in cropping activity
                CropImage.activity()
                        .setAspectRatio(1, 1)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setMinCropWindowSize(500, 500)
                        .start(ProfileActivity.this);

            }
        });
        setupcalendar();
        setupspinner();


    }

    private void setupspinner() {
        // Spinner click listener
        user_gender.setOnItemSelectedListener(this);
        user_gender.setPrompt("Gender");

        // Spinner Drop down elements
        List<String> categories = new ArrayList<String>();
        categories.add("Male");
        categories.add("Female");
        categories.add("Others");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        user_gender.setAdapter(dataAdapter);
    }

    private void setupcalendar() {
        myCalendar = Calendar.getInstance();

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub

                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                Numerlogy = getNumerlogy(year, monthOfYear + 1, dayOfMonth);
                Zodiac zodiac = new Zodiac();
                userzodiac = zodiac.zodiac_sign(dayOfMonth, monthOfYear + 1);
                switch (userzodiac) {
                    case "Sagittarius":
                    case "Leo":
                    case "Aries":
                        elements = "Fire";
                        break;
                    case "Gemini":
                    case "Libra":
                    case "Aquarius":
                        elements = "Air";
                        break;
                    case "Taurus":
                    case "Virgo":
                    case "Capricorn":
                        elements = "Earth";
                        break;
                    case "Cancer":
                    case "Scorpio":
                    case "Pisces":
                        elements = "Water";
                        break;
                }
                try {
                    updateLabel();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

        };
        user_dob.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                DatePickerDialog d = new DatePickerDialog(ProfileActivity.this, date, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH));
                d.getDatePicker().setMaxDate(System.currentTimeMillis());
                d.show();


            }
        });
    }

    private int getNumerlogy(int year, int monthOfYear, int dayOfMonth) {
        int number = 0;
        while (year > 0) {
            number += year % 10;
            year = year / 10;
        }
        while (monthOfYear > 0) {
            number += monthOfYear % 10;
            monthOfYear = monthOfYear / 10;
        }
        while (dayOfMonth > 0) {
            number += dayOfMonth % 10;
            dayOfMonth = dayOfMonth / 10;
        }
        return number;
    }

    private void updateLabel() throws ParseException {
        String myFormat = "dd/MM/yyyy"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
        user_dob.setText(sdf.format(myCalendar.getTime()));
        String formatted = sdf.format(myCalendar.getTime());
        Date date1 = new SimpleDateFormat("dd/MM/yyyy").parse(formatted);
        Date now = new Date();
        long timeBetween = now.getTime() - date1.getTime();
        double yearsBetween = timeBetween / 3.15576e+10;
        age1 = (int) Math.floor(yearsBetween);
        if (age1 < 18) {
            Snackbar snackBar = Snackbar.make(rootlayout
                    , "Age should be more than 18 years", Snackbar.LENGTH_LONG);
            snackBar.show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            final CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                mProgessDialog = new ProgressDialog(this);
                mProgessDialog.setMessage("Please wait while we upload the image");
                mProgessDialog.setTitle("Uploading Image...");
                mProgessDialog.setCancelable(false);
                mProgessDialog.show();


                Uri resultUri = result.getUri();

                File thumb_file = new File(resultUri.getPath());

                final String current_userid = mCurrentUser.getUid();

                Bitmap file = null;

                Bitmap thumb_bitmap = null;
                final byte[] thumb_byte;
                final byte[] file_byte;

                try {
                    thumb_bitmap = new Compressor(this)
                            .setMaxHeight(200)
                            .setMaxWidth(200)
                            .setQuality(30)
                            .compressToBitmap(thumb_file);

                    file=new Compressor(this)
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

                                            mProgessDialog.dismiss();
                                            userimamge.setImageURI(result.getUri());

                                        } else {
                                            mProgessDialog.dismiss();
                                            Toast.makeText(ProfileActivity.this, "Try Again Later", Toast.LENGTH_SHORT).show();
                                        }

                                    }
                                }
                            });
                        } else {
                            mProgessDialog.dismiss();
                            Toast.makeText(ProfileActivity.this, "Try Again Later", Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }

    public void Savetodatabase(View view) {

        if (user_displayname.getText().toString().isEmpty() || user_dob.getText().equals("Date Of Birth") || download_url == null) {
            Snackbar snackBar;
            if (user_displayname.getText().toString().isEmpty()) {
                snackBar = Snackbar.make(rootlayout
                        , "Username Cannot Be Empty", Snackbar.LENGTH_SHORT);
                snackBar.show();
            } else if (user_dob.getText().equals("Date Of Birth")) {
                snackBar = Snackbar.make(rootlayout
                        , "DOB Cannot Be Empty", Snackbar.LENGTH_SHORT);
                snackBar.show();
            } else if (user_gender.getSelectedItem() == null) {
                snackBar = Snackbar.make(rootlayout
                        , "Gender Cannot Be Empty", Snackbar.LENGTH_SHORT);
                snackBar.show();
            } else if (download_url == null) {
                snackBar = Snackbar.make(rootlayout
                        , "Image Cannot Be Empty", Snackbar.LENGTH_SHORT);
                snackBar.show();
            }

        }
        else if(age1<18){
            Snackbar snackBar = Snackbar.make(rootlayout
                    , "Age should be more than 18 years", Snackbar.LENGTH_LONG);
            snackBar.show();
        }else {
            final String device_token = FirebaseInstanceId.getInstance().getToken();
            Map<String, String> userMap = new HashMap<>();
            userMap.put("name", user_displayname.getText().toString());
            userMap.put("gender", user_gender.getSelectedItem().toString());
            userMap.put("DOB", user_dob.getText().toString());
            userMap.put("image", download_url);
            userMap.put("thumb_image", thumb_downloadurl);
            userMap.put("Zodiac", userzodiac);
            userMap.put("Numerlogy", "" + Numerlogy);
            userMap.put("Element", elements);
            userMap.put("device_token", device_token);
            Log.i("TAG", "Savetodatabase: " + userMap.toString());
            mDatabase.setValue(userMap)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Datsme.getPreferenceManager().putBoolean(MyPreference.ProfileId,true);
                                mProgessDialog.dismiss();
                                startActivity(new Intent(ProfileActivity.this, CompleteProfileActivity.class));

                            }
                        }
                    });
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}