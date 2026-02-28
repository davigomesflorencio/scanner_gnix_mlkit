package com.davi.dev.scannermlkit.domain.qrcode

import android.util.Log
import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import com.davi.dev.scannermlkit.presentation.screens.viewModel.ScannerQrCodeViewModel
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.common.InputImage

class BarcodeAnalizer(private val viewModel: ScannerQrCodeViewModel) : ImageAnalysis.Analyzer {
    private val scanner = BarcodeScanning.getClient()

    @OptIn(ExperimentalGetImage::class)
    override fun analyze(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image ?: return
        val rotationDegrees = imageProxy.imageInfo.rotationDegrees
        val image = InputImage.fromMediaImage(mediaImage, rotationDegrees)

        // Dimensões da imagem após a rotação
        val imageWidth = if (rotationDegrees == 90 || rotationDegrees == 270) imageProxy.height else imageProxy.width
        val imageHeight = if (rotationDegrees == 90 || rotationDegrees == 270) imageProxy.width else imageProxy.height

        scanner.process(image)
            .addOnSuccessListener { barcodes ->
                if (barcodes.isEmpty()) {
                    viewModel.clearDetection()
                } else {
                    barcodes.forEach { barcode ->
                        barcode.rawValue?.let { 
                            viewModel.onQrCodeScanned(it, barcode.boundingBox, imageWidth, imageHeight) 
                        }
                    }
                }
            }
            .addOnFailureListener { Log.e("QRScanner", "Error scanning QR code: $it") }
            .addOnCompleteListener { imageProxy.close() }
    }
}