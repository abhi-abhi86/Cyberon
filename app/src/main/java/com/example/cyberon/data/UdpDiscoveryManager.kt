package com.example.cyberon.data

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import java.net.DatagramPacket
import java.net.DatagramSocket
import java.net.InetAddress
import java.net.InetSocketAddress
import java.util.concurrent.atomic.AtomicBoolean

class UdpDiscoveryManager {
    private val TAG = "UdpDiscovery"
    private val BROADCAST_PORT = 5000
    private val BEACON_INTERVAL = 2000L
    private val HEADER = "Cyberon_Beacon"
    
    private val isRunning = AtomicBoolean(false)
    
    // Beacon Broadcaster
    suspend fun startBroadcasting(deviceName: String) = withContext(Dispatchers.IO) {
        val socket = DatagramSocket()
        socket.broadcast = true
        
        val message = "$HEADER:$deviceName"
        val buffer = message.toByteArray()
        val packet = DatagramPacket(buffer, buffer.size, InetAddress.getByName("255.255.255.255"), BROADCAST_PORT)
        
        isRunning.set(true)
        Log.d(TAG, "Starting broadcast: $message")
        
        while (isRunning.get()) {
            try {
                socket.send(packet)
                delay(BEACON_INTERVAL)
            } catch (e: Exception) {
                Log.e(TAG, "Broadcast failed", e)
            }
        }
        socket.close()
    }
    
    // Beacon Listener
    fun startListening(): Flow<List<NetworkDevice>> = callbackFlow {
        val socket = DatagramSocket(null)
        socket.reuseAddress = true
        socket.bind(InetSocketAddress(BROADCAST_PORT))
        
        val buffer = ByteArray(1024)
        val packet = DatagramPacket(buffer, buffer.size)
        
        val discoveredDevices = mutableMapOf<String, NetworkDevice>() // IP -> Device
        val running = AtomicBoolean(true)

        Log.d(TAG, "Listening on port $BROADCAST_PORT")

        // Helper to prune old decides could be added here
        
        val thread = Thread {
            while (running.get()) {
                try {
                    socket.receive(packet)
                    val message = String(packet.data, 0, packet.length)
                    if (message.startsWith(HEADER)) {
                        val parts = message.split(":")
                        if (parts.size == 2) {
                            val name = parts[1]
                            val ip = packet.address
                            // Simple dedupe by IP
                            if (!discoveredDevices.containsKey(ip.hostAddress)) {
                                discoveredDevices[ip.hostAddress ?: ""] = NetworkDevice(name, ip, 8080) // Assume TCP port 8080 default
                                trySend(discoveredDevices.values.toList())
                                Log.d(TAG, "Found: $name at $ip")
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Listen failed", e)
                }
            }
        }
        thread.start()
        
        awaitClose {
            running.set(false)
            socket.close()
            thread.join()
        }
    }
    
    fun stop() {
        isRunning.set(false)
    }
}
