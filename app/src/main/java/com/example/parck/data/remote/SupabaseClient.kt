package com.example.parck.data.remote

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.serializer.KotlinXSerializer
import io.github.jan.supabase.storage.Storage
import kotlinx.serialization.json.Json

object SupabaseConfig {
    val client = createSupabaseClient(
        supabaseUrl = "https://eldpaphasnszfjhpvxqw.supabase.co",
        supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6ImVsZHBhcGhhc25zemZqaHB2eHF3Iiwicm9sZSI6ImFub24iLCJpYXQiOjE3NzY3OTc4MjYsImV4cCI6MjA5MjM3MzgyNn0.lv6VmRBFvAKyPWf7kdqGpNhkEqVCUq2GVxjclKr9VyU"
    ) {
        install(Postgrest)
        install(Storage)

        // On assigne correctement le serializer
        defaultSerializer = KotlinXSerializer(Json {
            ignoreUnknownKeys = true
            coerceInputValues = true
        })
    }
}
