package com.example.sportmaktab.Admin;

import com.google.gson.annotations.SerializedName;

public class TrainingSchedule {
    @SerializedName("id")
    private int id;

    @SerializedName("date")
    private String date;

    @SerializedName("time")
    private String time;

    @SerializedName("sport_type_id")
    private int sportTypeId;

    @SerializedName("sport_name")
    private String sportName;

    @SerializedName("coach_id")
    private int coachId;

    @SerializedName("coach_name")
    private String coachName;

    @SerializedName("room")
    private String room;

    @SerializedName("created_at")
    private String createdAt;

    // Konstruktorlar
    public TrainingSchedule() {}

    public TrainingSchedule(int id, String date, String time, int sportTypeId, String sportName,
                            int coachId, String coachName, String room, String createdAt) {
        this.id = id;
        this.date = date;
        this.time = time;
        this.sportTypeId = sportTypeId;
        this.sportName = sportName;
        this.coachId = coachId;
        this.coachName = coachName;
        this.room = room;
        this.createdAt = createdAt;
    }

    // Getter va Setterlar
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getDate() { return date; }
    public void setDate(String date) { this.date = date; }

    public String getTime() { return time; }
    public void setTime(String time) { this.time = time; }

    public int getSportTypeId() { return sportTypeId; }
    public void setSportTypeId(int sportTypeId) { this.sportTypeId = sportTypeId; }

    public String getSportName() { return sportName; }
    public void setSportName(String sportName) { this.sportName = sportName; }

    public int getCoachId() { return coachId; }
    public void setCoachId(int coachId) { this.coachId = coachId; }

    public String getCoachName() { return coachName; }
    public void setCoachName(String coachName) { this.coachName = coachName; }

    public String getRoom() { return room; }
    public void setRoom(String room) { this.room = room; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}