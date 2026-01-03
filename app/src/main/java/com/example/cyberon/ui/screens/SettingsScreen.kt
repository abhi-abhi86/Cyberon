package com.example.cyberon.ui.screens

import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cyberon.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit) {
    var notificationsEnabled by remember { mutableStateOf(true) }
    var darkModeEnabled by remember { mutableStateOf(true) }
    val context = LocalContext.current

    Scaffold(
        containerColor = Color.Transparent,
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("SETTINGS", color = NeonBlue, letterSpacing = 2.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = CyberTextSecondary)
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(listOf(CyberBlack, CyberDark)))
                .padding(padding)
                .padding(16.dp)
        ) {
            SettingItem(
                icon = Icons.Default.Notifications,
                title = "Notifications",
                subtitle = "Receive transfer updates",
                control = {
                    Switch(
                        checked = notificationsEnabled,
                        onCheckedChange = { notificationsEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = NeonBlue,
                            checkedTrackColor = NeonBlue.copy(alpha = 0.3f),
                            uncheckedThumbColor = CyberTextSecondary,
                            uncheckedTrackColor = CyberSurface
                        )
                    )
                }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            SettingItem(
                icon = Icons.Default.DarkMode,
                title = "Cyber Mode",
                subtitle = "Always on dark aesthetic",
                control = {
                    Switch(
                        checked = darkModeEnabled,
                        onCheckedChange = { darkModeEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = NeonPurple,
                            checkedTrackColor = NeonPurple.copy(alpha = 0.3f),
                            uncheckedThumbColor = CyberTextSecondary,
                            uncheckedTrackColor = CyberSurface
                        )
                    )
                }
            )

            Spacer(modifier = Modifier.height(16.dp))
            
            SettingActionItem(
                icon = Icons.Default.Security,
                title = "Privacy Policy",
                onClick = { 
                    Toast.makeText(context, "Opening Privacy Policy...", Toast.LENGTH_SHORT).show()
                    // Intent to open URL could go here
                }
            )
            
            Spacer(modifier = Modifier.weight(1f))
            
            Text(
                "Version 1.0.0 (CyberON)", 
                color = CyberTextSecondary, 
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
        }
    }
}

@Composable
fun SettingItem(
    icon: ImageVector, 
    title: String, 
    subtitle: String, 
    control: @Composable () -> Unit
) {
    Surface(
        color = CyberSurface,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, tint = NeonBlue)
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(title, color = Color.White, fontWeight = FontWeight.Bold)
                Text(subtitle, color = CyberTextSecondary, style = MaterialTheme.typography.labelSmall)
            }
            control()
        }
    }
}

@Composable
fun SettingActionItem(
    icon: ImageVector, 
    title: String, 
    onClick: () -> Unit
) {
    Surface(
        color = CyberSurface,
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(icon, null, tint = NeonBlue)
            Spacer(modifier = Modifier.width(16.dp))
            Text(title, color = Color.White, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
        }
    }
}
