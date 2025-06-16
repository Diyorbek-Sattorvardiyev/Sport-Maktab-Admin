package com.example.sportmaktab.Admin;

public class LoginResponse {
    private String token;
    private String role;
    private int id;
    private String firstName;
    private String lastName;

    // Konstruktorlar
    public LoginResponse() {}

    // Getter va Setterlar
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
}