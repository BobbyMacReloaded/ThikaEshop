package com.example.thikaeshop.data.models

data class Products(
    val id: String,
    val title: String,
    val price: Int,
    val originalPrice: Int? = null,
    val description: String,
    val icon: String,
    val sellerId: String,
    val sellerName: String,
    val sellerIcon: String,
    val sellerRating: Double,
    val sellerReviews: Int,
    val location: String,
    val landmark: String? = null,
    val condition: String? = null,
    val isSecondHand: Boolean
)

fun SampleData(productId: String): Products {

    return Products(
        id = productId,
        title = "Programming Textbook - Kotlin",
        price = 450,
        originalPrice = 1200,

        description = "Like new condition. No highlights or marks. Perfect for BIT students. Covers Kotlin basics to advanced topics including coroutines and Compose.",

        icon = "📚",

        sellerId = "seller_001",

        sellerName = "John Kimani",
        sellerIcon = "👨‍🎓",
        sellerRating = 4.8,
        sellerReviews = 23,

        location = "Landless, Thika",
        landmark = "Near Blue Water Tank",

        condition = "Like New",

        isSecondHand = true
    )
}