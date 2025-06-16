package com.example.sportmaktab.Admin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.sportmaktab.LoginActivity;
import com.example.sportmaktab.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VievSliderActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private SliderAdapter sliderAdapter;
    private List<Slider> sliderList = new ArrayList<>();
    private FloatingActionButton addNewsBtn;
    private ApiService apiService;
    private TextView tvEmptyView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viev_slider);

        // SharedPreferences inizialatsiya qilish
        sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);

        // Agar token bo'sh bo'lsa, login oynasiga qaytarish
        if (getToken().isEmpty()) {
            Toast.makeText(this, "Sessiya tugagan. Qayta login qiling", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // UI elementlarini inizialatsiya qilish
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout1);
        recyclerView = findViewById(R.id.slidersRecyclerView);
        progressBar = findViewById(R.id.progressBar);
        tvEmptyView = findViewById(R.id.tvSlider);

        apiService = RetrofitClient.getApiService();
        sliderAdapter = new SliderAdapter(sliderList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(sliderAdapter);

        loadSlider();

        // Yangilash uchun swipe-to-refresh ni sozlash
        swipeRefreshLayout.setOnRefreshListener(() -> loadSlider());
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Activity qayta ochilganda ma'lumotlarni yangilash
        loadSlider();
    }

    private void loadSlider() {
        progressBar.setVisibility(View.VISIBLE);
        tvEmptyView.setVisibility(View.GONE);

        // Tokenni olish
        String token = getToken();

        // Agar token bo'sh bo'lsa, login oynasiga qaytarish
        if (token.isEmpty()) {
            handleUnauthorized();
            return;
        }

        Call<List<Slider>> call = apiService.getSliders("Bearer " + token);
        call.enqueue(new Callback<List<Slider>>() {
            @Override
            public void onResponse(Call<List<Slider>> call, Response<List<Slider>> response) {
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null) {
                    sliderList.clear();
                    sliderList.addAll(response.body());
                    sliderAdapter.notifyDataSetChanged();

                    // Agar ro'yxat bo'sh bo'lsa, bo'sh xabarni ko'rsatish
                    if (sliderList.isEmpty()) {
                        tvEmptyView.setVisibility(View.VISIBLE);
                    } else {
                        tvEmptyView.setVisibility(View.GONE);
                    }
                } else {
                    // Token muddati tugagan bo'lsa (401 xatolik)
                    if (response.code() == 401) {
                        handleUnauthorized();
                        return;
                    }

                    Toast.makeText(getApplicationContext(),
                            "Xatolik: Slider ma'lumotlarini yuklab bo'lmadi " + response.code(),
                            Toast.LENGTH_SHORT).show();
                    tvEmptyView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<List<Slider>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getApplicationContext(),
                        "Tarmoq xatosi: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
                tvEmptyView.setVisibility(View.VISIBLE);
            }
        });
    }

    // SharedPreferences dan tokenni olish
    private String getToken() {
        return sharedPreferences.getString("token", "");
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