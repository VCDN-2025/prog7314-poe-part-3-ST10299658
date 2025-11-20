package com.skhaftin_poe

data class NotificationPreferences(
    val dailyReminders: Boolean = true,
    val foodUpdates: Boolean = true,
    val testNotifications: Boolean = true
)

data class NotificationPreferencesResponse(
    val success: Boolean,
    val data: NotificationPreferences? = null
)

data class UpdateNotificationPreferencesRequest(
    val dailyReminders: Boolean? = null,
    val foodUpdates: Boolean? = null,
    val testNotifications: Boolean? = null
)