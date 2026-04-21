package com.example.parck.data.model

data class ParkingSession(
    val id: String? = null,
    val plateNumber: String,
    val vehicleType: String, // Moto, Voiture, Camion
    val photoUrl: String,
    val entryTime: Long, // Instant.now().toEpochMilli()
    val exitTime: Long? = null,
    val totalAmount: Double = 0.0,
    val isPaid: Boolean = false
)