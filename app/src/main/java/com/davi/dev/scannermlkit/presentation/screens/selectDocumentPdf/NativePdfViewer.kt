package com.davi.dev.scannermlkit.presentation.screens.documentPdf

import android.graphics.pdf.PdfRenderer
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.davi.dev.scannermlkit.domain.model.SignatureData
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NativePdfViewer(uri: Uri) {
    val context = LocalContext.current
    val density = LocalDensity.current

    val rendererResource = remember(uri) {
        val pfd = context.contentResolver.openFileDescriptor(uri, "r")
        pfd?.let { PdfRenderer(it) }
    }

    DisposableEffect(rendererResource) {
        onDispose {
            rendererResource?.close()
        }
    }

    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()
    var showBottomSheet by remember { mutableStateOf(false) }
    var currentPageIndex by remember { mutableStateOf(0) } // Track the currently viewed page

    // Store signatures per page
    val signaturesOnPage = remember { mutableStateMapOf<Int, MutableList<SignatureData>>() }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                showBottomSheet = true
            }) {
                Icon(imageVector = Icons.Default.Draw, contentDescription = "Sign PDF")
            }
        }
    ) { padding ->
        BoxWithConstraints(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            val maxWidthPx = with(density) { maxWidth }
            val maxHeightPx = with(density) { maxHeight }

            if (rendererResource != null) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    items(rendererResource.pageCount) { pageIdx ->
                        val pageSignatures = signaturesOnPage.getOrDefault(pageIdx, emptyList())
                        PDFPage(
                            renderer = rendererResource,
                            pageIndex = pageIdx,
                            signatures = pageSignatures,
                            onPageScrolled = { currentPageIndex = it }, // Update current page when scrolled
                            parentMaxWidthPx = maxWidthPx, // Pass max width constraint
                            parentMaxHeightPx = maxHeightPx // Pass max height constraint
                        )
                    }
                }
            } else {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            if (showBottomSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showBottomSheet = false },
                    sheetState = sheetState,
                ) {
                    Box(modifier = Modifier.height(300.dp)) {
                        SignaturePad(
                            currentPageIndex = currentPageIndex, // Pass the current page index
                            onDone = { signatureData ->
                                val pageIndex = currentPageIndex
                                val pageSignatures = signaturesOnPage.getOrPut(pageIndex) { mutableListOf() }
                                pageSignatures.add(signatureData)
                                signaturesOnPage[pageIndex] = pageSignatures

                                scope.launch { sheetState.hide() }.invokeOnCompletion { showBottomSheet = false }
                            },
                            onCancel = {
                                scope.launch { sheetState.hide() }.invokeOnCompletion { showBottomSheet = false }
                            }
                        )
                    }
                }
            }
        }
    }
}
