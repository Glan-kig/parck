package com.example.parck.data.repository

import com.example.parck.data.model.ParkingSession

interface ParkingRepository {
    // Opération READ : Récupérer les véhicules présents
    suspend fun getActiveSessions(): List<ParkingSession>

    // Opération CREATE : Enregistrer une entrée avec photo
    suspend fun registerEntry(session: ParkingSession, imageBytes: ByteArray): Result<Unit>

    // Opération UPDATE : Sortie du véhicule et paiement
    suspend fun registerExit(sessionId: String, finalAmount: Double): Result<Unit>

    // Opération DELETE : Annuler une erreur
    suspend fun deleteSession(sessionId: String): Result<Unit>
}