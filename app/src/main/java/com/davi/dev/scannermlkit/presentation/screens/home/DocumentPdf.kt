package com.davi.dev.scannermlkit.presentation.screens.home

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.pdf.PdfRenderer
import android.os.ParcelFileDescriptor
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.davi.dev.scannermlkit.R
import java.io.File

@Composable
fun DocumentPdf(backStack: SnapshotStateList<Any>) {
    val context = LocalContext.current
    val filesDir = context.filesDir
    val pdfFiles = remember {
        filesDir.listFiles { file -> file.name.endsWith(".pdf") }?.toList() ?: emptyList()
    }

    LazyColumn(modifier = Modifier.padding(16.dp)) {
        items(pdfFiles) { file ->
            PdfListItem(file = file)
        }
    }
}

@Composable
fun PdfListItem(file: File) {
    val context = LocalContext.current
    val authority = "${context.packageName}.provider"
    val pdfUri = FileProvider.getUriForFile(context, authority, file)


    Card(
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 6.dp)) {
            PdfPreview(
                file = file,
                modifier = Modifier.width(64.dp)
                    .weight(1.5f)
            )

            Text(
                text = file.name,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(horizontal = 6.dp).weight(3f)
            )
            Row(horizontalArrangement = Arrangement.spacedBy(1.dp),

                modifier = Modifier.weight(1.1f)) {
                // Botão para visualizar o PDF
                IconButton(onClick = {
                    val viewIntent = Intent().apply {
                        action = Intent.ACTION_VIEW
                        setDataAndType(pdfUri, "application/pdf")
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    context.startActivity(Intent.createChooser(viewIntent, "View PDF"))
                }) {
                    Icon(
                        painterResource(R.drawable.ic_share),
                        contentDescription = "Share doc"
                    )
                     }

                // Botão para compartilhar/download do PDF
                IconButton(onClick = {
                    val shareIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_STREAM, pdfUri)
                        type = "application/pdf"
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    context.startActivity(Intent.createChooser(shareIntent, "Export PDF"))
                }) {
                    Icon(
                        painterResource(R.drawable.ic_ellipsis_vertical),
                        contentDescription = "Share doc"
                    )
                }

            }
        }
    }
}


@Composable
fun PdfPreview(file: File, modifier: Modifier = Modifier) {
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

    Box(modifier = modifier) {
        if (bitmap != null) {
            Image(
                bitmap = bitmap.asImageBitmap(),
                contentDescription = "PDF preview of ${file.name}",
                modifier = Modifier.aspectRatio(1f / 1.41f) // A4 paper aspect ratio
            )
        } else {
            // Placeholder in case of an error
            Box(
                modifier = Modifier
                    .aspectRatio(1f / 1.41f)
                    .fillMaxWidth()
            )
        }
    }
}