package com.example.cyberon.ui.screens

import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cyberon.ui.theme.*

@Composable
fun HomeScreen(
    onNavigateToSend: () -> Unit,
    onNavigateToReceive: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToSettings: () -> Unit
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(CyberBlack, CyberDark)))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 32.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "CYBERON",
                style = MaterialTheme.typography.headlineMedium,
                color = NeonBlue,
                fontWeight = FontWeight.Bold
            )
            IconButton(onClick = onNavigateToSettings) {
                Icon(Icons.Default.Settings, contentDescription = "Settings", tint = CyberTextSecondary)
            }
        }

        // Main Actions (Send / Receive)
        Row(
            modifier = Modifier.fillMaxWidth().height(180.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            DashboardButton(
                text = "SEND",
                icon = Icons.Default.Send,
                color = NeonBlue,
                onClick = onNavigateToSend,
                modifier = Modifier.weight(1f).padding(end = 8.dp)
            )
            DashboardButton(
                text = "RECEIVE",
                icon = Icons.Default.Download,
                color = NeonPurple,
                onClick = onNavigateToReceive,
                modifier = Modifier.weight(1f).padding(start = 8.dp)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Quick Actions Dashboard
        Text(
            text = "QUICK TOOLS",
            style = MaterialTheme.typography.labelLarge,
            color = CyberTextSecondary,
            modifier = Modifier.align(Alignment.Start)
        )
        
        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            QuickToolItem(Icons.Default.History, "History", NeonBlue) { onNavigateToHistory() }
            QuickToolItem(Icons.Default.CleaningServices, "Clean", Color.Green) { 
                Toast.makeText(context, "Cleaning junk files...", Toast.LENGTH_SHORT).show() 
            }
            QuickToolItem(Icons.Default.RocketLaunch, "Boost", Color.Yellow) {
                Toast.makeText(context, "Boosting RAM...", Toast.LENGTH_SHORT).show()
            }
            QuickToolItem(Icons.Default.Settings, "Analyze", NeonPurple) {
                Toast.makeText(context, "Analyzing storage...", Toast.LENGTH_SHORT).show()
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
        // Promo / Info Card
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(CyberSurface)
                .border(1.dp, Brush.linearGradient(listOf(NeonBlue.copy(alpha=0.3f), Color.Transparent)), RoundedCornerShape(16.dp))
        ) {
            Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                 Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(NeonBlue.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.RocketLaunch, null, tint = NeonBlue)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text("Ultra Fast Transfer", color = Color.White, fontWeight = FontWeight.Bold)
                    Text("Share files at lighting speed", color = CyberTextSecondary, style = MaterialTheme.typography.labelSmall)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun DashboardButton(
    text: String,
    icon: ImageVector,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(containerColor = CyberSurface),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, color.copy(alpha=0.5f)),
        modifier = modifier
            .fillMaxHeight()
            .shadow(
                elevation = 16.dp, 
                shape = RoundedCornerShape(24.dp),
                spotColor = color.copy(alpha = 0.5f), 
                ambientColor = color.copy(alpha = 0.2f)
            )
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
             Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .background(color.copy(alpha = 0.1f))
                    .border(2.dp, color, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(32.dp))
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(text, fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color.White)
        }
    }
}

@Composable
fun QuickToolItem(icon: ImageVector, label: String, color: Color, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(56.dp)
                .clip(CircleShape)
                .background(CyberSurface)
                .border(1.dp, color.copy(alpha=0.3f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = null, tint = color)
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(label, color = CyberTextSecondary, style = MaterialTheme.typography.labelSmall)
    }
}
