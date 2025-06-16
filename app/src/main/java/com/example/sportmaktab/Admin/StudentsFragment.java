package com.example.sportmaktab.Admin;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.sportmaktab.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StudentsFragment extends Fragment {

    private RecyclerView recyclerView;
    private StudentAdapter adapter;
    private List<Student> studentList;
    private ApiService apiService;
    private String token;
    private String search;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;
    private TextView tvEmptyView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_students, container, false);

//        // UI elementlarini bog'lash
//        recyclerView = view.findViewById(R.id.recyclerViewStudents);
//        swipeRefreshLayout = view.findViewById(R.id.swipeRefreshLayout);
//        progressBar = view.findViewById(R.id.progressBar);
//        tvEmptyView = view.findViewById(R.id.tvEmptyView);
//
//        SharedPreferences sharedPreferences = getActivity().getSharedPreferences("SportsSchoolPrefs", Context.MODE_PRIVATE);
//        token = sharedPreferences.getString("token", "");

        // RecyclerView ni sozlash
//        studentList = new ArrayList<>();
//        adapter = new StudentAdapter(studentList, getContext());
//        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//        recyclerView.setAdapter(adapter);

//        // API service ni olish
//        apiService = RetrofitClient.getClient().create(ApiService.class);
//
//        // Talabalar ro'yxatini yangilash
//        loadStudents();
//
//        // SwipeRefreshLayout ni sozlash
//        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                loadStudents();
//            }
//        });

        return view;
    }

//    @Override
//    public void onResume() {
//        super.onResume();
//        // Fragment faol bo'lganda talabalar ro'yxatini yangilash
//        loadStudents();
//    }
//
//    private void loadStudents() {
//        progressBar.setVisibility(View.VISIBLE);
//        tvEmptyView.setVisibility(View.GONE);
//
//        // Agar search bo'sh bo'lsa, uni null yoki bo'sh satr sifatida yuboring
//        if (search == null || search.isEmpty()) {
//            search = "";  // Yoki null
//        }
//        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpZCI6MSwibG9naW4iOiJhZG1pbiIsInJvbGUiOiJhZG1pbiIsImV4cCI6MTc0MzIyNDQ5NH0.TiJBZ6JksZxY6AV8XfDpqF8KWz_gZWv_DDIqkWQQhGI";
//
//        Call<List<Student>> call = apiService.getStudents("Bearer " + token, search);
//        call.enqueue(new Callback<List<Student>>() {
//            @Override
//            public void onResponse(Call<List<Student>> call, Response<List<Student>> response) {
//                progressBar.setVisibility(View.GONE);
//                swipeRefreshLayout.setRefreshing(false);
//
//                if (response.isSuccessful() && response.body() != null) {
//                    studentList.clear();
//                    studentList.addAll(response.body());
//                    adapter.notifyDataSetChanged();
//
//                    // Ro'yxat bo'sh bo'lsa, empty view ko'rsatiladi
//                    if (studentList.isEmpty()) {
//                        tvEmptyView.setVisibility(View.VISIBLE);
//                    } else {
//                        tvEmptyView.setVisibility(View.GONE);
//                    }
//                } else {
//                    Toast.makeText(getContext(), "Ma'lumotlarni olishda xatolik yuz berdi", Toast.LENGTH_SHORT).show();
//                    tvEmptyView.setVisibility(View.VISIBLE);
//                }
//            }
//
//            @Override
//            public void onFailure(Call<List<Student>> call, Throwable t) {
//                progressBar.setVisibility(View.GONE);
//                swipeRefreshLayout.setRefreshing(false);
//                Toast.makeText(getContext(), "Tarmoq xatoligi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
//                tvEmptyView.setVisibility(View.VISIBLE);
//            }
//        });
//    }

}