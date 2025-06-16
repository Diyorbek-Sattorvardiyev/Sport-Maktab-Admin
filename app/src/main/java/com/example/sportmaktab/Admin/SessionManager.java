package com.example.sportmaktab.Admin;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "SportsSchoolPref";
    private static final String KEY_TOKEN = "token";
    private static final String KEY_USER_ID = "user_id";
    private static final String KEY_ROLE = "role";
    private static final String KEY_FIRST_NAME = "first_name";
    private static final String KEY_LAST_NAME = "last_name";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";

    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private Context context;

    public SessionManager(Context context) {
        this.context = context;
        pref = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = pref.edit();
    }

    public void createLoginSession(String token, int userId, String role, String firstName, String lastName) {
        editor.putString(KEY_TOKEN, token);
        editor.putInt(KEY_USER_ID, userId);
        editor.putString(KEY_ROLE, role);
        editor.putString(KEY_FIRST_NAME, firstName);
        editor.putString(KEY_LAST_NAME, lastName);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.commit();
    }

    public String getToken() {
        return "Bearer " + pref.getString(KEY_TOKEN, "");
    }

    public String getRawToken() {
        return pref.getString(KEY_TOKEN, "");
    }

    public int getUserId() {
        return pref.getInt(KEY_USER_ID, 0);
    }

    public String getRole() {
        return pref.getString(KEY_ROLE, "");
    }

    public String getFirstName() {
        return pref.getString(KEY_FIRST_NAME, "");
    }

    public String getLastName() {
        return pref.getString(KEY_LAST_NAME, "");
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public boolean isAdmin() {
        return "admin".equals(getRole());
    }

    public boolean isCoach() {
        return "coach".equals(getRole());
    }

    public boolean isStudent() {
        return "student".equals(getRole());
    }

    public void logoutUser() {
        editor.clear();
        editor.commit();
    }
}