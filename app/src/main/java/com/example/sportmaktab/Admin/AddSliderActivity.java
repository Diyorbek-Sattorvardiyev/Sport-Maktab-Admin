package com.example.sportmaktab.Admin;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

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

public class AddSliderActivity extends AppCompatActivity {

    private EditText etSchoolName, etDescription;
    private ImageView ivSliderImage;
    private AppCompatButton btnSelectImage, btnUploadSlider;
    private ProgressBar progressBar;
    private ApiService apiService;
    private SharedPreferences sharedPreferences;
    private String token;
    private Uri imageUri;
    private ActivityResultLauncher<Intent> imagePickerLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_slider);

        // SharedPreferences dan token olish
        sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        token = sharedPreferences.getString("token", "");

        // Agar token bo'sh bo'lsa, login oynasiga qaytarish
        if (token.isEmpty()) {
            Toast.makeText(this, "Sessiya tugagan. Qayta login qiling", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // Viewsni olish
        etSchoolName = findViewById(R.id.etSchoolName);
        etDescription = findViewById(R.id.etDescription);
        ivSliderImage = findViewById(R.id.ivSliderImage);
        btnSelectImage = findViewById(R.id.btnSelectImage);
        btnUploadSlider = findViewById(R.id.btnUploadSlider);
        progressBar = findViewById(R.id.progressBar);

        // Retrofit API service yaratish
        apiService = RetrofitClient.getApiService();

        // Image tanlash uchun launcher
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                        imageUri = result.getData().getData();
                        ivSliderImage.setImageURI(imageUri);
                    }
                }
        );

        // Image tanlash tugmasi
        btnSelectImage.setOnClickListener(v -> selectImage());

        // Slider yuklash tugmasi
        btnUploadSlider.setOnClickListener(v -> uploadSlider());
    }

    // Image tanlash
    private void selectImage() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    // Sliderni yuklash
    private void uploadSlider() {
        String title = etSchoolName.getText().toString().trim();
        String content = etDescription.getText().toString().trim();

        // Agar kerakli maydonlar bo'sh bo'lsa, xato
        if (title.isEmpty() || content.isEmpty() || imageUri == null) {
            Toast.makeText(this, "Barcha maydonlarni to'ldiring", Toast.LENGTH_SHORT).show();
            return;
        }

        // Loading ko'rsatish
        progressBar.setVisibility(View.VISIBLE);

        // Image faylini olish
        File file = new File(getRealPathFromURI(imageUri));
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part imagePart = MultipartBody.Part.createFormData("image", file.getName(), requestBody);

        // RequestBody uchun nom va tavsif
        RequestBody titleBody = RequestBody.create(MediaType.parse("text/plain"), title);
        RequestBody contentBody = RequestBody.create(MediaType.parse("text/plain"), content);

        // API'ga so'rov yuborish
        apiService.addSlider("Bearer " + token, titleBody, contentBody, imagePart)
                .enqueue(new Callback<Map<String, Object>>() {
                    @Override
                    public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                        progressBar.setVisibility(View.GONE);

                        if (response.isSuccessful()) {
                            Toast.makeText(AddSliderActivity.this, "Slider muvaffaqiyatli qo'shildi", Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            // Token muddati tugagan bo'lsa (401 xatolik)
                            if (response.code() == 401) {
                                handleUnauthorized();
                                return;
                            }
                            Toast.makeText(AddSliderActivity.this,
                                    "Xatolik yuz berdi: " + response.code(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(AddSliderActivity.this,
                                "Xatolik: " + t.getMessage(),
                                Toast.LENGTH_SHORT).show();
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

    // URI'dan haqiqiy path olish
    private String getRealPathFromURI(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) return null;
        int columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String path = cursor.getString(columnIndex);
        cursor.close();
        return path;
    }
}