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
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.sdsmdg.tastytoast.TastyToast;

public class SignUpActivity extends AppCompatActivity {
    EditText emailId, password, cnfrmPassword;
    Button btnSignUp;
    TextView btnLogin;
    FirebaseAuth mFirebaseAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        this.getSupportActionBar().hide();
        mFirebaseAuth = FirebaseAuth.getInstance();
        emailId = findViewById(R.id.username);
        password = findViewById(R.id.password);
        cnfrmPassword = findViewById(R.id.cnfrmPassword);
        btnSignUp = findViewById(R.id.btnSignUp);
        btnLogin = findViewById(R.id.btnSignIn);

        btnLogin.setOnClickListener(v -> {
            startActivity(new Intent(this, LoginActivity.class));
        });

        ImageView btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            super.onBackPressed();
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailId.getText().toString();
                String pwd = password.getText().toString();
                String confirmPassword = cnfrmPassword.getText().toString();

                if(!pwd.equals(confirmPassword)) {
                    cnfrmPassword.setError("Password Doesn't Match!");
                    cnfrmPassword.requestFocus();
                    return;
                }

                if(email.isEmpty()){
                    emailId.setError("Please enter email id");
                    emailId.requestFocus();
                }
                else  if(pwd.isEmpty()){
                    password.setError("Please enter your password");
                    password.requestFocus();
                }
                else  if(email.isEmpty() && pwd.isEmpty()){
                    TastyToast.makeText(getApplicationContext(), "All Fields Are Empty!", TastyToast.LENGTH_SHORT, TastyToast.ERROR).show();
                }
                else  if(!(email.isEmpty() && pwd.isEmpty())){
                    mFirebaseAuth.createUserWithEmailAndPassword(email, pwd).addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                TastyToast.makeText(getApplicationContext(), "An Anonymous Error Occurred while proceeding your request.", TastyToast.LENGTH_SHORT, TastyToast.ERROR).show();
                            }
                            else {
                                TastyToast.makeText(getApplicationContext(), "Account Created Successfully!", TastyToast.LENGTH_SHORT, TastyToast.SUCCESS).show();
                                startActivity(new Intent(SignUpActivity.this,HomeActivity.class));
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
}