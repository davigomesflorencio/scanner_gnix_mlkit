package com.davi.dev.scannermlkit.presentation.screens.viewModel

import android.app.Application
import android.net.Uri
import android.provider.OpenableColumns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.davi.dev.scannermlkit.domain.model.PdfFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AllPdfViewModel(application: Application) : AndroidViewModel(application) {

    var pdfFiles by mutableStateOf<List<PdfFile>>(emptyList())
        private set

    var isLoading by mutableStateOf(false)
        private set

    fun processPdfUris(uris: List<Uri>) {
        viewModelScope.launch {
            isLoading = true
            val newPdfFiles = mutableListOf<PdfFile>()
            withContext(Dispatchers.IO) {
                uris.forEach { uri ->
                    getApplication<Application>().contentResolver.query(uri, null, null, null, null)?.use { cursor ->
                        val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                        val sizeIndex = cursor.getColumnIndex(OpenableColumns.SIZE)
                        cursor.moveToFirst()
                        val name = if (nameIndex != -1) cursor.getString(nameIndex) else uri.lastPathSegment ?: "Unknown Document"
                        val size = if (sizeIndex != -1) cursor.getLong(sizeIndex) else 0L

                        // For SAF URIs, path and dateAdded are not directly available or relevant in the same way as MediaStore
                        // Setting path to null and dateAdded to null as per updated PdfFile model
                        newPdfFiles.add(PdfFile(name = name, uri = uri, size = size, path = null, dateAdded = null))
                    }
                }
            }
            pdfFiles = newPdfFiles
            isLoading = false
        }
    }
}
