package com.example.android_app_new;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin;
    private TextView tvGoToSignup;
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
        tvGoToSignup = findViewById(R.id.tvGoToSignup);

        btnLogin.setOnClickListener(v -> {
            String email = etEmail.getText().toString().trim();
            String pass = etPassword.getText().toString().trim();

            if (email.isEmpty() || pass.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    fetchUserRole(mAuth.getCurrentUser().getUid());
                } else {
                    // Providing the actual error message helps debugging
                    String error = task.getException() != null ? task.getException().getMessage() : "Login Failed";
                    Toast.makeText(this, error, Toast.LENGTH_SHORT).show();
                }
            });
        });

        tvGoToSignup.setOnClickListener(v -> startActivity(new Intent(this, SignupActivity.class)));
    }

    private void fetchUserRole(String uid) {
        db.collection("Users").document(uid).get().addOnSuccessListener(document -> {
            if (document.exists()) {
                String role = document.getString("role");

                Intent intent;
                if ("admin".equals(role)) {
                    intent = new Intent(this, AdminMainActivity.class);
                } else {
                    // CHANGED: Redirect regular users to the new Dashboard
                    startActivity(new Intent(this, DashboardActivity.class));
                    finish();
                    return;
                }

                startActivity(intent);
                finish(); // Prevents user from going back to login screen
            } else {
                Toast.makeText(this, "User data not found in database", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(e -> {
            Toast.makeText(this, "Error fetching role: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        });
    }
}