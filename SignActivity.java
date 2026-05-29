package com.software.agricycle;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.software.agricycle.databinding.ActivitySignBinding;

public class SignActivity extends AppCompatActivity {

    // ViewBinding object for activity_sign.xml
    ActivitySignBinding binding;

    // Firebase Authentication instance
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize FirebaseAuth
        auth = FirebaseAuth.getInstance();



        // --- Sign In button click ---
        binding.btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String email = binding.edtEmail.getText().toString().trim();
                String password = binding.edtPassword.getText().toString().trim();

                if (!email.isEmpty() && !password.isEmpty()) {

                    // Sign in with Firebase using email & password
                    auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Login success -> go to main dashboard
                                        Intent i = new Intent(SignActivity.this, MainActivity.class);
                                        startActivity(i);
                                        finish();
                                    } else {
                                        // Login failed
                                        Toast.makeText(SignActivity.this,
                                                "Login failed: " + task.getException().getMessage(),
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                } else {
                    Toast.makeText(SignActivity.this,
                            "Enter correct Email and Password!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        // --- "Sign up" text click -> go to SignupActivity ---
        binding.txtGoToSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });

        // --- "Forgot password?" text click ---
        binding.txtForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = binding.edtEmail.getText().toString().trim();
                String password = binding.edtPassword.getText().toString().trim();

                if (!email.isEmpty()) {
                    Toast.makeText(SignActivity.this,
                            "Password request sent!",
                            Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(SignActivity.this,
                            "Enter your Email!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        // If user is already logged in, skip sign-in screen
        if (auth.getCurrentUser() != null) {
            startActivity(new Intent(SignActivity.this, MainActivity.class));
            finish();
        }
    }
}
