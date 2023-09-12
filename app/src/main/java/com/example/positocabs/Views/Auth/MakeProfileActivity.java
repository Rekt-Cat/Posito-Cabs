package com.example.positocabs.Views.Auth;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
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
import androidx.lifecycle.ViewModelProvider;

import com.example.positocabs.Callback.TaskCallback;
import com.example.positocabs.Models.DataModel.User;
import com.example.positocabs.R;
import com.example.positocabs.ViewModel.SaveUserDataViewModel;
import com.example.positocabs.Views.MainScreen.RiderMain.RiderMainActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class MakeProfileActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private ImageView backBtn;
    private CircleImageView userPfp;
    private TextInputLayout lName, lEmail;
    private TextInputEditText name, email;
    private Spinner gender;
    private TextView dob;
    private DatePickerDialog datePickerDialog;
    private ProgressBar progressBarContinue,progressBarPfp;
    private AppCompatButton continueBtn;

    ProgressDialog pd;

    private StorageTask uploadTask;
    private User tempUser;
    private Uri imageUri;
    private String myUrl ="";
    private String genderText,userType;
    private SaveUserDataViewModel saveUserDataViewModel;

    private static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1;
    private static final int PICK_IMAGE_REQUEST_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_profile);

        //casting views
        backBtn=findViewById(R.id.back_btn);
        userPfp=findViewById(R.id.profile_image);
        lName=findViewById(R.id.name_layout);
        name=findViewById(R.id.name_edit_text);
        lEmail=findViewById(R.id.email_layout);
        email=findViewById(R.id.email_edit_text);
        dob=findViewById(R.id.dob);
        gender=findViewById(R.id.gender_spinner);
        progressBarContinue=findViewById(R.id.progress_bar_continue);
        progressBarPfp=findViewById(R.id.progress_bar_pfp);
        continueBtn=findViewById(R.id.continue_btn);

        saveUserDataViewModel= new ViewModelProvider(this).get(SaveUserDataViewModel.class);

        //getting userType
        userType = getIntent().getStringExtra("userType");

        //Gender logic (Spinner)

        String[] genders = getResources().getStringArray(R.array.gender_options);
        // Create an ArrayAdapter using a string array and a default spinner layout
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_spinner_item, genders);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        gender.setAdapter(adapter);
        gender.setOnItemSelectedListener(this);

        //DOB logic
        initDatePicker();
        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                datePickerDialog.show();
            }
        });

        //pfp logic
        userPfp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (ContextCompat.checkSelfPermission(MakeProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    // Request the permission
                    ActivityCompat.requestPermissions(MakeProfileActivity.this,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                } else {
                    // Permission already granted, proceed with accessing the content URI
                    openGallery();
                }
            }
        });

        //Continue btn logic
        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(name.getText().toString().isEmpty()){
                    lName.setError("The field must not be empty!");
                }
                else if(imageUri == null || imageUri.toString().isEmpty()){
                    Toast.makeText(MakeProfileActivity.this, "Profile Picture must not be empty!", Toast.LENGTH_SHORT).show();
                }
                else if (email.getText().toString().isEmpty()) {
                    lEmail.setError("The field must not be empty!");
                }
                else if(gender.getSelectedItem() == null){
                    Toast.makeText(MakeProfileActivity.this, "Gender must not be empty!", Toast.LENGTH_SHORT).show();
                }
                else if(dob.getText().toString() == "DOB"){
                    Toast.makeText(MakeProfileActivity.this, "DOB must not be empty!", Toast.LENGTH_SHORT).show();
                }
                else {
                    showContinueBtnProgressBar();

                    tempUser = new User(name.getText().toString(),
                            email.getText().toString(), genderText, dob.getText().toString());
                    tempUser.setUserPfp(imageUri.toString());

                    saveUserDataViewModel.saveUserData(tempUser, userType, imageUri, new TaskCallback() {
                                @Override
                                public void onSuccess() {
                                    if(userType.equals("Rider")){
                                        Intent intent = new Intent(MakeProfileActivity.this, RiderMainActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        hideContinueBtnProgressBar();
                                    }
                                    else{
                                        Intent intent = new Intent(MakeProfileActivity.this,DocVerificationActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(intent);
                                        hideContinueBtnProgressBar();
                                    }
                                }

                                @Override
                                public void onFailure(String errorMessage) {
                                    hideContinueBtnProgressBar();
                                    Toast.makeText(MakeProfileActivity.this, errorMessage, Toast.LENGTH_SHORT).show();
                                }
                            });
                }
            }
        });

        //back btn logic
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

    private void showContinueBtnProgressBar(){
        progressBarContinue.setVisibility(View.VISIBLE);
        continueBtn.setVisibility(View.GONE);
    }

    private void hideContinueBtnProgressBar(){
        progressBarContinue.setVisibility(View.GONE);
        continueBtn.setVisibility(View.VISIBLE);
    }

    private void showPfpProgressBar(){
        progressBarPfp.setVisibility(View.VISIBLE);
        userPfp.setVisibility(View.GONE);
    }

    private void hidePfpProgressBar(){
        progressBarPfp.setVisibility(View.GONE);
        userPfp.setVisibility(View.VISIBLE);
    }

}