package com.example.parck.ui.screens

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.common.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.parck.data.model.ParkingSession
import com.example.parck.ui.theme.AccessGreen
import com.example.parck.ui.theme.BitumenDark
import com.example.parck.ui.theme.BitumenGrey
import com.example.parck.ui.theme.BlueElectric
import com.example.parck.ui.theme.PlateTypography
import com.example.parck.ui.viewmodel.ParkingViewModel
import java.time.Duration
import java.time.Instant

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ExitScreen(
    session: ParkingSession,
    viewModel: ParkingViewModel,
    onExitConfirmed: () -> Unit
) {
    // On calcule le montant final au moment de l'ouverture de l'écran
    val finalAmount = remember { viewModel.calculateCurrentFees(session) }
    val duration = remember {
        Duration.between(Instant.ofEpochMilli(session.entryTime), Instant.now())
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp)
            .background(BitumenDark),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Validation de Sortie",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Rappel du véhicule
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            colors = CardDefaults.cardColors(containerColor = BitumenGrey)
        ) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text(text = session.plateNumber, style = PlateTypography, color = BlueElectric)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Temps passé : ${duration.toHours()}h ${duration.toMinutes() % 60}min", color = Color.LightGray)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // MONTANT À PAYER (Mise en évidence)
        Text(text = "TOTAL À PERCEVOIR", style = MaterialTheme.typography.labelLarge, color = Color.Gray)
        Text(
            text = "${String.format("%.2f", finalAmount)} €",
            style = MaterialTheme.typography.displayMedium,
            fontWeight = FontWeight.Black,
            color = AccessGreen
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Bouton de confirmation finale
        Button(
            onClick = {
                viewModel.endVehicleSession(session.id!!, finalAmount)
                onExitConfirmed()
            },
            modifier = Modifier.fillMaxWidth().height(64.dp),
            colors = ButtonDefaults.buttonColors(containerColor = AccessGreen),
            shape = MaterialTheme.shapes.medium
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.CheckCircle, contentDescription = null, tint = BitumenDark)
                Spacer(modifier = Modifier.width(8.dp))
                Text("ENCAISSER ET LIBÉRER", color = BitumenDark, fontWeight = FontWeight.Bold)
            }
        }

        TextButton(onClick = { /* Annuler et retourner au dashboard */ }) {
            Text("ANNULER", color = Color.Red)
        }
    }
}