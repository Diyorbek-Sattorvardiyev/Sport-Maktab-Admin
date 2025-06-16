package com.example.sportmaktab.Admin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.sportmaktab.LoginActivity;
import com.example.sportmaktab.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import java.util.List;

public class ViewScheduleActivity extends AppCompatActivity {

    private ListView listViewSchedules;
    private ScheduleAdapter scheduleAdapter; // Custom Adapter
    private ApiService apiService;
    private String token;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_schedule);

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

        listViewSchedules = findViewById(R.id.listViewSchedules);
        apiService = RetrofitClient.getApiService();

        fetchSchedules();
    }

    private void fetchSchedules() {
        Call<List<TrainingSchedule>> call = apiService.getTrainingSchedule("Bearer " + token);
        call.enqueue(new Callback<List<TrainingSchedule>>() {
            @Override
            public void onResponse(Call<List<TrainingSchedule>> call, Response<List<TrainingSchedule>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<TrainingSchedule> schedules = response.body();
                    scheduleAdapter = new ScheduleAdapter(ViewScheduleActivity.this, schedules);
                    listViewSchedules.setAdapter(scheduleAdapter);
                } else {
                    // Token muddati tugagan bo'lsa (401 xatolik)
                    if (response.code() == 401) {
                        handleUnauthorized();
                        return;
                    }
                    Toast.makeText(ViewScheduleActivity.this,
                            "Jadvalni yuklashda xatolik: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<TrainingSchedule>> call, Throwable t) {
                Toast.makeText(ViewScheduleActivity.this,
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

    @Override
    protected void onResume() {
        super.onResume();
        // Activity qayta ochilganda ma'lumotlarni yangilash
        fetchSchedules();
    }
}