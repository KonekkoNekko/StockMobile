package com.bielcode.stockmobile.ui.screens.utility.camera

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bielcode.stockmobile.data.repository.Repository
import kotlinx.coroutines.launch
import java.io.ByteArrayOutputStream
import android.graphics.Bitmap
import android.graphics.BitmapFactory

class CameraViewModel(private val repository: Repository) : ViewModel() {

    fun uploadPhoto(
        context: Context,
        uri: Uri,
        filename: String,
        onSuccess: (String) -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val inputStream = context.contentResolver.openInputStream(uri)
                val bitmap = BitmapFactory.decodeStream(inputStream)
                val byteArrayOutputStream = ByteArrayOutputStream()
                val isJpg = filename.endsWith(".jpg", ignoreCase = true) || filename.endsWith(".jpeg", ignoreCase = true)
                bitmap.compress(
                    if (isJpg) Bitmap.CompressFormat.JPEG else Bitmap.CompressFormat.PNG,
                    100,
                    byteArrayOutputStream
                )
                val imageData = byteArrayOutputStream.toByteArray()
                repository.uploadPhoto(imageData, filename, onSuccess, onFailure)
            } catch (e: Exception) {
                onFailure(e)
            }
        }
    }
}
