package com.example.sportmaktab.Admin;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sportmaktab.LoginActivity;
import com.example.sportmaktab.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class StudentAdapter extends RecyclerView.Adapter<StudentAdapter.StudentViewHolder> {

    private List<Student> studentList;
    private Activity activity;
    private SharedPreferences sharedPreferences;

    public StudentAdapter(List<Student> studentList, Activity activity) {
        this.studentList = studentList;
        this.activity = activity;
        this.sharedPreferences = activity.getSharedPreferences("UserSession", Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public StudentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student, parent, false);
        return new StudentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull StudentViewHolder holder, int position) {
        Student student = studentList.get(position);

        holder.tvName.setText(student.getFirstName()+" "+student.getLastName());
        holder.tvPhone.setText(student.getPhone() != null && !student.getPhone().isEmpty()
                ? student.getPhone() : "Telefon raqam ko'rsatilmagan");
        holder.tvLogin.setText(student.getLogin());

        holder.btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog(student);
            }
        });
    }

    @Override
    public int getItemCount() {
        return studentList.size();
    }

    class StudentViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvPhone;
        Chip tvLogin;
        MaterialButton btnEdit, btnDelete;

        public StudentViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvStudentName);
            tvPhone = itemView.findViewById(R.id.tvStudentPhone);
            tvLogin = itemView.findViewById(R.id.chipLogin);
            btnEdit = itemView.findViewById(R.id.btnEditStudent);
            btnDelete = itemView.findViewById(R.id.btnDeleteStudent);
        }
    }

    private void showDeleteConfirmationDialog(Student student) {
        if (activity == null || activity.isFinishing()) {
            Toast.makeText(activity, "Xatolik: Dialogni ochib bo'lmadi", Toast.LENGTH_SHORT).show();
            return;
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle("Talabani o'chirish");
        builder.setMessage(student.getFirstName() + " " + student.getLastName() + " ni o'chirishni istaysizmi?");
        builder.setPositiveButton("Ha", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteStudent(student.getId());
            }
        });
        builder.setNegativeButton("Yo'q", null);
        builder.show();
    }

    private void deleteStudent(int studentId) {
        // SharedPreferences dan tokenni olish
        String token = sharedPreferences.getString("token", "");

        // Token bo'sh bo'lsa, foydalanuvchini login oynasiga yo'naltirish
        if (token.isEmpty()) {
            handleUnauthorized();
            return;
        }

        ApiService apiService = RetrofitClient.getApiService();

        Call<ResponseBody> call = apiService.deleteStudent("Bearer " + token, studentId);
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(activity, "Talaba muvaffaqiyatli o'chirildi", Toast.LENGTH_SHORT).show();
                    for (int i = 0; i < studentList.size(); i++) {
                        if (studentList.get(i).getId() == studentId) {
                            studentList.remove(i);
                            notifyItemRemoved(i);
                            break;
                        }
                    }
                } else {
                    // Token muddati tugagan bo'lsa (401 xatolik)
                    if (response.code() == 401) {
                        handleUnauthorized();
                        return;
                    }
                    Toast.makeText(activity,
                            "Talabani o'chirishda xatolik yuz berdi: " + response.code(),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(activity, "Tarmoq xatoligi: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Token muddati tugagan yoki noto'g'ri bo'lsa
    private void handleUnauthorized() {
        Toast.makeText(activity, "Sessiya tugagan, qaytadan login qiling", Toast.LENGTH_LONG).show();
        // SharedPreferences dan ma'lumotlarni o'chirish
        sharedPreferences.edit().clear().apply();
        // Login oynasiga qaytarish
        Intent intent = new Intent(activity, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
        activity.finish();
    }
}