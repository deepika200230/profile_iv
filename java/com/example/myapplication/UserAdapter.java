package com.example.myapplication;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;
public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private Context context;
    private List<User> userList;
    private DatabaseHelper db;
    private OnUserDeletedListener deleteListener;

    public interface OnUserDeletedListener {
        void onUserDeleted(String deletedUserId);
    }

    public UserAdapter(Context context, List<User> userList, DatabaseHelper db, OnUserDeletedListener listener) {
        this.context = context;
        this.userList = userList;
        this.db = db;
        this.deleteListener = listener;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user_profile, parent, false);
        return new UserViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = userList.get(position);

        holder.tvUserName.setText(user.name);
        holder.tvUserAge.setText("Age: " + user.age);
        holder.tvUserDob.setText("DOB: " + user.dob);

        Glide.with(context)
                .load(Uri.parse(user.image))
                .placeholder(R.drawable.person)
                .into(holder.imgUserProfile);

        holder.btnDelete.setOnClickListener(v -> {
            if (db.deleteUser(user.userId)) {
                userList.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, userList.size());
                if (deleteListener != null) {
                    deleteListener.onUserDeleted(user.userId);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView tvUserName, tvUserAge, tvUserDob;
        ImageView imgUserProfile, btnDelete;

        UserViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserName = itemView.findViewById(R.id.tvUserName);
            tvUserAge = itemView.findViewById(R.id.tvUserAge);
            tvUserDob = itemView.findViewById(R.id.tvUserDob);
            imgUserProfile = itemView.findViewById(R.id.imgUserProfile);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}
