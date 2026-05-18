package com.example.thikaeshop.data.models


data class UserProfile(
    val userId: String = "",
    val name: String = "",
    val email: String = "",
    val phoneNumber: String = "",
    val studentId: String = "",
    val isVerified: Boolean = false,
    val memberSince: String = "",
    val profileImageUrl: String = "",
    val rating: Double = 0.0,
    val totalOrders: Int = 0,
    val totalSpent: Int = 0
)

data class ListingItem(
    val id: String,
    val title: String,
    val price: String,
    val icon: String,
    val status: String  // Active, Sold, Pending
)

data class LandmarkItem(
    val id: String,
    val area: String,
    val description: String,
    val timesUsed: String
)

object SampleProfileData {
    val userProfile = UserProfile(
        userId = "user_123",
        name = "Emmanuel Muriuki",
        email = "emmanueli@mku.ac.ke",
        phoneNumber = "+254 711 234 567",
        studentId = "BIT/2024/61120",
        isVerified = true,
        memberSince = "Jan 2024",
        rating = 4.8,
        totalOrders = 12,
        totalSpent = 8450
    )

    val myListings = listOf(
        ListingItem("1", "Programming Textbook", "KSh 450", "📚", "Active"),
        ListingItem("2", "Scientific Calculator", "KSh 800", "📟", "Active"),
        ListingItem("3", "Bed Sheet Set", "KSh 1800", "🛏️", "Sold")
    )

    val savedLandmarks = listOf(
        LandmarkItem("1", "Landless", "Near Blue Water Tank", "Used 5 times"),
        LandmarkItem("2", "Kiganjo", "Opposite Church", "Used 3 times"),
        LandmarkItem("3", "Kiandutu", "Next to Shop", "Used 2 times")
    )
}