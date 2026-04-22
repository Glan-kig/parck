package com.example.parck.worker

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.parck.data.repository.ParkingRepository
import com.example.parck.data.repository.ParkingRepositoryImpl
import java.time.Duration
import java.time.Instant

class ParkingAlertWorker(
    context: Context,
    workerParams: WorkerParameters,
    private val repository: ParkingRepository = ParkingRepositoryImpl() // Idéalement via Injection de dépendance
) : CoroutineWorker(context, workerParams) {

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun doWork(): Result {
        val activeSessions = repository.getActiveSessions()
        val now = Instant.now()

        activeSessions.forEach { session ->
            val entryInstant = Instant.ofEpochMilli(session.entryTime)
            val hoursElapsed = Duration.between(entryInstant, now).toHours()

            // Si le véhicule est là depuis plus de 24h
            if (hoursElapsed >= 24) {
                sendNotification(session.plateNumber, hoursElapsed)
            }
        }
        return Result.success()
    }

    private fun sendNotification(plate: String, hours: Long) {
        // Logique standard Android pour afficher une notification locale
        // Titre: "Alerte Stationnement Prolongé"
        // Message: "Le véhicule $plate est stationné depuis $hours heures."
    }
}