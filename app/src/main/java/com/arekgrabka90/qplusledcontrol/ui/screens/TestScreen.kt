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
import com.arekgrabka90.qplusledcontrol.led.LEDController
import kotlinx.coroutines.launch

@Composable
fun TestScreen(
    ledController: LEDController
) {
    val scope = rememberCoroutineScope()
    var testResult by remember { mutableStateOf("Ready to test") }
    var isTestRunning by remember { mutableStateOf(false) }
    var selectedTest by remember { mutableStateOf(0) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(48.dp)
            .onKeyEvent { event ->
                when {
                    event.key == Key.DirectionDown -> {
                        selectedTest = (selectedTest + 1) % 4
                        true
                    }
                    event.key == Key.DirectionUp -> {
                        selectedTest = if (selectedTest > 0) selectedTest - 1 else 3
                        true
                    }
                    event.key == Key.Enter -> {
                        isTestRunning = true
                        scope.launch {
                            testResult = when (selectedTest) {
                                0 -> {
                                    val result = ledController.testAllLEDs()
                                    if (result.success) "All LEDs tested successfully" else "LED test failed: ${result.message}"
                                }
                                1 -> {
                                    val result = ledController.testNormalLED()
                                    if (result.success) "Normal LED test passed" else "Normal LED test failed: ${result.message}"
                                }
                                2 -> {
                                    val result = ledController.testStandbyLED()
                                    if (result.success) "Standby LED test passed" else "Standby LED test failed: ${result.message}"
                                }
                                3 -> {
                                    val result = ledController.testNetworkLED()
                                    if (result.success) "Network LED test passed" else "Network LED test failed: ${result.message}"
                                }
                                else -> "Unknown test"
                            }
                            isTestRunning = false
                        }
                        true
                    }
                    else -> false
                }
            },
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Text(
            text = "LED HARDWARE TEST",
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1f78d1)
        )

        Spacer(modifier = Modifier.height(32.dp))

        TestButton(
            text = "TEST ALL LEDs",
            selected = selectedTest == 0,
            running = isTestRunning
        )

        TestButton(
            text = "TEST NORMAL LED",
            selected = selectedTest == 1,
            running = isTestRunning
        )

        TestButton(
            text = "TEST STANDBY LED",
            selected = selectedTest == 2,
            running = isTestRunning
        )

        TestButton(
            text = "TEST NETWORK LED",
            selected = selectedTest == 3,
            running = isTestRunning
        )

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = testResult,
            fontSize = 28.sp,
            color = Color.White,
            modifier = Modifier.padding(24.dp)
        )
    }
}

@Composable
fun TestButton(
    text: String,
    selected: Boolean,
    running: Boolean
) {
    Button(
        onClick = { },
        modifier = Modifier
            .width(400.dp)
            .height(80.dp),
        enabled = !running
    ) {
        Text(
            text = text,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = if (selected) Color(0xFF1f78d1) else Color.White
        )
    }
}
