package com.davi.dev.scannermlkit.presentation.components

import android.net.Uri
import android.provider.OpenableColumns
import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.davi.dev.scannermlkit.domain.date.DateUtil.formatDate
import com.davi.dev.scannermlkit.presentation.screens.home.ImagePdfPreview
import java.io.File
import java.io.FileOutputStream

@Composable
fun PDFSimpleItem(uri: Uri) {
    val context = LocalContext.current
    val contentResolver = context.contentResolver

    val file: File? = remember(uri) {
        when (uri.scheme) {
            "file" -> uri.path?.let(::File)
            "content" -> {
                try {
                    val fileNameFromUri = contentResolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
                        if (cursor.moveToFirst()) {
                            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                            if (nameIndex != -1) cursor.getString(nameIndex) else null
                        } else null
                    } ?: uri.lastPathSegment

                    if (fileNameFromUri != null) {
                        val tempFile = File(context.cacheDir, "temp_pdf_${System.currentTimeMillis()}_$fileNameFromUri")
                        contentResolver.openInputStream(uri)?.use { inputStream ->
                            FileOutputStream(tempFile).use { outputStream ->
                                inputStream.copyTo(outputStream)
                            }
                        }
                        tempFile
                    } else null
                } catch (e: Exception) {
                    Log.e("PDFItemMerged", "Erro ao processar content Uri para File: $uri", e)
                    null
                }
            }

            else -> null
        }
    }

    val fileName = remember(file, uri) {
        file?.name ?: uri.lastPathSegment ?: "unknown_pdf"
    }

    val formattedDate = remember(file) {
        file?.lastModified()?.let { formatDate(it) } ?: "Data desconhecida"
    }


    Card(
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(4.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 6.dp)
        ) {
            file?.let {
                ImagePdfPreview(
                    file = it,
                    modifier = Modifier
                        .size(64.dp)
                )
            } ?: run {
                // Placeholder ou mensagem de erro se o arquivo não puder ser derivado
                Box(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(CircleShape)

                ) {

                }
            }

            Column(
                modifier = Modifier
                    .padding(horizontal = 3.dp)
                    .fillMaxWidth()
                    .padding(8.dp)
            ) {
                Text(
                    text = fileName,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = 2,
                    softWrap = true,
                    overflow = TextOverflow.Clip
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.labelMedium,
                )
            }
        }
    }
}