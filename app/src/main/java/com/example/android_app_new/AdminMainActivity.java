package com.example.android_app_new;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import java.util.ArrayList;

public class AdminMainActivity extends AppCompatActivity {

    private ListView lvUsers;
    private Button btnLogoutAdmin;
    private FirebaseFirestore db;
    private ArrayList<String> userList;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_main);

        db = FirebaseFirestore.getInstance();
        lvUsers = findViewById(R.id.lvUsers);
        btnLogoutAdmin = findViewById(R.id.btnLogoutAdmin);
        userList = new ArrayList<>();

        // Fetch Users from Firestore
        db.collection("Users").get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    String email = document.getString("email");
                    String role = document.getString("role");
                    userList.add(email + " (" + role + ")");
                }
                adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, userList);
                lvUsers.setAdapter(adapter);
            } else {
                Toast.makeText(this, "Failed to load users", Toast.LENGTH_SHORT).show();
            }
        });

        btnLogoutAdmin.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        });
    }
}