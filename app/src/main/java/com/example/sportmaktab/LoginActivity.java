package com.example.sportmaktab;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.sportmaktab.Admin.AdminDashboardActivity;
import com.example.sportmaktab.Admin.ApiService;
import com.example.sportmaktab.Admin.LoginResponse;
import com.example.sportmaktab.Admin.RetrofitClient;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText editTextLogin, editTextPassword;
    private AppCompatButton loginButton;
    private ProgressBar progressBar;
    private ApiService apiService;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // UI elementlarini topib olish
        editTextLogin = findViewById(R.id.editTextLogin);
        editTextPassword = findViewById(R.id.editTextPassword);
        loginButton = findViewById(R.id.cirLoginButton);
        progressBar = findViewById(R.id.progressBar);

        // SharedPreferences ni sozlash
        sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);

        // Agar allaqachon login qilingan bo'lsa, asosiy oynaga o'tkazish
        if (isLoggedIn()) {
            navigateToMainActivity();
            finish();
            return;
        }

        // RetrofitClient orqali API servisni olish
        apiService = RetrofitClient.getApiService();

        // Login tugmasi bosilganda
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginUser();
            }
        });
    }

    // Sessiya mavjudligini tekshirish
    private boolean isLoggedIn() {
        return sharedPreferences.contains("token") &&
                "admin".equals(sharedPreferences.getString("role", ""));
    }

    private void loginUser() {
        String login = editTextLogin.getText().toString().trim();
        String password = editTextPassword.getText().toString().trim();

        // Bo'sh maydonlarni tekshirish
        if (login.isEmpty() || password.isEmpty()) {
            Toast.makeText(LoginActivity.this,
                    "Login va parol kiritish majburiy!",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        // Progress ko'rsatish
        progressBar.setVisibility(View.VISIBLE);
        loginButton.setEnabled(false);

        // Login uchun map yaratish
        Map<String, String> loginData = new HashMap<>();
        loginData.put("login", login);
        loginData.put("password", password);

        // API zaprosini yuborish
        Call<LoginResponse> call = apiService.login(loginData);
        call.enqueue(new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                progressBar.setVisibility(View.GONE);
                loginButton.setEnabled(true);

                if (response.isSuccessful() && response.body() != null) {
                    LoginResponse loginResponse = response.body();
                    String userRole = loginResponse.getRole();

                    // Faqat admin rolini tekshirish
                    if (!userRole.equalsIgnoreCase("admin")) {
                        Toast.makeText(LoginActivity.this,
                                "Faqat admin kirishi mumkin!",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Ma'lumotlarni SharedPreferences ga saqlash
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("token", loginResponse.getToken());
                    editor.putString("role", userRole);
                    editor.putInt("id", loginResponse.getId());
                    editor.putString("first_name", loginResponse.getFirstName());
                    editor.putString("last_name", loginResponse.getLastName());
                    editor.putString("login", login);
                    editor.putString("password", password);
                    editor.apply();

                    Toast.makeText(LoginActivity.this,
                            "Xush kelibsiz, " + loginResponse.getFirstName() + " " + loginResponse.getLastName(),
                            Toast.LENGTH_SHORT).show();

                    // Asosiy oynaga o'tish
                    navigateToMainActivity();
                    finish();
                } else {
                    // Xatolik bo'lsa
                    Toast.makeText(LoginActivity.this,
                            "Login yoki parol noto'g'ri!",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                loginButton.setEnabled(true);
                Toast.makeText(LoginActivity.this,
                        "Xatolik: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    // AdminDashboard aktivitiga o'tish
    private void navigateToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, AdminDashboardActivity.class);
        startActivity(intent);
    }
}