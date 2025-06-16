package com.example.sportmaktab.Admin;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sportmaktab.LoginActivity;
import com.example.sportmaktab.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddResultActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private EditText etCompetitionName;
    private EditText etDate;
    private EditText etDescription;
    private ImageView ivResultImage;
    private Button btnPickImage;
    private Button btnAddResult;
    private ProgressBar progressBar;

    private Uri imageUri;
    private Calendar calendar;
    private ApiService apiService;
    private SharedPreferences sharedPreferences;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_result);

        // SharedPreferences dan tokenni olish
        sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        token = sharedPreferences.getString("token", "");

        // Agar token bo'sh bo'lsa, login oynasiga qaytarish
        if (token.isEmpty()) {
            Toast.makeText(this, "Sessiya tugagan. Qayta login qiling", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Initialize UI components
        etCompetitionName = findViewById(R.id.etCompetitionName);
        etDate = findViewById(R.id.etDate);
        etDescription = findViewById(R.id.etDescription);
        ivResultImage = findViewById(R.id.ivResultImage);
        btnPickImage = findViewById(R.id.btnPickImage);
        btnAddResult = findViewById(R.id.btnAddResult);
        progressBar = findViewById(R.id.progressBar);

        // Initialize calendar for date picker
        calendar = Calendar.getInstance();

        // Initialize API service
        apiService = RetrofitClient.getApiService();

        // Set up date picker dialog
        DatePickerDialog.OnDateSetListener date = (view, year, month, dayOfMonth) -> {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDateField();
        };

        // Open date picker when clicking on the date field
        etDate.setOnClickListener(v -> {
            new DatePickerDialog(AddResultActivity.this, date, calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        // Set up image picker button
        btnPickImage.setOnClickListener(v -> openImagePicker());

        // Set up add result button
        btnAddResult.setOnClickListener(v -> {
            if (validateInputs()) {
                addResult();
            }
        });
    }

    private void updateDateField() {
        String format = "yyyy-MM-dd";
        SimpleDateFormat sdf = new SimpleDateFormat(format, Locale.US);
        etDate.setText(sdf.format(calendar.getTime()));
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            ivResultImage.setImageURI(imageUri);
            ivResultImage.setVisibility(View.VISIBLE);
        }
    }

    private boolean validateInputs() {
        if (etCompetitionName.getText().toString().trim().isEmpty()) {
            etCompetitionName.setError("Musobaqa nomini kiriting");
            return false;
        }

        if (etDate.getText().toString().trim().isEmpty()) {
            etDate.setError("Sanani kiriting");
            return false;
        }

        if (etDescription.getText().toString().trim().isEmpty()) {
            etDescription.setError("Tavsifni kiriting");
            return false;
        }

        if (imageUri == null) {
            Toast.makeText(this, "Iltimos, rasmni tanlang", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void addResult() {
        progressBar.setVisibility(View.VISIBLE);

        // Create request bodies
        RequestBody competitionName = RequestBody.create(MediaType.parse("text/plain"), etCompetitionName.getText().toString().trim());
        RequestBody date = RequestBody.create(MediaType.parse("text/plain"), etDate.getText().toString().trim());
        RequestBody description = RequestBody.create(MediaType.parse("text/plain"), etDescription.getText().toString().trim());

        // Prepare image file
        File imageFile = com.example.sportmaktab.utils.FileUtil.getFileFromUri(this, imageUri);
        if (imageFile == null) {
            Toast.makeText(this, "Rasm faylini olishda xatolik", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
            return;
        }

        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), imageFile);
        MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image", imageFile.getName(), requestFile);

        // Make API call using token from SharedPreferences
        Call<Map<String, Object>> call = apiService.addResult("Bearer " + token, competitionName, date, description, imagePart);
        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful()) {
                    Toast.makeText(AddResultActivity.this, "Natija muvaffaqiyatli qo'shildi", Toast.LENGTH_SHORT).show();
                    finish(); // Return to results list
                } else {
                    // Token muddati tugagan bo'lsa (401 xatolik)
                    if (response.code() == 401) {
                        handleUnauthorized();
                        return;
                    }
                    Toast.makeText(AddResultActivity.this, "Natija qo'shishda xatolik: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AddResultActivity.this, "Tarmoq xatosi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Token muddati tugagan yoki noto'g'ri bo'lsa
    private void handleUnauthorized() {
        Toast.makeText(this, "Sessiya tugagan, qaytadan login qiling", Toast.LENGTH_LONG).show();
        // SharedPreferences dan ma'lumotlarni o'chirish
        sharedPreferences.edit().clear().apply();
        // Login oynasiga qaytarish
        startActivity(new Intent(this, LoginActivity.class));
        finish();
    }
}