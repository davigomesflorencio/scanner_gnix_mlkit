package com.davi.dev.scannermlkit.presentation.screens.scannerQrCode

import android.Manifest
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.davi.dev.scannermlkit.presentation.components.camera.CameraPreview
import com.davi.dev.scannermlkit.presentation.screens.viewModel.ScannerQrCodeViewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun ScannerQrCode(viewModel: ScannerQrCodeViewModel) {
    val cameraPermissionState = rememberPermissionState(permission = Manifest.permission.CAMERA)

    LaunchedEffect(Unit) {
        if (!cameraPermissionState.status.isGranted) {
            cameraPermissionState.launchPermissionRequest()
        }
    }

    val qrCodeData by viewModel.qrCodeData.collectAsState()

    Box(
        modifier = Modifier.fillMaxSize(),
//        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CameraPreview(viewModel)

        Box(Modifier.align(Alignment.BottomStart)) {
            qrCodeData?.let {
                Text(
                    text = "Scanned Data: $it", fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            } ?: Text("Scan a QR Code")
        }
    }
}