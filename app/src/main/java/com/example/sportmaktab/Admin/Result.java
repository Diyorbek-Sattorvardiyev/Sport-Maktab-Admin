package com.example.sportmaktab.Admin;

import com.google.gson.annotations.SerializedName;

public class Result {
    @SerializedName("id")
    private int id;

    @SerializedName("competition_name")
    private String competitionName;

    @SerializedName("date")
    private String date;

    @SerializedName("image_path")
    private String imagePath;

    @SerializedName("description")
    private String description;

    @SerializedName("created_at")
    private String createdAt;

    // Konstruktorlar
    public Result() {}

    public Result(int id, String competitionName, String date, String imagePath,
                  String description, String createdAt) {
        this.id = id;
        this.competitionName = competitionName;
        this.date = date;
        this.imagePath = imagePath;
        this.description = description;
        this.createdAt = createdAt;
    }

    // Getter va Setterlar
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCompetitionName() { return competitionName; }
    public void setCompetitionName(String competitionName) { this.competitionName = competitionName; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}