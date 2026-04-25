package com.example.parck.ui.screens

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.parck.data.model.VehicleType
import com.example.parck.ui.theme.BitumenGrey
import com.example.parck.ui.theme.BlueElectric
import com.example.parck.ui.theme.PlateTypography
import com.example.parck.ui.viewmodel.ParkingViewModel
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EntryFormScreen(viewModel: ParkingViewModel, onEntrySaved: () -> Unit) {
    var plateNumber by remember { mutableStateOf("") }
    var selectedType by remember { mutableStateOf(VehicleType.VOITURE) }
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isUploading by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { capturedBitmap ->
        bitmap = capturedBitmap
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Nouvelle Entrée", style = MaterialTheme.typography.headlineMedium, color = Color.White)
        Spacer(modifier = Modifier.height(24.dp))

        OutlinedTextField(
            value = plateNumber,
            onValueChange = { plateNumber = it.uppercase() },
            label = { Text("Plaque d'immatriculation") },
            textStyle = PlateTypography,
            modifier = Modifier.fillMaxWidth(),
            enabled = !isUploading
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            VehicleType.values().forEach { type ->
                FilterChip(
                    selected = selectedType == type,
                    onClick = { selectedType = type },
                    label = { Text(type.name) },
                    enabled = !isUploading
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Box(
            modifier = Modifier.size(200.dp).clip(MaterialTheme.shapes.large).background(BitumenGrey)
                .clickable(enabled = !isUploading) { cameraLauncher.launch() },
            contentAlignment = Alignment.Center
        ) {
            if (bitmap != null) {
                Image(bitmap!!.asImageBitmap(), contentDescription = null, contentScale = ContentScale.Crop)
            } else {
                Icon(painter = painterResource(id = android.R.drawable.ic_menu_camera), contentDescription = null, tint = BlueElectric)
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        if (isUploading) {
            CircularProgressIndicator(color = BlueElectric)
        } else {
            Button(
                onClick = {
                    isUploading = true
                    scope.launch {
                        val stream = ByteArrayOutputStream()
                        bitmap?.compress(Bitmap.CompressFormat.JPEG, 80, stream)
                        viewModel.registerVehicleEntry(plateNumber, selectedType, stream.toByteArray())
                        onEntrySaved()
                    }
                },
                enabled = plateNumber.isNotBlank() && bitmap != null,
                modifier = Modifier.fillMaxWidth().height(56.dp),
                colors = ButtonDefaults.buttonColors(containerColor = BlueElectric)
            ) {
                Text("ENREGISTRER L'ENTRÉE", fontWeight = FontWeight.Bold)
            }
        }
    }
}