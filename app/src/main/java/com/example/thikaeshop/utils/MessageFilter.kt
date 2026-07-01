package com.example.thikaeshop.utils

import java.util.regex.Pattern

object MessageFilter {

    // Kenyan phone number patterns
    private val PHONE_PATTERNS = listOf(
        Pattern.compile("\\b(?:0|\\+?254)?7[0-9]{8}\\b"),      // 0712345678
        Pattern.compile("\\b(?:0|\\+?254)?1[0-9]{8}\\b"),      // 0112345678
        Pattern.compile("\\b(?:0|\\+?254)?[0-9]{9}\\b")        // General 9-digit
    )

    // WhatsApp number patterns (wa.me links)
    private val WHATSAPP_PATTERNS = listOf(
        Pattern.compile("wa\\.me/\\+?[0-9]+"),
        Pattern.compile("https?://wa\\.me/[0-9]+"),
        Pattern.compile("api\\.whatsapp\\.com/send\\?phone=[0-9]+")
    )

    // Social media handles
    private val SOCIAL_PATTERNS = listOf(
        Pattern.compile("@[a-zA-Z0-9_]+"),  // @username
        Pattern.compile("instagram\\.com/[a-zA-Z0-9_]+"),
        Pattern.compile("twitter\\.com/[a-zA-Z0-9_]+"),
        Pattern.compile("t\\.me/[a-zA-Z0-9_]+")
    )

    fun containsPhoneNumber(text: String): Boolean {
        return PHONE_PATTERNS.any { it.matcher(text).find() } ||
                WHATSAPP_PATTERNS.any { it.matcher(text).find() }
    }

    fun containsSocialMedia(text: String): Boolean {
        return SOCIAL_PATTERNS.any { it.matcher(text).find() }
    }

    fun containsSuspiciousContent(text: String): Boolean {
        return containsPhoneNumber(text) || containsSocialMedia(text)
    }

    fun maskPhoneNumber(text: String): String {
        // Replace phone numbers with [PHONE NUMBER HIDDEN]
        var masked = text
        PHONE_PATTERNS.forEach { pattern ->
            masked = pattern.matcher(masked).replaceAll("[📱 NUMBER HIDDEN]")
        }
        WHATSAPP_PATTERNS.forEach { pattern ->
            masked = pattern.matcher(masked).replaceAll("[🔗 LINK HIDDEN]")
        }
        return masked
    }
}