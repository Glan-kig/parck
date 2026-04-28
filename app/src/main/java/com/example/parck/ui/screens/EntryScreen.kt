package com.example.parck.ui.screens

import android.graphics.Bitmap
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.launch
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.parck.data.model.VehicleType
import com.example.parck.ui.theme.*
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
        if (capturedBitmap != null) bitmap = capturedBitmap
    }

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
                text = "Nouvelle Entrée",
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                fontWeight = FontWeight.Light,
                letterSpacing = 1.5.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Zone Photo (Mise en avant)
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .background(BitumenGrey.copy(alpha = 0.5f))
                    .border(
                        width = 2.dp,
                        brush = Brush.linearGradient(listOf(BlueElectric, Color.Transparent)),
                        shape = RoundedCornerShape(32.dp)
                    )
                    .clickable(enabled = !isUploading) { cameraLauncher.launch() },
                contentAlignment = Alignment.Center
            ) {
                if (bitmap != null) {
                    Image(
                        bitmap!!.asImageBitmap(),
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                    // Petit badge pour indiquer qu'on peut changer la photo
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .padding(12.dp)
                            .size(32.dp)
                            .background(BlueElectric, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = android.R.drawable.ic_menu_camera),
                            contentDescription = null,
                            tint = BitumenDark,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            painter = painterResource(id = android.R.drawable.ic_menu_camera),
                            contentDescription = null,
                            tint = BlueElectric,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("PRENDRE PHOTO", style = MaterialTheme.typography.labelSmall, color = BlueElectric)
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Formulaire
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(28.dp),
                colors = CardDefaults.cardColors(containerColor = BitumenGrey.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "INFORMATIONS VÉHICULE",
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.Gray
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    TextField(
                        value = plateNumber,
                        onValueChange = { plateNumber = it.uppercase() },
                        placeholder = { Text("EX: AA-123-BB", color = Color.DarkGray) },
                        textStyle = PlateTypography.copy(textAlign = TextAlign.Center, fontSize = 24.sp),
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            focusedIndicatorColor = BlueElectric,
                            unfocusedIndicatorColor = Color.Gray.copy(alpha = 0.5f),
                            cursorColor = BlueElectric,
                            focusedTextColor = Color.White
                        ),
                        singleLine = true,
                        enabled = !isUploading
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Sélecteur de type simplifié
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        VehicleType.values().forEach { type ->
                            val isSelected = selectedType == type
                            Surface(
                                modifier = Modifier
                                    .weight(1f)
                                    .height(48.dp)
                                    .clickable { selectedType = type },
                                shape = RoundedCornerShape(12.dp),
                                color = if (isSelected) BlueElectric else Color.White.copy(alpha = 0.05f),
                                border = if (isSelected) null else borderStroke(1.dp, Color.White.copy(alpha = 0.1f))
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(
                                        text = type.name,
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) BitumenDark else Color.White
                                    )
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Action
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
                    enabled = plateNumber.length >= 3 && bitmap != null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(72.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BlueElectric,
                        disabledContainerColor = Color.Gray.copy(alpha = 0.2f)
                    ),
                    shape = RoundedCornerShape(20.dp),
                    elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
                ) {
                    Text(
                        "VALIDER L'ENTRÉE",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }
    }
}

// Petite fonction utilitaire pour les bordures
@Composable
fun borderStroke(width: androidx.compose.ui.unit.Dp, color: Color) = androidx.compose.foundation.BorderStroke(width, color)