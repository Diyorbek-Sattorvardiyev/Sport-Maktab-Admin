package com.example.sportmaktab.Admin;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

public class DashboardPagerAdapter extends FragmentStateAdapter {
    private static final int TAB_COUNT = 5;

    public DashboardPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        // Har bir tab uchun tegishli fragment'ni qaytarish
        switch (position) {
            case 0:
                return new StudentsFragment();
            case 1:
                return new StudentsFragment();
            case 2:
                return new StudentsFragment();
            case 3:
                return new StudentsFragment();
            case 4:
                return new StudentsFragment();
            default:
                return new StudentsFragment(); // Xatolik bo'lmasligi uchun default qaytaramiz
        }
    }

    @Override
    public int getItemCount() {
        return TAB_COUNT;
    }
}