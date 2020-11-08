package com.cool.smartappointmentorganizer.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.cool.smartappointmentorganizer.model.User;

public class StaticConfig {
    public static User user;

    public static int REQUEST_CODE_READ_CONTACTS = 1;

    // String Extras
    public static String appointmentId = "appointmentId";

    // Shared Preference
    private static final String PREFERENCE_LAST_NOTIFICATION_ID = "PREFERENCE_LAST_NOTIFICATION_ID";
    public static final String PREFERENCE_NOTIFICATION_ID_STORAGE = "PREFERENCE_NOTIFICATION_ID_STORAGE";

    // FIREBASE PATHS
    public static String FIREBASE_APPOINTMENT = "appointment";
    public static String FIREBASE_USER = "user";

    // notification id
    public static int getNotificationId(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        int id = sharedPreferences.getInt(PREFERENCE_LAST_NOTIFICATION_ID, 0) + 1;
        if (id == Integer.MAX_VALUE) { id = 0; }
        sharedPreferences.edit().putInt(PREFERENCE_LAST_NOTIFICATION_ID, id).apply();
        return id;
    }
}
