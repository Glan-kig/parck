package com.example.parck.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.ui.res.painterResource
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.parck.data.model.ParkingSession
import com.example.parck.ui.theme.AccessGreen
import com.example.parck.ui.theme.BlueElectric
import com.example.parck.ui.theme.PlateTypography
import com.example.parck.utils.toCurrencyString
import com.example.parck.utils.toFormattedTime

@Composable
fun VehicleCard(
    session: ParkingSession,
    currentAmount: Double, // Montant calculé passé par le ViewModel
    onExitClick: () -> Unit,
    formatTime: (Long) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        shape = MaterialTheme.shapes.large, // Coins coupés définis à l'étape 5
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            // Affichage de la photo avec Coil
            AsyncImage(
                model = session.photoUrl,
                contentDescription = "Photo du véhicule",
                modifier = Modifier.size(90.dp).clip(MaterialTheme.shapes.medium),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.weight(1f).padding(start = 16.dp)) {
                // Plaque d'immatriculation (Style Monospace)
                Text(
                    text = session.plateNumber,
                    style = PlateTypography, // Défini à l'étape 5
                    color = Color.White
                )
                Text(text = "Entrée à : ${session.entryTime.toFormattedTime()}", style = MaterialTheme.typography.bodySmall)

                // PRIX EN TEMPS RÉEL
                Text(
                    text = "${currentAmount.toCurrencyString()} ",
                    color = AccessGreen,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            }

            // Bouton de sortie
            IconButton(onClick = onExitClick) {
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_media_play), // Icône "Play" orientée vers la droite (symbolise le départ)
                    contentDescription = "Sortie",
                    tint = BlueElectric,
                    modifier = Modifier.size(28.dp)
                )
            }
        }
    }
}