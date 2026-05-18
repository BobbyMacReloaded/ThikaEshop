package com.example.thikaeshop.data.models

data class SecondHandItem(
    val id: String,
    val title: String,
    val price: Int,
    val originalPrice: Int? = null,
    val description: String,
    val icon: String,
    val condition: String, // New, Like New, Good, Fair
    val sellerName: String,
    val sellerVerified: Boolean,
    val sellerRating: Double,
    val location: String,
    val landmark: String,
    val category: String // Textbooks, Electronics, Household
)
val sampleSecondHandItems = listOf(
    SecondHandItem(
        id = "1",
        title = "Programming Textbook - Kotlin",
        price = 450,
        originalPrice = 1200,
        description = "Like new, no highlights or marks",
        icon = "📚",
        condition = "Like New",
        sellerName = "John Kimani",
        sellerVerified = true,
        sellerRating = 4.8,
        location = "Landless, Thika",
        landmark = "Near Blue Water Tank",
        category = "Textbooks"
    ),
    SecondHandItem(
        id = "2",
        title = "Scientific Calculator",
        price = 800,
        originalPrice = 2500,
        description = "Good condition, works perfectly",
        icon = "📟",
        condition = "Good",
        sellerName = "Mary Wanjiku",
        sellerVerified = true,
        sellerRating = 4.5,
        location = "Kiganjo, Thika",
        landmark = "Opposite Church",
        category = "Electronics"
    ),
    SecondHandItem(
        id = "3",
        title = "Bed Sheet Set",
        price = 1200,
        originalPrice = 3000,
        description = "Single size, used for 3 months",
        icon = "🛏️",
        condition = "Good",
        sellerName = "Peter Otieno",
        sellerVerified = false,
        sellerRating = 4.2,
        location = "Kiandutu, Thika",
        landmark = "Next to Shop",
        category = "Household"
    ),
    SecondHandItem(
        id = "4",
        title = "Samsung Galaxy A14",
        price = 12000,
        originalPrice = 22000,
        description = "6 months old, original box included",
        icon = "📱",
        condition = "Like New",
        sellerName = "James Mwangi",
        sellerVerified = true,
        sellerRating = 4.9,
        location = "Landless, Thika",
        landmark = "Behind Hostel",
        category = "Electronics"
    ),
    SecondHandItem(
        id = "5",
        title = "Introduction to Algorithms",
        price = 350,
        originalPrice = 1800,
        description = "Some highlights, still usable",
        icon = "📚",
        condition = "Fair",
        sellerName = "Grace Achieng",
        sellerVerified = true,
        sellerRating = 4.3,
        location = "Kiganjo, Thika",
        landmark = "Near School Gate",
        category = "Textbooks"
    ),
    SecondHandItem(
        id = "6",
        title = "Desk Lamp",
        price = 500,
        originalPrice = 1500,
        description = "LED lamp, works perfectly",
        icon = "💡",
        condition = "Good",
        sellerName = "David Kamau",
        sellerVerified = false,
        sellerRating = 4.0,
        location = "Kiandutu, Thika",
        landmark = "Opposite Church",
        category = "Household"
    )
)