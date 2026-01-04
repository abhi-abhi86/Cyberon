package com.example.cyberon.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cyberon.ui.CyberonViewModel
import com.example.cyberon.data.NetworkDevice
import com.example.cyberon.ui.theme.*

@Composable
fun LiquidRipple() {
    val infiniteTransition = rememberInfiniteTransition()
    
    // Wave 1 (Cyan)
    val scale1 by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 4f,
        animationSpec = infiniteRepeatable(tween(3000, easing = LinearEasing), RepeatMode.Restart)
    )
    val alpha1 by infiniteTransition.animateFloat(
        initialValue = 0.5f, targetValue = 0f,
        animationSpec = infiniteRepeatable(tween(3000, easing = LinearEasing), RepeatMode.Restart)
    )

    // Wave 2 (Purple)
    val scale2 by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 4f,
        animationSpec = infiniteRepeatable(tween(3000, delayMillis = 1000, easing = LinearEasing), RepeatMode.Restart)
    )
    val alpha2 by infiniteTransition.animateFloat(
        initialValue = 0.5f, targetValue = 0f,
        animationSpec = infiniteRepeatable(tween(3000, delayMillis = 1000, easing = LinearEasing), RepeatMode.Restart)
    )

    Canvas(modifier = Modifier.size(200.dp)) {
        // Cyan Wave
        drawCircle(
            color = CyberColors.NeonCyan.copy(alpha = alpha1),
            radius = size.minDimension / 2 * (scale1 / 3),
            style = Stroke(width = 2f)
        )
        // Purple Wave
        drawCircle(
            color = CyberColors.NeonPurple.copy(alpha = alpha2),
            radius = size.minDimension / 2 * (scale2 / 3),
            style = Stroke(width = 2f)
        )
        // Core Glow
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(CyberColors.NeonCyan.copy(alpha = 0.8f), Color.Transparent),
                radius = size.minDimension / 4
            ),
            radius = size.minDimension / 4
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: CyberonViewModel = viewModel(),
    onNavigateScan: () -> Unit,
    onNavigateSend: () -> Unit,
    onNavigateHistory: () -> Unit,
    onNavigateSettings: () -> Unit
) {
    val peers by viewModel.peers.collectAsState()
    var selectedPeer by remember { mutableStateOf<NetworkDevice?>(null) }
    
    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let {
            selectedPeer?.let { peer -> viewModel.sendFile(peer, it) }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(CyberColors.DeepSpaceBlack, CyberColors.MidnightBlue, Color(0xFF1A0B2E))
                )
            )
    ) {
        // Background Glow Orbs for Ambiance
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(colors = listOf(CyberColors.NeonPurple.copy(0.1f), Color.Transparent)),
                radius = 800f,
                center = Offset(size.width, 0f)
            )
            drawCircle(
                brush = Brush.radialGradient(colors = listOf(CyberColors.NeonCyan.copy(0.1f), Color.Transparent)),
                radius = 600f,
                center = Offset(0f, size.height)
            )
        }

        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                CenterAlignedTopAppBar(
                    title = { 
                        Text(
                            "CYBERON", 
                            style = MaterialTheme.typography.displaySmall, 
                            color = Color.White
                        ) 
                    },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = Color.Transparent
                    ),
                    actions = {
                        IconButton(onClick = onNavigateHistory) {
                            Icon(Icons.Default.History, null, tint = CyberColors.NeonCyan)
                        }
                        IconButton(onClick = onNavigateSettings) {
                            Icon(Icons.Default.Settings, null, tint = CyberColors.NeonPurple)
                        }
                    }
                )
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Liquid Pulse
                Box(contentAlignment = Alignment.Center, modifier = Modifier.height(300.dp)) {
                    LiquidRipple()
                }

                // Glass Buttons
                Row(
                    modifier = Modifier.fillMaxWidth().height(160.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    GlassButton(
                        text = "SEND",
                        icon = Icons.Default.Send,
                        gradient = Brush.linearGradient(listOf(CyberColors.NeonCyan, Color.Blue)),
                        onClick = onNavigateSend
                    )
                    GlassButton(
                        text = "RECEIVE",
                        icon = Icons.Default.Download,
                        gradient = Brush.linearGradient(listOf(CyberColors.NeonPurple, Color.Magenta)),
                        onClick = onNavigateScan
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Glass List
                Text(
                    "NEARBY DEVICES",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.Gray,
                    modifier = Modifier.align(Alignment.Start)
                )
                
                Spacer(modifier = Modifier.height(8.dp))

                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(peers) { peer ->
                        GlassCard(
                            onClick = {
                                selectedPeer = peer
                                launcher.launch(arrayOf("*/*"))
                            }
                        ) {
                            ListItem(
                                headlineContent = { Text(peer.name, color = Color.White) },
                                supportingContent = { Text(peer.host.hostAddress, color = Color.Gray) },
                                leadingContent = {
                                    Box(
                                        Modifier
                                            .size(8.dp)
                                            .clip(CircleShape)
                                            .background(CyberColors.NeonCyan)
                                            .run {
                                                // Create a glowing effect
                                                this // Blur not supported on modifier heavily without min sdk 31 effectively for basics
                                            }
                                    )
                                },
                                colors = ListItemDefaults.colors(containerColor = Color.Transparent)
                            )
                        }
                        Spacer(Modifier.height(8.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun GlassButton(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector, gradient: Brush, onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .size(150.dp)
            .clip(RoundedCornerShape(30.dp))
            .background(CyberColors.GlassWhite)
            .border(BorderStroke(1.dp, CyberColors.GlassBorder), RoundedCornerShape(30.dp))
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        // Inner Gradient Blob
        Box(
            modifier = Modifier
                .size(150.dp)
                .background(gradient, alpha = 0.2f)
        )
        
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Icon(icon, contentDescription = null, tint = Color.White, modifier = Modifier.size(48.dp))
            Spacer(Modifier.height(12.dp))
            Text(text, style = MaterialTheme.typography.titleMedium, color = Color.White)
        }
    }
}

@Composable
fun GlassCard(onClick: () -> Unit, content: @Composable () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CyberColors.GlassWhite),
        border = BorderStroke(1.dp, CyberColors.GlassBorder)
    ) {
        content()
    }
}
