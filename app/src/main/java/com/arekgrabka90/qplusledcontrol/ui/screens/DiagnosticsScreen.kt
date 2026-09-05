package com.arekgrabka90.qplusledcontrol.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arekgrabka90.qplusledcontrol.data.PreferenceRepository
import com.arekgrabka90.qplusledcontrol.led.LEDController
import com.arekgrabka90.qplusledcontrol.led.LEDStatus
import kotlinx.coroutines.launch

@Composable
fun DiagnosticsScreen(
    preferenceRepository: PreferenceRepository,
    ledController: LEDController
) {
    val scope = rememberCoroutineScope()
    var ledState by remember { mutableStateOf(com.arekgrabka90.qplusledcontrol.led.LEDState()) }

    LaunchedEffect(Unit) {
        scope.launch {
            ledState = ledController.getState()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(48.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "DIAGNOSTICS",
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1f78d1)
        )

        Spacer(modifier = Modifier.height(32.dp))

        DiagnosticItem(
            label = "NORMAL LED",
            status = ledState.normalLED
        )

        DiagnosticItem(
            label = "STANDBY LED",
            status = ledState.standbyLED
        )

        DiagnosticItem(
            label = "NETWORK LED",
            status = ledState.networkLED
        )

        DiagnosticItem(
            label = "SYSFS ACCESS",
            status = if (ledState.hasSysfsAccess) LEDStatus.ON else LEDStatus.UNAVAILABLE
        )

        DiagnosticItem(
            label = "ROOT ACCESS",
            status = if (ledState.hasRootAccess) LEDStatus.ON else LEDStatus.UNAVAILABLE
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Access Method: ${ledState.accessMethod.name}",
            fontSize = 28.sp,
            color = Color.White
        )

        Button(
            onClick = {
                scope.launch {
                    ledController.testAllLEDs()
                }
            },
            modifier = Modifier
                .width(400.dp)
                .height(80.dp),
        ) {
            Text(
                text = "TEST ALL",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}

@Composable
fun DiagnosticItem(
    label: String,
    status: LEDStatus
) {
    val statusText = when (status) {
        LEDStatus.ON -> "OK"
        LEDStatus.OFF -> "OFF"
        LEDStatus.UNAVAILABLE -> "UNAVAILABLE"
        LEDStatus.UNKNOWN -> "UNKNOWN"
    }

    val statusColor = when (status) {
        LEDStatus.ON -> Color.Green
        LEDStatus.OFF -> Color.Yellow
        LEDStatus.UNAVAILABLE -> Color.Red
        LEDStatus.UNKNOWN -> Color.Gray
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White
        )
        Text(
            text = statusText,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = statusColor
        )
    }
}
