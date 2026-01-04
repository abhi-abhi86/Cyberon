package com.example.cyberon.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cyberon.ui.CyberonViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    viewModel: CyberonViewModel = viewModel(),
    onBack: () -> Unit
) {
    val deviceName by viewModel.deviceName.collectAsState()
    var isDark by remember { mutableStateOf(true) } // Mock state, normally from DataStore
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(Modifier.padding(padding).padding(16.dp)) {
            Text("General", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(16.dp))
            
            OutlinedTextField(
                value = deviceName,
                onValueChange = { viewModel.setDeviceName(it) },
                label = { Text("Device Name") },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(Modifier.height(24.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Text("Dark Mode")
                Switch(checked = isDark, onCheckedChange = { isDark = it })
            }
            
            Spacer(Modifier.height(24.dp))
            Divider()
            Spacer(Modifier.height(24.dp))
            
            Text("About", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            Spacer(Modifier.height(8.dp))
            Text("Version: 2.0.0 (Production)")
        }
    }
}
