package com.example.parck.data.remote

import com.example.parck.data.model.ParkingSession
import com.example.parck.data.model.VehicleType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ParkingDto(
    @SerialName("id") val id: String? = null,
    @SerialName("plate_number") val plateNumber: String,
    @SerialName("vehicle_type") val vehicleType: String, // Reçu comme String (MOTO, VOITURE...)
    @SerialName("photo_url") val photoUrl: String? = null,
    @SerialName("entry_time") val entryTime: Long,
    @SerialName("exit_time") val exitTime: Long? = null,
    @SerialName("total_amount") val totalAmount: Double = 0.0,
    @SerialName("is_paid") val isPaid: Boolean = false,
    @SerialName("created_at") val createdAt: String? = null
)

// Extension pour convertir le DTO vers ton modèle métier ParkingSession
fun ParkingDto.toDomain(): ParkingSession {
    return ParkingSession(
        id = id,
        plateNumber = plateNumber,
        vehicleType = VehicleType.valueOf(vehicleType), // Conversion String -> Enum
        photoUrl = photoUrl,
        entryTime = entryTime,
        exitTime = exitTime,
        totalAmount = totalAmount,
        isPaid = isPaid
    )
}

// Extension pour convertir ton modèle métier vers le DTO (pour les inserts/updates)
fun ParkingSession.toDto(): ParkingDto {
    return ParkingDto(
        id = id,
        plateNumber = plateNumber,
        vehicleType = vehicleType.name, // Conversion Enum -> String
        photoUrl = photoUrl,
        entryTime = entryTime,
        exitTime = exitTime,
        totalAmount = totalAmount,
        isPaid = isPaid
    )
}