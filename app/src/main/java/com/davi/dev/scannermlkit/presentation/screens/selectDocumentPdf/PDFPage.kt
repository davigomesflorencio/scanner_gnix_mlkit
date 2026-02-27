package com.davi.dev.scannermlkit.presentation.screens.selectDocumentPdf

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.core.graphics.createBitmap
import com.davi.dev.scannermlkit.domain.model.SignatureData
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@Composable
fun PDFPage(
    renderer: PdfRenderer,
    pageIndex: Int,
    signatures: List<SignatureData>,
    onPageScrolled: (Int) -> Unit, // Callback to notify when page changes due to scroll
    parentMaxWidthPx: Dp, // Max width constraint from parent
    parentMaxHeightPx: Dp // Max height constraint from parent
) {
    var bitmap by remember { mutableStateOf<Bitmap?>(null) }
    val pageBitmapWidthPx = remember { mutableStateOf(0f) } // Store actual width in pixels
    val pageBitmapHeightPx = remember { mutableStateOf(0f) } // Store actual height in pixels
    val density = LocalDensity.current

    LaunchedEffect(pageIndex) {
        withContext(Dispatchers.IO) {
            val page = renderer.openPage(pageIndex)
            // Renderização em alta resolução (3x)
            val scale = 3f
            val bitmapT = createBitmap((page.width * scale).toInt(), (page.height * scale).toInt())
            val matrix = android.graphics.Matrix().apply { postScale(scale, scale) }

            page.render(bitmapT, null, matrix, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)

            bitmap = bitmapT
            pageBitmapWidthPx.value = page.width.toFloat()
            pageBitmapHeightPx.value = page.height.toFloat()
            page.close()
        }
    }

    Box(
        modifier = Modifier
            .background(Color.White)
            .fillMaxWidth()
            .heightIn(min = 300.dp)
            .onGloballyPositioned { coordinates ->
                // Simple check: if the top of the page is near the top of the screen, consider it the current page.
                // This might need refinement for better accuracy with scroll detection.
                val positionY = coordinates.positionInRoot().y
                val screenHeight = coordinates.size.height
                if (positionY < screenHeight * 0.5f && positionY > -coordinates.size.height * 0.5f) {
                    onPageScrolled(pageIndex)
                }
            },
        contentAlignment = Alignment.Center
    ) {
        bitmap?.let {
            val pageW = pageBitmapWidthPx.value // Original PDF page width in pixels
            val pageH = pageBitmapHeightPx.value // Original PDF page height in pixels

            // Calculate the displayed width and height of the PDF image in Dp
            val displayedImageWidthDp = parentMaxWidthPx
            val displayedImageHeightDp = with(density) {
                // Calculate displayed height based on aspect ratio and displayedImageWidthDp
                (parentMaxWidthPx.toPx() * (pageH / pageW)).toDp()
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(displayedImageHeightDp) // Explicitly set height
            ) { // Use fillMaxWidth to respect page aspect ratio
                androidx.compose.foundation.Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = "Página ${pageIndex + 1}",
                    modifier = Modifier.fillMaxWidth(),
                    contentScale = ContentScale.FillWidth
                )

                // Draw signatures on this page
                signatures.forEach { sd ->
                    DraggableAssignature(
                        signatureData = sd,
                        parentWidth = displayedImageWidthDp,
                        parentHeight = displayedImageHeightDp
                    )
                }
            }
        } ?: CircularProgressIndicator() // Placeholder while loading
    }
}
