package com.example.thikaeshop.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.example.thikaeshop.data.models.OrderDisplay
import com.example.thikaeshop.data.models.OrderInsert
import com.example.thikaeshop.data.models.OrderTrackingData
import com.example.thikaeshop.data.models.Product
import com.example.thikaeshop.data.models.UserProfile
import com.example.thikaeshop.utils.SupabaseClient
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.query.Order as PostgrestOrder
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

        val supabaseUrl =
            "https://ylzlxqxvlqdzzxuhdjzi.supabase.co"

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

        val imageUrl = uploadProductImageBytes(
            imageBytes = imageBytes,
            extension = imageExtension
        )

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

        SupabaseClient.database
            .from("products")
            .insert(product)

        product
    }

    suspend fun getAllProducts(): List<Product> =
        withContext(Dispatchers.IO) {

            try {

                SupabaseClient.database
                    .from("products")
                    .select {
                        filter {
                            eq("is_available", true)
                        }

                        order(
                            column = "created_at",
                            order = PostgrestOrder.DESCENDING
                        )
                    }
                    .decodeList<Product>()

            } catch (e: Exception) {

                e.printStackTrace()
                emptyList()
            }
        }

    suspend fun getProductsByCategory(
        category: String
    ): List<Product> = withContext(Dispatchers.IO) {

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

    suspend fun searchProducts(
        query: String
    ): List<Product> = withContext(Dispatchers.IO) {

        try {

            if (query.isEmpty()) {
                return@withContext getAllProducts()
            }

            getAllProducts().filter { product ->

                product.title.contains(
                    query,
                    ignoreCase = true
                ) ||

                        product.description.contains(
                            query,
                            ignoreCase = true
                        ) ||

                        product.category.contains(
                            query,
                            ignoreCase = true
                        )
            }

        } catch (e: Exception) {

            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun deleteProduct(
        productId: String
    ) = withContext(Dispatchers.IO) {

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

    suspend fun getProductById(
        productId: String
    ): Product? = withContext(Dispatchers.IO) {

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
    // USER PROFILE FUNCTIONS
    // =========================================================

    suspend fun getUserProfile(
        userId: String
    ): UserProfile? = withContext(Dispatchers.IO) {

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

    suspend fun saveUserProfile(
        userProfile: UserProfile
    ) = withContext(Dispatchers.IO) {

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

    suspend fun promoteToFeatured(
        productId: String
    ) = withContext(Dispatchers.IO) {

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

    suspend fun removeProduct(
        productId: String
    ) = withContext(Dispatchers.IO) {

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

                        order(
                            column = "created_at",
                            order = PostgrestOrder.DESCENDING
                        )
                    }
                    .decodeList<OrderDisplay>()

            } catch (e: Exception) {

                e.printStackTrace()
                emptyList()
            }
        }

    suspend fun getOrderById(
        orderId: String
    ): OrderTrackingData? = withContext(Dispatchers.IO) {

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

    suspend fun updateOrderStatus(
        orderId: String,
        status: String
    ) = withContext(Dispatchers.IO) {

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
}