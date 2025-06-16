package com.example.sportmaktab.Admin;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.example.sportmaktab.LoginActivity;
import com.example.sportmaktab.R;

import java.io.File;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddSportTypeActivity extends AppCompatActivity {

    private EditText etSportName, etSportDescription;
    private ImageView ivSportImage;
    private Button btnSelectImage, btnSaveSport, btnCancel;
    private Toolbar toolbar;

    private ApiService apiService;
    private SharedPreferences sharedPreferences;
    private String token;
    private Uri selectedImageUri;
    private static final int REQUEST_STORAGE_PERMISSION = 101;
    private static final int REQUEST_IMAGE_PICK = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_sport_type);

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

        // UI elementlarini initialize qilish
        initViews();

        // RetrofitClient orqali ApiService ni olish
        apiService = RetrofitClient.getApiService();

        // Button listener'larni o'rnatish
        setupListeners();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Sport turini qo'shish");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        etSportName = findViewById(R.id.etSportName);
        etSportDescription = findViewById(R.id.etSportDescription);
        ivSportImage = findViewById(R.id.ivSportImage);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        btnSaveSport = findViewById(R.id.btnSaveSport);
        btnCancel = findViewById(R.id.btnCancel);
    }

    private void setupListeners() {
        btnSelectImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkStoragePermissionAndPickImage();
            }
        });

        btnSaveSport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveSportType();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void checkStoragePermissionAndPickImage() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    REQUEST_STORAGE_PERMISSION);
        } else {
            pickImageFromGallery();
        }
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_PICK);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImageFromGallery();
            } else {
                Toast.makeText(this, "Ruxsat berilmadi", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            Glide.with(this).load(selectedImageUri).centerCrop().into(ivSportImage);
            ivSportImage.setVisibility(View.VISIBLE);
        }
    }

    private void saveSportType() {
        String name = etSportName.getText().toString().trim();
        String description = etSportDescription.getText().toString().trim();

        if (name.isEmpty()) {
            etSportName.setError("Iltimos, sport turining nomini kiriting");
            return;
        }

        if (selectedImageUri == null) {
            Toast.makeText(this, "Iltimos, rasmni tanlang", Toast.LENGTH_SHORT).show();
            return;
        }

        // RequestBody objectlarni tayyorlash
        RequestBody nameBody = RequestBody.create(MediaType.parse("text/plain"), name);
        RequestBody descriptionBody = RequestBody.create(MediaType.parse("text/plain"), description);

        // Rasmni tayyorlash
        String filePath = FileUtils.getPath(this, selectedImageUri);
        File file = new File(filePath);
        RequestBody requestFile = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image", file.getName(), requestFile);

        // API so'rovini yuborish
        Call<Map<String, Object>> call = apiService.addSportType("Bearer " + token, nameBody, descriptionBody, imagePart);
        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AddSportTypeActivity.this, "Sport turi muvaffaqiyatli qo'shildi!", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    // Token muddati tugagan bo'lsa (401 xatolik)
                    if (response.code() == 401) {
                        handleUnauthorized();
                        return;
                    }
                    Toast.makeText(AddSportTypeActivity.this, "Xatolik: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                Toast.makeText(AddSportTypeActivity.this, "Xatolik: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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