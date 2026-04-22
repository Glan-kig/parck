package com.example.parck

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.parck.data.model.ParkingSession
import com.example.parck.data.model.VehicleType
import com.example.parck.data.repository.ParkingRepositoryImpl
import com.example.parck.ui.components.VehicleCard
import com.example.parck.ui.screens.DashboardScreen
import com.example.parck.ui.screens.EntryFormScreen
import com.example.parck.ui.screens.ExitScreen
import com.example.parck.ui.theme.ParckTheme
import com.example.parck.ui.theme.ParkSmartTheme
import com.example.parck.ui.viewmodel.ParkingViewModel
import com.example.parck.worker.ParkingAlertWorker
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. Initialisation de la couche de données
        val repository = ParkingRepositoryImpl()
        val viewModel = ParkingViewModel(repository)

        setContent {
            // 2. Application du thème "Urbain" défini à l'étape 5
            ParkSmartTheme {
                val navController = rememberNavController()

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // 3. Gestion de la Navigation
                    NavHost(
                        navController = navController,
                        startDestination = "dashboard"
                    ) {
                        // Écran 1 : Dashboard (Liste des véhicules)
                        composable("dashboard") {
                            DashboardScreen(
                                viewModel = viewModel,
                                onNavigateToEntry = { navController.navigate("entry") },
                                onNavigateToExit = { session: ParkingSession ->
                                    // On passe la session à l'écran de sortie
                                    navController.currentBackStackEntry?.savedStateHandle?.set(
                                        key = "session",
                                        value = session
                                    )
                                    navController.navigate("exit")
                                }
                            )
                        }

                        // Écran 2 : Formulaire d'entrée (Ajout)
                        composable("entry") {
                            EntryFormScreen(
                                viewModel = viewModel,
                                onEntrySaved = { navController.popBackStack() }
                            )
                        }

                        // Écran 3 : Validation de sortie (Paiement)
                        composable("exit") {
                            val session = navController.previousBackStackEntry
                                ?.savedStateHandle?.get<ParkingSession>("session")

                            session?.let {
                                ExitScreen(
                                    session = it,
                                    viewModel = viewModel,
                                    onExitConfirmed = { navController.popBackStack() }
                                )
                            }
                        }
                    }
                }
            }
        }

        // 4. Lancement du WorkManager (Alerte 24h)
        setupWorkManager()
    }

    private fun setupWorkManager() {
        val alertRequest = PeriodicWorkRequestBuilder<ParkingAlertWorker>(1, TimeUnit.HOURS).build()
        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "ParkingAlerts",
            ExistingPeriodicWorkPolicy.KEEP,
            alertRequest
        )
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DashboardPreview() {
    // 1. On applique ton thème personnalisé (Step 5)
    ParkSmartTheme {
        // 2. On crée une fausse liste de sessions pour le visuel
        val fakeSessions = listOf(
            ParkingSession(
                id = "1",
                plateNumber = "GK-123-LU",
                vehicleType = VehicleType.VOITURE,
                entryTime = System.currentTimeMillis() - 3600000, // Il y a 1h
                photoUrl = null
            ),
            ParkingSession(
                id = "2",
                plateNumber = "MOTO-77",
                vehicleType = VehicleType.MOTO,
                entryTime = System.currentTimeMillis() - 7200000, // Il y a 2h
                photoUrl = null
            )
        )

        // 3. On affiche uniquement l'UI (sans logique réseau)
        Scaffold(
            topBar = {
                SmallTopAppBar(title = { Text("ParkSmart - Aperçu") })
            }
        ) { padding ->
            LazyColumn(modifier = Modifier.padding(padding)) {
                items(fakeSessions) { session ->
                    VehicleCard(
                        session = session,
                        currentAmount = 5.0, // Montant fictif pour l'aperçu
                        onExitClick = {},
                        formatTime = { /* Mock */ }
                    )
                }
            }
        }
    }
}

@Composable
fun SmallTopAppBar(title: @Composable () -> Unit) {
    TODO("Not yet implemented")
}