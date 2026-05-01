package com.example.parck.data.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
@Parcelize
data class ParkingSession(
    val id: String? = null,
    val plateNumber: String,
    val vehicleType: VehicleType,
    val photoUrl: String? = null,
    val entryTime: Long = Instant.now().toEpochMilli(),
    val exitTime: Long? = null,
    val totalAmount: Double = 0.0,
    val isPaid: Boolean = false
) : Parcelable