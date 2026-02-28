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

class WatermarkPdfViewModel(application: Application) : AndroidViewModel(application) {

    private val _pdfFileUri = MutableStateFlow<Uri?>(null)
    val pdfFileUri: StateFlow<Uri?> = _pdfFileUri

    private val _watermarkStatus = MutableSharedFlow<String>()
    val watermarkStatus: SharedFlow<String> = _watermarkStatus

    fun setFileUri(uri: Uri) {
        _pdfFileUri.value = uri
    }

    fun addWatermark(text: String, fileName: String) {
        val uri = _pdfFileUri.value ?: return

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val context = getApplication<Application>().applicationContext
                val outputDir = context.filesDir
                val outputFile = File(outputDir, "${fileName}.pdf")

                val inputStream = context.contentResolver.openInputStream(uri)

                if (inputStream != null) {
                    FileOutputStream(outputFile).use { outputStream ->
                        PdfManager.addWatermark(inputStream, outputStream, text)
                    }
                    _watermarkStatus.emit("Watermark added: ${outputFile.absolutePath}")
                    _pdfFileUri.value = null
                } else {
                    _watermarkStatus.emit("Error: Could not open file.")
                }
            } catch (e: Exception) {
                _watermarkStatus.emit("Error: ${e.message}")
            }
        }
    }
}
