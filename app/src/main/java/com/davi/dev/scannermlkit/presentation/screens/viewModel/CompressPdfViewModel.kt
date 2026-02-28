package com.davi.dev.scannermlkit.presentation.screens.viewModel

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.davi.dev.scannermlkit.domain.enums.CompressionLevel
import com.davi.dev.scannermlkit.domain.pdf.PdfManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.File
import java.io.FileOutputStream

class CompressPdfViewModel(application: Application) : AndroidViewModel(application) {
    private val _selectedPdfUri = MutableStateFlow<Uri?>(null)
    val selectedPdfUri: StateFlow<Uri?> = _selectedPdfUri

    private val _status = MutableSharedFlow<String>()
    val status: SharedFlow<String> = _status

    private val _compressionLevel = MutableStateFlow(CompressionLevel.MEDIUM)
    val compressionLevel: StateFlow<CompressionLevel> = _compressionLevel

    fun selectPdf(uri: Uri) {
        _selectedPdfUri.value = uri
    }

    fun setCompressionLevel(level: CompressionLevel) {
        _compressionLevel.value = level
    }

    fun compressPdf(fileName: String) {
        val uri = _selectedPdfUri.value ?: return

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val context = getApplication<Application>().applicationContext
                val outputDir = context.filesDir
                val outputFileName = if (fileName.endsWith(".pdf")) fileName else "$fileName.pdf"
                val outputFile = File(outputDir, "compressed_$outputFileName")

                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    FileOutputStream(outputFile).use { outputStream ->
                        PdfManager.compressPdf(inputStream, outputStream, _compressionLevel.value.level)
                    }
                    _status.emit("PDF comprimido com sucesso: ${outputFile.name}")
                    _selectedPdfUri.value = null
                } ?: run {
                    _status.emit("Erro: Não foi possível abrir o arquivo.")
                }
            } catch (e: Exception) {
                _status.emit("Erro ao comprimir o PDF: ${e.message}")
            }
        }
    }
}
