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

class ProtectPdfViewModel(application: Application) : AndroidViewModel(application) {
    private val _selectedPdfUri = MutableStateFlow<Uri?>(null)
    val selectedPdfUri: StateFlow<Uri?> = _selectedPdfUri

    private val _splitStatus = MutableSharedFlow<String>()
    val splitStatus: SharedFlow<String> = _splitStatus

    fun selectPdf(uri: Uri) {
        _selectedPdfUri.value = uri
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val context = getApplication<Application>().applicationContext
                context.contentResolver.openInputStream(uri)?.use { _ ->
                }
            } catch (e: Exception) {
                _splitStatus.emit("Erro ao ler PDF: ${e.message}")
            }
        }
    }

    fun protectPdf(fileName: String, password: String, confirmPassword: String) {
        val uri = _selectedPdfUri.value ?: return

        viewModelScope.launch(Dispatchers.IO) {
            try {
                val context = getApplication<Application>().applicationContext
                val outputDir = context.filesDir
                val outputFile = File(outputDir, if (fileName.endsWith(".pdf")) fileName else "$fileName.pdf")

                context.contentResolver.openInputStream(uri)?.use { inputStream ->
                    FileOutputStream(outputFile).use { outputStream ->
                        val success = PdfManager.protectPdf(inputStream, outputStream, password, confirmPassword)
                        if (success) {
                            _splitStatus.emit("PDF protegido com sucesso: ${outputFile.name}")
                            _selectedPdfUri.value = null
                        } else {
                            _splitStatus.emit("Erro ao proteger o PDF.")
                        }
                    }
                } ?: run {
                    _splitStatus.emit("Erro: Não foi possível abrir o arquivo.")
                }
            } catch (e: Exception) {
                _splitStatus.emit("Erro ao proteger o PDF: ${e.message}")
            }
        }
    }
}
