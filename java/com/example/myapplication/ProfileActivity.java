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
import com.bumptech.glide.Glide;

public class ProfileActivity extends AppCompatActivity {

    TextView tvName, tvAge, tvDob;
    ImageView imgView;
    DatabaseHelper db;

    private void setBoldLabelText(TextView textView, String label, String value) {
        SpannableString styledText = new SpannableString(label + value);
        styledText.setSpan(new StyleSpan(Typeface.BOLD), 0, label.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        textView.setText(styledText);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.grey));
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        tvName = findViewById(R.id.tvName);
        tvAge = findViewById(R.id.tvAge);
        tvDob = findViewById(R.id.tvDob);
        imgView = findViewById(R.id.imgProfile);

        db = new DatabaseHelper(this);
        String userId = getIntent().getStringExtra("user");
        User user = db.getUserById(userId);
        db.printAllUsers();

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
        }
    }
}