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
import kotlinx.coroutines.flow.update

class ParkingViewModel(private val repository: ParkingRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<List<ParkingSession>>(emptyList())
    val uiState: StateFlow<List<ParkingSession>> = _uiState.asStateFlow()

    init {
        loadSessions()
        startRealTimeUpdates()
    }

    fun loadSessions() {
        viewModelScope.launch {
            try {
                val sessions = repository.getActiveSessions()
                _uiState.emit(sessions) // Utilisation de emit pour garantir la mise à jour
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun calculateCurrentFees(session: ParkingSession): Double {
        val entryInstant = Instant.ofEpochMilli(session.entryTime)
        val now = Instant.now()
        val duration = Duration.between(entryInstant, now)
        val hours = duration.toMinutes() / 60.0
        return hours * session.vehicleType.hourlyRate
    }

    private fun startRealTimeUpdates() {
        viewModelScope.launch {
            while (true) {
                delay(30_000) // Rafraîchir toutes les 30 secondes
                loadSessions() // Recharger vraiment les données depuis le serveur
            }
        }
    }

    fun registerVehicleEntry(plate: String, type: VehicleType, imageBytes: ByteArray) {
        viewModelScope.launch {
            val newSession = ParkingSession(
                plateNumber = plate,
                vehicleType = type
            )
            val result = repository.registerEntry(newSession, imageBytes)
            if (result.isSuccess) {
                loadSessions()
            }
        }
    }

    fun endVehicleSession(sessionId: String, finalAmount: Double) {
        viewModelScope.launch {
            val result = repository.registerExit(sessionId, finalAmount)
            if (result.isSuccess) {
                loadSessions()
            }
        }
    }
}