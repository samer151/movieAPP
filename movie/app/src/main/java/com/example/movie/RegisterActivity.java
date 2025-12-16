package com.example.movie;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {

    FirebaseAuth auth;
    TextInputLayout emailFieldLayout, passwordFieldLayout, confirmPasswordFieldLayout;
    TextInputEditText emailField, passwordField, confirmPasswordField;
    Button registerBtn;
    TextView goLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        auth = FirebaseAuth.getInstance();

        // Initialize views
        emailFieldLayout = findViewById(R.id.emailField);
        passwordFieldLayout = findViewById(R.id.passwordField);
        confirmPasswordFieldLayout = findViewById(R.id.confirmPasswordField);

        emailField = (TextInputEditText) emailFieldLayout.getEditText();
        passwordField = (TextInputEditText) passwordFieldLayout.getEditText();
        confirmPasswordField = (TextInputEditText) confirmPasswordFieldLayout.getEditText();

        registerBtn = findViewById(R.id.registerBtn);
        goLogin = findViewById(R.id.goLogin);

        registerBtn.setOnClickListener(v -> register());

        goLogin.setOnClickListener(v -> {
            finish(); // Go back to login activity
        });
    }

    private void register() {
        String email = emailField.getText().toString().trim();
        String password = passwordField.getText().toString().trim();
        String confirmPassword = confirmPasswordField.getText().toString().trim();

        // Validate inputs
        if (TextUtils.isEmpty(email)) {
            emailFieldLayout.setError("Email is required");
            emailField.requestFocus();
            return;
        } else {
            emailFieldLayout.setError(null);
        }

        if (TextUtils.isEmpty(password)) {
            passwordFieldLayout.setError("Password is required");
            passwordField.requestFocus();
            return;
        } else {
            passwordFieldLayout.setError(null);
        }

        if (password.length() < 6) {
            passwordFieldLayout.setError("Password must be at least 6 characters");
            passwordField.requestFocus();
            return;
        }

        if (TextUtils.isEmpty(confirmPassword)) {
            confirmPasswordFieldLayout.setError("Please confirm your password");
            confirmPasswordField.requestFocus();
            return;
        } else {
            confirmPasswordFieldLayout.setError(null);
        }

        if (!password.equals(confirmPassword)) {
            confirmPasswordFieldLayout.setError("Passwords do not match");
            confirmPasswordField.requestFocus();
            return;
        }

        // Show loading or disable button during registration
        registerBtn.setEnabled(false);

        // Create user with Firebase Authentication
        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    registerBtn.setEnabled(true);

                    if (task.isSuccessful()) {
                        Toast.makeText(RegisterActivity.this,
                                "Account created successfully!", Toast.LENGTH_SHORT).show();
                        finish(); // Go back to login activity
                    } else {
                        String errorMessage = task.getException() != null ?
                                task.getException().getMessage() : "Registration failed";

                        // Handle specific Firebase errors
                        if (errorMessage.contains("email address is already in use")) {
                            emailFieldLayout.setError("Email already registered");
                            emailField.requestFocus();
                        } else if (errorMessage.contains("badly formatted")) {
                            emailFieldLayout.setError("Invalid email format");
                            emailField.requestFocus();
                        } else {
                            Toast.makeText(RegisterActivity.this,
                                    "Error: " + errorMessage, Toast.LENGTH_LONG).show();
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    registerBtn.setEnabled(true);
                    Toast.makeText(RegisterActivity.this,
                            "Registration failed: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Clear errors when activity resumes
        emailFieldLayout.setError(null);
        passwordFieldLayout.setError(null);
        confirmPasswordFieldLayout.setError(null);
    }
}