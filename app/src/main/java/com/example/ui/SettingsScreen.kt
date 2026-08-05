package com.example.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.ThemePreference

@Composable
fun SettingsScreen(viewModel: DriveSyncViewModel, onNavigateBack: () -> Unit) {
    val currentTheme by viewModel.themePreference.collectAsStateWithLifecycle()
    var themeDropdownExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text("Account", style = MaterialTheme.typography.titleMedium)
        Button(onClick = { /* Handle Google Sign In */ }, modifier = Modifier.fillMaxWidth()) {
            Text("Sign in with Google Drive")
        }

        HorizontalDivider()

        Text("App Preferences", style = MaterialTheme.typography.titleMedium)
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Text("Theme")
            
            Box {
                OutlinedButton(onClick = { themeDropdownExpanded = true }) {
                    Text(currentTheme.name)
                }
                DropdownMenu(
                    expanded = themeDropdownExpanded,
                    onDismissRequest = { themeDropdownExpanded = false }
                ) {
                    ThemePreference.values().forEach { theme ->
                        DropdownMenuItem(
                            text = { Text(theme.name) },
                            onClick = {
                                viewModel.setThemePreference(theme)
                                themeDropdownExpanded = false
                            }
                        )
                    }
                }
            }
        }

        HorizontalDivider()

        Text("Global Sync Preferences", style = MaterialTheme.typography.titleMedium)
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Text("Sync only over WiFi")
            Switch(checked = true, onCheckedChange = { /* Update prefs */ })
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            Text("Sync only while charging")
            Switch(checked = false, onCheckedChange = { /* Update prefs */ })
        }
    }
}
