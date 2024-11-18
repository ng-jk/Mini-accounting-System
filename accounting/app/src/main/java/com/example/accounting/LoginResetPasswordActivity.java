package com.example.accounting;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class LoginResetPasswordActivity extends AppCompatActivity {

        // UI Components
        private EditText Email;
        private Button loginButton,SkipButton;

        // Firebase Auth Instance
        private FirebaseAuth mAuth;
    @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.reset_password_frag);

            // Initialize Firebase Auth
            mAuth = FirebaseAuth.getInstance();

            // Initialize UI Components
            Email = findViewById(R.id.email);
            loginButton = findViewById(R.id.loginButton);
            SkipButton = findViewById(R.id.closeButton);

            // Set onClick listener for Send Reset Email button
            loginButton.setOnClickListener(v -> sendResetEmail());

            // Set onClick listener for Back to Login TextView
            SkipButton.setOnClickListener(v -> {
                Intent intent = new Intent(LoginResetPasswordActivity.this, MainSettingActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                finish();
            });
        }

        private void sendResetEmail() {

            String email = Email.getText().toString().trim();

            // Validate email input
            if (TextUtils.isEmpty(email)) {
                Email.setError("Email is required");
                return;
            }

            // Send password reset email
            mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginResetPasswordActivity.this, "Password reset email sent to " + email, Toast.LENGTH_LONG).show();

                            // Optionally, navigate back to LoginActivity
                            Intent intent = new Intent(LoginResetPasswordActivity.this, LoginActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            startActivity(intent);
                            finish();
                        } else {
                            // Handle failures
                            String errorMessage = task.getException() != null ?
                                    task.getException().getMessage() :
                                    "Failed to send reset email.";
                            Toast.makeText(LoginResetPasswordActivity.this,
                                    "Error: " + errorMessage,
                                    Toast.LENGTH_LONG).show();
                        }
                    });
        }
    }
