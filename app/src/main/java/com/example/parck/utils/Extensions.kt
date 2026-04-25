package com.example.parck.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/**
 * Formate un timestamp (Long) en heure lisible (ex: 14:30)
 */
fun Long.toFormattedTime(): String {
    val sdf = SimpleDateFormat("HH:mm", Locale.getDefault())
    return sdf.format(Date(this))
}

/**
 * Formate un montant Double en monnaie (ex: 5.5 -> 5,50 €)
 */
fun Double.toCurrencyString(): String {
    return String.format("%.2f €", this)
}

/**
 * Calcule la durée lisible entre un timestamp et maintenant
 * Utile pour l'affichage dans la VehicleCard
 */
fun Long.toDurationLabel(): String {
    val diff = System.currentTimeMillis() - this
    val hours = diff / (1000 * 60 * 60)
    val minutes = (diff / (1000 * 60)) % 60
    return "${hours}h ${minutes}min"
}