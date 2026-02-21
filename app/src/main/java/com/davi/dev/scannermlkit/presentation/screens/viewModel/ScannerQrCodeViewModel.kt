package com.davi.dev.scannermlkit.presentation.screens.viewModel

import androidx.lifecycle.ViewModel
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.ZoomSuggestionOptions
import com.google.mlkit.vision.barcode.common.Barcode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ScannerQrCodeViewModel: ViewModel() {

    val options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(
            Barcode.FORMAT_QR_CODE,
            Barcode.FORMAT_QR_CODE,
            Barcode.FORMAT_AZTEC)
        .build()

    private val _qrCodeData = MutableStateFlow<String?>("")
    val qrCodeData: StateFlow<String?> = _qrCodeData.asStateFlow()

    fun onQrCodeScanned(data: String) {
        _qrCodeData.value = data
    }

}