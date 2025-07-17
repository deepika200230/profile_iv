package com.example.myapplication;

import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.StyleSpan;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;public class ProfileActivity extends AppCompatActivity {

    TextView tvName, tvAge, tvDob;
    ImageView imgView;
    RecyclerView recyclerUsers;
    DatabaseHelper db;
    String currentUserId;
    User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.  grey));
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        tvName = findViewById(R.id.tvName);
        tvAge = findViewById(R.id.tvAge);
        tvDob = findViewById(R.id.tvDob);
        imgView = findViewById(R.id.imgProfile);
        recyclerUsers = findViewById(R.id.recyclerUsers);

        db = new DatabaseHelper(this);
        currentUserId = getIntent().getStringExtra("user");
        currentUser = db.getUserById(currentUserId);

        loadProfile(currentUser);
        loadAllUsers();
    }

    private void loadProfile(User user) {
        if (user != null) {
            setBoldLabelText(tvName, "Name: ", user.name);
            setBoldLabelText(tvAge, "Age: ", String.valueOf(user.age));
            setBoldLabelText(tvDob, "DOB: ", user.dob);

            if (user.image != null && !user.image.isEmpty()) {
                Glide.with(this)
                        .load(Uri.parse(user.image))
                        .placeholder(R.drawable.person)
                        .into(imgView);
            }
        } else {
            tvName.setText("User Deleted");
            tvAge.setText("");
            tvDob.setText("");
            imgView.setImageResource(R.drawable.person);
        }
    }

    private void loadAllUsers() {
        List<User> allUsers = db.getAllUsers();
        recyclerUsers.setLayoutManager(new LinearLayoutManager(this));
        recyclerUsers.setAdapter(new UserAdapter(this, allUsers, db, deletedUserId -> {
            if (deletedUserId.equals(currentUserId)) {
                currentUser = null;
                loadProfile(null); // Clear profile if current is deleted
            }
        }));
    }

    private void setBoldLabelText(TextView textView, String label, String value) {
        SpannableString styledText = new SpannableString(label + value);
        styledText.setSpan(new StyleSpan(Typeface.BOLD), 0, label.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(styledText);
    }
}
