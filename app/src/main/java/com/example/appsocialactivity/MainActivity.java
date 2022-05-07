package com.example.appsocialactivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;


import com.example.appsocialactivity.databinding.ActivityMainBinding;

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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;

import java.util.HashMap;
import java.util.Map;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // configure action bar, title, backbutton
        actionBar = getSupportActionBar();
        actionBar.setTitle(getString(R.string.login_button));

        // Initialize firebase auth
        firebaseAuth=FirebaseAuth.getInstance();

        // Initialize firebase user
        firebaseUser=firebaseAuth.getCurrentUser();

        // Check condition
        /*if(firebaseUser != null)
        {
            String email = firebaseUser.getEmail();
            binding.mail.setText(email);
        }*/

        // Initialize sign in client
        gsc= GoogleSignIn.getClient(MainActivity.this, GoogleSignInOptions.DEFAULT_SIGN_IN);
/*
        binding.logout.setOnClickListener(view -> {
            // Sign out from google
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
        });*/

    }
}