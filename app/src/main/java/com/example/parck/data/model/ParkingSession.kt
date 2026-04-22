package com.example.parck.data.model

import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class ParkingSession(
    val id: String? = null, // UUID généré par Supabase
    val plateNumber: String, // Plaque d'immatriculation
    val vehicleType: VehicleType, // Type avec tarif spécifique
    val photoUrl: String? = null, // URL de l'image dans le Bucket
    val entryTime: Long = Instant.now().toEpochMilli(), // Capturée automatiquement
    val exitTime: Long? = null, // Enregistré lors de la sortie
    val totalAmount: Double = 0.0, // Frais calculés
    val isPaid: Boolean = false // État de la session
)