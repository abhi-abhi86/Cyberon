package com.example.cyberon.data

import android.content.Context
import android.net.Uri
import android.util.Log
import com.google.android.gms.nearby.Nearby
import com.google.android.gms.nearby.connection.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.FileNotFoundException

class P2PManager(private val context: Context) {

    private val connectionsClient = Nearby.getConnectionsClient(context)
    private val SERVICE_ID = "com.example.cyberon"
    private val TAG = "P2PManager"

    // State Flows for UI
    private val _discoveredDevices = MutableStateFlow<List<DiscoveredDevice>>(emptyList())
    val discoveredDevices: StateFlow<List<DiscoveredDevice>> = _discoveredDevices.asStateFlow()

    private val _connectionStatus = MutableStateFlow<ConnectionStatus>(ConnectionStatus.Disconnected)
    val connectionStatus: StateFlow<ConnectionStatus> = _connectionStatus.asStateFlow()
    
    private val _transferProgress = MutableStateFlow<Float>(0f)
    val transferProgress: StateFlow<Float> = _transferProgress.asStateFlow()

    // Internal State
    private var connectedEndpointId: String? = null
    private val discoveredEndpoints = mutableMapOf<String, DiscoveredDevice>()

    // Advertiser (Receiver)
    fun startAdvertising(nickName: String) {
        val advertisingOptions = AdvertisingOptions.Builder()
            .setStrategy(Strategy.P2P_POINT_TO_POINT)
            .build()

        connectionsClient.startAdvertising(
            nickName,
            SERVICE_ID,
            connectionLifecycleCallback,
            advertisingOptions
        )
            .addOnSuccessListener {
                Log.d(TAG, "Advertising started")
                _connectionStatus.value = ConnectionStatus.Advertising
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Advertising failed", e)
                _connectionStatus.value = ConnectionStatus.Error(e.message ?: "Advertising failed")
            }
    }

    // Discoverer (Sender)
    fun startDiscovery() {
        _discoveredDevices.value = emptyList()
        discoveredEndpoints.clear()
        
        val discoveryOptions = DiscoveryOptions.Builder()
            .setStrategy(Strategy.P2P_POINT_TO_POINT)
            .build()

        connectionsClient.startDiscovery(
            SERVICE_ID,
            endpointDiscoveryCallback,
            discoveryOptions
        )
            .addOnSuccessListener {
                Log.d(TAG, "Discovery started")
                _connectionStatus.value = ConnectionStatus.Discovering
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Discovery failed", e)
                _connectionStatus.value = ConnectionStatus.Error(e.message ?: "Discovery failed")
            }
    }

    fun stopDiscovery() {
        connectionsClient.stopDiscovery()
        _connectionStatus.value = ConnectionStatus.Disconnected
    }

    fun stopAdvertising() {
        connectionsClient.stopAdvertising()
        _connectionStatus.value = ConnectionStatus.Disconnected
    }

    fun requestConnection(endpointId: String, myName: String) {
        connectionsClient.requestConnection(
            myName,
            endpointId,
            connectionLifecycleCallback
        )
            .addOnSuccessListener {
                Log.d(TAG, "Connection requested")
                _connectionStatus.value = ConnectionStatus.Connecting
            }
            .addOnFailureListener { e ->
                 Log.e(TAG, "Connection request failed", e)
                 _connectionStatus.value = ConnectionStatus.Error("Connection Failed")
            }
    }
    
    fun sendFile(uri: Uri) {
        connectedEndpointId?.let { endpointId ->
             try {
                val pfd = context.contentResolver.openFileDescriptor(uri, "r")
                pfd?.let {
                    val payload = Payload.fromFile(pfd)
                    connectionsClient.sendPayload(endpointId, payload)
                    Log.d(TAG, "File sent: $uri")
                }
            } catch (e: FileNotFoundException) {
                Log.e(TAG, "File not found", e)
            }
        }
    }

    // Callbacks
    private val endpointDiscoveryCallback = object : EndpointDiscoveryCallback() {
        override fun onEndpointFound(endpointId: String, info: DiscoveredEndpointInfo) {
            Log.d(TAG, "Endpoint found: $endpointId, ${info.endpointName}")
            val device = DiscoveredDevice(endpointId, info.endpointName, "Available")
            discoveredEndpoints[endpointId] = device
            _discoveredDevices.value = discoveredEndpoints.values.toList()
        }

        override fun onEndpointLost(endpointId: String) {
            Log.d(TAG, "Endpoint lost: $endpointId")
            discoveredEndpoints.remove(endpointId)
            _discoveredDevices.value = discoveredEndpoints.values.toList()
        }
    }

    private val connectionLifecycleCallback = object : ConnectionLifecycleCallback() {
        override fun onConnectionInitiated(endpointId: String, info: ConnectionInfo) {
            Log.d(TAG, "Connection initiated: $endpointId")
            // Automatically accept for now (In real app, show prompt)
            connectionsClient.acceptConnection(endpointId, payloadCallback)
            _connectionStatus.value = ConnectionStatus.Connecting
        }

        override fun onConnectionResult(endpointId: String, result: ConnectionResolution) {
            when (result.status.statusCode) {
                ConnectionsStatusCodes.STATUS_OK -> {
                    Log.d(TAG, "Connected to $endpointId")
                    connectedEndpointId = endpointId
                    _connectionStatus.value = ConnectionStatus.Connected
                    connectionsClient.stopDiscovery() // Stop discovery once connected
                    connectionsClient.stopAdvertising()
                }
                else -> {
                    Log.e(TAG, "Connection failed: ${result.status.statusCode}")
                    _connectionStatus.value = ConnectionStatus.Error("Connection Failed")
                }
            }
        }

        override fun onDisconnected(endpointId: String) {
             Log.d(TAG, "Disconnected from $endpointId")
             if (connectedEndpointId == endpointId) {
                 connectedEndpointId = null
                 _connectionStatus.value = ConnectionStatus.Disconnected
             }
        }
    }
    
    private val payloadCallback = object : PayloadCallback() {
        override fun onPayloadReceived(endpointId: String, payload: Payload) {
            Log.d(TAG, "Payload received from $endpointId, type: ${payload.type}")
            if (payload.type == Payload.Type.FILE) {
                // File received
            }
        }

        override fun onPayloadTransferUpdate(endpointId: String, update: PayloadTransferUpdate) {
             // Update progress
             if (update.totalBytes > 0) {
                 val progress = update.bytesTransferred.toFloat() / update.totalBytes
                 _transferProgress.value = progress
             }
        }
    }
}

data class DiscoveredDevice(
    val id: String,
    val name: String,
    val status: String
)

sealed class ConnectionStatus {
    object Disconnected : ConnectionStatus()
    object Discovering : ConnectionStatus()
    object Advertising : ConnectionStatus()
    object Connecting : ConnectionStatus()
    object Connected : ConnectionStatus()
    data class Error(val message: String) : ConnectionStatus()
}
