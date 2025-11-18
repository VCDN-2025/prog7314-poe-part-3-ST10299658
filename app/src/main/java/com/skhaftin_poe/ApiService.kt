
package com.skhaftin_poe

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {

    @GET("api/users/profile")
    suspend fun getProfile(): Response<ApiResponse<UserProfile>>

    @GET("api/users/profile")
    suspend fun getUserProfile(): Response<ApiResponse<UserProfile>>

    @PUT("api/users/fcm-token")
    suspend fun updateFCMToken(@Body request: UpdateFCMTokenRequest): Response<ApiResponse<Any>>

    @POST("api/notifications/send-test")
    suspend fun sendTestNotification(@Body request: TestNotificationRequest): Response<ApiResponse<Any>>
}

