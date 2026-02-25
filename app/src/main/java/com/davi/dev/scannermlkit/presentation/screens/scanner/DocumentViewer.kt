package com.davi.dev.scannermlkit.presentation.screens.scanner

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

@OptIn(ExperimentalMaterial3Api::class)
@Composable()
fun DocumentViewer() {
    var documentUri by remember { mutableStateOf<Uri?>(null) }
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) {
        documentUri = it
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if(documentUri == null) {
            CenterAlignedTopAppBar(
                title = { Text("Document Viewer") }
            )
            FilledTonalButton(
                onClick = {
                    launcher.launch(arrayOf("application/pdf"))
                }
            ) {
                Text(text = "Select Document")
            }
        }
        documentUri?.let { uri ->
            // Passamos a URI aqui
            NativePdfViewer(uri)
        } ?: Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("Nenhum documento selecionado", color = Color.Gray)
        }
    }
}
