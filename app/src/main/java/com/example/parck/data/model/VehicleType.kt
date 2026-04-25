package com.example.parck.data.model

import kotlinx.serialization.Serializable

@Serializable
enum class VehicleType(val hourlyRate: Double) {
    MOTO(1.0),
    VOITURE(2.5),
    CAMION(5.0)
}