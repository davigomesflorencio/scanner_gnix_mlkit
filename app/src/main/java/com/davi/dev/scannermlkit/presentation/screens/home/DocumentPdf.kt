package com.davi.dev.scannermlkit.presentation.screens.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.davi.dev.scannermlkit.presentation.components.CustomDivider
import com.davi.dev.scannermlkit.presentation.components.FunctionsHomeApp

@Composable
fun DocumentPdf(backStack: SnapshotStateList<Any>) {
    val context = LocalContext.current
    val filesDir = context.filesDir
    val pdfFiles = remember {
        filesDir.listFiles { file -> file.name.endsWith(".pdf") }?.toList() ?: emptyList()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        FunctionsHomeApp(backStack)

        CustomDivider()

        LazyColumn(modifier = Modifier.padding(16.dp)) {

            items(pdfFiles) { file ->
                PdfListItem(file = file)
            }
        }
    }
}