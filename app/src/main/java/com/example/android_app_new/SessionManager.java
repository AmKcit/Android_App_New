package com.example.android_app_new;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "UserSession";
    private static final String KEY_UID = "uid";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_LOGIN_TIME = "loginTime";
    private static final long SESSION_TIMEOUT = 86400000; // 24 hours in milliseconds

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    /**
     * Create login session for user
     */
    public void createLoginSession(String uid, String email) {
        editor.putString(KEY_UID, uid);
        editor.putString(KEY_EMAIL, email);
        editor.putLong(KEY_LOGIN_TIME, System.currentTimeMillis());
        editor.apply();
    }

    /**
     * Check if user is logged in
     */
    public boolean isLoggedIn() {
        return sharedPreferences.contains(KEY_UID);
    }

    /**
     * Check if session is still valid (not expired)
     */
    public boolean isSessionValid() {
        if (!isLoggedIn()) {
            return false;
        }

        long loginTime = sharedPreferences.getLong(KEY_LOGIN_TIME, 0);
        long currentTime = System.currentTimeMillis();
        long timeDifference = currentTime - loginTime;

        // Session is valid if less than 24 hours have passed
        return timeDifference < 86400000;
    }

    /**
     * Get user UID
     */
    public String getUserUid() {
        return sharedPreferences.getString(KEY_UID, null);
    }

    /**
     * Get user email
     */
    public String getUserEmail() {
        return sharedPreferences.getString(KEY_EMAIL, null);
    }

    /**
     * Clear session - logout user
     */
    public void clearSession() {
        editor.clear();
        editor.apply();
    }
}


