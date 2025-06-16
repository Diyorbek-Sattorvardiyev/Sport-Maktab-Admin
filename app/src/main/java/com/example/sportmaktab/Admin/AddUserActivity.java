package com.example.sportmaktab.Admin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.sportmaktab.LoginActivity;
import com.example.sportmaktab.R;
import com.google.android.material.appbar.MaterialToolbar;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddUserActivity extends AppCompatActivity {
    private EditText etFirstName, etLastName, etPhone, etLogin, etPassword;
    private AppCompatButton btnSave;
    private ProgressBar progressBar;
    private ApiService apiService;
    private SharedPreferences sharedPreferences;
    private String token;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_user);

        // UI elementlarini bog'lash
        etFirstName = findViewById(R.id.firstNameInput);
        etLastName = findViewById(R.id.lastNameInput);
        etPhone = findViewById(R.id.phoneInput);
        etLogin = findViewById(R.id.loginInput);
        etPassword = findViewById(R.id.passwordInput);
        btnSave = findViewById(R.id.saveButton);
        progressBar = findViewById(R.id.progressBar);

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

        // Retrofit service ni olish
        apiService = RetrofitClient.getApiService();

        // Saqlash tugmasini sozlash
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInput()) {
                    addStudent();
                }
            }
        });

        // Orqaga qaytish uchun toolbar ni sozlash
        MaterialToolbar toolbar = findViewById(R.id.toolbar2);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Talaba qo'shish");
    }

    private boolean validateInput() {
        boolean isValid = true;

        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String login = etLogin.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        if (firstName.isEmpty()) {
            etFirstName.setError("Ism kiritilishi kerak");
            isValid = false;
        }

        if (lastName.isEmpty()) {
            etLastName.setError("Familiya kiritilishi kerak");
            isValid = false;
        }

        if (login.isEmpty()) {
            etLogin.setError("Login kiritilishi kerak");
            isValid = false;
        }

        if (password.isEmpty()) {
            etPassword.setError("Parol kiritilishi kerak");
            isValid = false;
        } else if (password.length() < 6) {
            etPassword.setError("Parol kamida 6 ta belgidan iborat bo'lishi kerak");
            isValid = false;
        }

        return isValid;
    }

    private void addStudent() {
        progressBar.setVisibility(View.VISIBLE);

        // Malumotlarni tayyorlash
        String firstName = etFirstName.getText().toString().trim();
        String lastName = etLastName.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String login = etLogin.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // Student ma'lumotlarini map ga o'tkazamiz
        Map<String, String> studentData = new HashMap<>();
        studentData.put("first_name", firstName);
        studentData.put("last_name", lastName);
        studentData.put("phone", phone);
        studentData.put("login", login);
        studentData.put("password", password);

        // API call - token SharedPreferences dan olinadi
        Call<ResponseBody> call = apiService.addStudent("Bearer " + token, studentData);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful()) {
                    Toast.makeText(AddUserActivity.this, "Talaba muvaffaqiyatli qo'shildi", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    // Token muddati tugagan bo'lsa (401 xatolik)
                    if (response.code() == 401) {
                        handleUnauthorized();
                        return;
                    }

                    try {
                        String errorBody = response.errorBody().string();
                        Log.e("API_ERROR", "Error body: " + errorBody);
                        JSONObject jObjError = new JSONObject(errorBody);
                        String message = jObjError.getString("message");
                        Toast.makeText(AddUserActivity.this, message, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Log.e("API_ERROR", "Error parsing error response", e);
                        Toast.makeText(AddUserActivity.this, "Xatolik yuz berdi: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Log.e("API_ERROR", "Request failed", t);
                Toast.makeText(AddUserActivity.this, "Tarmoq xatoligi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}