package com.davi.dev.scannermlkit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.davi.dev.scannermlkit.presentation.navigation.AppNavHost
import com.davi.dev.scannermlkit.presentation.screens.viewModel.MergeDocumentViewModel
import com.davi.dev.scannermlkit.presentation.screens.viewModel.ScannerDocumentViewModel
import com.davi.dev.scannermlkit.presentation.screens.viewModel.ScannerQrCodeViewModel
import com.davi.dev.scannermlkit.presentation.theme.ScannermlkitTheme

class MainActivity : ComponentActivity() {

    val scannerQrCodeViewModel: ScannerQrCodeViewModel by viewModels()
    val scannerDocumentViewModel: ScannerDocumentViewModel by viewModels()
    val mergeDocumentViewModel: MergeDocumentViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            ScannermlkitTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.surface
                ) {
                    AppNavHost(
                        scannerQrCodeViewModel = scannerQrCodeViewModel,
                        scannerDocumentViewModel = scannerDocumentViewModel,
                        mergeDocumentViewModel = mergeDocumentViewModel
                    )
                }
            }
        }
    }
}