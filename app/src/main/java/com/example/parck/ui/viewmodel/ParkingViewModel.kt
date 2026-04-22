package com.example.parck.ui.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parck.data.model.ParkingSession
import com.example.parck.data.model.VehicleType
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.Instant
import com.example.parck.data.repository.ParkingRepository

class ParkingViewModel(private val repository: ParkingRepository) : ViewModel() {

    // L'état de l'UI : une liste de sessions qui sera observée par Compose
    private val _uiState = MutableStateFlow<List<ParkingSession>>(emptyList())
    val uiState: StateFlow<List<ParkingSession>> = _uiState.asStateFlow()

    init {
        loadSessions()
        startRealTimeUpdates()
    }

    // Charger les données depuis Supabase
    fun loadSessions() {
        viewModelScope.launch {
            val sessions = repository.getActiveSessions()
            _uiState.value = sessions
        }
    }

    // --- L'ALGORITHME DE CALCUL (À expliquer dans ta vidéo) ---
    @RequiresApi(Build.VERSION_CODES.O)
    fun calculateCurrentFees(session: ParkingSession): Double {
        val entryInstant = Instant.ofEpochMilli(session.entryTime)
        val now = Instant.now()

        // Calcul de la durée entre l'entrée et maintenant
        val duration = Duration.between(entryInstant, now)

        // On convertit en heures (ex: 90 min = 1.5h)
        val hours = duration.toMinutes() / 60.0

        // On multiplie par le tarif du type de véhicule (Moto, Voiture, etc.)
        return hours * session.vehicleType.hourlyRate
    }

    // Boucle de rafraîchissement automatique toutes les minutes
    private fun startRealTimeUpdates() {
        viewModelScope.launch {
            while (true) {
                // On force le StateFlow à se mettre à jour pour déclencher la recomposition
                _uiState.value = _uiState.value.toList()
                delay(60_000) // 60 secondes
            }
        }
    }

    // Méthode pour enregistrer une entrée (appelée par l'écran Formulaire)
    fun registerVehicleEntry(plate: String, type: VehicleType, imageBytes: ByteArray) {
        viewModelScope.launch {
            val newSession = ParkingSession(
                plateNumber = plate,
                vehicleType = type
            )
            val result = repository.registerEntry(newSession, imageBytes)
            if (result.isSuccess) {
                loadSessions() // Recharger la liste après ajout
            }
        }
    }
}