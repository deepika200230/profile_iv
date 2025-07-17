package com.example.myapplication;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import com.google.android.material.snackbar.Snackbar;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CreateUserActivity extends AppCompatActivity {

    EditText etUserId, etPassword, etName, etAge, etDob;
    ImageView imgProfile;
    Button btnRegister;

    private @Nullable Uri photoUri = null;
    DatabaseHelper db;

    private final ActivityResultLauncher<Intent> pickOrCapture =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK) {
                            Uri uri = null;
                            Intent data = result.getData();
                            if (data != null && data.getData() != null) {
                                Uri selectedUri = data.getData();
                                uri = copyImageToInternalStorage(selectedUri);
                                photoUri = uri;
                            } else if (photoUri != null) {
                                uri = photoUri;
                            }
                            if (uri != null) imgProfile.setImageURI(uri);
                        }
                    });

    private Uri copyImageToInternalStorage(Uri srcUri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(srcUri);
            if (inputStream == null) return null;

            File file = new File(getFilesDir(), "profile_" + System.currentTimeMillis() + ".jpg");
            OutputStream outputStream = new FileOutputStream(file);

            byte[] buffer = new byte[4096];
            int bytesRead;
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }

            inputStream.close();
            outputStream.close();

            return Uri.fromFile(file);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private File createTempImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File storageDir = getCacheDir();
        return File.createTempFile("IMG_" + timeStamp, ".jpg", storageDir);
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();


        calendar.add(Calendar.YEAR, -18);
        int maxYear = calendar.get(Calendar.YEAR);
        int maxMonth = calendar.get(Calendar.MONTH);
        int maxDay = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dpd = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            String dob = String.format(Locale.getDefault(), "%02d/%02d/%04d", dayOfMonth, month + 1, year);
            int calculatedAge = calculateAge(year, month, dayOfMonth);

            if (calculatedAge < 18) {
                Snackbar.make(etDob, "You must be at least 18 years old", Snackbar.LENGTH_LONG).show();
                etDob.setText("");
                etAge.setText("");
            } else {
                etDob.setText(dob);
                etAge.setText(String.valueOf(calculatedAge));
            }

        }, maxYear, maxMonth, maxDay);


        dpd.getDatePicker().setMaxDate(calendar.getTimeInMillis());

        dpd.show();
    }


    private int calculateAge(int year, int month, int day) {
        Calendar dob = Calendar.getInstance();
        dob.set(year, month, day);
        Calendar today = Calendar.getInstance();

        int age = today.get(Calendar.YEAR) - dob.get(Calendar.YEAR);
        if (today.get(Calendar.DAY_OF_YEAR) < dob.get(Calendar.DAY_OF_YEAR)) {
            age--;
        }
        return age;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.  grey));
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

        etUserId  = findViewById(R.id.etUserId);
        etPassword = findViewById(R.id.etPassword);
        etName    = findViewById(R.id.etName);
        etAge     = findViewById(R.id.etAge);
        etDob     = findViewById(R.id.etDob);
        imgProfile = findViewById(R.id.imgProfile);
        btnRegister = findViewById(R.id.btnRegister);
        db = new DatabaseHelper(this);

        etDob.setFocusable(false);
        etDob.setOnClickListener(v -> showDatePickerDialog());

        imgProfile.setOnClickListener(v -> {
            try {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).setType("image/*");
                File photoFile = createTempImageFile();
                photoUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", photoFile);

                Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        .putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                        .addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);

                Intent chooser = Intent.createChooser(galleryIntent, "Select Profile Picture");
                chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{cameraIntent});
                pickOrCapture.launch(chooser);

            } catch (IOException e) {
                Snackbar.make(imgProfile, "Could not open camera", Snackbar.LENGTH_LONG).show();
            }
        });

        btnRegister.setOnClickListener(v -> {
            String errorMsg = getValidationErrors();
            if (!errorMsg.isEmpty()) {
                Snackbar.make(btnRegister, errorMsg, Snackbar.LENGTH_LONG).show();
                return;
            }

            User user = new User();
            user.userId = etUserId.getText().toString().trim();
            user.password = etPassword.getText().toString().trim();
            user.name = etName.getText().toString().trim();
            user.age = Integer.parseInt(etAge.getText().toString().trim());
            user.dob = etDob.getText().toString().trim();
            user.image = photoUri != null ? photoUri.toString() : "";

            if (db.insertUser(user)) {
                Snackbar.make(btnRegister, "User Created!", Snackbar.LENGTH_SHORT).show();
                db.printAllUsers();
                finish();
            } else {
                Snackbar.make(btnRegister, "Failed to create user", Snackbar.LENGTH_LONG).show();
            }
        });
    }

    private String getValidationErrors() {
        if (etUserId.getText().toString().trim().isEmpty()) return "Enter User ID";
        if (etPassword.getText().toString().trim().isEmpty()) return "Enter Password";
        if (etName.getText().toString().trim().isEmpty()) return "Enter Name";
        if (etDob.getText().toString().trim().isEmpty()) return "Select DOB";
        if (etAge.getText().toString().trim().isEmpty()) return "Age not calculated";
        int age = Integer.parseInt(etAge.getText().toString().trim());
        if (age < 18) return "User must be at least 18 years old";
        if (photoUri == null) return "Select Profile Image";
        return "";
    }
}
