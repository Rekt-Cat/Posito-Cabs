package com.example.positocabs.Views.Profile;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.positocabs.Callback.TaskCallback;
import com.example.positocabs.Models.User;
import com.example.positocabs.R;
import com.example.positocabs.ViewModel.SaveUserDataViewModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class EditProfileActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private ImageView backBtn;
    private CircleImageView userPfp;
    private TextInputLayout lName, lEmail, lAddress;
    private TextInputEditText name, email, address;
    private Spinner gender;
    private TextView dob;
    private DatePickerDialog datePickerDialog;
    private ProgressBar progressBarUpdate,progressBarPfp;
    private AppCompatButton updateBtn;
    private String userType;
    private SaveUserDataViewModel saveUserDataViewModel;

    private String genderText;
    private Uri imageUri;
    private User tempUser;

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private static final int PICK_IMAGE_REQUEST_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile_screen);

        backBtn=findViewById(R.id.back_btn);
        userPfp=findViewById(R.id.profile_image);
        lName=findViewById(R.id.name_layout);
        name=findViewById(R.id.name_edit_text);
        lEmail=findViewById(R.id.email_layout);
        email=findViewById(R.id.email_edit_text);
        lAddress=findViewById(R.id.address_layout);
        address=findViewById(R.id.address_edit_text);
        gender=findViewById(R.id.gender_spinner);
        dob=findViewById(R.id.dob);
        progressBarUpdate=findViewById(R.id.progress_bar);
        progressBarPfp=findViewById(R.id.progress_bar_pfp);
        updateBtn=findViewById(R.id.update_btn);

        //fetching data
        userType = getIntent().getStringExtra("userType");
        saveUserDataViewModel = new ViewModelProvider(this).get(SaveUserDataViewModel.class);
        saveUserDataViewModel.readUserData(userType).observe(this, new Observer<User>() {
            @Override
            public void onChanged(User user) {
                //setting user data
                tempUser = user;
                name.setText(user.getName());
                email.setText(user.getEmail());
                genderText = user.getGender();
                dob.setText(user.getDob());

                if(user.gender.equals("Female")){
                    gender.setSelection(0);
                } else if (user.gender.equals("Male")) {
                    gender.setSelection(1);
                } else {
                    gender.setSelection(2);
                }

                setPicture(userPfp,user.getUserPfp());
            }
        });


        //Gender logic (Spinner)
        String[] genders = getResources().getStringArray(R.array.gender_options);
        // Create an ArrayAdapter using a string array and a default spinner layout
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, genders);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gender.setAdapter(adapter);
        gender.setOnItemSelectedListener(this);

        //userPfp btn
        userPfp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(EditProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Request the permission
                    ActivityCompat.requestPermissions(EditProfileActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                } else {
                    // Permission already granted, proceed with accessing the content URI
                    openGallery();
                }
            }
        });


        //DOB logic
        initDatePicker();
        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog.show();
            }
        });

        //Update btn logic
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(name.getText().toString().isEmpty()){
                    lName.setError("The field must not be empty!");
                }
                else if (email.getText().toString().isEmpty()) {
                    lEmail.setError("The field must not be empty!");
                }
//                else if(address.getText().toString().isEmpty()){
//                    lAddress.setError("The field must not be empty!");
//                }
                else if(gender.getSelectedItem() == null){
                    Toast.makeText(EditProfileActivity.this, "Please select gender!", Toast.LENGTH_SHORT).show();
                }
                else if(dob.getText().toString() == "DOB"){
                    Toast.makeText(EditProfileActivity.this, "Please select DOB!", Toast.LENGTH_SHORT).show();
                }
                else {
                    showUpdateProgressBar();

                    if(imageUri == null){
                        imageUri = Uri.parse(tempUser.getUserPfp());
                    }

                    tempUser.setName(name.getText().toString());
                    tempUser.setEmail(email.getText().toString());
                    tempUser.setGender(genderText);
                    tempUser.setDob(dob.getText().toString());

                    saveUserDataViewModel.updateUserData(tempUser, imageUri, userType, new TaskCallback() {
                        @Override
                        public void onSuccess() {
                            hideUpdateProgressBar();
                            Toast.makeText(EditProfileActivity.this, "Updated!", Toast.LENGTH_SHORT).show();
                            finish();
                        }

                        @Override
                        public void onFailure(String errorMessage) {
                            hideUpdateProgressBar();
                            Toast.makeText(EditProfileActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                        }
                    });

                }
            }
        });

        //back btn
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    private void initDatePicker(){
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                month = month + 1;
                String date = makeDateString(day,month,year);
                dob.setText(date);
            }
        };
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        int style = AlertDialog.THEME_HOLO_LIGHT;

        datePickerDialog = new DatePickerDialog(this,style,dateSetListener,year,month,day);
    }

    private String makeDateString(int day,int month,int year){
        return getMonthFormat(month) + " " + day + " " + year;
    }

    private String getMonthFormat(int month){
        String monthAbbreviation;

        switch (month) {
            case 1:
                monthAbbreviation = "JAN";
                break;
            case 2:
                monthAbbreviation = "FEB";
                break;
            case 3:
                monthAbbreviation = "MAR";
                break;
            case 4:
                monthAbbreviation = "APR";
                break;
            case 5:
                monthAbbreviation = "MAY";
                break;
            case 6:
                monthAbbreviation = "JUN";
                break;
            case 7:
                monthAbbreviation = "JUL";
                break;
            case 8:
                monthAbbreviation = "AUG";
                break;
            case 9:
                monthAbbreviation = "SEP";
                break;
            case 10:
                monthAbbreviation = "OCT";
                break;
            case 11:
                monthAbbreviation = "NOV";
                break;
            case 12:
                monthAbbreviation = "DEC";
                break;
            default:
                monthAbbreviation = "JAN";
                break;
        }

        return monthAbbreviation;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        genderText = adapterView.getItemAtPosition(i).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void openGallery() {
        Intent photoIntent = new Intent(Intent.ACTION_PICK);
        photoIntent.setType("image/*");
        startActivityForResult(photoIntent, PICK_IMAGE_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE) {
            // Check if the permission was granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, proceed with accessing the content URI
                openGallery();
            } else {
                // Permission denied, handle accordingly (e.g., show an error message)
                Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            setLocalPicture(userPfp,imageUri);

        }
    }

    private void setLocalPicture(ImageView imageView, Uri imageUri){
        // Show the progress bar
        showPfpProgressBar();

        Picasso.get()
                .load(imageUri)
                .error(R.drawable.default_pfp_ico)
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        // Hide the progress bar on success
                        hidePfpProgressBar();
                    }

                    @Override
                    public void onError(Exception e) {
                        // Hide the progress bar on success
                        hidePfpProgressBar();
                    }
                });
    }

    private void setPicture(ImageView imageView, String imageUrl){
        // Show the progress bar
        showPfpProgressBar();

        Picasso.get()
                .load(imageUrl)
                .error(R.drawable.default_pfp_ico)
                .into(imageView, new Callback() {
                    @Override
                    public void onSuccess() {
                        // Hide the progress bar on success
                        hidePfpProgressBar();
                    }

                    @Override
                    public void onError(Exception e) {
                        // Hide the progress bar on success
                        hidePfpProgressBar();
                    }
                });
    }

    private void showPfpProgressBar(){
        progressBarPfp.setVisibility(View.VISIBLE);
        userPfp.setVisibility(View.GONE);
    }

    private void hidePfpProgressBar(){
        progressBarPfp.setVisibility(View.GONE);
        userPfp.setVisibility(View.VISIBLE);
    }

    private void showUpdateProgressBar(){
        progressBarUpdate.setVisibility(View.VISIBLE);
        updateBtn.setVisibility(View.GONE);
    }

    private void hideUpdateProgressBar(){
        progressBarUpdate.setVisibility(View.GONE);
        updateBtn.setVisibility(View.VISIBLE);
    }
}