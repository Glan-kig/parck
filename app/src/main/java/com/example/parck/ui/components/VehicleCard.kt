package com.example.parck.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
    currentAmount: Double,
    onExitClick: () -> Unit,
    onDeleteConfirm: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp)
            .clip(RoundedCornerShape(24.dp))
            .border(
                width = 1.dp,
                brush = Brush.horizontalGradient(
                    listOf(Color.White.copy(alpha = 0.12f), Color.Transparent)
                ),
                shape = RoundedCornerShape(24.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f))
    ) {
        // Suppression du Box modifier padding pour gérer les clics séparément
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 1. PHOTO DU VÉHICULE
            AsyncImage(
                model = session.photoUrl,
                contentDescription = null,
                modifier = Modifier
                    .size(85.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.White.copy(alpha = 0.05f))
                    .clickable { onExitClick() }, // Clic sur l'image mène à la sortie
                contentScale = ContentScale.Crop
            )

            // 2. INFOS CENTRALES (Zone cliquable pour la sortie)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 16.dp)
                    .clickable { onExitClick() }
            ) {
                Text(
                    text = session.plateNumber.uppercase(),
                    style = PlateTypography.copy(fontSize = 18.sp, letterSpacing = 1.sp),
                    color = Color.White
                )
                Text(
                    text = session.entryTime.toFormattedTime(),
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                Text(
                    text = currentAmount.toCurrencyString(),
                    color = AccessGreen,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.ExtraBold)
                )
            }

            // 3. ACTIONS VERTICALES (Suppression + Sortie)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // BOUTON SUPPRIMER (Plus visible maintenant)
                IconButton(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier
                        .size(36.dp)
                        .background(Color(0xFFFF5252).copy(alpha = 0.15f), CircleShape)
                ) {
                    Icon(
                        painter = painterResource(id = android.R.drawable.ic_menu_delete),
                        contentDescription = "Supprimer",
                        tint = Color(0xFFFF5252), // Rouge plus vif
                        modifier = Modifier.size(18.dp)
                    )
                }

                // BOUTON SORTIE (Principal)
                Surface(
                    modifier = Modifier
                        .size(44.dp)
                        .clickable { onExitClick() },
                    shape = CircleShape,
                    color = BlueElectric
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            painter = painterResource(id = android.R.drawable.ic_media_play),
                            contentDescription = "Sortie",
                            tint = Color.Black, // Contraste fort sur le bleu
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }

    // Le dialogue reste identique (il est déjà très bien)
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            containerColor = Color(0xFF1E1E1E),
            titleContentColor = Color.White,
            textContentColor = Color.Gray,
            title = { Text("Annuler l'entrée ?", fontWeight = FontWeight.Bold) },
            text = { Text("Voulez-vous vraiment supprimer cette session ?") },
            confirmButton = {
                TextButton(onClick = {
                    onDeleteConfirm()
                    showDeleteDialog = false
                }) {
                    Text("Supprimer", color = Color(0xFFFF5252), fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Garder", color = Color.White)
                }
            },
            shape = RoundedCornerShape(28.dp)
        )
    }
}