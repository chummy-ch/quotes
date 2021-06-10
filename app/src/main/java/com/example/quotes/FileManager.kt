package com.example.quotes

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.util.*


class FileManager {
    private val random = Random()

    fun saveImageWithMediaStore(context: Context, bitmap: Bitmap, imageName: String): Uri? {
        var uri: Uri? = null
        val relativeLocation = Environment.DIRECTORY_PICTURES + "/${context.getString(R.string.quotes_picture_dir)}"
        val fileName = "${imageName}_${random.nextInt()}.jpg"
        val fos: OutputStream? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val resolver: ContentResolver = context.contentResolver
            val contentValues = ContentValues()
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, relativeLocation)
            uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            resolver.openOutputStream(uri!!)
        } else {
            val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()
            val image = File(imagesDir, fileName)
            uri = Uri.fromFile(image)
            FileOutputStream(image)
        }

        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
        fos?.close()

        return uri
    }

    fun getSharedImageIntent(uri: Uri): Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_STREAM, uri)
        flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        type = "image/*"
    }
}
