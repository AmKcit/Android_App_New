package com.example.android_app_new;

public class AdminManager {
    // Hardcoded admin credentials
    private static final String ADMIN_EMAIL = "admin@airquality.com";
    private static final String ADMIN_PASSWORD = "Admin@123456";

    /**
     * Check if provided credentials are admin credentials
     * @param email The email to check
     * @param password The password to check
     * @return true if credentials match admin credentials
     */
    public static boolean isAdmin(String email, String password) {
        if (email == null || password == null) {
            return false;
        }
        return email.trim().equalsIgnoreCase(ADMIN_EMAIL) &&
               password.equals(ADMIN_PASSWORD);
    }

    /**
     * Get the admin email
     * @return admin email
     */
    public static String getAdminEmail() {
        return ADMIN_EMAIL;
    }

    /**
     * Get the admin password (only for display purposes)
     * @return admin password
     */
    public static String getAdminPassword() {
        return ADMIN_PASSWORD;
    }
}

