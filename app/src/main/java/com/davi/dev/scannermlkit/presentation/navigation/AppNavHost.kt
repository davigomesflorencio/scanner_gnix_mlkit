package com.davi.dev.scannermlkit.presentation.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.davi.dev.scannermlkit.presentation.components.AppBar
import com.davi.dev.scannermlkit.presentation.components.BottomBar
import com.davi.dev.scannermlkit.presentation.screens.home.DocumentPdf
import com.davi.dev.scannermlkit.presentation.screens.scanner.DocumentViewer
import com.davi.dev.scannermlkit.presentation.screens.scanner.ScannerMlkit
import com.davi.dev.scannermlkit.presentation.screens.scannerqrcode.ScannerQrCode
import com.davi.dev.scannermlkit.presentation.screens.viewModel.ScannerQrCodeViewModel
import com.google.mlkit.vision.documentscanner.GmsDocumentScanner

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavHost(scanner: GmsDocumentScanner) {
    val backStack = remember { mutableStateListOf<Any>(ListDocument) }
    val startDestination = Destination.DOCUMENTS
    val selectedDestination = rememberSaveable { mutableIntStateOf(startDestination.ordinal) }

    val qrcodeViewModel = ScannerQrCodeViewModel()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            AppBar()
        },
        bottomBar = {
            BottomBar(backStack, selectedDestination)
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxSize()
        ) {
            NavDisplay(
                backStack = backStack,
                onBack = { backStack.removeLastOrNull() },
                entryProvider = { key ->
                    when (key) {
                        is ListDocument -> NavEntry(key) {
                            DocumentPdf(backStack)
                        }

                        is ScanPdf -> NavEntry(key) {
                            ScannerMlkit(scanner, backStack)
                        }

                        is ScanQrCode -> NavEntry(key) {
                            ScannerQrCode(qrcodeViewModel)
                        }

                        is ViewPDF -> NavEntry(key) {
                            DocumentViewer()
                        }

                        else -> {
                            error("Unknown route: $key")
                        }
                    }
                }
            )
        }
    }
}