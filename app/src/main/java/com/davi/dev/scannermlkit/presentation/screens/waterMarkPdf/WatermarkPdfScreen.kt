package com.davi.dev.scannermlkit.presentation.screens.watermarkPdf

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.TextSnippet
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.davi.dev.scannermlkit.presentation.components.CustomDivider
import com.davi.dev.scannermlkit.presentation.components.PDFSimpleItem
import com.davi.dev.scannermlkit.presentation.screens.viewModel.WatermarkPdfViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WatermarkPdfScreen(
    watermarkPdfViewModel: WatermarkPdfViewModel = viewModel()
) {
    val context = LocalContext.current
    val selectedPdfUri by watermarkPdfViewModel.pdfFileUri.collectAsState()

    var watermarkText by remember { mutableStateOf("") }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            watermarkPdfViewModel.setFileUri(it)
        }
    }

    LaunchedEffect(Unit) {
        watermarkPdfViewModel.watermarkStatus.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Watermark PDF",
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(bottom = 24.dp)
        )

        CustomDivider()

        Button(
            onClick = { launcher.launch("application/pdf") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.FileUpload, contentDescription = null)
                Text(text = "Selecionar Arquivo PDF", modifier = Modifier.padding(start = 8.dp))
            }
        }

        if (selectedPdfUri != null) {
            Spacer(Modifier.height(8.dp))

            PDFSimpleItem(selectedPdfUri!!)

            Spacer(Modifier.height(8.dp))

            OutlinedTextField(
                value = watermarkText,
                onValueChange = { watermarkText = it },
                label = { Text("Watermark Text") },
                leadingIcon = { Icon(Icons.Filled.TextSnippet, contentDescription = null) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                enabled = watermarkText.isNotBlank(),
                onClick = {
                    val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                    watermarkPdfViewModel.addWatermark(watermarkText, "watermarked_${timestamp}")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add Watermark")
            }
        }
    }
}
