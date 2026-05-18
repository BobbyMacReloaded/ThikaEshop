package com.example.thikaeshop.data.models

data class AdminUser(
    val id: String,
    val name: String,
    val email: String,
    val phone: String,
    val isVerified: Boolean,
    val isSuspended: Boolean,
    val joinDate: String
)

data class AdminTransaction(
    val id: String,
    val buyer: String,
    val seller: String,
    val amount: Int,
    val status: String,
    val escrowHeld: Boolean,
    val date: String
)

data class AdminProduct(
    val id: String,
    val name: String,
    val seller: String,
    val price: Int,
    val isSecondHand: Boolean,
    val status: String,
    val reported: Boolean
)

data class AdminDispute(
    val id: String,
    val orderId: String,
    val buyer: String,
    val seller: String,
    val amount: Int,
    val reason: String,
    val status: String
)

data class RiderApplication(
    val id: String,
    val name: String,
    val phone: String,
    val idNumber: String,
    val experience: String,
    val status: String
)

data class ReportedItem(
    val id: String,
    val productName: String,
    val seller: String,
    val reason: String,
    val reportedBy: String,
    val status: String
)
data class PendingVerification(
    val id: String,
    val userId: String,
    val studentId: String,
    val fullName: String,
    val university: String,
    val idPhotoUrl: String,
    val submittedAt: String,
    val status: String = "pending"
)
data class PendingLandmark(
    val id: String,
    val userId: String,
    val userName: String,
    val area: String,
    val landmarkName: String,
    val latitude: Double,
    val longitude: Double,
    val submittedAt: String,
    val status: String = "pending"
)
data class PendingDelivery(
    val orderId: String,
    val buyerName: String,
    val buyerPhone: String,
    val deliveryLocation: String,
    val landmark: String,
    val items: String,
    val totalAmount: Int,
    val status: String, // "pending", "assigned", "picked", "delivered"
    val createdAt: String
)

data class AvailableRider(
    val id: String,
    val name: String,
    val phone: String,
    val rating: Double,
    val currentLocation: String,
    val isAvailable: Boolean
)

data class AssignedDelivery(
    val orderId: String,
    val riderId: String,
    val riderName: String,
    val assignedAt: String,
    val status: String,
    val estimatedDelivery: String
)
// Sample Data
object SampleAdminData {
    val pendingVerifications = listOf(
        PendingVerification("1", "user_001", "BIT/2024/61120", "Emmanuel Muriuki", "Mount Kenya University", "", "2024-01-15 10:30 AM"),
        PendingVerification("2", "user_002", "COM/2024/12345", "Jane Wanjiku", "Thika Technical Institute", "", "2024-01-15 09:15 AM")
    )

    val pendingLandmarks = listOf(
        PendingLandmark("1", "user_001", "Emmanuel Muriuki", "Landless, Thika", "Near Blue Water Tank", -1.0385, 37.0839, "2024-01-15 11:00 AM"),
        PendingLandmark("2", "user_004", "Mary Achieng", "Kiganjo, Thika", "Behind shopping centre", -1.0450, 37.0900, "2024-01-14 02:30 PM")
    )

    val users = listOf(
        AdminUser("1", "Emmanuel Muriuki", "emmanuel@mku.ac.ke", "+254711234567", true, false, "2024-01-15"),
        AdminUser("2", "Jane Wanjiku", "jane@ttc.ac.ke", "+254722345678", false, false, "2024-02-01"),
        AdminUser("3", "Peter Otieno", "peter@mku.ac.ke", "+254733456789", true, true, "2024-01-20")
    )

    val transactions = listOf(
        AdminTransaction("ORD-001", "Emmanuel Muriuki", "John Kimani", 450, "Delivered", true, "2024-01-15"),
        AdminTransaction("ORD-002", "Jane Wanjiku", "Mary Wanjiku", 12000, "Processing", true, "2024-01-16"),
        AdminTransaction("ORD-003", "Peter Otieno", "James Mwangi", 800, "Shipped", true, "2024-01-14")
    )

    val products = listOf(
        AdminProduct("1", "Programming Textbook", "John Kimani", 450, true, "Active", false),
        AdminProduct("2", "Samsung Galaxy A14", "Mary Wanjiku", 12000, true, "Active", true),
        AdminProduct("3", "Fresh Vegetables", "Local Vendor", 250, false, "Active", false)
    )

    val disputes = listOf(
        AdminDispute("1", "ORD-001", "Emmanuel Muriuki", "John Kimani", 450, "Item not as described", "Pending"),
        AdminDispute("2", "ORD-002", "Jane Wanjiku", "Mary Wanjiku", 12000, "Not delivered", "In Review")
    )

    val riderApplications = listOf(
        RiderApplication("1", "James Mwangi", "+254711234567", "ID123456", "2 years", "Pending"),
        RiderApplication("2", "Ann Wanjiru", "+254722345678", "ID789012", "1 year", "Pending")
    )

    val reportedItems = listOf(
        ReportedItem("1", "Samsung Galaxy A14", "Mary Wanjiku", "Fake product", "John Doe", "Pending"),
        ReportedItem("2", "Used Textbook", "Peter Otieno", "Missing pages", "Jane Doe", "Reviewed")
    )
    val pendingDeliveries = listOf(
        PendingDelivery(
            orderId = "ORD-001",
            buyerName = "Emmanuel Muriuki",
            buyerPhone = "+254711234567",
            deliveryLocation = "Landless, Thika",
            landmark = "Near Blue Water Tank",
            items = "Programming Textbook (1)",
            totalAmount = 450,
            status = "pending",
            createdAt = "2024-01-16 10:30 AM"
        ),
        PendingDelivery(
            orderId = "ORD-002",
            buyerName = "Jane Wanjiku",
            buyerPhone = "+254722345678",
            deliveryLocation = "Kiganjo, Thika",
            landmark = "Opposite Church",
            items = "Samsung Galaxy A14 (1)",
            totalAmount = 12000,
            status = "pending",
            createdAt = "2024-01-16 09:15 AM"
        )
    )

    val availableRiders = listOf(
        AvailableRider("1", "James Mwangi", "+254711234567", 4.8, "Thika Town", true),
        AvailableRider("2", "Ann Wanjiru", "+254722345678", 4.5, "Landless", true),
        AvailableRider("3", "Peter Otieno", "+254733456789", 4.2, "Kiganjo", false)
    )

    val assignedDeliveries = listOf(
        AssignedDelivery(
            orderId = "ORD-003",
            riderId = "1",
            riderName = "James Mwangi",
            assignedAt = "2024-01-15 11:00 AM",
            status = "picked",
            estimatedDelivery = "15-20 minutes"
        )
    )
}