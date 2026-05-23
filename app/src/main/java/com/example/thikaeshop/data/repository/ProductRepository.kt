package com.example.thikaeshop.data.repository

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.example.thikaeshop.data.models.Product
import kotlinx.coroutines.tasks.await
import java.util.UUID


class ProductRepository {

    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val currentUserId: String
        get() = auth.currentUser?.uid ?: ""

    private val currentUserName: String
        get() = auth.currentUser?.displayName ?: "User"

    private val currentUserPhone: String
        get() = auth.currentUser?.phoneNumber ?: ""

    // Upload product image to Firebase Storage
    suspend fun uploadProductImage(imageUri: Uri): String {
        val fileName = "products/${UUID.randomUUID()}.jpg"
        val storageRef = storage.reference.child(fileName)

        storageRef.putFile(imageUri).await()
        val downloadUrl = storageRef.downloadUrl.await()

        return downloadUrl.toString()
    }

    // Save product to Firestore
    suspend fun saveProduct(
        title: String,
        description: String,
        price: Int,
        category: String,
        isSecondHand: Boolean,
        condition: String,
        location: String,
        landmark: String,
        imageUri: Uri
    ): Product {
        // First upload image
        val imageUrl = uploadProductImage(imageUri)

        // Create product object
        val product = Product(
            id = firestore.collection("products").document().id,
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
            createdAt = com.google.firebase.Timestamp.now(),
            isAvailable = true,
            isFeatured = false
        )

        // Save to Firestore
        firestore.collection("products")
            .document(product.id)
            .set(product)
            .await()

        return product
    }

    // Get all products
    suspend fun getAllProducts(): List<Product> {
        val snapshot = firestore.collection("products")
            .whereEqualTo("isAvailable", true)
            .orderBy("createdAt", com.google.firebase.firestore.Query.Direction.DESCENDING)
            .get()
            .await()

        return snapshot.documents.mapNotNull { it.toObject(Product::class.java) }
    }

    // Get products by category
    suspend fun getProductsByCategory(category: String): List<Product> {
        val snapshot = firestore.collection("products")
            .whereEqualTo("category", category)
            .whereEqualTo("isAvailable", true)
            .get()
            .await()

        return snapshot.documents.mapNotNull { it.toObject(Product::class.java) }
    }

    // Get second-hand products (Student Exchange)
    suspend fun getSecondHandProducts(): List<Product> {
        val snapshot = firestore.collection("products")
            .whereEqualTo("isSecondHand", true)
            .whereEqualTo("isAvailable", true)
            .get()
            .await()

        return snapshot.documents.mapNotNull { it.toObject(Product::class.java) }
    }

    // Get featured products (admin promoted)
    suspend fun getFeaturedProducts(): List<Product> {
        val snapshot = firestore.collection("products")
            .whereEqualTo("isFeatured", true)
            .whereEqualTo("isAvailable", true)
            .get()
            .await()

        return snapshot.documents.mapNotNull { it.toObject(Product::class.java) }
    }
    // Add this function to your SupabaseProductRepository class
    suspend fun searchProducts(query: String): List<Product> {
        if (query.isEmpty()) {
            return getAllProducts()
        }

        // Get all products and filter locally (simpler for now)
        // Later you can implement full-text search in Supabase
        val allProducts = getAllProducts()
        return allProducts.filter { product ->
            product.title.contains(query, ignoreCase = true) ||
                    product.description.contains(query, ignoreCase = true) ||
                    product.category.contains(query, ignoreCase = true)
        }
    }

    // Admin: Promote product to featured
    suspend fun promoteToFeatured(productId: String) {
        firestore.collection("products")
            .document(productId)
            .update("isFeatured", true)
            .await()
    }

    // Admin: Remove product
    suspend fun removeProduct(productId: String) {
        firestore.collection("products")
            .document(productId)
            .update("isAvailable", false)
            .await()
    }
}