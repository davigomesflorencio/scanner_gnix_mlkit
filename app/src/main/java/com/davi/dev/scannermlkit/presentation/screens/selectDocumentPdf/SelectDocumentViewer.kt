package com.davi.dev.scannermlkit.presentation.screens.selectDocumentPdf

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.davi.dev.scannermlkit.R
import com.davi.dev.scannermlkit.presentation.screens.allPdf.NativePdfViewer
import com.davi.dev.scannermlkit.presentation.screens.viewModel.ScannerDocumentViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable()
fun SelectDocumentViewer(
    navBackStack: NavBackStack<NavKey>,
    scannerDocumentViewModel: ScannerDocumentViewModel
) {
    val documentUri by scannerDocumentViewModel.documentUri.collectAsState()

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) {
        it?.let { uri -> scannerDocumentViewModel.setUri(uri) }
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        if (documentUri == null) {
            Button(
                onClick = {
                    launcher.launch(arrayOf("application/pdf"))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 20.dp, start = 8.dp, end = 8.dp)
                    .align(Alignment.BottomCenter)
            ) {
                Text(
                    text = "Select Document",
                )
            }
        }
        documentUri?.let { uri ->
            NativePdfViewer(uri, navBackStack)
        } ?: Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp)
                    .align(Alignment.TopCenter)
            ) {
                Image(
                    painterResource(R.drawable.screenshoot2),
                    contentScale = ContentScale.FillWidth,
                    contentDescription = "Preview app",
                    modifier = Modifier.fillMaxSize(.5f)
                )

                Text(
                    "You can also edit and customize scan results",
                    style = MaterialTheme.typography.titleMedium,
                    fontSize = 40.sp,
                    lineHeight = 40.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .padding(vertical = 10.dp)
                )
                Text(
                    "No documents selected.",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}
