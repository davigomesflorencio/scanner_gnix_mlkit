package com.davi.dev.scannermlkit.presentation.screens.viewDocumentPdf

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import java.io.File

@Composable
fun ViewDocumentPdf(filePath: String) {
    val context = LocalContext.current
    val file = File(filePath)
    if (file.exists()) {
        val uri: Uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.provider", // Deve ser igual ao 'authorities' no Manifest
            file
        )
        ViewPage(uri)
    } else {
        Column(
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            Text("Arquivo não encontrado")
        }
    }
}