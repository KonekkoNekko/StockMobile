package com.bielcode.stockmobile.ui.screens.utility.documentscanner

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.bielcode.stockmobile.data.repository.Repository
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream
import java.net.URL

class DocumentScannerViewModel(private val repository: Repository) : ViewModel() {
    internal val _imageUris = MutableStateFlow<List<Uri>>(emptyList())
    val imageUris: StateFlow<List<Uri>> = _imageUris

    init {
        // Initialize with empty list
        _imageUris.value = emptyList()
    }

    fun handleScanResult(context: Context, result: GmsDocumentScanningResult?, transactionCode: String) {
        result?.let {
            val pages = it.pages
            if (pages != null) {
                val uris = pages.map { page -> page.imageUri }
                _imageUris.value = uris
                Log.d("DocumentScannerVM", "Scanned image URIs: $uris")

                // Save scanned URIs to persistent storage
                saveUris(context, uris)

                viewModelScope.launch(Dispatchers.IO) {
                    uris.forEachIndexed { index, uri ->
                        saveImage(context, uri, "scan_page_$index.jpg")
                    }

                    it.pdf?.let { pdf ->
                        val pdfUri = saveFile(context, pdf.uri, "$transactionCode.pdf")
                        val jpgUri = _imageUris.value.firstOrNull()?.let { uri -> saveFile(context, uri, "$transactionCode.jpg") }
                        if (pdfUri != null && jpgUri != null) {
                            repository.saveDocumentUris(transactionCode, pdfUri.toString(), jpgUri.toString())
                            // Upload scanned documents to Firebase Storage
                            repository.uploadFile(Uri.parse(pdfUri.toString()), "$transactionCode.pdf", "transactions")
                            repository.uploadFile(Uri.parse(jpgUri.toString()), "$transactionCode.jpg", "transactions")
                        }
                    }
                }
            } else {
                Log.e("DocumentScannerVM", "No pages found in scan result")
            }
        }
    }

    fun loadDocumentUris(context: Context, transactionCode: String) {
        viewModelScope.launch {
            val documentUris = repository.getDocumentUrisFromFirebase(transactionCode)
            val uris = listOfNotNull(documentUris.first, documentUris.second).map { Uri.parse(it) }
            _imageUris.value = uris
        }
    }

    private fun saveImage(context: Context, uri: Uri, fileName: String): Uri? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val file = File(context.filesDir, fileName)
            val outputStream = FileOutputStream(file)

            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            Log.d("DocumentScannerVM", "Image saved at: ${file.absolutePath}")
            Uri.fromFile(file)
        } catch (e: Exception) {
            Log.e("DocumentScannerVM", "Failed to save image: ${e.message}")
            null
        }
    }

    private fun saveFile(context: Context, uri: Uri, fileName: String): Uri? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val file = File(context.filesDir, fileName)
            val outputStream = FileOutputStream(file)

            inputStream?.use { input ->
                outputStream.use { output ->
                    input.copyTo(output)
                }
            }
            Log.d("DocumentScannerVM", "File saved at: ${file.absolutePath}")
            Uri.fromFile(file)
        } catch (e: Exception) {
            Log.e("DocumentScannerVM", "Failed to save file: ${e.message}")
            null
        }
    }

    private fun saveUris(context: Context, uris: List<Uri>) {
        val sharedPreferences = context.getSharedPreferences("DocumentScannerPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putStringSet("scanned_uris", uris.map { it.toString() }.toSet())
        editor.apply()
    }

    fun loadSavedUris(context: Context) {
        val sharedPreferences = context.getSharedPreferences("DocumentScannerPrefs", Context.MODE_PRIVATE)
        val uriStrings = sharedPreferences.getStringSet("scanned_uris", emptySet())
        val uris = uriStrings?.map { Uri.parse(it) } ?: emptyList()
        _imageUris.value = uris
    }
}
