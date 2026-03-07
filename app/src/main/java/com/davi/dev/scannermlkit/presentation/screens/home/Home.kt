package com.davi.dev.scannermlkit.presentation.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import com.davi.dev.scannermlkit.presentation.components.CustomDivider
import com.davi.dev.scannermlkit.presentation.screens.viewModel.HomeViewModel
import com.davi.dev.scannermlkit.presentation.screens.viewModel.ScannerDocumentViewModel

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun Home(
    backStack: NavBackStack<NavKey>,
    homeViewModel: HomeViewModel = viewModel(),
    scannerDocumentViewModel: ScannerDocumentViewModel = viewModel()
) {
    val context = LocalContext.current
    val pdfFiles by homeViewModel.filteredFiles.collectAsState()
    val isSearchActive by homeViewModel.isSearchActive.collectAsState()
    val searchQuery by homeViewModel.searchQuery.collectAsState()

    LaunchedEffect(Unit) {
        homeViewModel.loadFiles()
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        UpdateChecker(context = context)
        FunctionsHomeApp(backStack, scannerDocumentViewModel)

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.padding(16.dp)
        ) {
            item {
                Text(
                    if (isSearchActive || searchQuery.isNotEmpty()) "Search results" else "Recents",
                    style = MaterialTheme.typography.titleLarge,
                    color = MaterialTheme.colorScheme.inversePrimary
                )
            }
            item {
                CustomDivider()
            }
            itemsIndexed(pdfFiles.take(10)) { index, file ->
                PDFItem(
                    homeViewModel = homeViewModel,
                    navBackStack = backStack,
                    file = file,
                )
            }
        }
    }
}
