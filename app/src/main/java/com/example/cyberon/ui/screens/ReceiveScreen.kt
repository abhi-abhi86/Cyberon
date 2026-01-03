package com.example.cyberon.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.QrCode
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.WifiTethering
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cyberon.data.P2PManager
import com.example.cyberon.ui.theme.*

@Composable
fun ReceiveScreen(p2pManager: P2PManager) {
    val infiniteTransition = rememberInfiniteTransition(label = "radar")
    
    // Simulations
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(4000, easing = LinearEasing), RepeatMode.Restart), label = "rot"
    )
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.8f, targetValue = 1.2f,
        animationSpec = infiniteRepeatable(tween(2000, easing = FastOutSlowInEasing), RepeatMode.Reverse), label = "pulse"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.radialGradient(listOf(CyberDark, CyberBlack), radius = 1000f))
            .padding(top = 48.dp, bottom = 16.dp), // Adjust for BottomBar padding safe area
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title Section
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                "RECEIVE MODE",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold, letterSpacing = 2.sp),
                color = NeonBlue
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                "Broadcast visible to nearby devices",
                style = MaterialTheme.typography.bodyMedium,
                color = CyberTextSecondary
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        // Radar Animation
        Box(contentAlignment = Alignment.Center) {
            // Static Rings
            // We use explicit density to avoid confusion if needed, but DrawScope has size.
            Canvas(modifier = Modifier.size(300.dp)) {
                val strokeWidth1 = 2.dp.toPx()
                val strokeWidth2 = 1.dp.toPx()
                val strokeWidth3 = 6.dp.toPx()
                
                drawCircle(
                    color = NeonBlue.copy(alpha = 0.1f), 
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeWidth1)
                )
                drawCircle(
                    color = NeonBlue.copy(alpha = 0.05f), 
                    radius = size.minDimension / 2.5f, 
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeWidth2)
                )
                drawCircle(
                    color = NeonPurple.copy(alpha = 0.05f), 
                    radius = size.minDimension / 3.5f, 
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = strokeWidth3)
                )
            }
            
            // Rotating Sweep
            Box(
                modifier = Modifier
                    .size(300.dp)
                    .rotate(rotation)
                    .border(
                        border = BorderStroke(2.dp, Brush.sweepGradient(listOf(Color.Transparent, NeonBlue, Color.Transparent))),
                        shape = CircleShape
                    )
            )

            // Scaled Pulse Ring
            Box(
                modifier = Modifier
                    .size(140.dp)
                    .scale(pulse)
                    .background(NeonBlue.copy(alpha = 0.15f), CircleShape)
                    .border(2.dp, NeonBlue.copy(alpha = 0.5f), CircleShape)
            )

            // Center Icon
            Box(
                 modifier = Modifier
                     .size(100.dp)
                     .background(CyberSurface, CircleShape)
                     .border(2.dp, NeonBlue, CircleShape)
                     .shadow(elevation = 16.dp, shape = CircleShape, spotColor = NeonBlue, ambientColor = NeonBlue),
                 contentAlignment = Alignment.Center
            ) {
                 Icon(Icons.Default.QrCode, null, tint = Color.White, modifier = Modifier.size(48.dp))
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        // Device Info Card
        Card(
            colors = CardDefaults.cardColors(containerColor = CyberSurface),
            shape = RoundedCornerShape(24.dp),
            border = BorderStroke(1.dp, Brush.horizontalGradient(listOf(NeonPurple.copy(alpha=0.5f), Color.Transparent))),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 16.dp)
                .shadow(elevation = 8.dp, shape = RoundedCornerShape(24.dp), spotColor = NeonPurple, ambientColor = NeonPurple)
        ) {
            Row(
                modifier = Modifier.padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Device Icon
                Box(
                    modifier = Modifier.size(48.dp).background(CyberDark, RoundedCornerShape(12.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Default.Smartphone, null, tint = NeonBlue)
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column {
                    Text("My Device: CyberPhone X", style = MaterialTheme.typography.titleSmall, color = Color.White)
                    Text("Ready to receive", style = MaterialTheme.typography.bodySmall, color = Color.Green)
                }
            }
            
            Divider(color = CyberDark, thickness = 1.dp)
            
            Row(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(Icons.Default.Security, null, tint = NeonPurple, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(8.dp))
                Text("Connection Encrypted", color = CyberTextSecondary, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}
