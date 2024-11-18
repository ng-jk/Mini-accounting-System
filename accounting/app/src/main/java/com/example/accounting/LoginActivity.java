package com.example.accounting;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Firebase;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextEmail, editTextPassword;
    private Button buttonLogin, CloseButton;
    private TextView forgetPassword;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    // SharedPreferences to keep user logged in
    public SharedPreferences sharedPreferencesLogin;
    public static final String PREF_NAME = "login_pref";
    public static final String KEY_EMAIL = "email";
    public static final String KEY_IS_LOGGED_IN = "is_logged_in";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_view);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        editTextEmail = findViewById(R.id.email);
        editTextPassword = findViewById(R.id.password);
        buttonLogin = findViewById(R.id.loginButton);
        forgetPassword = findViewById(R.id.forgetPassword);
        CloseButton = findViewById(R.id.closeButton);

        sharedPreferencesLogin = getSharedPreferences(PREF_NAME, MODE_PRIVATE);

        // Set onClick listener for Login button
        buttonLogin.setOnClickListener(v -> handleLogin());
        forgetPassword.setOnClickListener(v -> navigateToResetPassword());
        CloseButton.setOnClickListener(v -> navigateToMainSettingActivity());

    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Toast.makeText(getBaseContext(),"You Already Login", Toast.LENGTH_SHORT).show();
            SharedPreferences.Editor editor = sharedPreferencesLogin.edit();
            editor.putBoolean(KEY_IS_LOGGED_IN, true);
            editor.putString(KEY_EMAIL,mAuth.getCurrentUser().getEmail());
            editor.apply();
            navigateToMainSettingActivity();
        }
    }

    private void handleLogin() {
        String email = editTextEmail.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        // Validate input
        if (TextUtils.isEmpty(email)) {
            editTextEmail.setError("Email is required");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            editTextPassword.setError("Password is required");
            return;
        }

        // Attempt to sign in the user
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success
                        FirebaseUser user = mAuth.getCurrentUser();

                        if (user != null && user.isEmailVerified()) {
                            Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();
                            Toast.makeText(LoginActivity.this, "the account email already verified", Toast.LENGTH_LONG).show();
                            navigateToMainSettingActivity();
                        }else{
                            user.sendEmailVerification().addOnCompleteListener(task1 -> {
                                navigateToMainSettingActivity();
                            });
                        }
                        SharedPreferences.Editor editor = sharedPreferencesLogin.edit();
                        editor.putBoolean(KEY_IS_LOGGED_IN, true);
                        editor.putString(KEY_EMAIL, email);
                        editor.apply();
                    } else {
                        if (task.getException() != null && task.getException().getMessage().contains("There is no user record")) {
                            // User does not exist, proceed to create a new account
                            createUser(email, password);
                        } else {
                            // Other sign-in errors
                            Toast.makeText(LoginActivity.this,
                                    "Authentication Failed: " + task.getException().getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void createUser(String email, String password) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Account creation success
                        FirebaseUser user = mAuth.getCurrentUser();
                        user.sendEmailVerification().addOnCompleteListener(task1 -> {
                            if(task1.isSuccessful()) {
                                Toast.makeText(LoginActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                                SharedPreferences.Editor editor = sharedPreferencesLogin.edit();
                                editor.putBoolean(KEY_IS_LOGGED_IN, true);
                                editor.putString(KEY_EMAIL, email);
                                editor.apply();
                                storeUserInfo(email,password);
                                navigateToMainSettingActivity();
                            }else{
                                user.delete();
                            }
                        });
                    } else {
                        // If registration fails, display a message to the user.
                        Toast.makeText(LoginActivity.this,
                                "Registration Failed: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void storeUserInfo(String email, String thePassword) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        String uid = user.getUid();
        int s=4;
        StringBuffer result= new StringBuffer();
        for (int i=0; i<thePassword.length(); i++){
                if (Character.isUpperCase(thePassword.charAt(i)))
                {
                    char ch = (char)(((int)thePassword.charAt(i) + s - 65) % 26 + 65);
                    result.append(ch);
                }
                else
                {
                    char ch = (char)(((int)thePassword.charAt(i) + s - 97) % 26 + 97);
                    result.append(ch);
                }
            }
        String password = result.toString();

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("email", email);
        userMap.put("password",password);
        userMap.put("uid", uid);

        DocumentReference documentReference = db.collection("user").document(uid);
        documentReference.set(userMap).addOnSuccessListener(aVoid -> {
                    // Successfully written to Firestore
                    Toast.makeText(LoginActivity.this, "User information saved", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    // Failed to write to Firestore
                    Toast.makeText(LoginActivity.this, "Failed to save user info: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.d("storeTag", "storeUserInfo: "+e.getMessage());
                });
    }

    private void navigateToMainSettingActivity() {
        Intent intent = new Intent(LoginActivity.this, MainSettingActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
    private void navigateToResetPassword(){
        Intent intent = new Intent(LoginActivity.this, LoginResetPasswordActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}
