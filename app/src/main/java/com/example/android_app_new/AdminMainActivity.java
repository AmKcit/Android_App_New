package com.example.android_app_new;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminMainActivity extends AppCompatActivity implements UserAdapter.OnUserActionListener {

    private static final String TAG = "AdminMainActivity";
    private RecyclerView rvUsers;
    private Button btnLogoutAdmin, btnRefresh, btnSettings, btnAnalytics;
    private TextView tvTotalUsers, tvActiveUsers, tvAdminCount;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private UserAdapter userAdapter;
    private List<User> userList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        Log.d(TAG, "AdminMainActivity onCreate - Admin logged in");

        // ...existing code...
        rvUsers = findViewById(R.id.rvUsers);
        btnLogoutAdmin = findViewById(R.id.btnLogoutAdmin);
        btnRefresh = findViewById(R.id.btnRefresh);
        btnSettings = findViewById(R.id.btnSettings);
        btnAnalytics = findViewById(R.id.btnAnalytics);
        tvTotalUsers = findViewById(R.id.tvTotalUsers);
        tvActiveUsers = findViewById(R.id.tvActiveUsers);
        tvAdminCount = findViewById(R.id.tvAdminCount);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        // Initialize user list and adapter
        userList = new ArrayList<>();
        userAdapter = new UserAdapter(userList, this);

        // Set up RecyclerView
        rvUsers.setLayoutManager(new LinearLayoutManager(this));
        rvUsers.setAdapter(userAdapter);

        // Load users from Firestore
        loadUsers();

        // Set up button listeners
        btnLogoutAdmin.setOnClickListener(v -> logoutAdmin());
        btnRefresh.setOnClickListener(v -> loadUsers());
        btnSettings.setOnClickListener(v -> openSettings());
        btnAnalytics.setOnClickListener(v -> openAnalytics());
    }

    private void loadUsers() {
        Log.d(TAG, "loadUsers() called - Fetching users from Firestore");
        db.collection("Users").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Log.d(TAG, "Firestore query successful");
                userList.clear();
                int totalUsers = 0;
                int activeUsers = 0;
                int adminCount = 0;

                for (QueryDocumentSnapshot document : task.getResult()) {
                    try {
                        String uid = document.getId();
                        String email = document.getString("email");
                        String role = document.getString("role");
                        Boolean activeObj = document.getBoolean("active");
                        boolean isActive = activeObj != null ? activeObj : false;
                        long lastLogin = document.getLong("lastLogin") != null ? document.getLong("lastLogin") : 0;
                        long createdAt = document.getLong("createdAt") != null ? document.getLong("createdAt") : 0;

                        Log.d(TAG, "User: " + email + ", Role: " + role + ", Active: " + isActive);

                        User user = new User(uid, email, role != null ? role : "user", isActive);
                        user.setLastLogin(lastLogin);
                        user.setCreatedAt(createdAt);
                        userList.add(user);

                        totalUsers++;
                        if (isActive) activeUsers++;
                        if ("admin".equals(role)) adminCount++;
                    } catch (Exception e) {
                        Log.e(TAG, "Error parsing user: " + e.getMessage());
                    }
                }

                Log.d(TAG, "Loaded: " + totalUsers + " users");
                tvTotalUsers.setText(String.valueOf(totalUsers));
                tvActiveUsers.setText(String.valueOf(activeUsers));
                tvAdminCount.setText(String.valueOf(adminCount));
                userAdapter.updateList(userList);

                if (totalUsers == 0) {
                    Toast.makeText(this, "No users found", Toast.LENGTH_SHORT).show();
                }
            } else {
                Log.e(TAG, "Query failed: " + task.getException().getMessage());
                Toast.makeText(this, "Error: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onEditUser(User user) {
        Log.d(TAG, "onEditUser() called for: " + user.getEmail());
        // Show options dialog
        String[] options = {"Change Role", "Toggle Status", "Cancel"};
        new AlertDialog.Builder(this)
                .setTitle("Edit User: " + user.getEmail())
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        changeUserRole(user);
                    } else if (which == 1) {
                        toggleUserStatus(user);
                    }
                })
                .show();
    }

    @Override
    public void onDeleteUser(User user) {
        Log.d(TAG, "onDeleteUser() called for: " + user.getEmail());
        new AlertDialog.Builder(this)
                .setTitle("Delete User Account")
                .setMessage("Are you sure you want to permanently delete " + user.getEmail() + "?\n\nThis action:\n• Removes user from database\n• Cannot be undone\n• Email can be reused for signup")
                .setPositiveButton("Delete Permanently", (dialog, which) -> {
                    deleteUserCompletely(user);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void deleteUserCompletely(User user) {
        Log.d(TAG, "deleteUserCompletely() called for: " + user.getEmail());

        // Step 1: Delete from Firestore Users collection
        db.collection("Users").document(user.getUid()).delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "User deleted from Users collection: " + user.getEmail());

                    // Step 2: Delete any user-related data
                    deleteUserRelatedData(user.getUid());

                    // Step 3: Show success message and refresh
                    Toast.makeText(this, "User " + user.getEmail() + " deleted.\nEmail can be reused.", Toast.LENGTH_LONG).show();
                    loadUsers();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to delete user: " + e.getMessage());
                    Toast.makeText(this, "Failed to delete: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void deleteUserRelatedData(String uid) {
        Log.d(TAG, "deleteUserRelatedData() called for uid: " + uid);

        // Delete user's AQI data if any
        db.collection("AQIData")
                .whereEqualTo("userId", uid)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        document.getReference().delete();
                        Log.d(TAG, "Deleted AQI data: " + document.getId());
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error deleting AQI data: " + e.getMessage());
                });
    }

    private void changeUserRole(User user) {
        Log.d(TAG, "changeUserRole() called for: " + user.getEmail());

        String[] roles = {"user", "admin"};
        new AlertDialog.Builder(this)
                .setTitle("Select New Role")
                .setItems(roles, (dialog, which) -> {
                    String newRole = roles[which];
                    Log.d(TAG, "Changing role to: " + newRole);

                    db.collection("Users").document(user.getUid())
                            .update("role", newRole)
                            .addOnSuccessListener(aVoid -> {
                                Log.d(TAG, "Role updated successfully");
                                Toast.makeText(this, "Role updated to " + newRole, Toast.LENGTH_SHORT).show();
                                loadUsers();
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Failed to update role: " + e.getMessage());
                                Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .show();
    }

    private void toggleUserStatus(User user) {
        Log.d(TAG, "toggleUserStatus() called for: " + user.getEmail());

        boolean newStatus = !user.isActive();
        Log.d(TAG, "New status: " + newStatus);

        db.collection("Users").document(user.getUid())
                .update("active", newStatus)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Status updated successfully");
                    String newStatusStr = newStatus ? "activated" : "deactivated";
                    Toast.makeText(this, "User " + newStatusStr, Toast.LENGTH_SHORT).show();
                    loadUsers();
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Failed to update status: " + e.getMessage());
                    Toast.makeText(this, "Failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void openSettings() {
        Log.d(TAG, "openSettings() called");
        startActivity(new Intent(this, AdminSettingsActivity.class));
    }

    private void openAnalytics() {
        Log.d(TAG, "openAnalytics() called");
        startActivity(new Intent(this, AdminAnalyticsActivity.class));
    }

    private void logoutAdmin() {
        Log.d(TAG, "logoutAdmin() called");
        mAuth.signOut();
        Toast.makeText(this, "Logged out", Toast.LENGTH_SHORT).show();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}






