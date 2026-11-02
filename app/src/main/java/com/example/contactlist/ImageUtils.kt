package com.example.contactlist

import android.content.Context
import android.graphics.BitmapFactory
import android.net.Uri
import android.widget.ImageView
import androidx.core.content.FileProvider
import java.io.File
import java.io.FileOutputStream
import java.util.UUID

object ImageUtils {

    /**
     * Copies an image from a content Uri to app's internal storage
     * to guarantee persistent access across app restarts and activities.
     */
    fun saveImageToInternalStorage(context: Context, sourceUri: Uri): String? {
        return try {
            val fileName = "contact_${UUID.randomUUID()}.jpg"
            val destFile = File(context.filesDir, fileName)

            context.contentResolver.openInputStream(sourceUri)?.use { input ->
                FileOutputStream(destFile).use { output ->
                    input.copyTo(output)
                }
            }
            Uri.fromFile(destFile).toString()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    /**
     * Creates a new temporary Uri using FileProvider for taking a camera photo.
     */
    fun createTempCameraUri(context: Context): Pair<Uri, String> {
        val fileName = "camera_${System.currentTimeMillis()}.jpg"
        val imageFile = File(context.cacheDir, fileName)
        val authority = "${context.packageName}.fileprovider"
        val contentUri = FileProvider.getUriForFile(context, authority, imageFile)
        val fileUriString = Uri.fromFile(imageFile).toString()
        return Pair(contentUri, fileUriString)
    }

    /**
     * Safely loads contact photo into an ImageView from Uri string, resource ID, or fallback drawable.
     */
    fun loadContactPhoto(
        context: Context,
        photoUriStr: String?,
        photoResId: Int?,
        imageView: ImageView,
        fallbackResId: Int = R.drawable.ic_avatar_placeholder
    ) {
        if (!photoUriStr.isNullOrEmpty()) {
            try {
                val uri = Uri.parse(photoUriStr)
                if (uri.scheme == "file" || uri.scheme == "content") {
                    context.contentResolver.openInputStream(uri)?.use { inputStream ->
                        val bitmap = BitmapFactory.decodeStream(inputStream)
                        if (bitmap != null) {
                            imageView.setImageBitmap(bitmap)
                            return
                        }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        if (photoResId != null && photoResId != 0) {
            try {
                imageView.setImageResource(photoResId)
                return
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        imageView.setImageResource(fallbackResId)
    }
}