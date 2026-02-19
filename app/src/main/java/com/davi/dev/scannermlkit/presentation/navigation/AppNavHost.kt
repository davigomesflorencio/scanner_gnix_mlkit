package com.davi.dev.scannermlkit.presentation.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarDefaults
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation3.runtime.NavEntry
import androidx.navigation3.ui.NavDisplay
import com.davi.dev.scannermlkit.presentation.DocumentPdf
import com.davi.dev.scannermlkit.R
import com.davi.dev.scannermlkit.presentation.ScannerMlkit
import com.google.mlkit.vision.documentscanner.GmsDocumentScanner

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavHost(scanner: GmsDocumentScanner) {
    val backStack = remember { mutableStateListOf<Any>(ListDocument) }
    val startDestination = Destination.DOCUMENTS
    var selectedDestination by rememberSaveable { mutableIntStateOf(startDestination.ordinal) }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                ),
                title = {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.scanner),
                            contentDescription = null,
                            modifier = Modifier.size(32.dp)
                        )
                        Text(
                            "Scanner MLKIT", style = MaterialTheme.typography
                                .titleLarge,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            )
        },
        bottomBar = {
            NavigationBar(windowInsets = NavigationBarDefaults.windowInsets) {
                Destination.entries.forEachIndexed { index, destination ->
                    NavigationBarItem(
                        selected = selectedDestination == index,
                        onClick = {
                            backStack.add(destination.route)
                            selectedDestination = index
                        },
                        icon = {
                            Icon(
                                destination.icon,
                                contentDescription = destination.contentDescription
                            )
                        },
                        label = {
                            Text(
                                destination.label, style = MaterialTheme.typography
                                    .headlineMedium
                            )
                        }
                    )
                }
            }
        }
    ) { contentPadding ->
        Column(
            modifier = Modifier
                .padding(contentPadding)
                .fillMaxSize()
        ) {
            NavDisplay(
                backStack = backStack,
                onBack = { backStack.removeLastOrNull() },
                entryProvider = { key ->
                    when (key) {
                        is ListDocument -> NavEntry(key) {
                            DocumentPdf(backStack)
                        }

                        is ScanPdf -> NavEntry(key) {
                            ScannerMlkit(scanner, backStack)
                        }

                        else -> {
                            error("Unknown route: $key")
                        }
                    }
                }
            )
        }
    }
}