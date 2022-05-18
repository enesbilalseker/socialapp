package com.example.appsocialactivity;

import static com.example.appsocialactivity.constants.SharedPrefNames.CONTACT_PREF;
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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.appsocialactivity.constants.Interests;
import com.example.appsocialactivity.databinding.ActivityLoginBinding;
import com.example.appsocialactivity.databinding.ActivityProfileBinding;
import com.example.appsocialactivity.dbmodel.Event;
import com.example.appsocialactivity.dbmodel.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ProfileActivity extends AppCompatActivity {

    static public String TAG = "PROFILE_ACTIVITY_TAG";

    // ViewBinding
    private ActivityProfileBinding binding;

    String userid;
    GeoPoint locat;
    ArrayList<String> interestsOfUser;
    Integer numOfPeople = 0;
    User user;
    DocumentReference docRef;
    private FirebaseFirestore db;
    ArrayList<String> eventIdList;
    ArrayList<Event> usersEventList;
    ArrayList<String> usersEventIdList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityProfileBinding.inflate(getLayoutInflater());

        setContentView(binding.getRoot());


        // get instance of firestore db
        db = FirebaseFirestore.getInstance();

        eventIdList = new ArrayList<>();
        usersEventList = new ArrayList<>();
        usersEventIdList = new ArrayList<>();
        SharedPreferences prefs = getSharedPreferences(USER_ID_PREF, MODE_PRIVATE);
        userid = prefs.getString("userId", "ERROR");

        binding.submitPhoneBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validate_data(binding.phoneNumberEdit.getText().toString());
            }
        });

        binding.addEventBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileActivity.this, EventActivity.class);
                SharedPreferences.Editor editor = getSharedPreferences(CONTACT_PREF, MODE_PRIVATE).edit();
                editor.putString("contactphone", user.getPhoneNumber());
                editor.apply();
                startActivity(intent);
                finish();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        docRef = db.collection("User").document(userid);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    DocumentSnapshot document = task.getResult();
                    user = document.toObject(User.class);
                    Log.i("TAKKEW", userid);
                    eventIdList =  user.getEventList();
                    binding.profileName.setText(user.getNameSurname());
                    binding.phoneNumber.setText(user.getPhoneNumber());

                   if(user.getInterestsOfUser() == null){
                       interestsOfUser = new ArrayList<>();
                   }
                   else{
                       interestsOfUser = user.getInterestsOfUser();
                   }

                   if(interestsOfUser.contains(Interests.TOP_SPORLARI)){
                       binding.cb1.setChecked(true);
                   }
                   if(interestsOfUser.contains(Interests.SANS_OYUNLARI)){
                        binding.cb2.setChecked(true);
                    }
                   if(interestsOfUser.contains(Interests.TARTISMA)){
                        binding.cb3.setChecked(true);
                    }
                    binding.cb1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                            if (compoundButton.isChecked()){
                                interestsOfUser.add(Interests.TOP_SPORLARI);
                                docRef.update("interestsOfUser", interestsOfUser);
                            }else{
                                interestsOfUser.remove(Interests.TOP_SPORLARI);
                                docRef.update("interestsOfUser", interestsOfUser);
                            }
                        }
                    });
                    binding.cb2.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                            if (compoundButton.isChecked()){
                                interestsOfUser.add(Interests.SANS_OYUNLARI);
                                docRef.update("interestsOfUser", interestsOfUser);
                            }else{
                                interestsOfUser.remove(Interests.SANS_OYUNLARI);
                                docRef.update("interestsOfUser", interestsOfUser);
                            }
                        }
                    });
                    binding.cb3.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

                            if (compoundButton.isChecked()){
                                interestsOfUser.add(Interests.TARTISMA);
                                docRef.update("interestsOfUser", interestsOfUser);
                            }else{
                                interestsOfUser.remove(Interests.TARTISMA);
                                docRef.update("interestsOfUser", interestsOfUser);
                            }
                        }
                    });

                }
                db.collection("Event").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                for (int i = 0; i < eventIdList.size(); i++) {
                                    if (document.getId().equals(eventIdList.get(i))){
                                        usersEventList.add(document.toObject(Event.class));
                                        usersEventIdList.add(document.getId());
                                    }
                                }

                            }

                            SetListVieww(usersEventList, usersEventIdList, userid);
                        }
                    }
                });
            }
        });
    }

    private void validate_data(String number){
    if(number.length()!=11){
        Toast.makeText(this, "Geçerli bir telefon numarası giriniz", Toast.LENGTH_SHORT).show();
    }else{
        AddPhoneNumber(number);
    }
    }

    private void AddPhoneNumber(String number){
        DocumentReference docRef = db.collection("User").document(userid);
        docRef.update("phoneNumber", number);
        binding.phoneNumber.setText(number);
    }

    private void SetListVieww(ArrayList<Event> elist, ArrayList<String> idlist, String suserid){
        CustomListHistoryAdapter adapter = new CustomListHistoryAdapter(ProfileActivity.this, elist, idlist, suserid);

        // get the ListView and attach the adapter
        ListView itemsListView  = (ListView) findViewById(R.id.event_listview_history);
        itemsListView.setAdapter(adapter);
    }

}