package com.example.cyberon.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.cyberon.ui.CyberonViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: CyberonViewModel = viewModel(),
    onNavigateScan: () -> Unit
) {
    val peers by viewModel.peers.collectAsState()
    val progress by viewModel.transferProgress.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    
    // File Picker
    var selectedPeer by remember { mutableStateOf<com.example.cyberon.data.NetworkDevice?>(null) }
    val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        uri?.let {
             selectedPeer?.let { peer ->
                 viewModel.sendFile(peer, it)
             }
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Spacer(Modifier.height(12.dp))
                NavigationDrawerItem(
                    label = { Text("Home") },
                    selected = true,
                    onClick = { /* Handle nav */ }
                )
                NavigationDrawerItem(
                    label = { Text("History") },
                    selected = false,
                    onClick = { /* Handle nav */ }
                )
                Divider()
                NavigationDrawerItem(
                    label = { Text("Settings") },
                    selected = false,
                    onClick = { /* Handle nav */ }
                )
            }
        }
    ) {
        Scaffold(
            topBar = {
                CenterAlignedTopAppBar(
                    title = { Text("Cyberon") },
                    navigationIcon = {
                        IconButton(onClick = { /* Open Drawer */ }) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu")
                        }
                    }
                )
            }
        ) { padding ->
            Column(Modifier.padding(padding).padding(16.dp)) {
                // Progress
                if (progress > 0 && progress < 1.0f) {
                    Card(Modifier.fillMaxWidth().padding(bottom = 16.dp)) {
                        Column(Modifier.padding(16.dp)) {
                            Text("Current Transfer")
                            LinearProgressIndicator(
                                progress = progress,
                                modifier = Modifier.fillMaxWidth().height(8.dp)
                            )
                            Text("${(progress * 100).toInt()}%")
                        }
                    }
                }
            
                Text("Nearby Devices", style = MaterialTheme.typography.titleLarge)
                Spacer(modifier = Modifier.height(8.dp))
                
                LazyColumn {
                    items(peers) { peer ->
                        ListItem(
                            headlineContent = { Text(peer.name) },
                            supportingContent = { Text("${peer.host}:${peer.port}") },
                            leadingContent = { Icon(Icons.Default.Person, null) },
                            modifier = Modifier.clickable {
                                selectedPeer = peer
                                launcher.launch(arrayOf("*/*"))
                            }
                        )
                        Divider()
                    }
                }
                
                if (peers.isEmpty()) {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Text("Searching for devices...")
                    }
                }
            }
        }
    }
}
