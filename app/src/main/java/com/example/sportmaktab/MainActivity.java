package com.example.sportmaktab;

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

import com.example.sportmaktab.Admin.ApiService;
import com.example.sportmaktab.Admin.RetrofitClient;
import com.example.sportmaktab.Admin.Student;
import com.example.sportmaktab.Admin.StudentAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private StudentAdapter adapter;
    private List<Student> studentList;
    private ApiService apiService;
    private SharedPreferences sharedPreferences;
    private String token;
    private String search;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private TextView tvEmptyView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

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

        // UI elementlarini bog'lash
        recyclerView = findViewById(R.id.recyclerViewStudents);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        progressBar = findViewById(R.id.progressBar);
        tvEmptyView = findViewById(R.id.tvEmptyView);

        // RecyclerView ni sozlash
        studentList = new ArrayList<>();
        adapter = new StudentAdapter(studentList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
        recyclerView.setAdapter(adapter);

        // API service ni olish
        apiService = RetrofitClient.getApiService();

        // Talabalar ro'yxatini yangilash
        loadStudents();

        // SwipeRefreshLayout ni sozlash
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadStudents();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Activity qayta ochilganda ma'lumotlarni yangilash
        loadStudents();
    }

    private void loadStudents() {
        progressBar.setVisibility(View.VISIBLE);
        tvEmptyView.setVisibility(View.GONE);

        // Agar search bo'sh bo'lsa, uni bo'sh satr sifatida yuboring
        if (search == null || search.isEmpty()) {
            search = "";
        }

        Call<List<Student>> call = apiService.getStudents("Bearer " + token, search);
        call.enqueue(new Callback<List<Student>>() {
            @Override
            public void onResponse(Call<List<Student>> call, Response<List<Student>> response) {
                progressBar.setVisibility(View.GONE);
                swipeRefreshLayout.setRefreshing(false);

                if (response.isSuccessful() && response.body() != null) {
                    studentList.clear();
                    studentList.addAll(response.body());
                    adapter.notifyDataSetChanged();

                    // Ro'yxat bo'sh bo'lsa, empty view ko'rsatiladi
                    if (studentList.isEmpty()) {
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
            public void onFailure(Call<List<Student>> call, Throwable t) {
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