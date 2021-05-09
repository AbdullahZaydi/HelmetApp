package com.zayditech.helmetapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sdsmdg.tastytoast.TastyToast;

public class LoginActivity extends AppCompatActivity {
    EditText emailId, password;
    Button btnLogin;
    TextView btnSignUp;
    View _view;
    FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        this.getSupportActionBar().hide();
        btnSignUp = findViewById(R.id.btnSignUp);
        mFirebaseAuth = FirebaseAuth.getInstance();
        emailId = findViewById(R.id.username);
        password = findViewById(R.id.password);
        btnLogin = findViewById(R.id.btnLogin);
        btnSignUp.setOnClickListener(v -> {
            startActivity(new Intent(this, SignUpActivity.class));
        });
        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            super.onBackPressed();
        });
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mFirebaseUser = mFirebaseAuth.getCurrentUser();
                if( mFirebaseUser != null ){
                    TastyToast.makeText(getApplicationContext(), "Logged in Successfully!", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS).show();
                    startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                    finish();
                }
                else{
                    TastyToast.makeText(getApplicationContext(), "Please Login!", TastyToast.LENGTH_SHORT, TastyToast.INFO).show();
                }
            }
        };

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailId.getText().toString();
                String pwd = password.getText().toString();
                if(email.isEmpty()){
                    emailId.setError("Please enter email id");
                    emailId.requestFocus();
                }
                else  if(pwd.isEmpty()){
                    password.setError("Please enter your password");
                    password.requestFocus();
                }
                else  if(email.isEmpty() && pwd.isEmpty()){
                    Snackbar.make(_view, "Please Fill in All the fields!", Snackbar.LENGTH_SHORT).show();
                }
                else  if(!(email.isEmpty() && pwd.isEmpty())){
                    mFirebaseAuth.signInWithEmailAndPassword(email, pwd).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                TastyToast.makeText(getApplicationContext(), "An Anonymous Error Occurred while proceeding your request.", TastyToast.LENGTH_SHORT, TastyToast.ERROR).show();
                            }
                            else{
                                startActivity(new Intent(LoginActivity.this,HomeActivity.class));
                                finish();
                            }
                        }
                    });
                }
                else{
                    TastyToast.makeText(getApplicationContext(), "An Anonymous Error Occurred while proceeding your request.", TastyToast.LENGTH_SHORT, TastyToast.ERROR).show();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }
}