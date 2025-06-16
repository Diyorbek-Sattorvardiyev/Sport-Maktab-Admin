package com.example.sportmaktab.Admin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.sportmaktab.LoginActivity;
import com.example.sportmaktab.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddSchuldeActivity extends AppCompatActivity {

    private EditText etWorkoutDate, etWorkoutTime, etRoom;
    private Spinner spinnerSportType, spinnerCoach;
    private AppCompatButton btnSaveWorkout, btnCancel;

    private ApiService apiService;
    private SharedPreferences sharedPreferences;
    private String token;
    private List<SportType> sportTypeList;
    private List<Coach> coachList;
    private ArrayAdapter<String> sportTypeAdapter;
    private ArrayAdapter<String> coachAdapter;
    // Sport turlari va murabbiylar uchun map (id va nom)
    private Map<String, Integer> sportTypeMap = new HashMap<>();
    private Map<String, Integer> coachMap = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_schulde);

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

        // Sport turlari va murabbiylarni olish
        fetchSportTypes();
        fetchCoaches();

        // Buttonlarga clickListener o'rnatish
        setupClickListeners();
    }

    private void initViews() {
        etWorkoutDate = findViewById(R.id.etWorkoutDate);
        etWorkoutTime = findViewById(R.id.etWorkoutTime);
        etRoom = findViewById(R.id.etRoom);
        spinnerSportType = findViewById(R.id.spinnerSportType);
        spinnerCoach = findViewById(R.id.spinnerCoach);
        btnSaveWorkout = findViewById(R.id.btnSaveWorkout);
        btnCancel = findViewById(R.id.btnCancel);
    }

    private void setupClickListeners() {
        btnSaveWorkout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveTrainingSchedule();
            }
        });

        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Activity ni yopish
            }
        });
    }

    private void fetchSportTypes() {
        Call<List<SportType>> call = apiService.getSportTypes("Bearer " + token);
        call.enqueue(new Callback<List<SportType>>() {
            @Override
            public void onResponse(Call<List<SportType>> call, Response<List<SportType>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    sportTypeList = response.body();

                    // Faqat sport turlarining nomlarini olish
                    List<String> sportNames = new ArrayList<>();

                    for (SportType sportType : sportTypeList) {
                        sportNames.add(sportType.getName());
                        // ID va nomni saqlab olish keyingi ishlatish uchun
                        sportTypeMap.put(sportType.getName(), sportType.getId());
                    }

                    // Spinnerga faqat nomlarni qo'yish
                    sportTypeAdapter = new ArrayAdapter<>(AddSchuldeActivity.this,
                            android.R.layout.simple_spinner_item, sportNames);
                    sportTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerSportType.setAdapter(sportTypeAdapter);
                } else {
                    // Token muddati tugagan bo'lsa (401 xatolik)
                    if (response.code() == 401) {
                        handleUnauthorized();
                        return;
                    }
                    Toast.makeText(AddSchuldeActivity.this, "Sport turlarini yuklashda xatolik: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<SportType>> call, Throwable t) {
                Toast.makeText(AddSchuldeActivity.this, "Xatolik: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchCoaches() {
        Call<List<Coach>> call = apiService.getCoaches("Bearer " + token, "");
        call.enqueue(new Callback<List<Coach>>() {
            @Override
            public void onResponse(Call<List<Coach>> call, Response<List<Coach>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    coachList = response.body();

                    // Faqat murabbiylarning ismlarini olish
                    List<String> coachNames = new ArrayList<>();

                    for (Coach coach : coachList) {
                        // To'liq ism (familiya, ism, otasining ismi)
                        String fullName = coach.getLastName() + " " + coach.getFirstName() + " " + coach.getFirstName();
                        coachNames.add(fullName);
                        // ID va ismni saqlab olish keyingi ishlatish uchun
                        coachMap.put(fullName, coach.getId());
                    }

                    // Spinnerga faqat ismlarni qo'yish
                    coachAdapter = new ArrayAdapter<>(AddSchuldeActivity.this,
                            android.R.layout.simple_spinner_item, coachNames);
                    coachAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerCoach.setAdapter(coachAdapter);
                } else {
                    // Token muddati tugagan bo'lsa (401 xatolik)
                    if (response.code() == 401) {
                        handleUnauthorized();
                        return;
                    }
                    Toast.makeText(AddSchuldeActivity.this, "Murabbiylarni yuklashda xatolik: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Coach>> call, Throwable t) {
                Toast.makeText(AddSchuldeActivity.this, "Xatolik: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void saveTrainingSchedule() {
        // Ma'lumotlarni tekshirish
        if (validateInput()) {
            // Ma'lumotlarni yig'ish
            Map<String, Object> scheduleData = new HashMap<>();

            scheduleData.put("date", etWorkoutDate.getText().toString().trim());
            scheduleData.put("time", etWorkoutTime.getText().toString().trim());
            scheduleData.put("room", etRoom.getText().toString().trim());

            // Tanlangan sport turi va murabbiy nomlarini olish
            String selectedSportType = (String) spinnerSportType.getSelectedItem();
            String selectedCoach = (String) spinnerCoach.getSelectedItem();

            // Nomlardan ID larni olish
            Integer sportTypeId = sportTypeMap.get(selectedSportType);
            Integer coachId = coachMap.get(selectedCoach);

            scheduleData.put("sport_type_id", sportTypeId);
            scheduleData.put("coach_id", coachId);

            // API ga jo'natish
            Call<Map<String, Object>> call = apiService.addTrainingSchedule("Bearer " + token, scheduleData);
            call.enqueue(new Callback<Map<String, Object>>() {
                @Override
                public void onResponse(Call<Map<String, Object>> call, Response<Map<String, Object>> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(AddSchuldeActivity.this, "Mashg'ulot jadvali muvaffaqiyatli qo'shildi!", Toast.LENGTH_SHORT).show();
                        finish();
                    } else {
                        // Token muddati tugagan bo'lsa (401 xatolik)
                        if (response.code() == 401) {
                            handleUnauthorized();
                            return;
                        }
                        Toast.makeText(AddSchuldeActivity.this, "Mashg'ulot jadvalini qo'shishda xatolik: " + response.code(), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<Map<String, Object>> call, Throwable t) {
                    Toast.makeText(AddSchuldeActivity.this, "Xatolik: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
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

    private boolean validateInput() {
        boolean isValid = true;

        // Sana tekshirish
        if (etWorkoutDate.getText().toString().trim().isEmpty()) {
            etWorkoutDate.setError("Sanani kiriting");
            isValid = false;
        }

        // Vaqt tekshirish
        if (etWorkoutTime.getText().toString().trim().isEmpty()) {
            etWorkoutTime.setError("Vaqtni kiriting");
            isValid = false;
        }

        // Xona tekshirish
        if (etRoom.getText().toString().trim().isEmpty()) {
            etRoom.setError("Xonani kiriting");
            isValid = false;
        }

        // Sport turi tanlangan-tanlanmaganligini tekshirish
        if (spinnerSportType.getSelectedItem() == null) {
            Toast.makeText(this, "Iltimos, sport turini tanlang", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        // Murabbiy tanlangan-tanlanmaganligini tekshirish
        if (spinnerCoach.getSelectedItem() == null) {
            Toast.makeText(this, "Iltimos, murabbiyni tanlang", Toast.LENGTH_SHORT).show();
            isValid = false;
        }

        return isValid;
    }
}