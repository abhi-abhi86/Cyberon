package com.example.cyberon.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cyberon.ui.theme.*
import com.example.cyberon.data.P2PManager

@Composable
fun DiscoverScreen(p2pManager: P2PManager, onNavigateToTransfer: () -> Unit) {
    val devices by p2pManager.discoveredDevices.collectAsState()
    val status by p2pManager.connectionStatus.collectAsState()
    var isScanning by remember { mutableStateOf(false) }
    
    // Auto-update scanning state based on connection status if needed, or just keep manual toggle
    // For now, manual toggle triggers manager
    
    // Pulse animation for scanning
    val infiniteTransition = rememberInfiniteTransition(label = "scan")
    val scanAlpha by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )
    val radarRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(CyberBlack, CyberDark)))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "DISCOVER DEVICES",
            style = MaterialTheme.typography.titleLarge.copy(letterSpacing = 2.sp),
            color = NeonBlue
        )
        Text(
            text = "Find nearby devices to share files securely",
            style = MaterialTheme.typography.labelMedium,
            color = CyberTextSecondary
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Radar / Scan Button Area
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(200.dp)) {
            if (isScanning) {
                // Rotating Sweep
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .rotate(radarRotation)
                        .clip(CircleShape)
                        .background(
                            Brush.sweepGradient(
                                listOf(
                                    Color.Transparent,
                                    NeonBlue.copy(alpha = 0.1f),
                                    NeonBlue.copy(alpha = 0.5f)
                                )
                            )
                        )
                )
                // Pulse Ring
                Box(
                    modifier = Modifier
                        .size(160.dp)
                        .border(1.dp, NeonBlue.copy(alpha = scanAlpha), CircleShape)
                )
                // Outer static ring
                 Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .border(2.dp, NeonBlue.copy(alpha = 0.3f), CircleShape)
                )
            }
            
            Button(
                onClick = { 
                    isScanning = !isScanning 
                    if (isScanning) p2pManager.startDiscovery() else p2pManager.stopDiscovery()
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isScanning) CyberSurface else NeonBlue,
                    contentColor = if (isScanning) NeonBlue else CyberBlack
                ),
                shape = CircleShape,
                modifier = Modifier
                    .size(100.dp) // Slightly smaller button to show radar better
                    .shadow(
                        elevation = if (isScanning) 0.dp else 20.dp,
                        spotColor = NeonBlue,
                        shape = CircleShape
                    ),
                border = if (isScanning) BorderStroke(2.dp, NeonBlue) else null
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        if (isScanning) Icons.Default.Search else Icons.Default.Search, 
                        contentDescription = null, 
                        modifier = Modifier.size(28.dp)
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = if (isScanning) "STOP" else "SCAN",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, fontSize = 10.sp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(48.dp))
        
        Text(
            text = "NEARBY DEVICES",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            color = CyberTextSecondary,
            modifier = Modifier.align(Alignment.Start)
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isScanning) {
                items(devices) { device ->
                    DeviceItem(
                        name = device.name,
                        status = "Available", // Or derived from manager if needed
                        onConnect = {
                            p2pManager.requestConnection(device.id, "CyberUser")
                            onNavigateToTransfer()
                        }
                    )
                }
                if (devices.isEmpty()) {
                   item { Text("Searching...", color = CyberTextSecondary, modifier = Modifier.padding(16.dp)) }
                }
            } else {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("Tap SCAN to find devices", color = CyberTextSecondary.copy(alpha=0.5f))
                    }
                }
            }
        }
    }
}

@Composable
fun DeviceItem(name: String, status: String, onConnect: () -> Unit) {
    val isConnecting = status == "Connecting..."
    
    Surface(
        color = CyberSurface.copy(alpha = 0.6f),
        shape = RoundedCornerShape(16.dp),
        border = if (isConnecting) BorderStroke(1.dp, NeonPurple) else null,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(CyberDark),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Devices, 
                        contentDescription = null, 
                        tint = if (isConnecting) NeonPurple else NeonBlue
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = name, 
                        style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Bold), 
                        color = Color.White
                    )
                    Text(
                        text = status, 
                        style = MaterialTheme.typography.labelSmall, 
                        color = if (isConnecting) NeonPurple else Color.Green
                    )
                }
            }
            
            if (isConnecting) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = NeonPurple,
                    strokeWidth = 2.dp
                )
            } else {
                Button(
                    onClick = onConnect,
                    colors = ButtonDefaults.buttonColors(containerColor = NeonBlue.copy(alpha=0.1f)),
                    contentPadding = PaddingValues(horizontal = 20.dp, vertical = 0.dp),
                    modifier = Modifier.height(36.dp),
                    border = BorderStroke(1.dp, NeonBlue.copy(alpha=0.5f))
                ) {
                    Text("Connect", color = NeonBlue, style = MaterialTheme.typography.labelSmall)
                }
            }
        }
    }
}
