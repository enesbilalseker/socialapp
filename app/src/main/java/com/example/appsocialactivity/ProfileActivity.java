package com.example.appsocialactivity;

import static com.example.appsocialactivity.constants.SharedPrefNames.USER_ID_PREF;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.icu.util.Calendar;
import android.icu.util.GregorianCalendar;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.appsocialactivity.databinding.ActivityLoginBinding;
import com.example.appsocialactivity.databinding.ActivityProfileBinding;
import com.example.appsocialactivity.dbmodel.Event;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ProfileActivity extends AppCompatActivity {

    static public String TAG = "PROFILE_ACTIVITY_TAG";

    // ViewBinding
    private ActivityProfileBinding binding;

    Long time;
    String userid;
    GeoPoint locat;
    ArrayList<String> interestsOfUser;
    Integer numOfPeople = 0;
    private View dialogView;
    private AlertDialog alertDialog;

    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityProfileBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());


        // get instance of firestore db
        db = FirebaseFirestore.getInstance();


        SharedPreferences prefs = getSharedPreferences(USER_ID_PREF, MODE_PRIVATE);
        userid = prefs.getString("userId", "ERROR");



            binding.submitDateBTN.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    dialogView = View.inflate(ProfileActivity.this, R.layout.date_time_picker, null);
                    alertDialog = new AlertDialog.Builder(ProfileActivity.this).create();
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
            binding.submitEventBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AddEvent();
                }
            });

    }

    private void AddPhoneNumber(){
        DocumentReference docRef = db.collection("User").document(userid);
        docRef.update("phoneNumber", "123123123");
    }

    private void validate_data(){
        String title = binding.title.getText().toString();
        String description = binding.description.getText().toString();
        String dateTime = binding.dateTV.getText().toString();

        if(title.length()<2){
            Toast.makeText(this, "Title must be at least 2 characters", Toast.LENGTH_SHORT).show();
        }
        else if(description.length()<10){
            Toast.makeText(this, "You must add a decent description", Toast.LENGTH_SHORT).show();
        } else if(dateTime.length()<2){
            Toast.makeText(this, "You must pick a date", Toast.LENGTH_SHORT).show();
        }else{
            //locat, time, description, title, numOfPeople, interestsOfUser
            AddEvent();
        }

    }
    //GeoPoint location, Long time, String description, String name, Integer numOfPeople, ArrayList<String> interestsOfUser
    private void AddEvent(){
        Event event = new Event();
        event.setDate(12312L);
        event.setLocation(new GeoPoint(0,0));
        event.setEventDescription("hehe");
        event.setEventName("asd");
        event.setNumOfPeople(6);
        event.setInterestsOfEvent(null);
        db.collection("Event").add(event).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText(ProfileActivity.this, "Event added successfully", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ProfileActivity.this, "Failed to add event", Toast.LENGTH_SHORT).show();
            }
        });
    }
}