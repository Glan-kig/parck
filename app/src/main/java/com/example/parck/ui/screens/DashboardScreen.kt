package com.example.parck.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LargeTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.parck.data.model.ParkingSession
import com.example.parck.ui.components.VehicleCard
import com.example.parck.ui.theme.BitumenDark
import com.example.parck.ui.theme.BlueElectric
import com.example.parck.ui.viewmodel.ParkingViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun DashboardScreen(
    viewModel: ParkingViewModel,
    onNavigateToEntry: () -> Unit,
    onNavigateToExit: (ParkingSession) -> Unit
) {
    // On observe la liste des sessions
    val sessions by viewModel.activeSessions.collectAsState()

    Scaffold(
        topBar = {
            LargeTopAppBar(
                title = { Text("ParkSmart - Occupation", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.largeTopAppBarColors(containerColor = BitumenDark)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToEntry,
                containerColor = BlueElectric
            ) {
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_input_add),
                    contentDescription = "Entrée véhicule",
                    tint = Color.White
                )
            }
        },
        containerColor = BitumenDark
    ) { padding ->
        LazyColumn(
            modifier = Modifier.padding(padding).fillMaxSize(),
            contentPadding = PaddingValues(8.dp)
        ) {
            items(sessions) { session ->
                // On passe le calcul dynamique du ViewModel à la carte
                VehicleCard(
                    session = session,
                    currentAmount = viewModel.calculateCurrentFees(session),
                    onExitClick = { onNavigateToExit(session) },
                    formatTime = { time ->
                        SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(time))
                    }
                )
            }
        }
    }
}