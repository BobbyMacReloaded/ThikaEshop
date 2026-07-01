package com.example.thikaeshop.utils

import com.google.mlkit.nl.entityextraction.Entity
import com.google.mlkit.nl.entityextraction.EntityExtraction
import com.google.mlkit.nl.entityextraction.EntityExtractionParams
import com.google.mlkit.nl.entityextraction.EntityExtractorOptions
import kotlinx.coroutines.tasks.await

class AIContentDetector {

    // Detect phone numbers using ML Kit
    suspend fun detectPhoneNumber(text: String): Boolean {
        return try {
            // Builder requires a language model identifier — EntityExtractorOptions.ENGLISH
            val options = EntityExtractorOptions.Builder(EntityExtractorOptions.ENGLISH)
                .build()

            val entityExtractor = EntityExtraction.getClient(options)

            // Make sure the model is downloaded before extracting (first run on device)
            entityExtractor.downloadModelIfNeeded().await()

            // Entity type filtering happens via EntityExtractionParams, not the options builder
            val params = EntityExtractionParams.Builder(text)
                .setEntityTypesFilter(
                    setOf(
                        Entity.TYPE_PHONE,
                        Entity.TYPE_EMAIL
                    )
                )
                .build()

            val annotations = entityExtractor.annotate(params).await()

            // annotations is a list of EntityAnnotation, each containing a list of Entity
            annotations.any { annotation ->
                annotation.entities.any { it.type == Entity.TYPE_PHONE }
            }
        } catch (e: Exception) {
            false
        }
    }

    // Detect if message contains intent to share contact
    suspend fun hasContactSharingIntent(text: String): Boolean {
        // Common phrases in different languages (English + Sheng/Swahili)
        val contactPhrases = listOf(
            "call me", "call me on", "call me at",
            "text me", "message me", "my number is",
            "contact me", "reach me", "phone is",
            "ping me", "dm me", "whatsapp",
            "nipigie", "nipigie simu", "namba yangu ni",
            "contact me on", "hit me up", "send me a message"
        )

        val lowerText = text.lowercase()
        return contactPhrases.any { lowerText.contains(it) }
    }

    // Combined AI detection
    suspend fun isSuspiciousMessage(text: String): DetectionResult {
        val hasPhone = detectPhoneNumber(text)
        val hasIntent = hasContactSharingIntent(text)
        val score = calculateSuspicionScore(text)

        return DetectionResult(
            isSuspicious = hasPhone || hasIntent || score > 0.6,
            confidence = score,
            detectedType = when {
                hasPhone -> "PHONE_NUMBER"
                hasIntent -> "CONTACT_INTENT"
                else -> "GENERAL_SUSPICION"
            }
        )
    }

    // Calculate suspicion score (0-1)
    private fun calculateSuspicionScore(text: String): Float {
        var score = 0f

        // Check for number patterns with spaces
        val numberWithSpaces = Regex("\\d\\s+\\d\\s+\\d")
        if (numberWithSpaces.containsMatchIn(text)) score += 0.3f

        // Check for "number" + digits combination
        if (text.contains(Regex("(number|namba|phone).*\\d", RegexOption.IGNORE_CASE))) {
            score += 0.3f
        }

        // Check for wa.me or whatsapp
        if (text.contains(Regex("wa\\.me|whatsapp", RegexOption.IGNORE_CASE))) {
            score += 0.4f
        }

        return score.coerceAtMost(1f)
    }
}

data class DetectionResult(
    val isSuspicious: Boolean,
    val confidence: Float,
    val detectedType: String
)