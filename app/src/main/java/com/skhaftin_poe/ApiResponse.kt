package com.skhaftin_poe

data class ApiResponse<T>(
    val success: Boolean,
    val data: T?,
    val message: String? = null,
    val count: Int? = null
)

data class UpdateProfileRequest(
    val username: String?,
    val location: String?
)

data class BaseResponse(
    val success: Boolean,
    val message: String?
)