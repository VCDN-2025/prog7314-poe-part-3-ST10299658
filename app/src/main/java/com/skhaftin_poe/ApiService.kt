
package com.skhaftin_poe

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    @GET("api/profile")
    suspend fun getProfile(): Response<UserProfileResponse>

    @GET("api/users/profile")
    suspend fun getUserProfile(): Response<UserProfile>

    @PUT("api/users/profile")
    suspend fun updateUserProfile(@Body request: UpdateProfileRequest): Response<BaseResponse>

    @POST("api/users/fcm-token")
    suspend fun updateFCMToken(@Body request: UpdateFCMTokenRequest): Response<BaseResponse>

    @GET("api/users/notification-preferences")
    suspend fun getNotificationPreferences(): Response<NotificationPreferencesResponse>

    @POST("api/food-items")
    suspend fun createFoodItem(@Body request: CreateFoodItemRequest): Response<BaseResponse>

    @GET("api/food-items")
    suspend fun getAllFoodItems(): Response<FoodItemsResponse>

    @GET("api/food-items/my-items")
    suspend fun getMyFoodItems(): Response<FoodItemsResponse>

    @PUT("api/food-items/{foodId}")
    suspend fun updateFoodItem(@Path("foodId") foodId: String, @Body request: CreateFoodItemRequest): Response<BaseResponse>

    @DELETE("api/food-items/{foodId}")
    suspend fun deleteFoodItem(@Path("foodId") foodId: String): Response<BaseResponse>

    @POST("api/notifications/send-test")
    suspend fun sendTestNotification(@Body request: TestNotificationRequest): Response<ApiResponse<Any>>

    @PUT("api/users/notification-preferences")
    suspend fun updateNotificationPreferences(@Body request: UpdateNotificationPreferencesRequest): Response<BaseResponse>
}