package com.example.sportmaktab.Talaba;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.viewpager2.widget.ViewPager2;

import com.example.sportmaktab.R;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TalabaDashboardActivity extends AppCompatActivity {
    private ViewPager2 viewPagerBanner;
    private TabLayout sliderIndicator;
    private BannerAdapter bannerAdapter;
    private Handler sliderHandler = new Handler();
    private Runnable sliderRunnable;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_talaba_dashboard);
        viewPagerBanner = findViewById(R.id.viewPagerBanner);
        sliderIndicator = findViewById(R.id.sliderIndicator);
        List<BannerItem> bannerItems = new ArrayList<>();

        bannerItems.add(new BannerItem("Bolalar va O'smirlar Sport Maktabi",
                "Sog'lom turmush - yorqin kelajak!", R.drawable.banner_image1));
        bannerItems.add(new BannerItem("Bolalar va O'smirlar Sport Maktabi",
                "Professional murabbiylar bilan shug'ullaning", R.drawable.banner_image2));
        bannerItems.add(new BannerItem("Bolalar va O'smirlar Sport Maktabi",
                "Zamonaviy jihozlangan sport majmuasi", R.drawable.banner_image3));

        // Banner adapterni o'rnatish
        bannerAdapter = new BannerAdapter(this, bannerItems);
        viewPagerBanner.setAdapter(bannerAdapter);

        // TabLayout indikatorini o'rnatish
        TabLayoutMediator tabLayoutMediator = new TabLayoutMediator(sliderIndicator,
                viewPagerBanner, (tab, position) -> {
        });
        tabLayoutMediator.attach();

        // Avtomatik slayd effektini qo'shish
        setAutoScrollSlider();

        // Kartalar uchun click animatsiya
        setupCardAnimations();

        // Buttonlar uchun click listenerlarni o'rnatish



    }
    // Avtomatik slayder
    private void setAutoScrollSlider () {
        sliderRunnable = new Runnable() {
            @Override
            public void run() {
                int currentItem = viewPagerBanner.getCurrentItem();
                int totalCount = bannerAdapter.getItemCount();

                if (currentItem < totalCount - 1) {
                    viewPagerBanner.setCurrentItem(currentItem + 1, true);
                } else {
                    viewPagerBanner.setCurrentItem(0, true);
                }

                sliderHandler.postDelayed(this, 5000); // 3 soniyada bir slayd
            }
        };

        sliderHandler.postDelayed(sliderRunnable, 5000);
    }

    // Karta animatsiyalari
    private void setupCardAnimations () {
        List<MaterialCardView> cards = Arrays.asList(
                findViewById(R.id.cardSportTurlari),
                findViewById(R.id.cardMurabbiylar),
                findViewById(R.id.cardMashgulotJadvali),
                findViewById(R.id.cardSonggiNatijalar),
                findViewById(R.id.cardYangiliklar)
        );

        for (MaterialCardView card : cards) {
            card.setOnTouchListener((v, event) -> {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Karta bosilganda
                        ObjectAnimator scaleDownX = ObjectAnimator.ofFloat(card, "scaleX", 0.95f);
                        ObjectAnimator scaleDownY = ObjectAnimator.ofFloat(card, "scaleY", 0.95f);
                        scaleDownX.setDuration(150);
                        scaleDownY.setDuration(150);
                        AnimatorSet scaleDown = new AnimatorSet();
                        scaleDown.play(scaleDownX).with(scaleDownY);
                        scaleDown.start();
                        break;
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        // Karta qo'yib yuborilganda
                        ObjectAnimator scaleUpX = ObjectAnimator.ofFloat(card, "scaleX", 1f);
                        ObjectAnimator scaleUpY = ObjectAnimator.ofFloat(card, "scaleY", 1f);
                        scaleUpX.setDuration(150);
                        scaleUpY.setDuration(150);
                        AnimatorSet scaleUp = new AnimatorSet();
                        scaleUp.play(scaleUpX).with(scaleUpY);
                        scaleUp.start();
                        break;
                }
                return false;
            });
        }
    }

    @Override
    protected void onPause () {
        super.onPause();
        sliderHandler.removeCallbacks(sliderRunnable);
    }

    @Override
    protected void onResume () {
        super.onResume();
        sliderHandler.postDelayed(sliderRunnable, 3000);
    }
}