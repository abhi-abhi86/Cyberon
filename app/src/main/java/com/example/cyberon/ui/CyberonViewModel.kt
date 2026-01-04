package com.example.cyberon.ui

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.cyberon.data.NetworkDevice
import com.example.cyberon.data.UdpDiscoveryManager
import com.example.cyberon.data.TransferRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import android.os.Build

class CyberonViewModel(application: Application) : AndroidViewModel(application) {
    
    // Switch to UDP Manager
    private val udpManager = UdpDiscoveryManager()
    private val repository = TransferRepository(application)
    
    private val _peers = MutableStateFlow<List<NetworkDevice>>(emptyList())
    val peers = _peers.asStateFlow()
    
    val transferProgress = repository.transferProgress
    
    // Settings
    private val _deviceName = MutableStateFlow("${Build.BRAND} ${Build.MODEL}")
    val deviceName = _deviceName.asStateFlow()
    
    // History (Mock)
    private val _history = MutableStateFlow<List<HistoryItem>>(emptyList())
    val history = _history.asStateFlow()
    
    init {
        // Mock History
        _history.value = listOf(
            HistoryItem("video_vacation.mp4", "Sent to Pixel 7", "Success", "10:30 AM"),
            HistoryItem("document.pdf", "Received from Samsung", "Success", "Yesterday")
        )
        
        val currentName = _deviceName.value
        
        // Start UDP
        viewModelScope.launch {
            launch { udpManager.startBroadcasting(currentName) }
            launch {
                udpManager.startListening().collect {
                    _peers.value = it
                }
            }
        }
        
        // Start TCP Server
        viewModelScope.launch {
            repository.startServer()
        }
    }
    
    fun sendFile(peer: NetworkDevice, uri: Uri) {
        viewModelScope.launch {
            repository.sendFile(peer.host.hostAddress ?: "", 8080, uri)
        }
    }
    
    fun setDeviceName(name: String) {
        _deviceName.value = name
        // Restart broadcast with new name logic would go here
    }
    
    override fun onCleared() {
        super.onCleared()
        udpManager.stop()
        repository.stop()
    }
}

data class HistoryItem(
    val fileName: String,
    val detail: String,
    val status: String,
    val time: String
)
