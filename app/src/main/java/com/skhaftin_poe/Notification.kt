package com.skhaftin_poe

data class UpdateFCMTokenRequest(
    val fcmToken: String
)

data class TestNotificationRequest(
    val title: String,
    val message: String
)