package com.example.parck

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.example.parck.data.model.ParkingSession
import com.example.parck.data.repository.ParkingRepositoryImpl
import com.example.parck.ui.screens.DashboardScreen
import com.example.parck.ui.screens.EntryFormScreen
import com.example.parck.ui.screens.ExitScreen
import com.example.parck.ui.theme.BitumenDark
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
            // Utilisation du thème personnalisé de l'app
            ParkSmartTheme {
                // Surface principale configurée sur la couleur sombre du thème
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = BitumenDark
                ) {
                    ParkSmartApp(viewModel)
                }
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ParkSmartApp(viewModel: ParkingViewModel) {
    val navController = rememberNavController()

    // NavHost avec animations de transition premium (Slide & Fade)
    NavHost(
        navController = navController,
        startDestination = "dashboard",
        enterTransition = { fadeIn(animationSpec = tween(400)) },
        exitTransition = { fadeOut(animationSpec = tween(400)) }
    ) {
        composable(
            route = "dashboard",
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(400)
                )
            }
        ) {
            DashboardScreen(
                viewModel = viewModel,
                onNavigateToEntry = { navController.navigate("entry") },
                onNavigateToExit = { session ->
                    navController.currentBackStackEntry?.savedStateHandle?.set("session", session)
                    navController.navigate("exit")
                }
            )
        }

        composable(
            route = "entry",
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Up,
                    animationSpec = tween(400)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Down,
                    animationSpec = tween(400)
                )
            }
        ) {
            EntryFormScreen(
                viewModel = viewModel,
                onEntrySaved = { navController.popBackStack() }
            )
        }

        composable(
            route = "exit",
            enterTransition = {
                slideIntoContainer(
                    AnimatedContentTransitionScope.SlideDirection.Left,
                    animationSpec = tween(400)
                )
            },
            exitTransition = {
                slideOutOfContainer(
                    AnimatedContentTransitionScope.SlideDirection.Right,
                    animationSpec = tween(400)
                )
            }
        ) {
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