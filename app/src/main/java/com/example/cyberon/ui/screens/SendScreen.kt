package com.example.cyberon.ui.screens

import android.graphics.Bitmap
import android.net.wifi.WifiManager
import android.os.Build
import android.text.format.Formatter
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.cyberon.ui.utils.QRGenerator
import org.json.JSONObject
import androidx.core.content.ContextCompat.getSystemService

@Composable
fun SendScreen(onSend: () -> Unit) {
    val context = LocalContext.current
    var qrBitmap by remember { mutableStateOf<Bitmap?>(null) }
    
    LaunchedEffect(Unit) {
        // Get IP
        val wifiManager = context.getSystemService(android.content.Context.WIFI_SERVICE) as WifiManager
        val ipAddress = Formatter.formatIpAddress(wifiManager.connectionInfo.ipAddress)
        
        // Spec v2.0 JSON
        val json = JSONObject()
        json.put("ip", ipAddress)
        json.put("port", 8080)
        json.put("device", "${Build.BRAND} ${Build.MODEL}")
        
        qrBitmap = QRGenerator.generateQR(json.toString())
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text("Scan to Connect", style = MaterialTheme.typography.headlineMedium)
        Spacer(Modifier.height(32.dp))
        
        qrBitmap?.let { bitmap ->
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "QR Code",
                modifier = Modifier.size(250.dp)
            )
        }
        
        Spacer(Modifier.height(32.dp))
        Text("This device is ready to send files.")
        
        Button(onClick = onSend, modifier = Modifier.padding(top=16.dp)) {
           Text("Done")
        }
    }
}
