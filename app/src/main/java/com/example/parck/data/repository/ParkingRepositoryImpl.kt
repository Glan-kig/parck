package com.example.parck.data.repository

import android.os.Build
import androidx.annotation.RequiresApi
import com.example.parck.data.model.ParkingSession
import com.example.parck.data.remote.ParkingDto
import com.example.parck.data.remote.SupabaseConfig
import com.example.parck.data.remote.toDomain
import com.example.parck.data.remote.toDto
import io.github.jan.supabase.postgrest.from
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.storage.storage
import java.time.Instant

class ParkingRepositoryImpl : ParkingRepository {
    private val client = SupabaseConfig.client

    override suspend fun getActiveSessions(): List<ParkingSession> {
        return try {
            val response = SupabaseConfig.client.from("parking_sessions")
                .select {
                    filter {
                        eq("is_paid", false) // On ne veut que les voitures encore présentes
                    }
                }
                .decodeList<ParkingDto>() // On récupère les données via le DTO

            // On convertit chaque DTO en ParkingSession grâce à l'extension .toDomain()
            response.map { it.toDomain() }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun registerEntry(session: ParkingSession, imageBytes: ByteArray): Result<Unit> {
        return try {
            // ... (logique d'upload d'image ici) ...
            val fileName = "${session.plateNumber}_${System.currentTimeMillis()}.jpg"
            val bucket = client.storage["vehicle-photos"]
            bucket.upload(fileName, imageBytes)

            // 2. Récupération de l'URL publique pour Coil [cite: 29]
            val photoUrl = bucket.publicUrl(fileName)
            session.copy(photoUrl = photoUrl)

            // On convertit notre session Kotlin en DTO JSON-friendly
            val dto = session.toDto()

            SupabaseConfig.client.from("parking_sessions").insert(dto)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun registerExit(sessionId: String, amount: Double): Result<Unit> {
        return try {
            SupabaseConfig.client.from("parking_sessions").update(
                {
                    // On utilise les noms définis dans le DTO pour être sûr
                    set("total_amount", amount)
                    set("is_paid", true)
                    set("exit_time", Instant.now().toString())
                }
            ) {
                filter { eq("id", sessionId) }
            }
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deleteSession(sessionId: String): Result<Unit> = runCatching {
        client.postgrest["parking_sessions"].delete {
            filter { eq("id", sessionId) }
        }
    }
}