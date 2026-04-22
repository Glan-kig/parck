package com.example.parck.ui.components

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.parck.data.model.VehicleType
import com.example.parck.ui.viewmodel.ParkingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntryFormScreen(viewModel: ParkingViewModel) {
    var plateNumber by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(VehicleType.VOITURE) }
    var capturedImageUri by remember { mutableStateOf<Uri?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Champ Plaque avec police Mono
        OutlinedTextField(
            value = plateNumber,
            onValueChange = { plateNumber = it.uppercase() },
            label = { Text("Plaque d'immatriculation") },
            textStyle = TextStyle(fontFamily = FontFamily.Monospace, fontWeight = FontWeight.Bold),
            modifier = Modifier.fillMaxWidth(),
            shape = CutCornerShape(8.dp) // Design urbain
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Sélecteur de type de véhicule
        Text("Type de véhicule :", style = MaterialTheme.typography.bodyLarge)
        Row {
            VehicleType.values().forEach { type ->
                FilterChip(
                    selected = selectedType == type,
                    onClick = { selectedType = type },
                    label = { Text(type.name) },
                    modifier = Modifier.padding(4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Zone d'aperçu de la photo
        Box(
            modifier = Modifier
                .size(200.dp)
                .clip(CutCornerShape(12.dp))
                .background(Color.DarkGray),
            contentAlignment = Alignment.Center
        ) {
            if (capturedImageUri != null) {
                AsyncImage(model = capturedImageUri, contentDescription = null)
            } else {
                IconButton(onClick = { /* Lancer Camera */ }) {
                    Icon(Icons.Default.AddAPhoto, contentDescription = "Prendre une photo", tint = Color.White)
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Bouton de validation (Bleu Électrique)
        Button(
            onClick = { viewModel.registerEntry(plateNumber, selectedType, capturedImageUri) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2979FF)),
            shape = CutCornerShape(8.dp)
        ) {
            Text("ENREGISTRER L'ENTRÉE")
        }
    }
}