package com.example.parck.ui.viewmodel

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parck.data.domain.PriceCalculator
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
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update

class ParkingViewModel(private val repository: ParkingRepository) : ViewModel() {

    private val _uiState = MutableStateFlow<List<ParkingSession>>(emptyList())
    val uiState: StateFlow<List<ParkingSession>> = _uiState.asStateFlow()

    // Cette liste se mettra à jour automatiquement dès que _uiState change
    val activeSessions = _uiState.map { sessions ->
        sessions.filter { it.exitTime == null && !it.isPaid }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    init {
        loadSessions()
        startRealTimeUpdates()
    }

    fun loadSessions() {
        viewModelScope.launch {
            _isRefreshing.value = true
            try {
                val sessions = repository.getActiveSessions()
                _uiState.emit(sessions)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _isRefreshing.value = false
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun calculateCurrentFees(session: ParkingSession): Double {
        return PriceCalculator.calculateFees(
            session.entryTime,
            session.vehicleType.hourlyRate
        )
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

    // Dans ParkingViewModel.kt
    fun endVehicleSession(sessionId: String, finalAmount: Double) {
        viewModelScope.launch {
            try {
                val result = repository.registerExit(sessionId, finalAmount)
                if (result.isSuccess) {
                    // On met à jour l'UI localement immédiatement
                    _uiState.update { currentList ->
                        currentList.map {
                            if (it.id == sessionId) it.copy(isPaid = true, exitTime = System.currentTimeMillis())
                            else it
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteParkingSession(sessionId: String) {
        viewModelScope.launch {
            val result = repository.deleteSession(sessionId)
            if (result.isSuccess) {
                // On retire la session de la liste locale pour que l'UI disparaisse de suite
                _uiState.update { currentList ->
                    currentList.filterNot { it.id == sessionId }
                }
            } else {
                // Optionnel : tu peux ajouter un StateFlow pour afficher une erreur (Toast)
            }
        }
    }
}