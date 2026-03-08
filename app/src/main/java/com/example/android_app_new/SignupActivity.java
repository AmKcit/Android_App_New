package com.example.android_app_new;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.HashMap;
import java.util.Map;

public class SignupActivity extends AppCompatActivity {

    private TextInputEditText etEmail, etPassword, etConfirmPassword;
    private MaterialCheckBox cbIsAdmin;
    private MaterialButton btnSignup;
    private TextView tvGoToLogin, tvPasswordStrength, tvPasswordMatch;
    private LinearLayout passwordMatchContainer;
    private ImageView ivPasswordMatch;
    private View strengthBar1, strengthBar2, strengthBar3;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialize views
        etEmail = findViewById(R.id.etEmail);
        etPassword = findViewById(R.id.etPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        cbIsAdmin = findViewById(R.id.cbIsAdmin);
        btnSignup = findViewById(R.id.btnSignup);
        tvGoToLogin = findViewById(R.id.tvGoToLogin);
        tvPasswordStrength = findViewById(R.id.tvPasswordStrength);
        tvPasswordMatch = findViewById(R.id.tvPasswordMatch);
        passwordMatchContainer = findViewById(R.id.passwordMatchContainer);
        ivPasswordMatch = findViewById(R.id.ivPasswordMatch);
        strengthBar1 = findViewById(R.id.strengthBar1);
        strengthBar2 = findViewById(R.id.strengthBar2);
        strengthBar3 = findViewById(R.id.strengthBar3);

        // Password strength listener
        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                updatePasswordStrength(s.toString());
                checkPasswordMatch();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Confirm password listener
        etConfirmPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                checkPasswordMatch();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        // Signup button click listener
        btnSignup.setOnClickListener(v -> registerUser());

        // Go to login
        tvGoToLogin.setOnClickListener(v -> finish());
    }

    private void updatePasswordStrength(String password) {
        int strength = calculatePasswordStrength(password);

        // Reset all bars
        strengthBar1.setBackgroundColor(getResources().getColor(android.R.color.darker_gray, getTheme()));
        strengthBar2.setBackgroundColor(getResources().getColor(android.R.color.darker_gray, getTheme()));
        strengthBar3.setBackgroundColor(getResources().getColor(android.R.color.darker_gray, getTheme()));

        if (strength == 0) {
            tvPasswordStrength.setText("Password strength: Weak");
            tvPasswordStrength.setTextColor(getResources().getColor(android.R.color.holo_red_light, getTheme()));
        } else if (strength == 1) {
            strengthBar1.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_light, getTheme()));
            tvPasswordStrength.setText("Password strength: Fair");
            tvPasswordStrength.setTextColor(getResources().getColor(android.R.color.holo_orange_light, getTheme()));
        } else if (strength == 2) {
            strengthBar1.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_light, getTheme()));
            strengthBar2.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_light, getTheme()));
            tvPasswordStrength.setText("Password strength: Good");
            tvPasswordStrength.setTextColor(getResources().getColor(android.R.color.holo_green_light, getTheme()));
        } else {
            strengthBar1.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light, getTheme()));
            strengthBar2.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light, getTheme()));
            strengthBar3.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light, getTheme()));
            tvPasswordStrength.setText("Password strength: Strong");
            tvPasswordStrength.setTextColor(getResources().getColor(android.R.color.holo_green_light, getTheme()));
        }
    }

    private int calculatePasswordStrength(String password) {
        if (password.length() < 6) return 0;

        boolean hasUpperCase = password.matches(".*[A-Z].*");
        boolean hasLowerCase = password.matches(".*[a-z].*");
        boolean hasNumbers = password.matches(".*\\d.*");
        boolean hasSpecialChar = password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*");

        int strength = 0;
        if (hasUpperCase) strength++;
        if (hasNumbers) strength++;
        if (hasSpecialChar) strength++;

        if (password.length() >= 12) strength++;

        return Math.min(strength, 3);
    }

    private void checkPasswordMatch() {
        String password = etPassword.getText().toString();
        String confirmPassword = etConfirmPassword.getText().toString();

        if (confirmPassword.isEmpty()) {
            passwordMatchContainer.setVisibility(View.GONE);
            return;
        }

        if (password.equals(confirmPassword)) {
            passwordMatchContainer.setVisibility(View.VISIBLE);
            ivPasswordMatch.setImageResource(android.R.drawable.ic_menu_view);
            ivPasswordMatch.setColorFilter(getResources().getColor(android.R.color.holo_green_light, getTheme()));
            tvPasswordMatch.setText("Passwords match ✓");
            tvPasswordMatch.setTextColor(getResources().getColor(android.R.color.holo_green_light, getTheme()));
        } else {
            passwordMatchContainer.setVisibility(View.VISIBLE);
            ivPasswordMatch.setImageResource(android.R.drawable.ic_menu_close_clear_cancel);
            ivPasswordMatch.setColorFilter(getResources().getColor(android.R.color.holo_red_light, getTheme()));
            tvPasswordMatch.setText("Passwords do not match");
            tvPasswordMatch.setTextColor(getResources().getColor(android.R.color.holo_red_light, getTheme()));
        }
    }

    private void registerUser() {
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();
        String role = cbIsAdmin.isChecked() ? "admin" : "user";

        // Validation
        if (email.isEmpty()) {
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String uid = mAuth.getCurrentUser().getUid();
                Map<String, Object> user = new HashMap<>();
                user.put("email", email);
                user.put("role", role);

                db.collection("Users").document(uid).set(user).addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "Signup Success!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(this, LoginActivity.class));
                    finish();
                }).addOnFailureListener(e -> {
                    Toast.makeText(this, "Error saving user data: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            } else {
                String errorMsg = task.getException() != null ? task.getException().getMessage() : "Signup failed";
                Toast.makeText(this, "Error: " + errorMsg, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
