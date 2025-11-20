package com.skhaftin_poe

import android.content.Context
import android.content.SharedPreferences
import android.util.Log

object SharedPrefs {
    private const val PREFS_NAME = "Skhaftin_prefs"
    private const val KEY_AUTH_TOKEN = "auth_token"
    private const val KEY_USER_EMAIL = "user_email"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_BIOMETRIC_ENABLED = "biometric_enabled"
    private const val KEY_BIOMETRIC_EMAIL = "biometric_email"
    private const val KEY_FIRST_LAUNCH = "first_launch"
    private const val KEY_SETTINGS_SOUND = "settings_sound"
    private const val KEY_SETTINGS_VIBRATION = "settings_vibration"

    // Notification preferences keys
    private const val KEY_NOTIFICATION_DAILY_REMINDERS = "notifications_daily_reminders"
    private const val KEY_NOTIFICATION_FOOD_UPDATES = "notifications_food_updates"
    private const val KEY_NOTIFICATION_TEST = "notifications_test"
    private const val KEY_FCM_TOKEN = "fcm_token"

    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    // Auth token management
    fun saveAuthToken(token: String) {
        prefs.edit().putString(KEY_AUTH_TOKEN, token).apply()
    }

    fun getAuthToken(): String? {
        return prefs.getString(KEY_AUTH_TOKEN, null)
    }

    // User info
    fun saveUserInfo(email: String, userId: String) {
        prefs.edit().putString(KEY_USER_EMAIL, email)
            .putString(KEY_USER_ID, userId)
            .apply()
    }

    fun getUserEmail(): String? {
        return prefs.getString(KEY_USER_EMAIL, null)
    }

    fun getUserId(): String? {
        return prefs.getString(KEY_USER_ID, null)
    }

    // Biometric Preferences
    fun setBiometricEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_BIOMETRIC_ENABLED, enabled).apply()
    }

    fun isBiometricEnabled(): Boolean {
        return prefs.getBoolean(KEY_BIOMETRIC_ENABLED, false)
    }

    fun saveBiometricEmail(email: String) {
        prefs.edit().putString(KEY_BIOMETRIC_EMAIL, email).apply()
    }

    fun getBiometricEmail(): String? {
        return prefs.getString(KEY_BIOMETRIC_EMAIL, null)
    }

    // App state
    fun isFirstLaunch(): Boolean {
        return prefs.getBoolean(KEY_FIRST_LAUNCH, true)
    }

    fun setFirstLaunchCompleted() {
        prefs.edit().putBoolean(KEY_FIRST_LAUNCH, false).apply()
    }

    // FCM token management
    fun saveFCMToken(token: String) {
        prefs.edit().putString(KEY_FCM_TOKEN, token).apply()
        Log.d("SharedPrefs", "FCM token saved: $token")
    }

    fun getFCMToken(): String? {
        return prefs.getString(KEY_FCM_TOKEN, null)
    }

    // Notification preferences
    fun saveNotificationPreferences(notificationPrefs: NotificationPreferences) {
        prefs.edit()
            .putBoolean(KEY_NOTIFICATION_DAILY_REMINDERS, notificationPrefs.dailyReminders)
            .putBoolean(KEY_NOTIFICATION_FOOD_UPDATES, notificationPrefs.foodUpdates)
            .putBoolean(KEY_NOTIFICATION_TEST, notificationPrefs.testNotifications)
            .apply()
        Log.d("SharedPrefs", "Notification preferences saved: $notificationPrefs")
    }

    fun getNotificationPreference(key: String, default: Boolean): Boolean {
        return when (key) {
            "daily_reminders" -> prefs.getBoolean(KEY_NOTIFICATION_DAILY_REMINDERS, default)
            "food_updates" -> prefs.getBoolean(KEY_NOTIFICATION_FOOD_UPDATES, default)
            "test_notifications" -> prefs.getBoolean(KEY_NOTIFICATION_TEST, default)
            else -> default
        }
    }

    fun getNotificationPreferences(): NotificationPreferences {
        return NotificationPreferences(
            dailyReminders = prefs.getBoolean(KEY_NOTIFICATION_DAILY_REMINDERS, true),
            foodUpdates = prefs.getBoolean(KEY_NOTIFICATION_FOOD_UPDATES, true),
            testNotifications = prefs.getBoolean(KEY_NOTIFICATION_TEST, true)
        )
    }

    // Check if user is logged in
    fun isUserLoggedIn(): Boolean {
        return getAuthToken() != null && getUserId() != null
    }

    // Clear all data when users logout
    fun clearUserData() {
        prefs.edit()
            .remove(KEY_AUTH_TOKEN)
            .remove(KEY_USER_EMAIL)
            .remove(KEY_USER_ID)
            .remove(KEY_BIOMETRIC_ENABLED)
            .remove(KEY_BIOMETRIC_EMAIL)
            .apply()
        // Note: We don't clear FCM token and notification preferences as they are device-specific
    }

    // Settings management
    fun saveSettings(settings: Settings) {
        prefs.edit()
            .putBoolean(KEY_SETTINGS_SOUND, settings.soundEnabled)
            .putBoolean(KEY_SETTINGS_VIBRATION, settings.vibrationEnabled)
            .apply()
    }

    fun getSettings(): Settings {
        return Settings(
            soundEnabled = prefs.getBoolean(KEY_SETTINGS_SOUND, true),
            vibrationEnabled = prefs.getBoolean(KEY_SETTINGS_VIBRATION, true)
        )
    }
}