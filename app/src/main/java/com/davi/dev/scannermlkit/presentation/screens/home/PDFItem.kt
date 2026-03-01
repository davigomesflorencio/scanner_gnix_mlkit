package com.davi.dev.scannermlkit.presentation.screens.home

import android.content.Intent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.davi.dev.scannermlkit.R
import com.davi.dev.scannermlkit.domain.date.DateUtil.formatDate
import com.davi.dev.scannermlkit.presentation.navigation.Routes
import java.io.File


@Composable
fun PDFItem(navBackStack: NavBackStack<NavKey>, file: File) {
    val context = LocalContext.current
    val authority = "${context.packageName}.provider"
    val pdfUri = FileProvider.getUriForFile(context, authority, file)
    val formattedDate = remember(file) { formatDate(file.lastModified()) }

    Card(
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 3.dp),
        shape = RoundedCornerShape(30.dp),
        modifier = Modifier
            .clickable {
//                val viewIntent = Intent().apply {
//                    action = Intent.ACTION_VIEW
//                    setDataAndType(pdfUri, "application/pdf")
//                    addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
//                }
//                context.startActivity(Intent.createChooser(viewIntent, "View PDF"))

                navBackStack.add(Routes.ViewDocument(file.path))
            }
            .fillMaxWidth()
            .padding(4.dp),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(8.dp)
        ) {
            ImagePdfPreview(
                file = file,
                modifier = Modifier
                    .size(75.dp)
            )

            Column(
                modifier = Modifier
                    .padding(horizontal = 6.dp)
                    .fillMaxWidth(.7f)
            ) {
                Text(
                    text = file.name,
                    style = MaterialTheme.typography.titleMedium,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = formattedDate,
                    style = MaterialTheme.typography.labelMedium,
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(1.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Botão para visualizar o PDF
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
                        painterResource(R.drawable.ic_share),
                        contentDescription = "Share doc"
                    )
                }

                // Botão para compartilhar/download do PDF
                IconButton(onClick = {
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