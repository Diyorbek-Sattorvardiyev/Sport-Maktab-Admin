package com.example.sportmaktab.Admin;

import android.app.DatePickerDialog;
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
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportmaktab.LoginActivity;
import com.example.sportmaktab.R;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddNewsActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private TextInputEditText titleEditText, contentEditText;
    private TextInputEditText dateTextView;
    private AppCompatButton selectImagesButton, saveButton;
    private RecyclerView selectedImagesRecyclerView;
    private ProgressBar progressBar;

    private SelectedImagesAdapter imagesAdapter;
    private List<Uri> selectedImages = new ArrayList<>();
    private String currentDate;

    private ApiService apiService;
    private SharedPreferences sharedPreferences;
    private String token;
    private Uri imageUri;

    private ImageView previewImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_news);

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

        titleEditText = findViewById(R.id.etNewsTitle);
        contentEditText = findViewById(R.id.etNewsContent);
        dateTextView = findViewById(R.id.etNewsDate);
        selectImagesButton = findViewById(R.id.btnSelectImages);
        saveButton = findViewById(R.id.btnAddNews);
        selectedImagesRecyclerView = findViewById(R.id.selectedImagesRecyclerView);
        progressBar = findViewById(R.id.progressBar);
        previewImage = findViewById(R.id.previewImage);

        // Retrofit service ni olish
        apiService = RetrofitClient.getApiService();

        selectImagesButton.setOnClickListener(v -> pickImage());
        saveButton.setOnClickListener(v -> saveNews());
    }

    private void pickImage() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, 101);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == RESULT_OK && data != null) {
            imageUri = data.getData();
            previewImage.setImageURI(imageUri);
        }
    }

    private void saveNews() {
        String title = titleEditText.getText().toString().trim();
        String content = contentEditText.getText().toString().trim();
        String date = dateTextView.getText().toString().trim();

        if (title.isEmpty() || content.isEmpty() || date.isEmpty() || imageUri == null) {
            Toast.makeText(this, "Barcha maydonlarni to'ldiring", Toast.LENGTH_SHORT).show();
            return;
        }

        // Progress bar ko'rsatish
        progressBar.setVisibility(View.VISIBLE);

        File file = new File(getRealPathFromURI(imageUri));
        RequestBody requestBody = RequestBody.create(MediaType.parse("image/*"), file);
        MultipartBody.Part imagePart = MultipartBody.Part.createFormData("images", file.getName(), requestBody);

        RequestBody titleBody = RequestBody.create(MediaType.parse("text/plain"), title);
        RequestBody contentBody = RequestBody.create(MediaType.parse("text/plain"), content);
        RequestBody dateBody = RequestBody.create(MediaType.parse("text/plain"), date);

        List<MultipartBody.Part> imageList = new ArrayList<>();
        imageList.add(imagePart);

        apiService.addNews("Bearer " + token, titleBody, contentBody, dateBody, imageList)
                .enqueue(new Callback<Map<String, Object>>() {
                    @Override
                    public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                        progressBar.setVisibility(View.GONE);

                        if (response.isSuccessful()) {
                            Toast.makeText(AddNewsActivity.this, "Yangilik muvaffaqiyatli qo'shildi", Toast.LENGTH_SHORT).show();
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            // Token muddati tugagan bo'lsa (401 xatolik)
                            if (response.code() == 401) {
                                handleUnauthorized();
                                return;
                            }
                            Toast.makeText(AddNewsActivity.this,
                                    "Xatolik yuz berdi: " + response.code(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(AddNewsActivity.this,
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