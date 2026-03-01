package com.davi.dev.scannermlkit.presentation.screens.selectDocumentPdf

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.davi.dev.scannermlkit.presentation.screens.viewModel.ScannerDocumentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable()
fun SelectDocumentViewer(scannerDocumentViewModel: ScannerDocumentViewModel) {
    val documentUri by scannerDocumentViewModel.documentUri.collectAsState()

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) {
        it?.let { uri -> scannerDocumentViewModel.setUri(uri) }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        if (documentUri == null) {
            FilledTonalButton(
                onClick = {
                    launcher.launch(arrayOf("application/pdf"))
                },
                modifier = Modifier.fillMaxWidth()
                    .padding(bottom = 20.dp,start=8.dp,end=8.dp)
                    .align(Alignment.BottomCenter)
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
