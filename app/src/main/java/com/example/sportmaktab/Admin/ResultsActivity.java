package com.example.sportmaktab.Admin;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportmaktab.LoginActivity;
import com.example.sportmaktab.R;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResultsActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private ResultsAdapter adapter;
    private List<Result> resultsList;
    private ProgressBar progressBar;
    private FloatingActionButton fabAddResult;
    private ApiService apiService;
    private String token;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_results);

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

        // UI komponentlarini inizialatsiya qilish
        recyclerView = findViewById(R.id.recyclerViewResults);
        progressBar = findViewById(R.id.progressBar);
        fabAddResult = findViewById(R.id.fabAddResult);

        // RecyclerView ni sozlash
        resultsList = new ArrayList<>();
        adapter = new ResultsAdapter(this, resultsList);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // API servisni inizialatsiya qilish
        apiService = RetrofitClient.getApiService();

        // FloatingActionButton bosilganda yangi natija qo'shish oynasiga o'tish
        fabAddResult.setOnClickListener(v -> {
            Intent intent = new Intent(ResultsActivity.this, AddResultActivity.class);
            startActivity(intent);
        });

        // Natijalarni yuklash
        loadResults();

        // Adapterni sozlash
        // O'zingizning adaptersining xususiyatlariga ko'ra sozlash
        // Agar adapter uchun onClick va boshqa metodlar bo'lsa qo'shing
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadResults(); // Activity qayta ochilganda ro'yxatni yangilash
    }

    private void loadResults() {
        progressBar.setVisibility(View.VISIBLE);

        Call<List<Result>> call = apiService.getResults("Bearer " + token);
        call.enqueue(new Callback<List<Result>>() {
            @Override
            public void onResponse(Call<List<Result>> call, Response<List<Result>> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful()) {
                    resultsList.clear();
                    if (response.body() != null) {
                        resultsList.addAll(response.body());
                    }
                    adapter.notifyDataSetChanged();

                    // Agar natijalar bo'sh bo'lsa, bo'sh xabarni ko'rsatish
                    if (resultsList.isEmpty()) {
                        findViewById(R.id.tvNoResults).setVisibility(View.VISIBLE);
                    } else {
                        findViewById(R.id.tvNoResults).setVisibility(View.GONE);
                    }
                } else {
                    // Token muddati tugagan bo'lsa (401 xatolik)
                    if (response.code() == 401) {
                        handleUnauthorized();
                        return;
                    }
                    Toast.makeText(ResultsActivity.this,
                            "Natijalarni yuklashda xatolik: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Result>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ResultsActivity.this,
                        "Tarmoq xatosi: " + t.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void deleteResult(int resultId, int position) {
        progressBar.setVisibility(View.VISIBLE);

        Call<Map<String, String>> call = apiService.deleteResult("Bearer " + token, resultId);
        call.enqueue(new Callback<Map<String, String>>() {
            @Override
            public void onResponse(Call<Map<String, String>> call, Response<Map<String, String>> response) {
                progressBar.setVisibility(View.GONE);

                if (response.isSuccessful()) {
                    resultsList.remove(position);
                    adapter.notifyItemRemoved(position);
                    adapter.notifyItemRangeChanged(position, resultsList.size());

                    Toast.makeText(ResultsActivity.this,
                            "Natija muvaffaqiyatli o'chirildi",
                            Toast.LENGTH_SHORT).show();

                    // Agar natijalar bo'sh bo'lsa, bo'sh xabarni ko'rsatish
                    if (resultsList.isEmpty()) {
                        findViewById(R.id.tvNoResults).setVisibility(View.VISIBLE);
                    }
                } else {
                    // Token muddati tugagan bo'lsa (401 xatolik)
                    if (response.code() == 401) {
                        handleUnauthorized();
                        return;
                    }
                    Toast.makeText(ResultsActivity.this,
                            "Natijani o'chirishda xatolik: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Map<String, String>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ResultsActivity.this,
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