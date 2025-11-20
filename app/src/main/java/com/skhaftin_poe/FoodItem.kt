package com.skhaftin_poe

data class FoodItem(
    val foodId: String?,
    val userId: String?,
    val foodName: String,
    val description: String,
    val category: String,
    val expiryDate: String,
    val urgency: String,
    val quantity: String,
    val imageUrl: String?,
    val createdAt: String?,
    val updatedAt: String?
)

data class CreateFoodItemRequest(
    val foodName: String,
    val description: String,
    val category: String,
    val expiryDate: String,
    val urgency: String,
    val quantity: String,
    val imageUrl: String?
)

data class FoodItemsResponse(
    val success: Boolean,
    val data: FoodItemsData?,
    val message: String?,
    val count: Int?
)

data class FoodItemsData(
    val foodItems: List<FoodItem>?
)