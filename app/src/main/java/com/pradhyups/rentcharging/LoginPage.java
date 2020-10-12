package com.pradhyups.rentcharging;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LoginPage extends AppCompatActivity {

    private Button mLogIn;
    private Button mRegister;
    private EditText mUerName;
    private EditText mPassword;

    private String mUNameString;
    private String mPaString;

    private FirebaseAuth mFirebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        mLogIn = findViewById(R.id.LogIn);
        mRegister = findViewById(R.id.Register);
        mUerName = findViewById(R.id.User_Name);
        mPassword = findViewById(R.id.Password);

        mFirebaseAuth = FirebaseAuth.getInstance();

        mLogIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mUNameString = mUerName.getText().toString();
                mPaString = mPassword.getText().toString();

                mLogIn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        //authenticate User Info
                        mFirebaseAuth.signInWithEmailAndPassword(mUNameString, mPaString).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(task.isSuccessful()) {
                                    Toast.makeText(LoginPage.this, "Successfully Logged in", Toast.LENGTH_SHORT).show();
                                    Intent mIntent = new Intent(LoginPage.this, MainActivity.class);
                                    startActivity(mIntent);
                                }
                                else {
                                    Toast.makeText(LoginPage.this, "Error" + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            }
        });

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mIntent = new Intent(LoginPage.this, RegistrationPage.class);
                startActivity(mIntent);
            }
        });


        if(mFirebaseAuth.getCurrentUser() != null) {
            Intent mIntent = new Intent(LoginPage.this, MainActivity.class);
            startActivity(mIntent);
        }
    }
}