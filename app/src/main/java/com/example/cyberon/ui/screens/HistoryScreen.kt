package com.example.cyberon.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.cyberon.ui.CyberonViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryScreen(
    viewModel: CyberonViewModel = viewModel(),
    onBack: () -> Unit
) {
    val history by viewModel.history.collectAsState()
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Transfer History") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(Modifier.padding(padding)) {
            items(history) { item ->
                ListItem(
                    headlineContent = { Text(item.fileName) },
                    supportingContent = { Text("${item.detail} • ${item.time}") },
                    leadingContent = {
                        Icon(Icons.Default.CheckCircle, null, tint = Color.Green)
                    }
                )
                Divider()
            }
        }
    }
}
