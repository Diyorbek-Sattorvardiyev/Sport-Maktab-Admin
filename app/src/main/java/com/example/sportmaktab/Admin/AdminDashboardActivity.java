package com.example.sportmaktab.Admin;

import static android.view.View.VISIBLE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.viewpager2.widget.ViewPager2;

import com.example.sportmaktab.LoginActivity;
import com.example.sportmaktab.MainActivity;
import com.example.sportmaktab.R;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminDashboardActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    private ViewPager2 viewPager;
    private TabLayout tabLayout;
    private FloatingActionButton fab;
    private DashboardPagerAdapter pagerAdapter;
    private ApiService apiService;
    private String token;
    private Handler handler = new Handler();
    private Runnable runnable;
    private SharedPreferences sharedPreferences;

    // Grafik uchun o'zgaruvchilar
    private PieChart pieChart;
    private BarChart barChart;
    private LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_dashboard);

        // SharedPreferences dan token va boshqa ma'lumotlarni olish
        sharedPreferences = getSharedPreferences("UserSession", MODE_PRIVATE);
        token = sharedPreferences.getString("token", "");

        // Agar token bo'sh bo'lsa, login oynasiga qaytarish
        if (token.isEmpty()) {
            Toast.makeText(this, "Sessiya tugagan. Qayta login qiling", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        linearLayout=findViewById(R.id.statistikLayout);
        pieChart=findViewById(R.id.pieChart);
        barChart=findViewById(R.id.barChart);

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (pieChart.getVisibility() == View.VISIBLE) {
                    // Agar hozir ko'rinsa, yashirish
                    pieChart.setVisibility(View.GONE);
                    barChart.setVisibility(View.GONE);
                } else {
                    // Agar hozir ko'rinmasa, ko'rsatish
                    pieChart.setVisibility(View.VISIBLE);
                    barChart.setVisibility(View.VISIBLE);
                }
            }
        });

        // Toolbar va Navigation Drawer'ni o'rnatish
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawerLayout = findViewById(R.id.drawerLayout);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,
                R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.navView);
        navigationView.setNavigationItemSelectedListener(this);

        // Navigation drawer header'dagi foydalanuvchi ma'lumotlarini o'rnatish
        View headerView = navigationView.getHeaderView(0);
        TextView txtUserName = headerView.findViewById(R.id.nav_header_name);
        TextView txtUserEmail = headerView.findViewById(R.id.nav_header_email);

        String firstName = sharedPreferences.getString("first_name", "");
        String lastName = sharedPreferences.getString("last_name", "");
        String login = sharedPreferences.getString("login", "");

        // Agar navigation header view'da ushbu elementlar mavjud bo'lsa
        if (txtUserName != null && txtUserEmail != null) {
            txtUserName.setText(firstName + " " + lastName);
            txtUserEmail.setText(login);
        }

        // TabLayout va ViewPager'ni o'rnatish
        viewPager = findViewById(R.id.viewPager);
        tabLayout = findViewById(R.id.tabLayout);

        pagerAdapter = new DashboardPagerAdapter(this);
        viewPager.setAdapter(pagerAdapter);

        // FAB'ni o'rnatish
        fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {

                    showAddStudentDialog();

        });

        // API bilan ishlash uchun kerakli obyektlarni initialize qilish
        apiService = RetrofitClient.getApiService();

        // Tezkor boshqaruv buttonlarini o'rnatish
        setupQuickActions();

        // Grafik elementlarini initialize qilish
        pieChart = findViewById(R.id.pieChart);
        barChart = findViewById(R.id.barChart);

        // Statistika ma'lumotlarini yuklash
        // 30 soniyada bir marta loadStatistics metodini chaqirish
        runnable = new Runnable() {
            @Override
            public void run() {
                loadStatistics();
                handler.postDelayed(this, 30000); // 30 soniya kutish
            }
        };
        handler.post(runnable);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Aktivitet yopilganda handlerni to'xtatish
        handler.removeCallbacks(runnable);
    }

    // Tezkor boshqaruv tugmachalari uchun click listener'larni o'rnatish
    private void setupQuickActions() {
        findViewById(R.id.btnAddBanner).setOnClickListener(v -> showAddBannerDialog());
        findViewById(R.id.btnAddNews).setOnClickListener(v -> showAddNewsDialog());
        findViewById(R.id.btnAddSport).setOnClickListener(v -> showAddSportDialog());
        findViewById(R.id.btnAddCoach).setOnClickListener(v -> showAddCoachDialog());
        findViewById(R.id.btnAddSchedule).setOnClickListener(v -> showAddScheduleDialog());
        findViewById(R.id.btnAddResult).setOnClickListener(v -> showAddResultDialog());
    }

    // Statistika ma'lumotlarini yuklash va ko'rsatish
    private void loadStatistics() {
        TextView txtStudentCount = findViewById(R.id.txtStudentCount);
        TextView txtCoachCount = findViewById(R.id.txtCoachCount);
        TextView txtSportsCount = findViewById(R.id.txtSportsCount);
        TextView txtNewsCount = findViewById(R.id.txtNewsCount);

        // Avval default qiymatlarni ko'rsatib, API javob berguncha kutmaslik
        txtStudentCount.setText("0");
        txtCoachCount.setText("0");
        txtSportsCount.setText("0");
        txtNewsCount.setText("0");

        // Ma'lumotlarni saqlash uchun o'zgaruvchilar
        final int[] studentCount = {0};
        final int[] coachCount = {0};
        final int[] sportsCount = {0};
        final int[] newsCount = {0};

        // Token bo'sh emas ekanini tekshirish
        if (token.isEmpty()) {
            Toast.makeText(this, "Token yo'q. Qaytadan login qiling.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, LoginActivity.class));
            finish();
            return;
        }

        // 1. Talabalar sonini yuklash
        apiService.getStudents("Bearer " + token, "").enqueue(new Callback<List<Student>>() {
            @Override
            public void onResponse(Call<List<Student>> call, Response<List<Student>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    studentCount[0] = response.body().size();
                    txtStudentCount.setText(String.valueOf(studentCount[0]));
                    updateCharts(studentCount[0], coachCount[0], sportsCount[0], newsCount[0]);
                } else {
                    // Agar token xato bo'lsa (401), login oynasiga qaytarish
                    if (response.code() == 401) {
                        handleUnauthorized();
                        return;
                    }
                    txtStudentCount.setText("0");
                    Toast.makeText(AdminDashboardActivity.this,
                            "Talabalar ma'lumotlarini yuklashda xatolik", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Student>> call, Throwable t) {
                txtStudentCount.setText("0");
                Toast.makeText(AdminDashboardActivity.this,
                        "Xatolik: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        // 2. Murabbiylar sonini yuklash
        apiService.getCoaches("Bearer " + token, "").enqueue(new Callback<List<Coach>>() {
            @Override
            public void onResponse(Call<List<Coach>> call, Response<List<Coach>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    coachCount[0] = response.body().size();
                    txtCoachCount.setText(String.valueOf(coachCount[0]));
                    updateCharts(studentCount[0], coachCount[0], sportsCount[0], newsCount[0]);
                } else {
                    if (response.code() == 401) {
                        handleUnauthorized();
                        return;
                    }
                    txtCoachCount.setText("0");
                    Toast.makeText(AdminDashboardActivity.this,
                            "Murabbiylar ma'lumotlarini yuklashda xatolik", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Coach>> call, Throwable t) {
                txtCoachCount.setText("0");
            }
        });

        // 3. Sport turlari sonini yuklash
        apiService.getSportTypes("Bearer " + token).enqueue(new Callback<List<SportType>>() {
            @Override
            public void onResponse(Call<List<SportType>> call, Response<List<SportType>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    sportsCount[0] = response.body().size();
                    txtSportsCount.setText(String.valueOf(sportsCount[0]));
                    updateCharts(studentCount[0], coachCount[0], sportsCount[0], newsCount[0]);
                } else {
                    if (response.code() == 401) {
                        handleUnauthorized();
                        return;
                    }
                    txtSportsCount.setText("0");
                    Toast.makeText(AdminDashboardActivity.this,
                            "Sport turlari ma'lumotlarini yuklashda xatolik", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<SportType>> call, Throwable t) {
                txtSportsCount.setText("0");
            }
        });

        // 4. Yangiliklar sonini yuklash
        apiService.getNews("Bearer " + token).enqueue(new Callback<List<News>>() {
            @Override
            public void onResponse(Call<List<News>> call, Response<List<News>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    newsCount[0] = response.body().size();
                    txtNewsCount.setText(String.valueOf(newsCount[0]));
                    updateCharts(studentCount[0], coachCount[0], sportsCount[0], newsCount[0]);
                } else {
                    if (response.code() == 401) {
                        handleUnauthorized();
                        return;
                    }
                    txtNewsCount.setText("0");
                    Toast.makeText(AdminDashboardActivity.this,
                            "Yangiliklar ma'lumotlarini yuklashda xatolik", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<News>> call, Throwable t) {
                txtNewsCount.setText("0");
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

    // Grafiklarni yangilash funksiyasi
    private void updateCharts(int studentCount, int coachCount, int sportsCount, int newsCount) {
        // Pie Chart sozlamalari
        ArrayList<PieEntry> pieEntries = new ArrayList<>();
        pieEntries.add(new PieEntry(studentCount, "Talabalar"));
        pieEntries.add(new PieEntry(coachCount, "Murabbiylar"));
        pieEntries.add(new PieEntry(sportsCount, "Sport turlari"));
        pieEntries.add(new PieEntry(newsCount, "Yangiliklar"));

        PieDataSet pieDataSet = new PieDataSet(pieEntries, "Statistika");
        pieDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        pieDataSet.setValueTextSize(12f);
        pieDataSet.setValueTextColor(android.R.color.white);

        PieData pieData = new PieData(pieDataSet);
        pieChart.setData(pieData);
        pieChart.getDescription().setEnabled(false);
        pieChart.setCenterText("Umumiy Statistika");
        pieChart.setCenterTextSize(16f);
        pieChart.setHoleRadius(40f);
        pieChart.setTransparentCircleRadius(45f);
        pieChart.animateY(1000);
        pieChart.invalidate();

        // Bar Chart sozlamalari
        ArrayList<BarEntry> barEntries = new ArrayList<>();
        barEntries.add(new BarEntry(1f, studentCount));
        barEntries.add(new BarEntry(2f, coachCount));
        barEntries.add(new BarEntry(3f, sportsCount));
        barEntries.add(new BarEntry(4f, newsCount));

        BarDataSet barDataSet = new BarDataSet(barEntries, "Statistika");
        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
        barDataSet.setValueTextSize(12f);
        barDataSet.setValueTextColor(android.R.color.black);

        BarData barData = new BarData(barDataSet);
        barChart.setData(barData);
        barChart.getDescription().setEnabled(false);
        barChart.setFitBars(true);
        barChart.animateY(1000);
        barChart.getXAxis().setValueFormatter(new com.github.mikephil.charting.formatter.IndexAxisValueFormatter(
                new String[]{"", "Talabalar", "Murabbiylar", "Sport turlari", "Yangiliklar"}));
        barChart.invalidate();
    }

    // Dialog oynalari
    private void showAddBannerDialog() {
        Intent intent = new Intent(this, AddSliderActivity.class);
        startActivity(intent);
        Toast.makeText(this, "Yangilik Slider oynasi", Toast.LENGTH_SHORT).show();
    }

    private void showAddNewsDialog() {
        Intent intent = new Intent(this, AddNewsActivity.class);
        startActivity(intent);
        Toast.makeText(this, "Yangilik qo'shish oynasi", Toast.LENGTH_SHORT).show();
    }

    private void showAddSportDialog() {
        Intent intent = new Intent(this, AddSportTypeActivity.class);
        startActivity(intent);
        Toast.makeText(this, "Sport qo'shish oynasi", Toast.LENGTH_SHORT).show();
    }

    private void showAddCoachDialog() {
        Intent intent = new Intent(this, AddCoachActivity.class);
        startActivity(intent);
        Toast.makeText(this, "Murabbiy qo'shish oynasi", Toast.LENGTH_SHORT).show();
    }

    private void showAddScheduleDialog() {
        Intent intent = new Intent(this, AddSchuldeActivity.class);
        startActivity(intent);
        Toast.makeText(this, "Mashg'ulot qo'shish oynasi", Toast.LENGTH_SHORT).show();
    }

    private void showAddStudentDialog() {
        Intent intent = new Intent(this, AddUserActivity.class);
        startActivity(intent);
        Toast.makeText(this, "Talaba qo'shish oynasi", Toast.LENGTH_SHORT).show();
    }

    private void showAddResultDialog() {
        Intent intent = new Intent(this, AddResultActivity.class);
        startActivity(intent);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_dashboard) {
            // Hozirda shu activity'daman
        } else if (id == R.id.nav_students) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_coaches) {
            Intent intent = new Intent(this, MurabiyVievActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_sports) {
            Intent intent = new Intent(this, SportTypesListActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_schedule) {
            Intent intent = new Intent(this, ViewScheduleActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_results) {
            Intent intent = new Intent(this, ResultsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_news) {
            Intent intent = new Intent(this, NewsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_banners) {
            Intent intent = new Intent(this, VievSliderActivity.class);
            startActivity(intent);
            Toast.makeText(this, "BannerManagementActivity ", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_settings) {
            Toast.makeText(this, "SettingsActivity ", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.nav_logout) {
            new AlertDialog.Builder(this)
                    .setTitle("Tizimdan chiqish")
                    .setMessage("Tizimdan chiqishni xohlaysizmi?")
                    .setPositiveButton("Ha", (dialog, which) -> {
                        // Sessiya ma'lumotlarini o'chirish
                        sharedPreferences.edit().clear().apply();

                        // Login oynasiga qaytarish
                        Intent intent = new Intent(this, LoginActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish();
                    })
                    .setNegativeButton("Yo'q", null)
                    .show();
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}