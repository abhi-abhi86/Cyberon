package com.example.cyberon.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.cyberon.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SendScreen(onSend: () -> Unit) {
    var selectedCategory by remember { mutableStateOf(0) }
    val categories = listOf("Apps", "Photos", "Videos", "Files")
    
    // Mock Selection State
    val selectedItems = remember { mutableStateListOf<String>() }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(listOf(CyberBlack, CyberDark)))
    ) {
        // App Bar
        CenterAlignedTopAppBar(
            title = { Text("SELECT FILES", style = MaterialTheme.typography.titleMedium, color = NeonBlue, letterSpacing = 2.sp) },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(containerColor = Color.Transparent)
        )

        // Category Tabs
        ScrollableTabRow(
            selectedTabIndex = selectedCategory,
            containerColor = Color.Transparent,
            contentColor = NeonBlue,
            edgePadding = 0.dp,
            indicator = { tabPositions ->
                TabRowDefaults.Indicator(
                    modifier = Modifier.tabIndicatorOffset(tabPositions[selectedCategory]),
                    color = NeonBlue,
                    height = 2.dp
                )
            }
        ) {
            categories.forEachIndexed { index, title ->
                Tab(
                    selected = selectedCategory == index,
                    onClick = { selectedCategory = index },
                    text = { 
                        Text(
                            title, 
                            color = if (selectedCategory == index) Color.White else CyberTextSecondary,
                            fontWeight = if (selectedCategory == index) FontWeight.Bold else FontWeight.Normal
                        ) 
                    }
                )
            }
        }

        // Content Grid
        Box(modifier = Modifier.weight(1f)) {
            when (selectedCategory) {
                0 -> CategoryGrid(
                    items = (1..20).map { "App Item $it" }, 
                    icon = Icons.Default.Android, 
                    selectedItems = selectedItems,
                    onToggle = { if (selectedItems.contains(it)) selectedItems.remove(it) else selectedItems.add(it) }
                )
                1 -> CategoryGrid(
                    items = (1..15).map { "Photo $it.jpg" }, 
                    icon = Icons.Default.Image,
                    selectedItems = selectedItems,
                    onToggle = { if (selectedItems.contains(it)) selectedItems.remove(it) else selectedItems.add(it) }
                )
                2 -> CategoryGrid(
                    items = (1..10).map { "Video $it.mp4" }, 
                    icon = Icons.Default.Videocam,
                    selectedItems = selectedItems,
                    onToggle = { if (selectedItems.contains(it)) selectedItems.remove(it) else selectedItems.add(it) }
                )
                3 -> FilesTab() // Keep old file picker logic here if needed, or simple placeholder
            }
        }

        // Bottom Selection Bar
        if (selectedItems.isNotEmpty()) {
            Surface(
                color = CyberSurface,
                tonalElevation = 8.dp,
                shadowElevation = 16.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text("Selected", color = CyberTextSecondary, style = MaterialTheme.typography.labelSmall)
                        Text("${selectedItems.size} files", color = NeonBlue, style = MaterialTheme.typography.titleMedium)
                    }
                    
                    Button(
                        onClick = onSend,
                        colors = ButtonDefaults.buttonColors(containerColor = NeonBlue, contentColor = CyberBlack),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("NEXT >", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
fun CategoryGrid(
    items: List<String>, 
    icon: androidx.compose.ui.graphics.vector.ImageVector, 
    selectedItems: List<String>,
    onToggle: (String) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(3),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(items) { item ->
            val isSelected = selectedItems.contains(item)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.clickable { onToggle(item) }
            ) {
                Box(contentAlignment = Alignment.TopEnd) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (isSelected) NeonBlue.copy(alpha=0.2f) else CyberSurface,
                        border = if (isSelected) BorderStroke(2.dp, NeonBlue) else null,
                        modifier = Modifier.size(80.dp)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(icon, null, tint = if (isSelected) NeonBlue else CyberTextSecondary, modifier = Modifier.size(32.dp))
                        }
                    }
                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .offset(x = 6.dp, y = (-6).dp)
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(NeonBlue)
                                .border(2.dp, CyberBlack, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Check, null, tint = CyberBlack, modifier = Modifier.size(16.dp))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    item, 
                    style = MaterialTheme.typography.labelSmall, 
                    color = if (isSelected) NeonBlue else CyberTextSecondary,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
fun FilesTab() {
     val launcher = rememberLauncherForActivityResult(contract = ActivityResultContracts.GetMultipleContents()) { }
     
     Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
         Button(onClick = { launcher.launch("*/*") }, colors = ButtonDefaults.buttonColors(containerColor = CyberSurface)) {
             Text("Browse System Files", color = NeonBlue)
         }
     }
}
