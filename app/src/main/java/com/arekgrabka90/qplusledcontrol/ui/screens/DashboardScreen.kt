package com.arekgrabka90.qplusledcontrol.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arekgrabka90.qplusledcontrol.data.PreferenceRepository
import com.arekgrabka90.qplusledcontrol.led.LEDController
import com.arekgrabka90.qplusledcontrol.models.LEDMode
import kotlinx.coroutines.launch

@Composable
fun DashboardScreen(
    preferenceRepository: PreferenceRepository,
    ledController: LEDController,
    onNavigate: (String) -> Unit
) {
    val scope = rememberCoroutineScope()
    val ledMode by preferenceRepository.ledMode.collectAsState(initial = LEDMode.FIXED)
    val selectedColor by preferenceRepository.selectedColor.collectAsState(initial = com.arekgrabka90.qplusledcontrol.models.PresetColors.WHITE)
    val currentColor by preferenceRepository.currentColor.collectAsState(initial = com.arekgrabka90.qplusledcontrol.models.PresetColors.WHITE)

    val focusRequester = remember { FocusRequester() }
    var selectedButton by remember { mutableStateOf(0) }

    LaunchedEffect(Unit) {
        focusRequester.requestFocus()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(48.dp)
            .onKeyEvent { event ->
                when {
                    event.key == Key.DirectionDown -> {
                        selectedButton = (selectedButton + 1) % 5
                        true
                    }
                    event.key == Key.DirectionUp -> {
                        selectedButton = if (selectedButton > 0) selectedButton - 1 else 4
                        true
                    }
                    event.key == Key.Enter -> {
                        when (selectedButton) {
                            0 -> onNavigate("ledmode")
                            1 -> onNavigate("color")
                            2 -> onNavigate("test")
                            3 -> onNavigate("standby")
                            4 -> onNavigate("settings")
                        }
                        true
                    }
                    else -> false
                }
            }
            .focusRequester(focusRequester)
            .focusable(),
        verticalArrangement = Arrangement.spacedBy(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Q+ LED Control",
            fontSize = 56.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1f78d1)
        )

        Spacer(modifier = Modifier.height(24.dp))

        Text(
            text = "LED Mode: ${ledMode.name}",
            fontSize = 32.sp,
            color = Color.White
        )

        Text(
            text = "Current Color: ${currentColor.toHex()}",
            fontSize = 32.sp,
            color = Color.White
        )

        Spacer(modifier = Modifier.height(32.dp))

        DashboardButton(
            text = "LED MODE",
            selected = selectedButton == 0
        )

        DashboardButton(
            text = "COLOR",
            selected = selectedButton == 1
        )

        DashboardButton(
            text = "LED TEST",
            selected = selectedButton == 2
        )

        DashboardButton(
            text = "STANDBY",
            selected = selectedButton == 3
        )

        DashboardButton(
            text = "SETTINGS",
            selected = selectedButton == 4
        )
    }
}

@Composable
fun DashboardButton(
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
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = if (selected) Color(0xFF1f78d1) else Color.White
        )
    }
}
