package com.example.positocabs.Views.Auth;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.positocabs.R;
import com.example.positocabs.ViewModel.SaveUserDataViewModel;
import com.example.positocabs.Views.MainScreen.RiderMain.RiderMainActivity;
import com.example.positocabs.Views.Profile.EditProfileActivity;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.io.IOException;
import java.util.Calendar;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;

public class MakeProfileActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private ImageView backBtn;
    private CircleImageView profilePic;
    private TextInputLayout lName, lEmail;
    private TextInputEditText name, email;
    private Spinner gender;
    private TextView dob;
    private DatePickerDialog datePickerDialog;
    private ProgressBar progressBar;
    private AppCompatButton continueBtn;

    ProgressDialog pd;

    private StorageTask uploadTask;
    private Uri imageUri;
    String myUrl ="";
    String text=null;
    private SaveUserDataViewModel saveUserDataViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_profile);

        //casting views
        backBtn=findViewById(R.id.back_btn);
        profilePic=findViewById(R.id.profile_image);
        lName=findViewById(R.id.name_layout);
        name=findViewById(R.id.name_edit_text);
        lEmail=findViewById(R.id.email_layout);
        email=findViewById(R.id.email_edit_text);
        dob=findViewById(R.id.dob);
        gender=findViewById(R.id.gender_spinner);
        progressBar=findViewById(R.id.progress_bar);
        continueBtn=findViewById(R.id.continue_btn);

        saveUserDataViewModel= new ViewModelProvider(this).get(SaveUserDataViewModel.class);

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
        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoIntent = new Intent(Intent.ACTION_PICK);
                photoIntent.setType("image/*");
                startActivityForResult(photoIntent, 1);
            }
        });

        //Continue btn logic
        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(name.getText().toString().isEmpty()){
                    lName.setError("The field must not be empty!");
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
                    progressBar.setVisibility(View.VISIBLE);
                    continueBtn.setVisibility(View.INVISIBLE);

                    //getting userType
                    Intent xIntent=getIntent();
                    String userType =xIntent.getStringExtra("userType");

                    saveUserDataViewModel.saveUserData(userType,name.getText().toString(),
                            email.getText().toString(),text,dob.getText().toString(),imageUri);

                    if(userType=="Rider"){
                        Intent intent = new Intent(MakeProfileActivity.this, RiderMainActivity.class);
                        intent.putExtra("userType", userType);
                        startActivity(intent);
                    }
                    else{
                        Intent intent = new Intent(MakeProfileActivity.this,DocVerificationActivity.class);
                        intent.putExtra("userType", userType);
                        startActivity(intent);
                    }

                    Toast.makeText(MakeProfileActivity.this, "done!", Toast.LENGTH_SHORT).show();

                    progressBar.setVisibility(View.INVISIBLE);
                    continueBtn.setVisibility(View.VISIBLE);
                }
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
         text = adapterView.getItemAtPosition(i).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            getImageInImageView();
        } else {
            Log.d("hehe", "onActivityResult: NOO");
        }
    }

    private void getImageInImageView() {

        Bitmap bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), imageUri);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        profilePic.setImageBitmap(bitmap);

    }


}