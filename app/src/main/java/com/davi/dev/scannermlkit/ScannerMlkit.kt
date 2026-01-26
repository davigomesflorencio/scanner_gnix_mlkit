package com.davi.dev.scannermlkit

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.LocalActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import coil3.compose.AsyncImage
import com.google.mlkit.vision.documentscanner.GmsDocumentScanner
import com.google.mlkit.vision.documentscanner.GmsDocumentScanningResult
import java.io.File
import java.io.FileOutputStream

@Composable
fun ScannerMlkit(scanner: GmsDocumentScanner, backStack: SnapshotStateList<Any>) {
    val activity = LocalActivity.current
    val context = LocalContext.current
    val contentResolver = context.contentResolver
    val filesDir = context.filesDir
    var imageUris by remember { mutableStateOf<List<Uri>>(emptyList()) }
    var pdfFile by remember { mutableStateOf<File?>(null) }

    val scannerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = {
            if (it.resultCode == RESULT_OK) {
                val result = GmsDocumentScanningResult.fromActivityResultIntent(it.data)
                imageUris = result?.pages?.map { it.imageUri } ?: emptyList()

                result?.pdf?.let { pdf ->
                    val file = File(filesDir, "scan_${System.currentTimeMillis()}.pdf")
                    val fos = FileOutputStream(file)
                    contentResolver.openInputStream(pdf.uri)?.use { inputStream ->
                        inputStream.copyTo(fos)
                    }
                    pdfFile = file
                }
            }
        }
    )

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        imageUris.forEach { uri ->
            AsyncImage(
                model = uri,
                contentDescription = null,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier.fillMaxWidth()
            )
        }
        Button(onClick = {
            activity?.let {
                scanner.getStartScanIntent(it)
                    .addOnFailureListener {
                        Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show()
                    }
                    .addOnSuccessListener { intent ->
                        scannerLauncher.launch(IntentSenderRequest.Builder(intent).build())
                    }
            }
        }, modifier = Modifier.padding(top = 10.dp)) {
            Text(text = "Scan PDF")
        }

        pdfFile?.let { file ->
            Button(
                onClick = {
                    val authority = "${context.packageName}.provider"
                    val pdfUri = FileProvider.getUriForFile(context, authority, file)
                    val shareIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(Intent.EXTRA_STREAM, pdfUri)
                        type = "application/pdf"
                        addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                    }
                    context.startActivity(Intent.createChooser(shareIntent, "Export PDF"))
                },
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text(text = "Export PDF")
            }
        }
    }
}
