package com.example.cyberon.ui.navigation

sealed class Screen(val route: String, val title: String) {
    object Home : Screen("home", "Home")
    object Discover : Screen("discover", "Discover")
    object Send : Screen("send", "Send")
    object Receive : Screen("receive", "Receive")
    object History : Screen("history", "History")
    object TransferProgress : Screen("transfer_progress", "Transfer")
    object Settings : Screen("settings", "Settings")
}
