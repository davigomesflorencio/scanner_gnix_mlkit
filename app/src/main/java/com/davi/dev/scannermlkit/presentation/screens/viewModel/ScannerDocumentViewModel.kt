package com.davi.dev.scannermlkit.presentation.screens.viewModel

import android.app.Activity
import android.app.Application
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.IntentSenderRequest
import androidx.lifecycle.AndroidViewModel
import com.google.mlkit.vision.documentscanner.GmsDocumentScanner
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.io.File
import java.io.FileOutputStream

class ScannerDocumentViewModel(application: Application) : AndroidViewModel(application) {

    private val context: Context = application.applicationContext
    private val contentResolver = context.contentResolver
    private val filesDir = context.filesDir

    private val _imageUris = MutableStateFlow<List<Uri>>(emptyList())
    val imageUris: StateFlow<List<Uri>> = _imageUris

    private val _pdfFile = MutableStateFlow<File?>(null)
    val pdfFile: StateFlow<File?> = _pdfFile

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _documentUri = MutableStateFlow<Uri?>(null)
    var documentUri: StateFlow<Uri?> = _documentUri

    fun setUri(uri: Uri) {
        _documentUri.value = uri
    }

    fun cleanScan() {
        _pdfFile.value = null
    }

    fun handleScanResult(resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            val result = GmsDocumentScanningResult.fromActivityResultIntent(data)
            _imageUris.value = result?.pages?.map { xi -> xi.imageUri } ?: emptyList()

            Log.d("xing", "size -> " + _imageUris.value.size)

            result?.pdf?.let { pdf ->
                val file = File(filesDir, "scan_${System.currentTimeMillis()}.pdf")
                val fos = FileOutputStream(file)
                contentResolver.openInputStream(pdf.uri)?.use { inputStream ->
                    inputStream.copyTo(fos)
                }
                _pdfFile.value = file
            }
        } else {
            Log.d("xing", "Result not ok")
        }
    }

    fun startScan(scanner: GmsDocumentScanner, activity: Activity, scannerLauncher: ActivityResultLauncher<IntentSenderRequest>) {
        scanner.getStartScanIntent(activity)
            .addOnFailureListener { ix ->
                _errorMessage.value = ix.message
            }
            .addOnSuccessListener { intent ->
                scannerLauncher.launch(IntentSenderRequest.Builder(intent).build())
            }
    }

    fun clearErrorMessage() {
        _errorMessage.value = null
    }
}