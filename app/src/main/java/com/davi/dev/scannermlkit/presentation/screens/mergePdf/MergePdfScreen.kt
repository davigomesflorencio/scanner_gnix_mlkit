package com.davi.dev.scannermlkit.presentation.screens.mergePdf

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.davi.dev.scannermlkit.presentation.components.CustomDivider
import com.davi.dev.scannermlkit.presentation.screens.viewModel.MergeDocumentViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MergePdfScreen(mergeDocumentViewModel: MergeDocumentViewModel) {
    val pdfFilesUri by mergeDocumentViewModel.pdfFilesUri.collectAsState()
    val stateTextField = rememberTextFieldState("")
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        mergeDocumentViewModel.mergeStatus.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.OpenMultipleDocuments()) { uris ->
        uris.forEach { uri ->
            mergeDocumentViewModel.addFileUri(uri)
        }
        if (uris.isNotEmpty()) {
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            stateTextField.edit {
                replace(0, length, "document_$timestamp")
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(bottom = 120.dp)
        ) {
            item {
                Text(
                    "Merge PDF",
                    style = MaterialTheme.typography.titleLarge
                )
            }
            item {
                Text(
                    "${pdfFilesUri.size}  selected files to be Merged",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            item {
                CustomDivider()
            }
            item {
                Text(
                    "File Name",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )
            }
            item {
                TextField(
                    state = stateTextField,
                    label = { Text("File Name") },
                    lineLimits = TextFieldLineLimits.MultiLine(maxHeightInLines = 2),
                    modifier = Modifier.fillMaxWidth()
                )
            }
            items(pdfFilesUri) { uri ->
                PDFItemMerged(
                    uri = uri,
                    onRemove = { mergeDocumentViewModel.removeFileUri(uri) }
                )
            }
        }

        FilledTonalButton(
            onClick = {
                launcher.launch(arrayOf("application/pdf"))
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 80.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Add, contentDescription = "Add")
                Text(
                    "Add More Files",
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }

        Button(
            onClick = {
                val fileName = stateTextField.text.toString()
                if (fileName.isNotBlank()) {
                    mergeDocumentViewModel.mergePdf(fileName)
                } else {
                    Toast.makeText(context, "Por favor, insira um nome para o arquivo", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 20.dp),
            enabled = pdfFilesUri.size >= 2 // Opcional: habilitar apenas se houver pelo menos 2 arquivos
        ) {
            Text(
                "Merge",
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}