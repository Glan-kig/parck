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

    Scaffold(
        containerColor = Color.Transparent, // On met transparent pour voir le dégradé du fond
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "PARCK",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 4.sp
                            ),
                            color = Color.White
                        )
                        Text(
                            "GESTION TEMPS RÉEL",
                            style = MaterialTheme.typography.labelSmall,
                            color = BlueElectric
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = { viewModel.loadSessions() },
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .background(Color.White.copy(alpha = 0.05f), CircleShape)
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_refresh),
                            contentDescription = "Rafraîchir",
                            tint = if (refreshing) Color.Gray else Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = BitumenDark.copy(alpha = 0.9f)
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToEntry,
                containerColor = BlueElectric,
                shape = RoundedCornerShape(20.dp),
                elevation = FloatingActionButtonDefaults.elevation(8.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_add),
                        contentDescription = null,
                        tint = BitumenDark
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("ENTRÉE", color = BitumenDark, fontWeight = FontWeight.Bold)
                }
            }
        }
    ) { padding ->
        // Fond dégradé identique aux autres écrans
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(BitumenDark, Color(0xFF121416))
                    )
                )
                .padding(padding)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {

                // Barre d'état / Statistiques
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        color = Color.White.copy(alpha = 0.05f),
                        shape = RoundedCornerShape(50)
                    ) {
                        Text(
                            text = "${sessions.size} VÉHICULE${if (sessions.size > 1) "S" else ""} ACTIF(S)",
                            color = Color.LightGray,
                            style = MaterialTheme.typography.labelMedium,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                        )
                    }

                    if (refreshing) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp,
                            color = BlueElectric
                        )
                    }
                }

                if (sessions.isEmpty() && !refreshing) {
                    // État vide plus accueillant
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            painter = painterResource(id = android.R.drawable.ic_menu_info_details),
                            contentDescription = null,
                            tint = Color.DarkGray,
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Le parking est vide",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.Gray
                        )
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 88.dp, start = 16.dp, end = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(sessions, key = { it.id ?: it.entryTime }) { session ->
                            VehicleCard(
                                session = session,
                                currentAmount = viewModel.calculateCurrentFees(session),
                                onExitClick = { onNavigateToExit(session) },
                                formatTime = { time ->
                                    SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(time))
                                },
                                onDeleteConfirm = { viewModel.deleteParkingSession(session.id ?: "") }
                            )
                        }
                    }
                }
            }
        }
    }
}