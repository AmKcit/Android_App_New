package com.example.android_app_new;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvGoToSignup, tvForgotPassword; // Added tvForgotPassword

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        tvGoToSignup = findViewById(R.id.tvSignup);
        tvForgotPassword = findViewById(R.id.tvForgotPassword); // Ensure this ID exists in your XML

        // --- LOGIN LOGIC ---
        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String pass = etPassword.getText().toString().trim();

            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.signInWithEmailAndPassword(email, pass)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null && user.isEmailVerified()) {
                                fetchUserRole(user.getUid());
                            } else {
                                Toast.makeText(this, "Please verify your email before login.", Toast.LENGTH_LONG).show();
                                mAuth.signOut();
                            }
                        } else {
                            String error = task.getException() != null ? task.getException().getMessage() : "Login Failed";
                            Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        // --- FORGOT PASSWORD LOGIC ---
        tvForgotPassword.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                etEmail.setError("Enter your registered email here");
                etEmail.requestFocus();
                return;
            }

            // Firebase built-in method to send reset email
            mAuth.sendPasswordResetEmail(email)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Password reset link sent to your email!", Toast.LENGTH_LONG).show();
                        } else {
                            String error = task.getException() != null ? task.getException().getMessage() : "Failed to send reset email";
                            Toast.makeText(LoginActivity.this, error, Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        tvGoToSignup.setOnClickListener(v ->
                startActivity(new Intent(this, SignupActivity.class)));
    }

    private void fetchUserRole(String uid) {
        db.collection("Users").document(uid)
                .get()
                .addOnSuccessListener(document -> {
                    if (document.exists()) {
                        String role = document.getString("role");
                        Intent intent;
                        if ("admin".equals(role)) {
                            intent = new Intent(this, AdminMainActivity.class);
                        } else {
                            intent = new Intent(this, DashboardActivity.class);
                        }
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(this, "User data not found in database", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Error fetching role: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}