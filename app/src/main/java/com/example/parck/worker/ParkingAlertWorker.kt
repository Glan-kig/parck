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
import android.app.NotificationChannel
import android.app.NotificationManager
import android.graphics.Color
import androidx.core.app.NotificationCompat

class ParkingAlertWorker(
    context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    // On initialise le repository ici car WorkManager utilise le constructeur par défaut
    private val repository: ParkingRepository = ParkingRepositoryImpl()

    @RequiresApi(Build.VERSION_CODES.O)
    override suspend fun doWork(): Result {
        return try {
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
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }

    private fun sendNotification(plate: String, hours: Long) {
        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val channelId = "parking_alerts_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Alertes de stationnement",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notifie quand un véhicule dépasse 24h"
                enableLights(true)
                lightColor = Color.RED
                enableVibration(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val notification = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("Alerte Stationnement Prolongé")
            .setContentText("Le véhicule $plate est là depuis $hours heures.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        notificationManager.notify(plate.hashCode(), notification)
    }
}