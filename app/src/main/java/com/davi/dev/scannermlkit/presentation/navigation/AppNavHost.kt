package com.davi.dev.scannermlkit.presentation.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.davi.dev.scannermlkit.presentation.components.AppBar
import com.davi.dev.scannermlkit.presentation.components.BottomBar
import com.davi.dev.scannermlkit.presentation.screens.home.DocumentPdf
import com.davi.dev.scannermlkit.presentation.screens.scanner.DocumentViewer
import com.davi.dev.scannermlkit.presentation.screens.scanner.ScannerMlkit
import com.davi.dev.scannermlkit.presentation.screens.scannerqrcode.ScannerQrCode
import com.davi.dev.scannermlkit.presentation.screens.viewModel.ScannerQrCodeViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavHost() {
    val backStack = rememberNavBackStack(ListDocument)

    val scannerViewModel = ScannerQrCodeViewModel()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            AppBar()
        },
        bottomBar = {
            BottomBar(backStack)
        },
        floatingActionButton = {
//            if (backStack.lastOrNull() is ViewPDF) {
//                Column(
//                    horizontalAlignment = androidx.compose.ui.Alignment.End,
//                ) {
//                    FloatingActionButton(
//                        onClick = { /* Abrir canvas para assinar */ },
//                        modifier = Modifier.padding(bottom = 8.dp)
//                    ) {
//                        Icon(
//                            imageVector = androidx.compose.material.icons.Icons.Default.Edit,
//                            contentDescription = "Assinar Canvas"
//                        )
//                    }
//                }
//            }
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
                            ScannerMlkit(scannerViewModel.scanner, backStack)
                        }

                        is ScanQrCode -> NavEntry(key) {
                            ScannerQrCode(scannerViewModel)
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