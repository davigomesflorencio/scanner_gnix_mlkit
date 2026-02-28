package com.davi.dev.scannermlkit.presentation.screens.splitPdf

import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.input.TextFieldLineLimits
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.davi.dev.scannermlkit.presentation.components.CustomDivider
import com.davi.dev.scannermlkit.presentation.components.PDFSimpleItem
import com.davi.dev.scannermlkit.presentation.screens.viewModel.SplitPdfViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SplitPdfScreen(viewModel: SplitPdfViewModel) {
    val context = LocalContext.current
    val selectedPdfUri by viewModel.selectedPdfUri.collectAsState()
    val pageCount by viewModel.pageCount.collectAsState()

    val fileNameState = rememberTextFieldState("")
    var startPage by remember { mutableStateOf("1") }
    var endPage by remember { mutableStateOf("") }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            viewModel.selectPdf(it)
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            fileNameState.edit {
                replace(0, length, "split_$timestamp")
            }
        }
    }

    LaunchedEffect(Unit) {
        viewModel.splitStatus.collect { message ->
            Toast.makeText(context, message, Toast.LENGTH_LONG).show()
        }
    }

    LaunchedEffect(pageCount) {
        if (pageCount > 0) {
            endPage = pageCount.toString()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                "Split PDF",
                style = MaterialTheme.typography.titleLarge
            )

            CustomDivider()

            Button(
                onClick = { launcher.launch("application/pdf") },
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.FileUpload, contentDescription = null)
                    Text(text = "Selecionar Arquivo PDF", modifier = Modifier.padding(start = 8.dp))
                }
            }

            if (selectedPdfUri != null) {
                PDFSimpleItem(selectedPdfUri!!)

                CustomDivider()

                Text(
                    "Configurações",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    "Nome do Arquivo",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
                
                TextField(
                    state = fileNameState,
                    label = { Text("Nome do Arquivo") },
                    lineLimits = TextFieldLineLimits.MultiLine(maxHeightInLines = 2),
                    modifier = Modifier.fillMaxWidth()
                )

                Text(
                    "Intervalo de Páginas (Total: $pageCount)",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = startPage,
                        onValueChange = { if (it.all { char -> char.isDigit() }) startPage = it },
                        label = { Text("Início") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = endPage,
                        onValueChange = { if (it.all { char -> char.isDigit() }) endPage = it },
                        label = { Text("Fim") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        if (selectedPdfUri != null) {
            Button(
                onClick = {
                    val start = startPage.toIntOrNull() ?: 1
                    val end = endPage.toIntOrNull() ?: pageCount
                    val fileName = fileNameState.text.toString()

                    if (start < 1 || end > pageCount || start > end) {
                        Toast.makeText(context, "Intervalo de páginas inválido (1-$pageCount)", Toast.LENGTH_SHORT).show()
                    } else if (fileName.isBlank()) {
                        Toast.makeText(context, "Insira um nome para o arquivo", Toast.LENGTH_SHORT).show()
                    } else {
                        viewModel.splitPdf(fileName, start, end)
                    }
                },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(bottom = 20.dp)
            ) {
                Text(
                    "Split File",
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }
    }
}
