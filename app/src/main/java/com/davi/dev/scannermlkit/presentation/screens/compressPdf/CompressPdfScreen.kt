package com.davi.dev.scannermlkit.presentation.screens.compressPdf

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
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.davi.dev.scannermlkit.domain.enums.CompressionLevel
import com.davi.dev.scannermlkit.presentation.components.CustomDivider
import com.davi.dev.scannermlkit.presentation.components.PDFSimpleItem
import com.davi.dev.scannermlkit.presentation.screens.viewModel.CompressPdfViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompressPdfScreen(
    compressPdfViewModel: CompressPdfViewModel = viewModel()
) {
    val context = LocalContext.current
    val selectedPdfUri by compressPdfViewModel.selectedPdfUri.collectAsState()
    val compressionLevel by compressPdfViewModel.compressionLevel.collectAsState()

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            compressPdfViewModel.selectPdf(it)
        }
    }

    LaunchedEffect(Unit) {
        compressPdfViewModel.status.collect { message ->
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
            text = "Compress PDF",
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
            Spacer(modifier = Modifier.height(24.dp))

            PDFSimpleItem(selectedPdfUri!!)

            CustomDivider()

            Text(
                text = "Nível de Compressão",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.align(Alignment.Start)
            )

            Column(Modifier.selectableGroup()) {
                CompressionLevel.entries.forEach { level ->
                    Row(
                        Modifier
                            .fillMaxWidth()
                            .height(40.dp)
                            .selectable(
                                selected = (level == compressionLevel),
                                onClick = { compressPdfViewModel.setCompressionLevel(level) },
                                role = Role.RadioButton
                            )
                            .padding(horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = (level == compressionLevel),
                            onClick = null // null recommended for accessibility with selectable modifier
                        )
                        Text(
                            text = level.label,
                            style = MaterialTheme.typography.bodyLarge,
                            modifier = Modifier.padding(start = 16.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
                    compressPdfViewModel.compressPdf("compressed_${timestamp}.pdf")
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Comprimir PDF")
            }

            Spacer(modifier = Modifier.height(16.dp))

            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Informação:",
                        style = MaterialTheme.typography.titleSmall
                    )
                    Text(
                        text = when (compressionLevel) {
                            CompressionLevel.LOW -> "Baixa compressão: Mantém melhor qualidade, mas o arquivo fica maior."
                            CompressionLevel.MEDIUM -> "Média compressão: Equilíbrio entre qualidade e tamanho de arquivo."
                            CompressionLevel.HIGH -> "Alta compressão: Menor tamanho de arquivo possível, mas a qualidade pode ser reduzida."
                        },
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CompressPdfScreenPreview() {
    CompressPdfScreen()
}
