package com.example.sportmaktab.Admin;

import com.google.gson.annotations.SerializedName;

public class Coach {
    @SerializedName("id")
    private int id;

    @SerializedName("first_name")
    private String firstName;

    @SerializedName("last_name")
    private String lastName;

    @SerializedName("birth_date")
    private String birthDate;

    @SerializedName("phone")
    private String phone;

    @SerializedName("sport_type_id")
    private int sportTypeId;

    @SerializedName("sport_name")
    private String sportName;

    @SerializedName("login")
    private String login;

    @SerializedName("created_at")
    private String createdAt;

    // Konstruktorlar
    public Coach() {}

    // Getter va Setterlar
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getBirthDate() { return birthDate; }
    public void setBirthDate(String birthDate) { this.birthDate = birthDate; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public int getSportTypeId() { return sportTypeId; }
    public void setSportTypeId(int sportTypeId) { this.sportTypeId = sportTypeId; }

    public String getSportName() { return sportName; }
    public void setSportName(String sportName) { this.sportName = sportName; }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}