package com.example.cyberon

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.ui.platform.LocalContext
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Download
import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.cyberon.ui.navigation.Screen
import com.example.cyberon.ui.screens.*
import com.example.cyberon.data.P2PManager
import com.example.cyberon.ui.theme.CyberonTheme
import com.example.cyberon.ui.theme.NeonBlue
import com.example.cyberon.ui.theme.NeonPurple

class MainActivity : ComponentActivity() {

    init {
        System.loadLibrary("native-lib")
    }

    private external fun calculateChecksumFromJNI(filePath: String): String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Intent handling for shared files could be passed to the specific screen
        // For now, we launch the main app structure
        
        setContent {
            CyberonTheme {
                MainApp()
            }
        }
    }
}

@Composable
fun MainApp() {
    val navController = rememberNavController()
    val context = LocalContext.current
    
    // Runtime Permissions
    val permissions = remember {
        buildList {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                add(Manifest.permission.NEARBY_WIFI_DEVICES)
                add(Manifest.permission.READ_MEDIA_IMAGES)
                add(Manifest.permission.READ_MEDIA_VIDEO)
                add(Manifest.permission.READ_MEDIA_AUDIO)
            } else {
                add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                add(Manifest.permission.BLUETOOTH_SCAN)
                add(Manifest.permission.BLUETOOTH_ADVERTISE)
                add(Manifest.permission.BLUETOOTH_CONNECT)
            }
            add(Manifest.permission.ACCESS_FINE_LOCATION)
            add(Manifest.permission.ACCESS_COARSE_LOCATION)
            add(Manifest.permission.ACCESS_WIFI_STATE)
            add(Manifest.permission.CHANGE_WIFI_STATE)
        }.toTypedArray()
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { results ->
        // Handle results
    }

    LaunchedEffect(Unit) {
        launcher.launch(permissions)
    }

    val p2pManager = remember { P2PManager(context) }
    
    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = navBackStackEntry?.destination

                val items = listOf(
                    Screen.Discover to Icons.Default.Home,
                    Screen.Send to Icons.Default.Send,
                    Screen.Receive to Icons.Default.Download, // Using Download as proxy for Receive
                    Screen.History to Icons.Default.History
                )

                items.forEach { (screen, icon) ->
                    NavigationBarItem(
                        icon = { Icon(icon, contentDescription = screen.title) },
                        label = { Text(screen.title) },
                        selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor = NeonBlue,
                            selectedTextColor = NeonBlue,
                            indicatorColor = NeonBlue.copy(alpha = 0.2f),
                            unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                            unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(
                Screen.Home.route,
                enterTransition = { fadeIn(tween(500)) },
                exitTransition = { fadeOut(tween(500)) }
            ) {
                HomeScreen(
                    onNavigateToSend = { navController.navigate(Screen.Send.route) },
                    onNavigateToReceive = { navController.navigate(Screen.Receive.route) },
                    onNavigateToHistory = { navController.navigate(Screen.History.route) },
                    onNavigateToSettings = { navController.navigate(Screen.Settings.route) }
                )
            }
            composable(
                Screen.Discover.route,
                enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }, animationSpec = tween(500)) + fadeIn() },
                exitTransition = { slideOutHorizontally(targetOffsetX = { -1000 }, animationSpec = tween(500)) + fadeOut() },
                popEnterTransition = { slideInHorizontally(initialOffsetX = { -1000 }, animationSpec = tween(500)) + fadeIn() },
                popExitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }, animationSpec = tween(500)) + fadeOut() }
            ) {
                DiscoverScreen(
                    p2pManager = p2pManager,
                    onNavigateToTransfer = { navController.navigate(Screen.TransferProgress.route) }
                )
            }
            composable(
                Screen.Send.route,
                enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }, animationSpec = tween(500)) + fadeIn() },
                exitTransition = { slideOutHorizontally(targetOffsetX = { -1000 }, animationSpec = tween(500)) + fadeOut() },
                popEnterTransition = { slideInHorizontally(initialOffsetX = { -1000 }, animationSpec = tween(500)) + fadeIn() },
                popExitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }, animationSpec = tween(500)) + fadeOut() }
            ) {
                SendScreen(
                    onSend = { navController.navigate(Screen.Discover.route) }
                )
            }
            composable(
                Screen.Receive.route,
                enterTransition = { slideInHorizontally(initialOffsetX = { 1000 }, animationSpec = tween(500)) + fadeIn() },
                exitTransition = { slideOutHorizontally(targetOffsetX = { -1000 }, animationSpec = tween(500)) + fadeOut() },
                popEnterTransition = { slideInHorizontally(initialOffsetX = { -1000 }, animationSpec = tween(500)) + fadeIn() },
                popExitTransition = { slideOutHorizontally(targetOffsetX = { 1000 }, animationSpec = tween(500)) + fadeOut() }
            ) {
                ReceiveScreen(p2pManager = p2pManager)
            }
            composable(Screen.History.route) {
                Text("History Screen", color = NeonBlue)
            }
            composable(
                Screen.TransferProgress.route,
                enterTransition = { slideInVertically(initialOffsetY = { 1000 }, animationSpec = tween(500)) + fadeIn() },
                exitTransition = { slideOutVertically(targetOffsetY = { 1000 }, animationSpec = tween(500)) + fadeOut() },
                popEnterTransition = { fadeIn() },
                popExitTransition = { slideOutVertically(targetOffsetY = { 1000 }, animationSpec = tween(500)) + fadeOut() }
            ) {
                TransferProgressScreen(
                    onCancel = { navController.popBackStack() }
                )
            }
            composable(Screen.Settings.route) {
                SettingsScreen(
                    onBack = { navController.popBackStack() }
                )
            }
        }
    }
}
