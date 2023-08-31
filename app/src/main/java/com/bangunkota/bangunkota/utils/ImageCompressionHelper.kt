package com.bangunkota.bangunkota.utils

import android.content.Context
import android.graphics.Bitmap
import android.media.MediaScannerConnection
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.core.content.FileProvider
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class ImageCompressionHelper(private val context: Context) {

    // Fungsi untuk mengkompresi gambar
    fun compressImage(imageUri: Uri): Uri? {
        try {
            val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)

            // Mengubah ukuran gambar jika diperlukan
            val resizedBitmap = resizeBitmap(bitmap, 800) // Ubah 800 sesuai kebutuhan Anda

            // Kompress gambar dengan kualitas yang diinginkan (misalnya 80)
            val outputStream = ByteArrayOutputStream()
            resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 80, outputStream)

            // Simpan gambar hasil kompresi di penyimpanan eksternal
            val storageDir = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val imageFileName = "JPEG_" + timeStamp + "_"
            val file = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
            )

            val fileOutputStream = FileOutputStream(file)
            fileOutputStream.write(outputStream.toByteArray())
            fileOutputStream.close()

            // Memindai ulang media untuk mengenali gambar baru
            MediaScannerConnection.scanFile(context, arrayOf(file.path), null, null)

            return FileProvider.getUriForFile(
                context,
                "com.example.yourapp.fileprovider", // Ubah dengan nama FileProvider Anda
                file
            )
        } catch (e: IOException) {
            e.printStackTrace()
            return null
        }
    }

    // Fungsi untuk meresize bitmap
    private fun resizeBitmap(bitmap: Bitmap, maxSize: Int): Bitmap {
        var width = bitmap.width
        var height = bitmap.height

        val bitmapRatio: Double = width.toDouble() / height.toDouble()
        if (bitmapRatio > 1) {
            width = maxSize
            height = (width / bitmapRatio).toInt()
        } else {
            height = maxSize
            width = (height * bitmapRatio).toInt()
        }

        return Bitmap.createScaledBitmap(bitmap, width, height, true)
    }
}
