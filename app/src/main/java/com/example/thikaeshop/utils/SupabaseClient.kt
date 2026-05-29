package com.example.thikaeshop.utils

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.realtime.Realtime
import io.github.jan.supabase.storage.storage
import kotlinx.serialization.json.Json

object SupabaseClient {


    private const val SUPABASE_URL = "https://ylzlxqxvlqdzzxuhdjzi.supabase.co"
    private const val SUPABASE_ANON_KEY = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Inlsemx4cXh2bHFkenp4dWhkanppIiwicm9sZSI6ImFub24iLCJpYXQiOjE3Nzk1MDUzNjgsImV4cCI6MjA5NTA4MTM2OH0.wfnK2d6btZTSDsDpiccxCWVIvsSCV0_wwKCC5Dae1oM"

    val client = createSupabaseClient(
        supabaseUrl = SUPABASE_URL,
        supabaseKey = SUPABASE_ANON_KEY
    ) {
        install(Postgrest){
            serializer = io.github.jan.supabase.serializer.KotlinXSerializer(
                Json {
                    coerceInputValues = true
                    ignoreUnknownKeys= true
                    isLenient = true
                }
            )
        }
        install(Storage)
        install(Auth)
        install(Realtime)
    }

    val database: Postgrest
        get() = client.postgrest

    val storage: Storage
        get() = client.storage

    val auth: Auth
        get() = client.auth
}