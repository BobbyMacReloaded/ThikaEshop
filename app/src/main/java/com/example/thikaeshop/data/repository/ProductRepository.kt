package com.example.thikaeshop.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.example.thikaeshop.data.models.OrderDisplay
import com.example.thikaeshop.data.models.OrderInsert
import com.example.thikaeshop.data.models.OrderTrackingData
import com.example.thikaeshop.data.models.Product
import com.example.thikaeshop.data.models.Subscription
import com.example.thikaeshop.data.models.UserProfile
import com.example.thikaeshop.utils.SupabaseClient
import com.google.firebase.Timestamp
import io.github.jan.supabase.storage.storage
import io.github.jan.supabase.postgrest.query.Order as PostgrestOrder
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.UUID

class ProductRepository {

    private val auth = FirebaseAuth.getInstance()

    val currentUserId: String
        get() = auth.currentUser?.uid ?: "guest_user"

    private val currentUserName: String
        get() = auth.currentUser?.displayName ?: "User"

    private val currentUserPhone: String
        get() = auth.currentUser?.phoneNumber ?: ""

    // =========================================================
    // DSA - SORTING ALGORITHMS
    // =========================================================

    fun quickSortByPrice(products: List<Product>, ascending: Boolean = true): List<Product> {
        if (products.size <= 1) return products

        val pivot = products[products.size / 2]
        val left = products.filter { if (ascending) it.price < pivot.price else it.price > pivot.price }
        val middle = products.filter { it.price == pivot.price }
        val right = products.filter { if (ascending) it.price > pivot.price else it.price < pivot.price }

        return quickSortByPrice(left, ascending) + middle + quickSortByPrice(right, ascending)
    }

    fun mergeSortByRating(products: List<Product>, ascending: Boolean = true): List<Product> {
        if (products.size <= 1) return products

        val mid = products.size / 2
        val left = mergeSortByRating(products.subList(0, mid), ascending)
        val right = mergeSortByRating(products.subList(mid, products.size), ascending)

        return merge(left, right, ascending)
    }

    private fun merge(left: List<Product>, right: List<Product>, ascending: Boolean): List<Product> {
        var i = 0
        var j = 0
        val result = mutableListOf<Product>()

        while (i < left.size && j < right.size) {
            val condition = if (ascending) {
                left[i].sellerRating <= right[j].sellerRating
            } else {
                left[i].sellerRating >= right[j].sellerRating
            }

            if (condition) {
                result.add(left[i])
                i++
            } else {
                result.add(right[j])
                j++
            }
        }

        result.addAll(left.subList(i, left.size))
        result.addAll(right.subList(j, right.size))

        return result
    }

    fun binarySearchById(products: List<Product>, targetId: String): Product? {
        val sorted = products.sortedBy { it.id }
        var left = 0
        var right = sorted.size - 1

        while (left <= right) {
            val mid = left + (right - left) / 2
            val comparison = sorted[mid].id.compareTo(targetId)

            when {
                comparison == 0 -> return sorted[mid]
                comparison < 0 -> left = mid + 1
                else -> right = mid - 1
            }
        }
        return null
    }

    // =========================================================
    // PRODUCT FUNCTIONS
    // =========================================================

    suspend fun uploadProductImageBytes(
        imageBytes: ByteArray,
        extension: String = "jpg"
    ): String = withContext(Dispatchers.IO) {

        val fileName = "${UUID.randomUUID()}.$extension"

        SupabaseClient.client.storage
            .from("product-images")
            .upload(
                path = fileName,
                data = imageBytes
            )

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

        // ★ Check if user has active subscription for visibility boost
        val boost = if (hasActiveSubscription()) 100 else 0
        val boostedUntil = if (boost > 0) {
            Instant.now().plus(30, ChronoUnit.DAYS).toString()
        } else null

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
            isFeatured = false,
            visibilityBoost = boost,      // ★ NEW
            boostedUntil = boostedUntil   // ★ NEW
        )

        SupabaseClient.database
            .from("products")
            .insert(product)

        product
    }

    suspend fun getAllProducts(): List<Product> =
        withContext(Dispatchers.IO) {

            try {
                val products = SupabaseClient.database
                    .from("products")
                    .select {
                        filter {
                            eq("is_available", true)
                        }
                    }
                    .decodeList<Product>()

                // ★ SORT: Boosted products first, then by created date
                products.sortedWith(
                    compareByDescending<Product> {
                        if (it.isBoosted) it.visibilityBoost else 0
                    }.thenByDescending { it.createdAt }
                )

            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        }

    suspend fun getProductsByCategory(category: String): List<Product> =
        withContext(Dispatchers.IO) {

            try {
                SupabaseClient.database
                    .from("products")
                    .select {
                        filter {
                            eq("category", category)
                            eq("is_available", true)
                        }
                    }
                    .decodeList<Product>()

            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        }

    suspend fun getSecondHandProducts(): List<Product> =
        withContext(Dispatchers.IO) {

            try {
                SupabaseClient.database
                    .from("products")
                    .select {
                        filter {
                            eq("is_second_hand", true)
                            eq("is_available", true)
                        }
                    }
                    .decodeList<Product>()

            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        }

    suspend fun getFeaturedProducts(): List<Product> =
        withContext(Dispatchers.IO) {

            try {
                SupabaseClient.database
                    .from("products")
                    .select {
                        filter {
                            eq("is_featured", true)
                            eq("is_available", true)
                        }
                    }
                    .decodeList<Product>()

            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        }

    suspend fun searchProducts(query: String): List<Product> =
        withContext(Dispatchers.IO) {

            try {
                if (query.isEmpty()) {
                    return@withContext getAllProducts()
                }

                getAllProducts().filter { product ->
                    product.title.contains(query, ignoreCase = true) ||
                            product.description.contains(query, ignoreCase = true) ||
                            product.category.contains(query, ignoreCase = true)
                }

            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        }

    suspend fun deleteProduct(productId: String) =
        withContext(Dispatchers.IO) {

            try {
                SupabaseClient.database
                    .from("products")
                    .delete {
                        filter {
                            eq("id", productId)
                        }
                    }

            } catch (e: Exception) {
                e.printStackTrace()
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

        try {
            SupabaseClient.database
                .from("products")
                .update(
                    {
                        set("title", title)
                        set("description", description)
                        set("price", price)
                        set("category", category)
                        set("location", location)
                        set("landmark", landmark)
                    }
                ) {
                    filter {
                        eq("id", productId)
                    }
                }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    suspend fun getProductById(productId: String): Product? =
        withContext(Dispatchers.IO) {

            try {
                SupabaseClient.database
                    .from("products")
                    .select {
                        filter {
                            eq("id", productId)
                        }
                    }
                    .decodeSingleOrNull<Product>()

            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

    // =========================================================
    // SUBSCRIPTION HELPER
    // =========================================================

    private suspend fun hasActiveSubscription(): Boolean {
        return try {
            val sub = SupabaseClient.database
                .from("subscriptions")
                .select {
                    filter {
                        eq("user_id", currentUserId)
                        eq("status", "active")
                    }
                }
                .decodeList<Subscription>()
                .firstOrNull()

            sub != null && sub.isActive && sub.tier != "free"
        } catch (e: Exception) {
            false
        }
    }

    // =========================================================
    // USER PROFILE FUNCTIONS
    // =========================================================

    suspend fun getUserProfile(userId: String): UserProfile? =
        withContext(Dispatchers.IO) {

            try {
                SupabaseClient.database
                    .from("users")
                    .select {
                        filter {
                            eq("id", userId)
                        }
                    }
                    .decodeSingleOrNull<UserProfile>()

            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

    suspend fun saveUserProfile(userProfile: UserProfile) =
        withContext(Dispatchers.IO) {

            try {
                SupabaseClient.database
                    .from("users")
                    .upsert(userProfile)

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    suspend fun updateUserProfile(
        userId: String,
        name: String,
        email: String,
        phone: String
    ) = withContext(Dispatchers.IO) {

        try {
            SupabaseClient.database
                .from("users")
                .update(
                    {
                        set("name", name)
                        set("email", email)
                        set("phone", phone)
                    }
                ) {
                    filter {
                        eq("id", userId)
                    }
                }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // =========================================================
    // ADMIN FUNCTIONS
    // =========================================================

    suspend fun promoteToFeatured(productId: String) =
        withContext(Dispatchers.IO) {

            try {
                SupabaseClient.database
                    .from("products")
                    .update(
                        {
                            set("is_featured", true)
                        }
                    ) {
                        filter {
                            eq("id", productId)
                        }
                    }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    suspend fun removeProduct(productId: String) =
        withContext(Dispatchers.IO) {

            try {
                SupabaseClient.database
                    .from("products")
                    .update(
                        {
                            set("is_available", false)
                        }
                    ) {
                        filter {
                            eq("id", productId)
                        }
                    }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    // =========================================================
    // ORDER FUNCTIONS
    // =========================================================

    private fun generateOrderNumber(): String {
        return "ORD-${System.currentTimeMillis()}"
    }

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

        try {
            val orderId = UUID.randomUUID().toString()

            val order = OrderInsert(
                id = orderId,
                orderNumber = generateOrderNumber(),
                buyerId = currentUserId,
                sellerId = sellerId,
                productId = productId,
                productName = productName,
                productImageUrl = productImageUrl,
                quantity = quantity,
                totalAmount = totalAmount,
                status = "processing",
                deliveryLocation = deliveryLocation,
                landmark = landmark,
                paymentMethod = paymentMethod,
                isEscrowHeld = true
            )

            SupabaseClient.database
                .from("orders")
                .insert(order)

            println("ORDER SAVED SUCCESSFULLY")

            orderId

        } catch (e: Exception) {
            println("SAVE ORDER ERROR")
            e.printStackTrace()
            throw e
        }
    }

    suspend fun getMyOrders(): List<OrderDisplay> =
        withContext(Dispatchers.IO) {

            try {
                SupabaseClient.database
                    .from("orders")
                    .select {
                        filter {
                            or {
                                eq("buyer_id", currentUserId)
                                eq("seller_id", currentUserId)
                            }
                        }
                        order("created_at", PostgrestOrder.DESCENDING)
                    }
                    .decodeList<OrderDisplay>()

            } catch (e: Exception) {
                e.printStackTrace()
                emptyList()
            }
        }

    suspend fun getOrderById(orderId: String): OrderTrackingData? =
        withContext(Dispatchers.IO) {

            try {
                SupabaseClient.database
                    .from("orders")
                    .select {
                        filter {
                            eq("id", orderId)
                        }
                    }
                    .decodeSingleOrNull<OrderTrackingData>()

            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }

    suspend fun updateOrderStatus(orderId: String, status: String) =
        withContext(Dispatchers.IO) {

            try {
                SupabaseClient.database
                    .from("orders")
                    .update(
                        {
                            set("status", status)
                        }
                    ) {
                        filter {
                            eq("id", orderId)
                        }
                    }

            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

    // =========================================================
    // REVIEW FUNCTIONS
    // =========================================================

    suspend fun saveReview(
        orderId: String,
        productId: String,
        rating: Int,
        comment: String
    ) = withContext(Dispatchers.IO) {

        try {
            val review = mapOf(
                "id" to UUID.randomUUID().toString(),
                "order_id" to orderId,
                "product_id" to productId,
                "user_id" to currentUserId,
                "rating" to rating,
                "comment" to comment
            )

            SupabaseClient.database
                .from("reviews")
                .insert(review)

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    // =========================================================
    // AI RECOMMENDATIONS FUNCTIONS
    // =========================================================

    suspend fun trackProductView(userId: String, productId: String) =
        withContext(Dispatchers.IO) {
            try {
                SupabaseClient.database.from("product_views").insert(
                    mapOf(
                        "user_id" to userId,
                        "product_id" to productId,
                        "viewed_at" to Timestamp.now()
                    )
                )
            } catch (e: Exception) {
                Log.e("ProductRepo", "Failed to track view: ${e.message}")
            }
        }

    suspend fun trackInteraction(userId: String, productId: String, type: String) =
        withContext(Dispatchers.IO) {
            try {
                SupabaseClient.database.from("user_interactions").insert(
                    mapOf(
                        "user_id" to userId,
                        "product_id" to productId,
                        "interaction_type" to type,
                        "created_at" to Timestamp.now()
                    )
                )
            } catch (e: Exception) {
                Log.e("ProductRepo", "Failed to track interaction: ${e.message}")
            }
        }

    suspend fun getRecommendationsForUser(userId: String, limit: Int = 10): List<Product> =
        withContext(Dispatchers.IO) {
            try {
                val userInteractions = SupabaseClient.database.from("user_interactions")
                    .select {
                        filter { eq("user_id", userId) }
                    }
                    .decodeList<Map<String, Any>>()

                val userProductIds = userInteractions
                    .mapNotNull { it["product_id"] as? String }
                    .distinct()

                if (userProductIds.isEmpty()) {
                    return@withContext getPopularProducts(limit)
                }

                val allProducts = getAllProducts()
                val userProducts = allProducts.filter { it.id in userProductIds }
                val categories = userProducts.map { it.category }.distinct()

                if (categories.isEmpty()) {
                    return@withContext getPopularProducts(limit)
                }

                val recommendations = allProducts.filter { product ->
                    product.category in categories &&
                            product.id !in userProductIds &&
                            product.isAvailable
                }

                recommendations.take(limit)
            } catch (e: Exception) {
                Log.e("ProductRepo", "Failed to get recommendations: ${e.message}")
                getPopularProducts(limit)
            }
        }

    suspend fun getPopularProducts(limit: Int = 10): List<Product> =
        withContext(Dispatchers.IO) {
            try {
                val views = SupabaseClient.database.from("product_views")
                    .select()
                    .decodeList<Map<String, Any>>()

                val viewCounts = mutableMapOf<String, Int>()
                views.forEach { view ->
                    val productId = view["product_id"] as? String
                    if (productId != null) {
                        viewCounts[productId] = (viewCounts[productId] ?: 0) + 1
                    }
                }

                val popularIds = viewCounts.toList()
                    .sortedByDescending { it.second }
                    .take(limit)
                    .map { it.first }

                if (popularIds.isEmpty()) {
                    return@withContext getAllProducts().take(limit)
                }

                getAllProducts().filter { it.id in popularIds }.take(limit)
            } catch (e: Exception) {
                Log.e("ProductRepo", "Failed to get popular products: ${e.message}")
                getAllProducts().take(limit)
            }
        }
}