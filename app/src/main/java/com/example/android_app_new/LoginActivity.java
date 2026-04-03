package com.example.android_app_new;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
    private TextView tvGoToSignup, tvForgotPassword;

    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private SessionManager sessionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();
        sessionManager = new SessionManager(this);

        // Check if user is already logged in
        if (sessionManager.isLoggedIn() && sessionManager.isSessionValid()) {
            redirectToMainActivity();
            return;
        }

        setContentView(R.layout.activity_login);

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

            // CHECK IF ADMIN CREDENTIALS
            if (AdminManager.isAdmin(email, pass)) {
                Toast.makeText(this, "Admin Login Successful!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(LoginActivity.this, AdminMainActivity.class);
                startActivity(intent);
                finish();
                return;
            }

            // REGULAR USER LOGIN
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
        Log.d("LoginActivity", "fetchUserRole called with uid: " + uid);

        db.collection("Users").document(uid)
                .get()
                .addOnSuccessListener(document -> {
                    Log.d("LoginActivity", "Firestore query successful");

                    if (document.exists()) {
                        Log.d("LoginActivity", "User document found in Firestore");
                        String role = document.getString("role");
                        FirebaseUser user = mAuth.getCurrentUser();

                        // Create session
                        if (user != null) {
                            sessionManager.createLoginSession(uid, user.getEmail());
                            Log.d("LoginActivity", "Session created for user: " + user.getEmail());
                        }

                        Intent intent;
                        if ("admin".equals(role)) {
                            Log.d("LoginActivity", "User is admin, redirecting to AdminMainActivity");
                            intent = new Intent(this, AdminMainActivity.class);
                        } else {
                            Log.d("LoginActivity", "User is regular user, redirecting to DashboardActivity");
                            intent = new Intent(this, DashboardActivity.class);
                        }
                        startActivity(intent);
                        finish();
                    } else {
                        // User data not found - default to regular user
                        Log.d("LoginActivity", "User document not found in Firestore, defaulting to user role");
                        Toast.makeText(this, "Logging in...", Toast.LENGTH_SHORT).show();
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            sessionManager.createLoginSession(uid, user.getEmail());
                        }
                        startActivity(new Intent(this, DashboardActivity.class));
                        finish();
                    }
                })
                .addOnFailureListener(e -> {
                    // Handle permission denied or connection error
                    String errorMessage = e.getMessage();
                    Log.e("LoginActivity", "Firestore error: " + errorMessage, e);

                    if (errorMessage != null && (errorMessage.contains("permission") || errorMessage.contains("PERMISSION"))) {
                        // Firestore rules issue - log user in anyway as regular user
                        Log.w("LoginActivity", "Permission denied by Firestore rules, allowing login anyway");
                        Toast.makeText(this, "Logging in...", Toast.LENGTH_SHORT).show();
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            sessionManager.createLoginSession(uid, user.getEmail());
                            Log.d("LoginActivity", "Session created despite Firestore permission error");
                        }
                        startActivity(new Intent(this, DashboardActivity.class));
                        finish();
                    } else {
                        Log.e("LoginActivity", "Unexpected error: " + errorMessage);
                        Toast.makeText(this, "Error: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void redirectToMainActivity() {
        // Determine user role or just go to dashboard
        Intent intent = new Intent(this, DashboardActivity.class);
        startActivity(intent);
        finish();
    }
}