package com.example.appsocialactivity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.appsocialactivity.databinding.ActivitySignupBinding;
import com.example.appsocialactivity.databinding.ActivitySignupBinding;
import com.example.appsocialactivity.dbmodel.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class SignupActivity extends AppCompatActivity {

    private static final String TAG = "SIGNUP";

    //ViewBinding
    private ActivitySignupBinding binding;

    private FirebaseFirestore db;
    private FirebaseAuth firebaseAuth;

    // actionbar
    private ActionBar actionBar;

    private String email = "", password = "" , nameSurname = "", userID = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // configure action bar, title, backbutton
        actionBar = getSupportActionBar();
        actionBar.setTitle(getString(R.string.signup_button));
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);

        firebaseAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        binding.signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validateData();
            }
        });
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed(); // geri butonuna basildiginda bir onceki aktivitye gider
        return super.onSupportNavigateUp();
    }

    private void validateData() {
        email= binding.emailEditText.getText().toString().trim();
        password = binding.passwordEditText.getText().toString().trim();

        // email adresi valid degil ise devam etme
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.emailEditText.setError(getString(R.string.invalid_email_error));
        }
        // password girilmemis ise
        else if(TextUtils.isEmpty(password)){
            binding.passwordEditText.setError(getString(R.string.empty_password_error));
        }
        // password uznlugu 6 karakterden kucuk ise
        else if(password.length()< 6){
            binding.passwordEditText.setError(getString(R.string.password_length_error));
        }
        else{
            // data valide edilmistir, signup islemi baslangici
            firebaseEmailSignUp();
        }
    }

    private void firebaseEmailSignUp() {
        // progressi gosteren kod
        binding.progressBar.setVisibility(View.VISIBLE);

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                // signup basarili ise buraya girer
                binding.progressBar.setVisibility(View.INVISIBLE);

                // user info alan kod
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                String email = firebaseUser.getEmail();
                userID = firebaseUser.getUid();
                nameSurname = binding.nameSurnameEditText.getText().toString();
                WriteNewUser(userID, nameSurname);
                Toast.makeText(
                        SignupActivity.this,
                        getString(R.string.account_created_succesfuly) + "\n" + email,
                        Toast.LENGTH_SHORT);

                // HomeActivity yonlendirmesi
                binding.progressBar.setVisibility(View.VISIBLE);
                firebaseAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        // login basarili
                        // kullanici bilgilerini getir
                        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                        String email = firebaseUser.getEmail();

                        Toast.makeText(SignupActivity.this, getString(R.string.logged_in_succesfuly) + "\n" + email, Toast.LENGTH_SHORT).show();

                        // HomeActivity yonlendirmesi
                        Intent intent = new Intent(SignupActivity.this, MapsActivity.class);
                        intent.putExtra("signInMethod", 0);// home activity kisminda hangi signin metodu ile islem yapilacagini
                        startActivity(intent);                        // belirlemek icin kkullanilir (0 --> firebase email signIn)
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // login basarisiz, hatayi goster
                        binding.progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(SignupActivity.this, ""+ e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // signup basarili degil ise buraya girer
                binding.progressBar.setVisibility(View.INVISIBLE);
                Toast.makeText(SignupActivity.this, ""+ e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void WriteNewUser(String userId, String nameSurname) {
        User user = new User(nameSurname);
        db.collection("User").document(userId).set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Log.i(TAG, "basarili");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.i(TAG, "basarisiz");
            }
        });
    }

}