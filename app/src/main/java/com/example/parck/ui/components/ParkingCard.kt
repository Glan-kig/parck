package com.example.parck.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.parck.data.model.ParkingSession
import com.example.parck.ui.viewmodel.ParkingViewModel

@Composable
fun ParkingCard(
    session: ParkingSession,
    calculatedAmount: Double
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        // Formes aux coins coupés pour l'aspect urbain 
        shape = CutCornerShape(topStart = 16.dp, bottomEnd = 16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF263238) // Gris Bitume
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Affichage de la photo via Coil [cite: 29]
            AsyncImage(
                model = session.photoUrl,
                contentDescription = "Photo véhicule",
                modifier = Modifier
                    .size(80.dp)
                    .clip(CutCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )

            Column(modifier = Modifier.padding(start = 16.dp)) {
                // Typographie à espacement fixe pour la plaque 
                Text(
                    text = session.plateNumber,
                    style = TextStyle(
                        fontFamily = FontFamily.Monospace, // Ou Space Mono 
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = Color.White
                    )
                )

                Text(
                    text = "Type: ${session.vehicleType}",
                    color = Color.LightGray
                )

                // Montant accumulé mis à jour en temps réel [cite: 13, 27]
                Text(
                    text = "Frais : ${String.format("%.2f", calculatedAmount)} €",
                    color = Color(0xFF00E676), // Vert "Accès autorisé" 
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 18.sp
                )
            }
        }
    }
}

@Composable
fun DashboardScreen(viewModel: ParkingViewModel = viewModel()) {
    // Observation de l'état via StateFlow
    val sessions by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            SmallTopAppBar(title = { Text("ParkSmart - Dashboard") })
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { /* Naviguer vers le formulaire d'entrée [cite: 14] */ },
                containerColor = Color(0xFF2979FF) // Bleu Électrique
            ) {
                Icon(Icons.Default.Add, contentDescription = "Ajouter un véhicule")
            }
        }
    ) { padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(sessions) { session ->
                // Chaque carte affiche les données en temps réel [cite: 13]
                ParkingCard(
                    session = session,
                    calculatedAmount = viewModel.getCurrentAmount(session)
                )
            }
        }
    }
}

@Composable
fun SmallTopAppBar(title: @Composable () -> Unit) {
    TODO("Not yet implemented")
}