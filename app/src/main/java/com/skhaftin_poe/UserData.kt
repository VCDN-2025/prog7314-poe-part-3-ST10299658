package com.skhaftin_poe

data class UserData(
    val userId: String? = null,
    val email: String,
    val displayName: String? = null,

)
data class UserProfile(
    val user: UserData?
)
data class UpdateDisplayNameRequest(
    val displayName: String
)