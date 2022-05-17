package com.example.appsocialactivity;

import static com.example.appsocialactivity.constants.SharedPrefNames.CONTACT_PREF;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.icu.util.GregorianCalendar;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.appsocialactivity.databinding.ActivityEventBinding;
import com.example.appsocialactivity.databinding.ActivityProfileBinding;
import com.example.appsocialactivity.dbmodel.Event;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

public class EventActivity extends AppCompatActivity {

    static public String TAG = "EVENT_ACTIVITY_TAG";


    private FirebaseFirestore db;
    // ViewBinding
    private ActivityEventBinding binding;




    Long time;
    String interest = "Seçiniz";
    GeoPoint loc;
    String contactNumber ="";

    private View dialogView;
    private AlertDialog alertDialog;
    Spinner spinnerInterests;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEventBinding.inflate(getLayoutInflater());
        Intent intent = getIntent();
        loc = new GeoPoint(intent.getDoubleExtra("lat", 0),intent.getDoubleExtra("lng",0));


        setContentView(binding.getRoot());



        // get instance of firestore db
        db = FirebaseFirestore.getInstance();

        spinnerInterests = findViewById(R.id.spinner_interests);
        ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(this, R.array.interests,android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        spinnerInterests.setAdapter(adapter);
        spinnerInterests.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent.getItemAtPosition(position).equals(0)){
                    ((TextView) parent.getChildAt(0)).setText("Seçiniz");
                    ((TextView) parent.getChildAt(0)).setTextColor(Color.BLACK);
                    ((TextView) parent.getChildAt(0)).setTextSize(23);
                }else {
                    interest = parent.getItemAtPosition(position).toString();

                    Toast.makeText(parent.getContext(),"Selected: " +interest, Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        binding.submitDateBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogView = View.inflate(EventActivity.this, R.layout.date_time_picker, null);
                alertDialog = new AlertDialog.Builder(EventActivity.this).create();
                dialogView.findViewById(R.id.date_time_set).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                        DatePicker datePicker = (DatePicker) dialogView.findViewById(R.id.date_picker);
                        TimePicker timePicker = (TimePicker) dialogView.findViewById(R.id.time_picker);

                        Calendar calendar = new GregorianCalendar(datePicker.getYear(),
                                datePicker.getMonth(),
                                datePicker.getDayOfMonth(),
                                timePicker.getCurrentHour(),
                                timePicker.getCurrentMinute());

                        time = calendar.getTimeInMillis();

                        Date date = new Date(time);
                        Format format = new SimpleDateFormat("yyyy MM dd HH:mm:ss");
                        String formatString = format.format(date);
                        binding.dateTV.setText(formatString);
                        alertDialog.dismiss();
                    }});
                alertDialog.setView(dialogView);
                if (time!= null){
                    Log.i(TAG,time.toString());
                }
                alertDialog.show();
            }
        });
        binding.addEventBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                validate_data();
            }
        });

        binding.addLocationBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(EventActivity.this, MapsActivity.class));
            }
        });
    }
    private void validate_data(){
        String title = binding.title.getText().toString();
        String description = binding.description.getText().toString();
        String dateTime = binding.dateTV.getText().toString();
        Integer numOfPeople = binding.numberOfPeople.getText().toString().equals("")? 0: Integer.parseInt(binding.numberOfPeople.getText().toString());
        String nameOfPlace = binding.address.getText().toString();
        if(title.length()<2){
            Toast.makeText(this, "Title must be at least 2 characters", Toast.LENGTH_SHORT).show();
        }
        else if(description.length()<10){
            Toast.makeText(this, "You must add a decent description", Toast.LENGTH_SHORT).show();
        }else if(nameOfPlace.length() <= 2){
            Toast.makeText(this, "Yo must add a decent address for your event", Toast.LENGTH_SHORT).show();
        }
        else if(loc.getLatitude() == 0.0 && loc.getLongitude() == 0.0){
            Toast.makeText(this, "You must select a location for your event", Toast.LENGTH_SHORT).show();
        }
        else if(dateTime.length()<2){
            Toast.makeText(this, "You must pick a date", Toast.LENGTH_SHORT).show();
        }else if(numOfPeople <= 0){
            Toast.makeText(this, "You must select at least 1 people to attend", Toast.LENGTH_SHORT).show();
        }else if(interest.equals("Seçiniz")){
            Toast.makeText(this, "You must pick an interest for your event", Toast.LENGTH_SHORT).show();
        }
        else{
            AddEvent();

        }
    }
    //GeoPoint location, Long time, String description, String name, Integer numOfPeople, ArrayList<String> interestsOfUser
    private void AddEvent(){
        SharedPreferences prefs = getSharedPreferences(CONTACT_PREF, MODE_PRIVATE);
        String contactphone = prefs.getString("contactphone", "No phone defined");

        Event event = new Event();
        event.setDate(time);
        event.setLocation(loc);
        event.setContactNumber(contactphone);
        event.setEventDescription(binding.description.getText().toString());
        event.setEventName(binding.title.getText().toString());
        event.setInterestsOfEvent(interest);
        event.setNameOfPlace(binding.address.getText().toString());
        event.setNumOfPeople(Integer.parseInt(binding.numberOfPeople.getText().toString()));

        Log.i(TAG, "asdasd: " + contactNumber);
        db.collection("Event").add(event).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(EventActivity.this, "Event added successfully", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(EventActivity.this, MainActivity.class));
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EventActivity.this, "Failed to add event", Toast.LENGTH_SHORT).show();
            }
        });
    }
}