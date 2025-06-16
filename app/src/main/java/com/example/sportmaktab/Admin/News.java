package com.example.sportmaktab.Admin;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class News {
    @SerializedName("id")
    private int id;

    @SerializedName("title")
    private String title;

    @SerializedName("content")
    private String content;

    @SerializedName("date")
    private String date;

    @SerializedName("created_at")
    private String createdAt;

    @SerializedName("images")
    private List<String> imageUrls;

    public News() {
        this.imageUrls = new ArrayList<>();
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }

    public List<String> getImageUrls() {
        if (imageUrls == null) {
            return new ArrayList<>();
        }
        return imageUrls.stream()
                .map(url -> url != null ? url.replace("\\", "/") : "") // Backslash’ni slash’ga almashtirish
                .collect(Collectors.toList());
    }

    public void setImageUrls(List<String> imageUrls) {
        this.imageUrls = (imageUrls != null) ? new ArrayList<>(imageUrls) : new ArrayList<>();
    }
}