package com.example.sportmaktab.Admin;

import com.google.gson.annotations.SerializedName;

public class Slider {
    @SerializedName("id")
    private int id;

    @SerializedName("school_name")
    private String schoolName;

    @SerializedName("image_path")
    private String imagePath;

    @SerializedName("description")
    private String description;

    @SerializedName("created_at")
    private String createdAt;

    // Konstruktorlar
    public Slider() {}

    public Slider(int id, String schoolName, String imagePath, String description, String createdAt) {
        this.id = id;
        this.schoolName = schoolName;
        this.imagePath = imagePath;
        this.description = description;
        this.createdAt = createdAt;
    }

    // Getter va Setterlar
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getSchoolName() { return schoolName; }
    public void setSchoolName(String schoolName) { this.schoolName = schoolName; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}