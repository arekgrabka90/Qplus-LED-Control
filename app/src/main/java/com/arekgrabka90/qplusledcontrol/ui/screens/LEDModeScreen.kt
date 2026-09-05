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
fun LEDModeScreen(
    preferenceRepository: PreferenceRepository,
    ledController: LEDController
) {
    val scope = rememberCoroutineScope()
    val ledMode by preferenceRepository.ledMode.collectAsState(initial = LEDMode.FIXED)
    val focusRequester = remember { FocusRequester() }
    var selectedMode by remember { mutableStateOf(ledMode) }

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
                        selectedMode = when (selectedMode) {
                            LEDMode.FIXED -> LEDMode.AUTOMATIC
                            LEDMode.AUTOMATIC -> LEDMode.APPLICATION
                            LEDMode.APPLICATION -> LEDMode.MANUAL
                            LEDMode.MANUAL -> LEDMode.FIXED
                        }
                        true
                    }

                    event.key == Key.DirectionUp -> {
                        selectedMode = when (selectedMode) {
                            LEDMode.FIXED -> LEDMode.MANUAL
                            LEDMode.AUTOMATIC -> LEDMode.FIXED
                            LEDMode.APPLICATION -> LEDMode.AUTOMATIC
                            LEDMode.MANUAL -> LEDMode.APPLICATION
                        }
                        true
                    }

                    event.key == Key.Enter -> {
                        scope.launch {
                            preferenceRepository.setLedMode(selectedMode)
                        }
                        true
                    }

                    else -> false
                }
            }
            .focusRequester(focusRequester)
            .focusable(),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "LED MODES",
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1f78d1)
        )

        Spacer(modifier = Modifier.height(32.dp))

        LEDModeOption(
            text = "FIXED COLOR",
            selected = selectedMode == LEDMode.FIXED
        )

        LEDModeOption(
            text = "AUTOMATIC",
            selected = selectedMode == LEDMode.AUTOMATIC
        )

        LEDModeOption(
            text = "APPLICATION COLOR",
            selected = selectedMode == LEDMode.APPLICATION
        )

        LEDModeOption(
            text = "MANUAL",
            selected = selectedMode == LEDMode.MANUAL
        )
    }
}

@Composable
fun LEDModeOption(
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
