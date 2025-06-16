package com.example.sportmaktab.Admin;

import com.google.gson.annotations.SerializedName;

public class Student {
    @SerializedName("id")
    private int id;

    @SerializedName("first_name")
    private String firstName;

    @SerializedName("last_name")
    private String lastName;

    @SerializedName("phone")
    private String phone;

    @SerializedName("login")
    private String login;

    @SerializedName("created_at")
    private String createdAt;

    // Konstruktorlar
    public Student() {}

    public Student(int id, String firstName, String lastName, String phone, String login, String createdAt) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.phone = phone;
        this.login = login;
        this.createdAt = createdAt;
    }

    // Getter va Setterlar
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getLogin() { return login; }
    public void setLogin(String login) { this.login = login; }

    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
}