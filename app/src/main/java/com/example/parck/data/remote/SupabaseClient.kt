package com.example.parck.data.remote

import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage

object SupabaseConfig {
    val client = createSupabaseClient(
        supabaseUrl = "https://eldpaphasnszfjhpvxqw.supabase.co", // À récupérer dans tes paramètres Supabase
        supabaseKey = "sb_publishable_C0V2vlAK0Gd759F-1XfUJQ_UwPs_tHL"
    ) {
        install(Postgrest) // Pour la base de données PostgreSQL
        install(Storage)   // Pour le Bucket d'images
    }
}