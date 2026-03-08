package com.example.android_app_new;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class AdminMainActivity extends AppCompatActivity implements UserAdapter.OnUserActionListener {

    private RecyclerView rvUsers;
    private Button btnLogoutAdmin, btnRefresh, btnSettings, btnAnalytics;
    private TextView tvTotalUsers, tvActiveUsers, tvAdminCount;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;
    private UserAdapter userAdapter;
    private List<User> userList;
    private SwipeRefreshLayout swipeRefreshLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        // Initialize views
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
        db.collection("Users").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                userList.clear();
                int totalUsers = 0;
                int activeUsers = 0;
                int adminCount = 0;

                for (QueryDocumentSnapshot document : task.getResult()) {
                    String uid = document.getId();
                    String email = document.getString("email");
                    String role = document.getString("role");
                    boolean isActive = document.getBoolean("active") != null && document.getBoolean("active");
                    long lastLogin = document.getLong("lastLogin") != null ? document.getLong("lastLogin") : 0;
                    long createdAt = document.getLong("createdAt") != null ? document.getLong("createdAt") : 0;

                    User user = new User(uid, email, role != null ? role : "user", isActive);
                    user.setLastLogin(lastLogin);
                    user.setCreatedAt(createdAt);
                    userList.add(user);

                    totalUsers++;
                    if (isActive) activeUsers++;
                    if ("admin".equals(role)) adminCount++;
                }

                // Update statistics
                tvTotalUsers.setText(String.valueOf(totalUsers));
                tvActiveUsers.setText(String.valueOf(activeUsers));
                tvAdminCount.setText(String.valueOf(adminCount));

                userAdapter.updateList(userList);
            } else {
                Toast.makeText(this, "Failed to load users", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onEditUser(User user) {
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
        new AlertDialog.Builder(this)
                .setTitle("Delete User")
                .setMessage("Are you sure you want to delete " + user.getEmail() + "?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    db.collection("Users").document(user.getUid()).delete()
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(this, "User deleted successfully", Toast.LENGTH_SHORT).show();
                                loadUsers();
                            })
                            .addOnFailureListener(e -> Toast.makeText(this, "Failed to delete user: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton("No", null)
                .show();
    }

    private void changeUserRole(User user) {
        String[] roles = {"user", "admin"};
        new AlertDialog.Builder(this)
                .setTitle("Select New Role")
                .setItems(roles, (dialog, which) -> {
                    String newRole = roles[which];
                    db.collection("Users").document(user.getUid())
                            .update("role", newRole)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(this, "User role updated to " + newRole, Toast.LENGTH_SHORT).show();
                                loadUsers();
                            })
                            .addOnFailureListener(e -> Toast.makeText(this, "Failed to update role: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                })
                .show();
    }

    private void toggleUserStatus(User user) {
        db.collection("Users").document(user.getUid())
                .update("active", !user.isActive())
                .addOnSuccessListener(aVoid -> {
                    String newStatus = !user.isActive() ? "activated" : "deactivated";
                    Toast.makeText(this, "User " + newStatus, Toast.LENGTH_SHORT).show();
                    loadUsers();
                })
                .addOnFailureListener(e -> Toast.makeText(this, "Failed to update status: " + e.getMessage(), Toast.LENGTH_SHORT).show());
    }

    private void openSettings() {
        startActivity(new Intent(this, AdminSettingsActivity.class));
    }

    private void openAnalytics() {
        startActivity(new Intent(this, AdminAnalyticsActivity.class));
    }

    private void logoutAdmin() {
        mAuth.signOut();
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}






