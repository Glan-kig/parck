package com.example.parck.data.model

import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class ParkingSession @RequiresApi(Build.VERSION_CODES.O) constructor(
    val id: String? = null,
    val plateNumber: String,
    val vehicleType: VehicleType,
    val photoUrl: String? = null,
    val entryTime: Long = Instant.now().toEpochMilli(), // Utilisation de java.time.Instant
    val exitTime: Long? = null,
    val totalAmount: Double = 0.0,
    val isPaid: Boolean = false
)

enum class VehicleType(val hourlyRate: Double) {
    MOTO(1.0),    // Tarif horaire différent
    VOITURE(2.5),
    CAMION(5.0)
}