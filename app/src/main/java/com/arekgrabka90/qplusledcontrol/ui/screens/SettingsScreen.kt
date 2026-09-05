package com.arekgrabka90.qplusledcontrol.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
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
import kotlinx.coroutines.launch

@Composable
fun SettingsScreen(
    preferenceRepository: PreferenceRepository
) {
    val scope = rememberCoroutineScope()
    val appSettings by preferenceRepository.appSettings.collectAsState(
        initial = com.arekgrabka90.qplusledcontrol.models.AppSettings()
    )

    var selectedOption by remember { mutableStateOf(0) }
    var launchAtBoot by remember { mutableStateOf(appSettings.launchAtBoot) }
    var backgroundControl by remember { mutableStateOf(appSettings.backgroundControl) }
    var detectAppChanges by remember { mutableStateOf(appSettings.detectAppChanges) }
    var reactToStandby by remember { mutableStateOf(appSettings.reactToStandby) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(48.dp)
            .onKeyEvent { event ->
                when {
                    event.key == Key.DirectionDown -> {
                        selectedOption = (selectedOption + 1) % 6
                        true
                    }
                    event.key == Key.DirectionUp -> {
                        selectedOption = if (selectedOption > 0) selectedOption - 1 else 5
                        true
                    }
                    event.key == Key.Enter -> {
                        when (selectedOption) {
                            0 -> launchAtBoot = !launchAtBoot
                            1 -> backgroundControl = !backgroundControl
                            2 -> detectAppChanges = !detectAppChanges
                            3 -> reactToStandby = !reactToStandby
                            4 -> {
                                scope.launch {
                                    preferenceRepository.updateAppSettings(
                                        appSettings.copy(
                                            launchAtBoot = launchAtBoot,
                                            backgroundControl = backgroundControl,
                                            detectAppChanges = detectAppChanges,
                                            reactToStandby = reactToStandby
                                        )
                                    )
                                }
                            }
                            5 -> {
                                // Navigate to diagnostics
                            }
                        }
                        true
                    }
                    else -> false
                }
            },
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "SETTINGS",
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1f78d1)
        )

        Spacer(modifier = Modifier.height(24.dp))

        SettingsCheckbox(
            label = "Launch at Boot",
            checked = launchAtBoot,
            selected = selectedOption == 0
        )

        SettingsCheckbox(
            label = "Background Control",
            checked = backgroundControl,
            selected = selectedOption == 1
        )

        SettingsCheckbox(
            label = "Detect App Changes",
            checked = detectAppChanges,
            selected = selectedOption == 2
        )

        SettingsCheckbox(
            label = "React to Standby",
            checked = reactToStandby,
            selected = selectedOption == 3
        )

        Spacer(modifier = Modifier.height(32.dp))

        Button(
            onClick = { },
            modifier = Modifier
                .width(400.dp)
                .height(80.dp),
        ) {
            Text(
                text = "SAVE",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = if (selectedOption == 4) Color(0xFF1f78d1) else Color.White
            )
        }

        Button(
            onClick = { },
            modifier = Modifier
                .width(400.dp)
                .height(80.dp),
        ) {
            Text(
                text = "DIAGNOSTICS",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = if (selectedOption == 5) Color(0xFF1f78d1) else Color.White
            )
        }
    }
}

@Composable
fun SettingsCheckbox(
    label: String,
    checked: Boolean,
    selected: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = { },
            modifier = Modifier.size(40.dp)
        )
        Text(
            text = label,
            fontSize = 28.sp,
            fontWeight = FontWeight.Bold,
            color = if (selected) Color(0xFF1f78d1) else Color.White
        )
    }
}
