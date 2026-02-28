package com.davi.dev.scannermlkit.presentation.navigation

import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialShapes
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.toShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.graphics.shapes.RoundedPolygon
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.davi.dev.scannermlkit.R
import com.davi.dev.scannermlkit.presentation.components.AppBar.AppBar
import com.davi.dev.scannermlkit.presentation.components.bottomBar.BottomBar
import com.davi.dev.scannermlkit.presentation.screens.compressPdf.CompressPdfScreen
import com.davi.dev.scannermlkit.presentation.screens.home.Home
import com.davi.dev.scannermlkit.presentation.screens.mergePdf.MergePdfScreen
import com.davi.dev.scannermlkit.presentation.screens.protectPdf.ProtectPdfScreen
import com.davi.dev.scannermlkit.presentation.screens.scannerDocument.ScannerDocument
import com.davi.dev.scannermlkit.presentation.screens.scannerQrCode.ScannerQrCode
import com.davi.dev.scannermlkit.presentation.screens.selectDocumentPdf.SelectDocumentViewer
import com.davi.dev.scannermlkit.presentation.screens.splitPdf.SplitPdfScreen
import com.davi.dev.scannermlkit.presentation.screens.viewDocumentPdf.ViewDocumentPdf
import com.davi.dev.scannermlkit.presentation.screens.watermarkPdf.WatermarkPdfScreen
import com.davi.dev.scannermlkit.presentation.screens.viewModel.CompressPdfViewModel
import com.davi.dev.scannermlkit.presentation.screens.viewModel.HomeViewModel
import com.davi.dev.scannermlkit.presentation.screens.viewModel.MergeDocumentViewModel
import com.davi.dev.scannermlkit.presentation.screens.viewModel.ProtectPdfViewModel
import com.davi.dev.scannermlkit.presentation.screens.viewModel.ScannerDocumentViewModel
import com.davi.dev.scannermlkit.presentation.screens.viewModel.ScannerQrCodeViewModel
import com.davi.dev.scannermlkit.presentation.screens.viewModel.SplitPdfViewModel
import com.davi.dev.scannermlkit.presentation.screens.viewModel.WatermarkPdfViewModel
import com.google.mlkit.vision.documentscanner.GmsDocumentScannerOptions
import com.google.mlkit.vision.documentscanner.GmsDocumentScanning

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun AppNavHost(
    scannerQrCodeViewModel: ScannerQrCodeViewModel = viewModel(),
    scannerDocumentViewModel: ScannerDocumentViewModel = viewModel(),
    mergeDocumentViewModel: MergeDocumentViewModel = viewModel(),
    splitPdfViewModel: SplitPdfViewModel = viewModel(),
    protectPdfViewModel: ProtectPdfViewModel = viewModel(),
    compressPdfViewModel: CompressPdfViewModel = viewModel(),
    watermarkPdfViewModel: WatermarkPdfViewModel = viewModel(),
    homeViewModel: HomeViewModel = viewModel()
) {
    val backStack = rememberNavBackStack(Routes.Home)
    val activity = LocalActivity.current

    val options = GmsDocumentScannerOptions.Builder()
        .setScannerMode(GmsDocumentScannerOptions.SCANNER_MODE_FULL)
        .setResultFormats(GmsDocumentScannerOptions.RESULT_FORMAT_PDF)
        .build()
    val documentScanner = GmsDocumentScanning.getClient(options)

    val scannerActivityResultLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = {
            scannerDocumentViewModel.handleScanResult(it.resultCode, it.data)
        }
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            AppBar(homeViewModel = homeViewModel)
        },
        bottomBar = {
            if (backStack.last() in listOf(Routes.Home, Routes.ScanDocument, Routes.Account)) {
                BottomBar(backStack)
            }
        },
        floatingActionButton = {
            if (backStack.lastOrNull() is Routes.ScanDocument) {
                FloatingActionButton(
                    onClick = {
                        scannerDocumentViewModel.cleanScan()
                        scannerDocumentViewModel.startScan(
                            scanner = documentScanner,
                            activity = activity!!,
                            scannerLauncher = scannerActivityResultLauncher
                        )
                    },
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                        .clip(RoundedPolygon(MaterialShapes.Arch).toShape(0))
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_scan_pdf),
                        contentDescription = "Scanner PDF"
                    )
                }
            } else if (backStack.lastOrNull() is Routes.Home) {
                FloatingActionButton(
                    onClick = {
                        backStack.add(Routes.SelectViewDocument)
                    },
                    modifier = Modifier
                        .padding(bottom = 8.dp)
                ) {
                    Icon(
                        painter = painterResource(R.drawable.ic_file_search),
                        contentDescription = "File Scan PDF"
                    )
                }
            }
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
                transitionSpec = {
                    val isForward = targetState.entries.size > initialState.entries.size

                    if (isForward) {
                        slideInHorizontally { it } + fadeIn() togetherWith
                                slideOutHorizontally { -it } + fadeOut()
                    } else {
                        slideInHorizontally { -it } + fadeIn() togetherWith
                                slideOutHorizontally { it } + fadeOut()
                    }
                },
                popTransitionSpec = {
                    val isPop = targetState.entries.size > initialState.entries.size

                    if (isPop) {
                        slideInHorizontally { -it } + fadeIn() togetherWith
                                slideOutHorizontally { it } + fadeOut()
                    } else {
                        slideInHorizontally { it } + fadeIn() togetherWith
                                slideOutHorizontally { -it } + fadeOut()
                    }
                },
                entryProvider = { key ->
                    when (key) {
                        is Routes.Home -> NavEntry(key) {
                            Home(backStack, homeViewModel)
                        }

                        is Routes.ScanDocument -> NavEntry(key) {
                            ScannerDocument(viewModel = scannerDocumentViewModel)
                        }

                        is Routes.ScanQrCode -> NavEntry(key) {
                            ScannerQrCode(viewModel = scannerQrCodeViewModel)
                        }

                        is Routes.SelectViewDocument -> NavEntry(key) {
                            SelectDocumentViewer(scannerDocumentViewModel)
                        }

                        is Routes.ViewDocument -> NavEntry(key) {
                            ViewDocumentPdf(key.filePath)
                        }

                        is Routes.MergePdf -> NavEntry(key) {
                            MergePdfScreen(mergeDocumentViewModel)
                        }

                        is Routes.SplitPdf -> NavEntry(key) {
                            SplitPdfScreen(splitPdfViewModel)
                        }

                        is Routes.ProtectPdf -> NavEntry(key) {
                            ProtectPdfScreen(protectPdfViewModel)
                        }

                        is Routes.CompressPdf -> NavEntry(key) {
                            CompressPdfScreen(compressPdfViewModel = compressPdfViewModel)
                        }

                        is Routes.WatermarkPdf -> NavEntry(key) {
                            WatermarkPdfScreen(watermarkPdfViewModel = watermarkPdfViewModel)
                        }

                        is Routes.Account -> NavEntry(key) {
                            Column(
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.fillMaxSize()
                            ) {
                                Text("Account")
                            }
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
