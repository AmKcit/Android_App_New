package com.example.android_app_new;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {

    private List<User> userList;
    private OnUserActionListener listener;
    private FirebaseFirestore db;

    public interface OnUserActionListener {
        void onEditUser(User user);
        void onDeleteUser(User user);
    }

    public UserAdapter(List<User> userList, OnUserActionListener listener) {
        this.userList = userList;
        this.listener = listener;
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_admin, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);
        holder.bind(user, listener);
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void updateList(List<User> newList) {
        userList = newList;
        notifyDataSetChanged();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {

        private TextView tvInitials;
        private TextView tvUserEmail;
        private TextView tvUserRole;
        private TextView tvUserStatus;
        private Button btnEdit;
        private Button btnDelete;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvInitials = itemView.findViewById(R.id.tvInitials);
            tvUserEmail = itemView.findViewById(R.id.tvUserEmail);
            tvUserRole = itemView.findViewById(R.id.tvUserRole);
            tvUserStatus = itemView.findViewById(R.id.tvUserStatus);
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }

        public void bind(User user, OnUserActionListener listener) {
            // Set user initials
            String initials = user.getEmail().substring(0, 1).toUpperCase();
            tvInitials.setText(initials);

            // Set user email
            tvUserEmail.setText(user.getEmail());

            // Set role with appropriate color
            tvUserRole.setText(user.getRole().substring(0, 1).toUpperCase() + user.getRole().substring(1));
            if ("admin".equals(user.getRole())) {
                tvUserRole.setBackgroundColor(itemView.getContext().getColor(android.R.color.holo_red_light));
            } else {
                tvUserRole.setBackgroundColor(itemView.getContext().getColor(android.R.color.holo_orange_light));
            }

            // Set status
            boolean isActive = user.isActive();
            tvUserStatus.setText(isActive ? "Active" : "Inactive");
            tvUserStatus.setBackgroundColor(isActive ?
                    itemView.getContext().getColor(android.R.color.holo_green_light) :
                    itemView.getContext().getColor(android.R.color.holo_red_light));

            // Handle edit button
            btnEdit.setOnClickListener(v -> {
                Intent intent = new Intent(itemView.getContext(), EditUserActivity.class);
                intent.putExtra("user", user);
                itemView.getContext().startActivity(intent);
            });

            // Handle delete button
            btnDelete.setOnClickListener(v -> {
                if (listener != null) {
                    listener.onDeleteUser(user);
                }
            });
        }
    }
}




