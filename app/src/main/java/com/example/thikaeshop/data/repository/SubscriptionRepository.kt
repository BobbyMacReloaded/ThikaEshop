package com.example.thikaeshop.data.repository

import android.util.Log
import com.example.thikaeshop.data.models.*
import com.example.thikaeshop.utils.SupabaseClient
import com.google.firebase.auth.FirebaseAuth
import io.github.jan.supabase.postgrest.query.Order
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.UUID

class SubscriptionRepository {

    private val auth = FirebaseAuth.getInstance()

    // FIXED: Handle null user ID
    private val currentUserId: String
        get() {
            val uid = auth.currentUser?.uid
            return if (uid.isNullOrEmpty()) {
                Log.w("SubscriptionRepo", "No Firebase user found, using 'guest_user'")
                "guest_user"
            } else {
                uid
            }
        }

    // ── Get current user's active subscription ─────────────────────────────────
    suspend fun getMySubscription(): Subscription = withContext(Dispatchers.IO) {
        try {
            val results = SupabaseClient.database.from("subscriptions")
                .select {
                    filter {
                        eq("user_id", currentUserId)
                        eq("status", "active")
                    }
                    order("expires_at", Order.DESCENDING)
                }
                .decodeList<Subscription>()

            val sub = results.firstOrNull() ?: return@withContext Subscription(userId = currentUserId)

            // Check expiry client-side as a safety net
            val expiresAt = Instant.parse(sub.expiresAt)
            if (Instant.now().isAfter(expiresAt)) {
                Subscription(userId = currentUserId, status = "expired")
            } else {
                sub
            }
        } catch (e: Exception) {
            Log.e("SubscriptionRepo", "Error getting subscription: ${e.message}")
            Subscription(userId = currentUserId) // defaults to free tier
        }
    }

    // ── Subscribe / upgrade (call after successful M-Pesa payment) ─────────────
    suspend fun activateSubscription(
        tier: SubscriptionTier,
        paymentRef: String,
        amountPaid: Int
    ): Subscription = withContext(Dispatchers.IO) {
        val now = Instant.now()
        val expires = now.plus(30, ChronoUnit.DAYS)

        val userId = currentUserId
        Log.d("SubscriptionRepo", "Activating subscription for user: $userId")

        val subscription = Subscription(
            id = UUID.randomUUID().toString(),
            userId = userId,
            tier = tier.toDbValue(),
            status = "active",
            startedAt = now.toString(),
            expiresAt = expires.toString(),
            autoRenew = false,
            paymentRef = paymentRef,
            amountPaid = amountPaid
        )

        Log.d("SubscriptionRepo", "Subscription data: $subscription")

        try {
            SupabaseClient.database.from("subscriptions").insert(subscription)
            Log.d("SubscriptionRepo", "✅ Subscription inserted successfully")
        } catch (e: Exception) {
            Log.e("SubscriptionRepo", "❌ Insert failed: ${e.message}", e)
            throw e
        }

        subscription
    }

    // ── Search signal tracking ──────────────────────────────────────────────────
    suspend fun recordSearchSignal(term: String, university: String) = withContext(Dispatchers.IO) {
        try {
            SupabaseClient.database.from("search_signals").insert(
                SearchSignal(
                    id = UUID.randomUUID().toString(),
                    searchTerm = term.lowercase().trim(),
                    university = university
                )
            )
        } catch (_: Exception) { /* non-critical, fail silently */ }
    }

    // ── Fetch intent signals for Pro sellers ────────────────────────────────────
    suspend fun getIntentSignals(university: String): List<IntentSignalSummary> = withContext(Dispatchers.IO) {
        try {
            val signals = SupabaseClient.database.from("search_signals")
                .select {
                    filter { eq("university", university) }
                }
                .decodeList<SearchSignal>()

            signals.groupBy { it.searchTerm }
                .map { (term, list) ->
                    IntentSignalSummary(
                        term = term,
                        count = list.sumOf { it.searchCount },
                        trend = if (list.size > 5) "rising" else "stable"
                    )
                }
                .sortedByDescending { it.count }
                .take(10)
        } catch (e: Exception) {
            emptyList()
        }
    }

    // ── Study Squads ─────────────────────────────────────────────────────────
    suspend fun createSquad(name: String, course: String, university: String, yearGroup: String): StudySquad =
        withContext(Dispatchers.IO) {
            val squad = StudySquad(
                id = UUID.randomUUID().toString(),
                name = name,
                course = course,
                university = university,
                yearGroup = yearGroup,
                createdBy = currentUserId,
                memberCount = 1
            )
            SupabaseClient.database.from("study_squads").insert(squad)
            joinSquad(squad.id)
            squad
        }

    suspend fun joinSquad(squadId: String) = withContext(Dispatchers.IO) {
        SupabaseClient.database.from("squad_members").insert(
            mapOf(
                "id" to UUID.randomUUID().toString(),
                "squad_id" to squadId,
                "user_id" to currentUserId
            )
        )
    }

    suspend fun getMySquads(): List<StudySquad> = withContext(Dispatchers.IO) {
        try {
            val memberships = SupabaseClient.database.from("squad_members")
                .select { filter { eq("user_id", currentUserId) } }
                .decodeList<Map<String, String>>()

            val squadIds = memberships.mapNotNull { it["squad_id"] }
            if (squadIds.isEmpty()) return@withContext emptyList()

            SupabaseClient.database.from("study_squads")
                .select { filter { isIn("id", squadIds) } }
                .decodeList<StudySquad>()
        } catch (e: Exception) {
            emptyList()
        }
    }

    // ── Squad-exclusive deals ─────────────────────────────────────────
    suspend fun postSquadDeal(
        squadId: String,
        productId: String,
        discountPercent: Int,
        description: String
    ) = withContext(Dispatchers.IO) {
        SupabaseClient.database.from("squad_deals").insert(
            SquadDeal(
                id = UUID.randomUUID().toString(),
                sellerId = currentUserId,
                squadId = squadId,
                productId = productId,
                discountPercent = discountPercent,
                description = description
            )
        )
    }

    // ── Boost a listing's visibility ─────────────────────────────────────────
    suspend fun boostListing(productId: String, durationHours: Long = 168) = withContext(Dispatchers.IO) {
        val boostedUntil = Instant.now().plus(durationHours, ChronoUnit.HOURS).toString()
        SupabaseClient.database.from("products")
            .update(
                update = {
                    set("visibility_boost", 100)
                    set("boosted_until", boostedUntil)
                },
                request = { filter { eq("id", productId) } }
            )
    }

    // ── Active banners ─────────────────────────────────────────────────────────
    suspend fun getActiveBanners(): List<SellerBanner> = withContext(Dispatchers.IO) {
        try {
            SupabaseClient.database.from("seller_banners")
                .select { filter { eq("is_active", true) } }
                .decodeList<SellerBanner>()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun createBanner(title: String, subtitle: String, imageUrl: String?) = withContext(Dispatchers.IO) {
        SupabaseClient.database.from("seller_banners").insert(
            SellerBanner(
                id = UUID.randomUUID().toString(),
                sellerId = currentUserId,
                bannerImageUrl = imageUrl,
                title = title,
                subtitle = subtitle,
                isActive = true
            )
        )
    }
}