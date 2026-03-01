package com.davi.dev.scannermlkit.presentation.screens.selectDocumentPdf

import android.graphics.pdf.PdfRenderer
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Draw
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.davi.dev.scannermlkit.domain.model.SignatureData
import com.davi.dev.scannermlkit.domain.pdf.PdfManager
import com.davi.dev.scannermlkit.presentation.components.CustomCircularProgress
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NativePdfViewer(uri: Uri) {
    val context = LocalContext.current
    val density = LocalDensity.current
    val scope = rememberCoroutineScope()

    var currentUri by remember { mutableStateOf<Uri?>(null) }
    var isEncrypted by remember { mutableStateOf(false) }
    var showPasswordDialog by remember { mutableStateOf(false) }
    var password by remember { mutableStateOf("") }
    var isCheckingEncryption by remember { mutableStateOf(true) }

    LaunchedEffect(uri) {
        withContext(Dispatchers.IO) {
            val isLocked = try {
                context.contentResolver.openInputStream(uri)?.use {
                    PdfManager.isPdfEncrypted(it)
                } ?: false
            } catch (_: Exception) {
                false
            }

            if (isLocked) {
                isEncrypted = true
                showPasswordDialog = true
            } else {
                currentUri = uri
            }
            isCheckingEncryption = false
        }
    }

    val rendererResource = remember(currentUri) {
        currentUri?.let {
            try {
                val pfd = context.contentResolver.openFileDescriptor(it, "r")
                pfd?.let { fd -> PdfRenderer(fd) }
            } catch (_: Exception) {
                null
            }
        }
    }

    DisposableEffect(rendererResource) {
        onDispose {
            rendererResource?.close()
        }
    }

    val sheetState = rememberModalBottomSheetState()
    var showBottomSheet by remember { mutableStateOf(false) }
    var currentPageIndex by remember { mutableIntStateOf(0) }

    val signaturesOnPage = remember { mutableStateMapOf<Int, List<SignatureData>>() }

    if (showPasswordDialog) {
        AlertDialog(
            onDismissRequest = { /* Bloquear dismiss se quiser */ },
            title = { Text(text = "PDF Protegido") },
            text = {
                Column {
                    Text(text = "Este arquivo requer uma senha para ser aberto.")
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Senha") },
                        visualTransformation = PasswordVisualTransformation(),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    scope.launch(Dispatchers.IO) {
                        val tempFile = File(context.cacheDir, "decrypted_temp.pdf")
                        val success = context.contentResolver.openInputStream(uri)?.use { input ->
                            FileOutputStream(tempFile).use { output ->
                                PdfManager.decryptPdf(input, output, password)
                            }
                        } ?: false

                        if (success) {
                            withContext(Dispatchers.Main) {
                                currentUri = Uri.fromFile(tempFile)
                                showPasswordDialog = false
                            }
                        } else {
                            withContext(Dispatchers.Main) {
                                Toast.makeText(context, "Senha incorreta", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                }) {
                    Text("Abrir")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPasswordDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }

    Scaffold(
        floatingActionButton = {
            if (rendererResource != null) {
                FloatingActionButton(onClick = {
                    showBottomSheet = true
                }) {
                    Icon(imageVector = Icons.Default.Draw, contentDescription = "Sign PDF")
                }
            }
        }
    ) { padding ->
        BoxWithConstraints(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            val maxWidthPx = with(density) { maxWidth }
            val maxHeightPx = with(density) { maxHeight }

            if (isCheckingEncryption) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CustomCircularProgress()
                }
            } else if (rendererResource != null) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    items(rendererResource.pageCount) { pageIdx ->
                        val pageSignatures = signaturesOnPage.getOrDefault(pageIdx, emptyList())
                        PDFPage(
                            renderer = rendererResource,
                            pageIndex = pageIdx,
                            signatures = pageSignatures,
                            onPageScrolled = { currentPageIndex = it },
                            parentMaxWidthPx = maxWidthPx,
                            parentMaxHeightPx = maxHeightPx,
                            onDeleteSignature = { signatureToDelete ->
                                val currentList = signaturesOnPage[pageIdx] ?: emptyList()
                                signaturesOnPage[pageIdx] = currentList.filter { it != signatureToDelete }
                            },
                            onUpdateSignature = { updatedSignature ->
                                val currentList = signaturesOnPage[pageIdx] ?: emptyList()
                                signaturesOnPage[pageIdx] = currentList.map {
                                    if (it.path == updatedSignature.path) updatedSignature else it
                                }
                            }
                        )
                    }
                    item {
                        Spacer(Modifier.height(100.dp))
                    }
                }
                Box(
                    modifier = Modifier.align(Alignment.BottomStart),
                    contentAlignment = Alignment.Center
                ) {
                    Card(modifier = Modifier.padding(10.dp)) {
                        Text(
                            "${currentPageIndex + 1} / ${rendererResource.pageCount}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White,
                            modifier = Modifier.padding(10.dp)
                        )
                    }
                }
            } else if (!isEncrypted || !showPasswordDialog) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    if (isEncrypted) {
                        Text("PDF protegido por senha.")
                    } else {
                        CustomCircularProgress()
                    }
                }
            }

            if (showBottomSheet) {
                ModalBottomSheet(
                    onDismissRequest = { showBottomSheet = false },
                    sheetState = sheetState,
                ) {
                    Box(modifier = Modifier.height(300.dp)) {
                        SignaturePad(
                            currentPageIndex = currentPageIndex,
                            onDone = { signatureData ->
                                val pageIndex = currentPageIndex
                                val currentList = signaturesOnPage[pageIndex] ?: emptyList()
                                signaturesOnPage[pageIndex] = currentList + signatureData

                                scope.launch {
                                    sheetState.hide()
                                    showBottomSheet = false
                                }
                            },
                            onCancel = {
                                scope.launch {
                                    sheetState.hide()
                                    showBottomSheet = false
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}
