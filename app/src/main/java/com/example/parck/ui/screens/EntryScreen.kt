package com.example.parck.ui.screens

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.common.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.parck.data.model.VehicleType
import com.example.parck.ui.theme.BitumenGrey
import com.example.parck.ui.theme.BlueElectric
import com.example.parck.ui.theme.PlateTypography
import com.example.parck.ui.viewmodel.ParkingViewModel
import java.io.ByteArrayOutputStream

@Composable
fun EntryFormScreen(viewModel: ParkingViewModel, onEntrySaved: () -> Unit) {
    var plateNumber by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(VehicleType.VOITURE) }
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }

    // Launcher pour la caméra
    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { capturedBitmap ->
        bitmap = capturedBitmap
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Nouvelle Entrée",
            style = MaterialTheme.typography.headlineMedium,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Champ Plaque (Style Monospace)
        OutlinedTextField(
            value = plateNumber,
            onValueChange = { plateNumber = it.uppercase() },
            label = { Text("Plaque d'immatriculation") },
            textStyle = PlateTypography,
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.medium
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Sélecteur de type (MOTO, VOITURE, CAMION)
        Text("Type de véhicule", color = Color.LightGray)
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            VehicleType.values().forEach { type ->
                FilterChip(
                    selected = selectedType == type,
                    onClick = { selectedType = type },
                    label = { Text(type.name) },
                    shape = MaterialTheme.shapes.small
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Zone Photo
        Box(
            modifier = Modifier
                .size(200.dp)
                .clip(MaterialTheme.shapes.large)
                .background(BitumenGrey)
                .clickable { cameraLauncher.launch() },
            contentAlignment = Alignment.Center
        ) {
            if (bitmap != null) {
                Image(bitmap!!.asImageBitmap(), contentDescription = null, contentScale = ContentScale.Crop)
            } else {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(Icons.Default.AddAPhoto, contentDescription = null, tint = BlueElectric)
                    Text("Prendre une photo", color = BlueElectric, style = MaterialTheme.typography.labelSmall)
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Bouton Valider (Bleu Électrique)
        Button(
            onClick = {
                val stream = ByteArrayOutputStream()
                bitmap?.compress(Bitmap.CompressFormat.JPEG, 80, stream)
                val imageBytes = stream.toByteArray()

                viewModel.registerVehicleEntry(plateNumber, selectedType, imageBytes)
                onEntrySaved()
            },
            enabled = plateNumber.isNotBlank() && bitmap != null,
            modifier = Modifier.fillMaxWidth().height(56.dp),
            colors = ButtonDefaults.buttonColors(containerColor = BlueElectric),
            shape = MaterialTheme.shapes.medium
        ) {
            Text("ENREGISTRER L'ENTRÉE", fontWeight = FontWeight.Bold)
        }
    }
}