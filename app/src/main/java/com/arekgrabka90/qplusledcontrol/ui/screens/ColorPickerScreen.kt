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
import com.arekgrabka90.qplusledcontrol.models.PresetColors

@Composable
fun ColorPickerScreen(
    preferenceRepository: PreferenceRepository,
    ledController: LEDController
) {
    val scope = rememberCoroutineScope()
    val selectedColor by preferenceRepository.selectedColor.collectAsState(initial = PresetColors.WHITE)
    var selectedColorIndex by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(48.dp)
            .onKeyEvent { event ->
                when {
                    event.key == Key.DirectionDown -> {
                        selectedColorIndex = (selectedColorIndex + 1) % PresetColors.all.size
                        true
                    }
                    event.key == Key.DirectionUp -> {
                        selectedColorIndex = if (selectedColorIndex > 0) selectedColorIndex - 1 else PresetColors.all.size - 1
                        true
                    }
                    event.key == Key.Enter -> {
                        kotlinx.coroutines.GlobalScope.launch {
                            preferenceRepository.setSelectedColor(PresetColors.all[selectedColorIndex])
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
            color = Color(0xFF1f78d1)
        )

        Spacer(modifier = Modifier.height(32.dp))

        PresetColors.all.forEachIndexed { index, color ->
            ColorOption(
                colorName = getColorName(index),
                color = color,
                selected = index == selectedColorIndex
            )
        }
    }
}

@Composable
fun ColorOption(
    colorName: String,
    color: com.arekgrabka90.qplusledcontrol.models.RGBColor,
    selected: Boolean
) {
    Button(
        onClick = { },
        modifier = Modifier
            .width(400.dp)
            .height(80.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(Color(color.toInt()))
            )
            Text(
                text = colorName,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = if (selected) Color(0xFF1f78d1) else Color.White
            )
        }
    }
}

private fun getColorName(index: Int): String {
    return when (index) {
        0 -> "Red"
        1 -> "Green"
        2 -> "Blue"
        3 -> "Yellow"
        4 -> "Purple"
        5 -> "Cyan"
        6 -> "White"
        7 -> "Orange"
        8 -> "Pink"
        else -> "Custom"
    }
}
