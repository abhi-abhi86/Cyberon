package com.example.cyberon.ui.utils

import android.graphics.Bitmap
import android.graphics.Color
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter

object QRGenerator {
    fun generateQR(content: String, size: Int = 512): Bitmap? {
        return try {
            val writer = MultiFormatWriter()
            val matrix = writer.encode(content, BarcodeFormat.QR_CODE, size, size)
            val width = matrix.width
            val height = matrix.height
            
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
            
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(x, y, if (matrix.get(x, y)) Color.BLACK else Color.WHITE)
                }
            }
            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}
