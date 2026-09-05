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
import com.arekgrabka90.qplusledcontrol.models.NightModeBehavior
import com.arekgrabka90.qplusledcontrol.models.StandbyConfig
import kotlinx.coroutines.launch

@Composable
fun StandbyScreen(
    preferenceRepository: PreferenceRepository
) {
    val scope = rememberCoroutineScope()
    val standbyConfig by preferenceRepository.standbyConfig.collectAsState(
        initial = StandbyConfig(
            mode = com.arekgrabka90.qplusledcontrol.models.LEDMode.FIXED,
            color = com.arekgrabka90.qplusledcontrol.models.PresetColors.WHITE
        )
    )

    var selectedOption by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(48.dp)
            .onKeyEvent { event ->
                when {
                    event.key == Key.DirectionDown -> {
                        selectedOption = (selectedOption + 1) % 3
                        true
                    }
                    event.key == Key.DirectionUp -> {
                        selectedOption = if (selectedOption > 0) selectedOption - 1 else 2
                        true
                    }
                    else -> false
                }
            },
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "STANDBY MODE",
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1f78d1)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Current Mode: ${standbyConfig.mode.name}",
            fontSize = 32.sp,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(24.dp))

        StandbyOption(
            text = "USE DEFAULT COLOR",
            selected = selectedOption == 0
        )

        StandbyOption(
            text = "USE LAST COLOR",
            selected = selectedOption == 1
        )

        StandbyOption(
            text = "LED OFF",
            selected = selectedOption == 2
        )
    }
}

@Composable
fun StandbyOption(
    text: String,
    selected: Boolean
) {
    Button(
        onClick = { },
        modifier = Modifier
            .width(400.dp)
            .height(80.dp),
    ) {
        Text(
            text = text,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = if (selected) Color(0xFF1f78d1) else Color.White
        )
    }
}
