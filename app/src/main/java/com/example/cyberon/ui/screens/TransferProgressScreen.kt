package com.example.cyberon.ui.screens

import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.view.WindowManager
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cyberon.ui.theme.*

@Composable
fun TransferProgressScreen(onCancel: () -> Unit) {
    val context = LocalContext.current
    
    // Keep Screen On specifically for this high-performance task
    DisposableEffect(Unit) {
        val activity = context.findActivity()
        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        onDispose {
            activity?.window?.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(CyberBlack, CyberDark)))
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "TRANSFER IN PROGRESS",
            style = MaterialTheme.typography.titleLarge.copy(letterSpacing = 1.sp),
            color = NeonBlue
        )
        Text(
            text = "Sending data to remote device...",
            style = MaterialTheme.typography.labelMedium,
            color = CyberTextSecondary
        )

        Spacer(modifier = Modifier.height(48.dp))

        // Large Progress Graph
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(240.dp)) {
             CircularProgressIndicator(
                progress = 1f,
                modifier = Modifier.fillMaxSize(),
                color = CyberSurface,
                strokeWidth = 20.dp,
                strokeCap = StrokeCap.Round
            )
            CircularProgressIndicator(
                progress = 0.67f,
                modifier = Modifier.fillMaxSize(),
                color = NeonPurple,
                trackColor = Color.Transparent,
                strokeWidth = 20.dp,
                strokeCap = StrokeCap.Round
            )
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("67%", fontSize = 56.sp, fontWeight = FontWeight.Bold, color = Color.White)
                Text("12 MB/s", style = MaterialTheme.typography.labelMedium, color = NeonBlue)
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        Text("2 of 3 files sent", color = CyberTextSecondary)

        Spacer(modifier = Modifier.height(48.dp))

        Surface(
            shape = RoundedCornerShape(16.dp),
            color = CyberSurface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Current File", color = CyberTextSecondary, style = MaterialTheme.typography.labelSmall)
                    Text("3.4 MB / 5.1 MB", color = Color.White, style = MaterialTheme.typography.labelSmall)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("Project_Alpha_V2.pdf", color = NeonBlue, fontWeight = FontWeight.Bold, maxLines = 1)
                Spacer(modifier = Modifier.height(12.dp))
                LinearProgressIndicator(
                    progress = 0.67f, 
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)), 
                    color = NeonBlue, 
                    trackColor = CyberDark
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    StatItem("Time Elapsed", "00:12")
                    StatItem("Time Remaining", "00:06")
                }
            }
        }

        Spacer(modifier = Modifier.weight(1f))

        OutlinedButton(
            onClick = onCancel,
            colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.Red),
            border = BorderStroke(1.dp, Color.Red.copy(alpha=0.5f)),
            modifier = Modifier.fillMaxWidth().height(50.dp),
             shape = RoundedCornerShape(12.dp)
        ) {
            Text("CANCEL TRANSFER")
        }
        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = CyberTextSecondary)
        Text(value, style = MaterialTheme.typography.bodyMedium, color = Color.White, fontWeight = FontWeight.Bold)
    }
}

fun Context.findActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}
