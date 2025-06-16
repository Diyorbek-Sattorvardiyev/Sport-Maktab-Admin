package com.example.sportmaktab.Admin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
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

public class MurabiyVievActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private CoachAdapter adapter;
    private List<Coach> coachList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private EditText searchEditText;
    private String authToken;
    private ApiService apiService;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_murabiy_viev);

        // SharedPreferences dan tokenni olish
        sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        authToken = sharedPreferences.getString("token", "");

        // Agar token bo'sh bo'lsa, login oynasiga qaytarish
        if (authToken.isEmpty()) {
            Toast.makeText(this, "Sessiya tugagan. Qayta login qiling", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        recyclerView = findViewById(R.id.recyclerViewCoaches);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshCoaches);
        searchEditText = findViewById(R.id.editTextSearchCoach);
        FloatingActionButton fabAddCoach = findViewById(R.id.fabAddCoach);

        // RecyclerView ni sozlash
        apiService = RetrofitClient.getApiService();
        coachList = new ArrayList<>();

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new CoachAdapter(coachList, this);
        recyclerView.setAdapter(adapter);

        // Activity ishga tushganda murabbiylarni yuklash
        loadCoaches("");

        // Yangilash uchun swipe refresh ni sozlash
        swipeRefreshLayout.setOnRefreshListener(() -> loadCoaches(searchEditText.getText().toString()));

        // Qidirish funksiyasini sozlash
        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                loadCoaches(s.toString());
            }
        });

        // Yangi murabbiy qo'shish uchun FAB tugmasini sozlash
        fabAddCoach.setOnClickListener(view -> {
            Intent intent = new Intent(MurabiyVievActivity.this, AddCoachActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Activity qayta ochilganda ma'lumotlarni yangilash
        loadCoaches(searchEditText.getText().toString());
    }

    private void loadCoaches(String searchQuery) {
        swipeRefreshLayout.setRefreshing(true);

        Call<List<Coach>> call = apiService.getCoaches("Bearer " + authToken, searchQuery);
        call.enqueue(new Callback<List<Coach>>() {
            @Override
            public void onResponse(Call<List<Coach>> call, Response<List<Coach>> response) {
                swipeRefreshLayout.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null) {
                    coachList.clear();
                    coachList.addAll(response.body());
                    adapter.notifyDataSetChanged();

                    // Ro'yxat bo'sh bo'lsa, "Murabbiylar topilmadi" xabarini ko'rsatish
                    if (coachList.isEmpty()) {
                        findViewById(R.id.textViewNoCoaches).setVisibility(View.VISIBLE);
                    } else {
                        findViewById(R.id.textViewNoCoaches).setVisibility(View.GONE);
                    }
                } else {
                    // Token muddati tugagan bo'lsa (401 xatolik)
                    if (response.code() == 401) {
                        handleUnauthorized();
                        return;
                    }

                    Toast.makeText(MurabiyVievActivity.this,
                            "Murabiylarni yuklashda xatolik: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Coach>> call, Throwable t) {
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(MurabiyVievActivity.this,
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
}