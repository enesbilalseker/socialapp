package com.example.appsocialactivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;
import android.widget.Toast;


import com.example.appsocialactivity.databinding.ActivityMainBinding;
import com.example.appsocialactivity.dbmodel.Event;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.zip.Inflater;


public class MainActivity extends AppCompatActivity {

    // TAGS
    private static final String TAG = "DB_CONTROL";

    // view binding
    private ActivityMainBinding binding;

    // actionbar
    private ActionBar actionBar;

    private GoogleSignInOptions gso;
    private GoogleSignInClient gsc;
    private GoogleSignInAccount googleAccount;
    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore db;


    private ListView mList;
    private ArrayList<Event> eventArrayList;
    private ArrayList<Event> filteredEventArrayList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        // configure action bar, title, backbutton
        actionBar = getSupportActionBar();
        actionBar.setTitle("Event");
        mList = findViewById(R.id.event_listview);


        // Initialize firebase auth
        firebaseAuth=FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        // Initialize firebase user
        firebaseUser=firebaseAuth.getCurrentUser();
        eventArrayList = new ArrayList<>();
        filteredEventArrayList = new ArrayList<>();
        FetchEvents();


        binding.searchSV.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                FilterEventList(s);
                filteredEventArrayList = new ArrayList<>();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
        binding.searchSV.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                SetListView(eventArrayList);
                filteredEventArrayList = new ArrayList<>();
                return false;
            }
        });





        // Initialize sign in client
        gsc= GoogleSignIn.getClient(MainActivity.this, GoogleSignInOptions.DEFAULT_SIGN_IN);






    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);

        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    private void FilterEventList(String filter){

        if(filter.equalsIgnoreCase("") || filter.equals(null)){
            SetListView(eventArrayList);
            filteredEventArrayList = new ArrayList<>();
            return;
        }

        for (int i = 0; i < eventArrayList.size(); i++) {
            if(eventArrayList.get(i).getInterestsOfEvent().toLowerCase().contains(filter.toLowerCase())){
                filteredEventArrayList.add(eventArrayList.get(i));
            }
        }
        SetListView(filteredEventArrayList);

    }

    private void FetchEvents() {
    db.collection("Event").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
    @Override
    public void onComplete(@NonNull Task<QuerySnapshot> task) {
        if(task.isSuccessful()){
            for (QueryDocumentSnapshot document : task.getResult()) {
                eventArrayList.add(document.toObject(Event.class));

            }

            SetListView(eventArrayList);
        }
    }
});
    }

    private void SetListView(ArrayList<Event> elist){
        CustomListAdapter adapter = new CustomListAdapter(MainActivity.this, elist);

        // get the ListView and attach the adapter
        ListView itemsListView  = (ListView) findViewById(R.id.event_listview);
        itemsListView.setAdapter(adapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.profileButton:

                // Do Activity menu item stuff here
                startActivity(new Intent(MainActivity.this, ProfileActivity.class));
                return true;
            case R.id.logoutButton:

                LogoutActions();
                return true;

            default:
                break;
        }

        return false;
    }

    private void LogoutActions(){
        gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                // Check condition
                if(task.isSuccessful())
                {
                    // When task is successful
                    // Sign out from firebase
                    firebaseAuth.signOut();

                    // Display Toast
                    Toast.makeText(getApplicationContext(), "Logout successful", Toast.LENGTH_SHORT).show();

                    // Finish activity
                    finish();
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));

                }
            }
        });
    }
}