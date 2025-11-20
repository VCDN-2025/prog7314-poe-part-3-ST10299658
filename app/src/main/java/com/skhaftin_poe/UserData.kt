package com.skhaftin_poe


data class UserProfileResponse(
    val success: Boolean,
    val data: UserData?,
    val message: String?
)

data class UserData(
    val user: User?
)

data class User(
    val userId: String?,
    val email: String?,
    val username: String?,
    val location: String?,
    val createdAt: String?,
    val updatedAt: String?
)

data class UserProfile(
    val success: Boolean,
    val data: UserData?,
    val message: String?
)