package com.davi.dev.scannermlkit.presentation.screens.viewModel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.davi.dev.scannermlkit.domain.pdf.PdfManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class MergeDocumentViewModel(application: Application) : AndroidViewModel(application) {

    private val _pdfFilesUri = MutableStateFlow<List<Uri>>(emptyList())
    val pdfFilesUri: StateFlow<List<Uri>> = _pdfFilesUri

    private val _mergeStatus = MutableSharedFlow<String>()
    val mergeStatus: SharedFlow<String> = _mergeStatus

    fun addFileUri(uri: Uri) {
        _pdfFilesUri.value += uri
    }

    fun removeFileUri(uri: Uri) {
        _pdfFilesUri.value -= uri
    }

    fun mergePdf(fileName: String) {
        val uris = _pdfFilesUri.value
        if (uris.isEmpty()) return

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val context = getApplication<Application>().applicationContext
                val outputDir = context.filesDir
                val outputFile = File(outputDir, "$fileName.pdf")

                val inputStreams = uris.mapNotNull { uri ->
                    context.contentResolver.openInputStream(uri)
                }

                if (inputStreams.isNotEmpty()) {
                    FileOutputStream(outputFile).use { outputStream ->
                        PdfManager.mergePdfs(inputStreams, outputStream)
                    }
                    _mergeStatus.emit("PDF created in: ${outputFile.absolutePath}")
                } else {
                    _mergeStatus.emit("Error: No file could be opened.")
                }
                cleanPdfs()
            } catch (e: Exception) {
                _mergeStatus.emit("Error merging PDFs: ${e.message}")
            }
        }
    }

    fun cleanPdfs() {
        _pdfFilesUri.value = emptyList()
    }
}