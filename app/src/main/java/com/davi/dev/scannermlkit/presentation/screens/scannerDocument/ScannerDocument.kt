package com.davi.dev.scannermlkit.presentation.screens.scannerDocument

import android.content.Intent
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.davi.dev.scannermlkit.R
import com.davi.dev.scannermlkit.presentation.components.CustomDivider
import com.davi.dev.scannermlkit.presentation.screens.selectDocumentPdf.PDFPage
import com.davi.dev.scannermlkit.presentation.screens.viewModel.ScannerDocumentViewModel

@Composable
fun ScannerDocument(
    viewModel: ScannerDocumentViewModel
) {
    val context = LocalContext.current
    val pdfFile by viewModel.pdfFile.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    val rendererResource = remember(pdfFile) {
        if (pdfFile == null) return@remember null
        ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY)?.let { pfd ->
            PdfRenderer(pfd)
        }
    }

    DisposableEffect(rendererResource) {
        onDispose {
            rendererResource?.close()
        }
    }

    LaunchedEffect(errorMessage) {
        errorMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            viewModel.clearErrorMessage()
        }
    }

    if (pdfFile == null) {
        NotInityScan()
    } else
        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxSize()
        ) {
            item {
                Text(
                    "Scan finalized!",
                    style = MaterialTheme.typography.titleLarge
                )
            }
            item {
                CustomDivider()
            }
            if (rendererResource != null) {
                items(rendererResource.pageCount) { pageIdx ->
                    PDFPage(
                        renderer = rendererResource,
                        pageIndex = pageIdx,
                        signatures = emptyList(),
                        onPageScrolled = { }, // Update current page when scrolled
                        parentMaxWidthPx = 300.dp, // Pass max width constraint
                        parentMaxHeightPx = 600.dp, // Pass max height constraint
                        onDeleteSignature = { }
                    )
                }
            }
            item {
                CustomDivider()
            }
            item {
                Row {
                    Text(
                        "Name: ",
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        pdfFile!!.name,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            item {
                CustomDivider()
            }
            item {
                pdfFile?.let { file ->
                    Button(
                        onClick = {
                            val authority = "${context.packageName}.provider"
                            val pdfUri = FileProvider.getUriForFile(context, authority, file)
                            val shareIntent = Intent().apply {
                                action = Intent.ACTION_SEND
                                putExtra(Intent.EXTRA_STREAM, pdfUri)
                                type = "application/pdf"
                                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Export PDF"))
                        },
                        modifier = Modifier.padding(top = 4.dp)
                    ) {
                        Text(text = "Export PDF")
                    }
                }
                Spacer(Modifier.height(80.dp))
            }
        }
}

@Composable
fun NotInityScan() {
    Box(Modifier.fillMaxSize()) {
        Box(
            Modifier
                .width(200.dp)
                .height(400.dp)
                .align(Alignment.TopCenter)
        ) {
            Image(
                painterResource(R.drawable.screenshoot2),
                contentScale = ContentScale.FillWidth,
                contentDescription = "Preview app",
            )
        }

        Text(
            "Scan All yout documents quickly and easily",
            style = MaterialTheme.typography.titleMedium,
            fontSize = 40.sp,
            lineHeight = 40.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 70.dp)
        )
    }
}
