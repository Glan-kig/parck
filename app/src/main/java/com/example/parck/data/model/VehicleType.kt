package com.example.parck.data.model

import android.os.Parcelable
import kotlinx.serialization.Serializable
import kotlinx.parcelize.Parcelize

@Serializable
@Parcelize
enum class VehicleType(val hourlyRate: Double) : Parcelable {
    MOTO(1.0),
    VOITURE(2.5),
    CAMION(5.0)
}
