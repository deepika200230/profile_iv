package com.example.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    EditText etUserId, etPassword;
    Button btnLogin, btnCreate;
    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.whitegrey));
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }


        etUserId = findViewById(R.id.etUserId);
        etPassword = findViewById(R.id.etPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnCreate = findViewById(R.id.btnCreate);

        db = new DatabaseHelper(this);

        btnLogin.setOnClickListener(v -> {
            String userId = etUserId.getText().toString();
            String password = etPassword.getText().toString();
            User user = db.login(userId, password);
            if (user != null) {
                Intent i = new Intent(MainActivity.this, ProfileActivity.class);
                i.putExtra("user", user.userId);
                startActivity(i);
            } else {
                Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
            }
        });

        btnCreate.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CreateUserActivity.class);
            startActivity(intent);
        });
    }
}
