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

class SplitPdfViewModel(application: Application) : AndroidViewModel(application) {

    private val _selectedPdfUri = MutableStateFlow<Uri?>(null)
    val selectedPdfUri: StateFlow<Uri?> = _selectedPdfUri

    private val _pageCount = MutableStateFlow(0)
    val pageCount: StateFlow<Int> = _pageCount

    private val _splitStatus = MutableSharedFlow<String>()
    val splitStatus: SharedFlow<String> = _splitStatus

    fun selectPdf(uri: Uri) {
        _selectedPdfUri.value = uri
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val context = getApplication<Application>().applicationContext
                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    _pageCount.value = PdfManager.getPageCount(inputStream)
                }
            } catch (e: Exception) {
                _pageCount.value = 0
                _splitStatus.emit("Error reading PDF: ${e.message}")
            }
        }
    }

    fun splitPdf(fileName: String, startPage: Int, endPage: Int) {
        val uri = _selectedPdfUri.value ?: return

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val context = getApplication<Application>().applicationContext
                val outputDir = context.filesDir
                val outputFile = File(outputDir, if (fileName.endsWith(".pdf")) fileName else "$fileName.pdf")

                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    FileOutputStream(outputFile).use { outputStream ->
                        PdfManager.splitPdf(inputStream, outputStream, startPage, endPage)
                    }
                    _splitStatus.emit("PDF split successfully: ${outputFile.name}")
                } ?: run {
                    _splitStatus.emit("Error: Could not open the file.")
                }
            } catch (e: Exception) {
                _splitStatus.emit("Error splitting PDF: ${e.message}")
            }
        }
    }
}