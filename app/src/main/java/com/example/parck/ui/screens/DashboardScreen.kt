package com.example.parck.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.parck.R
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
    val sessions by viewModel.activeSessions.collectAsState()
    val refreshing by viewModel.isRefreshing.collectAsState()

    // Palette Premium
    val surfaceGradient = Brush.verticalGradient(
        colors = listOf(BitumenDark, Color(0xFF0F1113))
    )

    Scaffold(
        containerColor = BitumenDark,
        topBar = {
            // Utilisation d'une barre avec un effet de profondeur
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "PARCK",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 6.sp,
                                fontSize = 22.sp
                            ),
                            color = Color.White
                        )
                        Box(
                            modifier = Modifier
                                .width(40.dp)
                                .height(2.dp)
                                .background(BlueElectric, CircleShape)
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.loadSessions() },
                        modifier = Modifier
                            .padding(end = 12.dp)
                            .size(40.dp)
                            .background(Color.White.copy(alpha = 0.08f), CircleShape)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_refresh),
                            contentDescription = "Refresh",
                            tint = if (refreshing) BlueElectric else Color.White,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent // On laisse le fond s'occuper du style
                )
            )
        },
        floatingActionButton = {
            // Un FAB étendu avec un dégradé électrique
            ExtendedFloatingActionButton(
                onClick = onNavigateToEntry,
                containerColor = BlueElectric,
                contentColor = BitumenDark,
                shape = RoundedCornerShape(24.dp),
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 12.dp),
                modifier = Modifier.padding(bottom = 16.dp)
            ) {
                Icon(painter = painterResource(id = R.drawable.ic_add), contentDescription = null)
                Spacer(modifier = Modifier.width(12.dp))
                Text("NOUVELLE ENTRÉE", fontWeight = FontWeight.ExtraBold, letterSpacing = 1.sp)
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(surfaceGradient)
                .padding(padding)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {

                // Section "Statut" stylisée
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            text = "ÉTAT DU PARKING",
                            style = MaterialTheme.typography.labelMedium,
                            color = BlueElectric,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = if (sessions.isEmpty()) "Libre" else "${sessions.size} véhicules actifs",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Black
                        )
                    }

                    if (refreshing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp).padding(bottom = 4.dp),
                            strokeWidth = 3.dp,
                            color = BlueElectric
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                if (sessions.isEmpty() && !refreshing) {
                    EmptyParkingView()
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            bottom = 100.dp, // Espace pour le FAB
                            start = 20.dp,
                            end = 20.dp,
                            top = 12.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(sessions, key = { it.id ?: it.entryTime }) { session ->
                            // Ici, assure-toi que VehicleCard utilise un design Glassmorphic (voir ci-dessous)
                            VehicleCard(
                                session = session,
                                currentAmount = viewModel.calculateCurrentFees(session),
                                onExitClick = { onNavigateToExit(session) },
                                /*formatTime = { time ->
                                    SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(time))
                                },*/
                                onDeleteConfirm = { viewModel.deleteParkingSession(session.id ?: "") }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun EmptyParkingView() {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Surface(
            modifier = Modifier.size(100.dp),
            color = Color.White.copy(alpha = 0.03f),
            shape = CircleShape
        ) {
            Icon(
                painter = painterResource(id = android.R.drawable.ic_menu_info_details),
                contentDescription = null,
                tint = Color.DarkGray,
                modifier = Modifier.padding(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            "AUCUNE ACTIVITÉ",
            style = MaterialTheme.typography.titleSmall,
            color = Color.Gray,
            letterSpacing = 2.sp
        )
    }
}