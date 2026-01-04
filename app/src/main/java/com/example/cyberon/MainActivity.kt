package com.example.cyberon

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.cyberon.ui.CyberonViewModel
import com.example.cyberon.ui.screens.HomeScreen
import com.example.cyberon.ui.screens.QRScanScreen
import com.example.cyberon.ui.theme.CyberonTheme

class MainActivity : ComponentActivity() {
    init {
        System.loadLibrary("native-lib")
    }
    
    // Checksum function from JNI
    external fun calculateChecksumFromJNI(filePath: String): String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CyberonTheme {
                val navController = rememberNavController()
                
                // Permission Logic
                val permissions = buildList {
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
                    add(Manifest.permission.CAMERA)
                }.toTypedArray()
                
                val launcher = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.RequestMultiplePermissions()
                ) {
                    // Start app
                }
                
                LaunchedEffect(Unit) {
                    launcher.launch(permissions)
                }

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Create ViewModel SCOPED to Activity (Shared across screens)
                    val viewModel: CyberonViewModel = viewModel()
                    
                    NavHost(navController = navController, startDestination = "home") {
                        composable("home") {
                            HomeScreen(
                                viewModel = viewModel,
                                onNavigateScan = {
                                    navController.navigate("scan")
                                },
                                onNavigateSend = {
                                    navController.navigate("send")
                                },
                                onNavigateHistory = {
                                    navController.navigate("history")
                                },
                                onNavigateSettings = {
                                    navController.navigate("settings")
                                }
                            )
                        }
                        composable("scan") {
                            QRScanScreen(
                                onCodeScanned = { code -> 
                                    navController.popBackStack()
                                }
                            )
                        }
                        composable("send") {
                           com.example.cyberon.ui.screens.SendScreen(
                               onSend = { navController.popBackStack() }
                           )
                        }
                        composable("history") {
                            com.example.cyberon.ui.screens.HistoryScreen(
                                viewModel = viewModel,
                                onBack = { navController.popBackStack() }
                            )
                        }
                        composable("settings") {
                            com.example.cyberon.ui.screens.SettingsScreen(
                                viewModel = viewModel,
                                onBack = { navController.popBackStack() }
                            )
                        }
                    }
                }
            }
        }
    }
}
