package com.example.parck.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.parck.data.model.ParkingSession
import com.example.parck.data.remote.SupabaseConfig
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import java.time.Instant

class ParkingRepositoryImpl : ParkingRepository {
    private val client = SupabaseConfig.client

    override suspend fun getActiveSessions(): List<ParkingSession> {
        return client.postgrest["parking_sessions"]
            .select { filter { eq("is_paid", false) } }
            .decodeList<ParkingSession>()
    }

    override suspend fun registerEntry(session: ParkingSession, imageBytes: ByteArray): Result<Unit> = runCatching {
        // 1. Upload de la photo dans le Bucket
        val fileName = "${session.plateNumber}_${System.currentTimeMillis()}.jpg"
        val bucket = client.storage["vehicle-photos"]
        bucket.upload(fileName, imageBytes)

        // 2. Récupération de l'URL publique pour Coil [cite: 29]
        val photoUrl = bucket.publicUrl(fileName)

        // 3. Insertion en base de données
        val sessionWithPhoto = session.copy(photoUrl = photoUrl)
        client.postgrest["parking_sessions"].insert(sessionWithPhoto)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun registerExit(sessionId: String, finalAmount: Double): Result<Unit> = runCatching {
        client.postgrest["parking_sessions"].update({
            set("exit_time", Instant.now().toEpochMilli())
            set("total_amount", finalAmount)
            set("is_paid", true)
        }) {
            filter { eq("id", sessionId) }
        }
    }

    override suspend fun deleteSession(sessionId: String): Result<Unit> = runCatching {
        client.postgrest["parking_sessions"].delete {
            filter { eq("id", sessionId) }
        }
    }
}