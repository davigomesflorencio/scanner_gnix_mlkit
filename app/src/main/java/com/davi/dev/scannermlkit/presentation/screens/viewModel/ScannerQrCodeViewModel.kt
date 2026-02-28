package com.davi.dev.scannermlkit.presentation.screens.viewModel

import android.app.Application
import android.graphics.Rect
import androidx.lifecycle.AndroidViewModel
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.common.Barcode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class ScannerQrCodeViewModel(application: Application) : AndroidViewModel(application) {

    val optionsScannerQrCode = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(
            Barcode.FORMAT_QR_CODE,
            Barcode.FORMAT_AZTEC
        )
        .build()

    private val _qrCodeData = MutableStateFlow<String?>("")
    val qrCodeData: StateFlow<String?> = _qrCodeData.asStateFlow()

    private val _barcodeBoundingBox = MutableStateFlow<Rect?>(null)
    val barcodeBoundingBox: StateFlow<Rect?> = _barcodeBoundingBox.asStateFlow()

    private val _imageDimensions = MutableStateFlow<Pair<Int, Int>?>(null)
    val imageDimensions: StateFlow<Pair<Int, Int>?> = _imageDimensions.asStateFlow()

    fun onQrCodeScanned(data: String, boundingBox: Rect?, imageWidth: Int, imageHeight: Int) {
        _qrCodeData.value = data
        _barcodeBoundingBox.value = boundingBox
        _imageDimensions.value = Pair(imageWidth, imageHeight)
    }

    fun clearDetection() {
        _barcodeBoundingBox.value = null
    }
}