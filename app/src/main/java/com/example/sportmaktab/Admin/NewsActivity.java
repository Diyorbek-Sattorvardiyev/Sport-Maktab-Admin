package com.example.sportmaktab.Admin;

import static java.security.AccessController.getContext;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
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

public class NewsActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private NewsAdapter newsAdapter;
    private List<News> newsList = new ArrayList<>();
    private FloatingActionButton addNewsBtn;
    private ApiService apiService;
    private TextView tvEmptyView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private String token;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_news);

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

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayoutNevs);
        recyclerView = findViewById(R.id.recyclerViewNevs);
        progressBar = findViewById(R.id.progressNevs);
        tvEmptyView = findViewById(R.id.tvYangilik);

        apiService = RetrofitClient.getApiService();
        newsList = new ArrayList<>();
        newsAdapter = new NewsAdapter(newsList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(newsAdapter);

        // Pull-to-refresh ni sozlash
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadNews();
            }
        });

        loadNews();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Activity faol bo'lganda yangiliklar ro'yxatini yangilash
        loadNews();
    }

    private void loadNews() {
        progressBar.setVisibility(View.VISIBLE);
        tvEmptyView.setVisibility(View.GONE);

        Call<List<News>> call = apiService.getNews("Bearer " + token);
        call.enqueue(new Callback<List<News>>() {
            @Override
            public void onResponse(Call<List<News>> call, Response<List<News>> response) {
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null) {
                    newsList.clear();
                    newsList.addAll(response.body());
                    newsAdapter.notifyDataSetChanged();

                    // Ro'yxat bo'sh bo'lsa, empty view ko'rsatiladi
                    if (newsList.isEmpty()) {
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
                            "Ma'lumotlarni olishda xatolik yuz berdi: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                    tvEmptyView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onFailure(Call<List<News>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(getApplicationContext(),
                        "Tarmoq xatoligi: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
                tvEmptyView.setVisibility(View.VISIBLE);
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