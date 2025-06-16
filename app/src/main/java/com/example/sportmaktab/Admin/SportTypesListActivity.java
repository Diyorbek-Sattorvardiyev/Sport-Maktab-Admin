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
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.sportmaktab.LoginActivity;
import com.example.sportmaktab.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SportTypesListActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private SportTypesAdapter adapter;
    private List<SportType> sportTypesList;
    private ProgressBar progressBar;
    private TextView tvEmptyList;
    private SwipeRefreshLayout swipeRefresh;
    private FloatingActionButton fabAddSport;
    private Toolbar toolbar;

    private ApiService apiService;
    private String token;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_sport_types_list);

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

        // RecyclerView ni sozlash
        setupRecyclerView();

        // Button listener'larni o'rnatish
        setupListeners();

        // Ma'lumotlarni yuklash
        loadSportTypes();
    }

    private void initViews() {
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Sport turlari");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        tvEmptyList = findViewById(R.id.tvEmptyList);
        swipeRefresh = findViewById(R.id.swipeRefresh);
        fabAddSport = findViewById(R.id.fabAddSport);
    }

    private void setupRecyclerView() {
        sportTypesList = new ArrayList<>();
        adapter = new SportTypesAdapter(this, sportTypesList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Adapter click listener
        adapter.setOnItemClickListener(new SportTypesAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(SportType sportType) {
                // Sport turi tanlanganda detallarini ko'rsatish
                Intent intent = new Intent(SportTypesListActivity.this, AddSportTypeActivity.class);
                intent.putExtra("SPORT_ID", sportType.getId());
                startActivity(intent);
            }
        });
    }

    private void setupListeners() {
        // SwipeRefresh listeneri
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadSportTypes();
            }
        });

        // FAB listeneri
        fabAddSport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SportTypesListActivity.this, AddSportTypeActivity.class);
                startActivity(intent);
            }
        });

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void loadSportTypes() {
        progressBar.setVisibility(View.VISIBLE);
        tvEmptyList.setVisibility(View.GONE);

        Call<List<SportType>> call = apiService.getSportTypes("Bearer " + token);
        call.enqueue(new Callback<List<SportType>>() {
            @Override
            public void onResponse(Call<List<SportType>> call, Response<List<SportType>> response) {
                progressBar.setVisibility(View.GONE);
                swipeRefresh.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null) {
                    sportTypesList.clear();
                    sportTypesList.addAll(response.body());
                    adapter.notifyDataSetChanged();

                    // Ro'yxat bo'sh bo'lsa, xabarni ko'rsatish
                    if (sportTypesList.isEmpty()) {
                        tvEmptyList.setVisibility(View.VISIBLE);
                    }
                } else {
                    // Token muddati tugagan bo'lsa (401 xatolik)
                    if (response.code() == 401) {
                        handleUnauthorized();
                        return;
                    }

                    Toast.makeText(SportTypesListActivity.this,
                            "Xatolik: " + response.code(), Toast.LENGTH_SHORT).show();
                    tvEmptyList.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<List<SportType>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                swipeRefresh.setRefreshing(false);
                Toast.makeText(SportTypesListActivity.this,
                        "Xatolik: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                tvEmptyList.setVisibility(View.VISIBLE);
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
        // Faoliyat qayta ochilganda ma'lumotlarni yangilash
        loadSportTypes();
    }
}