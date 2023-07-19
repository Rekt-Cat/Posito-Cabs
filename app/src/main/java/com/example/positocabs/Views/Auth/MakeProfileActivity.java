package com.example.positocabs.Views.Auth;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.os.Bundle;
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
import com.example.positocabs.Views.Profile.EditProfileActivity;

import java.util.Calendar;

import de.hdodenhof.circleimageview.CircleImageView;

public class MakeProfileActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{

    private ImageView backBtn;
    private CircleImageView pfpEdit;
    private EditText name,phoneNo,email;
    private Spinner gender;
    private TextView dob;
    private DatePickerDialog datePickerDialog;
    private ProgressBar progressBar;
    private AppCompatButton continueBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_profile);

        //casting views
        backBtn=findViewById(R.id.back_btn);
        pfpEdit=findViewById(R.id.profile_image);
        name=findViewById(R.id.name_edit_text);
        phoneNo=findViewById(R.id.phone_no_edit_text);
        email=findViewById(R.id.email_edit_text);
        dob=findViewById(R.id.dob);
        gender=findViewById(R.id.gender_spinner);
        continueBtn=findViewById(R.id.continue_btn);

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

        //Continue btn logic
        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MakeProfileActivity.this, "done!", Toast.LENGTH_SHORT).show();
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
        String text = adapterView.getItemAtPosition(i).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

}