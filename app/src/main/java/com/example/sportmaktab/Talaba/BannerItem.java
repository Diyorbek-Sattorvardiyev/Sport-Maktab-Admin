package com.example.sportmaktab.Talaba;

public class BannerItem {
    private String title;
    private String subtitle;
    private int imageResId;

    public BannerItem(String title, String subtitle, int imageResId) {
        this.title = title;
        this.subtitle = subtitle;
        this.imageResId = imageResId;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public int getImageResId() {
        return imageResId;
    }
}

