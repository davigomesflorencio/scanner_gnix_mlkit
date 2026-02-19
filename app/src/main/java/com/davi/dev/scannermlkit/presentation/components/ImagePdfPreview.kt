package com.davi.dev.scannermlkit.presentation.components

import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import java.io.File

@Composable
fun ImagePdfPreview(file: File, modifier: Modifier = Modifier) {
    val bitmap = remember(file) {
        try {
            val fileDescriptor =
                ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
            val pdfRenderer = PdfRenderer(fileDescriptor)
            val page = pdfRenderer.openPage(0)
            val bitmap = Bitmap.createBitmap(page.width, page.height, Bitmap.Config.ARGB_8888)
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY)
            page.close()
            pdfRenderer.close()
            fileDescriptor.close()
            bitmap
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    Box(modifier = modifier.clip(RoundedCornerShape(10.dp))) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "PDF preview of ${file.name}",
                modifier = Modifier.aspectRatio(1f)
            )
        } else {
            // Placeholder in case of an error
            Box(
                modifier = Modifier
                    .aspectRatio(1f)
                    .fillMaxWidth()
            )
        }
    }
}