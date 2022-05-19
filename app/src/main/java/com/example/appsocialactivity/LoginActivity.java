package com.example.appsocialactivity;


import static com.example.appsocialactivity.constants.SharedPrefNames.USER_ID_PREF;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.example.appsocialactivity.databinding.ActivityLoginBinding;
import com.example.appsocialactivity.dbmodel.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class LoginActivity extends AppCompatActivity  {

    // TAGS
    private static final String TAG = "GOOGLE_SING_IN_TAG";

    // ViewBinding
    private ActivityLoginBinding binding;



    private String email = "", password = "";

    // actionbar
    private ActionBar actionBar;

    private GoogleSignInOptions gso;
    private GoogleSignInClient gsc;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private FirebaseFirestore db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // configure action bar, title, backbutton
        actionBar = getSupportActionBar();
        actionBar.setTitle(getString(R.string.login_button));

        // get instance of firestore db
        db = FirebaseFirestore.getInstance();
        // get instance of firebase auth


        GoogleSignInAction();
        FirebaseSignInAction();


    }

    // Firebase Email ile giriş işlemlerinin yapıldığı metod
    private void FirebaseSignInAction() {


        checkUser();

        binding.signUpTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity( new Intent(LoginActivity.this, SignupActivity.class));
            }
        });

        binding.signInButton.setOnClickListener(view -> {
            // validate data
            validateData();
        });
    }

    private void checkUser() {
        // kullanici zaten giris yapmis mi
        // oyleyse dogrudan home activitye yonlendir
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
        if (firebaseUser != null){
            // kullanici onceden giris yapmis
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("signInMethod", 0);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);

            finish();
        }
    }

    private void validateData() {
        // get data
        email = binding.emailEditText.getText().toString().trim();
        password =  binding.passwordEditText.getText().toString().trim();

        // validate data
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
            firebaseEmailLogin();
        }
    }

    private void firebaseEmailLogin() {
        binding.progressBar2.setVisibility(View.VISIBLE);
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                // login basarili
                // kullanici bilgilerini getir
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                String email = firebaseUser.getEmail();
                Toast.makeText(LoginActivity.this, getString(R.string.logged_in_succesfuly) + "\n" + email, Toast.LENGTH_SHORT).show();

                // MapsActivity yonlendirmesi
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                intent.putExtra("signInMethod", 0);// home activity kisminda hangi signin metodu ile islem yapilacagini
                startActivity(intent);                        // belirlemek icin kkullanilir (0 --> firebase email signIn)
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                // login basarisiz, hatayi goster
                binding.progressBar2.setVisibility(View.INVISIBLE);
                Toast.makeText(LoginActivity.this, ""+ e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    // Google ile giriş işlemlerinin yapıldığı metod
    private void GoogleSignInAction(){
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.web_client_id))
                .requestEmail()
                .build();

        gsc = GoogleSignIn.getClient(this, gso);
        binding.googleSignInButton.setOnClickListener(view -> {
            SignInGoogle();
        });
    }

    // Google ile giriş intentinin dispatchlendiği method
    private void SignInGoogle() {
        Intent intent = gsc.getSignInIntent();
        startActivityForResult(intent, 100);
    }

    // Google ile giriş yapma işlemleri sonucunu yakalayan
    //daha sonrasında yapılacak işlemleri konfigüre etmeye yarayan method
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            Task<GoogleSignInAccount> signInAccountTask = GoogleSignIn.getSignedInAccountFromIntent(data);

            if(signInAccountTask.isSuccessful()){
                // When google sign in successful
                // Initialize string
                String s="Google sign in successful";
                // Display Toast
                Toast.makeText(LoginActivity.this, s, Toast.LENGTH_SHORT).show();
                // Initialize sign in account
                try {
                    // Initialize sign in account
                    GoogleSignInAccount googleSignInAccount=signInAccountTask
                            .getResult(ApiException.class);
                    // Check condition
                    if(googleSignInAccount != null)
                    {
                        // When sign in account is not equal to null
                        // Initialize auth credential
                        AuthCredential authCredential= GoogleAuthProvider
                                .getCredential(googleSignInAccount.getIdToken()
                                        ,null);

                        // Check credential
                        firebaseAuth.signInWithCredential(authCredential)
                                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        // Check condition
                                        if(task.isSuccessful())
                                        {
                                            // Check if user logged in to google account for the first time

                                            FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                                            Log.i(TAG, firebaseUser.getUid());
                                            DocumentReference docRef = db.collection("User").document(firebaseUser.getUid());
                                            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                    if (task.isSuccessful()) {
                                                        DocumentSnapshot document = task.getResult();
                                                        if(!document.exists()){

                                                            WriteNewUser(firebaseUser.getUid(), signInAccountTask.getResult().getDisplayName());
                                                        }

                                                        // MapsActivity yonlendirmesi
                                                        Toast.makeText(LoginActivity.this, getString(R.string.logged_in_succesfuly) + "\n" + email, Toast.LENGTH_SHORT).show();
                                                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                        SharedPreferences.Editor editor = getSharedPreferences(USER_ID_PREF, MODE_PRIVATE).edit();
                                                        editor.putString("userID", firebaseUser.getUid());
                                                        Log.i("TAGGEW", firebaseUser.getUid());
                                                        Log.i(TAG, firebaseUser.getUid());
                                                        editor.apply();
                                                        intent.putExtra("signInMethod",1);// home activity kisminda hangi signin metodu ile islem yapilacagini
                                                        startActivity(intent);                       // belirlemek icin kkullanilir (1 --> google signIn)
                                                        finish();

                                                    }
                                                    else{
                                                        Log.d(TAG, "get failed with ", task.getException());
                                                    }
                                                }
                                            });


                                        }
                                        else
                                        {
                                            Toast.makeText(LoginActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });

                    }
                }
                catch (ApiException e)
                {
                    Log.wtf(TAG, String.valueOf(e));
                    Toast.makeText(this, "ERROR", Toast.LENGTH_SHORT).show();
                }
            }


        }
    }

    public void WriteNewUser(String userId, String nameSurname) {

        // save userId to shared pereferences
        SharedPreferences.Editor editor = getSharedPreferences(USER_ID_PREF, MODE_PRIVATE).edit();
        editor.putString("userID",userId);
        editor.apply();
        Log.i(TAG, userId);
        User user = new User(nameSurname);
        user.setEventList(new ArrayList<>());
        user.setInterestsOfUser(new ArrayList<>());
        user.setPhoneNumber("");
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