package com.example.thikaeshop.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.example.thikaeshop.data.models.Product
import com.example.thikaeshop.data.models.UserProfile
import com.example.thikaeshop.utils.SupabaseClient
import com.google.firebase.Timestamp
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.UUID

class ProductRepository {

    private val auth = FirebaseAuth.getInstance()

    val currentUserId: String
        get() = auth.currentUser?.uid ?: "guest_user"

    private val currentUserName: String
        get() = auth.currentUser?.displayName ?: "User"

    private val currentUserPhone: String
        get() = auth.currentUser?.phoneNumber ?: ""

    // ========== PRODUCT FUNCTIONS ==========

    suspend fun uploadProductImageBytes(imageBytes: ByteArray, extension: String = "jpg"): String =
        withContext(Dispatchers.IO) {
            val fileName = "${UUID.randomUUID()}.$extension"
            SupabaseClient.client.storage["product-images"].upload(fileName, imageBytes)
            val supabaseUrl = "https://ylzlxqxvlqdzzxuhdjzi.supabase.co"
            "$supabaseUrl/storage/v1/object/public/product-images/$fileName"
        }

    suspend fun saveProduct(
        title: String,
        description: String,
        price: Int,
        category: String,
        isSecondHand: Boolean,
        condition: String,
        location: String,
        landmark: String,
        imageBytes: ByteArray,
        imageExtension: String = "jpg"
    ): Product = withContext(Dispatchers.IO) {
        val imageUrl = uploadProductImageBytes(imageBytes, imageExtension)

        val product = Product(
            id = UUID.randomUUID().toString(),
            title = title,
            description = description,
            price = price,
            imageUrl = imageUrl,
            sellerId = currentUserId,
            sellerName = currentUserName,
            sellerPhone = currentUserPhone,
            isSecondHand = isSecondHand,
            category = category,
            location = location,
            landmark = landmark,
            condition = if (isSecondHand) condition else "",
            isAvailable = true,
            isFeatured = false
        )

        SupabaseClient.database["products"].insert(product)
        product
    }

    suspend fun getAllProducts(): List<Product> = withContext(Dispatchers.IO) {
        SupabaseClient.database["products"]
            .select {
                filter { eq("is_available", true) }
                order("created_at", Order.DESCENDING)
            }
            .decodeList<Product>()
    }

    suspend fun getProductsByCategory(category: String): List<Product> = withContext(Dispatchers.IO) {
        SupabaseClient.database["products"]
            .select {
                filter {
                    eq("category", category)
                    eq("is_available", true)
                }
            }
            .decodeList<Product>()
    }

    suspend fun getSecondHandProducts(): List<Product> = withContext(Dispatchers.IO) {
        SupabaseClient.database["products"]
            .select {
                filter {
                    eq("is_second_hand", true)
                    eq("is_available", true)
                }
            }
            .decodeList<Product>()
    }

    suspend fun getFeaturedProducts(): List<Product> = withContext(Dispatchers.IO) {
        SupabaseClient.database["products"]
            .select {
                filter {
                    eq("is_featured", true)
                    eq("is_available", true)
                }
            }
            .decodeList<Product>()
    }

    suspend fun searchProducts(query: String): List<Product> = withContext(Dispatchers.IO) {
        if (query.isEmpty()) return@withContext getAllProducts()
        getAllProducts().filter { product ->
            product.title.contains(query, ignoreCase = true) ||
                    product.description.contains(query, ignoreCase = true) ||
                    product.category.contains(query, ignoreCase = true)
        }
    }

    suspend fun deleteProduct(productId: String) = withContext(Dispatchers.IO) {
        SupabaseClient.database["products"]
            .delete {
                filter { eq("id", productId) }
            }
    }

    suspend fun updateProduct(
        productId: String,
        title: String,
        description: String,
        price: Int,
        category: String,
        location: String,
        landmark: String
    ) = withContext(Dispatchers.IO) {
        SupabaseClient.database["products"]
            .update(
                update = {
                    this["title"] = title
                    this["description"] = description
                    this["price"] = price
                    this["category"] = category
                    this["location"] = location
                    this["landmark"] = landmark
                }
            ) {
                filter { eq("id", productId) }
            }
    }

    suspend fun getProductById(productId: String): Product? = withContext(Dispatchers.IO) {
        SupabaseClient.database["products"]
            .select {
                filter { eq("id", productId) }
            }
            .decodeSingleOrNull<Product>()
    }

    // ========== USER PROFILE FUNCTIONS ==========

    suspend fun getUserProfile(userId: String): UserProfile? = withContext(Dispatchers.IO) {
        try {
            SupabaseClient.database["users"]
                .select {
                    filter { eq("id", userId) }
                }
                .decodeSingleOrNull<UserProfile>()
        } catch (e: Exception) {
            null
        }
    }

    suspend fun saveUserProfile(userProfile: UserProfile) = withContext(Dispatchers.IO) {
        SupabaseClient.database["users"]
            .upsert(userProfile)
    }

    suspend fun updateUserProfile(
        userId: String,
        name: String,
        email: String,
        phone: String
    ) = withContext(Dispatchers.IO) {
        SupabaseClient.database["users"]
            .update(
                update = {
                    this["name"] = name
                    this["email"] = email
                    this["phone"] = phone
                }
            ) {
                filter { eq("id", userId) }
            }
    }

    // Admin functions
    suspend fun promoteToFeatured(productId: String) = withContext(Dispatchers.IO) {
        SupabaseClient.database["products"].update(
            update = { this["is_featured"] = true },
            request = { filter { eq("id", productId) } }
        )
    }

    suspend fun removeProduct(productId: String) = withContext(Dispatchers.IO) {
        SupabaseClient.database["products"].update(
            update = { this["is_available"] = false },
            request = { filter { eq("id", productId) } }
        )
    }
    // ========== ORDER FUNCTIONS ==========

    // Generate unique order number
    private fun generateOrderNumber(): String {
        return "ORD-${System.currentTimeMillis()}"
    }

    // Save order from checkout
    suspend fun saveOrder(
        productId: String,
        productName: String,
        productImageUrl: String,
        quantity: Int,
        totalAmount: Int,
        sellerId: String,
        deliveryLocation: String,
        landmark: String,
        paymentMethod: String
    ): String = withContext(Dispatchers.IO) {
        val orderId = UUID.randomUUID().toString()
        val orderNumber = generateOrderNumber()

        val order = mapOf(
            "id" to orderId,
            "order_number" to orderNumber,
            "buyer_id" to currentUserId,
            "seller_id" to sellerId,
            "product_id" to productId,
            "product_name" to productName,
            "product_image_url" to productImageUrl,
            "quantity" to quantity,
            "total_amount" to totalAmount,
            "status" to "pending",
            "delivery_location" to deliveryLocation,
            "landmark" to landmark,
            "payment_method" to paymentMethod,
            "is_escrow_held" to true,
            "created_at" to Timestamp.now(),
            "updated_at" to Timestamp.now()
        )

        SupabaseClient.database["orders"].insert(order)
        orderId
    }

    // Get all orders for current user (as buyer or seller)
    suspend fun getMyOrders(): List<Map<String, Any>> = withContext(Dispatchers.IO) {
        SupabaseClient.database["orders"]
            .select {
                filter {
                    or {
                        eq("buyer_id", currentUserId)
                        eq("seller_id", currentUserId)
                    }
                }
                order("created_at", Order.DESCENDING)
            }
            .decodeList()
    }

    // Get single order by ID
    suspend fun getOrderById(orderId: String): Map<String, Any>? = withContext(Dispatchers.IO) {
        try {
            SupabaseClient.database["orders"]
                .select {
                    filter { eq("id", orderId) }
                }
                .decodeSingleOrNull()
        } catch (e: Exception) {
            null
        }
    }

    // Update order status
    suspend fun updateOrderStatus(orderId: String, status: String) = withContext(Dispatchers.IO) {
        SupabaseClient.database["orders"]
            .update(
                update = {
                    this["status"] = status
                    this["updated_at"] = Timestamp.now()
                }
            ) {
                filter { eq("id", orderId) }
            }
    }

// ========== REVIEW FUNCTIONS ==========

    // Save a review
    suspend fun saveReview(
        orderId: String,
        productId: String,
        rating: Int,
        comment: String
    ) = withContext(Dispatchers.IO) {
        val reviewId = UUID.randomUUID().toString()
        val review = mapOf(
            "id" to reviewId,
            "order_id" to orderId,
            "product_id" to productId,
            "user_id" to currentUserId,
            "rating" to rating,
            "comment" to comment,
            "created_at" to Timestamp.now()
        )
        SupabaseClient.database["reviews"].insert(review)

        // Update product's seller rating (optional: average rating calculation)
    }
}