package com.hafizzaturrahim.tambang;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class SessionManager {
    // Shared Preferences
    SharedPreferences pref;

    // Editor for Shared preferences
    Editor editor;

    // Context
    Context mcontext;

    // Shared pref mode
    int PRIVATE_MODE = 0;

    // Sharedpref file username
    private static final String PREF_NAME = "tambang";

    // All Shared Preferences Keys
    private static final String isLogin = "IsLoggedIn";
    private static final String keyUsername = "name";
    private static final String keyId = "id_user";
    private static final String keyLevel = "level";

    // Constructor
    public SessionManager(Context context) {
        this.mcontext = context;
        pref = mcontext.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    /**
     * Create login session
     */
    public void createLoginSession(String username, String id) {
        // Storing login value as TRUE
        editor.putBoolean(isLogin, true);

        // Storing name in pref
        editor.putString(keyUsername, username);
        editor.putString(keyId, id);
        // commit changes
        editor.commit();

        Log.d("Create Session", username + ", " + id + " sukses");
    }

    public void setLatitude(float latitude) {
        editor.putFloat("lat", latitude);
        editor.commit();
    }

    public float getLatitude() {
        return pref.getFloat("lat", 0);
    }

    public void setLongitude(float longitude) {
        editor.putFloat("lng", longitude);
        editor.commit();
    }

    public float getLongitude() {
        return pref.getFloat("lng", 0);
    }

    public String getUsername() {
        return pref.getString(keyUsername, null);
    }

    public String getIdLogin() {
        return pref.getString(keyId, null);
    }

    /**
     * Clear session details
     */
    public void logoutUser() {
        // Clearing all data from Shared Preferences
        editor.clear();
        editor.commit();

    }

    // Get Login State
    public boolean isLoggedIn() {
        return pref.getBoolean(isLogin, false);
    }
}