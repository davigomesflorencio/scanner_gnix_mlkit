package com.davi.dev.scannermlkit.presentation.screens.viewDocumentPdf

import android.graphics.pdf.PdfRenderer
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.Text
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import com.davi.dev.scannermlkit.presentation.screens.selectDocumentPdf.PDFPage

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewPage(uri: Uri) {
    val context = LocalContext.current
    val density = LocalDensity.current
    var currentPageIndex by remember { mutableStateOf(0) } // Track the currently viewed page

    val rendererResource = remember(uri) {
        val pfd = context.contentResolver.openFileDescriptor(uri, "r")
        pfd?.let { PdfRenderer(it) }
    }

    DisposableEffect(rendererResource) {
        onDispose {
            rendererResource?.close()
        }
    }

    BoxWithConstraints(
        modifier = Modifier
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
                item {
                    Text("items -> ${rendererResource.pageCount}")
                }
                items(rendererResource.pageCount) { pageIdx ->
                    PDFPage(
                        renderer = rendererResource,
                        pageIndex = pageIdx,
                        signatures = emptyList(),
                        onPageScrolled = { currentPageIndex = it }, // Update current page when scrolled
                        parentMaxWidthPx = maxWidthPx, // Pass max width constraint
                        parentMaxHeightPx = maxHeightPx // Pass max height constraint
                    )
                }
            }
            Box(
                modifier = Modifier.align(Alignment.BottomStart),
                contentAlignment = Alignment.Center
            ) {
                Card(modifier = Modifier.padding(10.dp)) {
                    Text(
                        "${currentPageIndex + 1} / ${rendererResource.pageCount}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }
        } else {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }
}
