package com.example.parck.data.model

enum class VehicleType(val hourlyRate: Double) {
    MOTO(1.0),    // Tarif pour les motos
    VOITURE(2.5), // Tarif pour les voitures
    CAMION(5.0)   // Tarif pour les camions
}