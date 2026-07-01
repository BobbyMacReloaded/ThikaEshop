package com.example.thikaeshop.data.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

enum class SubscriptionTier(val displayName: String, val priceKsh: Int, val emoji: String) {
    FREE("Hustler", 0, "🆓"),
    PRO("Campus Pro", 150, "🟡"),
    PRO_PLUS("Campus Pro+", 350, "🟠");

    companion object {
        fun fromString(value: String): SubscriptionTier = when (value) {
            "pro" -> PRO
            "pro_plus" -> PRO_PLUS
            else -> FREE
        }
    }

    fun toDbValue(): String = when (this) {
        FREE -> "free"
        PRO -> "pro"
        PRO_PLUS -> "pro_plus"
    }
}

@Serializable
data class Subscription(
    val id: String = "",
    @SerialName("user_id") val userId: String = "",
    val tier: String = "free",
    val status: String = "active",
    @SerialName("started_at") val startedAt: String? = null,
    @SerialName("expires_at") val expiresAt: String = "",
    @SerialName("auto_renew") val autoRenew: Boolean = false,
    @SerialName("payment_ref") val paymentRef: String? = null,
    @SerialName("amount_paid") val amountPaid: Int? = null
) {
    val tierEnum: SubscriptionTier get() = SubscriptionTier.fromString(tier)
    val isActive: Boolean get() = status == "active"
}

@Serializable
data class StudySquad(
    val id: String = "",
    val name: String = "",
    val course: String? = null,
    val university: String? = null,
    @SerialName("year_group") val yearGroup: String? = null,
    @SerialName("created_by") val createdBy: String = "",
    @SerialName("member_count") val memberCount: Int = 1
)

@Serializable
data class SquadDeal(
    val id: String = "",
    @SerialName("seller_id") val sellerId: String = "",
    @SerialName("squad_id") val squadId: String = "",
    @SerialName("product_id") val productId: String? = null,
    @SerialName("discount_percent") val discountPercent: Int = 0,
    val description: String = "",
    @SerialName("expires_at") val expiresAt: String? = null
)

@Serializable
data class SearchSignal(
    val id: String = "",
    @SerialName("search_term") val searchTerm: String = "",
    val university: String? = null,
    @SerialName("search_count") val searchCount: Int = 1,
    @SerialName("week_start") val weekStart: String? = null
)

data class IntentSignalSummary(
    val term: String,
    val count: Int,
    val trend: String  // "rising", "stable", "falling"
)

@Serializable
data class SellerBanner(
    val id: String = "",
    @SerialName("seller_id") val sellerId: String = "",
    @SerialName("banner_image_url") val bannerImageUrl: String? = null,
    val title: String = "",
    val subtitle: String = "",
    @SerialName("is_active") val isActive: Boolean = true,
    val impressions: Int = 0,
    val clicks: Int = 0
)

// Tier benefits — single source of truth, used in UI to render the comparison table
data class TierBenefit(val label: String, val free: Boolean, val pro: Boolean, val proPlus: Boolean)

object SubscriptionBenefits {
    val benefits = listOf(
        TierBenefit("Standard listing", free = true, pro = true, proPlus = true),
        TierBenefit("3x search visibility boost", free = false, pro = true, proPlus = true),
        TierBenefit("Verified Pro badge", free = false, pro = true, proPlus = true),
        TierBenefit("Trending This Week rotation", free = false, pro = true, proPlus = true),
        TierBenefit("Buyer Intent Signals", free = false, pro = true, proPlus = true),
        TierBenefit("Join/create Study Squads", free = false, pro = true, proPlus = true),
        TierBenefit("Homepage banner rotation", free = false, pro = false, proPlus = true),
        TierBenefit("Seller Spotlight eligibility", free = false, pro = false, proPlus = true),
        TierBenefit("24hr early access to new listings", free = false, pro = false, proPlus = true),
        TierBenefit("1 free boosted listing/week", free = false, pro = false, proPlus = true),
    )
}
