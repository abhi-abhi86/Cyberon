package com.example.cyberon.data

import android.content.Context
import android.net.Uri
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.DataInputStream
import java.io.DataOutputStream
import java.io.File
import java.io.FileOutputStream
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.atomic.AtomicBoolean

class TransferRepository(private val context: Context) {

    private val TAG = "TransferRepo"
    private val TCP_PORT = 8080 // Standardized Port
    private val _transferProgress = MutableStateFlow<Float>(0f)
    val transferProgress: StateFlow<Float> = _transferProgress
    
    private val isRunning = AtomicBoolean(true)
    
    // Callback for authorization (In real app, use a Channel/Flow to UI)
    var onConnectionRequest: ((String, String, () -> Unit, () -> Unit) -> Unit)? = null

    private var serverSocket: ServerSocket? = null

    // Server: Receive
    suspend fun startServer() = withContext(Dispatchers.IO) {
        try {
            serverSocket = ServerSocket(TCP_PORT)
            Log.d(TAG, "TCP Server listening on $TCP_PORT")

            while (isRunning.get()) {
                val socket = serverSocket?.accept()
                socket?.let { 
                    Log.d(TAG, "Connection from: ${it.inetAddress}")
                    handleClient(it) 
                }
            }
        } catch (e: Exception) {
             if (isRunning.get()) {
                 Log.e(TAG, "Server error", e)
             }
        } finally {
            serverSocket?.close()
        }
    }
    
    fun stop() {
        isRunning.set(false)
        try {
            serverSocket?.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun handleClient(socket: Socket) {
        try {
            val dis = DataInputStream(socket.getInputStream())
            val dos = DataOutputStream(socket.getOutputStream())

            // 1. Read JSON Handshake
            val jsonLen = dis.readInt()
            val jsonBytes = ByteArray(jsonLen)
            dis.readFully(jsonBytes)
            val jsonStr = String(jsonBytes)
            
            val metadata = JSONObject(jsonStr)
            val fileName = metadata.getString("filename")
            val fileSize = metadata.getLong("filesize")
            
            // 2. Authorization (Mocking blocking call for simplicity here, ideally async)
            // For now, auto-accept or logic
            // In a real implementation: suspend until UI replies.
            
            // 3. Check Resume
             val file = File(context.getExternalFilesDir(null), fileName + ".download")
            var receivedBytes = 0L
            
            if (file.exists()) {
                receivedBytes = file.length()
            }
            
            // 4. Send Response
            if (receivedBytes >= fileSize) {
                 dos.writeUTF("SKIP")
                 socket.close()
                 return
            }
            
            dos.writeUTF("ACCEPT")
            dos.writeLong(receivedBytes) // SEEK instruction
            
            // 5. Stream
            val fos = FileOutputStream(file, true) // Append
            val buffer = ByteArray(64 * 1024) // 64KB Chunk
            var bytes = 0
            
            while (receivedBytes < fileSize) {
                val read = dis.read(buffer)
                if (read == -1) break
                fos.write(buffer, 0, read)
                receivedBytes += read
                _transferProgress.value = receivedBytes.toFloat() / fileSize
            }
            
            fos.close()
            
            // Rename .download to actual
            file.renameTo(File(context.getExternalFilesDir(null), fileName))
            Log.d(TAG, "File received: $fileName")
            
        } catch (e: Exception) {
            Log.e(TAG, "Transfer failed", e)
        } finally {
            socket.close()
        }
    }

    // Client: Send
    suspend fun sendFile(host: String, port: Int, uri: Uri) = withContext(Dispatchers.IO) {
        try {
            val socket = Socket(host, port)
            val dos = DataOutputStream(socket.getOutputStream())
            val dis = DataInputStream(socket.getInputStream())
            
            // 1. Get File Info
            val cursor = context.contentResolver.query(uri, null, null, null, null)
            val nameIndex = cursor?.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
            val sizeIndex = cursor?.getColumnIndex(android.provider.OpenableColumns.SIZE)
            
            cursor?.moveToFirst()
            val fileName = cursor?.getString(nameIndex ?: 0) ?: "unknown_file"
            val fileSize = cursor?.getLong(sizeIndex ?: 0) ?: 0L
            cursor?.close()
            
            // 2. Send Handshake JSON
            val json = JSONObject()
            json.put("filename", fileName)
            json.put("filesize", fileSize)
            val jsonBytes = json.toString().toByteArray()
            
            dos.writeInt(jsonBytes.size)
            dos.write(jsonBytes)
            
            // 3. Wait for Response
            val status = dis.readUTF()
            if (status != "ACCEPT") {
                Log.d(TAG, "File skipped or rejected: $status")
                socket.close()
                return@withContext
            }
            
            val offset = dis.readLong()
            Log.d(TAG, "Resuming from $offset")
            
            // 4. Send Data at Offset
            val inputStream = context.contentResolver.openInputStream(uri)
            inputStream?.skip(offset)
            
            val buffer = ByteArray(64 * 1024)
            var sentBytes = offset
            
            while (sentBytes < fileSize) {
                val read = inputStream?.read(buffer) ?: -1
                if (read == -1) break
                dos.write(buffer, 0, read)
                sentBytes += read
                _transferProgress.value = sentBytes.toFloat() / fileSize
            }
            
            inputStream?.close()
            socket.close()
            Log.d(TAG, "File sent")
            
        } catch (e: Exception) {
            Log.e(TAG, "Send failed", e)
        }
    }
}
