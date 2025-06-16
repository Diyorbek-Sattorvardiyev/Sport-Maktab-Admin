package com.example.sportmaktab.Admin;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.sportmaktab.LoginActivity;
import com.example.sportmaktab.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddCoachActivity extends AppCompatActivity {

    private TextInputEditText firstNameInput, lastNameInput, birthYearInput, phoneInput, loginInput, passwordInput;
    private TextInputLayout firstNameLayout, lastNameLayout, birthYearLayout, phoneLayout, loginLayout, passwordLayout;
    private AppCompatButton saveButton;
    private ProgressBar progressBar;
    private ApiService apiService;
    private SharedPreferences sharedPreferences;
    private String token;
    AutoCompleteTextView sportTypeInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_coach);

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

        firstNameInput = findViewById(R.id.firstNameInputcash);
        lastNameInput = findViewById(R.id.lastNameInputC);
        birthYearInput = findViewById(R.id.birthYearInputC);
        phoneInput = findViewById(R.id.phoneInputC);
        loginInput = findViewById(R.id.loginInputC);
        passwordInput = findViewById(R.id.passwordInputC);
        progressBar = findViewById(R.id.progresChas);

        // UI elementlarni topamiz
        TextInputLayout sportTypeLayout = findViewById(R.id.sportTypeLayout);
        sportTypeInput = findViewById(R.id.sportTypeInput);

        // Sport turlari ro'yxatini yaratamiz
        String[] sportTypes = new String[]{"Futbol", "Basketbol", "Voleybol", "Tennis", "Shaxmat", "Kurash"};

        // Adapter yaratamiz
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, sportTypes);

        // Adapterni AutoCompleteTextView ga o'rnatamiz
        sportTypeInput.setAdapter(adapter);

        // Foydalanuvchi ro'yxatdan tanlaganda
        sportTypeInput.setOnItemClickListener((parent, view, position, id) -> {
            String selectedSport = (String) parent.getItemAtPosition(position);
            Toast.makeText(this, "Tanlangan sport turi: " + selectedSport, Toast.LENGTH_SHORT).show();
        });

        // Retrofit service ni olish
        apiService = RetrofitClient.getApiService();

        saveButton = findViewById(R.id.saveButtonCoash);

        // Set OnClickListener for the Save button
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInput()) {
                    saveCoachData();
                }
            }
        });
    }

    private boolean validateInput() {
        boolean isValid = true;
        String firstName = firstNameInput.getText().toString().trim();
        String lastName = lastNameInput.getText().toString().trim();
        String birthYear = birthYearInput.getText().toString().trim();
        String phone = phoneInput.getText().toString().trim();
        String login = loginInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String sportType = sportTypeInput.getText().toString().trim();

        // Validate inputs
        if (firstName.isEmpty()) {
            firstNameInput.setError("Ismni kiriting");
            isValid = false;
        }

        if(lastName.isEmpty()){
            lastNameInput.setError("Familiya kiritilishi kerak");
            isValid = false;
        }

        if (phone.isEmpty()) {
            phoneInput.setError("Telefon raqamini kiriting");
            isValid = false;
        }

        if (birthYear.isEmpty() || birthYear.length() != 4) {
            birthYearInput.setError("To'g'ri tug'ilgan yilingizni kiriting");
            isValid = false;
        }

        if (login.isEmpty()) {
            loginInput.setError("Login kiritilishi kerak");
            isValid = false;
        }

        if (password.isEmpty()) {
            passwordInput.setError("Parol kiritilishi kerak");
            isValid = false;
        } else if (password.length() < 6) {
            passwordInput.setError("Parol kamida 6 ta belgidan iborat bo'lishi kerak");
            isValid = false;
        }

        if (sportType.isEmpty()) {
            sportTypeInput.setError("Sport turini tanlang");
            isValid = false;
        }

        return isValid;
    }

    private void saveCoachData() {
        progressBar.setVisibility(View.VISIBLE);

        // Ma'lumotlarni tayyorlash
        String firstName = firstNameInput.getText().toString().trim();
        String lastName = lastNameInput.getText().toString().trim();
        String birthYear = birthYearInput.getText().toString().trim();
        String phone = phoneInput.getText().toString().trim();
        String login = loginInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();
        String sportType = sportTypeInput.getText().toString().trim();

        // Coach ma'lumotlarini map ga o'tkazamiz
        Map<String, String> coachData = new HashMap<>();
        coachData.put("birth_date", birthYear);
        coachData.put("first_name", firstName);
        coachData.put("last_name", lastName);
        coachData.put("login", login);
        coachData.put("password", password);
        coachData.put("phone", phone);
        coachData.put("sport_name", sportType);
        coachData.put("sport_type_id", null);

        // API chaqiruvi, token SharedPreferences dan olinadi
        Call<Map<String, Object>> call = apiService.addCoach("Bearer " + token, coachData);
        call.enqueue(new Callback<Map<String, Object>>() {
            @Override
            public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful()) {
                    Toast.makeText(AddCoachActivity.this, "Murabbiy muvaffaqiyatli qo'shildi", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    // Token muddati tugagan bo'lsa (401 xatolik)
                    if (response.code() == 401) {
                        handleUnauthorized();
                        return;
                    }

                    try {
                        // Agar xatolik bo'lsa, xato javobini olish
                        String errorBody = response.errorBody().string();
                        Log.e("API_ERROR", "Error body: " + errorBody);
                        JSONObject jObjError = new JSONObject(errorBody);
                        String message = jObjError.getString("message");
                        Toast.makeText(AddCoachActivity.this, message, Toast.LENGTH_LONG).show();
                    } catch (Exception e) {
                        Log.e("API_ERROR", "Error parsing error response", e);
                        Toast.makeText(AddCoachActivity.this,
                                "Xatolik yuz berdi: " + response.code(),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                // Tarmoq xatosi bo'lsa
                progressBar.setVisibility(View.GONE);
                Toast.makeText(AddCoachActivity.this,
                        "Tarmoq xatosi: " + t.getMessage(),
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}