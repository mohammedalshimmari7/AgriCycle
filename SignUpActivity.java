package com.software.agricycle;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.software.agricycle.databinding.ActivitySignUpBinding;


public class SignUpActivity extends AppCompatActivity {

    // ViewBinding for activity_sign_up.xml
    private ActivitySignUpBinding binding;

    // Firebase
    private FirebaseAuth auth;
    String role = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();

        // "Create account" button
        binding.btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        // "Sign in" text -> go back to SignActivity
        binding.txtGoToSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // assuming we came from SignActivity
            }
        });
    }

    /**
     * Validate input, create account in Firebase Auth,
     * then store extra user info in Realtime Database.
     */
    private void registerUser() {

        // Get values from form
        String fullName = binding.edtFullName.getText().toString().trim();
        String email = binding.edtEmailSignUp.getText().toString().trim();
        String phone = binding.edtPhone.getText().toString().trim();
        String password = binding.edtPasswordSignUp.getText().toString().trim();
        String confirmPassword = binding.edtConfirmPassword.getText().toString().trim();

        // Determine selected role from RadioGroup
        role = "";
        int selectedId = binding.radioGroupRole.getCheckedRadioButtonId();
        if (selectedId != -1) {
            RadioButton selectedRadio = findViewById(selectedId);
            String text = selectedRadio.getText().toString().toLowerCase();
            if (text.contains("buyer")) {
                role = "buyer";
            } else if (text.contains("supplier")) {
                role = "supplier";
            }
        }

        // ---- Simple validation ----
        if (TextUtils.isEmpty(role)) {
            Toast.makeText(this, "Please select Buyer or Supplier.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(fullName)) {
            binding.inputLayoutFullName.setError("Full name is required");
            return;
        } else {
            binding.inputLayoutFullName.setError(null);
        }

        if (TextUtils.isEmpty(email)) {
            binding.inputLayoutEmailSignUp.setError("Email is required");
            return;
        } else {
            binding.inputLayoutEmailSignUp.setError(null);
        }

        if (TextUtils.isEmpty(phone)) {
            binding.inputLayoutPhone.setError("Phone number is required");
            return;
        } else {
            binding.inputLayoutPhone.setError(null);
        }

        if (TextUtils.isEmpty(password)) {
            binding.inputLayoutPasswordSignUp.setError("Password is required");
            return;
        } else {
            binding.inputLayoutPasswordSignUp.setError(null);
        }

        if (password.length() < 6) {
            binding.inputLayoutPasswordSignUp.setError("Password must be at least 6 characters");
            return;
        }

        if (!password.equals(confirmPassword)) {
            binding.inputLayoutConfirmPassword.setError("Passwords do not match");
            return;
        } else {
            binding.inputLayoutConfirmPassword.setError(null);
        }

        // Optional: disable button while processing
        binding.btnCreateAccount.setEnabled(false);

        // ---- Create user in Firebase Auth ----
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        binding.btnCreateAccount.setEnabled(true);

                        if (task.isSuccessful()) {

                            // Get user id
                            String uid = task.getResult().getUser().getUid();

                            // Build user object
                            User user = new User(fullName, email, phone, role);

                            // Save to Realtime Database under "users/uid"
                            FirebaseDatabase.getInstance().getReference("users")
                                    .child(uid)
                                    .setValue(user)
                                    .addOnCompleteListener(saveTask -> {
                                        if (saveTask.isSuccessful()) {
                                            Toast.makeText(SignUpActivity.this,
                                                    "Account created successfully.",
                                                    Toast.LENGTH_SHORT).show();

                                            // Go to main screen or back to sign-in
                                            // Option 1: auto-login -> MainActivity

                                            finish();


                                        } else {
                                            Toast.makeText(SignUpActivity.this,
                                                    "Failed to save user: " +
                                                            saveTask.getException().getMessage(),
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });

                        } else {
                            Toast.makeText(SignUpActivity.this,
                                    "Registration failed: " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
