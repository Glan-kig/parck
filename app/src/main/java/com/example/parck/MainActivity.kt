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
import androidx.compose.material3.*
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
import com.example.parck.ui.theme.ParkSmartTheme
import com.example.parck.ui.viewmodel.ParkingViewModel
import com.example.parck.worker.ParkingAlertWorker
import java.util.concurrent.TimeUnit

class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = ParkingRepositoryImpl()
        val viewModel = ParkingViewModel(repository)

        setContent {
            ParkSmartTheme {
                // Appel de la fonction principale
                ParkSmartApp(viewModel)
            }
        }

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

// --- FONCTION DE NAVIGATION PRINCIPALE ---
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ParkSmartApp(viewModel: ParkingViewModel) {
    val navController = rememberNavController()

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        NavHost(
            navController = navController,
            startDestination = "dashboard"
        ) {
            composable("dashboard") {
                DashboardScreen(
                    viewModel = viewModel,
                    onNavigateToEntry = { navController.navigate("entry") },
                    onNavigateToExit = { session ->
                        navController.currentBackStackEntry?.savedStateHandle?.set("session", session)
                        navController.navigate("exit")
                    }
                )
            }

            composable("entry") {
                EntryFormScreen(
                    viewModel = viewModel,
                    onEntrySaved = { navController.popBackStack() }
                )
            }

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

// --- PREVIEW POUR LE DESIGN ---
@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true, showSystemUi = true)
@Composable
fun DashboardPreview() {
    ParkSmartTheme {
        val fakeSessions = listOf(
            ParkingSession(id = "1", plateNumber = "GK-123-LU", vehicleType = VehicleType.VOITURE, entryTime = System.currentTimeMillis() - 3600000),
            ParkingSession(id = "2", plateNumber = "MOTO-77", vehicleType = VehicleType.MOTO, entryTime = System.currentTimeMillis() - 7200000)
        )

        Scaffold(
            topBar = {
                // Remplacement de ta fonction TODO par un vrai composant Material 3
                TopAppBar(title = { Text("ParkSmart - Aperçu") })
            }
        ) { padding ->
            LazyColumn(modifier = Modifier.padding(padding)) {
                items(fakeSessions) { session ->
                    VehicleCard(
                        session = session,
                        currentAmount = 5.0,
                        onExitClick = {},
                        formatTime = { time -> time.toString() }
                    )
                }
            }
        }
    }
}