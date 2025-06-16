package com.example.sportmaktab.Admin;

import com.google.gson.annotations.SerializedName;

public class SportType {
    @SerializedName("id")
    private int id;

    @SerializedName("name")
    private String name;

    @SerializedName("description")
    private String description;

    @SerializedName("image_path")
    private String imagePath;

    @SerializedName("created_at")
    private String createdAt;

    // Konstruktorlar
    public SportType() {}

    public SportType(int id, String name, String description, String imagePath, String createdAt) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.imagePath = imagePath;
        this.createdAt = createdAt;
    }

    // Getter va Setterlar
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}