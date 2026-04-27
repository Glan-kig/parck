package com.example.parck.data.domain

import android.os.Build
import androidx.annotation.RequiresApi
import java.time.Duration
import java.time.Instant

object PriceCalculator {

    /**
     * Calcule le montant total en fonction du temps passé et du type de véhicule.
     * @param entryTime Temps d'entrée en millisecondes
     * @param hourlyRate Tarif horaire du véhicule
     * @return Le montant total à payer
     */
    @RequiresApi(Build.VERSION_CODES.O)
    fun calculateFees(entryTime: Long, hourlyRate: Double): Double {
        val entryInstant = Instant.ofEpochMilli(entryTime)
        val now = Instant.now()

        // Calcul de la durée réelle
        val duration = Duration.between(entryInstant, now)

        // On convertit en heures (ex: 90 min = 1.5h)
        // On utilise maxOf(0.1, ...) pour s'assurer qu'on facture un minimum même si le séjour est très court
        val hours = maxOf(0.0, duration.toMinutes() / 60.0)

        return hours * hourlyRate
    }
}