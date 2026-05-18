package com.example.thikaeshop.data.models

data class StudentVerification(
    val id: String = "",
    val userId: String = "",
    val studentId: String = "",
    val fullName: String = "",
    val university: String = "Mount Kenya University",
    val idPhotoUrl: String = "",
    val status: VerificationStatus = VerificationStatus.PENDING,
    val submittedAt: Long = System.currentTimeMillis(),
    val reviewedAt: Long? = null,
    val rejectionReason: String? = null
)

enum class VerificationStatus {
    PENDING,    // Waiting for admin review
    APPROVED,   // Verified student
    REJECTED    // ID invalid or mismatch
}

data class VerifiedUser(
    val userId: String,
    val isVerified: Boolean,
    val studentId: String,
    val verifiedAt: Long
)