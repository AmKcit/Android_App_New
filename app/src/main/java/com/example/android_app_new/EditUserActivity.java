package com.example.android_app_new;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EditUserActivity extends AppCompatActivity {

    private TextView tvUserEmail, tvUserEmailHeader, tvStatusLabel, tvCreatedDate, tvLastLogin;
    private Spinner spinnerRole;
    private Switch swUserStatus;
    private Button btnSaveUserEdit, btnCancelUserEdit;
    private FirebaseFirestore db;
    private String userId;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);

        // Initialize views
        tvUserEmail = findViewById(R.id.tvUserEmail);
        tvUserEmailHeader = findViewById(R.id.tvUserEmailHeader);
        tvStatusLabel = findViewById(R.id.tvStatusLabel);
        tvCreatedDate = findViewById(R.id.tvCreatedDate);
        tvLastLogin = findViewById(R.id.tvLastLogin);
        spinnerRole = findViewById(R.id.spinnerRole);
        swUserStatus = findViewById(R.id.swUserStatus);
        btnSaveUserEdit = findViewById(R.id.btnSaveUserEdit);
        btnCancelUserEdit = findViewById(R.id.btnCancelUserEdit);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();

        // Get user data from intent
        currentUser = (User) getIntent().getSerializableExtra("user");
        if (currentUser != null) {
            userId = currentUser.getUid();
            loadUserData();
        }

        // Set up role spinner
        setupRoleSpinner();

        // Set up button listeners
        btnSaveUserEdit.setOnClickListener(v -> saveUserChanges());
        btnCancelUserEdit.setOnClickListener(v -> finish());

        // Update status label when switch changes
        swUserStatus.setOnCheckedChangeListener((buttonView, isChecked) -> {
            tvStatusLabel.setText(isChecked ? "Active" : "Inactive");
        });
    }

    private void setupRoleSpinner() {
        List<String> roles = new ArrayList<>();
        roles.add("user");
        roles.add("admin");

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, roles);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerRole.setAdapter(adapter);

        // Set current role
        if (currentUser != null) {
            int position = roles.indexOf(currentUser.getRole());
            spinnerRole.setSelection(position >= 0 ? position : 0);
        }
    }

    private void loadUserData() {
        tvUserEmail.setText(currentUser.getEmail());
        tvUserEmailHeader.setText(currentUser.getEmail());
        swUserStatus.setChecked(currentUser.isActive());
        tvStatusLabel.setText(currentUser.isActive() ? "Active" : "Inactive");

        // Format dates
        if (currentUser.getCreatedAt() > 0) {
            tvCreatedDate.setText(formatDate(currentUser.getCreatedAt()));
        }

        if (currentUser.getLastLogin() > 0) {
            tvLastLogin.setText(formatDate(currentUser.getLastLogin()));
        }
    }

    private String formatDate(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }

    private void saveUserChanges() {
        String newRole = (String) spinnerRole.getSelectedItem();
        boolean newStatus = swUserStatus.isChecked();

        db.collection("Users").document(userId)
                .update(
                        "role", newRole,
                        "active", newStatus
                )
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(this, "User updated successfully", Toast.LENGTH_SHORT).show();
                    finish();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "Failed to update user: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }
}

