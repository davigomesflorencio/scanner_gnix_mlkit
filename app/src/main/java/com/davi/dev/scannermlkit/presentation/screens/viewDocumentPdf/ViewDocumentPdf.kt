package com.davi.dev.scannermlkit.presentation.screens.viewDocumentPdf

import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.davi.dev.scannermlkit.presentation.theme.ScannermlkitTheme
import java.io.File

@Composable
fun ViewDocumentPdf(filePath: String) {
    val context = LocalContext.current
    val file = File(filePath)

    val composition by rememberLottieComposition(
        LottieCompositionSpec.Asset("no_history.json")
    )

    val progress by animateLottieCompositionAsState(
        composition = composition,
        iterations = LottieConstants.IterateForever
    )
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
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxSize()
        ) {
            LottieAnimation(
                composition = composition,
                progress = { progress }
            )
            Spacer(Modifier.height(26.dp))
            Text(
                "File not found",
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}

@Preview
@Composable
fun PreviewViewDocumentPdf() {
    ScannermlkitTheme() {
        ViewDocumentPdf("filePath")
    }
}