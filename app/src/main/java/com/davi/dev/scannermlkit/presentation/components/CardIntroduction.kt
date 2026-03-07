package com.davi.dev.scannermlkit.presentation.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.davi.dev.scannermlkit.presentation.theme.ScannermlkitTheme

@Composable
fun CardIntroduction(title: String, description: String) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 5.dp)
            )
            CustomDivider()
            Text(
                description,
                style = MaterialTheme.typography.labelSmall,
                modifier = Modifier.padding(top = 5.dp)
            )
        }
    }
}

@Preview
@Composable
fun ProtectPdfScreenPreview() {
    ScannermlkitTheme() {
        CardIntroduction(
            title = "Protect PDF",
            description = "Set a password to protect your scan. This " +
                    "password will be required if you or the person " +
                    "you provide the scanned document wants to " +
                    "access the file. If you forget the password, then " +
                    "this file will not be accessible forever.",
        )
    }
}