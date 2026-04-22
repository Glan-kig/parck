package com.example.parck.ui.viewmodel

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.parck.data.model.ParkingSession
import com.example.parck.data.model.VehicleType
import com.example.parck.data.repository.ParkingRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ParkingViewModel(private val repository: ParkingRepository) : ViewModel() {

    // État de l'UI contenant la liste des véhicules en cours
    private val _uiState = MutableStateFlow<List<ParkingSession>>(emptyList())
    val uiState: StateFlow<List<ParkingSession>> = _uiState.asStateFlow()

    init {
        startRealTimePricingUpdates()
    }

    // Fonction pour recalculer les frais chaque minute
    private fun startRealTimePricingUpdates() {
        viewModelScope.launch {
            while (true) {
                refreshPrices()
                delay(60_000) // Rafraîchissement toutes les 60 secondes [cite: 27]
            }
        }
    }

    private fun refreshPrices() {
        // Logique de mise à jour des montants accumulés
    }

    fun getCurrentAmount(session: ParkingSession): Double {}
    fun registerEntry(plateNumber: String, selectedType: VehicleType, capturedImageUri: Uri?) {}
}