package com.example.parck.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource // Import important
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.parck.data.model.ParkingSession
import com.example.parck.ui.theme.*
import com.example.parck.ui.viewmodel.ParkingViewModel
import com.example.parck.utils.toDurationLabel
import java.time.Duration
import java.time.Instant

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ExitScreen(
    session: ParkingSession,
    viewModel: ParkingViewModel,
    onExitConfirmed: () -> Unit
) {
    val finalAmount = remember { viewModel.calculateCurrentFees(session) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(BitumenDark, Color(0xFF1A1C1E))
                )
            )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp, vertical = 40.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Text(
                text = "Validation de Sortie",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                fontWeight = FontWeight.Light,
                letterSpacing = 1.5.sp
            )

            Spacer(modifier = Modifier.weight(0.5f))

            // Carte du Véhicule
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = BitumenGrey.copy(alpha = 0.5f)),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Remplacement par painterResource (Utilise une icône système Android ou la tienne)
                    Icon(
                        painter = painterResource(id = android.R.drawable.ic_menu_directions),
                        contentDescription = null,
                        tint = BlueElectric,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = session.plateNumber.uppercase(),
                        style = PlateTypography.copy(fontSize = 32.sp),
                        color = Color.White,
                        textAlign = TextAlign.Center
                    )

                    HorizontalDivider(
                        modifier = Modifier.padding(vertical = 16.dp),
                        thickness = 1.dp,
                        color = Color.White.copy(alpha = 0.1f)
                    )

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            painter = painterResource(id = android.R.drawable.ic_menu_recent_history),
                            contentDescription = null,
                            tint = Color.LightGray,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Durée : ${session.entryTime.toDurationLabel()}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.LightGray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Zone du Montant
            Surface(
                color = AccessGreen.copy(alpha = 0.1f),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                Text(
                    text = "TOTAL À PERCEVOIR",
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelLarge,
                    color = AccessGreen,
                    fontWeight = FontWeight.Bold
                )
            }

            Text(
                text = "${String.format("%.2f", finalAmount)} €",
                style = MaterialTheme.typography.displayLarge,
                fontWeight = FontWeight.ExtraBold,
                color = Color.White
            )

            Spacer(modifier = Modifier.weight(1f))

            // Boutons d'action
            Button(
                onClick = {
                    viewModel.endVehicleSession(session.id!!, finalAmount)
                    onExitConfirmed()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(72.dp),
                colors = ButtonDefaults.buttonColors(containerColor = AccessGreen),
                shape = RoundedCornerShape(20.dp),
                elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        painter = painterResource(id = android.R.drawable.checkbox_on_background),
                        contentDescription = null,
                        tint = BitumenDark
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        "ENCAISSER ET LIBÉRER",
                        color = BitumenDark,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                onClick = onExitConfirmed,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    "ANNULER",
                    color = Color.White.copy(alpha = 0.6f),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}
