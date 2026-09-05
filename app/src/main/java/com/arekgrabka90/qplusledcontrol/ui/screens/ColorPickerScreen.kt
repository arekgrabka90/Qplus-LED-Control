package com.arekgrabka90.qplusledcontrol.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arekgrabka90.qplusledcontrol.data.PreferenceRepository
import com.arekgrabka90.qplusledcontrol.led.LEDController
import com.arekgrabka90.qplusledcontrol.models.PresetColors
import kotlinx.coroutines.launch

@Composable
fun ColorPickerScreen(
    preferenceRepository: PreferenceRepository,
    ledController: LEDController
) {
    val scope = rememberCoroutineScope()

    val selectedColor by preferenceRepository.selectedColor.collectAsState(
        initial = PresetColors.BLUE
    )

    var selectedColorIndex by remember { mutableStateOf(0) }

    LaunchedEffect(selectedColor) {
        selectedColorIndex = PresetColors.all.indexOf(selectedColor).coerceAtLeast(0)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(48.dp)
            .onKeyEvent { event ->
                when {
                    event.key == Key.DirectionLeft -> {
                        selectedColorIndex =
                            if (selectedColorIndex > 0) {
                                selectedColorIndex - 1
                            } else {
                                PresetColors.all.lastIndex
                            }
                        true
                    }

                    event.key == Key.DirectionRight -> {
                        selectedColorIndex =
                            if (selectedColorIndex < PresetColors.all.lastIndex) {
                                selectedColorIndex + 1
                            } else {
                                0
                            }
                        true
                    }

                    event.key == Key.Enter -> {
                        scope.launch {
                            preferenceRepository.setSelectedColor(
                                PresetColors.all[selectedColorIndex]
                            )
                        }
                        true
                    }

                    else -> false
                }
            },
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "COLOR PICKER",
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1F78D1)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "SELECT COLOR",
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(16.dp))

        PresetColors.all.forEachIndexed { index, color ->
            Button(
                onClick = {
                    selectedColorIndex = index

                    scope.launch {
                        preferenceRepository.setSelectedColor(color)
                    }
                },
                modifier = Modifier
                    .width(500.dp)
                    .height(70.dp)
            ) {
                Text(
                    text = color.name,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "CURRENT: ${PresetColors.all[selectedColorIndex].name}",
            fontSize = 26.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
    }
}
