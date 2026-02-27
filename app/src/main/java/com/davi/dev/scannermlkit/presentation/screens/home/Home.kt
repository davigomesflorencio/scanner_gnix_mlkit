package com.davi.dev.scannermlkit.presentation.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.davi.dev.scannermlkit.presentation.components.CustomDivider

@Composable
fun Home(backStack: NavBackStack<NavKey>) {
    val context = LocalContext.current
    val filesDir = context.filesDir
    val pdfFiles = remember {
        filesDir.listFiles { file ->
            file.name.endsWith(".pdf")
        }?.sortedByDescending { it.lastModified() } ?: emptyList()
    }

    Column(modifier = Modifier.fillMaxSize()) {
        FunctionsHomeApp(backStack)

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            item {
                Text(
                    "Recents",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.inversePrimary
                )
            }
            item {
                CustomDivider()
            }
            items(pdfFiles) { file ->
                PDFItem(
                    navBackStack = backStack,
                    file = file
                )
            }
        }
    }
}